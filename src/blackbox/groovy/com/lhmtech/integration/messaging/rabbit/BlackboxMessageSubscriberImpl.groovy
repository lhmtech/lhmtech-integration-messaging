package com.lhmtech.integration.messaging.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by lihe on 16-12-2.
 */
@Component(value = "BLACKBOX_SUBSCRIBER")
class BlackboxMessageSubscriberImpl extends BaseMessageSubscriber {
    @Value('${message.subscriber.exchange}')
    String exchangeName

    @Value('${message.subscriber.queue}')
    String queueName

    @Override
    void subscribe(byte[] messageBytes) {
        String message = new String(messageBytes)
        println('recv message ...')
    }

    @Override
    String getExchange() {
        exchangeName
    }

    @Override
    String getQueue() {
        queueName
    }
}
