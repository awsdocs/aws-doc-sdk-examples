# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Systems Manager unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto3 client.
"""

from datetime import datetime

from botocore.stub import ANY
from test_tools.example_stubber import ExampleStubber


class SsmStubber(ExampleStubber):
    """
    A class that implements a variety of stub functions that are used by the
    AWS Systems Manager unit tests.

    The stubbed functions all expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, client, use_stubs=True):
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param client: A Boto3 Systems Manager client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(client, use_stubs)

    def stub_send_command(
        self,
        instance_ids,
        commands=None,
        document_name="AWS-RunShellScript",
        command_id=None,
        timeout=3600,
        error_code=None,
    ):
        expected_parameters = {
            "InstanceIds": instance_ids,
            "DocumentName": document_name,
        }
        if commands:
            expected_parameters["Parameters"] = {"commands": commands}

        if timeout is not None:
            expected_parameters["TimeoutSeconds"] = timeout

        response = {}
        if command_id is not None:
            response["Command"] = {"CommandId": command_id}
        self._stub_bifurcator(
            "send_command", expected_parameters, response, error_code=error_code
        )

    def stub_list_command_invocations(
        self, command_id=None, instance_id=None, status_details=None, error_code=None
    ):
        expected_parameters = {}
        if instance_id is not None:
            expected_parameters["InstanceId"] = instance_id
        if command_id is not None:
            expected_parameters["CommandId"] = command_id

        command_response = {"RequestedDateTime": datetime.now()}

        if status_details is not None:
            command_response["StatusDetails"] = status_details

        if command_id is not None:
            command_response["CommandId"] = command_id

        if instance_id is not None:
            command_response["InstanceId"] = instance_id

        response = {"CommandInvocations": [command_response]}
        self._stub_bifurcator(
            "list_command_invocations",
            expected_parameters,
            response,
            error_code=error_code,
        )

    def stub_get_parameters_by_path(self, names, values, path=ANY, error_code=None):
        expected_params = {"Path": path}
        response = {
            "Parameters": [
                {"Name": name, "Value": value} for name, value in zip(names, values)
            ]
        }
        self._stub_bifurcator(
            "get_parameters_by_path", expected_params, response, error_code=error_code
        )

    def stub_get_parameter(self, name, value, error_code=None):
        expected_params = {"Name": name}
        response = {"Parameter": {"Value": value}}
        self._stub_bifurcator(
            "get_parameter", expected_params, response, error_code=error_code
        )

    def stub_put_parameter(self, name, value, error_code=None):
        expected_params = {"Name": name, "Value": value, "Overwrite": True}
        response = {}
        self._stub_bifurcator(
            "put_parameter", expected_params, response, error_code=error_code
        )

    def stub_create_document(self, content, name, error_code=None):
        expected_params = {"Name": name, "Content": content, "DocumentType": "Command"}
        response = {}
        self._stub_bifurcator(
            "create_document",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_delete_document(self, name, error_code=None):
        expected_params = {"Name": name}
        response = {}
        self._stub_bifurcator(
            "delete_document", expected_params, response, error_code=error_code
        )

    def stub_describe_document(self, name, error_code=None):
        expected_params = {"Name": name}

        response = {
            "Document": {
                "Status": "Active",
            }
        }

        self._stub_bifurcator(
            "describe_document", expected_params, response, error_code=error_code
        )

    def stub_create_maintenance_window(
        self,
        name,
        window_id,
        allow_unassociated_targets=False,
        cutoff=2,
        duration=2,
        schedule="cron(0 0 ? * MON *)",
        error_code=None,
    ):
        expected_params = {
            "Name": name,
            "AllowUnassociatedTargets": allow_unassociated_targets,
            "Cutoff": cutoff,
            "Duration": duration,
            "Schedule": schedule,
        }

        response = {"WindowId": window_id}

        self._stub_bifurcator(
            "create_maintenance_window",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_delete_maintenance_window(self, window_id, error_code=None):
        expected_params = {"WindowId": window_id}

        response = {}

        self._stub_bifurcator(
            "delete_maintenance_window",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_update_maintenance_window(
        self,
        window_id,
        name=None,
        allow_unassociated_targets=None,
        cutoff=None,
        duration=None,
        enabled=None,
        schedule=None,
        error_code=None,
    ):
        expected_params = {"WindowId": window_id}

        if name is not None:
            expected_params["Name"] = name
        if allow_unassociated_targets is not None:
            expected_params["AllowUnassociatedTargets"] = allow_unassociated_targets
        if cutoff is not None:
            expected_params["Cutoff"] = cutoff
        if duration is not None:
            expected_params["Duration"] = duration
        if enabled is not None:
            expected_params["Enabled"] = enabled
        if schedule is not None:
            expected_params["Schedule"] = schedule

        response = {"WindowId": window_id}

        self._stub_bifurcator(
            "update_maintenance_window",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_ops_item(
        self,
        title,
        source,
        category,
        severity,
        description,
        ops_item_id,
        error_code=None,
    ):
        expected_params = {
            "Title": title,
            "Source": source,
            "Category": category,
            "Severity": severity,
            "Description": description,
        }

        response = {"OpsItemId": ops_item_id}

        self._stub_bifurcator(
            "create_ops_item", expected_params, response, error_code=error_code
        )

    def stub_delete_ops_item(self, ops_item_id, error_code=None):
        expected_params = {"OpsItemId": ops_item_id}

        response = {}

        self._stub_bifurcator(
            "delete_ops_item", expected_params, response, error_code=error_code
        )

    def stub_describe_ops_items(self, filters, error_code=None):
        expected_params = {"OpsItemFilters": filters}

        response = {
            "OpsItemSummaries": [
                {
                    "OpsItemId": "oi-0123456789abcdef0",
                    "Title": "Test OpsItem",
                    "Source": "test-source",
                    "CreatedBy": "test-user",
                    "CreatedTime": datetime.now(),
                    "LastModifiedTime": datetime.now(),
                    "Status": "Open",
                    "Severity": "High",
                    "Category": "Availability",
                }
            ]
        }

        self._stub_bifurcator(
            "describe_ops_items", expected_params, response, error_code=error_code
        )

    def stub_update_ops_item(
        self,
        ops_item_id,
        title=None,
        description=None,
        source=None,
        severity=None,
        status=None,
        category=None,
        notifications=None,
        error_code=None,
    ):
        expected_params = {"OpsItemId": ops_item_id}

        if title is not None:
            expected_params["Title"] = title
        if description is not None:
            expected_params["Description"] = description
        if source is not None:
            expected_params["Source"] = source
        if severity is not None:
            expected_params["Severity"] = severity
        if status is not None:
            expected_params["Status"] = status
        if category is not None:
            expected_params["Category"] = category
        if notifications is not None:
            expected_params["Notifications"] = notifications

        response = {}

        self._stub_bifurcator(
            "update_ops_item", expected_params, response, error_code=error_code
        )
