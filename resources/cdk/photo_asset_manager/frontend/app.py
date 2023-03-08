#!/usr/bin/env python3
import os

import aws_cdk as cdk

from frontend.frontend_stack import FrontendStack

name = os.environ["PAM_NAME"]
backend_stack_id = os.environ["BACKEND_STACK_ID"]

app = cdk.App()
FrontendStack(app, name, backend_stack_id)
app.synth()
