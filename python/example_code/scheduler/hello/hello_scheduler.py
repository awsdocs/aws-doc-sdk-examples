# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[python.example_code.scheduler.Hello]
import boto3


def hello_scheduler(scheduler_client):
    """
    Use the AWS SDK for Python (Boto3) to create an Amazon EventBridge Scheduler
    client and list the schedules in your account.
    This example uses the default settings specified in your shared credentials
    and config files.

    :param scheduler_client: A Boto3 Amazon EventBridge Scheduler Client object. This object wraps
                             the low-level Amazon EventBridge Scheduler service API.
    """
    print("Hello, Amazon EventBridge Scheduler! Let's list some of your schedules:\n")
    paginator = scheduler_client.get_paginator("list_schedules")
    page_iterator = paginator.paginate(PaginationConfig={"MaxItems": 10})

    schedule_names: [str] = []
    for page in page_iterator:
        for schedule in page["Schedules"]:
            schedule_names.append(schedule["Name"])

    print(f"{len(schedule_names)} schedule(s) retrieved.")
    for schedule_name in schedule_names:
        print(f"\t{schedule_name}")


if __name__ == "__main__":
    hello_scheduler(boto3.client("scheduler"))
# snippet-end:[python.example_code.scheduler.Hello]
