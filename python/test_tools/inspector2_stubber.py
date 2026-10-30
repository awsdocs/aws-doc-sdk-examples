# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the Amazon Inspector (Inspector2) unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from datetime import datetime
from typing import Any, Optional

from botocore.client import BaseClient

from test_tools.example_stubber import ExampleStubber


def _state(status: str) -> dict[str, str]:
    """
    Builds an Inspector2 State shape.

    The service model marks status, errorCode, and errorMessage as required
    (errorMessage must be non-empty), so each status maps to the error code and
    message that the service reports for that state.
    """
    error_code, error_message = _STATE_DETAILS.get(
        status, ("INTERNAL_ERROR", f"Unexpected status {status}.")
    )
    return {"status": status, "errorCode": error_code, "errorMessage": error_message}


_STATE_DETAILS = {
    "ENABLED": ("ALREADY_ENABLED", "Inspector is enabled."),
    "ENABLING": ("ENABLE_IN_PROGRESS", "Inspector is being enabled."),
    "DISABLING": ("DISABLE_IN_PROGRESS", "Inspector is being disabled."),
    "DISABLED": ("RESOURCE_NOT_FOUND", "Inspector is not enabled."),
    "SUSPENDING": ("SUSPEND_IN_PROGRESS", "Inspector is being suspended."),
    "SUSPENDED": ("RESOURCE_NOT_FOUND", "Inspector is suspended."),
}


def _low_severity_criteria() -> dict[str, Any]:
    return {"severity": [{"comparison": "EQUALS", "value": "LOW"}]}


class Inspector2Stubber(ExampleStubber):
    """
    A class that implements stub functions used by Amazon Inspector unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and raise errors when the actual parameters differ from
    the expected.

    The wrapper calls list operations through paginators with
    PaginationConfig={"PageSize": n}. Botocore translates PageSize into the
    operation's maxResults parameter before sending the request, so the list
    stubs expect {"maxResults": n}.
    """

    def __init__(self, inspector2_client: BaseClient, use_stubs: bool = True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param inspector2_client: A Boto 3 Amazon Inspector2 client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(inspector2_client, use_stubs)

    def stub_batch_get_account_status(
        self,
        account_id: str,
        status: str,
        resource_states: Optional[dict[str, str]] = None,
        error_code: Optional[str] = None,
    ) -> None:
        """
        :param resource_states: Status per resourceState key (ec2, ecr, lambda,
                                lambdaCode). Defaults to `status` for ec2 and ecr.
        """
        if resource_states is None:
            resource_states = {"ec2": status, "ecr": status}
        expected_params = dict()
        response = {
            "accounts": [
                {
                    "accountId": account_id,
                    "state": _state(status),
                    "resourceState": {
                        key: _state(value) for key, value in resource_states.items()
                    },
                }
            ]
        }
        self._stub_bifurcator(
            "batch_get_account_status",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_enable(
        self,
        resource_types: list[str],
        account_id: str,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"resourceTypes": resource_types}
        response = {
            "accounts": [
                {
                    "accountId": account_id,
                    "status": "ENABLING",
                    "resourceStatus": {"ec2": "ENABLING", "ecr": "ENABLING"},
                }
            ]
        }
        self._stub_bifurcator(
            "enable", expected_params, response, error_code=error_code
        )

    def stub_disable(
        self,
        resource_types: list[str],
        account_id: str,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"resourceTypes": resource_types}
        response = {
            "accounts": [
                {
                    "accountId": account_id,
                    "status": "DISABLING",
                    "resourceStatus": {"ec2": "DISABLING", "ecr": "DISABLING"},
                }
            ]
        }
        self._stub_bifurcator(
            "disable", expected_params, response, error_code=error_code
        )

    def stub_list_members(
        self,
        members: list[dict[str, str]],
        max_results: int = 50,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"maxResults": max_results}
        response = {"members": members}
        self._stub_bifurcator(
            "list_members", expected_params, response, error_code=error_code
        )

    def stub_list_coverage(
        self,
        covered_resources: list[dict[str, Any]],
        max_results: int = 5,
        next_token: Optional[str] = None,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"maxResults": max_results}
        response = {"coveredResources": covered_resources}
        if next_token is not None:
            response["nextToken"] = next_token
        self._stub_bifurcator(
            "list_coverage", expected_params, response, error_code=error_code
        )

    def stub_list_coverage_statistics(
        self,
        total_counts: int,
        counts_by_group: list[dict[str, Any]],
        group_by: Optional[str] = None,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = dict()
        if group_by is not None:
            expected_params["groupBy"] = group_by
        response = {"totalCounts": total_counts}
        # countsByGroup has a minimum length of 1, so omit it when empty.
        if counts_by_group:
            response["countsByGroup"] = counts_by_group
        self._stub_bifurcator(
            "list_coverage_statistics",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_list_findings(
        self,
        findings: list[dict[str, Any]],
        max_results: int = 10,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"maxResults": max_results}
        response = {"findings": findings}
        self._stub_bifurcator(
            "list_findings", expected_params, response, error_code=error_code
        )

    def stub_batch_get_finding_details(
        self,
        finding_arns: list[str],
        finding_details: list[dict[str, Any]],
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"findingArns": finding_arns}
        response = {"findingDetails": finding_details, "errors": []}
        self._stub_bifurcator(
            "batch_get_finding_details",
            expected_params,
            response,
            error_code=error_code,
        )

    def stub_create_filter(
        self,
        name: str,
        description: str,
        filter_arn: str,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {
            "name": name,
            "description": description,
            "action": "SUPPRESS",
            "filterCriteria": _low_severity_criteria(),
        }
        response = {"arn": filter_arn}
        self._stub_bifurcator(
            "create_filter", expected_params, response, error_code=error_code
        )

    def stub_list_filters(
        self,
        filters: list[dict[str, str]],
        max_results: int = 10,
        next_token: Optional[str] = None,
        error_code: Optional[str] = None,
    ) -> None:
        """
        :param filters: A list of dicts with 'arn' and 'name' keys. The remaining
                        required Filter members are filled in.
        """
        expected_params = {"maxResults": max_results}
        response = {
            "filters": [
                {
                    "arn": f["arn"],
                    "name": f["name"],
                    "ownerId": "123456789012",
                    "criteria": _low_severity_criteria(),
                    "action": "SUPPRESS",
                    "createdAt": datetime(2026, 1, 1),
                    "updatedAt": datetime(2026, 1, 1),
                }
                for f in filters
            ]
        }
        if next_token is not None:
            response["nextToken"] = next_token
        self._stub_bifurcator(
            "list_filters", expected_params, response, error_code=error_code
        )

    def stub_delete_filter(
        self, filter_arn: str, error_code: Optional[str] = None
    ) -> None:
        expected_params = {"arn": filter_arn}
        response = {"arn": filter_arn}
        self._stub_bifurcator(
            "delete_filter", expected_params, response, error_code=error_code
        )

    def stub_list_usage_totals(
        self,
        totals: list[dict[str, Any]],
        max_results: int = 10,
        next_token: Optional[str] = None,
        error_code: Optional[str] = None,
    ) -> None:
        expected_params = {"maxResults": max_results}
        response = {"totals": totals}
        if next_token is not None:
            response["nextToken"] = next_token
        self._stub_bifurcator(
            "list_usage_totals", expected_params, response, error_code=error_code
        )
