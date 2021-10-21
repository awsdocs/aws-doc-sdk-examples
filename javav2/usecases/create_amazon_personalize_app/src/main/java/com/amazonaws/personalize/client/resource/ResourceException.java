/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

public class ResourceException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String name;
    private final String arn;

    public ResourceException(String name, String arn) {
        super();
        this.name = name;
        this.arn = arn;
    }

    @Override
    public String getMessage() {
        return "Resource creation completed unsuccessfully. Resource name: " + name + ", Arn: " + arn + ". Please check you resource status and retry.";
    }

}
