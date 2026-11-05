# Amazon SES v2 Search and Filter Feature Scenario for the SDK for Python (boto3)

## Overview

This example demonstrates how to use the AWS SDK for Python (boto3) to filter the results of Amazon Simple Email Service (Amazon SES) API v2 list operations on the server side. The scenario creates sample configuration sets, email identities, and tenants, lists each resource type with and without a filter, pages through filtered results, and then deletes the resources it created.

The `ListConfigurationSets`, `ListEmailIdentities`, and `ListTenants` operations accept a `Filter` parameter that returns only the resources whose names contain a given substring. This is useful in accounts with many resources, where retrieving everything and filtering on the client is slow.

## ⚠ Important

- Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).

- Running the tests might result in charges to your AWS account.

- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).

- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Code examples

### Prerequisites

For general setup instructions, including how to install Python, create a virtual environment, and configure your AWS credentials, see [Run the examples](../../../README.md#run-the-examples) in the `python` folder README.

Install the packages required by this example by running the following in a virtual environment, from this directory:

```
python -m pip install -r ../requirements.txt
```

This example requires boto3 and botocore version 1.43.105 or later, which add the `Filter` parameter to the SES v2 list operations.

### Scenario

This example uses a feature scenario to demonstrate filtered list operations. The scenario is divided into these stages:

1. **Setup**: Create sample configuration sets, email identities, and tenants, then wait until the new resources appear in filtered results.

2. **Configuration sets**: List all configuration sets, then list only the configuration sets whose names contain `marketing`.

3. **Email identities**: List all email identities, then list only the identities whose names contain `marketing`.

4. **Tenants**: List all tenants, then list only the tenants whose names contain `customer`.

5. **Pagination**: Page through the filtered configuration sets one result at a time by using `PageSize` and `NextToken`.

6. **Clean up**: Delete the configuration sets, email identities, and tenants that the scenario created.

> **Note:** Filter indexing is eventually consistent. A new resource can take several seconds to appear in filtered results, even though an unfiltered list returns it right away. The setup stage repeats the filtered calls until the new resources appear.

> **Note:** If a resource with one of the scenario's names already exists in your account, the scenario uses it as is and doesn't delete it during cleanup. Existing resources whose names match a filter value also appear in the filtered results.

#### Running the scenario

To run this feature scenario, run the command below from this directory:

```
python scenario_sesv2_search_filter_basics.py
```

## Tests

⚠ Running the integration tests might result in charges to your AWS account.

To run the unit tests for this scenario, which use stubbed responses and don't call AWS, run the following command from this directory:

```
python -m pytest -m "not integ"
```

To run the integration tests for this scenario, run the following command from this directory:

```
python -m pytest -m integ
```

## Additional resources

- [Amazon SES v2 Developer Guide](https://docs.aws.amazon.com/ses/latest/dg/Welcome.html)

- [Amazon SES v2 API Reference](https://docs.aws.amazon.com/ses/latest/APIReference-V2/Welcome.html)

- [boto3 Amazon SES v2 reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/sesv2.html)

---

© Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
