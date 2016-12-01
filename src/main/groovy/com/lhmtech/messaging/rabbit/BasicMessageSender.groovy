package com.lhmtech.messaging.rabbit

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle


/**
 * Created by lihe on 16-11-30.
 */
class BasicMessageSender implements SmartLifecycle{
    Logger logger = LoggerFactory.getLogger(this.getClass())
    String toExchange
    RabbitTemplate rabbitTemplate

    @Autowired
    ConnectionFactory connectionFactory

    void send(String text) {
        try{
        Message message = new Message(text.bytes, null)
        rabbitTemplate.convertAndSend(toExchange, null, message)
        }catch(exception) {
            logger.error("exception: ${exception.class.simpleName} - ${exception.message}, when sending message: ${text}")
        }
    }

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
}
