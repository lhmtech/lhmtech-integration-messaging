package com.lhmtech.integration.messaging.rabbit

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener

/**
 * Created by lihe on 16-12-2.
 */
abstract class BaseMessageReceiver implements ChannelAwareMessageListener {
    abstract void receive(byte[] messageBytes)

    @Override
    void onMessage(Message message, Channel channel) throws Exception {

    }
}
