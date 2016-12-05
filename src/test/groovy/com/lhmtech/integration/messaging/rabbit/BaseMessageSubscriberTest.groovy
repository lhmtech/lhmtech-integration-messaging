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
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by lihe on 16-12-2.
 */
class BaseMessageSubscriberTest extends Specification {
    RabbitConfiguration mockRabbitConfiguration
    ConnectionFactory mockConnectionFactory

    def setup() {
        mockRabbitConfiguration = Mock(RabbitConfiguration)
        mockConnectionFactory = Mock(ConnectionFactory)
    }
    //create_dead_letter
    def "create dead letter exchange and queue"() {
        given:
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
        messageSubscriber.rabbitConfiguration = mockRabbitConfiguration
        FanoutExchange mockWorkingExchange = GroovyMock(FanoutExchange, global: true)
        RabbitAdmin mockRabbitAdmin=GroovyMock(RabbitAdmin, global: true)
        GroovyMock(BindingBuilder, global: true)
        BindingBuilder.DestinationConfigurer mockDestinationConfigurer = GroovyMock(BindingBuilder.DestinationConfigurer)
        Binding mockBinding = Mock(Binding)
        SimpleMessageListenerContainer mockContainer = GroovyMock(SimpleMessageListenerContainer, global: true)
        ChannelAwareMessageListener mockListener = Mock(ChannelAwareMessageListener)

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
        1 * mockRabbitAdmin.declareQueue(mockWorkingQueue)
        1 * BindingBuilder.bind(mockWorkingQueue) >> mockDestinationConfigurer
        1 * mockDestinationConfigurer.to(mockWorkingExchange) >> mockBinding
        1 * mockRabbitAdmin.declareBinding(mockBinding)
        1 * new SimpleMessageListenerContainer(mockConnectionFactory) >> mockContainer
        //1 * new ChannelAwareMessageListener() >> mockListener
        1 * mockContainer.setMessageListener(_)
        1 * mockContainer.setQueueNames('test-subscriber-queue')
        1 * mockContainer.setAcknowledgeMode(AcknowledgeMode.MANUAL)
        1 * mockContainer.start()
    }

    def "is auto startup"() {
        expect:
        new MessageSubscriberTestImpl().isAutoStartup()
    }

    def "stop with callback call stop then callback"() {
        given:
        Boolean callbackCalled = false
        def callback = { -> callbackCalled = true }
        Boolean stopCalled = false
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        messageSubscriber.metaClass.stop = { stopCalled = true }

        when:
        messageSubscriber.stop(callback)

        then:
        //stopCalled
        callbackCalled
    }

    @Ignore
    def "start create dead letter and working queue"() {
        given:
        boolean createDLXQ = false
        boolean createWorkingXQ = false
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        messageSubscriber.metaClass.createDeadLetterExchangeAndQueue = { createDLXQ = true }
        messageSubscriber.metaClass.createWorkingExchangeAndQueue = { createWorkingXQ = true }

        when:
        messageSubscriber.start()

        then:
        createDLXQ
        createWorkingXQ
    }

    def "stop shutdown container"() {
        given:
        SimpleMessageListenerContainer mockContainer = Mock(SimpleMessageListenerContainer)
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        messageSubscriber.container = mockContainer

        when:
        messageSubscriber.stop()

        then:
        1* mockContainer.shutdown()
    }

    def "is running return false when container is null"() {
        given:
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        messageSubscriber.container = null

        when:
        boolean running = messageSubscriber.isRunning()

        then:
        !running
    }

    @Ignore
    @Unroll
    def "is running return container is active"() {
        given:
        SimpleMessageListenerContainer mockContainer = Mock(SimpleMessageListenerContainer)
        MessageSubscriberTestImpl messageSubscriber = new MessageSubscriberTestImpl()
        messageSubscriber.container = mockContainer
        mockContainer.isActive() >> expected

        when:
        boolean running = messageSubscriber.isRunning()

        then:

        running == expected

        where:
        expected << [true, false]
    }

}
