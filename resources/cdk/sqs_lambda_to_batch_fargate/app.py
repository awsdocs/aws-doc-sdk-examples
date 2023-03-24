#!/usr/bin/env python3

from aws_cdk import App
import aws_cdk as cdk

from consumer_stack.consumer_stack import ConsumerStack

env_USA = cdk.Environment(account="260778392212", region="us-east-1")

app = App()
ConsumerStack(app, "ConsumerStack", env=env_USA)

app.synth()
