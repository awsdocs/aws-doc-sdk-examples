# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Amazon Athena wrapper class for managing Athena operations.
This module provides a wrapper around the Athena client to demonstrate
common operations such as creating workgroups, running queries,
managing named queries, and retrieving results.
"""

import logging
import time
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.athena.AthenaWrapper.decl]
class AthenaWrapper:
    """Encapsulates Amazon Athena operations."""

    def __init__(self, athena_client):
        """
        Initializes the AthenaWrapper with an Athena client.

        :param athena_client: A Boto3 Athena client.
        """
        self.athena_client = athena_client

    @classmethod
    def from_client(cls):
        """Creates an AthenaWrapper instance with a default Athena client."""
        athena_client = boto3.client("athena")
        return cls(athena_client)

    # snippet-end:[python.example_code.athena.AthenaWrapper.decl]

    # snippet-start:[python.example_code.athena.CreateWorkGroup]
    def create_work_group(
        self,
        name: str,
        output_location: str,
        description: str = "Workgroup for Athena Basics scenario",
    ) -> None:
        """
        Creates an Athena workgroup with the specified configuration.

        :param name: The name of the workgroup to create.
        :param output_location: The S3 location for query results.
        :param description: A description of the workgroup.
        :raises ClientError: If the workgroup creation fails.
        """
        try:
            self.athena_client.create_work_group(
                Name=name,
                Configuration={
                    "ResultConfiguration": {"OutputLocation": output_location},
                    "EnforceWorkGroupConfiguration": True,
                },
                Description=description,
            )
            logger.info("Created workgroup '%s'.", name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request creating workgroup '%s': %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.CreateWorkGroup]

    # snippet-start:[python.example_code.athena.GetWorkGroup]
    def get_work_group(self, name: str) -> Dict[str, Any]:
        """
        Gets details about an Athena workgroup.

        :param name: The name of the workgroup to retrieve.
        :return: A dictionary containing the workgroup details.
        :raises ClientError: If the request fails.
        """
        try:
            response = self.athena_client.get_work_group(WorkGroup=name)
            work_group = response["WorkGroup"]
            logger.info("Retrieved workgroup '%s'.", name)
            return work_group
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request getting workgroup '%s': %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetWorkGroup]

    # snippet-start:[python.example_code.athena.StartQueryExecution]
    def start_query_execution(
        self,
        query_string: str,
        work_group: str,
        database: Optional[str] = None,
        output_location: Optional[str] = None,
    ) -> str:
        """
        Starts an Athena query execution.

        :param query_string: The SQL query string to execute.
        :param work_group: The workgroup in which to run the query.
        :param database: The database context for the query (optional).
        :param output_location: The S3 output location (optional, overrides workgroup config).
        :return: The query execution ID.
        :raises ClientError: If the query execution fails to start.
        """
        try:
            params = dict()
            params["QueryString"] = query_string
            params["WorkGroup"] = work_group
            if database is not None:
                params["QueryExecutionContext"] = {"Database": database}
            if output_location is not None:
                params["ResultConfiguration"] = {"OutputLocation": output_location}
            response = self.athena_client.start_query_execution(**params)
            query_execution_id = response["QueryExecutionId"]
            logger.info("Started query execution with ID '%s'.", query_execution_id)
            return query_execution_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request starting query execution: %s",
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.StartQueryExecution]

    # snippet-start:[python.example_code.athena.GetQueryExecution]
    def get_query_execution(self, query_execution_id: str) -> Dict[str, Any]:
        """
        Gets the status and details of a query execution.

        :param query_execution_id: The ID of the query execution.
        :return: A dictionary containing the query execution details.
        :raises ClientError: If the request fails.
        """
        try:
            response = self.athena_client.get_query_execution(
                QueryExecutionId=query_execution_id
            )
            query_execution = response["QueryExecution"]
            logger.info(
                "Query execution '%s' state: %s.",
                query_execution_id,
                query_execution["Status"]["State"],
            )
            return query_execution
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request getting query execution '%s': %s",
                    query_execution_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetQueryExecution]

    # snippet-start:[python.example_code.athena.GetQueryResults]
    def get_query_results(self, query_execution_id: str) -> Dict[str, Any]:
        """
        Retrieves the results of a query execution using pagination.

        :param query_execution_id: The ID of the query execution.
        :return: A dictionary containing the result set.
        :raises ClientError: If the request fails.
        """
        try:
            paginator = self.athena_client.get_paginator("get_query_results")
            all_rows = list()
            column_info = list()
            for page in paginator.paginate(QueryExecutionId=query_execution_id):
                result_set = page.get("ResultSet", dict())
                rows = result_set.get("Rows", list())
                all_rows.extend(rows)
                if not column_info:
                    metadata = result_set.get("ResultSetMetadata", dict())
                    column_info = metadata.get("ColumnInfo", list())
            result = {
                "ResultSet": {
                    "Rows": all_rows,
                    "ResultSetMetadata": {"ColumnInfo": column_info},
                }
            }
            logger.info(
                "Retrieved %d rows for query execution '%s'.",
                len(all_rows),
                query_execution_id,
            )
            return result
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request getting query results for '%s': %s",
                    query_execution_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetQueryResults]

    # snippet-start:[python.example_code.athena.ListQueryExecutions]
    def list_query_executions(self, work_group: str) -> List[str]:
        """
        Lists query execution IDs for a workgroup using pagination.

        :param work_group: The workgroup name.
        :return: A list of query execution IDs.
        :raises ClientError: If the request fails.
        """
        try:
            paginator = self.athena_client.get_paginator("list_query_executions")
            query_execution_ids = list()
            for page in paginator.paginate(WorkGroup=work_group):
                ids = page.get("QueryExecutionIds", list())
                query_execution_ids.extend(ids)
            logger.info(
                "Listed %d query execution IDs for workgroup '%s'.",
                len(query_execution_ids),
                work_group,
            )
            return query_execution_ids
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request listing query executions for '%s': %s",
                    work_group,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.ListQueryExecutions]

    # snippet-start:[python.example_code.athena.CreateNamedQuery]
    def create_named_query(
        self,
        name: str,
        description: str,
        database: str,
        query_string: str,
        work_group: str,
    ) -> str:
        """
        Creates a named query in the specified workgroup.

        :param name: The name of the named query.
        :param description: A description of the named query.
        :param database: The database against which the query runs.
        :param query_string: The SQL query string.
        :param work_group: The workgroup for the named query.
        :return: The named query ID.
        :raises ClientError: If the request fails.
        """
        try:
            response = self.athena_client.create_named_query(
                Name=name,
                Description=description,
                Database=database,
                QueryString=query_string,
                WorkGroup=work_group,
            )
            named_query_id = response["NamedQueryId"]
            logger.info("Created named query '%s' with ID '%s'.", name, named_query_id)
            return named_query_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request creating named query '%s': %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.CreateNamedQuery]

    # snippet-start:[python.example_code.athena.GetNamedQuery]
    def get_named_query(self, named_query_id: str) -> Dict[str, Any]:
        """
        Gets details about a named query.

        :param named_query_id: The ID of the named query.
        :return: A dictionary containing the named query details.
        :raises ClientError: If the request fails.
        """
        try:
            response = self.athena_client.get_named_query(
                NamedQueryId=named_query_id
            )
            named_query = response["NamedQuery"]
            logger.info("Retrieved named query '%s'.", named_query.get("Name"))
            return named_query
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request getting named query '%s': %s",
                    named_query_id,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetNamedQuery]

    # snippet-start:[python.example_code.athena.ListNamedQueries]
    def list_named_queries(self, work_group: str) -> List[str]:
        """
        Lists named query IDs for a workgroup using pagination.

        :param work_group: The workgroup name.
        :return: A list of named query IDs.
        :raises ClientError: If the request fails.
        """
        try:
            paginator = self.athena_client.get_paginator("list_named_queries")
            named_query_ids = list()
            for page in paginator.paginate(WorkGroup=work_group):
                ids = page.get("NamedQueryIds", list())
                named_query_ids.extend(ids)
            logger.info(
                "Listed %d named query IDs for workgroup '%s'.",
                len(named_query_ids),
                work_group,
            )
            return named_query_ids
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request listing named queries for '%s': %s",
                    work_group,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.ListNamedQueries]

    # snippet-start:[python.example_code.athena.DeleteWorkGroup]
    def delete_work_group(self, name: str, recursive: bool = True) -> None:
        """
        Deletes an Athena workgroup.

        :param name: The name of the workgroup to delete.
        :param recursive: Whether to recursively delete contents.
        :raises ClientError: If the request fails.
        """
        try:
            self.athena_client.delete_work_group(
                WorkGroup=name, RecursiveDeleteOption=recursive
            )
            logger.info("Deleted workgroup '%s'.", name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request deleting workgroup '%s': %s",
                    name,
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.DeleteWorkGroup]

    def wait_for_query_completion(
        self, query_execution_id: str, max_wait_seconds: int = 120
    ) -> Dict[str, Any]:
        """
        Polls GetQueryExecution until the query reaches a terminal state.

        :param query_execution_id: The ID of the query execution to wait on.
        :param max_wait_seconds: Maximum time to wait in seconds.
        :return: The final query execution details.
        :raises TimeoutError: If the query does not complete in time.
        :raises RuntimeError: If the query fails or is cancelled.
        """
        elapsed = 0
        while elapsed < max_wait_seconds:
            query_execution = self.get_query_execution(query_execution_id)
            state = query_execution["Status"]["State"]
            if state == "SUCCEEDED":
                return query_execution
            elif state in ("FAILED", "CANCELLED"):
                reason = query_execution["Status"].get("StateChangeReason", "Unknown")
                raise RuntimeError(
                    f"Query {query_execution_id} {state}: {reason}"
                )
            time.sleep(2)
            elapsed += 2
        raise TimeoutError(
            f"Query {query_execution_id} did not complete within {max_wait_seconds}s."
        )


# snippet-start:[python.example_code.athena.AthenaScenario]
class AthenaScenario:
    """Demonstrates an Amazon Athena basics scenario."""

    def __init__(self, athena_wrapper, s3_client, cf_client):
        """
        Initializes the scenario.

        :param athena_wrapper: An AthenaWrapper instance.
        :param s3_client: A Boto3 S3 client.
        :param cf_client: A Boto3 CloudFormation client.
        """
        self.athena_wrapper = athena_wrapper
        self.s3_client = s3_client
        self.cf_client = cf_client
        self.stack_name = None
        self.bucket_name = None
        self.workgroup_name = None

    def setup(self, stack_name: str, workgroup_name: str) -> None:
        """
        Sets up the scenario resources.

        :param stack_name: Name for the CloudFormation stack.
        :param workgroup_name: Name for the Athena workgroup.
        """
        self.stack_name = stack_name
        self.workgroup_name = workgroup_name

        # Create the CloudFormation stack with an S3 bucket
        template_body = """
