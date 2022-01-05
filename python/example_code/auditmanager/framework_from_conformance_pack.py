# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Audit Manager and AWS Config
to create a custom control and a custom framework from a conformance pack with managed
rules in AWS Config.
"""

# snippet-start:[python.example_code.auditmanager.Scenario_CustomFrameworkFromConformancePack]
import logging
import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class ConformancePack:
    def __init__(self, config_client, auditmanager_client):
        self.config_client = config_client
        self.auditmanager_client = auditmanager_client

    def get_conformance_pack(self):
        """
        Return a selected conformance pack from the list of conformance packs.

        :return: selected conformance pack
        """
        try:
            conformance_packs = self.config_client.describe_conformance_packs()
            print("Number of conformance packs fetched: ",
                  len(conformance_packs.get("ConformancePackDetails")))
            print("Fetched the following conformance packs: ")
            all_cpack_names = {
                cp['ConformancePackName']
                for cp in conformance_packs.get("ConformancePackDetails")}
            for pack in all_cpack_names:
                print(f"\t{pack}")
            cpack_name = input(
                'Provide ConformancePackName that you want to create a custom '
                'framework for: ')
            if cpack_name not in all_cpack_names:
                print(f'{cpack_name} is not in the list of conformance packs!')
                print('Provide a conformance pack name from the available list of '
                      'conformance packs.')
                raise Exception("Invalid conformance pack")
            print('-' * 88)
        except ClientError:
            logger.exception("Couldn't select conformance pack.")
            raise
        else:
            return cpack_name

    def create_custom_controls(self, cpack_name):
        """
        Create custom controls for all managed AWS Config rules in a conformance pack.

        :param cpack_name: The name of the conformance pack to create controls for.
        :return: The list of custom control IDs.
        """
        try:
            rules_in_pack = self.config_client.describe_conformance_pack_compliance(
                ConformancePackName=cpack_name)
            print('Number of rules in the conformance pack: ',
                  len(rules_in_pack.get('ConformancePackRuleComplianceList')))
            for rule in rules_in_pack.get('ConformancePackRuleComplianceList'):
                print(f"\t{rule.get('ConfigRuleName')}")
            print('-' * 88)
            print('Creating a custom control for each rule and a custom framework '
                  'consisting of these rules in Audit Manager.')
            am_controls = []
            for rule in rules_in_pack.get('ConformancePackRuleComplianceList'):
                config_rule = self.config_client.describe_config_rules(
                    ConfigRuleNames=[rule.get('ConfigRuleName')])
                source_id = config_rule.get('ConfigRules')[0].get('Source', {}).get(
                    'SourceIdentifier')
                custom_control = self.auditmanager_client.create_control(
                    name="Config-" + rule.get('ConfigRuleName'),
                    controlMappingSources=[{
                        'sourceName': 'ConfigRule',
                        'sourceSetUpOption': 'System_Controls_Mapping',
                        'sourceType': 'AWS_Config',
                        'sourceKeyword': {
                            'keywordInputType': 'SELECT_FROM_LIST',
                            'keywordValue': source_id}}]).get('control', {})
                am_controls.append({'id': custom_control.get('id')})
            print('Successfully created a control for each config rule.')
            print('-' * 88)
        except ClientError:
            logger.exception("Failed to create custom controls.")
            raise
        else:
            return am_controls

    def create_custom_framework(self, cpack_name, am_control_ids):
        """
        Create a custom Audit Manager framework from a selected AWS Config conformance
        pack.

        :param cpack_name: The name of the conformance pack to create a framework from.
        :param am_control_ids: The IDs of the custom controls created from the
                               conformance pack.
        """
        try:
            print('Creating custom framework...')
            custom_framework = self.auditmanager_client.create_assessment_framework(
                name='Config-Conformance-pack-' + cpack_name,
                controlSets=[{'name': cpack_name, 'controls': am_control_ids}])
            print(f"Successfully created the custom framework: ",
                  f"{custom_framework.get('framework').get('name')}: ",
                  f"{custom_framework.get('framework').get('id')}")
            print('-' * 88)
        except ClientError:
            logger.exception("Failed to create custom framework.")
            raise


def run_demo():
    print('-' * 88)
    print("Welcome to the AWS Audit Manager custom framework demo!")
    print('-' * 88)
    print("You can use this sample to select a conformance pack from AWS Config and "
          "use AWS Audit Manager to create a custom control for all the managed "
          "rules under the conformance pack. A custom framework is also created "
          "with these controls.")
    print('-' * 88)
    conf_pack = ConformancePack(boto3.client('config'), boto3.client('auditmanager'))
    cpack_name = conf_pack.get_conformance_pack()
    am_controls = conf_pack.create_custom_controls(cpack_name)
    conf_pack.create_custom_framework(cpack_name, am_controls)


if __name__ == '__main__':
    run_demo()
# snippet-end:[python.example_code.auditmanager.Scenario_CustomFrameworkFromConformancePack]
