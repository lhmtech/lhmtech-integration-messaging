package com.lhmtech.messaging.rabbit

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by lihe on 16-12-1.
 */
@Retention(RetentionPolicy.RUNTIME)
@interface MessageSender {
    String to
}

