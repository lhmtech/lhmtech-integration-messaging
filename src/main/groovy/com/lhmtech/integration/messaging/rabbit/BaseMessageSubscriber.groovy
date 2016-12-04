package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Message
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

    ChannelAwareMessageListener listener
    SimpleMessageListenerContainer container
    @Autowired
    RabbitConfiguration rabbitConfiguration

    Logger logger = LoggerFactory.getLogger(BaseMessageSubscriber)

    @Override
    boolean isAutoStartup() {
        return true
    }

    @Override
    void stop(Runnable callback) {
        stop()
        callback.run()
    }

    @Override
    void start() {
        createDeadLetterExchangeAndQueue()
        createWorkingExchangeAndQueue()
    }

    @Override
    void stop() {
        try {
            container.shutdown()
        } catch (exception) {
        }
    }

    @Override
    boolean isRunning() {
        if (container != null) {
            return container.isActive()
        }
    }

    @Override
    int getPhase() {
        Integer.MAX_VALUE
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
        rabbitAdmin.declareQueue(workingQueue)
        rabbitAdmin.declareBinding(BindingBuilder.bind(workingQueue).to(workingExchange))
        container = new SimpleMessageListenerContainer(rabbitConfiguration.connectionFactory)
        listener = new ChannelAwareMessageListener() {
            @Override
            void onMessage(Message message, Channel channel) throws Exception {
                subscribe(message.body)
            }
        }
        container.setMessageListener(listener)
        container.setQueueNames(this.getQueue())
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        container.start()
    }
}
