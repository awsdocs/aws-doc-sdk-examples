#!/usr/bin/env python3
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
import os

import aws_cdk as cdk
from public_ecr_repositories_stack.public_ecr_repositories_stack import (
    PublicEcrRepositoriesStack,
)

app = cdk.App()
PublicEcrRepositoriesStack(
    app,
    "PublicEcrRepositoriesStack",
    env=cdk.Environment(
        account=os.getenv("CDK_DEFAULT_ACCOUNT"), region=os.getenv("CDK_DEFAULT_REGION")
    ),
)

app.synth()
