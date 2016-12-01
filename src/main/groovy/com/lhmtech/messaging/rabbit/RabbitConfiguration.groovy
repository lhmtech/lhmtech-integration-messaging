package com.lhmtech.messaging.rabbit

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
/**
 * Created by lihe on 16-12-1.
 */
@Configuration
class RabbitConfiguration {
    @Value('${rabbit.host}')
    String host
    @Value('${rabbit.port}')
    Integer port
    @Value('${rabbit.useSSL}')
    Boolean useSSL

    ConnectionFactory getConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host)
        connectionFactory.setPort(port)
        if (useSSL) {
            connectionFactory.rabbitConnectionFactory.useSslProtocol('TLSv1.2')
        }
        return connectionFactory
    }
}
