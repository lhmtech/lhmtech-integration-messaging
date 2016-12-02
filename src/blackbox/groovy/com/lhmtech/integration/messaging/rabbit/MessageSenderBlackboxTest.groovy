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
class MessageSenderBlackboxTest extends Specification {
    @Autowired
    @Qualifier('BOX')
    BaseMessageSender basicMessageSender

    def "should boot up without errors"() {
        expect:
        basicMessageSender != null
        basicMessageSender.getExchange() == 'box-exchange'
    }

    def "send message hello"() {
        when:
        basicMessageSender.send("hello")

        then:
        true

    }
}
