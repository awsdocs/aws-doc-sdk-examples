# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon Comprehend to
train a custom multi-label classifier and run a job to classify documents.
The demo trains a classifier on a set of GitHub issues with known labels.
After the classifier is trained, the demo sends a second set of GitHub issues
to the classifier so they can be labeled.
"""

from io import BytesIO
import logging
from pprint import pprint
import sys
import boto3
from botocore.exceptions import ClientError
import requests

from comprehend_demo_resources import ComprehendDemoResources
from comprehend_classifier import ComprehendClassifier, ClassifierMode, JobInputFormat

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append('../..')
from demo_tools.custom_waiter import CustomWaiter, WaitState

logger = logging.getLogger(__name__)

GITHUB_SEARCH_URL = 'https://api.github.com/search/issues'


class ClassifierTrainedWaiter(CustomWaiter):
    """Waits for a classifier to be trained."""
    def __init__(self, client):
        super().__init__(
            'ClassifierTrained', 'DescribeDocumentClassifier',
            'DocumentClassifierProperties.Status',
            {'TRAINED': WaitState.SUCCESS, 'IN_ERROR': WaitState.FAILURE},
            client, delay=60)

    def wait(self, classifier_arn):
        self._wait(DocumentClassifierArn=classifier_arn)


class JobCompleteWaiter(CustomWaiter):
    """Waits for a job to complete."""
    def __init__(self, client):
        super().__init__(
            'JobComplete', 'DescribeDocumentClassificationJob',
            'DocumentClassificationJobProperties.JobStatus',
            {'COMPLETED': WaitState.SUCCESS, 'FAILED': WaitState.FAILURE},
            client, delay=30)

    def wait(self, job_id):
        self._wait(JobId=job_id)


# snippet-start:[python.example_code.comprehend.helper.ClassifierDemo_class]
class ClassifierDemo:
    """
    Encapsulates functions used to run the demonstration.
    """
    def __init__(self, demo_resources):
        """
        :param demo_resources: A ComprehendDemoResources class that manages resources
                               for the demonstration.
        """
        self.demo_resources = demo_resources
        self.training_prefix = 'training/'
        self.input_prefix = 'input/'
        self.input_format = JobInputFormat.per_line
        self.output_prefix = 'output/'

    def setup(self):
        """Creates AWS resources used by the demo."""
        self.demo_resources.setup('comprehend-classifier-demo')

    def cleanup(self):
        """Deletes AWS resources used by the demo."""
        self.demo_resources.cleanup()

    @staticmethod
    def _sanitize_text(text):
        """Removes characters that cause errors for the document parser."""
        return text.replace('\r', ' ').replace('\n', ' ').replace(',', ';')

    @staticmethod
    def _get_issues(query, issue_count):
        """
        Gets issues from GitHub using the specified query parameters.

        :param query: The query string used to request issues from the GitHub API.
        :param issue_count: The number of issues to retrieve.
        :return: The list of issues retrieved from GitHub.
        """
        issues = []
        logger.info("Requesting issues from %s?%s.", GITHUB_SEARCH_URL, query)
        response = requests.get(
            f'{GITHUB_SEARCH_URL}?{query}&per_page={issue_count}')
        if response.status_code == 200:
            issue_page = response.json()['items']
            logger.info("Got %s issues.", len(issue_page))
            issues = [{
                'title': ClassifierDemo._sanitize_text(issue['title']),
                'body': ClassifierDemo._sanitize_text(issue['body']),
                'labels': {label['name'] for label in issue['labels']}
            } for issue in issue_page]
        else:
            logger.error(
                "GitHub returned error code %s with message %s.",
                response.status_code, response.json())
        logger.info("Found %s issues.", len(issues))
        return issues

    def get_training_issues(self, training_labels):
        """
        Gets issues used for training the custom classifier. Training issues are
        closed issues from the Boto3 repo that have known labels. Comprehend
        requires a minimum of ten training issues per label.

        :param training_labels: The issue labels to use for training.
        :return: The set of issues used for training.
        """
        issues = []
        per_label_count = 15
        for label in training_labels:
            issues += self._get_issues(
                f'q=type:issue+repo:boto/boto3+state:closed+label:{label}',
                per_label_count)
            for issue in issues:
                issue['labels'] = issue['labels'].intersection(training_labels)
        return issues

    def get_input_issues(self, training_labels):
        """
        Gets input issues from GitHub. For demonstration purposes, input issues
        are open issues from the Boto3 repo with known labels, though in practice
        any issue could be submitted to the classifier for labeling.

        :param training_labels: The set of labels to query for.
        :return: The set of issues used for input.
        """
        issues = []
        per_label_count = 5
        for label in training_labels:
            issues += self._get_issues(
                f'q=type:issue+repo:boto/boto3+state:open+label:{label}',
                per_label_count)
        return issues

    def upload_issue_data(self, issues, training=False):
        """
        Uploads issue data to an Amazon S3 bucket, either for training or for input.
        The data is first put into the format expected by Comprehend. For training,
        the set of pipe-delimited labels is prepended to each document. For
        input, labels are not sent.

        :param issues: The set of issues to upload to Amazon S3.
        :param training: Indicates whether the issue data is used for training or
                         input.
        """
        try:
            obj_key = (
                self.training_prefix if training else self.input_prefix) + 'issues.txt'
            if training:
                issue_strings = [
                    f"{'|'.join(issue['labels'])},{issue['title']} {issue['body']}"
                    for issue in issues]
            else:
                issue_strings = [
                    f"{issue['title']} {issue['body']}" for issue in issues]
            issue_bytes = BytesIO('\n'.join(issue_strings).encode('utf-8'))
            self.demo_resources.bucket.upload_fileobj(issue_bytes, obj_key)
            logger.info(
                "Uploaded data as %s to bucket %s.", obj_key,
                self.demo_resources.bucket.name)
        except ClientError:
            logger.exception(
                "Couldn't upload data to bucket %s.", self.demo_resources.bucket.name)
            raise

    def extract_job_output(self, job):
        """Extracts job output from Amazon S3."""
        return self.demo_resources.extract_job_output(job)

    @staticmethod
    def reconcile_job_output(input_issues, output_dict):
        """
        Reconciles job output with the list of input issues. Because the input issues
        have known labels, these can be compared with the labels added by the
        classifier to judge the accuracy of the output.

        :param input_issues: The list of issues used as input.
        :param output_dict: The dictionary of data that is output by the classifier.
        :return: The list of reconciled input and output data.
        """
        reconciled = []
        for archive in output_dict.values():
            for line in archive['data']:
                in_line = int(line['Line'])
                in_labels = input_issues[in_line]['labels']
                out_labels = {label['Name'] for label in line['Labels']
                              if float(label['Score']) > 0.3}
                reconciled.append(
                    f"{line['File']}, line {in_line} has labels {in_labels}.\n"
                    f"\tClassifier assigned {out_labels}.")
        logger.info("Reconciled input and output labels.")
        return reconciled
# snippet-end:[python.example_code.comprehend.helper.ClassifierDemo_class]


# snippet-start:[python.example_code.comprehend.Scenario_CustomClassifier]
def usage_demo():
    print('-'*88)
    print("Welcome to the Amazon Comprehend custom document classifier demo!")
    print('-'*88)

    logging.basicConfig(level=logging.INFO, format='%(levelname)s: %(message)s')

    comp_demo = ClassifierDemo(ComprehendDemoResources(
        boto3.resource('s3'), boto3.resource('iam')))
    comp_classifier = ComprehendClassifier(boto3.client('comprehend'))
    classifier_trained_waiter = ClassifierTrainedWaiter(
        comp_classifier.comprehend_client)
    training_labels = {'bug', 'feature-request', 'dynamodb', 's3'}

    print("Setting up storage and security resources needed for the demo.")
    comp_demo.setup()

    print("Getting training data from GitHub and uploading it to Amazon S3.")
    training_issues = comp_demo.get_training_issues(training_labels)
    comp_demo.upload_issue_data(training_issues, True)

    classifier_name = 'doc-example-classifier'
    print(f"Creating document classifier {classifier_name}.")
    comp_classifier.create(
        classifier_name, 'en',
        comp_demo.demo_resources.bucket.name,
        comp_demo.training_prefix,
        comp_demo.demo_resources.data_access_role.arn,
        ClassifierMode.multi_label)
    print(f"Waiting until {classifier_name} is trained. This typically takes "
          f"30–40 minutes.")
    classifier_trained_waiter.wait(comp_classifier.classifier_arn)

    print(f"Classifier {classifier_name} is trained:")
    pprint(comp_classifier.describe())

    print("Getting input data from GitHub and uploading it to Amazon S3.")
    input_issues = comp_demo.get_input_issues(training_labels)
    comp_demo.upload_issue_data(input_issues)

    print("Starting classification job on input data.")
    job_info = comp_classifier.start_job(
        'issue_classification_job',
        comp_demo.demo_resources.bucket.name,
        comp_demo.input_prefix,
        comp_demo.input_format,
        comp_demo.demo_resources.bucket.name,
        comp_demo.output_prefix,
        comp_demo.demo_resources.data_access_role.arn)
    print(f"Waiting for job {job_info['JobId']} to complete.")
    job_waiter = JobCompleteWaiter(comp_classifier.comprehend_client)
    job_waiter.wait(job_info['JobId'])

    job = comp_classifier.describe_job(job_info['JobId'])
    print(f"Job {job['JobId']} complete:")
    pprint(job)

    print(f"Getting job output data from Amazon S3: "
          f"{job['OutputDataConfig']['S3Uri']}.")
    job_output = comp_demo.extract_job_output(job)
    print("Job output:")
    pprint(job_output)

    print("Reconciling job output with labels from GitHub:")
    reconciled_output = comp_demo.reconcile_job_output(input_issues, job_output)
    print(*reconciled_output, sep='\n')

    answer = input(f"Do you want to delete the classifier {classifier_name} (y/n)? ")
    if answer.lower() == 'y':
        print(f"Deleting {classifier_name}.")
        comp_classifier.delete()

    print("Cleaning up resources created for the demo.")
    comp_demo.cleanup()

    print("Thanks for watching!")
    print('-'*88)
# snippet-end:[python.example_code.comprehend.Scenario_CustomClassifier]


if __name__ == '__main__':
    usage_demo()
