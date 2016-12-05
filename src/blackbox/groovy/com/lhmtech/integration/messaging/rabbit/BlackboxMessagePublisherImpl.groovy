package com.lhmtech.integration.messaging.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Created by lihe on 16-12-2.
 */
@Component(value='BLACKBOX_PUBLISHER')
class BlackboxMessagePublisherImpl extends BaseMessagePublisher {
    @Value('${message.publisher.exchange}')
    String exchangeName

    @Override
    String getExchange() {
        return exchangeName
    }
}
