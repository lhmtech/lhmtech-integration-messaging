package com.lhmtech.integration.messaging.rabbit

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

/**
 * Created by lihe on 16-10-30.
 */
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessagePublishSubscribeBlackboxTest extends Specification {
    @Autowired
    @Qualifier('BLACKBOX_PUBLISHER')
    BaseMessagePublisher messagePublisher

    @Autowired
    @Qualifier('BLACKBOX_SUBSCRIBER')
    BaseMessageSubscriber messageSubscriber

    def "should boot up without errors"() {
        expect:
        messagePublisher != null
        messagePublisher.getExchange() == 'box-exchange'

        messageSubscriber != null
        messageSubscriber.getExchange() == 'box-exchange'
        messageSubscriber.getQueue() == 'box-queue'
    }

    def "publish message hello"() {
        when:
        messagePublisher.publish("hello")
        sleep(10000)

        then:
        true

    }
}
