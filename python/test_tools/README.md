# Test tools for Python code examples

## Purpose

Centralize test fixtures and stubbers so they can be used across all Python
code examples and can take advantage of a common structure.

## Prerequisites

- Python 3.6 or later
- Boto 3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)

## Using the tools

Include the tools in a test folder by adding the following code to the top of
your conftest.py file. The path should point from your module folder to the main
`python` folder of your repo.

```
import sys
# This is needed so Python can find test_tools on the path.
sys.path.append('../..')
from test_tools.fixtures.common import *
```

Pytest automatically finds this conftest.py file and imports it when started.
This code adds the main `python` folder of your repo to the path and imports the
common fixtures from the test tools.

### Stubs versus actual AWS

The main fixture in the test tools is `make_stubber`, which is a factory function
that lets you make a stubber for a specific AWS service client. The stubbers 
inherit from the botocore Stubber and contain stub functions that you can use to 
test the most common functions of the specified client.

All of the stubbers honor the `--use-real-aws-may-incur-charges` command line option.
When this option is not present, stubs are used in all tests and no requests are
made to your AWS account. When this option *is* present, the stubbers let requests
flow through to your actual AWS account, which might incur charges. 

### Example

See the `python/example_code/sqs` folder of this repo for an example of a module
and its accompanying tests that use these centralized test tools.

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
