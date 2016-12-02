package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.slf4j.Logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by lihe on 16-11-30.
 */
class BaseMessageSenderTest extends Specification {

    @Shared
    MessageSenderTestImpl messageSender
    Logger mockLogger
    RabbitTemplate mockRabbitTemplate
    String mockExchange
    RabbitConfiguration mockRabbitConfiguration

    def setup() {
        mockExchange = 'mock-exchange'
        messageSender = new MessageSenderTestImpl(exchangeName: mockExchange)
        mockRabbitTemplate = Mock(RabbitTemplate)
        messageSender.rabbitTemplate = mockRabbitTemplate
        mockLogger = Mock(Logger)
        messageSender.logger = mockLogger
        mockRabbitConfiguration = Mock(RabbitConfiguration)
        messageSender.rabbitConfiguration = mockRabbitConfiguration
    }

    def "send message"() {
        given:

        String hello = 'Hello, World'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        messageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(mockExchange, null, mockMessage)
    }

    def "send message will log error whnen exception occurs"() {
        given:
        String hello = 'Hello, World'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        messageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(*_) >> { throw new RuntimeException("Boom!") }
        1 * mockLogger.error(_) >> {
            error ->
                assert error[0].startsWith('exception: RuntimeException - Boom!, when sending message:')
        }
    }

    def "init create exchange and template"() {
        given:
        ConnectionFactory mockConnectionFactory = Mock(ConnectionFactory)
        Connection mockConnection = Mock(Connection)
        Channel mockChannel = Mock(Channel)
        GroovyMock(RabbitTemplate, global: true)
        messageSender.rabbitTemplate = null

        when:
        messageSender.init()

        then:
        1 * mockRabbitConfiguration.connectionFactory >> mockConnectionFactory
        1 * mockConnectionFactory.createConnection() >> mockConnection
        1 * mockConnection.createChannel(true) >> mockChannel
        1 * mockChannel.exchangeDeclare(mockExchange, 'fanout', true)
        1 * new RabbitTemplate(mockConnectionFactory) >> mockRabbitTemplate
        messageSender.rabbitTemplate == mockRabbitTemplate
    }

    def "is auto startup"() {
        expect:
        messageSender.isAutoStartup()

    }

    def "stop with callback call stop then callback"() {
        given:
        Boolean callbackCalled = false
        def callback = { -> callbackCalled = true }
        Boolean stopCalled = false
//        MessageSenderTestImpl.metaClass.stop = { stopCalled = true }

        when:
        messageSender.stop(callback)

        then:
//        stopCalled
        callbackCalled
    }

    @Ignore
    def "start calls init"() {
        given:
        Boolean initCalled = false
        MessageSenderTestImpl.metaClass.init = { initCalled = true }

        when:
        messageSender.start()

        then:
        initCalled
    }

    def "stop set rabbitTemplate to null"() {
        given:
        messageSender.rabbitTemplate = new RabbitTemplate()

        when:
        messageSender.stop()

        then:
        messageSender.rabbitTemplate == null
    }

    def "is running based on rabbit template"() {
        given:
        messageSender.rabbitTemplate = givenTemplate

        when:
        Boolean result = messageSender.isRunning()

        then:
        result == expected

        where:
        givenTemplate        | expected
        null                 | false
        new RabbitTemplate() | true
    }
}
