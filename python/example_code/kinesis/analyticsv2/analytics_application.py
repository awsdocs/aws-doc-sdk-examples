# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with version 2 of the Amazon Kinesis
Data Analytics API to create and manage applications.
"""

import json
import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.kinesis-analytics-v2.ApplicationClass]
class KinesisAnalyticsApplicationV2:
    """Encapsulates Kinesis Data Analytics application functions."""
    def __init__(self, analytics_client):
        """
        :param analytics_client: A Boto3 Kinesis Data Analytics v2 client.
        """
        self.analytics_client = analytics_client
        self.name = None
        self.arn = None
        self.version_id = None
        self.create_timestamp = None
# snippet-end:[python.example_code.kinesis-analytics-v2.ApplicationClass]

# snippet-start:[python.example_code.kinesis-analytics-v2.helper_update_details]
    def _update_details(self, details):
        """
        Updates object properties with application details retrieved from the service.

        :param details: Application details from the service.
        """
        self.name = details['ApplicationName']
        self.version_id = details['ApplicationVersionId']
        self.arn = details['ApplicationARN']
        self.create_timestamp = details['CreateTimestamp']
# snippet-end:[python.example_code.kinesis-analytics-v2.helper_update_details]

    @staticmethod
    def create_read_write_role(
            prefix, input_stream_arn, output_stream_arn, iam_resource):
        """
        Creates an AWS Identity and Access Management (IAM) role with an attached
        policy that lets Kinesis Data Analytics read from an input stream and
        write to an output stream.

        :param prefix: The prefix prepended to the created policy and role names.
        :param input_stream_arn: The Amazon Resource Name (ARN) of the input stream.
                                 The policy grants permission to read from this stream.
        :param output_stream_arn: The ARN of the output stream. The policy grants
                                  permission to write to this stream.
        :param iam_resource: A Boto3 IAM resource.
        :return: The newly created role.
        """
        policy_doc = {
            "Version": "2012-10-17",
            "Statement": [
                {
                    "Sid": "ReadInputKinesis",
                    "Effect": "Allow",
                    "Action": [
                        "kinesis:DescribeStream",
                        "kinesis:GetShardIterator",
                        "kinesis:GetRecords"],
                    "Resource": [input_stream_arn]
                }, {
                    "Sid": "WriteOutputKinesis",
                    "Effect": "Allow",
                    "Action": [
                        "kinesis:DescribeStream",
                        "kinesis:PutRecord",
                        "kinesis:PutRecords"
                    ],
                    "Resource": [output_stream_arn]
                }]}
        trust_policy = {
            "Version": "2012-10-17",
            "Statement": [{
                "Effect": "Allow",
                "Principal": {
                    "Service": "kinesisanalytics.amazonaws.com"},
                "Action": "sts:AssumeRole"}]}

        try:
            policy = iam_resource.create_policy(
                PolicyName=f'{prefix}-policy',
                PolicyDocument=json.dumps(policy_doc))
            role = iam_resource.create_role(
                RoleName=f'{prefix}-role',
                AssumeRolePolicyDocument=json.dumps(trust_policy))
            role.attach_policy(PolicyArn=policy.arn)
            logger.info(
                "Created role %s and attached policy %s to allow read from stream %s "
                "and write to stream %s.",
                role.name, policy.policy_name, input_stream_arn, output_stream_arn)
        except ClientError:
            logger.exception(
                "Couldn't create role or policy to read from stream %s and write to "
                "stream %s.", input_stream_arn, output_stream_arn)
            raise
        else:
            return role

# snippet-start:[python.example_code.kinesis-analytics-v2.CreateApplication]
    def create(self, app_name, role_arn, env='SQL-1_0'):
        """
        Creates a Kinesis Data Analytics application.

        :param app_name: The name of the application.
        :param role_arn: The ARN of a role that can be assumed by Kinesis Data
                         Analytics and grants needed permissions.
        :param env: The runtime environment of the application, such as SQL. Code
                    uploaded to the application runs in this environment.
        :return: Metadata about the newly created application.
        """
        try:
            response = self.analytics_client.create_application(
                ApplicationName=app_name, RuntimeEnvironment=env,
                ServiceExecutionRole=role_arn)
            details = response['ApplicationDetail']
            self._update_details(details)
            logger.info("Application %s created.", app_name)
        except ClientError:
            logger.exception("Couldn't create application %s.", app_name)
            raise
        else:
            return details
# snippet-end:[python.example_code.kinesis-analytics-v2.CreateApplication]

# snippet-start:[python.example_code.kinesis-analytics-v2.DeleteApplication]
    def delete(self):
        """
        Deletes an application.
        """
        try:
            self.analytics_client.delete_application(
                ApplicationName=self.name, CreateTimestamp=self.create_timestamp)
            logger.info("Deleted application %s.", self.name)
        except ClientError:
            logger.exception("Couldn't delete application %s.", self.name)
            raise
# snippet-end:[python.example_code.kinesis-analytics-v2.DeleteApplication]

# snippet-start:[python.example_code.kinesis-analytics-v2.DescribeApplication]
    def describe(self, name):
        """
        Gets metadata about an application.

        :param name: The name of the application to look up.
        :return: Metadata about the application.
        """
        try:
            response = self.analytics_client.describe_application(
                ApplicationName=name)
            details = response['ApplicationDetail']
            self._update_details(details)
            logger.info("Got metadata for application %s.", name)
        except ClientError:
            logger.exception("Couldn't get metadata for application %s.", name)
            raise
        else:
            return details
# snippet-end:[python.example_code.kinesis-analytics-v2.DescribeApplication]

# snippet-start:[python.example_code.kinesis-analytics-v2.DescribeApplicationSnapshot]
    def describe_snapshot(self, application_name, snapshot_name):
        """
        Gets metadata about a previously saved application snapshot.

        :param application_name: The name of the application.
        :param snapshot_name: The name of the snapshot.
        :return: Metadata about the snapshot.
        """
        try:
            response = self.analytics_client.describe_application_snapshot(
                ApplicationName=application_name, SnapshotName=snapshot_name)
            snapshot = response['SnapshotDetails']
            logger.info(
                "Got metadata for snapshot %s of application %s.", snapshot_name,
                application_name)
        except ClientError:
            logger.exception(
                "Couldn't get metadata for snapshot %s of application %s.",
                snapshot_name, application_name)
            raise
        else:
            return snapshot
# snippet-end:[python.example_code.kinesis-analytics-v2.DescribeApplicationSnapshot]

# snippet-start:[python.example_code.kinesis-analytics-v2.DiscoverInputSchema]
    def discover_input_schema(self, stream_arn, role_arn):
        """
        Discovers a schema that maps data in a stream to a format that is usable by
        an application's runtime environment. The stream must be active and have
        enough data moving through it for the service to sample. The returned schema
        can be used when you add the stream as an input to the application or you can
        write your own schema.

        :param stream_arn: The ARN of the stream to map.
        :param role_arn: A role that lets Kinesis Data Analytics read from the stream.
        :return: The discovered schema of the data in the input stream.
        """
        try:
            response = self.analytics_client.discover_input_schema(
                ResourceARN=stream_arn,
                ServiceExecutionRole=role_arn,
                InputStartingPositionConfiguration={'InputStartingPosition': 'NOW'})
            schema = response['InputSchema']
            logger.info("Discovered input schema for stream %s.", stream_arn)
        except ClientError:
            logger.exception(
                "Couldn't discover input schema for stream %s.", stream_arn)
            raise
        else:
            return schema
# snippet-end:[python.example_code.kinesis-analytics-v2.DiscoverInputSchema]

# snippet-start:[python.example_code.kinesis-analytics-v2.AddApplicationInput]
    def add_input(self, input_prefix, stream_arn, input_schema):
        """
        Adds an input stream to the application. The input stream data is mapped
        to an in-application stream that can be processed by your code running in
        Kinesis Data Analytics.

        :param input_prefix: The prefix prepended to in-application input stream names.
        :param stream_arn: The ARN of the input stream.
        :param input_schema: A schema that maps the data in the input stream to the
                             runtime environment. This can be automatically generated
                             by using `discover_input_schema` or you can create it
                             yourself.
        :return: Metadata about the newly added input.
        """
        try:
            response = self.analytics_client.add_application_input(
                ApplicationName=self.name,
                CurrentApplicationVersionId=self.version_id,
                Input={
                    'NamePrefix': input_prefix,
                    'KinesisStreamsInput': {'ResourceARN': stream_arn},
                    'InputSchema': input_schema})
            self.version_id = response['ApplicationVersionId']
            logger.info(
                "Add input stream %s to application %s.", stream_arn, self.name)
        except ClientError:
            logger.exception(
                "Couldn't add input stream %s to application %s.", stream_arn,
                self.name)
            raise
        else:
            return response
# snippet-end:[python.example_code.kinesis-analytics-v2.AddApplicationInput]

# snippet-start:[python.example_code.kinesis-analytics-v2.AddApplicationOutput]
    def add_output(self, in_app_stream_name, output_arn):
        """
        Adds an output stream to the application. Kinesis Data Analytics maps data
        from the specified in-application stream to the output stream.

        :param in_app_stream_name: The name of the in-application stream to map
                                   to the output stream.
        :param output_arn: The ARN of the output stream.
        :return: A list of metadata about the output resources currently assigned
                 to the application.
        """
        try:
            response = self.analytics_client.add_application_output(
                ApplicationName=self.name,
                CurrentApplicationVersionId=self.version_id,
                Output={
                    'Name': in_app_stream_name,
                    'KinesisStreamsOutput': {'ResourceARN': output_arn},
                    'DestinationSchema': {'RecordFormatType': 'JSON'}})
            outputs = response['OutputDescriptions']
            self.version_id = response['ApplicationVersionId']
            logging.info(
                "Added output %s to %s, which now has %s outputs.", output_arn,
                self.name, len(outputs))
        except ClientError:
            logger.exception("Couldn't add output %s to %s.", output_arn, self.name)
            raise
        else:
            return outputs
# snippet-end:[python.example_code.kinesis-analytics-v2.AddApplicationOutput]

# snippet-start:[python.example_code.kinesis-analytics-v2.UpdateApplication]
    def update_code(self, code):
        """
        Updates the code that runs in the application. The code must run in the
        runtime environment of the application, such as SQL. Application code
        typically reads data from in-application streams and transforms it in some way.

        :param code: The code to upload. This completely replaces any existing code
                     in the application.
        :return: Metadata about the application.
        """
        try:
            response = self.analytics_client.update_application(
                ApplicationName=self.name,
                CurrentApplicationVersionId=self.version_id,
                ApplicationConfigurationUpdate={
                    'ApplicationCodeConfigurationUpdate': {
                        'CodeContentTypeUpdate': 'PLAINTEXT',
                        'CodeContentUpdate': {
                            'TextContentUpdate': code}}})
            details = response['ApplicationDetail']
            self.version_id = details['ApplicationVersionId']
            logger.info("Update code for application %s.", self.name)
        except ClientError:
            logger.exception("Couldn't update code for application %s.", self.name)
            raise
        else:
            return details
# snippet-end:[python.example_code.kinesis-analytics-v2.UpdateApplication]

# snippet-start:[python.example_code.kinesis-analytics-v2.StartApplication]
    def start(self, input_id):
        """
        Starts an application. After the application is running, it reads from the
        specified input stream and runs the application code on the incoming data.

        :param input_id: The ID of the input to read.
        """
        try:
            self.analytics_client.start_application(
                ApplicationName=self.name,
                RunConfiguration={
                    'SqlRunConfigurations': [{
                        'InputId': input_id,
                        'InputStartingPositionConfiguration': {
                            'InputStartingPosition': 'NOW'}}]})
            logger.info("Started application %s.", self.name)
        except ClientError:
            logger.exception("Couldn't start application %s.", self.name)
            raise
# snippet-end:[python.example_code.kinesis-analytics-v2.StartApplication]

# snippet-start:[python.example_code.kinesis-analytics-v2.StopApplication]
    def stop(self):
        """
        Stops an application. This stops the application from processing data but
        does not delete any resources.
        """
        try:
            self.analytics_client.stop_application(ApplicationName=self.name)
            logger.info("Stopping application %s.", self.name)
        except ClientError:
            logger.exception("Couldn't stop application %s.", self.name)
            raise
# snippet-end:[python.example_code.kinesis-analytics-v2.StopApplication]
