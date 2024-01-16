#!/bin/sh
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#
# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
#
# A script that is run by `swiftbuild.py` instead of using `swiftc test` to
# test this example, which does not have a standard Swift test available.
#
# A swiftbuild test script returns 0 on success, any other integer on fail.

# There are no tests for this example, so always pass.
exit 0