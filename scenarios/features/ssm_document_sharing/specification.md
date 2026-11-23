# AWS Systems Manager & RAM: Share SSM Documents with Organizations and OUs Specification

> This specification describes a scenario that demonstrates how to share AWS Systems Manager (SSM) documents with AWS Organizations and Organizational Units (OUs) using AWS Resource Access Manager (RAM). This feature enables centralized management and distribution of SSM documents across an organization without needing to share individually with each AWS account.

This scenario walks through creating an SSM document, sharing it with an entire organization or specific OUs via a RAM resource share, verifying the sharing configuration, and then cleaning up all resources. The example demonstrates the end-to-end workflow a DevOps or security team would follow to distribute automation runbooks, compliance documents, or operational procedures across their multi-account organization.

### Resources

- An AWS account that is a member of an AWS Organization (the management account or a delegated administrator)
- A target Organization or Organizational Unit (OU) to share with
- IAM permissions for both SSM and RAM API operations
- AWS Organizations integration enabled in RAM settings

If the account does not already have these resources, the scenario's setup steps create them with an AWS CloudFormation stack, after the user confirms:

- If the account is not part of an organization, the stack creates an organization (all features enabled) and two OUs, and the account becomes the management account.
- If the account is the management account of an existing organization, the stack creates only two OUs under the root. No organization is created.
- If the account is a member account of an organization, nothing is created. The scenario asks the user for existing OU ARNs, because only the management account can create OUs or enable RAM sharing.

When it creates resources, the scenario also enables RAM sharing with the organization. At the end of the scenario, prompt the user if they want to clean up. Cleanup deletes the stack, which deletes the OUs and, if the stack created it, the organization.

### Relevant documentation

