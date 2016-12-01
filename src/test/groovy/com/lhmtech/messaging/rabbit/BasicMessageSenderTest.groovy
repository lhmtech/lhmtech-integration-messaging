package com.lhmtech.messaging.rabbit

import org.slf4j.Logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import spock.lang.Specification

/**
 * Created by lihe on 16-11-30.
 */
class BasicMessageSenderTest extends Specification {

    BasicMessageSender basicMessageSender
    Logger mockLogger
    RabbitTemplate mockRabbitTemplate
    def setup() {
        basicMessageSender = new BasicMessageSender()
        mockRabbitTemplate = Mock(RabbitTemplate)
        basicMessageSender.rabbitTemplate = mockRabbitTemplate
        mockLogger = Mock(Logger)
        basicMessageSender.logger = mockLogger

    }

    def "send message"() {
        given:

        String hello='Hello, World'
        String to ='hello-exchange'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        basicMessageSender.toExchange = to
        basicMessageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(to, null, mockMessage)
    }

    def "send message will log error whnen exception occurs"() {
        given:
        String hello='Hello, World'
        String to ='hello-exchange'
        Message mockMessage = GroovyMock(Message, global: true)

        when:
        basicMessageSender.toExchange = to
        basicMessageSender.send(hello)

        then:
        1 * new Message(hello.bytes, null) >> mockMessage
        1 * mockRabbitTemplate.convertAndSend(*_) >> { throw new RuntimeException("Boom!")}
        1 * mockLogger.error(_) >> {
            error ->
                assert error[0].startsWith('exception: RuntimeException - Boom!, when sending message:')
        }
    }
}
