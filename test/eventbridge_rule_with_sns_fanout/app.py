#!/usr/bin/env python3

from aws_cdk import App
import aws_cdk as cdk

from producer_stack.producer_stack import ProducerStack

import os

app = App()
ProducerStack(app, "ProducerStack",
    env=cdk.Environment(account=os.getenv('CDK_DEFAULT_ACCOUNT'), region=os.getenv('CDK_DEFAULT_REGION')),
    )

app.synth()