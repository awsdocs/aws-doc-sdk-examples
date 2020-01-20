.. Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0
   International License (the "License"). You may not use this file except in compliance with the
   License. A copy of the License is located at http://creativecommons.org/licenses/by-nc-sa/4.0/.

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
   either express or implied. See the License for the specific language governing permissions and
   limitations under the License.

================================
Amazon S3 Managed File Transfers
================================

Use the Boto 3 transfer manager to manage multipart uploads to and downloads
from an Amazon S3 bucket.

When the file to transfer is larger than the specified threshold, the transfer
manager automatically uses multipart uploads or downloads. This example
shows how to use several of the available transfer manager settings, and reports
thread usage and time to transfer.

Contents
================

The example contains the following two files.

file_transfer.py
    Creates and calls Boto 3 resources to configure the transfer manager and
    upload and download files.

demo_file_transfer.py
    Interactively demonstrates the code in file_transfer.py.
    Asks questions, takes actions, and manages artifact creation and cleanup.

Run the example
===============

Prerequisites
-------------

To run this example, you'll need the following:

* An Amazon S3 bucket to hold uploaded objects
* A folder on your local drive to hold created and downloaded files

Objects and files created during the demonstration are cleaned up at the end.

Usage
-----

Run the example from the command line.

.. code-block::

    python -m demo_file_transfer

