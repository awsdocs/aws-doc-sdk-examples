# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Stub functions that are used by the AWS Inspector unit tests.

When tests are run against an actual AWS account, the stubber class does not
set up stubs and passes all calls through to the Boto 3 client.
"""

from botocore.stub import ANY
from boto3 import client

from test_tools.example_stubber import ExampleStubber


class InspectorStubber(ExampleStubber):
    """
    A class that implements stub functions used by AWS Inspector unit tests.

    The stubbed functions expect certain parameters to be passed to them as
    part of the tests, and will raise errors when the actual parameters differ from
    the expected.
    """

    def __init__(self, inspector_client: client, use_stubs=True) -> None:
        """
        Initializes the object with a specific client and configures it for
        stubbing or AWS passthrough.

        :param inspector_client: A Boto 3 AWS Inspector client.
        :param use_stubs: When True, use stubs to intercept requests. Otherwise,
                          pass requests through to AWS.
        """
        super().__init__(inspector_client, use_stubs)

    def stub_enable(self, account_ids=None, resource_types=None, error_code=None):
        """
        Stub the Enable operation.

        :param account_ids: List of account IDs to enable Inspector for.
        :param resource_types: List of resource types to enable scanning for.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {}
        if account_ids is not None:
            expected_params["accountIds"] = account_ids
        if resource_types is not None:
            expected_params["resourceTypes"] = resource_types

        response = {
            "accounts": [
                {
                    "accountId": "123456789012",
                    "status": "ENABLED",
                    "resourceStatus": {
                        "ec2": "ENABLED",
                        "ecr": "ENABLED", 
                        "lambda": "ENABLED"
                    }
                }
            ]
        }
        self._stub_bifurcator(
            "enable", expected_params, response, error_code=error_code
        )

    def stub_batch_get_account_status(self, account_ids=None, error_code=None):
        """
        Stub the BatchGetAccountStatus operation.

        :param account_ids: List of account IDs to get status for.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {}
        if account_ids is not None:
            expected_params["accountIds"] = account_ids

        response = {
            "accounts": [
                {
                    "accountId": "123456789012",
                    "state": {
                        "status": "ENABLED",
                        "errorCode": "ALREADY_ENABLED",
                        "errorMessage": "Inspector is already enabled for this account"
                    },
                    "resourceState": {
                        "ec2": {
                            "status": "ENABLED",
                            "errorCode": "ALREADY_ENABLED",
                            "errorMessage": "EC2 scanning is already enabled"
                        },
                        "ecr": {
                            "status": "ENABLED", 
                            "errorCode": "ALREADY_ENABLED",
                            "errorMessage": "ECR scanning is already enabled"
                        },
                        "lambda": {
                            "status": "ENABLED",
                            "errorCode": "ALREADY_ENABLED", 
                            "errorMessage": "Lambda scanning is already enabled"
                        }
                    }
                }
            ]
        }
        self._stub_bifurcator(
            "batch_get_account_status", expected_params, response, error_code=error_code
        )

    def stub_list_findings(self, filter_criteria=None, max_results=None, next_token=None, sort_criteria=None, error_code=None):
        """
        Stub the ListFindings operation.

        :param filter_criteria: Filter criteria for findings.
        :param max_results: Maximum number of results to return.
        :param next_token: Token for pagination.
        :param sort_criteria: Sort criteria for findings.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {}
        if filter_criteria is not None:
            expected_params["filterCriteria"] = filter_criteria
        if max_results is not None:
            expected_params["maxResults"] = max_results
        if next_token is not None:
            expected_params["nextToken"] = next_token
        if sort_criteria is not None:
            expected_params["sortCriteria"] = sort_criteria

        response = {
            "findings": [
                {
                    "findingArn": "arn:aws:inspector2:us-east-1:123456789012:finding/finding-1",
                    "awsAccountId": "123456789012",
                    "type": "PACKAGE_VULNERABILITY",
                    "description": "CVE-2023-1234 - Critical vulnerability in package xyz",
                    "severity": "CRITICAL",
                    "firstObservedAt": "2023-01-01T00:00:00.000Z",
                    "lastObservedAt": "2023-01-01T00:00:00.000Z",
                    "updatedAt": "2023-01-01T00:00:00.000Z",
                    "status": "ACTIVE",
                    "title": "CVE-2023-1234 found in package xyz",
                    "inspectorScore": 9.8,
                    "remediation": {
                        "recommendation": {
                            "text": "Update package xyz to version 1.0.1 or later"
                        }
                    },
                    "inspectorScoreDetails": {
                        "adjustedCvss": {
                            "score": 9.8,
                            "scoreSource": "NVD",
                            "scoringVector": "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
                            "version": "3.1"
                        }
                    },
                    "networkReachabilityDetails": {
                        "networkPath": {
                            "steps": [
                                {
                                    "componentId": "i-1234567890abcdef0",
                                    "componentType": "AWS_EC2_INSTANCE"
                                }
                            ]
                        },
                        "openPortRange": {
                            "begin": 80,
                            "end": 80
                        },
                        "protocol": "TCP"
                    },
                    "packageVulnerabilityDetails": {
                        "cvss": [
                            {
                                "baseScore": 9.8,
                                "scoringVector": "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:U/C:H/I:H/A:H",
                                "source": "NVD",
                                "version": "3.1"
                            }
                        ],
                        "source": "NVD",
                        "sourceUrl": "https://nvd.nist.gov/vuln/detail/CVE-2023-1234",
                        "vendorCreatedAt": "2023-01-01T00:00:00.000Z",
                        "vendorSeverity": "CRITICAL",
                        "vulnerabilityId": "CVE-2023-1234",
                        "vulnerablePackages": [
                            {
                                "name": "xyz",
                                "version": "1.0.0",
                                "packageManager": "OS",
                                "fixedInVersion": "1.0.1"
                            }
                        ]
                    },
                    "resources": [
                        {
                            "id": "i-1234567890abcdef0",
                            "type": "AWS_EC2_INSTANCE",
                            "region": "us-east-1",
                            "partition": "aws",
                            "details": {
                                "awsEc2Instance": {
                                    "iamInstanceProfileArn": "arn:aws:iam::123456789012:instance-profile/EC2-Inspector-Role",
                                    "imageId": "ami-12345678",
                                    "ipV4Addresses": ["10.0.0.1"],
                                    "ipV6Addresses": [],
                                    "keyName": "my-key-pair",
                                    "launchedAt": "2023-01-01T00:00:00.000Z",
                                    "platform": "LINUX",
                                    "subnetId": "subnet-12345678",
                                    "type": "t3.micro",
                                    "vpcId": "vpc-12345678"
                                }
                            }
                        }
                    ]
                }
            ]
        }
        self._stub_bifurcator(
            "list_findings", expected_params, response, error_code=error_code
        )

    def stub_batch_get_finding_details(self, finding_arns, error_code=None):
        """
        Stub the BatchGetFindingDetails operation.

        :param finding_arns: List of finding ARNs to get details for.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {"findingArns": finding_arns}

        response = {
            "findingDetails": [
                {
                    "findingArn": "arn:aws:inspector2:us-east-1:123456789012:finding/finding-1",
                    "cisaData": {
                        "action": "Apply updates per vendor instructions",
                        "dateAdded": "2023-01-01T00:00:00.000Z"
                    },
                    "cwes": ["CWE-79", "CWE-89"],
                    "epssScore": 0.95,
                    "evidences": [
                        {
                            "evidenceDetail": "Package xyz version 1.0.0 is vulnerable",
                            "evidenceRule": "INSPECTOR_PACKAGE_VULNERABILITY",
                            "severity": "HIGH"
                        }
                    ],
                    "exploitObserved": {
                        "firstSeen": "2023-01-01T00:00:00.000Z",
                        "lastSeen": "2023-01-01T00:00:00.000Z"
                    },
                    "referenceUrls": [
                        "https://nvd.nist.gov/vuln/detail/CVE-2023-1234",
                        "https://example.com/security-advisory"
                    ],
                    "riskScore": 8,
                    "tools": ["INSPECTOR"],
                    "ttps": ["T1190", "T1203"]
                }
            ]
        }
        self._stub_bifurcator(
            "batch_get_finding_details", expected_params, response, error_code=error_code
        )

    def stub_list_coverage(self, filter_criteria=None, max_results=None, next_token=None, error_code=None):
        """
        Stub the ListCoverage operation.

        :param filter_criteria: Filter criteria for coverage.
        :param max_results: Maximum number of results to return.
        :param next_token: Token for pagination.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {}
        if filter_criteria is not None:
            expected_params["filterCriteria"] = filter_criteria
        if max_results is not None:
            expected_params["maxResults"] = max_results
        if next_token is not None:
            expected_params["nextToken"] = next_token

        response = {
            "coveredResources": [
                {
                    "accountId": "123456789012",
                    "resourceId": "i-1234567890abcdef0",
                    "resourceType": "AWS_EC2_INSTANCE",
                    "scanType": "PACKAGE",
                    "scanStatus": {
                        "statusCode": "ACTIVE",
                        "reason": "SUCCESSFUL"
                    },
                    "resourceMetadata": {
                        "ec2": {
                            "amiId": "ami-12345678",
                            "platform": "LINUX",
                            "tags": {
                                "Name": "test-instance",
                                "Environment": "development"
                            }
                        }
                    },
                    "lastScannedAt": "2023-01-01T00:00:00.000Z"
                },
                {
                    "accountId": "123456789012",
                    "resourceId": "123456789012.dkr.ecr.us-east-1.amazonaws.com/my-repo:latest",
                    "resourceType": "AWS_ECR_CONTAINER_IMAGE",
                    "scanType": "PACKAGE",
                    "scanStatus": {
                        "statusCode": "ACTIVE",
                        "reason": "SUCCESSFUL"
                    },
                    "resourceMetadata": {
                        "ecrRepository": {
                            "name": "my-repo",
                            "scanFrequency": "SCAN_ON_PUSH"
                        }
                    },
                    "lastScannedAt": "2023-01-01T00:00:00.000Z"
                }
            ]
        }
        self._stub_bifurcator(
            "list_coverage", expected_params, response, error_code=error_code
        )

    def stub_disable(self, account_ids=None, resource_types=None, error_code=None):
        """
        Stub the Disable operation.

        :param account_ids: List of account IDs to disable Inspector for.
        :param resource_types: List of resource types to disable scanning for.
        :param error_code: Error code to simulate an error response.
        """
        expected_params = {}
        if account_ids is not None:
            expected_params["accountIds"] = account_ids
        if resource_types is not None:
            expected_params["resourceTypes"] = resource_types

        response = {
            "accounts": [
                {
                    "accountId": "123456789012",
                    "status": "DISABLED",
                    "resourceStatus": {
                        "ec2": "DISABLED",
                        "ecr": "DISABLED",
                        "lambda": "DISABLED"
                    }
                }
            ]
        }
        self._stub_bifurcator(
            "disable", expected_params, response, error_code=error_code
        )