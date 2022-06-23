# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Comprehend to
run a topic modeling job. Topic modeling analyzes a set of documents and determines
common themes.
"""

from enum import Enum
import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class JobInputFormat(Enum):
    per_file = 'ONE_DOC_PER_FILE'
    per_line = 'ONE_DOC_PER_LINE'


# snippet-start:[python.example_code.comprehend.ComprehendTopicModeler]
class ComprehendTopicModeler:
    """Encapsulates a Comprehend topic modeler."""
    def __init__(self, comprehend_client):
        """
        :param comprehend_client: A Boto3 Comprehend client.
        """
        self.comprehend_client = comprehend_client
# snippet-end:[python.example_code.comprehend.ComprehendTopicModeler]

# snippet-start:[python.example_code.comprehend.StartTopicsDetectionJob]
    def start_job(
            self, job_name, input_bucket, input_key, input_format, output_bucket,
            output_key, data_access_role_arn):
        """
        Starts a topic modeling job. Input is read from the specified Amazon S3
        input bucket and written to the specified output bucket. Output data is stored
        in a tar archive compressed in gzip format. The job runs asynchronously, so you
        can call `describe_topics_detection_job` to get job status until it
        returns a status of SUCCEEDED.

        :param job_name: The name of the job.
        :param input_bucket: An Amazon S3 bucket that contains job input.
        :param input_key: The prefix used to find input data in the input
                             bucket. If multiple objects have the same prefix, all
                             of them are used.
        :param input_format: The format of the input data, either one document per
                             file or one document per line.
        :param output_bucket: The Amazon S3 bucket where output data is written.
        :param output_key: The prefix prepended to the output data.
        :param data_access_role_arn: The Amazon Resource Name (ARN) of a role that
                                     grants Comprehend permission to read from the
                                     input bucket and write to the output bucket.
        :return: Information about the job, including the job ID.
        """
        try:
            response = self.comprehend_client.start_topics_detection_job(
                JobName=job_name,
                DataAccessRoleArn=data_access_role_arn,
                InputDataConfig={
                    'S3Uri': f's3://{input_bucket}/{input_key}',
                    'InputFormat': input_format.value},
                OutputDataConfig={'S3Uri': f's3://{output_bucket}/{output_key}'})
            logger.info("Started topic modeling job %s.", response['JobId'])
        except ClientError:
            logger.exception("Couldn't start topic modeling job.")
            raise
        else:
            return response
# snippet-end:[python.example_code.comprehend.StartTopicsDetectionJob]

# snippet-start:[python.example_code.comprehend.DescribeTopicsDetectionJob]
    def describe_job(self, job_id):
        """
        Gets metadata about a topic modeling job.

        :param job_id: The ID of the job to look up.
        :return: Metadata about the job.
        """
        try:
            response = self.comprehend_client.describe_topics_detection_job(
                JobId=job_id)
            job = response['TopicsDetectionJobProperties']
            logger.info("Got topic detection job %s.", job_id)
        except ClientError:
            logger.exception("Couldn't get topic detection job %s.", job_id)
            raise
        else:
            return job
# snippet-end:[python.example_code.comprehend.DescribeTopicsDetectionJob]

# snippet-start:[python.example_code.comprehend.ListTopicsDetectionJobs]
    def list_jobs(self):
        """
        Lists topic modeling jobs for the current account.

        :return: The list of jobs.
        """
        try:
            response = self.comprehend_client.list_topics_detection_jobs()
            jobs = response['TopicsDetectionJobPropertiesList']
            logger.info("Got %s topic detection jobs.", len(jobs))
        except ClientError:
            logger.exception("Couldn't get topic detection jobs.")
            raise
        else:
            return jobs
# snippet-end:[python.example_code.comprehend.ListTopicsDetectionJobs]