AWSTemplateFormatVersion: '2010-09-09'
Description: S3 bucket for Athena Basics scenario
Resources:
  AthenaBucket:
    Type: AWS::S3::Bucket
    Properties:
      BucketName: !Sub 'athena-basics-${AWS::AccountId}-${AWS::Region}'
Outputs:
  BucketName:
    Value: !Ref AthenaBucket
    Description: Name of the S3 bucket
"""
        print(f"Creating CloudFormation stack '{stack_name}'...")
        self.cf_client.create_stack(
            StackName=stack_name,
            TemplateBody=template_body,
        )

        # Wait for stack creation
        waiter = self.cf_client.get_waiter("stack_create_complete")
        waiter.wait(StackName=stack_name)

        # Get the bucket name from outputs
        response = self.cf_client.describe_stacks(StackName=stack_name)
        outputs = response["Stacks"][0]["Outputs"]
        for output in outputs:
            if output["OutputKey"] == "BucketName":
                self.bucket_name = output["OutputValue"]
                break

        print(f"Stack created. Bucket: {self.bucket_name}")

        # Upload sample CSV data
        csv_data = (
            "order_id,product_name,category,quantity,price,order_date\n"
            "1,Widget A,Electronics,3,19.99,2024-01-15\n"
            "2,Gadget B,Electronics,1,49.99,2024-01-16\n"
            "3,Widget C,Home,5,9.99,2024-01-17\n"
            "4,Tool D,Hardware,2,29.99,2024-01-18\n"
            "5,Gadget E,Electronics,4,39.99,2024-01-19\n"
            "6,Widget F,Home,1,14.99,2024-01-20\n"
            "7,Tool G,Hardware,6,24.99,2024-01-21\n"
            "8,Widget H,Electronics,3,34.99,2024-01-22\n"
            "9,Gadget I,Home,2,44.99,2024-01-23\n"
            "10,Tool J,Hardware,7,19.99,2024-01-24\n"
            "11,Widget K,Electronics,4,54.99,2024-01-25\n"
            "12,Gadget L,Home,3,29.99,2024-02-01\n"
            "13,Tool M,Hardware,1,64.99,2024-02-02\n"
            "14,Widget N,Electronics,5,12.99,2024-02-03\n"
            "15,Gadget O,Home,2,22.99,2024-02-04\n"
        )
        self.s3_client.put_object(
            Bucket=self.bucket_name,
            Key="sample-data/sales.csv",
            Body=csv_data.encode("utf-8"),
        )
        print("Uploaded sample data to S3.")

    def run_scenario(self) -> None:
        """Runs the Athena basics scenario."""
        output_location = f"s3://{self.bucket_name}/query-results/"
        database = "athena_basics_db"

        print("\n" + "=" * 60)
        print("Amazon Athena Basics Scenario")
        print("=" * 60)

        # Step 1: Create a workgroup
        print(f"\nStep 1: Creating workgroup '{self.workgroup_name}'...")
        self.athena_wrapper.create_work_group(
            name=self.workgroup_name,
            output_location=output_location,
            description="Workgroup for Athena Basics scenario",
        )
        print(f"  Workgroup '{self.workgroup_name}' created.")

        # Step 2: Get workgroup details
        print(f"\nStep 2: Getting workgroup details...")
        work_group = self.athena_wrapper.get_work_group(self.workgroup_name)
        wg_detail = work_group.get("Configuration", dict())
        print(f"  Name: {work_group.get('Name')}")
        print(f"  State: {work_group.get('State')}")
        print(f"  Description: {work_group.get('Description')}")
        result_config = wg_detail.get("ResultConfiguration", dict())
        print(f"  Output Location: {result_config.get('OutputLocation')}")
        print(f"  Creation Time: {work_group.get('CreationTime')}")

        # Step 3: Create a database
        print(f"\nStep 3: Creating database '{database}'...")
        create_db_query = f"CREATE DATABASE IF NOT EXISTS {database}"
        query_id = self.athena_wrapper.start_query_execution(
            query_string=create_db_query, work_group=self.workgroup_name
        )
        self.athena_wrapper.wait_for_query_completion(query_id)
        print(f"  Database '{database}' created.")

        # Step 4: Create an external table
        print(f"\nStep 4: Creating external table 'sales'...")
        create_table_query = f"""
