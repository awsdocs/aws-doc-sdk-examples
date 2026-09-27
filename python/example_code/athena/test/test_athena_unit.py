# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Unit tests for the Amazon Athena wrapper (athena_wrapper.py) and the Hello
example (athena_hello.py).

These tests use the botocore Stubber (via the shared test_tools framework) to
mock AWS calls, so they run offline without live AWS resources or credentials.

Usage:
    python -m pytest test/test_athena_unit.py -v
"""

import boto3
from botocore.exceptions import ClientError
import pytest

from athena_wrapper import AthenaWrapper
import athena_hello


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_work_group(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    name = "test-wg"
    output_location = "s3://test-bucket/results/"

    athena_stubber.stub_create_work_group(name, output_location, error_code=error_code)

    if error_code is None:
        wrapper.create_work_group(name, output_location)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_work_group(name, output_location)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_work_group(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    name = "test-wg"
    work_group = {"Name": name, "State": "ENABLED"}

    athena_stubber.stub_get_work_group(name, work_group, error_code=error_code)

    if error_code is None:
        got = wrapper.get_work_group(name)
        assert got["Name"] == name
        assert got["State"] == "ENABLED"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_work_group(name)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
@pytest.mark.parametrize("database", [None, "test_db"])
def test_start_query_execution(make_stubber, database, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    query = "SELECT 1"
    work_group = "test-wg"
    execution_id = "exec-123"

    athena_stubber.stub_start_query_execution(
        query, work_group, execution_id, database=database, error_code=error_code
    )

    if error_code is None:
        got = wrapper.start_query_execution(query, work_group, database=database)
        assert got == execution_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.start_query_execution(query, work_group, database=database)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_query_execution(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    execution_id = "exec-123"

    athena_stubber.stub_get_query_execution(
        execution_id, "SUCCEEDED", error_code=error_code
    )

    if error_code is None:
        got = wrapper.get_query_execution(execution_id)
        assert got["Status"]["State"] == "SUCCEEDED"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_query_execution(execution_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


def test_wait_for_query_to_complete_success(make_stubber):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    execution_id = "exec-123"

    # First poll RUNNING, second poll SUCCEEDED.
    athena_stubber.stub_get_query_execution(execution_id, "RUNNING")
    athena_stubber.stub_get_query_execution(execution_id, "SUCCEEDED")

    got = wrapper.wait_for_query_to_complete(execution_id, max_wait_seconds=30)
    assert got["Status"]["State"] == "SUCCEEDED"


def test_wait_for_query_to_complete_failed(make_stubber):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    execution_id = "exec-123"

    athena_stubber.stub_get_query_execution(execution_id, "FAILED")

    with pytest.raises(RuntimeError):
        wrapper.wait_for_query_to_complete(execution_id, max_wait_seconds=30)


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_query_results(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    execution_id = "exec-123"
    columns = ["title", "rating"]
    # First row is the Athena header row; remaining rows are data.
    rows = [
        ["title", "rating"],
        ["The Godfather", "9.2"],
        ["The Dark Knight", "9.0"],
    ]

    athena_stubber.stub_get_query_results(
        execution_id, columns, rows, error_code=error_code
    )

    if error_code is None:
        got = wrapper.get_query_results(execution_id)
        assert got["columns"] == columns
        # Header row must be stripped, leaving exactly the two data rows.
        assert got["rows"] == [
            ["The Godfather", "9.2"],
            ["The Dark Knight", "9.0"],
        ]
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_query_results(execution_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_query_executions(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    work_group = "test-wg"
    execution_ids = ["exec-1", "exec-2"]

    athena_stubber.stub_list_query_executions(
        work_group, execution_ids, error_code=error_code
    )

    if error_code is None:
        got = wrapper.list_query_executions(work_group)
        assert got == execution_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_query_executions(work_group)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_create_named_query(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    named_query_id = "nq-123"

    athena_stubber.stub_create_named_query(
        "q-name",
        "desc",
        "test_db",
        "SELECT 1",
        "test-wg",
        named_query_id,
        error_code=error_code,
    )

    if error_code is None:
        got = wrapper.create_named_query(
            name="q-name",
            description="desc",
            database="test_db",
            query_string="SELECT 1",
            work_group="test-wg",
        )
        assert got == named_query_id
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.create_named_query(
                name="q-name",
                description="desc",
                database="test_db",
                query_string="SELECT 1",
                work_group="test-wg",
            )
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_get_named_query(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    named_query_id = "nq-123"
    named_query = {
        "NamedQueryId": named_query_id,
        "Name": "q-name",
        "Database": "test_db",
        "QueryString": "SELECT 1",
    }

    athena_stubber.stub_get_named_query(
        named_query_id, named_query, error_code=error_code
    )

    if error_code is None:
        got = wrapper.get_named_query(named_query_id)
        assert got["QueryString"] == "SELECT 1"
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.get_named_query(named_query_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_list_named_queries(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    work_group = "test-wg"
    named_query_ids = ["nq-1", "nq-2"]

    athena_stubber.stub_list_named_queries(
        work_group, named_query_ids, error_code=error_code
    )

    if error_code is None:
        got = wrapper.list_named_queries(work_group)
        assert got == named_query_ids
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.list_named_queries(work_group)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_named_query(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    named_query_id = "nq-123"

    athena_stubber.stub_delete_named_query(named_query_id, error_code=error_code)

    if error_code is None:
        wrapper.delete_named_query(named_query_id)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_named_query(named_query_id)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_delete_work_group(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    wrapper = AthenaWrapper(athena_client)
    name = "test-wg"

    athena_stubber.stub_delete_work_group(name, recursive=True, error_code=error_code)

    if error_code is None:
        wrapper.delete_work_group(name, recursive=True)
    else:
        with pytest.raises(ClientError) as exc_info:
            wrapper.delete_work_group(name, recursive=True)
        assert exc_info.value.response["Error"]["Code"] == error_code


@pytest.mark.parametrize("error_code", [None, "TestException"])
def test_hello_athena(make_stubber, error_code):
    athena_client = boto3.client("athena", region_name="us-east-1")
    athena_stubber = make_stubber(athena_client)
    work_groups = [
        {"Name": "primary", "State": "ENABLED"},
        {"Name": "test-wg", "State": "ENABLED"},
    ]

    athena_stubber.stub_list_work_groups(work_groups, error_code=error_code)

    if error_code is None:
        athena_hello.hello_athena(athena_client)
    else:
        with pytest.raises(ClientError) as exc_info:
            athena_hello.hello_athena(athena_client)
        assert exc_info.value.response["Error"]["Code"] == error_code
