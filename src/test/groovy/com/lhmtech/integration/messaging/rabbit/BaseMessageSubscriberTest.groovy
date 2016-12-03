package com.lhmtech.integration.messaging.rabbit

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
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
        /*>>
                .to(deadLetterExchange)
                .with(LHM_DEAD_LETTER_ROUTING_KEY))*/
    }

    //crea
}
