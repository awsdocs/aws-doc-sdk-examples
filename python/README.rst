.. Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

####################################
AWS SDK for Python (Boto 3) examples
####################################

These are examples for the AWS SDK for Python (Boto 3) public documentation.

Prerequisites
=============

To build and run the Python examples, you'll need:

- `Python 3 <https://www.python.org/downloads/>`_
- `pip <https://pip.pypa.io/en/stable/installing/>`_
- `AWS SDK for Python (Boto 3) <https://boto3.amazonaws.com/v1/documentation/api/latest/guide/quickstart.html>`_
- AWS credentials

For instructions on installing the AWS SDK for Python (Boto 3) and setting up
credentials, see `Boto 3 Docs Quickstart <https://boto3.amazonaws.com/v1/documentation/api/latest/guide/quickstart.html>`_.

Running the examples
====================

Many of the examples are written for execution in a command prompt window or
Python console.

For example, to run the s3.py example in a command prompt window, run the following.

::

    python -m s3 bucket_name region

Documentation
=============

For detailed documentation for the AWS SDK for Python (Boto 3), see the following:

- `AWS SDK for Python (Boto 3) Documentation <https://docs.aws.amazon.com/pythonsdk/>`_
- `Boto 3 Docs Quickstart <https://boto3.amazonaws.com/v1/documentation/api/latest/guide/quickstart.html>`_

Contributing
============

Use pylint to verify that your code follows the coding standards for the
repository, including the custom pylint rules found in
`tools/pylintrc <tools/pylintrc>`_. To run
pylint with this configuration, use the ``--rcfile`` option.

For example, to check s3.py, run the following.

::

    pylint s3.py --rcfile=../../tools/pylintrc