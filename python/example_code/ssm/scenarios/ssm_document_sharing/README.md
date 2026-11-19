# Share SSM Documents with Organizations and OUs Using RAM

This example demonstrates how to use the AWS SDK for Python (Boto3) with AWS Systems Manager (SSM) and AWS Resource Access Manager (RAM) to share SSM documents with AWS Organizations and Organizational Units (OUs).

## Overview

Previously, SSM document sharing was limited to individual AWS account IDs (up to 1,000) via `ModifyDocumentPermission`. With RAM integration, administrators can now share documents with an entire organization or specific OUs, greatly simplifying document distribution in multi-account environments.

## Organization setup

Before sharing, the scenario checks the account's organization status and asks before creating anything:

- **No organization:** creates an organization and two OUs with an AWS CloudFormation stack. The account becomes the management account. Deleting the stack at cleanup also deletes the organization.
- **Management account of an organization:** creates two OUs under the root. The existing organization is not changed.
- **Member account:** creates nothing. You enter the ARNs of existing OUs instead.

When the scenario creates the OUs, it also enables RAM sharing with the organization. That setting is left on at cleanup.

## Scenario Steps

1. **Setup**: Create an SSM Automation document for sharing.
2. **Step 1**: Share the document with an organization or OU using RAM `CreateResourceShare`.
3. **Step 2**: Verify the RAM resource share status using `GetResourceShares`.
4. **Step 3**: Add an additional OU to the resource share using `AssociateResourceShare`.
5. **Step 4**: Verify the resource share from RAM using `GetResourceShares`. A RAM-shared document can't use SSM's account-level permission APIs.
6. **Step 5**: Compare with direct account-level sharing on a second document using `ModifyDocumentPermission` and `DescribeDocumentPermission`.
7. **Step 6**: List documents to confirm visibility using `ListDocuments`.
8. **Cleanup**: Remove all resources in the correct dependency order.

## AWS Services Used

### AWS Systems Manager (SSM)
- `CreateDocument` - Creates an SSM document.
- `DescribeDocument` - Describes SSM document metadata.
- `ModifyDocumentPermission` - Shares a document with specific accounts.
- `DescribeDocumentPermission` - Describes sharing configuration.
- `ListDocuments` - Lists SSM documents with optional filtering.
- `DeleteDocument` - Deletes an SSM document.

### AWS Resource Access Manager (RAM)
- `CreateResourceShare` - Creates a resource share with specified resources/principals.
- `AssociateResourceShare` - Adds principals or resources to a share.
- `GetResourceShares` - Retrieves resource share details.
- `DisassociateResourceShare` - Removes principals or resources from a share.
- `DeleteResourceShare` - Deletes a resource share.

## Prerequisites

- Python 3.9 or later
- An AWS account that is either not in an organization, or is the management
  account of one. The scenario creates the OUs it needs and enables RAM sharing
  with the organization. From a member account, you can still run it by entering
  the ARNs of existing OUs.
- IAM permissions for SSM, RAM, AWS Organizations, and AWS CloudFormation

## Setup

1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

2. Configure AWS credentials (via environment variables, AWS CLI, or IAM role).

## Running the Scenario

```bash
python scenario_share_ssm_documents.py
```

The scenario is interactive and will prompt you for:
- Whether to create the organization resources (OUs, and an organization if the
  account has none)
- A unique name for the SSM document
- A name for the RAM resource share
- The ARN of a target organization or OU, if the scenario didn't create OUs
- Whether to add a second OU
- Whether to demonstrate direct account-level sharing, and an account ID to share with
- Whether to clean up resources

## Running Tests

Run from this folder:

```bash
python -m pytest test/test_scenario_share_ssm_documents.py -v -m integ
```

The integration test runs the whole scenario against real AWS. It needs the
management account of an existing organization, and is skipped in any other
account. It creates two OUs, RAM shares, and SSM documents, and deletes them
when it finishes. It takes several minutes, because RAM can take a few minutes
to recognize newly created OUs.

**Note**: Integration tests create real AWS resources. Run in a test/dev account.

## File Structure

```
ssm_document_sharing/
|-- organization_setup.py                       # Creates the OUs (and organization, if needed) with CloudFormation
|-- ssm_wrapper.py                              # SSM document wrapper class
|-- ram_wrapper.py                              # RAM resource share wrapper class
|-- scenario_share_ssm_documents.py             # Main interactive scenario
|-- requirements.txt                            # Python dependencies
|-- README.md                                   # This file
`-- test/
    |-- conftest.py                             # Shared test fixtures
    `-- test_scenario_share_ssm_documents.py    # Integration test
```
