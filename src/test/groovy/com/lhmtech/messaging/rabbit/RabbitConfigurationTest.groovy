package com.lhmtech.messaging.rabbit

import com.rabbitmq.client.ConnectionFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory

import spock.lang.Specification

/**
 * Created by lihe on 16-12-1.
 */
class RabbitConfigurationTest extends Specification {

    def "configuration with ssl"() {
        given:
        CachingConnectionFactory mockCachingConnectionFactory = GroovyMock(CachingConnectionFactory, global: true)
        ConnectionFactory mockRabbitConnectionFactory = Mock(ConnectionFactory)
        RabbitConfiguration rabbitConfiguration =
                new RabbitConfiguration(host: 'host', port: 1111, useSSL: true)

        when:
        rabbitConfiguration.getConnectionFactory()

        then:
        1 * new CachingConnectionFactory('host') >> mockCachingConnectionFactory
        1 * mockCachingConnectionFactory.setPort(1111)
        1 * mockCachingConnectionFactory.rabbitConnectionFactory >> mockRabbitConnectionFactory
        1 * mockRabbitConnectionFactory.useSslProtocol('TLSv1.2')
        rabbitConfiguration.port == 1111
        rabbitConfiguration.host == 'host'
    }

    def "configuration without ssl"() {
        given:
        CachingConnectionFactory mockCachingConnectionFactory = GroovyMock(CachingConnectionFactory, global: true)
        ConnectionFactory mockRabbitConnectionFactory = Mock(ConnectionFactory)
        RabbitConfiguration rabbitConfiguration =
                new RabbitConfiguration(host: 'host', port: 1111, useSSL: false)

        when:
        rabbitConfiguration.getConnectionFactory()

        then:
        1 * new CachingConnectionFactory('host') >> mockCachingConnectionFactory
        1 * mockCachingConnectionFactory.setPort(1111)
        0 * mockCachingConnectionFactory.rabbitConnectionFactory >> mockRabbitConnectionFactory
        0 * mockRabbitConnectionFactory.useSslProtocol('TLSv1.2')
        rabbitConfiguration.port == 1111
        rabbitConfiguration.host == 'host'
    }
}
