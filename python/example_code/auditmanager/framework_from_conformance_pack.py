# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

# snippet-start:[auditmanager.Python.framework_from_conformance_pack.create]
"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager and AWS Config to create a
custom control and a custom framework from a conformance pack with managed rules in AWS Config.
"""

import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)
config_client = boto3.client('config')
auditmanager_client = boto3.client('auditmanager')


def get_conformance_pack():
    """
    Return a selected conformance pack from the list of conformance packs
    :param self:
    :return: selected conformance pack
    """
    try:
        conformance_packs = config_client.describe_conformance_packs()
        print("Number of conformance packs fetched : ", len(conformance_packs.get("ConformancePackDetails")))
        print("Fetched the following conformance packs : ")
        all_cpack_names = {}
        for cp in conformance_packs.get("ConformancePackDetails"):
            all_cpack_names[cp.get("ConformancePackName")] = 1
            print("  " + cp.get("ConformancePackName"))
        cpack_name = input('Provide ConformancePackName that you want to create a custom framework for : ')
        print('Provided conformance pack is: ', cpack_name)
        if all_cpack_names.get(cpack_name) is None:
            print('Provide a conformance pack name from the available list of conformance packs')
            raise Exception("Invalid conformance pack")
        print('-' * 88)
        return cpack_name
    except ClientError:
        logger.exception("Couldn't select conformance pack")
        raise


def create_custom_controls(cpack_name):
    """
    Create custom controls for all managed AWS Config rules in a conformance pack
    :param cpack_name:
    :param self:
    :return: list of custom control ids
    """
    try:
        rules_in_pack = config_client.describe_conformance_pack_compliance(ConformancePackName=cpack_name)
        print('Number of rules in the conformance pack: ', len(rules_in_pack.get('ConformancePackRuleComplianceList')))
        for rule in rules_in_pack.get('ConformancePackRuleComplianceList'):
            print(rule.get('ConfigRuleName'))
        print('-' * 88)
        print('Creating a custom control for each rule and a custom framework consisting of these rules in Audit Manager')
        am_controls = []
        for rule in rules_in_pack.get('ConformancePackRuleComplianceList'):
            config_rule = config_client.describe_config_rules(ConfigRuleNames=[rule.get('ConfigRuleName')])
            source_id = config_rule.get('ConfigRules')[0].get('Source').get('SourceIdentifier')
            custom_control = auditmanager_client.create_control(
                name="Config-" + rule.get('ConfigRuleName'),
                controlMappingSources=[{'sourceName': 'ConfigRule',
                                        'sourceSetUpOption': 'System_Controls_Mapping',
                                        'sourceType': 'AWS_Config',
                                        'sourceKeyword': {
                                            'keywordInputType': 'SELECT_FROM_LIST',
                                            'keywordValue': source_id}, }]).get('control')
            am_controls.append({'id': custom_control.get('id')})
        print('Successfully created a control for each config rule')
        print('-' * 88)
        return am_controls
    except ClientError:
        logger.exception("Failed to create custom controls")
        raise


def create_custom_framework():
    """
    Create custom framework for a selected conformance pack
    :param self:
    :return: None
    """
    try:
        cpack_name = get_conformance_pack()
        am_controls = create_custom_controls(cpack_name)
        print('Creating custom framework ')
        custom_framework = auditmanager_client.create_assessment_framework(
            name='Config-Conformance-pack-' + cpack_name,
            controlSets=[
                {
                    'name': cpack_name,
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


# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    print('-' * 88)
    print("     Welcome to the AWS AuditManager Samples Demo!")
    print('-' * 88)
    print("You can use this sample to select a conformance pack from AWS Config and create a custom"
          " control for all the managed rules under the conformance pack. You can then create a custom framework with these"
          " controls.")
    print('-' * 88)
    create_custom_framework()
# snippet-end:[auditmanager.Python.framework_from_conformance_pack.create]