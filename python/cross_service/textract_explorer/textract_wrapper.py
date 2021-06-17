# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Textract to
detect text, form, and table elements in document images.
"""

import json
import logging
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


class TextractWrapper:
    """Encapsulates Textract functions."""
    def __init__(self, textract_client, s3_resource, sqs_resource):
        """
        :param textract_client: A Boto3 Textract client.
        :param s3_resource: A Boto3 Amazon S3 resource.
        :param sqs_resource: A Boto3 Amazon SQS resource.
        """
        self.textract_client = textract_client
        self.s3_resource = s3_resource
        self.sqs_resource = sqs_resource

    def detect_file_text(self, *, document_file_name=None, document_bytes=None):
        """
        Detects text elements in a local image file or from in-memory byte data.
        The image must be in PNG or JPG format.

        :param document_file_name: The name of a document image file.
        :param document_bytes: In-memory byte data of a document image.
        :return: The response from Amazon Textract, including a list of blocks
                 that describe elements detected in the image.
        """
        if document_file_name is not None:
            with open(document_file_name, 'rb') as document_file:
                document_bytes = document_file.read()
        try:
            response = self.textract_client.detect_document_text(
                Document={'Bytes': document_bytes})
            logger.info(
                "Detected %s blocks.", len(response['Blocks']))
        except ClientError:
            logger.exception("Couldn't detect text.")
            raise
        else:
            return response

    def analyze_file(
            self, feature_types, *, document_file_name=None, document_bytes=None):
        """
        Detects text and additional elements, such as forms or tables, in a local image
        file or from in-memory byte data.
        The image must be in PNG or JPG format.

        :param feature_types: The types of additional document features to detect.
        :param document_file_name: The name of a document image file.
        :param document_bytes: In-memory byte data of a document image.
        :return: The response from Amazon Textract, including a list of blocks
                 that describe elements detected in the image.
        """
        if document_file_name is not None:
            with open(document_file_name, 'rb') as document_file:
                document_bytes = document_file.read()
        try:
            response = self.textract_client.analyze_document(
                Document={'Bytes': document_bytes}, FeatureTypes=feature_types)
            logger.info(
                "Detected %s blocks.", len(response['Blocks']))
        except ClientError:
            logger.exception("Couldn't detect text.")
            raise
        else:
            return response

    def prepare_job(self, bucket_name, document_name, document_bytes):
        """
        Prepares a document image for an asynchronous detection job by uploading
        the image bytes to an Amazon S3 bucket. Amazon Textract must have permission
        to read from the bucket to process the image.

        :param bucket_name: The name of the Amazon S3 bucket.
        :param document_name: The name of the image stored in Amazon S3.
        :param document_bytes: The image as byte data.
        """
        try:
            bucket = self.s3_resource.Bucket(bucket_name)
            bucket.upload_fileobj(document_bytes, document_name)
            logger.info("Uploaded %s to %s.", document_name, bucket_name)
        except ClientError:
            logger.exception("Couldn't upload %s to %s.", document_name, bucket_name)
            raise

    def check_job_queue(self, queue_url, job_id):
        """
        Polls an Amazon SQS queue for messages that indicate a specified Textract
        job has completed.

        :param queue_url: The URL of the Amazon SQS queue to poll.
        :param job_id: The ID of the Textract job.
        :return: The status of the job.
        """
        status = None
        try:
            queue = self.sqs_resource.Queue(queue_url)
            messages = queue.receive_messages()
            if messages:
                msg_body = json.loads(messages[0].body)
                msg = json.loads(msg_body['Message'])
                if msg.get('JobId') == job_id:
                    messages[0].delete()
                    status = msg.get('Status')
                    logger.info(
                        "Got message %s with status %s.", messages[0].message_id,
                        status)
            else:
                logger.info("No messages in queue %s.", queue_url)
        except ClientError:
            logger.exception("Couldn't get messages from queue %s.", queue_url)
        else:
            return status

    def start_detection_job(
            self, bucket_name, document_file_name, sns_topic_arn, sns_role_arn):
        """
        Starts an asynchronous job to detect text elements in an image stored in an
        Amazon S3 bucket. Textract publishes a notification to the specified Amazon SNS
        topic when the job completes.
        The image must be in PNG, JPG, or PDF format.

        :param bucket_name: The name of the Amazon S3 bucket that contains the image.
        :param document_file_name: The name of the document image stored in Amazon S3.
        :param sns_topic_arn: The Amazon Resource Name (ARN) of an Amazon SNS topic
                              where the job completion notification is published.
        :param sns_role_arn: The ARN of an AWS Identity and Access Management (IAM)
                             role that can be assumed by Textract and grants permission
                             to publish to the Amazon SNS topic.
        :return: The ID of the job.
        """
        try:
            response = self.textract_client.start_document_text_detection(
                DocumentLocation={
                    'S3Object': {'Bucket': bucket_name, 'Name': document_file_name}},
                NotificationChannel={
                    'SNSTopicArn': sns_topic_arn, 'RoleArn': sns_role_arn})
            job_id = response['JobId']
            logger.info(
                "Started text detection job %s on %s.", job_id, document_file_name)
        except ClientError:
            logger.exception("Couldn't detect text in %s.", document_file_name)
            raise
        else:
            return job_id

    def get_detection_job(self, job_id):
        """
        Gets data for a previously started text detection job.

        :param job_id: The ID of the job to retrieve.
        :return: The job data, including a list of blocks that describe elements
                 detected in the image.
        """
        try:
            response = self.textract_client.get_document_text_detection(
                JobId=job_id)
            job_status = response['JobStatus']
            logger.info("Job %s status is %s.", job_id, job_status)
        except ClientError:
            logger.exception("Couldn't get data for job %s.", job_id)
            raise
        else:
            return response

    def start_analysis_job(
            self, bucket_name, document_file_name, feature_types, sns_topic_arn,
            sns_role_arn):
        """
        Starts an asynchronous job to detect text and additional elements, such as
        forms or tables, in an image stored in an Amazon S3 bucket. Textract publishes
        a notification to the specified Amazon SNS topic when the job completes.
        The image must be in PNG, JPG, or PDF format.

        :param bucket_name: The name of the Amazon S3 bucket that contains the image.
        :param document_file_name: The name of the document image stored in Amazon S3.
        :param feature_types: The types of additional document features to detect.
        :param sns_topic_arn: The Amazon Resource Name (ARN) of an Amazon SNS topic
                              where job completion notification is published.
        :param sns_role_arn: The ARN of an AWS Identity and Access Management (IAM)
                             role that can be assumed by Textract and grants permission
                             to publish to the Amazon SNS topic.
        :return: The ID of the job.
        """
        try:
            response = self.textract_client.start_document_analysis(
                DocumentLocation={
                    'S3Object': {'Bucket': bucket_name, 'Name': document_file_name}},
                NotificationChannel={
                    'SNSTopicArn': sns_topic_arn, 'RoleArn': sns_role_arn},
                FeatureTypes=feature_types)
            job_id = response['JobId']
            logger.info(
                "Started text analysis job %s on %s.", job_id, document_file_name)
        except ClientError:
            logger.exception("Couldn't analyze text in %s.", document_file_name)
            raise
        else:
            return job_id

    def get_analysis_job(self, job_id):
        """
        Gets data for a previously started detection job that includes additional
        elements.

        :param job_id: The ID of the job to retrieve.
        :return: The job data, including a list of blocks that describe elements
                 detected in the image.
        """
        try:
            response = self.textract_client.get_document_analysis(
                JobId=job_id)
            job_status = response['JobStatus']
            logger.info("Job %s status is %s.", job_id, job_status)
        except ClientError:
            logger.exception("Couldn't get data for job %s.", job_id)
            raise
        else:
            return response

    @staticmethod
    def _add_children(block, block_dict):
        """
        A recursive function that adds children to a block, based on its list
        of relationship IDs.

        :param block: The block to populate with children.
        :param block_dict: A dictionary of all blocks for fast lookup by ID.
        """
        for kids in [rels['Ids'] for rels in block.get('Relationships', [])
                     if rels['Type'] == 'CHILD']:
            block['Children'] = []
            for kid in [block_dict[k_id] for k_id in kids]:
                block['Children'].append(kid)
                TextractWrapper._add_children(kid, block_dict)

    @staticmethod
    def make_page_hierarchy(blocks):
        """
        Makes a list of pages that each contain a hierarchy of child blocks. The
        page hierarchies are built from the flat list of blocks that is returned by
        Textract.
        This hierarchy is used by the Textract Explorer application to display the
        detected elements.

        :param blocks: The list of blocks returned by Textract.
        :return: A single parent node that contains the list of pages as its children.
        """
        block_dict = {block['Id']: block for block in blocks}
        pages = []
        for block in block_dict.values():
            if block['BlockType'] == 'PAGE':
                pages.append(block)
                TextractWrapper._add_children(block, block_dict)
        return {'Children': pages}
