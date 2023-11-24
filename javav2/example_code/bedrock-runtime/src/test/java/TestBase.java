/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import org.junit.jupiter.api.Order;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TestBase {

    protected void assertNotNullOrEmpty(String string) {
        assertNotNull(string);
        assertFalse(string.trim().isEmpty());
    }

    protected void printSuccessMessage(Method testMethod) {
        Order order = testMethod.getAnnotation(Order.class);
        System.out.printf("Test %d passed.%n", order != null ? order.value() : 0);
    }
}
