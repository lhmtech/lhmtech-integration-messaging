package com.lhmtech.integration.messaging.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by lihe on 16-12-2.
 */
@Component(value='BOX')
class BlackboxMessagePublisherImpl extends BaseMessagePublisher {
    @Value('${message.box.exchange}')
    String exchangeName

    @Override
    String getExchange() {
        return exchangeName
    }
}
