# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Overview

Shows how to use the AWS SDK for Python (Boto3) with AWS Glue to:

1. Create and run a crawler that crawls a public Amazon Simple Storage
   Service (Amazon S3) bucket and generates a metadata database that describes the
   CSV-formatted data it finds.
2. List information about databases and tables in your AWS Glue Data Catalog.
3. Create and run a job that extracts CSV data from the source S3 bucket,
   transforms it by removing and renaming fields, and loads JSON-formatted output into
   another Amazon S3 bucket.
4. List information about job runs and view some of the transformed data.
5. Delete all resources created by the example.
"""

import argparse
import io
import json
import logging
from pprint import pprint
import sys
import time
import uuid
import boto3
from boto3.exceptions import S3UploadFailedError
from botocore.exceptions import ClientError

from glue_wrapper import GlueWrapper
from question import Question

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.glue.Scenario_GetStartedCrawlersJobs]
class GlueCrawlerJobScenario:
    """
    Encapsulates a scenario that shows how to create an AWS Glue crawler and job and use
    them to transform data from CSV to JSON format.
    """
    def __init__(self, glue_client, glue_service_role, glue_bucket):
        """
        :param glue_client: A Boto3 AWS Glue client.
        :param glue_service_role: An AWS Identity and Access Management (IAM) role
                                  that AWS Glue can assume to gain access to the
                                  resources it requires.
        :param glue_bucket: An S3 bucket that can hold a job script and output data
                            from AWS Glue job runs.
        """
        self.glue_client = glue_client
        self.glue_service_role = glue_service_role
        self.glue_bucket = glue_bucket

    @staticmethod
    def wait(seconds, tick=12):
        """
        Waits for a specified number of seconds, while also displaying an animated
        spinner.

        :param seconds: The number of seconds to wait.
        :param tick: The number of frames per second used to animate the spinner.
        """
        progress = '|/-\\'
        waited = 0
        while waited < seconds:
            for frame in range(tick):
                sys.stdout.write(f"\r{progress[frame % len(progress)]}")
                sys.stdout.flush()
                time.sleep(1/tick)
            waited += 1

    def upload_job_script(self, job_script):
        """
        Uploads a Python ETL script to an S3 bucket. The script is used by the AWS Glue
        job to transform data.

        :param job_script: The relative path to the job script.
        """
        try:
            self.glue_bucket.upload_file(Filename=job_script, Key=job_script)
            print(f"Uploaded job script '{job_script}' to the example bucket.")
        except S3UploadFailedError as err:
            logger.error("Couldn't upload job script. Here's why: %s", err)
            raise

    def run(
            self, crawler_name, db_name, db_prefix, data_source, job_script, job_name):
        """
        Runs the scenario. This is an interactive experience that runs at a command
        prompt and asks you for input throughout.

        :param crawler_name: The name of the crawler used in the scenario. If the
                             crawler does not exist, it is created.
        :param db_name: The name to give the metadata database created by the crawler.
        :param db_prefix: The prefix to give tables added to the database by the
                          crawler.
        :param data_source: The location of the data source that is targeted by the
                            crawler and extracted during job runs.
        :param job_script: The job script that is used to transform data during job
                           runs.
        :param job_name: The name to give the job definition that is created during the
                         scenario.
        """
        wrapper = GlueWrapper(self.glue_client)
        print(f"Checking for crawler {crawler_name}.")
        crawler = wrapper.get_crawler(crawler_name)
        if crawler is None:
            print(f"Creating crawler {crawler_name}.")
            wrapper.create_crawler(
                crawler_name, self.glue_service_role.arn, db_name, db_prefix, data_source)
            print(f"Created crawler {crawler_name}.")
            crawler = wrapper.get_crawler(crawler_name)
        pprint(crawler)
        print('-'*88)

        print(f"When you run the crawler, it crawls data stored in {data_source} and "
              f"creates a metadata database in the AWS Glue Data Catalog that describes "
              f"the data in the data source.")
        print("In this example, the source data is in CSV format.")
        ready = False
        while not ready:
            ready = Question.ask_question(
                "Ready to start the crawler? (y/n) ", Question.is_yesno)
        wrapper.start_crawler(crawler_name)
        print("Let's wait for the crawler to run. This typically takes a few minutes.")
        crawler_state = None
        while crawler_state != 'READY':
            self.wait(10)
            crawler = wrapper.get_crawler(crawler_name)
            crawler_state = crawler['State']
            print(f"Crawler is {crawler['State']}.")
        print('-'*88)

        database = wrapper.get_database(db_name)
        print(f"The crawler created database {db_name}:")
        pprint(database)
        print(f"The database contains these tables:")
        tables = wrapper.get_tables(db_name)
        for index, table in enumerate(tables):
            print(f"\t{index + 1}. {table['Name']}")
        table_index = Question.ask_question(
            f"Enter the number of a table to see more detail: ",
            Question.is_int, Question.in_range(1, len(tables)))
        pprint(tables[table_index - 1])
        print('-'*88)

        print(f"Creating job definition {job_name}.")
        wrapper.create_job(
            job_name, "Getting started example job.", self.glue_service_role.arn,
            f's3://{self.glue_bucket.name}/{job_script}')
        print("Created job definition.")
        print(f"When you run the job, it extracts data from {data_source}, transforms it "
              f"by using the {job_script} script, and loads the output into "
              f"S3 bucket {self.glue_bucket.name}.")
        print("In this example, the data is transformed from CSV to JSON, and only a few "
              "fields are included in the output.")
        job_run_status = None
        if Question.ask_question(f"Ready to run? (y/n) ", Question.is_yesno):
            job_run_id = wrapper.start_job_run(
                job_name, db_name, tables[0]['Name'], self.glue_bucket.name)
            print(f"Job {job_name} started. Let's wait for it to run.")
            while job_run_status not in ['SUCCEEDED', 'STOPPED', 'FAILED', 'TIMEOUT']:
                self.wait(10)
                job_run = wrapper.get_job_run(job_name, job_run_id)
                job_run_status = job_run['JobRunState']
                print(f"Job {job_name}/{job_run_id} is {job_run_status}.")
        print('-'*88)

        if job_run_status == 'SUCCEEDED':
            print(f"Data from your job run is stored in your S3 bucket '{self.glue_bucket.name}':")
            try:
                keys = [obj.key for obj in self.glue_bucket.objects.filter(Prefix='run-')]
                for index, key in enumerate(keys):
                    print(f"\t{index + 1}: {key}")
                lines = 4
                key_index = Question.ask_question(
                    f"Enter the number of a block to download it and see the first {lines} "
                    f"lines of JSON output in the block: ",
                    Question.is_int, Question.in_range(1, len(keys)))
                job_data = io.BytesIO()
                self.glue_bucket.download_fileobj(keys[key_index - 1], job_data)
                job_data.seek(0)
                for _ in range(lines):
                    print(job_data.readline().decode('utf-8'))
            except ClientError as err:
                logger.error(
                    "Couldn't get job run data. Here's why: %s: %s",
                    err.response['Error']['Code'], err.response['Error']['Message'])
                raise
            print('-'*88)

        job_names = wrapper.list_jobs()
        if job_names:
            print(f"Your account has {len(job_names)} jobs defined:")
            for index, job_name in enumerate(job_names):
                print(f"\t{index + 1}. {job_name}")
            job_index = Question.ask_question(
                f"Enter a number between 1 and {len(job_names)} to see the list of runs for "
                f"a job: ", Question.is_int, Question.in_range(1, len(job_names)))
            job_runs = wrapper.get_job_runs(job_names[job_index - 1])
            if job_runs:
                print(f"Found {len(job_runs)} runs for job {job_names[job_index - 1]}:")
                for index, job_run in enumerate(job_runs):
                    print(
                        f"\t{index + 1}. {job_run['JobRunState']} on "
                        f"{job_run['CompletedOn']:%Y-%m-%d %H:%M:%S}")
                run_index = Question.ask_question(
                    f"Enter a number between 1 and {len(job_runs)} to see details for a run: ",
                    Question.is_int, Question.in_range(1, len(job_runs)))
                pprint(job_runs[run_index - 1])
            else:
                print(f"No runs found for job {job_names[job_index - 1]}")
        else:
            print("Your account doesn't have any jobs defined.")
        print('-'*88)

        print(f"Let's clean up. During this example we created job definition '{job_name}'.")
        if Question.ask_question(
                "Do you want to delete the definition and all runs? (y/n) ", Question.is_yesno):
            wrapper.delete_job(job_name)
            print(f"Job definition '{job_name}' deleted.")
        tables = wrapper.get_tables(db_name)
        print(f"We also created database '{db_name}' that contains these tables:")
        for table in tables:
            print(f"\t{table['Name']}")
        if Question.ask_question(
                "Do you want to delete the tables and the database? (y/n) ", Question.is_yesno):
            for table in tables:
                wrapper.delete_table(db_name, table['Name'])
                print(f"Deleted table {table['Name']}.")
            wrapper.delete_database(db_name)
            print(f"Deleted database {db_name}.")
        print(f"We also created crawler '{crawler_name}'.")
        if Question.ask_question(
                "Do you want to delete the crawler? (y/n) ", Question.is_yesno):
            wrapper.delete_crawler(crawler_name)
            print(f"Deleted crawler {crawler_name}.")
        print('-'*88)


def parse_args(args):
    """
    Parse command line arguments.

    :param args: The command line arguments.
    :return: The parsed arguments.
    """
    parser = argparse.ArgumentParser(
        description="Runs the AWS Glue getting started with crawlers and jobs scenario. "
                    "Before you run this scenario, set up scaffold resources by running "
                    "'python scaffold.py deploy'.")
    parser.add_argument(
        'role_name',
        help="The name of an IAM role that AWS Glue can assume. This role must grant access "
             "to Amazon S3 and to the permissions granted by the AWSGlueServiceRole "
             "managed policy.")
    parser.add_argument(
        'bucket_name',
        help="The name of an S3 bucket that AWS Glue can access to get the job script and "
             "put job results.")
    parser.add_argument(
        '--job_script',
        default='flight_etl_job_script.py',
        help="The name of the job script file that is used in the scenario.")
    return parser.parse_args(args)


def main():
    args = parse_args(sys.argv[1:])
    try:
        print('-' * 88)
        print("Welcome to the AWS Glue getting started with crawlers and jobs scenario.")
        print('-' * 88)
        scenario = GlueCrawlerJobScenario(
            boto3.client('glue'),
            boto3.resource('iam').Role(args.role_name),
            boto3.resource('s3').Bucket(args.bucket_name))
        scenario.upload_job_script(args.job_script)
        scenario.run(
            'doc-example-crawler', 'doc-example-database', 'doc-example-',
            's3://crawler-public-us-east-1/flight/2016/csv',
            args.job_script, 'doc-example-job')
        print('-' * 88)
        print("To destroy scaffold resources, including the IAM role and S3 bucket "
              "used in this scenario, run 'python scaffold.py destroy'.")
        print("\nThanks for watching!")
        print('-' * 88)
    except Exception:
        logging.exception("Something went wrong with the example.")
# snippet-end:[python.example_code.glue.Scenario_GetStartedCrawlersJobs]


if __name__ == '__main__':
    main()
