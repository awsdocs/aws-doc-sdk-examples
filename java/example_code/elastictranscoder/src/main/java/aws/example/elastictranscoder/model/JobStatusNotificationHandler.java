// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
// snippet-start:[elastictranscoder.java.job_status_notification_handler.import]
package com.amazonaws.services.elastictranscoder.samples.model;

public interface JobStatusNotificationHandler {

    public void handle(JobStatusNotification jobStatusNotification);
}
// snippet-end:[elastictranscoder.java.job_status_notification_handler.import]
