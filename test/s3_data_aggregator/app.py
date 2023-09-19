#!/usr/bin/env python3

from aws_cdk import App
import aws_cdk as cdk

from data_stack.data_stack import DataStack

import os

app = App()
DataStack(app, "DataStack",
    env=cdk.Environment(account=os.getenv('CDK_DEFAULT_ACCOUNT'), region=os.getenv('CDK_DEFAULT_REGION')),
    )

app.synth()