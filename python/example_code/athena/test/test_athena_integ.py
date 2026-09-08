# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for athena_wrapper.py using botocore Stubber.
These tests run offline without AWS credentials.
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from datetime import datetime, timezone

import boto3
import pytest
from botocore.stub import Stubber

from athena_wrapper import AthenaWrapper


@pytest.fixture
def athena_client():
    """Create a boto3 Athena client for testing."""
    return boto3.client("athena", region_name="us-east-1")


@pytest.fixture
def wrapper(athena_client):
    """Create an AthenaWrapper instance with the test client."""
    return AthenaWrapper(athena_client)


class TestCreateWorkGroup:
    """Tests for create_work_group method."""

    def test_create_work_group(self, athena_client, wrapper):
        """Test successful workgroup creation."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "create_work_group",
            {},
            expected_params={
                "Name": "test-workgroup",
                "Configuration": {
                    "ResultConfiguration": {
                        "OutputLocation": "s3://test-bucket/results/"
                    },
                    "EnforceWorkGroupConfiguration": True,
                },
                "Description": "Test workgroup",
            },
        )
        stubber.activate()
        wrapper.create_work_group(
            name="test-workgroup",
            output_location="s3://test-bucket/results/",
            description="Test workgroup",
        )
        stubber.deactivate()

    def test_create_work_group_invalid_request(self, athena_client, wrapper):
        """Test workgroup creation with InvalidRequestException."""
        stubber = Stubber(athena_client)
        stubber.add_client_error(
            "create_work_group",
            service_error_code="InvalidRequestException",
            service_message="Workgroup already exists",
        )
        stubber.activate()
        with pytest.raises(Exception) as exc_info:
            wrapper.create_work_group(
                name="test-workgroup",
                output_location="s3://test-bucket/results/",
            )
        assert "InvalidRequestException" in str(
            exc_info.value.response["Error"]["Code"]
        )
        stubber.deactivate()


class TestGetWorkGroup:
    """Tests for get_work_group method."""

    def test_get_work_group(self, athena_client, wrapper):
        """Test successful workgroup retrieval."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "get_work_group",
            {
                "WorkGroup": {
                    "Name": "test-workgroup",
                    "State": "ENABLED",
                    "Description": "Test workgroup",
                    "Configuration": {
                        "ResultConfiguration": {
                            "OutputLocation": "s3://test-bucket/results/"
                        },
                        "EnforceWorkGroupConfiguration": True,
                    },
                    "CreationTime": datetime(2024, 1, 1, tzinfo=timezone.utc),
                }
            },
            expected_params={"WorkGroup": "test-workgroup"},
        )
        stubber.activate()
        result = wrapper.get_work_group("test-workgroup")
        assert result["Name"] == "test-workgroup"
        assert result["State"] == "ENABLED"
        stubber.deactivate()


class TestStartQueryExecution:
    """Tests for start_query_execution method."""

    def test_start_query_execution(self, athena_client, wrapper):
        """Test successful query execution start."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "start_query_execution",
            {"QueryExecutionId": "test-query-id-123"},
            expected_params={
                "QueryString": "SELECT * FROM test_table",
                "WorkGroup": "test-workgroup",
            },
        )
        stubber.activate()
        result = wrapper.start_query_execution(
            query_string="SELECT * FROM test_table",
            work_group="test-workgroup",
        )
        assert result == "test-query-id-123"
        stubber.deactivate()

    def test_start_query_execution_with_database(self, athena_client, wrapper):
        """Test query execution start with database context."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "start_query_execution",
            {"QueryExecutionId": "test-query-id-456"},
            expected_params={
                "QueryString": "SELECT * FROM sales",
                "WorkGroup": "test-workgroup",
                "QueryExecutionContext": {"Database": "test_db"},
            },
        )
        stubber.activate()
        result = wrapper.start_query_execution(
            query_string="SELECT * FROM sales",
            work_group="test-workgroup",
            database="test_db",
        )
        assert result == "test-query-id-456"
        stubber.deactivate()

    def test_start_query_execution_internal_error(self, athena_client, wrapper):
        """Test query execution with InternalServerException."""
        stubber = Stubber(athena_client)
        stubber.add_client_error(
            "start_query_execution",
            service_error_code="InternalServerException",
            service_message="Internal server error",
        )
        stubber.activate()
        with pytest.raises(Exception) as exc_info:
            wrapper.start_query_execution(
                query_string="SELECT * FROM test_table",
                work_group="test-workgroup",
            )
        assert "InternalServerException" in str(
            exc_info.value.response["Error"]["Code"]
        )
        stubber.deactivate()


