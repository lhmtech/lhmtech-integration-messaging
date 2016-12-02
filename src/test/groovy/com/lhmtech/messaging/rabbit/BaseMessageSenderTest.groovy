package com.lhmtech.messaging.rabbit

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import org.slf4j.Logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

/**
 * Created by lihe on 16-11-30.
 */
class BaseMessageSenderTest extends Specification {

    BaseMessageSender baseMessageSender
    Logger mockLogger
    RabbitTemplate mockRabbitTemplate
    String mockExchange
    RabbitConfiguration mockRabbitConfiguration

    def setup() {
        mockExchange = 'mock-exchange'
        baseMessageSender = new BaseMessageSender() {
            @Override
            String getExchange() {
                mockExchange
            }
        }
        mockRabbitTemplate = Mock(RabbitTemplate)
        baseMessageSender.rabbitTemplate = mockRabbitTemplate
        mockLogger = Mock(Logger)
        baseMessageSender.logger = mockLogger
        mockRabbitConfiguration = Mock(RabbitConfiguration)
        baseMessageSender.rabbitConfiguration = mockRabbitConfiguration
    }

    def "send message"() {
        given:

        String hello='Hello, World'
        String to ='hello-exchange'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        baseMessageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(mockExchange, null, mockMessage)
    }

    def "send message will log error whnen exception occurs"() {
        given:
        String hello='Hello, World'
        String to ='hello-exchange'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        baseMessageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(*_) >> { throw new RuntimeException("Boom!")}
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
        baseMessageSender.rabbitTemplate = null

        when:
        baseMessageSender.init()

        then:
        1 * mockRabbitConfiguration.connectionFactory >> mockConnectionFactory
        1 * mockConnectionFactory.createConnection() >> mockConnection
        1 * mockConnection.createChannel(true) >> mockChannel
        1 * mockChannel.exchangeDeclare(mockExchange, 'fanout', true)
        1 * new RabbitTemplate(mockConnectionFactory) >> mockRabbitTemplate
        baseMessageSender.rabbitTemplate == mockRabbitTemplate
    }
}
