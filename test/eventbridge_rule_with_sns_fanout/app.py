#!/usr/bin/env python3

import os

import aws_cdk as cdk
from aws_cdk import App
from producer_stack.producer_stack import ProducerStack

app = App()
ProducerStack(
    app,
    "ProducerStack",
    env=cdk.Environment(
        account=os.getenv("CDK_DEFAULT_ACCOUNT"), region=os.getenv("CDK_DEFAULT_REGION")
    ),
)

app.synth()