class TestGetQueryExecution:
    """Tests for get_query_execution method."""

    def test_get_query_execution(self, athena_client, wrapper):
        """Test successful query execution retrieval."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "get_query_execution",
            {
                "QueryExecution": {
                    "QueryExecutionId": "test-id-123",
                    "Query": "SELECT * FROM test",
                    "StatementType": "DML",
                    "Status": {
                        "State": "SUCCEEDED",
                        "SubmissionDateTime": datetime(2024, 1, 1, tzinfo=timezone.utc),
                        "CompletionDateTime": datetime(2024, 1, 1, tzinfo=timezone.utc),
                    },
                    "Statistics": {
                        "EngineExecutionTimeInMillis": 1500,
                        "DataScannedInBytes": 1024,
                    },
                    "WorkGroup": "test-workgroup",
                }
            },
            expected_params={"QueryExecutionId": "test-id-123"},
        )
        stubber.activate()
        result = wrapper.get_query_execution("test-id-123")
        assert result["QueryExecutionId"] == "test-id-123"
        assert result["Status"]["State"] == "SUCCEEDED"
        assert result["Statistics"]["DataScannedInBytes"] == 1024
        stubber.deactivate()


class TestGetQueryResults:
    """Tests for get_query_results method."""

    def test_get_query_results(self, athena_client, wrapper):
        """Test successful query results retrieval via paginator."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "get_query_results",
            {
                "ResultSet": {
                    "Rows": [
                        {
                            "Data": [
                                {"VarCharValue": "product_name"},
                                {"VarCharValue": "price"},
                            ]
                        },
                        {
                            "Data": [
                                {"VarCharValue": "Widget"},
                                {"VarCharValue": "9.99"},
                            ]
                        },
                    ],
                    "ResultSetMetadata": {
                        "ColumnInfo": [
                            {
                                "Name": "product_name",
                                "Type": "varchar",
                                "CatalogName": "hive",
                                "SchemaName": "",
                                "TableName": "",
                                "Label": "product_name",
                                "Precision": 0,
                                "Scale": 0,
                                "Nullable": "UNKNOWN",
                                "CaseSensitive": True,
                            },
                            {
                                "Name": "price",
                                "Type": "double",
                                "CatalogName": "hive",
                                "SchemaName": "",
                                "TableName": "",
                                "Label": "price",
                                "Precision": 0,
                                "Scale": 0,
                                "Nullable": "UNKNOWN",
                                "CaseSensitive": True,
                            },
                        ]
                    },
                },
                "UpdateCount": 0,
            },
            expected_params={"QueryExecutionId": "test-id-123"},
        )
        stubber.activate()
        result = wrapper.get_query_results("test-id-123")
        rows = result["ResultSet"]["Rows"]
        assert len(rows) == 2
        assert rows[0]["Data"][0]["VarCharValue"] == "product_name"
        assert rows[1]["Data"][0]["VarCharValue"] == "Widget"
        stubber.deactivate()

    def test_get_query_results_invalid_request(self, athena_client, wrapper):
        """Test query results with InvalidRequestException."""
        stubber = Stubber(athena_client)
        stubber.add_client_error(
            "get_query_results",
            service_error_code="InvalidRequestException",
            service_message="Query execution not found",
        )
        stubber.activate()
        with pytest.raises(Exception) as exc_info:
            wrapper.get_query_results("invalid-id")
        assert "InvalidRequestException" in str(
            exc_info.value.response["Error"]["Code"]
        )
        stubber.deactivate()


