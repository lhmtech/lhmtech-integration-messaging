package com.lhmtech.integration.messaging.rabbit

/**
 * Created by lihe on 16-12-2.
 */
class MessagePublisherTestImpl extends BaseMessagePublisher{
    @Override
    String getExchange() {
        return 'test-publisher-exchange'
    }
}
