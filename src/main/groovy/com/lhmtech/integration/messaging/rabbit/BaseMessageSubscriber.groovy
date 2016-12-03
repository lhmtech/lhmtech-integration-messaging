package com.lhmtech.integration.messaging.rabbit

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle

import javax.net.ssl.SSLException

/**
 * Created by lihe on 16-12-2.
 */
abstract class BaseMessageSubscriber implements SmartLifecycle{
    static final String LHM_DEAD_LETTER_EXCHANGE='dead-letter-exchange'
    static final String LHM_DEAD_LETTER_QUEUE='dead-letter-queue'
    static final String LHM_DEAD_LETTER_ROUTING_KEY='dead-letter-routing-key'

    abstract void subscribe(byte[] messageBytes)
    abstract String getExchange()
    abstract String getQueue()

    @Autowired
    RabbitConfiguration rabbitConfiguration

    Logger logger = LoggerFactory.getLogger(BaseMessageSubscriber)

    @Override
    boolean isAutoStartup() {
        return false
    }

    @Override
    void stop(Runnable callback) {

    }

    @Override
    void start() {

    }

    @Override
    void stop() {

    }

    @Override
    boolean isRunning() {
        return false
    }

    @Override
    int getPhase() {
        return 0
    }

    void createDeadLetterExchangeAndQueue() {
        DirectExchange deadLetterExchange = new DirectExchange(LHM_DEAD_LETTER_EXCHANGE)
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitConfiguration.connectionFactory)
        try{
            rabbitAdmin.declareExchange(deadLetterExchange)
        }catch (SSLException ex) {
            logger.error("SSLException while delcare exchange", ex)
            sleep(500)
            rabbitAdmin.declareExchange(deadLetterExchange)
        }
        Queue dealLetterQueue = new Queue(LHM_DEAD_LETTER_QUEUE, true)
        rabbitAdmin.declareQueue(dealLetterQueue)
        rabbitAdmin.declareBinding(
                BindingBuilder
                .bind(dealLetterQueue)
                .to(deadLetterExchange)
                .with(LHM_DEAD_LETTER_ROUTING_KEY))
    }

    void createWorkingExchangeAndQueue() {
        Map<String, Object> args = ["x-dead-letter-exchange": LHM_DEAD_LETTER_EXCHANGE,
                                    "x-dead-letter-routing-key": LHM_DEAD_LETTER_ROUTING_KEY]
        Queue workingQueue = new Queue(this.getQueue(), true, false, false, args)
        FanoutExchange workingExchange = new FanoutExchange(this.getExchange())
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitConfiguration.connectionFactory)
        rabbitAdmin.declareExchange(workingExchange)
        rabbitAdmin.declareQueue(queue)
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(workingExchange))
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer(rabbitConfiguration.connectionFactory)
        listenerContainer.set.setMessageListener(here)
        listenerContainer.setQueueNames(this.getQueue())
        listenerContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        listenerContainer.start()
        listnerApdapter.container = listenerContainer

    }
}
