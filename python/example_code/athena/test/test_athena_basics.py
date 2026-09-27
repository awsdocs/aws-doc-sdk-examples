# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Integration tests for the Amazon Athena Basics Scenario.

These tests exercise the AthenaWrapper and the scenario against live
AWS resources. No mocking of the Athena service is used.

Usage:
    pytest test_athena_basics.py -v
"""

import sys
import os

sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

import json
import logging
import time
import uuid

import boto3
import pytest
from botocore.exceptions import ClientError

from athena_wrapper import AthenaWrapper

logger = logging.getLogger(__name__)

DATABASE_NAME = "athena_integ_test_db"
TABLE_NAME = "test_movies"


@pytest.fixture(scope="module")
def test_resources():
    """
    Sets up shared test resources: a CloudFormation stack for an S3 bucket
    and an Athena workgroup. Tears down all resources after tests complete.
    """
    cf_client = boto3.client("cloudformation")
    s3_client = boto3.client("s3")
    athena_client = boto3.client("athena")
    wrapper = AthenaWrapper(athena_client)

    unique_suffix = uuid.uuid4().hex[:8]
    stack_name = f"athena-integ-test-{unique_suffix}"
    workgroup_name = f"athena-integ-wg-{unique_suffix}"
    bucket_name = ""
    named_query_id = ""

    # ---- Setup ----
    template_body = json.dumps(
        {
            "AWSTemplateFormatVersion": "2010-09-09",
            "Description": "S3 bucket for Athena integration tests.",
            "Resources": {
                "ResultsBucket": {
                    "Type": "AWS::S3::Bucket",
                    "Properties": {
                        "BucketName": f"{stack_name}-results",
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

    cf_client.create_stack(StackName=stack_name, TemplateBody=template_body)
    waiter = cf_client.get_waiter("stack_create_complete")
    waiter.wait(StackName=stack_name, WaiterConfig={"Delay": 10, "MaxAttempts": 60})

    response = cf_client.describe_stacks(StackName=stack_name)
    outputs = response["Stacks"][0].get("Outputs", list())
    for output in outputs:
        if output["OutputKey"] == "BucketName":
            bucket_name = output["OutputValue"]
            break

    output_location = f"s3://{bucket_name}/athena-results/"
    wrapper.create_work_group(name=workgroup_name, output_location=output_location)

    resources = {
        "wrapper": wrapper,
        "athena_client": athena_client,
        "cf_client": cf_client,
        "s3_client": s3_client,
        "stack_name": stack_name,
        "workgroup_name": workgroup_name,
        "bucket_name": bucket_name,
        "named_query_id": named_query_id,
    }

    yield resources

    # ---- Teardown ----
    try:
        if resources.get("named_query_id"):
            try:
                wrapper.delete_named_query(resources["named_query_id"])
            except ClientError:
                pass

        # Drop table and database.
        try:
            drop_table_id = wrapper.start_query_execution(
                query_string=f"DROP TABLE IF EXISTS {DATABASE_NAME}.{TABLE_NAME}",
                work_group=workgroup_name,
            )
            wrapper.wait_for_query_to_complete(drop_table_id)
        except (ClientError, RuntimeError):
            pass

        try:
            drop_db_id = wrapper.start_query_execution(
                query_string=f"DROP DATABASE IF EXISTS {DATABASE_NAME}",
                work_group=workgroup_name,
            )
            wrapper.wait_for_query_to_complete(drop_db_id)
        except (ClientError, RuntimeError):
            pass

        # Delete workgroup.
        try:
            wrapper.delete_work_group(name=workgroup_name, recursive=True)
        except ClientError:
            pass

        # Empty bucket and delete stack.
        try:
            paginator = s3_client.get_paginator("list_objects_v2")
            for page in paginator.paginate(Bucket=bucket_name):
                contents = page.get("Contents", list())
                if contents:
                    objects = [{"Key": obj["Key"]} for obj in contents]
                    s3_client.delete_objects(
                        Bucket=bucket_name, Delete={"Objects": objects}
                    )
        except ClientError:
            pass

        try:
            cf_client.delete_stack(StackName=stack_name)
            delete_waiter = cf_client.get_waiter("stack_delete_complete")
            delete_waiter.wait(
                StackName=stack_name,
                WaiterConfig={"Delay": 10, "MaxAttempts": 60},
            )
        except ClientError:
            pass
    except Exception as e:
        logger.error("Error during teardown: %s", e)


@pytest.mark.integ
class TestAthenaBasics:
    """Integration tests for Amazon Athena basics operations."""

    def test_get_work_group(self, test_resources):
        """Tests that GetWorkGroup returns valid workgroup details."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        wg = wrapper.get_work_group(wg_name)

        assert wg["Name"] == wg_name
        assert wg["State"] == "ENABLED"
        config = wg.get("Configuration", dict())
        assert config.get("EnforceWorkGroupConfiguration") is True
        assert config.get("PublishCloudWatchMetricsEnabled") is True

    def test_create_database(self, test_resources):
        """Tests creating a database via DDL query."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        query = f"CREATE DATABASE IF NOT EXISTS {DATABASE_NAME}"
        execution_id = wrapper.start_query_execution(
            query_string=query, work_group=wg_name
        )
        result = wrapper.wait_for_query_to_complete(execution_id)

        assert result["Status"]["State"] == "SUCCEEDED"

    def test_create_table_with_ctas(self, test_resources):
        """Tests creating a table using CTAS with inline data."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        query = f"""
        CREATE TABLE {DATABASE_NAME}.{TABLE_NAME} AS
        SELECT * FROM (
            VALUES
                (1, 'The Shawshank Redemption', 1994, 9.3),
                (2, 'The Godfather', 1972, 9.2),
                (3, 'The Dark Knight', 2008, 9.0)
        ) AS t(id, title, year, rating)
        """
        execution_id = wrapper.start_query_execution(
            query_string=query, work_group=wg_name, database=DATABASE_NAME
        )
        result = wrapper.wait_for_query_to_complete(execution_id)

        assert result["Status"]["State"] == "SUCCEEDED"

    def test_run_select_query_and_get_results(self, test_resources):
        """Tests running a SELECT query and retrieving results."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        query = (
            f"SELECT title, year, rating FROM {DATABASE_NAME}.{TABLE_NAME} "
            "WHERE rating >= 9.0 ORDER BY rating DESC"
        )
        execution_id = wrapper.start_query_execution(
            query_string=query, work_group=wg_name, database=DATABASE_NAME
        )
        wrapper.wait_for_query_to_complete(execution_id)

        results = wrapper.get_query_results(execution_id)

        assert "columns" in results
        assert "rows" in results
        assert len(results["columns"]) == 3
        assert len(results["rows"]) >= 1

    def test_create_named_query(self, test_resources):
        """Tests creating a named query."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        named_query_id = wrapper.create_named_query(
            name="integ-test-query",
            description="Integration test named query",
            database=DATABASE_NAME,
            query_string="SELECT * FROM movies LIMIT 5",
            work_group=wg_name,
        )
        test_resources["named_query_id"] = named_query_id

        assert named_query_id is not None
        assert len(named_query_id) > 0

    def test_list_named_queries(self, test_resources):
        """Tests listing named queries in the workgroup."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        named_query_ids = wrapper.list_named_queries(wg_name)

        assert isinstance(named_query_ids, list)
        # We created at least one named query in the previous test.
        assert len(named_query_ids) >= 1
        assert test_resources["named_query_id"] in named_query_ids

    def test_list_query_executions(self, test_resources):
        """Tests listing query executions in the workgroup."""
        wrapper = test_resources["wrapper"]
        wg_name = test_resources["workgroup_name"]

        execution_ids = wrapper.list_query_executions(wg_name)

        assert isinstance(execution_ids, list)
        # We ran several queries during this test session.
        assert len(execution_ids) >= 1

    def test_delete_named_query(self, test_resources):
        """Tests deleting a named query."""
        wrapper = test_resources["wrapper"]
        nq_id = test_resources.get("named_query_id")

        if nq_id:
            wrapper.delete_named_query(nq_id)
            # Clear so teardown doesn't try again.
            test_resources["named_query_id"] = ""

            # Verify the named query is no longer listed.
            remaining = wrapper.list_named_queries(
                test_resources["workgroup_name"]
            )
            assert nq_id not in remaining

    def test_hello_athena(self, test_resources):
        """Tests the Hello Athena example (ListWorkGroups)."""
        athena_client = test_resources["athena_client"]

        # list_work_groups does not support get_paginator in botocore,
        # so we paginate manually using NextToken.
        workgroups = list()
        next_token = None
        while True:
            kwargs = dict()
            if next_token is not None:
                kwargs["NextToken"] = next_token
            response = athena_client.list_work_groups(**kwargs)
            workgroups.extend(response.get("WorkGroups", list()))
            next_token = response.get("NextToken", None)
            if next_token is None:
                break

        # At minimum the 'primary' workgroup should exist.
        names = [wg["Name"] for wg in workgroups]
        assert "primary" in names
