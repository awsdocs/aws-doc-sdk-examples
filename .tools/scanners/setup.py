# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from setuptools import setup

setup(
    name="aws_doc_sdk_examples_readmes",
    version="0.0.1",
    entry_points={
        "console_scripts": ["writeme=writeme:main"],
    },
    build_requires=[
        "setuptools>=40.8.0",
        "wheel",
    ],
    install_requires=[
        "jinja2>=3.1.4",
        "pyyaml>=5.3.1",
        "aws-doc-sdk-examples-tools @ git+https://github.com/awsdocs/aws-doc-sdk-examples-tools",
    ],
)
