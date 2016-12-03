package com.lhmtech.integration.messaging.rabbit

/**
 * Created by lihe on 16-12-2.
 */
class MessageSubscriberTestImpl extends BaseMessageSubscriber {
    @Override
    void subscribe(byte[] messageBytes) {

    }

    @Override
    String getExchange() {
        return null
    }

    @Override
    String getQueue() {
        return null
    }
}
