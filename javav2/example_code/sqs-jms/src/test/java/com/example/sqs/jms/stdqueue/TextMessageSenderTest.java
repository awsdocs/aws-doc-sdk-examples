// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TextMessageSenderTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextMessageSenderTest.class);
    @Test
    void doSendTextMessageTest() {
        // The TextMessageSender.doSendTextMessage() can't be tested with an automated test because it requires user input.
    }

}