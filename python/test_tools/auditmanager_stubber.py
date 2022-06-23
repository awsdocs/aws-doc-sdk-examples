# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Audit Manager unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class AuditManagerStubber(ExampleStubber):
    """
    A class that implements stub functions used by Audit Manager unit tests.
    """
    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Audit Manager client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_get_assessment(self, assessment_id, error_code=None):
        expected_params = {'assessmentId': assessment_id}
        response = {}
        self._stub_bifurcator(
            'get_assessment', expected_params, response, error_code=error_code)

    def stub_get_evidence_folders_by_assessment(
            self, assessment_id, max_results, tokens, folders, error_code=None):
        expected_params = {'assessmentId': assessment_id, 'maxResults': max_results}
        if tokens[0] is not None:
            expected_params['nextToken'] = tokens[0]
        response = {'evidenceFolders': folders}
        if tokens[1] is not None:
            response['nextToken'] = tokens[1]
        self._stub_bifurcator(
            'get_evidence_folders_by_assessment', expected_params, response, error_code=error_code)

    def stub_disassociate_assessment_report_evidence_folder(
            self, assessment_id, folder_id, error_code=None):
        expected_params = {'assessmentId': assessment_id, 'evidenceFolderId': folder_id}
        response = {}
        self._stub_bifurcator(
            'disassociate_assessment_report_evidence_folder', expected_params, response, error_code=error_code)

    def stub_get_evidence_by_evidence_folder(
            self, assessment_id, folder_id, max_results, evidences, error_code=None):
        expected_params = {
            'assessmentId': assessment_id, 'controlSetId': folder_id,
            'evidenceFolderId': folder_id, 'maxResults': max_results}
        response = {'evidence': evidences}
        self._stub_bifurcator(
            'get_evidence_by_evidence_folder', expected_params, response, error_code=error_code)

    def stub_batch_disassociate_assessment_report_evidence(
            self, assessment_id, folder_id, evidence_ids, error_code=None):
        expected_params = {
            'assessmentId': assessment_id, 'evidenceFolderId': folder_id,
            'evidenceIds': evidence_ids}
        response = {}
        self._stub_bifurcator(
            'batch_disassociate_assessment_report_evidence', expected_params, response, error_code=error_code)

    def stub_associate_assessment_report_evidence_folder(
            self, assessment_id, folder_id, error_code=None):
        expected_params = {'assessmentId': assessment_id, 'evidenceFolderId': folder_id}
        response = {}
        self._stub_bifurcator(
            'associate_assessment_report_evidence_folder', expected_params, response, error_code=error_code)

    def stub_create_assessment_report(
            self, name, desc, assessment_id, report_id, error_code=None):
        expected_params = {
            'name': name, 'description': desc, 'assessmentId': assessment_id}
        response = {'assessmentReport': {'id': report_id}}
        self._stub_bifurcator(
            'create_assessment_report', expected_params, response, error_code=error_code)

    def stub_list_assessment_reports(self, report_ids, error_code=None):
        expected_params = {'maxResults': 1}
        response = {
            'assessmentReports': [
                {'id': report_id, 'status': 'COMPLETE'} for report_id in report_ids]}
        self._stub_bifurcator(
            'list_assessment_reports', expected_params, response, error_code=error_code)

    def stub_get_assessment_report_url(
            self, report_id, assessment_id, report_url, error_code=None):
        expected_params = {
            'assessmentReportId': report_id, 'assessmentId': assessment_id}
        response = {
            'preSignedUrl': {'link': report_url, 'hyperlinkName': 'stubbed_link'}
        }
        self._stub_bifurcator(
            'get_assessment_report_url', expected_params, response, error_code=error_code)

    def stub_create_control(self, name, source_id, control_id, error_code=None):
        expected_params = {
            'name': name,
            'controlMappingSources': [{
                'sourceName': 'ConfigRule',
                'sourceSetUpOption': 'System_Controls_Mapping',
                'sourceType': 'AWS_Config',
                'sourceKeyword': {
                    'keywordInputType': 'SELECT_FROM_LIST',
                    'keywordValue': source_id}}]}
        response = {'control': {'id': control_id}}
        self._stub_bifurcator(
            'create_control', expected_params, response, error_code=error_code)

    def stub_create_assessment_framework(self, name, control_sets, fw_id, error_code=None):
        expected_params = {'name': name, 'controlSets': control_sets}
        response = {'framework': {'name': name, 'id': fw_id}}
        self._stub_bifurcator(
            'create_assessment_framework', expected_params, response, error_code=error_code)

    def stub_list_controls(self, ctl_type, max_results, tokens, control_ids, error_code=None):
        expected_params = {'controlType': ctl_type, 'maxResults': max_results}
        if tokens[0] is not None:
            expected_params['nextToken'] = tokens[0]
        response = {'controlMetadataList': [{'id': ctl_id} for ctl_id in control_ids]}
        if tokens[1] is not None:
            response['nextToken'] = tokens[1]
        self._stub_bifurcator(
            'list_controls', expected_params, response, error_code=error_code)

    def stub_get_control(self, control_id, sources, error_code=None):
        expected_params = {'controlId': control_id}
        response = {'control': {'id': control_id, 'controlSources': sources}}
        self._stub_bifurcator(
            'get_control', expected_params, response, error_code=error_code)
