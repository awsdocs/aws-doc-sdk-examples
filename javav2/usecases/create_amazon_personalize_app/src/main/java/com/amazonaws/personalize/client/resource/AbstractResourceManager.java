/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
package com.amazonaws.personalize.client.resource;

import java.io.IOException;

import software.amazon.awssdk.services.personalize.PersonalizeClient;

public abstract class AbstractResourceManager implements ResourceManager {

    private final PersonalizeClient personalize;
    private final String name;

    public AbstractResourceManager(PersonalizeClient personalize, String name) {
        this.personalize = personalize;
        this.name = name;
    }

    protected abstract String createResourceInternal();

    protected abstract void deleteResourceInternal(String arn);

    protected abstract String getResourceStatus(String arn);

    protected abstract String getArnForResource(String name);

    protected PersonalizeClient getPersonalize() {
        return personalize;
    }

    protected String getName() {
        return name;
    }

    private void waitforResource(String arn) {
        while (!isResourceInTerminalState(arn)) {
            System.out.println("Waiting for resource " + name + " to get into terminal state.");
            try {
                Thread.sleep(60 * 1000L);
            } catch (InterruptedException e) {
            }
        }
    }

    private boolean isResourceInTerminalState(String arn) {
        if (arn == null) {
            return true;
        }
        String status = getResourceStatus(arn);
        if (status == null) {
            return true;
        }
        return !status.contentEquals("CREATE PENDING")
                && !status.contentEquals("CREATE IN_PROGRESS")
                && !status.contentEquals("DELETE PENDING")
                && !status.contentEquals("DELETE IN_PROGRESS")
                && !status.contentEquals("CREATE STOPPING")
                && !status.contentEquals("CREATE STOPPED");
    }


    public String createAndWaitForResource(boolean skipIfAlreadyExists) throws IOException, ResourceException {
        String arn = getArnForResource(name);

        if (!isResourceInTerminalState(arn)) {
            waitforResource(arn);
        }

        if (arn != null) {
            String status = getResourceStatus(arn);
            if (skipIfAlreadyExists && "ACTIVE".equals(status)) {
                return arn;
            }
            if ("CREATE FAILED".equals(status)) {
                deleteResouce(name);
            }
        }
        arn = createResourceInternal();
        waitforResource(arn);

        return arn;
    }

    public void deleteResouce(String name) throws IOException, ResourceException {
        String arn = getArnForResource(name);
        if (arn == null) {
            return;
        }

        deleteResourceInternal(arn);
        waitforResource(arn);

    }
}
