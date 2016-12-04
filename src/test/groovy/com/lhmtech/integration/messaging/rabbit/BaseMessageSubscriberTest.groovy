package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import spock.lang.Specification

/**
 * Created by lihe on 16-12-2.
 */
class BaseMessageSubscriberTest extends Specification {

    //create_dead_letter
    def "create dead letter exchange and queue"() {
        given:
        RabbitConfiguration mockRabbitConfiguration = Mock(RabbitConfiguration)
        ConnectionFactory mockConnectionFactory = Mock(ConnectionFactory)
        Queue mockQueue = GroovyMock(Queue, global: true)
        DirectExchange mockDirectExchange = GroovyMock(DirectExchange, global: true)
        RabbitAdmin mockRabbitAdmin=GroovyMock(RabbitAdmin, global: true)
        GroovyMock(BindingBuilder, global: true)
        MessageSubscriberTestImpl messageSubscriber =  new MessageSubscriberTestImpl()
        messageSubscriber.rabbitConfiguration = mockRabbitConfiguration
        BindingBuilder.DestinationConfigurer mockConfigurer = GroovyMock(BindingBuilder.DestinationConfigurer, global: true)
        BindingBuilder.DirectExchangeRoutingKeyConfigurer mockDirectExchangeRoutingKeyConfigurer = GroovyMock(BindingBuilder.DirectExchangeRoutingKeyConfigurer, global: true)
        Binding mockBinding = Mock(Binding)

        when:
        messageSubscriber.createDeadLetterExchangeAndQueue()

        then:
        1 * mockRabbitConfiguration.connectionFactory >> mockConnectionFactory
        1 * new Queue(BaseMessageSubscriber.LHM_DEAD_LETTER_QUEUE, true) >> mockQueue
        1 * new DirectExchange(BaseMessageSubscriber.LHM_DEAD_LETTER_EXCHANGE) >> mockDirectExchange
        1 * new RabbitAdmin(mockConnectionFactory) >> mockRabbitAdmin
        1 * mockRabbitAdmin.declareExchange(mockDirectExchange)
        1 * mockRabbitAdmin.declareQueue(mockQueue)
        1 * BindingBuilder.bind(mockQueue) >> mockConfigurer
        1 * mockConfigurer.to(mockDirectExchange) >> mockDirectExchangeRoutingKeyConfigurer
        1 * mockDirectExchangeRoutingKeyConfigurer.with(BaseMessageSubscriber.LHM_DEAD_LETTER_ROUTING_KEY) >> mockBinding
        1 * mockRabbitAdmin.declareBinding(mockBinding)
    }

    def "create working exchange and queue"() {
        given:
        Queue mockWorkingQueue = GroovyMock(Queue, global: true)
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        FanoutExchange mockWorkingExchange = GroovyMock(FanoutExchange, global: true)
        RabbitConfiguration mockRabbitConfiguration = Mock(RabbitConfiguration)
        ConnectionFactory mockConnectionFactory = Mock(ConnectionFactory)
        RabbitAdmin mockRabbitAdmin=GroovyMock(RabbitAdmin, global: true)
        GroovyMock(BindingBuilder, global: true)
        BindingBuilder.DestinationConfigurer mockDestinationConfigurer = GroovyMock(BindingBuilder.DestinationConfigurer)
        Binding mockBinding = Mock(Binding)

        when:
        messageSubscriber.createWorkingExchangeAndQueue()

        then:
        1 * new Queue('test-subscriber-queue',
                true, false, false,
                ["x-dead-letter-exchange": BaseMessageSubscriber.LHM_DEAD_LETTER_EXCHANGE,
                 "x-dead-letter-routing-key": BaseMessageSubscriber.LHM_DEAD_LETTER_ROUTING_KEY]) >> mockWorkingQueue
        1 * new FanoutExchange('test-subscriber-exchange') >> mockWorkingExchange
        1 * mockRabbitConfiguration.connectionFactory >> mockConnectionFactory
        1 * new RabbitAdmin(mockConnectionFactory) >> mockRabbitAdmin
        1 * mockRabbitAdmin.declareExchange(mockWorkingExchange)

        1 * BindingBuilder.bind(mockWorkingQueue) >> mockDestinationConfigurer
        1 * mockDestinationConfigurer.to(mockWorkingExchange) >> mockBinding
        1 * mockRabbitAdmin.declareBinding(mockBinding)
        /*M
        rabbitAdmin.declareQueue(workingQueue)
        rabbitAdmin.declareBinding(BindingBuilder.bind(workingQueue).to(workingExchange))
        container = new SimpleMessageListenerContainer(rabbitConfiguration.connectionFactory)
        listener = new ChannelAwareMessageListener() {
            @Override
            void onMessage(Message message, Channel channel) throws Exception {
                String messageText = new String(message.body)
                subscribe(messageText)
            }
        }
        container.setMessageListener(listener)
        container.setQueueNames(this.getQueue())
        container.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        container.start()*/
        true
    }
    //crea
}
