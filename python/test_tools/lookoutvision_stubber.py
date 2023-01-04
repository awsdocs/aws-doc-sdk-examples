# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Lookout for Vision unit tests.
"""

from test_tools.example_stubber import ExampleStubber


class LookoutVisionStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    Amazon Lookout for Vision unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        :param client: A Boto 3 Lambda client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_create_dataset(
            self, project_name, dataset_type, bucket, object_key, status, message,
            error_code=None):
        expected_params = {
            'ProjectName': project_name,
            'DatasetType': dataset_type,
            'DatasetSource': {
                'GroundTruthManifest': {
                    'S3Object': {'Bucket': bucket, 'Key': object_key}}}}
        response = {
            'DatasetMetadata': {
                'Status': status, 'StatusMessage': message, 'DatasetType': dataset_type}}
        self._stub_bifurcator(
            'create_dataset', expected_params, response, error_code=error_code)

    def stub_describe_dataset(
            self, project_name, dataset_type, status, message, image_stats=None,
            error_code=None):
        expected_params = {
            'ProjectName': project_name, 'DatasetType': dataset_type}
        response = {
            'DatasetDescription': {
                'ProjectName': project_name, 'DatasetType': dataset_type,
                'Status': status, 'StatusMessage': message}}
        if image_stats is not None:
            response['DatasetDescription']['ImageStats'] = image_stats
        self._stub_bifurcator(
            'describe_dataset', expected_params, response, error_code=error_code)

    def stub_delete_dataset(self, project_name, dataset_type, error_code=None):
        expected_params = {'ProjectName': project_name,
                           'DatasetType': dataset_type}
        response = {}
        self._stub_bifurcator(
            'delete_dataset', expected_params, response, error_code=error_code)


    def stub_list_dataset_entries(self, project_name, dataset_type, jsonline, error_code=None):
        expected_params = {'ProjectName': project_name,
                           'DatasetType': dataset_type,
                           'MaxResults': 100}
        response = {
            'DatasetEntries': [jsonline]}
        self._stub_bifurcator(
            'list_dataset_entries', expected_params, response, error_code=error_code)



    def stub_list_projects(
            self, project_names, extras=None, error_code=None):
        expected_params = {}
        if extras is None:
            response = {
                'Projects':
                    [{'ProjectName': project_name} for project_name in project_names]}
        else:
            response = {
                'Projects':
                    [{
                        'ProjectName': name,
                        'ProjectArn': extra['arn'],
                        'CreationTimestamp': extra['created']
                    } for name, extra in zip(project_names, extras)]}
        self._stub_bifurcator(
            'list_projects', expected_params, response, error_code=error_code)

    def stub_list_models(self, project_name, model_versions, error_code=None):
        expected_params = {'ProjectName': project_name}
        response = {
            'Models': [{'ModelVersion': version} for version in model_versions]}
        self._stub_bifurcator(
            'list_models', expected_params, response, error_code=error_code)

    def stub_describe_model(
            self, project_name, model_version, model_arn, status=None, extras=None,
            error_code=None):
        expected_params = {'ProjectName': project_name,
                           'ModelVersion': model_version}
        response = {
            'ModelDescription': {
                'ModelVersion': model_version, 'ModelArn': model_arn}}
        if status is not None:
            response['ModelDescription']['Status'] = status
        if extras is not None:
            desc = response['ModelDescription']
            desc['Description'] = extras['description']
            desc['StatusMessage'] = extras['message']
            desc['CreationTimestamp'] = extras['created']
            desc['EvaluationEndTimestamp'] = extras['trained']
            desc['Performance'] = {
                'Recall': extras['recall'],
                'Precision': extras['precision'],
                'F1Score': extras['f1']}
            desc['OutputConfig'] = {'S3Location': {
                'Bucket': extras['out_bucket'], 'Prefix': extras['out_folder']}}
        self._stub_bifurcator(
            'describe_model', expected_params, response, error_code=error_code)

    def stub_list_tags_for_resource(self, resource_arn, tags, error_code=None):
        expected_params = {'ResourceArn': resource_arn}
        response = {'Tags': [{'Key': key, 'Value': value}
                             for key, value in tags.items()]}
        self._stub_bifurcator(
            'list_tags_for_resource', expected_params, response, error_code=error_code)

    def stub_start_model(self, project_name, model_version, min_units, error_code=None):
        expected_params = {
            'ProjectName': project_name, 'ModelVersion': model_version,
            'MinInferenceUnits': min_units}
        response = {}
        self._stub_bifurcator(
            'start_model', expected_params, response, error_code=error_code)

    def stub_stop_model(self, project_name, model_version, status, error_code=None):
        expected_params = {'ProjectName': project_name,
                           'ModelVersion': model_version}
        response = {'Status': status}
        self._stub_bifurcator(
            'stop_model', expected_params, response, error_code=error_code)

    def stub_detect_anomalies(
            self, project_name, model_version, content_type, contents, anomalous,
            confidence, error_code=None):
        expected_params = {
            'ProjectName': project_name,
            'ContentType': content_type,
            'Body': contents,
            'ModelVersion': model_version}
        response = {
            'DetectAnomalyResult': {'IsAnomalous': anomalous, 'Confidence': confidence}}
        self._stub_bifurcator(
            'detect_anomalies', expected_params, response, error_code=error_code)

    def stub_create_model(
            self, project_name, out_bucket, out_folder, model_arn, model_version,
            error_code=None):
        expected_params = {
            'ProjectName': project_name,
            'OutputConfig': {'S3Location': {'Bucket': out_bucket, 'Prefix': out_folder}},
            'Tags': []}
        response = {
            'ModelMetadata': {'ModelArn': model_arn, 'ModelVersion': model_version}}
        self._stub_bifurcator(
            'create_model', expected_params, response, error_code=error_code)

    def stub_delete_model(self, project_name, model_version, error_code=None):
        expected_params = {'ProjectName': project_name,
                           'ModelVersion': model_version}
        response = {}
        self._stub_bifurcator(
            'delete_model', expected_params, response, error_code=error_code)

    def stub_create_project(self, project_name, project_arn, error_code=None):
        expected_params = {'ProjectName': project_name}
        response = {'ProjectMetadata': {'ProjectArn': project_arn}}
        self._stub_bifurcator(
            'create_project', expected_params, response, error_code=error_code)

    def stub_delete_project(self, project_name, project_arn, error_code=None):
        expected_params = {'ProjectName': project_name}
        response = {'ProjectArn': project_arn}
        self._stub_bifurcator(
            'delete_project', expected_params, response, error_code=error_code)

    def stub_describe_project(self, project_name, datasets, error_code=None):
        expected_params = {'ProjectName': project_name}
        response = {
            'ProjectDescription': {'Datasets': datasets}}
        self._stub_bifurcator(
            'describe_project', expected_params, response, error_code=error_code)

    def stub_update_dataset_entries(
            self, project_name, dataset_type, changes, status,
            error_code=None):
        expected_params = {
            'ProjectName': project_name,
            'DatasetType': dataset_type,
            'Changes': changes
        }
        response = {'Status': status}

        self._stub_bifurcator(
            'update_dataset_entries', expected_params, response, error_code=error_code)