- [AWS Systems Manager Documents](https://docs.aws.amazon.com/systems-manager/latest/userguide/documents.html)
- [Sharing SSM Documents](https://docs.aws.amazon.com/systems-manager/latest/userguide/documents-ssm-sharing.html)
- [What is AWS Resource Access Manager?](https://docs.aws.amazon.com/ram/latest/userguide/what-is.html)
- [Shareable AWS Resources — AWS Systems Manager](https://docs.aws.amazon.com/ram/latest/userguide/shareable.html)
- [SSM CreateDocument API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_CreateDocument.html)
- [SSM DescribeDocument API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DescribeDocument.html)
- [SSM ModifyDocumentPermission API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_ModifyDocumentPermission.html)
- [SSM DescribeDocumentPermission API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DescribeDocumentPermission.html)
- [SSM ListDocuments API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_ListDocuments.html)
- [SSM DeleteDocument API Reference](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DeleteDocument.html)
- [RAM CreateResourceShare API Reference](https://docs.aws.amazon.com/ram/latest/APIReference/API_CreateResourceShare.html)
- [RAM AssociateResourceShare API Reference](https://docs.aws.amazon.com/ram/latest/APIReference/API_AssociateResourceShare.html)
- [RAM GetResourceShares API Reference](https://docs.aws.amazon.com/ram/latest/APIReference/API_GetResourceShares.html)
- [RAM DisassociateResourceShare API Reference](https://docs.aws.amazon.com/ram/latest/APIReference/API_DisassociateResourceShare.html)
- [RAM DeleteResourceShare API Reference](https://docs.aws.amazon.com/ram/latest/APIReference/API_DeleteResourceShare.html)

### API Actions Used

**AWS Systems Manager (SSM):**
- [CreateDocument](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_CreateDocument.html) — Creates an SSM document that defines the actions Systems Manager performs on managed nodes.
- [DescribeDocument](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DescribeDocument.html) — Describes the specified SSM document, returning metadata such as status, owner, and document version.
- [ModifyDocumentPermission](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_ModifyDocumentPermission.html) — Shares an SSM document publicly or privately by specifying AWS account IDs.
- [DescribeDocumentPermission](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DescribeDocumentPermission.html) — Describes the permissions (sharing configuration) for an SSM document.
- [ListDocuments](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_ListDocuments.html) — Returns all SSM documents in the current account and Region, with optional filtering.
- [DeleteDocument](https://docs.aws.amazon.com/systems-manager/latest/APIReference/API_DeleteDocument.html) — Deletes an SSM document and all managed node associations.

**AWS Resource Access Manager (RAM):**
- [CreateResourceShare](https://docs.aws.amazon.com/ram/latest/APIReference/API_CreateResourceShare.html) — Creates a resource share with specified resources and principals (organizations/OUs).
- [AssociateResourceShare](https://docs.aws.amazon.com/ram/latest/APIReference/API_AssociateResourceShare.html) — Adds principals or resources to an existing resource share.
- [GetResourceShares](https://docs.aws.amazon.com/ram/latest/APIReference/API_GetResourceShares.html) — Retrieves details about resource shares you own or that are shared with you.
- [DisassociateResourceShare](https://docs.aws.amazon.com/ram/latest/APIReference/API_DisassociateResourceShare.html) — Removes principals or resources from a resource share.
- [DeleteResourceShare](https://docs.aws.amazon.com/ram/latest/APIReference/API_DeleteResourceShare.html) — Deletes the specified resource share.

---

## Scenario

### Share SSM Documents with Organizations and OUs Using RAM

This scenario demonstrates how to create an SSM document and share it across an AWS Organization or specific Organizational Units using AWS Resource Access Manager (RAM). Previously, SSM document sharing was limited to individual AWS account IDs (up to 1,000) via `ModifyDocumentPermission`. With RAM integration, administrators can now share documents with an entire organization or specific OUs, greatly simplifying document distribution in multi-account environments.

### Setup

1. **Create an SSM Automation document** for sharing.
   - Call `CreateDocument` with:
     - `Name`: A unique document name (e.g., `OrgShared-PatchBaseline-Automation`)
     - `Content`: A valid SSM Automation document in YAML or JSON format (e.g., a simple automation that describes an instance or performs a health check)
     - `DocumentType`: `Automation`
     - `DocumentFormat`: `YAML`
     - `Tags`: `[{Key: "Purpose", Value: "OrgSharing"}, {Key: "ManagedBy", Value: "CentralOps"}]`
   - Confirm the response returns a `DocumentDescription` with `Status` equal to `Active`.
   - Store the document `Name` for use in subsequent steps.

2. **Verify the document was created successfully.**
   - Call `DescribeDocument` with `Name` set to the document name from Step 1.
   - Confirm the document `Status` is `Active`, `Owner` matches the current account ID, and `DocumentType` is `Automation`.

### Step 1: Share the document with an organization or OU using RAM

Create a RAM resource share that includes the SSM document as a resource and targets the organization or OU as the principal.

- Call RAM `CreateResourceShare` with:
  - `name`: A descriptive name (e.g., `SSM-Document-OrgShare`)
  - `resourceArns`: The ARN of the SSM document (format: `arn:aws:ssm:<region>:<account-id>:document/<document-name>`)
  - `principals`: The ARN of the target organization or OU (e.g., `arn:aws:organizations::<management-account-id>:organization/o-exampleorgid` or `arn:aws:organizations::<management-account-id>:ou/o-exampleorgid/ou-examplerootid-exampleouid123`)
  - `allowExternalPrincipals`: `false` (restrict to organization members only)
  - `tags`: `[{key: "Feature", value: "SsmDocumentSharing"}]`
- Confirm the response contains a `resourceShare` object with `status` equal to `PENDING` or `ACTIVE`.
- Store the `resourceShareArn` for use in subsequent steps.

### Step 2: Verify the RAM resource share status

- Call RAM `GetResourceShares` with:
  - `resourceOwner`: `SELF`
  - `resourceShareArns`: The resource share ARN from Step 1
- Confirm the returned resource share has `status` equal to `ACTIVE`.
- Display the resource share name, ARN, owning account ID, and creation time.

### Step 3: Add an additional OU to the resource share

Demonstrate adding a second OU principal to an existing resource share using the `AssociateResourceShare` action.

- Call RAM `AssociateResourceShare` with:
  - `resourceShareArn`: The resource share ARN from Step 1
  - `principals`: The ARN of a second OU (e.g., `arn:aws:organizations::<management-account-id>:ou/o-exampleorgid/ou-examplerootid-secondouid456`)
- Confirm the response returns `resourceShareAssociations` with `associationType` equal to `PRINCIPAL` and `status` equal to `ASSOCIATING` or `ASSOCIATED`.

### Step 4: Verify the resource share from RAM

A document shared through RAM is managed by RAM. SSM rejects `DescribeDocumentPermission` and `ModifyDocumentPermission` for it with `InvalidDocumentOperation` ("This document can only be shared using RAM or resource policies"), so verify the sharing state from RAM.

- Call RAM `GetResourceShares` with:
  - `resourceOwner`: `SELF`
  - `resourceShareArns`: The resource share ARN
- Display the share's `status`, `allowExternalPrincipals`, and `lastUpdatedTime`.

### Step 5: Compare with direct account-level sharing (optional demonstration)

Demonstrate the traditional account-level sharing method for comparison. Because a RAM-shared document can't also be shared by account, use a second document.

- Call SSM `CreateDocument` to create a second Automation document named `<first document name>-direct`, using the same content.
- Call SSM `ModifyDocumentPermission` with:
  - `Name`: The second document name
  - `PermissionType`: `Share`
  - `AccountIdsToAdd`: A specific account ID (e.g., `444455556666`)
- Call SSM `DescribeDocumentPermission` with:
  - `Name`: The second document name
  - `PermissionType`: `Share`
- Confirm the new account ID appears in `AccountIds`.
- Note the difference: `ModifyDocumentPermission` targets individual accounts (max 20 per call, 1,000 total), while RAM resource shares can target entire organizations or OUs.

### Step 6: List documents to confirm visibility

- Call SSM `ListDocuments` with:
  - `Filters`: `[{Key: "Owner", Values: ["Self"]}]`
- Verify the created document appears in the results.
- Display the document name, type, and owner.

### Cleanup

Cleanup must occur in the correct order to avoid errors:

1. **Remove the additional OU from the resource share.**
   - Call RAM `DisassociateResourceShare` with:
     - `resourceShareArn`: The resource share ARN
     - `principals`: The ARN of the second OU added in Step 3
   - Confirm the response shows `status` of `DISASSOCIATING` or `DISASSOCIATED`.

2. **Delete the RAM resource share.**
   - Call RAM `DeleteResourceShare` with:
     - `resourceShareArn`: The resource share ARN
   - Confirm the response returns `returnValue` equal to `true`.

3. **Remove direct account-level sharing and delete the second document (if created in Step 5).**
   - Call SSM `ModifyDocumentPermission` with:
     - `Name`: The second document name
     - `PermissionType`: `Share`
     - `AccountIdsToRemove`: The account ID added in Step 5
   - Call SSM `DeleteDocument` with `Name` set to the second document name.

4. **Delete the SSM document.**
   - Call SSM `DeleteDocument` with:
     - `Name`: The document name
   - Confirm the operation returns successfully (HTTP 200 with empty body).

5. Remove any stack created by the scenario.

### Outcome

Upon successful completion of this scenario, the user will have:

- Created an SSM Automation document suitable for organization-wide distribution.
- Shared the document with an entire AWS Organization or specific OUs using RAM, demonstrating RAM-based sharing.
- Verified the RAM resource share with `GetResourceShares`.
- Demonstrated adding additional OU principals to an existing resource share.
- Compared RAM-based organization/OU sharing with traditional account-level sharing via `ModifyDocumentPermission` and `DescribeDocumentPermission` on a second document.
- Properly cleaned up all resources (RAM resource share, document permissions, SSM document) in the correct dependency order.

The key advantage demonstrated is that RAM-based sharing with organizations and OUs eliminates the need to manage sharing for individual accounts (which was limited to 1,000 accounts). When new accounts join the organization or OU, they automatically gain access to the shared documents without any additional API calls.

---

## Example Output

The following output is from a run in the management account of an existing organization. IDs are replaced with example values, and repeated "Press Enter to continue..." prompts are omitted.

```
======================================================================
Welcome to the SSM Document Sharing with Organizations/OUs Scenario
======================================================================

--- Setup: Organization Resources ---

This account is the management account of organization o-exampleorgid.

Create two OUs under the root of this organization for the scenario? (y/n) y
Creating stack 'ssm-document-sharing-1a2b3c4d'. This can take a few minutes...
Created OU: arn:aws:organizations::111122223333:ou/o-exampleorgid/ou-exam-11111111
Created OU: arn:aws:organizations::111122223333:ou/o-exampleorgid/ou-exam-22222222

Enabling RAM sharing with the organization...
RAM sharing with the organization is enabled.

--- Setup: Creating an SSM Automation Document ---

Current account: 111122223333
Current region:  us-west-2

Enter a unique name for the SSM document (e.g., OrgShared-PatchBaseline-Automation): ssm-sharing-demo
Creating SSM Automation document 'ssm-sharing-demo'...
Document created with status: Creating

Verifying document 'ssm-sharing-demo'...
  Status:       Active
  Owner:        111122223333
  DocumentType: Automation

--- Step 1: Share the Document Using RAM ---

SSM Document ARN: arn:aws:ssm:us-west-2:111122223333:document/ssm-sharing-demo

Sharing with the OU created for this scenario:
  arn:aws:organizations::111122223333:ou/o-exampleorgid/ou-exam-11111111

Enter a name for the RAM resource share (e.g., SSM-Document-OrgShare): SSM-Document-OrgShare
Creating RAM resource share 'SSM-Document-OrgShare'...
RAM doesn't recognize the OU yet. This is normal for a few minutes after an OU is created or sharing is enabled.
Waiting 15 seconds and trying again (attempt 1 of 24)...
Resource share created:
  ARN:    arn:aws:ram:us-west-2:111122223333:resource-share/12345678-90ab-cdef-1234-567890abcdef
  Status: ACTIVE

--- Step 2: Verify the RAM Resource Share ---

Resource share details:
  Name:            SSM-Document-OrgShare
  Owning Account:  111122223333
  Status:          ACTIVE

--- Step 3: Add an Additional OU to the Resource Share ---

Would you like to add a second OU to the resource share? (y/n) y
Using the second OU created for this scenario:
  arn:aws:organizations::111122223333:ou/o-exampleorgid/ou-exam-22222222
  Association type: PRINCIPAL
  Status:           ASSOCIATING

--- Step 4: Verify the Resource Share from RAM ---

Resource share 'SSM-Document-OrgShare':
  Status:                    ACTIVE
  Allows external principals: False

--- Step 5: Direct Account-Level Sharing (Comparison) ---

Would you like to demonstrate direct account sharing? (y/n) y

Creating SSM Automation document 'ssm-sharing-demo-direct'...

Enter an AWS account ID to share with (e.g., 444455556666): 444455556666

Sharing document 'ssm-sharing-demo-direct' with account '444455556666'...
Direct sharing applied successfully.

Verifying permissions with DescribeDocumentPermission...
Document is now shared with accounts: ['444455556666']

--- Step 6: List Documents to Confirm Visibility ---

Your SSM documents (owned by Self):
  *** ssm-sharing-demo (Type: Automation, Owner: 111122223333) ***
      ssm-sharing-demo-direct (Type: Automation, Owner: 111122223333)

Document 'ssm-sharing-demo' confirmed in list.

Do you want to clean up all resources created by this scenario? (y/n) y

--- Cleanup ---

Removing OU 'arn:aws:organizations::111122223333:ou/o-exampleorgid/ou-exam-22222222' from resource share...
  Disassociation status: DISASSOCIATING

Deleting RAM resource share 'arn:aws:ram:us-west-2:111122223333:resource-share/12345678-90ab-cdef-1234-567890abcdef'...
  Deletion successful: True

Removing direct sharing of 'ssm-sharing-demo-direct' with account '444455556666'...
  Direct sharing removed.

Deleting SSM document 'ssm-sharing-demo-direct'...
  Document deleted successfully.

Deleting SSM document 'ssm-sharing-demo'...
  Document deleted successfully.

Deleting stack 'ssm-document-sharing-1a2b3c4d', which removes the OUs. This can take a few minutes...
  Stack deleted successfully.

Cleanup complete.

======================================================================
Scenario complete!
======================================================================
```

---

## Errors

| Action | Exception | Handling |
|--------|-----------|----------|
| SSM CreateDocument | DocumentAlreadyExists | Check if a document with the same name already exists. Use a unique name or delete the existing document before creating a new one. |
| SSM DescribeDocument | InvalidDocument | Verify the document name is correct and the document exists in the current Region. The document may have been deleted or the name may be misspelled. |
| SSM ModifyDocumentPermission | DocumentPermissionLimit | The document cannot be shared with more accounts. You can share privately with up to 1,000 accounts total and up to 20 per API call. Use RAM for organization/OU-level sharing instead. |
| SSM DescribeDocumentPermission | InvalidPermissionType | The only valid permission type is Share. Verify the PermissionType parameter is set correctly. |
| SSM ListDocuments | InvalidFilterKey | Verify the filter key is one of the supported values: Owner, Name, PlatformTypes, DocumentType, or TargetType. |
| SSM DeleteDocument | InvalidDocumentOperation | The document is still shared. You must stop sharing (remove all account permissions and delete the RAM resource share) before deleting the document. |
| RAM CreateResourceShare | MalformedArnException | Verify the resource ARN and principal ARN formats are correct. SSM document ARN format: arn:aws:ssm:region:account-id:document/document-name. Organization ARN format: arn:aws:organizations::account-id:organization/o-id. |
| RAM AssociateResourceShare | OperationNotPermittedException | The caller does not have permission to perform this operation. Verify IAM policies grant ram:AssociateResourceShare and that AWS Organizations integration is enabled in RAM settings. |
| RAM GetResourceShares | InvalidParameterException | Verify the resourceOwner parameter is set to SELF or OTHER-ACCOUNTS, and any provided resource share ARNs are properly formatted. |
| RAM DisassociateResourceShare | UnknownResourceException | The specified resource share or principal was not found. Verify the resource share ARN exists and the principal is currently associated. |
| RAM DeleteResourceShare | InvalidStateTransitionException | The resource share is not in a valid state for deletion. Ensure all associations have been removed and the share is in ACTIVE state before deleting. |

---

## Metadata

| Action/Scenario | Metadata File | Metadata Key |
|----------------|---------------|--------------|
| SSM ModifyDocumentPermission | ssm_metadata.yaml | ssm_ModifyDocumentPermission |
| SSM DescribeDocumentPermission | ssm_metadata.yaml | ssm_DescribeDocumentPermission |
| SSM ListDocuments | ssm_metadata.yaml | ssm_ListDocuments |
| RAM CreateResourceShare | ram_metadata.yaml | ram_CreateResourceShare |
| RAM AssociateResourceShare | ram_metadata.yaml | ram_AssociateResourceShare |
| RAM GetResourceShares | ram_metadata.yaml | ram_GetResourceShares |
| RAM DisassociateResourceShare | ram_metadata.yaml | ram_DisassociateResourceShare |
| RAM DeleteResourceShare | ram_metadata.yaml | ram_DeleteResourceShare |
| Share SSM Documents with Organizations Scenario | ssm_metadata.yaml | ssm_ShareDocuments_Scenario |
| Share Documents with Organizations Scenario (RAM) | ram_metadata.yaml | ram_ShareDocuments_Scenario |
