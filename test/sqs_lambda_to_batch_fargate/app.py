#!/usr/bin/env python3

from aws_cdk import App
import aws_cdk as cdk

from consumer_stack.consumer_stack import ConsumerStack

import os

app = App()
ConsumerStack(app, f"ConsumerStack-{os.getenv('LANGUAGE_NAME')}",
              env=cdk.Environment(account=os.getenv('CDK_DEFAULT_ACCOUNT'), region=os.getenv('CDK_DEFAULT_REGION')),
              )

app.synth()