class TestListQueryExecutions:
    """Tests for list_query_executions method."""

    def test_list_query_executions(self, athena_client, wrapper):
        """Test successful listing of query executions via paginator."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "list_query_executions",
            {
                "QueryExecutionIds": [
                    "exec-id-1",
                    "exec-id-2",
                    "exec-id-3",
                ]
            },
            expected_params={"WorkGroup": "test-workgroup"},
        )
        stubber.activate()
        result = wrapper.list_query_executions("test-workgroup")
        assert len(result) == 3
        assert "exec-id-1" in result
        assert "exec-id-2" in result
        stubber.deactivate()


class TestCreateNamedQuery:
    """Tests for create_named_query method."""

    def test_create_named_query(self, athena_client, wrapper):
        """Test successful named query creation."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "create_named_query",
            {"NamedQueryId": "named-query-id-123"},
            expected_params={
                "Name": "Top selling products",
                "Description": "Returns top products",
                "Database": "test_db",
                "QueryString": "SELECT * FROM sales",
                "WorkGroup": "test-workgroup",
            },
        )
        stubber.activate()
        result = wrapper.create_named_query(
            name="Top selling products",
            description="Returns top products",
            database="test_db",
            query_string="SELECT * FROM sales",
            work_group="test-workgroup",
        )
        assert result == "named-query-id-123"
        stubber.deactivate()


class TestGetNamedQuery:
    """Tests for get_named_query method."""

    def test_get_named_query(self, athena_client, wrapper):
        """Test successful named query retrieval."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "get_named_query",
            {
                "NamedQuery": {
                    "Name": "Top selling products",
                    "Description": "Returns top products by revenue",
                    "Database": "test_db",
                    "QueryString": "SELECT product_name, SUM(quantity * price) FROM sales GROUP BY product_name",
                    "NamedQueryId": "named-query-id-123",
                    "WorkGroup": "test-workgroup",
                }
            },
            expected_params={"NamedQueryId": "named-query-id-123"},
        )
        stubber.activate()
        result = wrapper.get_named_query("named-query-id-123")
        assert result["Name"] == "Top selling products"
        assert result["Database"] == "test_db"
        assert "SELECT" in result["QueryString"]
        stubber.deactivate()


class TestListNamedQueries:
    """Tests for list_named_queries method."""

    def test_list_named_queries(self, athena_client, wrapper):
        """Test successful listing of named queries via paginator."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "list_named_queries",
            {
                "NamedQueryIds": [
                    "named-id-1",
                    "named-id-2",
                ]
            },
            expected_params={"WorkGroup": "test-workgroup"},
        )
        stubber.activate()
        result = wrapper.list_named_queries("test-workgroup")
        assert len(result) == 2
        assert "named-id-1" in result
        stubber.deactivate()


class TestDeleteWorkGroup:
    """Tests for delete_work_group method."""

    def test_delete_work_group(self, athena_client, wrapper):
        """Test successful workgroup deletion."""
        stubber = Stubber(athena_client)
        stubber.add_response(
            "delete_work_group",
            {},
            expected_params={
                "WorkGroup": "test-workgroup",
                "RecursiveDeleteOption": True,
            },
        )
        stubber.activate()
        wrapper.delete_work_group("test-workgroup", recursive=True)
        stubber.deactivate()

    def test_delete_work_group_invalid_request(self, athena_client, wrapper):
        """Test workgroup deletion with InvalidRequestException."""
        stubber = Stubber(athena_client)
        stubber.add_client_error(
            "delete_work_group",
            service_error_code="InvalidRequestException",
            service_message="Workgroup not found",
        )
        stubber.activate()
        with pytest.raises(Exception) as exc_info:
            wrapper.delete_work_group("nonexistent-workgroup")
        assert "InvalidRequestException" in str(
            exc_info.value.response["Error"]["Code"]
        )
        stubber.deactivate()
