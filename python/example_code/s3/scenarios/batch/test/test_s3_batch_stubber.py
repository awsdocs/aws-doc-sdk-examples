# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""Stubber functions for S3 batch operations tests."""

from botocore.stub import Stubber


class S3BatchStubber:
    """Stubber for S3 Batch Operations service methods."""

    def __init__(self, s3_client, s3control_client, sts_client):
        """Initialize stubbers for all clients."""
        self.s3_stubber = Stubber(s3_client)
        self.s3control_stubber = Stubber(s3control_client)
        self.sts_stubber = Stubber(sts_client)

    def stub_get_caller_identity(self, account_id, error_code=None):
        """Stub STS get_caller_identity method."""
        expected_params = {}
        if error_code is None:
            response = {"Account": account_id}
            self.sts_stubber.add_response("get_caller_identity", response, expected_params)
        else:
            self.sts_stubber.add_client_error("get_caller_identity", error_code, expected_params=expected_params)

    def stub_create_bucket(self, bucket_name, region=None, error_code=None):
        """Stub S3 create_bucket method."""
        expected_params = {"Bucket": bucket_name}
        if region and region != "us-east-1":
            expected_params["CreateBucketConfiguration"] = {"LocationConstraint": region}
        
        if error_code is None:
            response = {}
            self.s3_stubber.add_response("create_bucket", response, expected_params)
        else:
            self.s3_stubber.add_client_error("create_bucket", error_code, expected_params=expected_params)

    def stub_put_object(self, bucket_name, key, etag="test-etag", error_code=None):
        """Stub S3 put_object method."""
        expected_params = {"Bucket": bucket_name, "Key": key, "Body": Stubber.ANY}
        
        if error_code is None:
            response = {"ETag": f'"{etag}"'}
            self.s3_stubber.add_response("put_object", response, expected_params)
        else:
            self.s3_stubber.add_client_error("put_object", error_code, expected_params=expected_params)

    def stub_head_object(self, bucket_name, key, etag="test-etag", error_code=None):
        """Stub S3 head_object method."""
        expected_params = {"Bucket": bucket_name, "Key": key}
        
        if error_code is None:
            response = {"ETag": f'"{etag}"'}
            self.s3_stubber.add_response("head_object", response, expected_params)
        else:
            self.s3_stubber.add_client_error("head_object", error_code, expected_params=expected_params)

    def stub_create_job(self, account_id, job_id, error_code=None):
        """Stub S3Control create_job method."""
        expected_params = {
            "AccountId": account_id,
            "Operation": Stubber.ANY,
            "Report": Stubber.ANY,
            "Manifest": Stubber.ANY,
            "Priority": Stubber.ANY,
            "RoleArn": Stubber.ANY,
            "Description": Stubber.ANY,
            "ConfirmationRequired": Stubber.ANY
        }
        
        if error_code is None:
            response = {"JobId": job_id}
            self.s3control_stubber.add_response("create_job", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("create_job", error_code, expected_params=expected_params)

    def stub_describe_job(self, account_id, job_id, status="Ready", failure_reasons=None, error_code=None):
        """Stub S3Control describe_job method."""
        expected_params = {"AccountId": account_id, "JobId": job_id}
        
        if error_code is None:
            job_data = {
                "JobId": job_id,
                "Status": status,
                "Priority": 10,
                "RoleArn": "arn:aws:iam::123456789012:role/S3BatchRole",
                "Description": "Batch job for tagging objects"
            }
            if failure_reasons:
                job_data["FailureReasons"] = failure_reasons
            
            response = {"Job": job_data}
            self.s3control_stubber.add_response("describe_job", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("describe_job", error_code, expected_params=expected_params)

    def stub_update_job_priority(self, account_id, job_id, priority=60, error_code=None):
        """Stub S3Control update_job_priority method."""
        expected_params = {"AccountId": account_id, "JobId": job_id, "Priority": priority}
        
        if error_code is None:
            response = {}
            self.s3control_stubber.add_response("update_job_priority", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("update_job_priority", error_code, expected_params=expected_params)

    def stub_update_job_status(self, account_id, job_id, status, error_code=None):
        """Stub S3Control update_job_status method."""
        expected_params = {"AccountId": account_id, "JobId": job_id, "RequestedJobStatus": status}
        
        if error_code is None:
            response = {}
            self.s3control_stubber.add_response("update_job_status", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("update_job_status", error_code, expected_params=expected_params)

    def stub_get_job_tagging(self, account_id, job_id, tags=None, error_code=None):
        """Stub S3Control get_job_tagging method."""
        expected_params = {"AccountId": account_id, "JobId": job_id}
        
        if error_code is None:
            response = {"Tags": tags or []}
            self.s3control_stubber.add_response("get_job_tagging", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("get_job_tagging", error_code, expected_params=expected_params)

    def stub_put_job_tagging(self, account_id, job_id, error_code=None):
        """Stub S3Control put_job_tagging method."""
        expected_params = {"AccountId": account_id, "JobId": job_id, "Tags": Stubber.ANY}
        
        if error_code is None:
            response = {}
            self.s3control_stubber.add_response("put_job_tagging", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("put_job_tagging", error_code, expected_params=expected_params)

    def stub_list_jobs(self, account_id, jobs=None, error_code=None):
        """Stub S3Control list_jobs method."""
        expected_params = {"AccountId": account_id, "JobStatuses": Stubber.ANY}
        
        if error_code is None:
            response = {"Jobs": jobs or []}
            self.s3control_stubber.add_response("list_jobs", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("list_jobs", error_code, expected_params=expected_params)

    def stub_delete_job_tagging(self, account_id, job_id, error_code=None):
        """Stub S3Control delete_job_tagging method."""
        expected_params = {"AccountId": account_id, "JobId": job_id}
        
        if error_code is None:
            response = {}
            self.s3control_stubber.add_response("delete_job_tagging", response, expected_params)
        else:
            self.s3control_stubber.add_client_error("delete_job_tagging", error_code, expected_params=expected_params)

    def stub_delete_object(self, bucket_name, key, error_code=None):
        """Stub S3 delete_object method."""
        expected_params = {"Bucket": bucket_name, "Key": key}
        
        if error_code is None:
            response = {}
            self.s3_stubber.add_response("delete_object", response, expected_params)
        else:
            self.s3_stubber.add_client_error("delete_object", error_code, expected_params=expected_params)

    def stub_list_objects_v2(self, bucket_name, prefix=None, contents=None, error_code=None):
        """Stub S3 list_objects_v2 method."""
        expected_params = {"Bucket": bucket_name}
        if prefix:
            expected_params["Prefix"] = prefix
        
        if error_code is None:
            response = {}
            if contents:
                response["Contents"] = contents
            self.s3_stubber.add_response("list_objects_v2", response, expected_params)
        else:
            self.s3_stubber.add_client_error("list_objects_v2", error_code, expected_params=expected_params)

    def stub_delete_bucket(self, bucket_name, error_code=None):
        """Stub S3 delete_bucket method."""
        expected_params = {"Bucket": bucket_name}
        
        if error_code is None:
            response = {}
            self.s3_stubber.add_response("delete_bucket", response, expected_params)
        else:
            self.s3_stubber.add_client_error("delete_bucket", error_code, expected_params=expected_params)