CREATE EXTERNAL TABLE IF NOT EXISTS {database}.sales (
  order_id INT,
  product_name STRING,
  category STRING,
  quantity INT,
  price DOUBLE,
  order_date STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
STORED AS TEXTFILE
LOCATION 's3://{self.bucket_name}/sample-data/'
TBLPROPERTIES ('skip.header.line.count'='1')
"""
        query_id = self.athena_wrapper.start_query_execution(
            query_string=create_table_query,
            work_group=self.workgroup_name,
            database=database,
        )
        self.athena_wrapper.wait_for_query_completion(query_id)
        print(f"  Table '{database}.sales' created.")

        # Step 5: Run a SELECT query and get results
        print(f"\nStep 5: Running analytical query...")
        select_query = (
            f"SELECT product_name, category, quantity, price "
            f"FROM {database}.sales WHERE quantity > 2 ORDER BY price DESC"
        )
        query_id = self.athena_wrapper.start_query_execution(
            query_string=select_query,
            work_group=self.workgroup_name,
            database=database,
        )
        query_execution = self.athena_wrapper.wait_for_query_completion(query_id)
        results = self.athena_wrapper.get_query_results(query_id)

        rows = results["ResultSet"]["Rows"]
        if rows:
            # First row is headers
            headers = [col.get("VarCharValue", "") for col in rows[0]["Data"]]
            print(f"  {'  '.join(f'{h:<15}' for h in headers)}")
            print(f"  {'-' * 60}")
            for row in rows[1:]:
                values = [col.get("VarCharValue", "") for col in row["Data"]]
                print(f"  {'  '.join(f'{v:<15}' for v in values)}")

        stats = query_execution.get("Statistics", dict())
        print(f"\n  Data scanned: {stats.get('DataScannedInBytes', 0)} bytes")
        print(
            f"  Execution time: {stats.get('EngineExecutionTimeInMillis', 0)} ms"
        )

        # Step 6: Create a named query
        print(f"\nStep 6: Creating named query...")
        named_query_id = self.athena_wrapper.create_named_query(
            name="Top selling products",
            description="Returns the top products by total revenue",
            database=database,
            query_string=(
                f"SELECT product_name, SUM(quantity * price) as total_revenue "
                f"FROM {database}.sales GROUP BY product_name "
                f"ORDER BY total_revenue DESC"
            ),
            work_group=self.workgroup_name,
        )
        print(f"  Named query created with ID: {named_query_id}")

        # Step 7: List named queries
        print(f"\nStep 7: Listing named queries...")
        named_query_ids = self.athena_wrapper.list_named_queries(self.workgroup_name)
        print(f"  Found {len(named_query_ids)} named query(ies):")
        for nq_id in named_query_ids:
            print(f"    - {nq_id}")
        assert named_query_id in named_query_ids, "Created named query not found!"

        # Step 8: Get named query details
        print(f"\nStep 8: Getting named query details...")
        named_query = self.athena_wrapper.get_named_query(named_query_id)
        print(f"  Name: {named_query.get('Name')}")
        print(f"  Description: {named_query.get('Description')}")
        print(f"  Database: {named_query.get('Database')}")
        print(f"  Query: {named_query.get('QueryString')}")
        print(f"  WorkGroup: {named_query.get('WorkGroup')}")

        # Step 9: List query executions
        print(f"\nStep 9: Listing query executions...")
        execution_ids = self.athena_wrapper.list_query_executions(self.workgroup_name)
        print(f"  Found {len(execution_ids)} query execution(s):")
        for exec_id in execution_ids[:5]:  # Show first 5
            print(f"    - {exec_id}")

        # Step 10: Delete the workgroup
        print(f"\nStep 10: Deleting workgroup '{self.workgroup_name}'...")
        self.athena_wrapper.delete_work_group(self.workgroup_name, recursive=True)
        print(f"  Workgroup '{self.workgroup_name}' deleted.")

        print("\n" + "=" * 60)
        print("Scenario complete!")
        print("=" * 60)

    def cleanup(self) -> None:
        """Cleans up all resources created by the scenario."""
        print("\nCleaning up resources...")

        # Clean up database and table using primary workgroup
        try:
            output_location = f"s3://{self.bucket_name}/query-results/"
            drop_table_id = self.athena_wrapper.start_query_execution(
                query_string="DROP TABLE IF EXISTS athena_basics_db.sales",
                work_group="primary",
                output_location=output_location,
            )
            self.athena_wrapper.wait_for_query_completion(drop_table_id)
            print("  Dropped table athena_basics_db.sales.")
        except Exception as e:
            logger.warning("Failed to drop table: %s", e)

        try:
            drop_db_id = self.athena_wrapper.start_query_execution(
                query_string="DROP DATABASE IF EXISTS athena_basics_db",
                work_group="primary",
                output_location=f"s3://{self.bucket_name}/query-results/",
            )
            self.athena_wrapper.wait_for_query_completion(drop_db_id)
            print("  Dropped database athena_basics_db.")
        except Exception as e:
            logger.warning("Failed to drop database: %s", e)

        # Empty the S3 bucket before deleting the stack
        if self.bucket_name:
            try:
                paginator = self.s3_client.get_paginator("list_objects_v2")
                for page in paginator.paginate(Bucket=self.bucket_name):
                    objects = page.get("Contents", list())
                    if objects:
                        delete_keys = [{"Key": obj["Key"]} for obj in objects]
                        self.s3_client.delete_objects(
                            Bucket=self.bucket_name,
                            Delete={"Objects": delete_keys},
                        )
                print(f"  Emptied bucket '{self.bucket_name}'.")
            except Exception as e:
                logger.warning("Failed to empty bucket: %s", e)

        # Delete the CloudFormation stack
        if self.stack_name:
            try:
                self.cf_client.delete_stack(StackName=self.stack_name)
                waiter = self.cf_client.get_waiter("stack_delete_complete")
                waiter.wait(StackName=self.stack_name)
                print(f"  Deleted stack '{self.stack_name}'.")
            except Exception as e:
                logger.warning("Failed to delete stack: %s", e)

        print("Cleanup complete.")


# snippet-end:[python.example_code.athena.AthenaScenario]


def main():
    """Main entry point for the Athena basics scenario."""
    import uuid

    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    suffix = str(uuid.uuid4())[:8]
    stack_name = f"athena-basics-{suffix}"
    workgroup_name = f"athena-basics-workgroup-{suffix}"

    athena_wrapper = AthenaWrapper.from_client()
    s3_client = boto3.client("s3")
    cf_client = boto3.client("cloudformation")

    scenario = AthenaScenario(athena_wrapper, s3_client, cf_client)

    try:
        scenario.setup(stack_name, workgroup_name)
        scenario.run_scenario()
    finally:
        scenario.cleanup()


if __name__ == "__main__":
    main()
