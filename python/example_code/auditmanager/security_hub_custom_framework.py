# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[auditmanager.Python.security_hub_custom_framework.create]
"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager to create a
custom framework with all standard controls using AWS Security Hub as their data source.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
auditmanager_client = boto3.client('auditmanager')


def get_sechub_controls():
    print('-' * 40)
    next_token = None
    page = 1
    sechub_control_list = []
    while True:
        print(" Page [" + str(page) + "]")
        if next_token is None:
            control_list = auditmanager_client.list_controls(
                controlType='Standard',
                maxResults=100
            )
        else:
            control_list = auditmanager_client.list_controls(
                controlType='Standard',
                nextToken=next_token,
                maxResults=100
            )
        print('Controls found:', len(control_list.get('controlMetadataList')))
        for control in control_list.get('controlMetadataList'):
            control_details = auditmanager_client.get_control(controlId=control.get('id'))
            if "AWS Security Hub" in control_details.get('control').get('controlSources'):
                sechub_control_list.append({'id': control_details.get('control').get('id')})
        next_token = control_list.get('nextToken')
        if not next_token:
            break
        page += 1
    print('Number of Security Hub controls found: ', len(sechub_control_list))
    return sechub_control_list


def create_custom_framework():
    """
    Create custom framework
    :param self:
    :return: None
    """
    try:
        am_controls = get_sechub_controls()
        print('Creating custom framework ')
        custom_framework = auditmanager_client.create_assessment_framework(
            name='All Security Hub Controls Framework',
            controlSets=[
                {
                    'name': "Security-Hub",
                    'controls': am_controls
                },
            ]
        )
        print('Successfully created the custom framework: ', custom_framework.get('framework').get('name'), ' : ',
              custom_framework.get('framework').get('id'))
        print('-' * 88)
    except ClientError:
        logger.exception("Failed to create custom framework")
        raise


if __name__ == '__main__':
    print('-' * 88)
    print("     Welcome to the AWS AuditManager Samples Demo!")
    print('-' * 88)
    print(" This script creates a custom framework with all Security Hub controls.")
    print('-' * 88)
    create_custom_framework()
# snippet-end:[auditmanager.Python.security_hub_custom_framework.create]