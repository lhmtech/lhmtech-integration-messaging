package com.lhmtech.messaging.rabbit

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.SmartLifecycle

/**
 * Created by lihe on 16-11-30.
 */
class MessageSender implements SmartLifecycle{
    String exchange
    RabbitTemplate rabbitTemplate

    void send(String message) {

    }

    @Override
    boolean isAutoStartup() {
        return false
    }

    @Override
    void stop(Runnable callback) {

    }

    @Override
    void start() {

    }

    @Override
    void stop() {

    }

    @Override
    boolean isRunning() {
        return false
    }

    @Override
    int getPhase() {
        return 0
    }
}
