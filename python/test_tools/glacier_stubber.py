# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Simple Storage Service Glacier unit tests.
"""

import io

from test_tools.example_stubber import ExampleStubber


class GlacierStubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon S3 Glacier unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors if the parameters are not as expected.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Glacier client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_vault(self, vault_name, vault_uri, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name}
        response = {'location': vault_uri}
        self._stub_bifurcator(
            'create_vault', expected_params, response, error_code=error_code)

    def stub_list_vaults(self, vault_names, error_code=None):
        expected_params = {'accountId': '-'}
        response = {
            'VaultList': [{
                'VaultARN': f'arn:aws:glacier:REGION:123456789012:vaults/{name}',
                'VaultName': name,
                'NumberOfArchives': index
            } for index, name in enumerate(vault_names)]}
        self._stub_bifurcator(
            'list_vaults', expected_params, response, error_code=error_code)

    def stub_upload_archive(
            self, vault_name, arch_desc, arch_file, arch_id, error_code=None):
        expected_params = {
            'accountId': '-', 'vaultName': vault_name, 'archiveDescription': arch_desc,
            'body': arch_file}
        response = {
            'location': f'12345678902/vaults/{vault_name}/archives/{arch_id}',
            'archiveId': arch_id
        }
        self._stub_bifurcator(
            'upload_archive', expected_params, response, error_code=error_code)

    def stub_initiate_job(
            self, vault_name, job_type, job_id, archive_id=None, error_code=None):
        expected_params = {
            'accountId': '-', 'vaultName': vault_name,
            'jobParameters': {'Type': job_type}}
        if archive_id is not None:
            expected_params['jobParameters']['ArchiveId'] = archive_id
        response = {'jobId': job_id}
        self._stub_bifurcator(
            'initiate_job', expected_params, response, error_code=error_code)

    def stub_describe_job(
            self, vault_name, job_id, job_action, job_status_code=None, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name, 'jobId': job_id}
        response = {'JobId': job_id, 'Action': job_action}
        if job_status_code is not None:
            response['StatusCode'] = job_status_code
        self._stub_bifurcator(
            'describe_job', expected_params, response, error_code=error_code)

    def stub_list_jobs(
            self, vault_name, status_code, completed, job_ids, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name}
        if status_code is not None:
            expected_params['statuscode'] = status_code
        if completed is not None:
            expected_params['completed'] = 'true' if completed else 'false'
        response = {
            'JobList': [{'JobId': job_id} for job_id in job_ids]}
        self._stub_bifurcator(
            'list_jobs', expected_params, response, error_code=error_code)

    def stub_delete_vault(self, vault_name, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name}
        response = {}
        self._stub_bifurcator(
            'delete_vault', expected_params, response, error_code=error_code)

    def stub_delete_archive(self, vault_name, archive_id, error_code=None):
        expected_params = {
            'accountId': '-', 'vaultName': vault_name, 'archiveId': archive_id}
        response = {}
        self._stub_bifurcator(
            'delete_archive', expected_params, response, error_code=error_code)

    def stub_get_job_output(
            self, vault_name, job_id, out_bytes, archive_desc=None, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name, 'jobId': job_id}
        response = {'body': io.BytesIO(out_bytes)}
        if archive_desc is not None:
            response['archiveDescription'] = archive_desc
        self._stub_bifurcator(
            'get_job_output', expected_params, response, error_code=error_code)

    def stub_set_vault_notifications(
            self, vault_name, topic_arn, events, error_code=None):
        expected_params = {
            'accountId': '-', 'vaultName': vault_name,
            'vaultNotificationConfig': {'SNSTopic': topic_arn, 'Events': events}}
        response = {}
        self._stub_bifurcator(
            'set_vault_notifications', expected_params, response, error_code=error_code)

    def stub_get_vault_notifications(
            self, vault_name, topic_arn, events, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name}
        response = {
            'vaultNotificationConfig': {'SNSTopic': topic_arn, 'Events': events}}
        self._stub_bifurcator(
            'get_vault_notifications', expected_params, response, error_code=error_code)

    def stub_delete_vault_notifications(self, vault_name, error_code=None):
        expected_params = {'accountId': '-', 'vaultName': vault_name}
        response = {}
        self._stub_bifurcator(
            'delete_vault_notifications', expected_params, response, error_code=error_code)
