# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""Unit tests for S3 batch operations module."""

import json
import pytest
from unittest.mock import Mock, patch
from botocore.exceptions import ClientError

from s3_batch import CloudFormationHelper, S3BatchScenario, setup_resources


class TestCloudFormationHelper:
    """Test cases for CloudFormationHelper class."""

    @pytest.fixture
    def cfn_helper(self):
        """Create CloudFormationHelper instance for testing."""
        return CloudFormationHelper('us-west-2')

    @patch('boto3.client')
    def test_init(self, mock_boto3_client):
        """Test CloudFormationHelper initialization."""
        CloudFormationHelper('us-east-1')
        mock_boto3_client.assert_called_with('cloudformation', region_name='us-east-1')

    @patch('boto3.client')
    def test_deploy_cloudformation_stack_success(self, mock_boto3_client, cfn_helper):
        """Test successful CloudFormation stack deployment."""
        mock_client = Mock()
        mock_boto3_client.return_value = mock_client
        cfn_helper.cfn_client = mock_client
        with patch.object(cfn_helper, '_wait_for_stack_completion'):
            cfn_helper.deploy_cloudformation_stack('test-stack')
        mock_client.create_stack.assert_called_once()
        call_args = mock_client.create_stack.call_args
        assert call_args[1]['StackName'] == 'test-stack'
        assert 'CAPABILITY_IAM' in call_args[1]['Capabilities']
        
        # Verify the template includes AmazonS3FullAccess policy
        template_body = json.loads(call_args[1]['TemplateBody'])
        assert 'ManagedPolicyArns' in template_body['Resources']['S3BatchRole']['Properties']
        assert 'arn:aws:iam::aws:policy/AmazonS3FullAccess' in template_body['Resources']['S3BatchRole']['Properties']['ManagedPolicyArns']

    @patch('boto3.client')
    def test_deploy_cloudformation_stack_failure(self, mock_boto3_client, cfn_helper):
        """Test CloudFormation stack deployment failure."""
        mock_client = Mock()
        mock_client.create_stack.side_effect = ClientError(
            {'Error': {'Code': 'ValidationError', 'Message': 'Invalid template'}},
            'CreateStack'
        )
        mock_boto3_client.return_value = mock_client
        cfn_helper.cfn_client = mock_client
        with pytest.raises(ClientError):
            cfn_helper.deploy_cloudformation_stack('test-stack')

    @patch('boto3.client')
    def test_get_stack_outputs_success(self, mock_boto3_client, cfn_helper):
        """Test successful retrieval of stack outputs."""
        mock_client = Mock()
        mock_client.describe_stacks.return_value = {
            'Stacks': [{
                'Outputs': [
                    {'OutputKey': 'S3BatchRoleArn', 'OutputValue': 'arn:aws:iam::123456789012:role/test-role'}
                ]
        }]
        }
        mock_boto3_client.return_value = mock_client
        cfn_helper.cfn_client = mock_client
        
        outputs = cfn_helper.get_stack_outputs('test-stack')
        assert outputs['S3BatchRoleArn'] == 'arn:aws:iam::123456789012:role/test-role'

    @patch('boto3.client')
    def test_destroy_cloudformation_stack_success(self, mock_boto3_client, cfn_helper):
        """Test successful CloudFormation stack deletion."""
        mock_client = Mock()
        mock_boto3_client.return_value = mock_client
        cfn_helper.cfn_client = mock_client
        
        with patch.object(cfn_helper, '_wait_for_stack_completion'):
            cfn_helper.destroy_cloudformation_stack('test-stack')
            
        mock_client.delete_stack.assert_called_once_with(StackName='test-stack')



