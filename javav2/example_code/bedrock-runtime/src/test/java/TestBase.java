/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public abstract class TestBase {

    protected void assertNotNullOrEmpty(String string) {
        assertNotNull(string);
        assertFalse(string.trim().isEmpty());
    }
}
