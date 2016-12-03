package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.connection.Connection
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle

/**
 * Created by lihe on 16-11-30.
 */
abstract class BaseMessagePublisher implements SmartLifecycle{
    Logger logger = LoggerFactory.getLogger(this.getClass())
    abstract String getExchange()
    RabbitTemplate rabbitTemplate

    @Autowired
    RabbitConfiguration rabbitConfiguration
//    ConnectionFactory connectionFactory

    void send(String text) {
        try{
        Message message = new Message(text.bytes, null)
        rabbitTemplate.convertAndSend(this.getExchange(), null, message)
        }catch(exception) {
            logger.error("exception: ${exception.class.simpleName} - ${exception.message}, when sending message: ${text}")
        }
    }

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
        init()
    }

    @Override
    void stop() {
        rabbitTemplate = null
    }

    @Override
    boolean isRunning() {
        return rabbitTemplate != null
    }

    @Override
    int getPhase() {
        return Integer.MAX_VALUE
    }

    void init() {
        ConnectionFactory connectionFactory = rabbitConfiguration.connectionFactory
        Connection connection = connectionFactory.createConnection()
        Channel channel = connection.createChannel(true)
        channel.exchangeDeclare(this.getExchange(), 'fanout', true)
        rabbitTemplate = new RabbitTemplate(connectionFactory)
    }
}
