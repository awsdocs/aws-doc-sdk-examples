// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package com.example.s3.async;

import com.example.s3.async.SelectObjectContentExample.EventStreamInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SelectObjectContentExampleTest {
    private static final Logger logger = LoggerFactory.getLogger(SelectObjectContentExampleTest.class);
    private SelectObjectContentExample selectObjectContentExample;

    @BeforeAll
    static void beforeAll() {
        SelectObjectContentExample.setUp();
    }

    @AfterAll
    static void afterAll() {
        SelectObjectContentExample.tearDown();
    }

    @BeforeEach
    void setUp() {
        selectObjectContentExample = new SelectObjectContentExample();
    }

    @Test
    void selectJSONObjectContentTest() {
        EventStreamInfo eventStreamInfo = selectObjectContentExample.runSelectObjectContentMethodForJSON();
        Assertions.assertTrue(eventStreamInfo.getCountOnRecordsCalled() > 0);
    }

    @Test
    void selectCSVObjectContentTest() {
        EventStreamInfo eventStreamInfo = selectObjectContentExample.runSelectObjectContentMethodForCSV();
        Assertions.assertTrue(eventStreamInfo.getCountOnRecordsCalled() > 0);
    }
}