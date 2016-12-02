package com.lhmtech.messaging.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by lihe on 16-12-2.
 */
@Component(value='PWJ')
class PwjMessageSenderImpl extends com.lhmtech.messaging.rabbit.BaseMessageSender {
    @Value('${message.pwj.exchange}')
    String exchangeName

    @Override
    String getExchange() {
        return exchangeName
    }
}
