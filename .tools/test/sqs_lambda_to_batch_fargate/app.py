#!/usr/bin/env python3

import os

import aws_cdk as cdk
from aws_cdk import App
from consumer_stack.consumer_stack import ConsumerStack

app = App()
ConsumerStack(
    app,
    f"ConsumerStack-{os.getenv('LANGUAGE_NAME').replace('_', '-')}",
    env=cdk.Environment(
        account=os.getenv("CDK_DEFAULT_ACCOUNT"), region=os.getenv("CDK_DEFAULT_REGION")
    ),
)

app.synth()
