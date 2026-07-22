# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
AWS Lambda handler for processing Amazon Kinesis Data Streams events.
"""

import base64
import json
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)


# snippet-start:[python.example_code.kinesis.handler.kinesis_lambda]
def lambda_handler(event, context):
    """
    Lambda function handler that processes Kinesis Data Streams events.
    Decodes and processes each record from the batch.

    :param event: The Kinesis event containing a batch of records.
    :param context: The Lambda context object.
    """
    for record in event["Records"]:
        # Kinesis data is base64 encoded
        payload = base64.b64decode(record["kinesis"]["data"]).decode("utf-8")
        logger.info("Decoded payload: %s", payload)
        # Process the record data
        data = json.loads(payload)
        logger.info("Processed record: %s", data)


# snippet-end:[python.example_code.kinesis.handler.kinesis_lambda]


# snippet-start:[python.example_code.kinesis.handler.kinesis_lambda_batch_item_failures]
def lambda_handler_batch_failures(event, context):
    """
    Lambda function handler that processes Kinesis events with
    partial batch failure reporting. Returns failed record sequence
    numbers so Lambda retries only those records.

    :param event: The Kinesis event containing a batch of records.
    :param context: The Lambda context object.
    :return: A dict with batchItemFailures listing failed record identifiers.
    """
    batch_item_failures = list()

    for record in event["Records"]:
        try:
            # Kinesis data is base64 encoded
            payload = base64.b64decode(record["kinesis"]["data"]).decode("utf-8")
            data = json.loads(payload)
            logger.info("Successfully processed record: %s", data)
        except Exception as e:
            logger.error(
                "Failed to process record %s: %s",
                record["kinesis"]["sequenceNumber"],
                e,
            )
            batch_item_failures.append(
                {"itemIdentifier": record["kinesis"]["sequenceNumber"]}
            )

    return {"batchItemFailures": batch_item_failures}


# snippet-end:[python.example_code.kinesis.handler.kinesis_lambda_batch_item_failures]