class TestS3BatchScenario:
    """Test cases for S3BatchScenario class."""

    @pytest.fixture
    def s3_scenario(self):
        """Create S3BatchScenario instance for testing."""
        return S3BatchScenario('us-west-2')

    @patch('boto3.client')
    def test_init(self, mock_boto3_client):
        """Test S3BatchScenario initialization."""
        scenario = S3BatchScenario('us-east-1')
        assert mock_boto3_client.call_count == 3
        assert scenario.region_name == 'us-east-1'

    @patch('boto3.client')
    def test_get_account_id(self, mock_boto3_client, s3_scenario):
        """Test getting AWS account ID."""
        mock_sts_client = Mock()
        mock_sts_client.get_caller_identity.return_value = {'Account': '123456789012'}
        s3_scenario.sts_client = mock_sts_client
        
        account_id = s3_scenario.get_account_id()
        assert account_id == '123456789012'

    @patch('boto3.client')
    def test_create_bucket_us_west_2(self, mock_boto3_client, s3_scenario):
        """Test bucket creation in us-west-2."""
        mock_s3_client = Mock()
        s3_scenario.s3_client = mock_s3_client
        
        s3_scenario.create_bucket('test-bucket')
        
        mock_s3_client.create_bucket.assert_called_once_with(
            Bucket='test-bucket',
            CreateBucketConfiguration={'LocationConstraint': 'us-west-2'}
        )

    @patch('boto3.client')
    def test_create_bucket_us_east_1(self, mock_boto3_client):
        """Test bucket creation in us-east-1."""
        scenario = S3BatchScenario('us-east-1')
        mock_s3_client = Mock()
        scenario.s3_client = mock_s3_client
        
        scenario.create_bucket('test-bucket')
        
        mock_s3_client.create_bucket.assert_called_once_with(Bucket='test-bucket')

    @patch('boto3.client')
    def test_upload_files_to_bucket(self, mock_boto3_client, s3_scenario):
        """Test uploading files to S3 bucket."""
        mock_s3_client = Mock()
        mock_s3_client.put_object.return_value = {'ETag': '"test-etag"'}
        s3_scenario.s3_client = mock_s3_client
        
        file_names = ['job-manifest.csv', 'test-file.txt']
        etag = s3_scenario.upload_files_to_bucket('test-bucket', file_names)
        
        assert etag == 'test-etag'
        assert mock_s3_client.put_object.call_count == 2

    @patch('boto3.client')
    def test_create_s3_batch_job_success(self, mock_boto3_client, s3_scenario):
        """Test successful S3 batch job creation."""
        mock_s3_client = Mock()
        mock_s3_client.head_object.return_value = {'ETag': '"test-etag"'}
        mock_s3control_client = Mock()
        mock_s3control_client.create_job.return_value = {'JobId': 'test-job-id'}
        
        s3_scenario.s3_client = mock_s3_client
        s3_scenario.s3control_client = mock_s3control_client
        
        job_id = s3_scenario.create_s3_batch_job(
            '123456789012',
            'arn:aws:iam::123456789012:role/test-role',
            'arn:aws:s3:::test-bucket/job-manifest.csv',
            'arn:aws:s3:::test-bucket'
        )
        
        assert job_id == 'test-job-id'
        mock_s3control_client.create_job.assert_called_once()
        
        # Verify ConfirmationRequired is set to False
        call_args = mock_s3control_client.create_job.call_args
        assert call_args[1]['ConfirmationRequired'] is False

    @patch('boto3.client')
    def test_check_job_failure_reasons(self, mock_boto3_client, s3_scenario):
        """Test checking job failure reasons."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {
                'FailureReasons': ['Reason 1', 'Reason 2']
            }
        }
        s3_scenario.s3control_client = mock_s3control_client
        
        reasons = s3_scenario.check_job_failure_reasons('test-job-id', '123456789012')
        
        assert reasons == ['Reason 1', 'Reason 2']

    @patch('boto3.client')
    @patch('time.sleep')
    def test_wait_for_job_ready_success(self, mock_sleep, mock_boto3_client, s3_scenario):
        """Test waiting for job to become ready."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {'Status': 'Ready'}
        }
        s3_scenario.s3control_client = mock_s3control_client
        
        result = s3_scenario.wait_for_job_ready('test-job-id', '123456789012')
        
        assert result is True
        
    @patch('boto3.client')
    @patch('time.sleep')
    def test_wait_for_job_ready_suspended(self, mock_sleep, mock_boto3_client, s3_scenario):
        """Test waiting for job with Suspended status."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {'Status': 'Suspended'}
        }
        s3_scenario.s3control_client = mock_s3control_client
        
        result = s3_scenario.wait_for_job_ready('test-job-id', '123456789012')
        
        assert result is True

    @patch('boto3.client')
    def test_update_job_priority_success(self, mock_boto3_client, s3_scenario):
        """Test successful job priority update."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {'Status': 'Suspended'}
        }
        s3_scenario.s3control_client = mock_s3control_client
        
        s3_scenario.update_job_priority('test-job-id', '123456789012')
            
        mock_s3control_client.update_job_priority.assert_called_once()
        mock_s3control_client.update_job_status.assert_called_once()
        
    @patch('boto3.client')
    def test_update_job_priority_with_ready_status(self, mock_boto3_client, s3_scenario):
        """Test job priority update with Ready status."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {'Status': 'Ready'}
        }
        s3_scenario.s3control_client = mock_s3control_client
        
        s3_scenario.update_job_priority('test-job-id', '123456789012')
            
        mock_s3control_client.update_job_priority.assert_called_once()
        mock_s3control_client.update_job_status.assert_called_once()
        
    @patch('boto3.client')
    def test_update_job_priority_error_handling(self, mock_boto3_client, s3_scenario):
        """Test error handling in job priority update."""
        mock_s3control_client = Mock()
        mock_s3control_client.describe_job.return_value = {
            'Job': {'Status': 'Suspended'}
        }
        mock_s3control_client.update_job_priority.side_effect = ClientError(
            {'Error': {'Code': 'InvalidRequest', 'Message': 'Cannot update priority'}},
            'UpdateJobPriority'
        )
        mock_s3control_client.update_job_status = Mock()
        s3_scenario.s3control_client = mock_s3control_client
        
        # Should not raise exception due to error handling
        s3_scenario.update_job_priority('test-job-id', '123456789012')
        
        # Should still try to activate the job even if priority update fails
        mock_s3control_client.update_job_status.assert_called_once()

    @patch('boto3.client')
    def test_cleanup_resources(self, mock_boto3_client, s3_scenario):
        """Test resource cleanup."""
        mock_s3_client = Mock()
        mock_s3_client.list_objects_v2.return_value = {
            'Contents': [{'Key': 'batch-op-reports/report1.csv'}]
        }
        s3_scenario.s3_client = mock_s3_client
        
        file_names = ['test-file.txt']
        s3_scenario.cleanup_resources('test-bucket', file_names)
        
        assert mock_s3_client.delete_object.call_count == 2  # file + report
        mock_s3_client.delete_bucket.assert_called_once_with(Bucket='test-bucket')


class TestUtilityFunctions:
    """Test cases for utility functions."""

    @patch('s3_batch.input', return_value='c')
    def test_wait_for_input_valid(self, mock_input):
        """Test wait_for_input with valid input."""
        # pylint: disable=import-outside-toplevel
        from s3_batch import wait_for_input
        wait_for_input()  # Should not raise exception

    @patch('s3_batch.input', side_effect=['invalid', 'c'])
    def test_wait_for_input_invalid_then_valid(self, mock_input):
        """Test wait_for_input with invalid then valid input."""
        # pylint: disable=import-outside-toplevel
        from s3_batch import wait_for_input
        wait_for_input()  # Should not raise exception

    def test_setup_resources(self):
        """Test setup_resources function."""
        mock_scenario = Mock()
        
        manifest_location, report_bucket_arn = setup_resources(
            mock_scenario, 'test-bucket', ['file1.txt', 'file2.txt']
        )
        
        assert manifest_location == 'arn:aws:s3:::test-bucket/job-manifest.csv'
        assert report_bucket_arn == 'arn:aws:s3:::test-bucket'
        mock_scenario.create_bucket.assert_called_once_with('test-bucket')
        mock_scenario.upload_files_to_bucket.assert_called_once()


class TestErrorHandling:
    """Test cases for error handling scenarios."""

    @pytest.fixture
    def s3_scenario(self):
        """Create S3BatchScenario instance for testing."""
        return S3BatchScenario('us-west-2')

    @patch('boto3.client')
    def test_create_bucket_client_error(self, mock_boto3_client, s3_scenario):
        """Test bucket creation with ClientError."""
        mock_s3_client = Mock()
        mock_s3_client.create_bucket.side_effect = ClientError(
            {'Error': {'Code': 'BucketAlreadyExists', 'Message': 'Bucket exists'}},
            'CreateBucket'
        )
        s3_scenario.s3_client = mock_s3_client
        
        with pytest.raises(ClientError):
            s3_scenario.create_bucket('test-bucket')

    @patch('boto3.client')
    def test_create_s3_batch_job_client_error(self, mock_boto3_client, s3_scenario):
        """Test S3 batch job creation with ClientError."""
        mock_s3_client = Mock()
        mock_s3_client.head_object.side_effect = ClientError(
            {'Error': {'Code': 'NoSuchKey', 'Message': 'Key not found'}},
            'HeadObject'
        )
        s3_scenario.s3_client = mock_s3_client
        
        with pytest.raises(ClientError):
            s3_scenario.create_s3_batch_job(
                '123456789012',
                'arn:aws:iam::123456789012:role/test-role',
                'arn:aws:s3:::test-bucket/job-manifest.csv',
                'arn:aws:s3:::test-bucket'
            )