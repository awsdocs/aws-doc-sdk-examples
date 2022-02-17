# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

State machine definitions for the AWS Step Functions demo.
"""


def make_definition(resources, include_sqs):
    """
    Makes a definition for the demo based on resources created by the
    AWS CloudFormation stack.

    :param resources: Resources to inject into the definition.
    :param include_sqs: When True, include a state that sends messages to an Amazon
                        Simple Queue Service (Amazon SQS). Otherwise, don't include
                        this state. This is used by the demo to show how to update
                        a state machine's definition.
    :return: The state machine definition.
    """
    state_record_sent = {
        "Record Sent": {
            "Type": "Task",
            "Resource": "arn:aws:states:::dynamodb:updateItem",
            "Parameters": {
                "TableName": resources["MessageTableName"],
                "Key": {
                    "user_name": {
                        "S.$": "$.user_name"},
                    "message_id": {
                        "S.$": "$.message_id"}},
                "UpdateExpression": "SET sent=:s",
                "ExpressionAttributeValues": {
                    ":s": {"BOOL": True}}},
            "End": True}}

    state_send_to_sqs = {
        "Send": {
            "Type": "Task",
            "Resource": "arn:aws:states:::sqs:sendMessage",
            "Parameters": {
                "QueueUrl": resources["SendQueueUrl"],
                "MessageBody.$": "$.message",
                "MessageAttributes": {
                    "user": {
                        "DataType": "String",
                        "StringValue.$": "$.user_name"},
                    "message_id": {
                        "DataType": "String",
                        "StringValue.$": "$.message_id"}}},
            "ResultPath": None,
            "Next": "Record Sent"}}

    map_states = state_record_sent
    if include_sqs:
        map_states.update(state_send_to_sqs)

    definition = {
        "Comment": "Read messages from DynamoDB in a loop.",
        "StartAt": "Scan DynamoDB For Messages",
        "TimeoutSeconds": 3600,
        "States": {
            "Scan DynamoDB For Messages": {
                "Type": "Task",
                "Resource": resources["ScanFunctionArn"],
                "ResultPath": "$.List",
                "Next": "Send Messages"},
            "Send Messages": {
                "Type": "Map",
                "ItemsPath": "$.List",
                "Iterator": {
                    "StartAt": "Send" if include_sqs else "Record Sent",
                    "States": map_states},
                "ResultPath": None,
                "Next": "Pause Then Loop"},
            "Pause Then Loop": {
                "InputPath": None,
                "Type": "Wait",
                "Seconds": 10,
                "Next": "Scan DynamoDB For Messages"}}}

    return definition
