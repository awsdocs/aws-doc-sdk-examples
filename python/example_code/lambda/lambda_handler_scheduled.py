# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to implement an AWS Lambda function that handles invocation from
Amazon EventBridge.
"""

import calendar
import logging
import dateutil.parser

logger = logging.getLogger()
logger.setLevel(logging.INFO)


def lambda_handler(event, context):
    """
    Logs the call with a friendly message and the full event data.

    :param event: The event dict that contains the parameters sent when the function
                  is invoked.
    :param context: The context in which the function is called.
    :return: The result of the specified action.
    """
    if 'time' in event:
        dt = dateutil.parser.parse(event['time'])
        logger.info(
            "Thanks for calling me on %s at %s.",
            calendar.day_name[dt.weekday()], dt.time().isoformat())
    logger.info("Full event: %s", event)
