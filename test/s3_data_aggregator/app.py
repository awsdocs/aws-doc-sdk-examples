#!/usr/bin/env python3

import os

import aws_cdk as cdk
from aws_cdk import App
from data_stack.data_stack import DataStack

app = App()
DataStack(
    app,
    "DataStack",
    env=cdk.Environment(
        account=os.getenv("CDK_DEFAULT_ACCOUNT"), region=os.getenv("CDK_DEFAULT_REGION")
    ),
)

app.synth()
