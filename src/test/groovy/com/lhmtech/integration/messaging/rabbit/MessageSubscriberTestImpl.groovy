package com.lhmtech.integration.messaging.rabbit

/**
 * Created by lihe on 16-12-2.
 */
class MessageSubscriberTestImpl extends BaseMessageSubscriber {
    @Override
    void subscribe(String messageText) {

    }

    @Override
    String getExchange() {
        return 'test-subscriber-exchange'
    }

    @Override
    String getQueue() {
        return 'test-subscriber-queue'
    }
}
