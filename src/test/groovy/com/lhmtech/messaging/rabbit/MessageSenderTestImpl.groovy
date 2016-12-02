package com.lhmtech.messaging.rabbit

/**
 * Created by lihe on 16-12-2.
 */
class MessageSenderTestImpl extends BaseMessageSender{
    String exchangeName
    @Override
    String getExchange() {
        return exchangeName
    }
}
