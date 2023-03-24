#!/usr/bin/env python3

from aws_cdk import App

from producer_stack.producer_stack import ProducerStack

app = App()
ProducerStack(app, "ProducerStack")

app.synth()
