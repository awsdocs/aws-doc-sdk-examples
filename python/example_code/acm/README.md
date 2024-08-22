# ACM code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Certificate Manager (ACM).

<!--custom.overview.start-->
<!--custom.overview.end-->

_ACM helps you to provision, manage, and renew publicly trusted TLS certificates on AWS based websites._

## ⚠ Important

* Running this code might result in charges to your AWS account. For more details, see [AWS Pricing](https://aws.amazon.com/pricing/) and [Free Tier](https://aws.amazon.com/free/).
* Running the tests might result in charges to your AWS account.
* We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
* This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

<!--custom.important.start-->
<!--custom.important.end-->

## Code examples

### Prerequisites

For prerequisites, see the [README](../../README.md#Prerequisites) in the `python` folder.

Install the packages required by these examples by running the following in a virtual environment:

```
python -m pip install -r requirements.txt
```

<!--custom.prerequisites.start-->
<!--custom.prerequisites.end-->

### Basics

Code examples that show you how to perform the essential operations within a service.

- [Learn the basics](certificate_basics.py)


### Single actions

Code excerpts that show you how to call individual service functions.

- [AddTagsToCertificate](certificate_basics.py#L168)
- [DeleteCertificate](certificate_basics.py#L152)
- [DescribeCertificate](certificate_basics.py#L37)
- [GetCertificate](certificate_basics.py#L61)
- [ImportCertificate](certificate_basics.py#L128)
- [ListCertificates](certificate_basics.py#L80)
- [ListTagsForCertificate](certificate_basics.py#L189)
- [RemoveTagsFromCertificate](certificate_basics.py#L211)
- [RequestCertificate](certificate_basics.py#L242)
- [ResendValidationEmail](certificate_basics.py#L293)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->


#### Learn the basics

This example shows you how to do the following:

- Request a certificate from ACM.
- Import a self-signed certificate.
- List and describe certificates.
- Remove certificates.

<!--custom.basic_prereqs.acm_Usage_ImportListRemove.start-->
<!--custom.basic_prereqs.acm_Usage_ImportListRemove.end-->

Start the example by running the following at a command prompt:

```
python certificate_basics.py
```


<!--custom.basics.acm_Usage_ImportListRemove.start-->
<!--custom.basics.acm_Usage_ImportListRemove.end-->


### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [ACM User Guide](https://docs.aws.amazon.com/acm/latest/userguide/acm-overview.html)
- [ACM API Reference](https://docs.aws.amazon.com/acm/latest/APIReference/Welcome.html)
- [SDK for Python ACM reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/acm.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0