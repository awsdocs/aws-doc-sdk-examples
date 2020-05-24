# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
A factory function that returns the stubber for an AWS service, based on the
name of the service that is used by Boto 3.

This factory is used by the make_stubber fixture found in the set of common fixtures.
"""

from test_tools.dynamodb_stubber import DynamoStubber
from test_tools.iam_stubber import IamStubber
from test_tools.lambda_stubber import LambdaStubber
from test_tools.pinpoint_stubber import PinpointStubber
from test_tools.s3_stubber import S3Stubber
from test_tools.s3control_stubber import S3ControlStubber
from test_tools.sqs_stubber import SqsStubber
from test_tools.sts_stubber import StsStubber


class StubberFactoryNotImplemented(Exception):
    pass


def stubber_factory(service_name):
    if service_name == 'dynamodb':
        return DynamoStubber
    elif service_name == 'iam':
        return IamStubber
    elif service_name == 'lambda':
        return LambdaStubber
    elif service_name == 'pinpoint':
        return PinpointStubber
    elif service_name == 's3':
        return S3Stubber
    elif service_name == 's3control':
        return S3ControlStubber
    elif service_name == 'sqs':
        return SqsStubber
    elif service_name == 'sts':
        return StsStubber
    else:
        raise StubberFactoryNotImplemented(
            "If you see this exception, it probably means that you forgot to add "
            "a new stubber to stubber_factory.py.")
