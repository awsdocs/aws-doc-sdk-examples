# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Creates, manages, and deletes AWS resources used by the Amazon Comprehend
demonstrations.
"""

import csv
import io
import json
import logging
import tarfile
import time
import uuid
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class ComprehendDemoResources:
    """Encapsulates resources used for demonstrations."""
    def __init__(self, s3_resource, iam_resource):
        """
        :param s3_resource: A Boto3 Amazon S3 resource.
        :param iam_resource: A Boto3 AWS Identity and Access Management (IAM) resource.
        """
        self.s3_resource = s3_resource
        self.iam_resource = iam_resource
        self.bucket = None
        self.data_access_role = None

    def setup(self, demo_name):
        """
        Creates an Amazon S3 bucket to be used for a demonstration.
        Creates an IAM role and policy that grants Amazon Comprehend permission to
        read from and write to the bucket.

        :param demo_name: The name prefix to give the IAM role and policy.
        """
        try:
            self.bucket = self.s3_resource.create_bucket(
                Bucket=f'doc-example-bucket-{uuid.uuid4()}',
                CreateBucketConfiguration={
                    'LocationConstraint':
                        self.s3_resource.meta.client.meta.region_name})
            logger.info("Created demo bucket %s.", self.bucket.name)
        except ClientError:
            logger.exception("Couldn't set up demo bucket.")
            raise

        try:
            self.data_access_role = self.iam_resource.create_role(
                RoleName=f'{demo_name}-role',
                AssumeRolePolicyDocument=json.dumps({
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Effect": "Allow",
                        "Principal": {
                            "Service": "comprehend.amazonaws.com"},
                        "Action": "sts:AssumeRole"}]}))
            role_waiter = self.iam_resource.meta.client.get_waiter('role_exists')
            role_waiter.wait(RoleName=self.data_access_role.name)
            policy = self.iam_resource.create_policy(
                PolicyName=f'{demo_name}-policy',
                PolicyDocument=json.dumps({
                    "Version": "2012-10-17",
                    "Statement": [{
                        "Action": ["s3:GetObject"],
                        "Resource": [f"arn:aws:s3:::{self.bucket.name}/*"],
                        "Effect": "Allow"
                    }, {
                        "Action": ["s3:ListBucket"],
                        "Resource": [f"arn:aws:s3:::{self.bucket.name}"],
                        "Effect": "Allow"
                    }, {
                        "Action": ["s3:PutObject"],
                        "Resource": [f"arn:aws:s3:::{self.bucket.name}/*"],
                        "Effect": "Allow"}]
                    }))
            policy_waiter = self.iam_resource.meta.client.get_waiter('policy_exists')
            policy_waiter.wait(PolicyArn=policy.arn)
            self.data_access_role.attach_policy(PolicyArn=policy.arn)
            logger.info(
                "Created data access role %s and attached policy %s.",
                self.data_access_role.name, policy.arn)
            print("Waiting for eventual consistency of role resource...")
            time.sleep(10)
        except ClientError:
            logger.exception("Couldn't create role and policy for data access.")
            raise

    def cleanup(self):
        """
        Cleans up resources use by a demonstration. All objects are deleted from
        the demonstration Amazon S3 bucket and the bucket itself is deleted. The
        IAM role and policy are also deleted.
        """
        if self.data_access_role is not None:
            try:
                for policy in self.data_access_role.attached_policies.all():
                    self.data_access_role.detach_policy(PolicyArn=policy.arn)
                    policy.delete()
                    logger.info("Detached and deleted policy %s.", policy.arn)
                self.data_access_role.delete()
                logger.info("Deleted data access role %s.", self.data_access_role.name)
                self.data_access_role = None
            except ClientError:
                logger.exception(
                    "Couldn't clean up role %s and attached policies.",
                    self.data_access_role.name)

        if self.bucket is not None:
            try:
                self.bucket.objects.delete()
                self.bucket.delete()
                logger.info("Emptied and deleted bucket %s.", self.bucket.name)
                self.bucket = None
            except ClientError:
                logger.exception(
                    "Couldn't empty or delete bucket %s.", self.bucket.name)

    def extract_job_output(self, job):
        """
        Extracts job output from the demonstration Amazon S3 bucket. Job output is
        stored as a tar archive compressed in gzip format. If the extracted output is
        in JSONL format, it is read into a list of strings. If the output is in CSV
        format, it is read into a list of dictionaries.

        :param job: Metadata about the job, including the location of job output.
        :return: Job output as a dictionary where the keys are the individual file
                 names in the tar archive.
        """
        output_key = job['OutputDataConfig']['S3Uri'].split(
            self.bucket.name + '/')[1]
        try:
            output_bytes = io.BytesIO()
            self.bucket.download_fileobj(output_key, output_bytes)
            logger.info("Downloaded job output %s.", output_key)
            output_bytes.seek(0)
            output_tar = tarfile.open(fileobj=output_bytes, mode='r:gz')
            output_dict = {
                name: {'file': output_tar.extractfile(name)} for name
                in output_tar.getnames()}
            total_lines = 0
            for name in output_dict:
                if name.split('.')[-1] == 'jsonl':
                    output_dict[name]['data'] = [
                        json.loads(line) for line in
                        output_dict[name]['file'].read().decode().strip().splitlines()]
                elif name.split('.')[-1] == 'csv':
                    text_wrapper = io.TextIOWrapper(
                        output_dict[name]['file'], encoding='utf-8')
                    reader = csv.DictReader(text_wrapper)
                    output_dict[name]['data'] = list(reader)
                total_lines += len(output_dict[name]['data'])
            logger.info(
                "Extracted %s lines of output data from tar archive.", total_lines)
        except ClientError:
            logger.exception(
                "Couldn't get output data from %s/%s", self.bucket.name,
                output_key)
        else:
            return output_dict
