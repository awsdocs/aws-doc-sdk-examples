# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Contains common test configuration used to run the Amazon ECS unit tests.
"""

import os
import sys

# The ecs_wrapper module lives in the parent directory
# (python/example_code/ecs/). Add it to the path so the tests can import it
# without needing any special working directory or installation step.
script_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.append(os.path.dirname(script_dir))
