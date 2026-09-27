# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Wrapper class for Amazon Athena operations.
Provides methods to create and manage workgroups, execute SQL queries,
manage named queries, and retrieve query results using the AWS SDK for Python (Boto3).
"""

import logging
import time
from typing import Any, Dict, List, Optional

import boto3
from botocore.exceptions import ClientError

logger = logging.getLogger(__name__)


# snippet-start:[python.example_code.athena.AthenaWrapper.class]
# snippet-start:[python.example_code.athena.AthenaWrapper.decl]
class AthenaWrapper:
    """Encapsulates Amazon Athena operations."""

    def __init__(self, athena_client: boto3.client) -> None:
        """
        Initializes the AthenaWrapper with an Athena client.

        :param athena_client: A Boto3 Amazon Athena client.
        """
        self.athena_client = athena_client

    @classmethod
    def from_client(cls) -> "AthenaWrapper":
        """Creates an AthenaWrapper with a default Athena client."""
        athena_client = boto3.client("athena")
        return cls(athena_client)

    # snippet-end:[python.example_code.athena.AthenaWrapper.decl]

    # snippet-start:[python.example_code.athena.CreateWorkGroup]
    def create_work_group(
        self,
        name: str,
        output_location: str,
        description: str = "Created by Athena basics scenario",
    ) -> None:
        """
        Creates an Athena workgroup with the specified name and configuration.

        :param name: The name for the new workgroup.
        :param output_location: The S3 location for query results (e.g., s3://bucket/prefix/).
        :param description: A description for the workgroup.
        :raises ClientError: If the workgroup could not be created.
        """
        try:
            self.athena_client.create_work_group(
                Name=name,
                Configuration={
                    "ResultConfiguration": {
                        "OutputLocation": output_location,
                    },
                    "EnforceWorkGroupConfiguration": True,
                    "PublishCloudWatchMetricsEnabled": True,
                },
                Description=description,
            )
            logger.info("Created workgroup '%s'.", name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request creating workgroup '%s'. The name may already "
                    "exist or the configuration is invalid. %s: %s",
                    name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.CreateWorkGroup]

    # snippet-start:[python.example_code.athena.GetWorkGroup]
    def get_work_group(self, name: str) -> Dict[str, Any]:
        """
        Returns information about the specified workgroup.

        :param name: The name of the workgroup.
        :return: A dictionary containing the workgroup details.
        :raises ClientError: If the workgroup information could not be retrieved.
        """
        try:
            response = self.athena_client.get_work_group(WorkGroup=name)
            work_group = response["WorkGroup"]
            logger.info("Retrieved workgroup '%s'.", name)
            return work_group
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request retrieving workgroup '%s'. The workgroup "
                    "was not found or the name is invalid. %s: %s",
                    name,
                    err.response["Error"]["Code"],
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
    ) -> str:
        """
        Runs a SQL query using Amazon Athena.

        :param query_string: The SQL query to execute.
        :param work_group: The workgroup in which to run the query.
        :param database: The database context for the query (optional).
        :return: The query execution ID.
        :raises ClientError: If the query could not be started.
        """
        try:
            params: Dict[str, Any] = dict()
            params["QueryString"] = query_string
            params["WorkGroup"] = work_group
            if database is not None:
                params["QueryExecutionContext"] = {"Database": database}
            response = self.athena_client.start_query_execution(**params)
            query_execution_id = response["QueryExecutionId"]
            logger.info(
                "Started query execution. ID: %s", query_execution_id
            )
            return query_execution_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request starting query execution. The query string, "
                    "database, or workgroup is invalid. %s: %s",
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.StartQueryExecution]

    # snippet-start:[python.example_code.athena.GetQueryExecution]
    def get_query_execution(self, query_execution_id: str) -> Dict[str, Any]:
        """
        Returns information about a single query execution.

        :param query_execution_id: The unique ID of the query execution.
        :return: A dictionary containing the query execution details.
        :raises ClientError: If the query execution information could not be retrieved.
        """
        try:
            response = self.athena_client.get_query_execution(
                QueryExecutionId=query_execution_id
            )
            query_execution = response["QueryExecution"]
            logger.info(
                "Retrieved query execution '%s'. State: %s",
                query_execution_id,
                query_execution["Status"]["State"],
            )
            return query_execution
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request retrieving query execution '%s'. "
                    "The execution ID is invalid or not found. %s: %s",
                    query_execution_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetQueryExecution]

    def wait_for_query_to_complete(
        self, query_execution_id: str, max_wait_seconds: int = 120
    ) -> Dict[str, Any]:
        """
        Polls GetQueryExecution until the query reaches a terminal state.

        :param query_execution_id: The unique ID of the query execution.
        :param max_wait_seconds: Maximum seconds to wait before timeout.
        :return: The final query execution details.
        :raises RuntimeError: If the query fails, is cancelled, or times out.
        """
        start_time = time.time()
        while True:
            query_execution = self.get_query_execution(query_execution_id)
            state = query_execution["Status"]["State"]
            if state == "SUCCEEDED":
                logger.info("Query '%s' succeeded.", query_execution_id)
                return query_execution
            elif state in ("FAILED", "CANCELLED"):
                reason = query_execution["Status"].get(
                    "AthenaError", dict()
                ).get("ErrorMessage", "Unknown error")
                raise RuntimeError(
                    f"Query '{query_execution_id}' {state.lower()}: {reason}"
                )
            elapsed = time.time() - start_time
            if elapsed >= max_wait_seconds:
                raise RuntimeError(
                    f"Query '{query_execution_id}' timed out after "
                    f"{max_wait_seconds} seconds. Last state: {state}"
                )
            time.sleep(2)

    # snippet-start:[python.example_code.athena.GetQueryResults]
    def get_query_results(
        self, query_execution_id: str
    ) -> Dict[str, Any]:
        """
        Retrieves the results of a completed query execution using pagination.

        :param query_execution_id: The unique ID of the query execution.
        :return: A dictionary with 'columns' (list of column names) and 'rows'
                 (list of lists of string values).
        :raises ClientError: If the query results could not be retrieved.
        """
        try:
            paginator = self.athena_client.get_paginator("get_query_results")
            page_iterator = paginator.paginate(
                QueryExecutionId=query_execution_id
            )
            columns = list()
            rows = list()
            first_page = True
            for page in page_iterator:
                result_set = page.get("ResultSet", dict())
                # Extract column names from metadata on first page
                if first_page:
                    column_info = result_set.get(
                        "ResultSetMetadata", dict()
                    ).get("ColumnInfo", list())
                    columns = [col["Name"] for col in column_info]
                    first_page = False
                page_rows = result_set.get("Rows", list())
                for row in page_rows:
                    data = row.get("Data", list())
                    row_values = [
                        datum.get("VarCharValue", "") for datum in data
                    ]
                    rows.append(row_values)
            # The first row from Athena is the header row; skip it if it
            # matches the column names.
            if rows and rows[0] == columns:
                rows = rows[1:]
            logger.info(
                "Retrieved %d result rows for query '%s'.",
                len(rows),
                query_execution_id,
            )
            return {"columns": columns, "rows": rows}
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request retrieving results for query '%s'. "
                    "The execution ID is invalid or the query has not completed. "
                    "%s: %s",
                    query_execution_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.GetQueryResults]

    # snippet-start:[python.example_code.athena.ListQueryExecutions]
    def list_query_executions(self, work_group: str) -> List[str]:
        """
        Lists query execution IDs for the specified workgroup using pagination.

        :param work_group: The name of the workgroup.
        :return: A list of query execution IDs.
        :raises ClientError: If the query executions could not be listed.
        """
        try:
            paginator = self.athena_client.get_paginator(
                "list_query_executions"
            )
            page_iterator = paginator.paginate(WorkGroup=work_group)
            execution_ids = list()
            for page in page_iterator:
                execution_ids.extend(
                    page.get("QueryExecutionIds", list())
                )
            logger.info(
                "Found %d query execution(s) in workgroup '%s'.",
                len(execution_ids),
                work_group,
            )
            return execution_ids
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request listing query executions for workgroup '%s'. "
                    "%s: %s",
                    work_group,
                    err.response["Error"]["Code"],
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
        Creates a named (saved) query in the specified workgroup.

        :param name: The name for the query.
        :param description: A description of the query.
        :param database: The database to which the query belongs.
        :param query_string: The SQL query string.
        :param work_group: The workgroup in which to save the query.
        :return: The named query ID.
        :raises ClientError: If the named query could not be created.
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
            logger.info(
                "Created named query '%s'. ID: %s", name, named_query_id
            )
            return named_query_id
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request creating named query '%s'. "
                    "The query name, database, or query string is invalid. "
                    "%s: %s",
                    name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.CreateNamedQuery]

    # snippet-start:[python.example_code.athena.ListNamedQueries]
    def list_named_queries(self, work_group: str) -> List[str]:
        """
        Lists named query IDs in the specified workgroup using pagination.

        :param work_group: The name of the workgroup.
        :return: A list of named query IDs.
        :raises ClientError: If the named queries could not be listed.
        """
        try:
            paginator = self.athena_client.get_paginator("list_named_queries")
            page_iterator = paginator.paginate(WorkGroup=work_group)
            named_query_ids = list()
            for page in page_iterator:
                named_query_ids.extend(
                    page.get("NamedQueryIds", list())
                )
            logger.info(
                "Found %d named query ID(s) in workgroup '%s'.",
                len(named_query_ids),
                work_group,
            )
            return named_query_ids
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request listing named queries for workgroup '%s'. "
                    "%s: %s",
                    work_group,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.ListNamedQueries]

    # snippet-start:[python.example_code.athena.DeleteNamedQuery]
    def delete_named_query(self, named_query_id: str) -> None:
        """
        Deletes a named query by its ID.

        :param named_query_id: The unique ID of the named query to delete.
        :raises ClientError: If the named query could not be deleted.
        """
        try:
            self.athena_client.delete_named_query(
                NamedQueryId=named_query_id
            )
            logger.info("Deleted named query '%s'.", named_query_id)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request deleting named query '%s'. "
                    "The named query ID was not found or is invalid. %s: %s",
                    named_query_id,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.DeleteNamedQuery]

    # snippet-start:[python.example_code.athena.DeleteWorkGroup]
    def delete_work_group(
        self, name: str, recursive: bool = True
    ) -> None:
        """
        Deletes the specified workgroup.

        :param name: The name of the workgroup to delete.
        :param recursive: If True, deletes the workgroup even if it contains
                          named queries or query executions.
        :raises ClientError: If the workgroup could not be deleted.
        """
        try:
            self.athena_client.delete_work_group(
                WorkGroup=name, RecursiveDeleteOption=recursive
            )
            logger.info("Deleted workgroup '%s'.", name)
        except ClientError as err:
            if err.response["Error"]["Code"] == "InvalidRequestException":
                logger.error(
                    "Invalid request deleting workgroup '%s'. "
                    "It may be the primary workgroup or contain resources "
                    "without RecursiveDeleteOption. %s: %s",
                    name,
                    err.response["Error"]["Code"],
                    err.response["Error"]["Message"],
                )
            raise

    # snippet-end:[python.example_code.athena.DeleteWorkGroup]


# snippet-end:[python.example_code.athena.AthenaWrapper.class]
