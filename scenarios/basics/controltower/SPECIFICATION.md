# AWS Control Tower Basics Scenario - Technical specification

This document contains the technical specifications for _AWS Control Tower Basics Scenario_,
a basics scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Hello](#hello)
- [Scenario](#scenario)
- [Errors](#errors)
- [Metadata](#metadata)

## Resources and User Input

- This example can run with no additional resources, or can use an existing landing zone. Since landing zone creation
- requires multiple AWS accounts (which cannot be deleted for 7 days), this example does not support creating new
- landing zones. The example will prompt to use a current landing zone, or run only that portion that doesn't 
- require landing zone identifiers. To set up a landing zone, follow the [QuickStart guide](https://docs.aws.amazon.com/controltower/latest/userguide/quick-start.html), and create new accounts when prompted.

### Hello
The Hello example is a separate runnable example.

- Set up the service client.
- List available Baselines by name.

Example
```
Hello, AWS Control Tower! Let's list available baselines:

7 baseline(s) retrieved.
        AuditBaseline
        LogArchiveBaseline
        IdentityCenterBaseline
        BackupCentralVaultBaseline
        BackupAdminBaseline
        BackupBaseline

```
## Scenario

#### Setup
- List available landing zones, and prompt the user if they would like to use the first or other landing zone.
- If no landing zones, provide a link to set up a landing zone and only use the list operations that do not require a target id.
- For the selected landing zone, the control tower actions may require the arn of the target organizational id. To get it, find the
- Sandbox organizational unit inside the root organization, or create it, and store the id of that OU for the other calls.

Example
```
----------------------------------------------------------------------------------------
        Welcome to the AWS Control Tower with ControlCatalog example scenario.
----------------------------------------------------------------------------------------
This demo will walk you through working with AWS Control Tower for landing zones,
managing baselines, and working with controls.
Some demo operations require the use of a landing zone. 
You can use an existing landing zone or opt out of these operations in the demo.
For instructions on how to set up a landing zone,
see https://docs.aws.amazon.com/controltower/latest/userguide/getting-started-from-console.html

Available Landing Zones:
1 arn:aws:controltower:us-east-1:478643181688:landingzone/MBQZZWORLOCC8WZ7)
Do you want to use the first landing zone in the list (arn:aws:controltower:us-east-1:478643181688:landingzone/MBQZZWORLOCC8WZ7)? (y/n) y
Using landing zone ID: arn:aws:controltower:us-east-1:478643181688:landingzone/MBQZZWORLOCC8WZ7)

Checking organization status...
Account is part of organization: o-phmlq23w1e
Checking for Sandbox OU...
Found existing Sandbox OU: ou-spdo-e3mtcidv


```

#### Baselines
- List available baselines.
- If a landing zone exists:
  - List enabled baselines.
  - Prompt the user if they would like to enable the Control Tower baseline. 
    - Notify the user if this baseline is already enabled.
  - Prompt the user if they would like to reset the baseline.
  - Prompt the user if they would like to disable the baseline.
  - For all baseline operations, wait for a successful status before moving on.
    - Get the operational status of the baseline operation.
  - Re-enable the Control Tower baseline if it was initially enabled.
    - The control tower baseline must be enabled in order to work with enabling/disabling controls.

Example
```
Managing Baselines:

Listing available Baselines:
AuditBaseline
LogArchiveBaseline
IdentityCenterBaseline
AWSControlTowerBaseline
BackupCentralVaultBaseline
BackupAdminBaseline
BackupBaseline

Listing enabled baselines:
arn:aws:controltower:us-east-1::baseline/LN25R72TTG6IGPTQ
arn:aws:controltower:us-east-1::baseline/4T4HA1KMO10S6311
arn:aws:controltower:us-east-1::baseline/J8HX46AHS5MIKQPD
arn:aws:controltower:us-east-1::baseline/17BSJV3IGJ2QSGA2
Do you want to enable the Control Tower Baseline? (y/n) y

Enabling Control Tower Baseline.
Baseline is already enabled for this target
No change, the selected baseline was already enabled.
Do you want to reset the Control Tower Baseline? (y/n) y

Resetting Control Tower Baseline. arn:aws:controltower:us-east-1:478643181688:enabledbaseline/XOVK3ATZUCD5A04QV
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: SUCCEEDED

Reset baseline operation id 64f9c26e-c2d4-46c1-8863-f2b6382c2b4d.

Do you want to disable the Control Tower Baseline? (y/n) y
Disabling baseline ARN: arn:aws:controltower:us-east-1:478643181688:enabledbaseline/XOVK3ATZUCD5BUH7N
Baseline operation status: IN_PROGRESS
Baseline operation status: SUCCEEDED

Disabled baseline operation id ff86f9d7-8f08-4bfa-b071-5469c0dfbe60.

Enabling Control Tower Baseline.
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: IN_PROGRESS
Baseline operation status: SUCCEEDED

```

#### Controls
Some control operations require the use of the ControlCatalog client. This client does not have it's own documentation,
and so is included as part of this example.

- List Controls in Control Catalog.
- If a landing zone exists:
  - List enabled controls.
  - Prompt the user if they would like to enable the first control.
    - Notify the user if this control is already enabled.
  - Prompt the user if they would like to disable the control.
  - For all control operations, wait for a success status before moving on.
    - Get the operational status of the control

Example
```
Managing Controls:

Listing first 5 available Controls:
1. Checks if a recovery point expires no earlier than after the specified period
2. Require any AWS CodeBuild project environment to have logging configured
3. Checks if AWS AppConfig configuration profiles have tags
4. ECS containers should run as non-privileged
5. Disallow changes to Amazon CloudWatch Logs log groups set up by AWS Control Tower

Listing enabled controls:
1. arn:aws:controltower:us-east-1::control/AWS-GR_CLOUDTRAIL_CHANGE_PROHIBITED
2. arn:aws:controltower:us-east-1::control/AWS-GR_CLOUDTRAIL_CLOUDWATCH_LOGS_ENABLED
3. arn:aws:controltower:us-east-1::control/AWS-GR_CLOUDTRAIL_ENABLED
4. arn:aws:controltower:us-east-1::control/AWS-GR_CLOUDTRAIL_VALIDATION_ENABLED
5. arn:aws:controltower:us-east-1::control/AWS-GR_CLOUDWATCH_EVENTS_CHANGE_PROHIBITED
6. arn:aws:controltower:us-east-1::control/AWS-GR_CONFIG_AGGREGATION_AUTHORIZATION_POLICY
7. arn:aws:controltower:us-east-1::control/AWS-GR_CONFIG_AGGREGATION_CHANGE_PROHIBITED
8. arn:aws:controltower:us-east-1::control/AWS-GR_CONFIG_CHANGE_PROHIBITED
9. arn:aws:controltower:us-east-1::control/AWS-GR_CONFIG_ENABLED
10. arn:aws:controltower:us-east-1::control/AWS-GR_CONFIG_RULE_CHANGE_PROHIBITED
11. arn:aws:controltower:us-east-1::control/AWS-GR_IAM_ROLE_CHANGE_PROHIBITED
12. arn:aws:controltower:us-east-1::control/AWS-GR_LAMBDA_CHANGE_PROHIBITED
13. arn:aws:controltower:us-east-1::control/AWS-GR_LOG_GROUP_POLICY
15. arn:aws:controltower:us-east-1::control/AWS-GR_SNS_SUBSCRIPTION_CHANGE_PROHIBITED
16. arn:aws:controlcatalog:::control/m7a5gbdf08wg2o0en010mkng
Do you want to enable the control arn:aws:controlcatalog:::control/m7a5gbdf08wg2o0en010mkng? (y/n) y

Enabling control: arn:aws:controlcatalog:::control/m7a5gbdf08wg2o0en010mkng
arn:aws:controlcatalog:::control/m7a5gbdf08wg2o0en010mkng
arn:aws:organizations::478643181688:ou/o-phmlq23w1e/ou-spdo-e3mtcidv
Control is already enabled for this target
Do you want to disable the control? (y/n) y

Disabling the control...
Control operation status: IN_PROGRESS
Control operation status: SUCCEEDED
Disable operation ID: c9c24ab0-9988-48fa-a8f3-1c5daf979176
This concludes the control tower scenario.
Thanks for watching!


```


---

## Errors
The following errors are handled in the Control Tower wrapper class:

| Action                 | Error                 | Handling                                                               |
|------------------------|-----------------------|------------------------------------------------------------------------|
| `ListBaselines`        | AccessDeniedException | Notify the user of insufficient permissions and exit.                  |
| `ListEnabledBaselines` | AccessDeniedException | Notify the user of insufficient permissions and exit.                  |
| `EnableBaseline`       | ValidationException   | Handle case where baseline is already enabled and return None.         |
| `DisableBaseline`      | ConflictException     | Notify the user that the baseline could not be disabled, and continue. |
| `ListControls`         | AccessDeniedException | Notify the user of insufficient permissions and exit.                  |
| `ListEnabledControls`  | ResourceNotFound      | Check for "not registered with AWS Control Tower" and notify the user. |
| `EnableControl`        | ValidationException   | Handle case where control is already enabled and return None.          |
| `EnableControl`        | ResourceNotFound      | Check for "not registered with AWS Control Tower" and notify the user. |
| `GetControlOperation`  | ResourceNotFound      | Notify the user that the control operation was not found.              |
| `DisableControl`       | ResourceNotFound      | Notify the user that the control was not found.                        | 
| `ListLandingZones`     | AccessDeniedException | Notify the user of insufficient permissions and exit.                  |


---

## Metadata

| action / scenario              | metadata file              | metadata key                      |
|--------------------------------|----------------------------|-----------------------------------|
| `ListBaselines`                | controltower_metadata.yaml | controltower_Hello                |
| `ListBaselines`                | controltower_metadata.yaml | controltower_ListBaselines        |
| `ListEnabledBaselines`         | controltower_metadata.yaml | controltower_ListEnabledBaselines |
| `EnableBaseline`               | controltower_metadata.yaml | controltower_EnableBaseline       |
| `DisableBaseline`              | controltower_metadata.yaml | controltower_DisableBaseline      |
| `ListEnabledControls`          | controltower_metadata.yaml | controltower_ListEnabledControls  |
| `EnableControl`                | controltower_metadata.yaml | controltower_EnableControl        |
| `GetControlOperation`          | controltower_metadata.yaml | controltower_GetControlOperation  |
| `DisableControl`               | controltower_metadata.yaml | controltower_DisableControl       |
| `ListLandingZones`             | controltower_metadata.yaml | controltower_ListLandingZones     |
| `Control Tower Basics Scenario` | controltower_metadata.yaml | controltower_Scenario             |

