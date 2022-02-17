/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import java.io.IOException;

public interface ResourceManager {

    String createAndWaitForResource(boolean skipIfAlreadyExists) throws IOException, ResourceException;

    void deleteResouce(String name) throws IOException, ResourceException;

}
