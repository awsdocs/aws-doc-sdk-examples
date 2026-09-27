# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Athena Basics Scenario

This scenario demonstrates a complete Amazon Athena workflow:
1. Deploy a CloudFormation stack to create an S3 bucket for query results.
2. Create an Athena workgroup.
3. Verify the workgroup configuration (GetWorkGroup).
4. Create a database using a DDL query (StartQueryExecution + GetQueryExecution).
5. Create a table with sample data using a CTAS query.
6. Run a SELECT query and retrieve results (GetQueryResults).
7. Create a named query (CreateNamedQuery).
8. List named queries (ListNamedQueries).
9. Execute the saved named query and retrieve results.
10. List query executions (ListQueryExecutions).
11. Clean up all resources.

Usage:
    python scenario_athena_basics.py
"""

import json
import logging
import time
import uuid

import boto3
from botocore.exceptions import ClientError

from athena_wrapper import AthenaWrapper

logger = logging.getLogger(__name__)

DASHES = "-" * 80
DATABASE_NAME = "athena_basics_db"
TABLE_NAME = "movies"


# snippet-start:[python.example_code.athena.AthenaScenario]
class AthenaScenario:
    """Runs the Amazon Athena basics scenario."""

    def __init__(
        self,
        athena_wrapper: AthenaWrapper,
        cf_client: boto3.client,
        s3_client: boto3.client,
    ) -> None:
        """
        Initializes the scenario.

        :param athena_wrapper: An AthenaWrapper instance for Athena operations.
        :param cf_client: A Boto3 CloudFormation client.
        :param s3_client: A Boto3 S3 client.
        """
        self.athena_wrapper = athena_wrapper
        self.cf_client = cf_client
        self.s3_client = s3_client
        self.stack_name = ""
        self.bucket_name = ""
        self.workgroup_name = ""
        self.named_query_id = ""

    def run(self) -> None:
        """Orchestrates the complete Athena basics scenario."""
        print(DASHES)
        print("Welcome to the Amazon Athena Basics Scenario!")
        print(
            "This scenario demonstrates how to use Amazon Athena to create "
            "workgroups, run SQL queries, manage named queries, and more."
        )
        print(DASHES)

        try:
            self.setup()
            self.step1_verify_workgroup()
            self.step2_create_database()
            self.step3_create_table()
            self.step4_run_select_query()
            self.step5_create_named_query()
            self.step6_list_named_queries()
            self.step7_execute_named_query()
            self.step8_list_query_executions()
        finally:
            self.cleanup()

        print(DASHES)
        print("Amazon Athena Basics scenario complete!")
        print(DASHES)

    # ---- Setup ---------------------------------------------------------------

    def setup(self) -> None:
        """Deploys the CloudFormation stack and creates the Athena workgroup."""
        print(DASHES)
        print("Setting up resources...")
        print(DASHES)

        # Deploy CloudFormation stack for S3 bucket.
        unique_suffix = uuid.uuid4().hex[:8]
        self.stack_name = f"athena-basics-stack-{unique_suffix}"
        self.workgroup_name = f"athena-basics-wg-{unique_suffix}"

        template_body = json.dumps(
            {
                "AWSTemplateFormatVersion": "2010-09-09",
                "Description": "S3 bucket for Athena basics scenario query results.",
                "Resources": {
                    "ResultsBucket": {
                        "Type": "AWS::S3::Bucket",
                        "Properties": {
                            "BucketName": f"{self.stack_name}-results",
                        },
                    }
                },
                "Outputs": {
                    "BucketName": {
                        "Value": {"Ref": "ResultsBucket"},
                        "Description": "Name of the S3 results bucket.",
                    }
                },
            }
        )

        print(f"Creating CloudFormation stack '{self.stack_name}'...")
        self.cf_client.create_stack(
            StackName=self.stack_name,
            TemplateBody=template_body,
        )

        # Wait for stack creation.
        waiter = self.cf_client.get_waiter("stack_create_complete")
        print("Waiting for stack creation to complete...")
        waiter.wait(
            StackName=self.stack_name,
            WaiterConfig={"Delay": 10, "MaxAttempts": 60},
        )

        # Retrieve bucket name from outputs.
        response = self.cf_client.describe_stacks(StackName=self.stack_name)
        outputs = response["Stacks"][0].get("Outputs", list())
        for output in outputs:
            if output["OutputKey"] == "BucketName":
                self.bucket_name = output["OutputValue"]
                break

        print(f"CloudFormation stack '{self.stack_name}' created successfully.")
        print(f"S3 bucket for query results: {self.bucket_name}")

        # Create Athena workgroup.
        output_location = f"s3://{self.bucket_name}/athena-results/"
        print(f"\nCreating Athena workgroup '{self.workgroup_name}'...")
        self.athena_wrapper.create_work_group(
            name=self.workgroup_name,
            output_location=output_location,
        )
        print(f"Workgroup '{self.workgroup_name}' created successfully.")
        print(DASHES)

    # ---- Step 1 --------------------------------------------------------------

    def step1_verify_workgroup(self) -> None:
        """Verifies the workgroup configuration."""
        print(DASHES)
        print("Step 1: Verifying workgroup configuration")

        wg = self.athena_wrapper.get_work_group(self.workgroup_name)
        config = wg.get("Configuration", dict())
        result_config = config.get("ResultConfiguration", dict())

        print(f"  Workgroup: {wg.get('Name')}")
        print(f"  State: {wg.get('State')}")
        print(f"  Output location: {result_config.get('OutputLocation', 'N/A')}")
        print(
            f"  Enforce configuration: "
            f"{config.get('EnforceWorkGroupConfiguration', 'N/A')}"
        )
        print(
            f"  CloudWatch metrics enabled: "
            f"{config.get('PublishCloudWatchMetricsEnabled', 'N/A')}"
        )
        print(DASHES)

    # ---- Step 2 --------------------------------------------------------------

    def step2_create_database(self) -> None:
        """Creates a database using a DDL query."""
        print(DASHES)
        print(f"Step 2: Creating database '{DATABASE_NAME}'")

        query = f"CREATE DATABASE IF NOT EXISTS {DATABASE_NAME}"
        print(f"Running query: {query}")

        execution_id = self.athena_wrapper.start_query_execution(
            query_string=query, work_group=self.workgroup_name
        )
        print(f"Query execution ID: {execution_id}")
        print("Waiting for query to complete...")

        result = self.athena_wrapper.wait_for_query_to_complete(execution_id)
        exec_time = (
            result.get("Statistics", dict()).get(
                "EngineExecutionTimeInMillis", 0
            )
        )
        print(
            f"Query completed successfully. (Execution time: {exec_time}ms)"
        )
        print(f"Database '{DATABASE_NAME}' created.")
        print(DASHES)

    # ---- Step 3 --------------------------------------------------------------

    def step3_create_table(self) -> None:
        """Creates a table with sample data using CTAS."""
        print(DASHES)
        print(f"Step 3: Creating table '{TABLE_NAME}' with sample data")

        query = f"""
        CREATE TABLE {DATABASE_NAME}.{TABLE_NAME} AS
        SELECT * FROM (
            VALUES
                (1, 'The Shawshank Redemption', 1994, 9.3),
                (2, 'The Godfather', 1972, 9.2),
                (3, 'The Dark Knight', 2008, 9.0),
                (4, 'Pulp Fiction', 1994, 8.9),
                (5, 'Forrest Gump', 1994, 8.8),
                (6, 'Inception', 2010, 8.8),
                (7, 'The Matrix', 1999, 8.7),
                (8, 'Goodfellas', 1990, 8.7),
                (9, 'The Silence of the Lambs', 1991, 8.6),
                (10, 'Saving Private Ryan', 1998, 8.6)
        ) AS t(id, title, year, rating)
        """
        print("Running CTAS query to create table with 10 movie records...")

        execution_id = self.athena_wrapper.start_query_execution(
            query_string=query,
            work_group=self.workgroup_name,
            database=DATABASE_NAME,
        )
        print(f"Query execution ID: {execution_id}")
        print("Waiting for query to complete...")

        result = self.athena_wrapper.wait_for_query_to_complete(execution_id)
        exec_time = (
            result.get("Statistics", dict()).get(
                "EngineExecutionTimeInMillis", 0
            )
        )
        print(
            f"Query completed successfully. (Execution time: {exec_time}ms)"
        )
        print(f"Table '{DATABASE_NAME}.{TABLE_NAME}' created with sample data.")
        print(DASHES)

    # ---- Step 4 --------------------------------------------------------------

    def step4_run_select_query(self) -> None:
        """Runs a SELECT query and retrieves results."""
        print(DASHES)
        print("Step 4: Running analytical query")

        query = (
            f"SELECT title, year, rating FROM {DATABASE_NAME}.{TABLE_NAME} "
            "WHERE rating >= 9.0 ORDER BY rating DESC"
        )
        print(f"Query: {query}")

        execution_id = self.athena_wrapper.start_query_execution(
            query_string=query,
            work_group=self.workgroup_name,
            database=DATABASE_NAME,
        )
        print(f"Query execution ID: {execution_id}")
        print("Waiting for query to complete...")

        result = self.athena_wrapper.wait_for_query_to_complete(execution_id)
        stats = result.get("Statistics", dict())
        exec_time = stats.get("EngineExecutionTimeInMillis", 0)
        data_scanned = stats.get("DataScannedInBytes", 0)
        print(
            f"Query completed successfully. "
            f"(Execution time: {exec_time}ms, Data scanned: {data_scanned} bytes)"
        )

        # Retrieve and display results.
        query_results = self.athena_wrapper.get_query_results(execution_id)
        columns = query_results["columns"]
        rows = query_results["rows"]

        print(f"\nQuery Results:")
        header = " | ".join(f"{c:<30}" for c in columns)
        print(f"  {header}")
        print(f"  {'-' * len(header)}")
        for row in rows:
            row_str = " | ".join(f"{v:<30}" for v in row)
            print(f"  {row_str}")
        print(f"\n{len(rows)} rows returned.")
        print(DASHES)

    # ---- Step 5 --------------------------------------------------------------

    def step5_create_named_query(self) -> None:
        """Creates a named (saved) query."""
        print(DASHES)
        print("Step 5: Creating a named (saved) query")

        self.named_query_id = self.athena_wrapper.create_named_query(
            name="top-rated-movies",
            description="Returns movies with a rating of 9.0 or higher",
            database=DATABASE_NAME,
            query_string=(
                "SELECT title, year, rating FROM movies "
                "WHERE rating >= 9.0 ORDER BY rating DESC"
            ),
            work_group=self.workgroup_name,
        )
        print(f"Named query 'top-rated-movies' created successfully.")
        print(f"Named Query ID: {self.named_query_id}")
        print("Description: Returns movies with a rating of 9.0 or higher")
        print(DASHES)

    # ---- Step 6 --------------------------------------------------------------

    def step6_list_named_queries(self) -> None:
        """Lists named queries in the workgroup."""
        print(DASHES)
        print("Step 6: Listing named queries in workgroup")

        named_query_ids = self.athena_wrapper.list_named_queries(
            self.workgroup_name
        )
        print(
            f"Found {len(named_query_ids)} named query ID(s) in workgroup "
            f"'{self.workgroup_name}':"
        )
        for nq_id in named_query_ids:
            print(f"  - {nq_id}")
        if self.named_query_id in named_query_ids:
            print("Verified: Our saved query is in the list.")
        print(DASHES)

    # ---- Step 7 --------------------------------------------------------------

    def step7_execute_named_query(self) -> None:
        """Executes the saved named query and retrieves results."""
        print(DASHES)
        print("Step 7: Executing the saved named query")

        # Retrieve the query string from the named query.
        query_string = (
            "SELECT title, year, rating FROM movies "
            "WHERE rating >= 9.0 ORDER BY rating DESC"
        )
        print("Running named query 'top-rated-movies'...")

        execution_id = self.athena_wrapper.start_query_execution(
            query_string=query_string,
            work_group=self.workgroup_name,
            database=DATABASE_NAME,
        )
        print(f"Query execution ID: {execution_id}")
        self.athena_wrapper.wait_for_query_to_complete(execution_id)
        print("Query completed successfully.")

        query_results = self.athena_wrapper.get_query_results(execution_id)
        columns = query_results["columns"]
        rows = query_results["rows"]

        print(f"\nResults:")
        header = " | ".join(f"{c:<30}" for c in columns)
        print(f"  {header}")
        print(f"  {'-' * len(header)}")
        for row in rows:
            row_str = " | ".join(f"{v:<30}" for v in row)
            print(f"  {row_str}")
        print(f"\n{len(rows)} rows returned.")
        print(DASHES)

    # ---- Step 8 --------------------------------------------------------------

    def step8_list_query_executions(self) -> None:
        """Lists query executions in the workgroup."""
        print(DASHES)
        print("Step 8: Listing query executions in workgroup")

        execution_ids = self.athena_wrapper.list_query_executions(
            self.workgroup_name
        )
        print(
            f"Found {len(execution_ids)} query execution(s) in workgroup "
            f"'{self.workgroup_name}':"
        )
        for i, eid in enumerate(execution_ids, 1):
            print(f"  {i}. {eid}")
        print("All queries were executed during this scenario.")
        print(DASHES)

    # ---- Cleanup -------------------------------------------------------------

    def cleanup(self) -> None:
        """Cleans up all resources created during the scenario."""
        print(DASHES)
        print("Cleaning up resources...")

        # 1. Delete the named query.
        if self.named_query_id:
            try:
                print(f"\nDeleting named query '{self.named_query_id}'...")
                self.athena_wrapper.delete_named_query(self.named_query_id)
                print("Named query deleted.")
            except ClientError as err:
                logger.error("Error deleting named query: %s", err)

        # 2. Drop the table and database via Athena queries.
        if self.workgroup_name:
            try:
                print(f"\nDropping table '{DATABASE_NAME}.{TABLE_NAME}'...")
                drop_table_id = self.athena_wrapper.start_query_execution(
                    query_string=f"DROP TABLE IF EXISTS {DATABASE_NAME}.{TABLE_NAME}",
                    work_group=self.workgroup_name,
                )
                self.athena_wrapper.wait_for_query_to_complete(drop_table_id)
                print("Table dropped.")
            except (ClientError, RuntimeError) as err:
                logger.error("Error dropping table: %s", err)

            try:
                print(f"\nDropping database '{DATABASE_NAME}'...")
                drop_db_id = self.athena_wrapper.start_query_execution(
                    query_string=f"DROP DATABASE IF EXISTS {DATABASE_NAME}",
                    work_group=self.workgroup_name,
                )
                self.athena_wrapper.wait_for_query_to_complete(drop_db_id)
                print("Database dropped.")
            except (ClientError, RuntimeError) as err:
                logger.error("Error dropping database: %s", err)

        # 3. Delete the Athena workgroup.
        if self.workgroup_name:
            try:
                print(f"\nDeleting workgroup '{self.workgroup_name}'...")
                self.athena_wrapper.delete_work_group(
                    name=self.workgroup_name, recursive=True
                )
                print("Workgroup deleted.")
            except ClientError as err:
                logger.error("Error deleting workgroup: %s", err)

        # 4. Delete the CloudFormation stack (and S3 bucket).
        if self.stack_name:
            try:
                # Empty the S3 bucket first.
                if self.bucket_name:
                    print(f"\nEmptying S3 bucket '{self.bucket_name}'...")
                    self._empty_bucket(self.bucket_name)

                print(f"\nDeleting CloudFormation stack '{self.stack_name}'...")
                self.cf_client.delete_stack(StackName=self.stack_name)
                waiter = self.cf_client.get_waiter("stack_delete_complete")
                print("Waiting for stack deletion...")
                waiter.wait(
                    StackName=self.stack_name,
                    WaiterConfig={"Delay": 10, "MaxAttempts": 60},
                )
                print("Stack deleted successfully.")
            except ClientError as err:
                logger.error("Error deleting CloudFormation stack: %s", err)

        print("\nAll resources cleaned up successfully.")
        print(DASHES)

    def _empty_bucket(self, bucket_name: str) -> None:
        """
        Deletes all objects in the specified S3 bucket.

        :param bucket_name: The name of the S3 bucket to empty.
        """
        try:
            paginator = self.s3_client.get_paginator("list_objects_v2")
            page_iterator = paginator.paginate(Bucket=bucket_name)
            for page in page_iterator:
                contents = page.get("Contents", list())
                if contents:
                    objects = [{"Key": obj["Key"]} for obj in contents]
                    self.s3_client.delete_objects(
                        Bucket=bucket_name,
                        Delete={"Objects": objects},
                    )
            logger.info("Emptied bucket '%s'.", bucket_name)
        except ClientError as err:
            logger.error("Error emptying bucket '%s': %s", bucket_name, err)


# snippet-end:[python.example_code.athena.AthenaScenario]


def main() -> None:
    """Entry point for the Athena basics scenario."""
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    athena_client = boto3.client("athena")
    cf_client = boto3.client("cloudformation")
    s3_client = boto3.client("s3")

    athena_wrapper = AthenaWrapper(athena_client)
    scenario = AthenaScenario(athena_wrapper, cf_client, s3_client)
    scenario.run()


if __name__ == "__main__":
    main()
