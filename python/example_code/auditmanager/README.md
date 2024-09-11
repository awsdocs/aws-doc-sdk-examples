# Audit Manager code examples for the SDK for Python

## Overview

Shows how to use the AWS SDK for Python (Boto3) to work with AWS Audit Manager.

<!--custom.overview.start-->
<!--custom.overview.end-->

_Audit Manager helps you continuously audit your AWS usage to simplify how you manage risk and compliance with regulations and industry standards._

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
For more information, see the following resources:

- Familiarize yourself with Audit Manager terminology and functionality. For a general
overview, see [What is AWS Audit Manager?](https://docs.aws.amazon.com/audit-manager/latest/userguide/what-is.html) and [AWS Audit Manager concepts and terminology](https://docs.aws.amazon.com/audit-manager/latest/userguide/concepts.html).
- Complete all the prerequisites that are described in
[Setting up AWS Audit Manager](https://docs.aws.amazon.com/audit-manager/latest/userguide/setting-up.html).
- Configure your AWS Identity and Access Management (IAM) identity to have the appropriate permissions to create resources in Audit
Manager. Two suggested policies that grant these permissions are
[Example 2: Allow full administrator access](https://docs.aws.amazon.com/audit-manager/latest/userguide/security_iam_id-based-policy-examples.html#example-1)
and [Example 3: Allow management access](https://docs.aws.amazon.com/audit-manager/latest/userguide/security_iam_id-based-policy-examples.html#example-2).
- To create custom controls that use AWS Security Hub as a data source, you must first
[enable AWS Security Hub](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-settingup.html), then [enable all security standards](https://docs.aws.amazon.com/securityhub/latest/userguide/securityhub-standards-enable-disable.html#securityhub-standard-enable-console).
- To create custom controls and frameworks from an AWS Config conformance pack, you
must first [enable AWS Config](https://docs.aws.amazon.com/config/latest/developerguide/gs-console.html),
then [deploy the conformance pack](https://docs.aws.amazon.com/config/latest/developerguide/conformance-pack-console.html)
that you want to use.
<!--custom.prerequisites.end-->
### Scenarios

Code examples that show you how to accomplish a specific task by calling multiple
functions within the same service.

- [Create a custom framework from an AWS Config conformance pack](framework_from_conformance_pack.py)
- [Create a custom framework that contains Security Hub controls](security_hub_custom_framework.py)
- [Create an assessment report](create_assessment_report.py)


<!--custom.examples.start-->
<!--custom.examples.end-->

## Run the examples

### Instructions


<!--custom.instructions.start-->
<!--custom.instructions.end-->



#### Create a custom framework from an AWS Config conformance pack

This example shows you how to do the following:

- Get a list of AWS Config conformance packs.
- Create an Audit Manager custom control for each managed rule in a conformance pack.
- Create an Audit Manager custom framework that contains the controls.

<!--custom.scenario_prereqs.auditmanager_Scenario_CustomFrameworkFromConformancePack.start-->
<!--custom.scenario_prereqs.auditmanager_Scenario_CustomFrameworkFromConformancePack.end-->

Start the example by running the following at a command prompt:

```
python framework_from_conformance_pack.py
```


<!--custom.scenarios.auditmanager_Scenario_CustomFrameworkFromConformancePack.start-->
<!--custom.scenarios.auditmanager_Scenario_CustomFrameworkFromConformancePack.end-->

#### Create a custom framework that contains Security Hub controls

This example shows you how to do the following:

- Get a list of all standard controls that have Security Hub as their data source.
- Create an Audit Manager custom framework that contains the controls.

<!--custom.scenario_prereqs.auditmanager_Scenario_CustomFrameworkFromSecurityHub.start-->
<!--custom.scenario_prereqs.auditmanager_Scenario_CustomFrameworkFromSecurityHub.end-->

Start the example by running the following at a command prompt:

```
python security_hub_custom_framework.py
```


<!--custom.scenarios.auditmanager_Scenario_CustomFrameworkFromSecurityHub.start-->
<!--custom.scenarios.auditmanager_Scenario_CustomFrameworkFromSecurityHub.end-->

#### Create an assessment report

This example shows you how to create an Audit Manager assessment report that contains one day of evidence.


<!--custom.scenario_prereqs.auditmanager_Scenario_CreateAssessmentReport.start-->
<!--custom.scenario_prereqs.auditmanager_Scenario_CreateAssessmentReport.end-->

Start the example by running the following at a command prompt:

```
python create_assessment_report.py
```


<!--custom.scenarios.auditmanager_Scenario_CreateAssessmentReport.start-->
<!--custom.scenarios.auditmanager_Scenario_CreateAssessmentReport.end-->

### Tests

⚠ Running tests might result in charges to your AWS account.


To find instructions for running these tests, see the [README](../../README.md#Tests)
in the `python` folder.



<!--custom.tests.start-->
<!--custom.tests.end-->

## Additional resources

- [Audit Manager User Guide](https://docs.aws.amazon.com/audit-manager/latest/userguide/what-is.html)
- [Audit Manager API Reference](https://docs.aws.amazon.com/audit-manager/latest/APIReference/Welcome.html)
- [SDK for Python Audit Manager reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/auditmanager.html)

<!--custom.resources.start-->
<!--custom.resources.end-->

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0