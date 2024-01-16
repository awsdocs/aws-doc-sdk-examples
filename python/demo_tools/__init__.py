# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0
def demo_func(func):
    def wrapper(*args, **kwargs):
        print("-" * 88)
        result = func(*args, **kwargs)
        print("-" * 88)
        return result

    return wrapper
