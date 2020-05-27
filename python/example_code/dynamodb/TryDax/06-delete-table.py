#!/usr/bin/env python3

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Deletes the Amazon DynamoDB table used in the demonstration.
"""

# snippet-start:[dynamodb.Python.TryDax.06-delete-table]
import boto3


def delete_dax_table(dyn_resource=None):
    """
    Deletes the demonstration table.

    :param dyn_resource: Either a Boto3 or DAX resource.
    """
    if dyn_resource is None:
        dyn_resource = boto3.resource('dynamodb')

    table = dyn_resource.Table('TryDaxTable')
    table.delete()

    print(f"Deleting {table.name}...")
    table.wait_until_not_exists()


if __name__ == '__main__':
    delete_dax_table()
    print("Table deleted!")
# snippet-end:[dynamodb.Python.TryDax.06-delete-table]
