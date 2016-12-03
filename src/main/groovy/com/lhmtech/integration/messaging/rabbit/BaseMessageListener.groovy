package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener
import org.springframework.amqp.rabbit.core.RabbitAdmin

/**
 * Created by lihe on 16-12-2.
 */
abstract class BaseMessageListener implements ChannelAwareMessageListener {
    abstract void receive(byte[] messageBytes)

    RabbitAdmin rabbitAdmin

    @Override
    void onMessage(Message message, Channel channel) throws Exception {

    }
}
