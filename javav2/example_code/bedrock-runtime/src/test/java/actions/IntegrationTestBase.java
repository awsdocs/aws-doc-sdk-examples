// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public abstract class IntegrationTestBase {
    protected void assertNotNullOrEmpty(String string) {
        assertNotNull(string);
        assertFalse(string.trim().isEmpty());
    }
}
