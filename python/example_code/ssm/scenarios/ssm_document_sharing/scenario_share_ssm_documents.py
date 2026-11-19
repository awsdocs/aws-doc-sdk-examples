# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

"""
Purpose

Shows how to use the AWS SDK for Python (Boto3) with AWS Systems Manager (SSM)
and AWS Resource Access Manager (RAM) to share SSM documents with AWS Organizations
and Organizational Units (OUs).

This scenario demonstrates:
1. Creating an SSM Automation document for sharing.
2. Sharing the document with an organization or OU via a RAM resource share.
3. Verifying the resource share from RAM.
4. Adding additional OU principals to the resource share.
5. Comparing RAM-based sharing with traditional account-level sharing, using a
   second document (a RAM-shared document can't also be shared by account).
6. Listing documents to confirm visibility.
7. Cleaning up all resources in the correct dependency order.
"""

import logging
import sys
import time
import uuid
from typing import Optional

import boto3
from botocore.exceptions import ClientError, WaiterError

from ssm_wrapper import SsmDocumentWrapper
from ram_wrapper import RamWrapper
from organization_setup import OrganizationSetup

# Add relative path to include demo_tools in this code example without need for setup.
sys.path.append("../../../..")
import demo_tools.question as q  # noqa

logger = logging.getLogger(__name__)

# Sample SSM Automation document content in YAML format.
SAMPLE_AUTOMATION_CONTENT = """---
schemaVersion: '0.3'
description: 'Organization-shared automation document for health checks.'
assumeRole: '{{ AutomationAssumeRole }}'
parameters:
  AutomationAssumeRole:
    type: String
    description: 'The ARN of the role that allows Automation to perform actions.'
    default: ''
  InstanceId:
    type: String
    description: 'The ID of the EC2 instance to check.'
mainSteps:
  - name: describeInstance
    action: 'aws:executeAwsApi'
    inputs:
      Service: ec2
      Api: DescribeInstances
      InstanceIds:
        - '{{ InstanceId }}'
    outputs:
      - Name: InstanceState
        Selector: '$.Reservations[0].Instances[0].State.Name'
        Type: String
"""


# snippet-start:[python.example_code.ssm.ShareDocumentsScenario]
class ShareSsmDocumentsScenario:
    """
    Runs an interactive scenario demonstrating how to share SSM documents
    with AWS Organizations and OUs using AWS Resource Access Manager (RAM).
    """

    def __init__(
        self,
        ssm_wrapper: SsmDocumentWrapper,
        ram_wrapper: RamWrapper,
        sts_client: boto3.client,
        org_setup: OrganizationSetup,
    ):
        """
        :param ssm_wrapper: An instance of SsmDocumentWrapper for SSM operations.
        :param ram_wrapper: An instance of RamWrapper for RAM operations.
        :param sts_client: A Boto3 STS client to look up the current account ID.
        :param org_setup: An instance of OrganizationSetup that creates the
                          organization resources when they are needed.
        """
        self.ssm_wrapper = ssm_wrapper
        self.ram_wrapper = ram_wrapper
        self.sts_client = sts_client
        self.org_setup = org_setup
        self.document_name = None
        self.resource_share_arn = None
        self.second_ou_arn = None
        self.direct_share_account_id = None
        self.direct_document_name = None
        self.stack_name = None
        self.created_organization = False
        self.principal_arn = None
        self.suggested_second_ou_arn = None

    def run_scenario(self) -> None:
        """
        Runs the full scenario demonstrating SSM document sharing via RAM.
        """
        print("=" * 70)
        print("Welcome to the SSM Document Sharing with Organizations/OUs Scenario")
        print("=" * 70)
        print(
            "\nThis scenario demonstrates how to create an SSM document and share\n"
            "it across an AWS Organization or specific Organizational Units (OUs)\n"
            "using AWS Resource Access Manager (RAM).\n"
        )

        try:
            self._setup_organization()
            self._setup()
            self._step1_share_via_ram()
            self._step2_verify_ram_share()
            self._step3_add_additional_ou()
            self._step4_verify_ram_principals()
            self._step5_direct_account_sharing()
            self._step6_list_documents()
        finally:
            cleanup = q.ask(
                "\nDo you want to clean up all resources created by this "
                "scenario? (y/n) ",
                q.is_yesno,
            )
            if cleanup:
                self._cleanup()
            else:
                print("Skipping cleanup. Resources remain active.")

        print("\n" + "=" * 70)
        print("Scenario complete!")
        print("=" * 70)

    def _setup_organization(self) -> None:
        """
        Makes sure an organization with OUs is available to share with. If the
        account has no organization, creates one with a CloudFormation stack, but
        only after the user agrees. If the account already manages an organization,
        offers to create two OUs in it. In every other case the user is asked for
        existing ARNs later in the scenario.
        """
        print("\n--- Setup: Organization Resources ---\n")

        account_id = self.sts_client.get_caller_identity()["Account"]
        organization = self.org_setup.get_organization()

        if organization is None:
            print(
                "This account is not part of an AWS Organization, and sharing with\n"
                "an organization or OU requires one. The scenario can create an\n"
                "organization and two OUs with an AWS CloudFormation stack.\n"
                "\nNote: this account becomes the organization's management account.\n"
                "If you choose cleanup at the end, the stack and the organization are\n"
                "deleted. If you skip cleanup, they remain until you delete the stack.\n"
            )
            create = q.ask(
                "Create an organization and two OUs now? (y/n) ", q.is_yesno
            )
            if not create:
                print("Skipping. You will be asked for existing ARNs instead.")
                return
            existing_root_id = None
        else:
            # The organization ARN contains the ID of the management account.
            management_account_id = organization["Arn"].split(":")[4]
            if management_account_id != account_id:
                print(
                    f"This account belongs to organization {organization['Id']}, but\n"
                    "it is a member account. Only the management account can create\n"
                    "OUs or enable RAM sharing, so you will be asked for existing\n"
                    "OU ARNs instead.\n"
                )
                return
            print(
                f"This account is the management account of organization "
                f"{organization['Id']}.\n"
            )
            create = q.ask(
                "Create two OUs under the root of this organization for the "
                "scenario? (y/n) ",
                q.is_yesno,
            )
            if not create:
                print("Skipping. You will be asked for existing ARNs instead.")
                return
            existing_root_id = self.org_setup.get_root_id()

        # Record the name first so cleanup can delete the stack even if it fails.
        self.stack_name = f"ssm-document-sharing-{uuid.uuid4().hex[:8]}"
        self.created_organization = existing_root_id is None
        outputs = self.org_setup.deploy_stack(self.stack_name, existing_root_id)
        self.principal_arn = outputs["PrimaryOuArn"]
        self.suggested_second_ou_arn = outputs["SecondaryOuArn"]
        print(f"Created OU: {self.principal_arn}")
        print(f"Created OU: {self.suggested_second_ou_arn}")

        print("\nEnabling RAM sharing with the organization...")
        self.org_setup.enable_ram_sharing()
        print("RAM sharing with the organization is enabled.")
        q.ask("\nPress Enter to continue...")

    def _setup(self) -> None:
        """
        Sets up the scenario by creating an SSM Automation document.
        """
        print("\n--- Setup: Creating an SSM Automation Document ---\n")

        account_id = self.sts_client.get_caller_identity()["Account"]
        region = self.ssm_wrapper.ssm_client.meta.region_name
        print(f"Current account: {account_id}")
        print(f"Current region:  {region}\n")

        self.document_name = q.ask(
            "Enter a unique name for the SSM document "
            "(e.g., OrgShared-PatchBaseline-Automation): ",
            q.non_empty,
        )

        print(f"\nCreating SSM Automation document '{self.document_name}'...")
        tags = [
            {"Key": "Purpose", "Value": "OrgSharing"},
            {"Key": "ManagedBy", "Value": "CentralOps"},
        ]
        doc_desc = self.ssm_wrapper.create_document(
            name=self.document_name,
            content=SAMPLE_AUTOMATION_CONTENT,
            document_type="Automation",
            document_format="YAML",
            tags=tags,
        )
        status = doc_desc.get("Status", "Unknown")
        print(f"Document created with status: {status}")

        # Verify the document
        print(f"\nVerifying document '{self.document_name}'...")
        doc = self.ssm_wrapper.describe_document(self.document_name)
        print(f"  Status:       {doc.get('Status')}")
        print(f"  Owner:        {doc.get('Owner')}")
        print(f"  DocumentType: {doc.get('DocumentType')}")
        q.ask("\nPress Enter to continue...")

    def _step1_share_via_ram(self) -> None:
        """
        Step 1: Share the SSM document with an organization or OU using RAM.
        """
        print("\n--- Step 1: Share the Document Using RAM ---\n")
        print(
            "To share the document via RAM, you need the ARN of the SSM document\n"
            "and the ARN of the target organization or OU.\n"
        )

        account_id = self.sts_client.get_caller_identity()["Account"]
        region = self.ssm_wrapper.ssm_client.meta.region_name
        document_arn = f"arn:aws:ssm:{region}:{account_id}:document/{self.document_name}"
        print(f"SSM Document ARN: {document_arn}\n")

        if self.principal_arn:
            principal_arn = self.principal_arn
            print(f"Sharing with the OU created for this scenario:\n  {principal_arn}\n")
        else:
            principal_arn = q.ask(
                "Enter the ARN of the target organization or OU\n"
                "(e.g., arn:aws:organizations::123456789012:organization/o-exampleorgid\n"
                " or   arn:aws:organizations::123456789012:ou/o-orgid/ou-rootid-ouid): ",
                q.non_empty,
            )

        share_name = q.ask(
            "\nEnter a name for the RAM resource share (e.g., SSM-Document-OrgShare): ",
            q.non_empty,
        )

        print(f"\nCreating RAM resource share '{share_name}'...")
        # RAM can take a few minutes to recognize newly created OUs, and the
        # organization itself right after sharing is turned on. Until then,
        # CreateResourceShare fails with UnknownResourceException, so retry.
        max_attempts = 24
        for attempt in range(1, max_attempts + 1):
            try:
                resource_share = self.ram_wrapper.create_resource_share(
                    name=share_name,
                    resource_arns=[document_arn],
                    principals=[principal_arn],
                    allow_external_principals=False,
                    tags=[{"key": "Feature", "value": "SsmDocumentSharing"}],
                )
                break
            except ClientError as err:
                if (
                    err.response["Error"]["Code"] != "UnknownResourceException"
                    or attempt == max_attempts
                ):
                    raise
                print(
                    "RAM doesn't recognize the OU yet. This is normal for a few "
                    "minutes after an OU is created or sharing is enabled.\n"
                    f"Waiting 15 seconds and trying again "
                    f"(attempt {attempt} of {max_attempts})..."
                )
                time.sleep(15)
        self.resource_share_arn = resource_share.get("resourceShareArn")
        share_status = resource_share.get("status")
        print(f"Resource share created:")
        print(f"  ARN:    {self.resource_share_arn}")
        print(f"  Status: {share_status}")
        q.ask("\nPress Enter to continue...")

    def _step2_verify_ram_share(self) -> None:
        """
        Step 2: Verify the RAM resource share status.
        """
        print("\n--- Step 2: Verify the RAM Resource Share ---\n")

        print("Waiting a few seconds for the resource share to activate...")
        time.sleep(5)

        shares = self.ram_wrapper.get_resource_shares(
            resource_owner="SELF",
            resource_share_arns=[self.resource_share_arn],
        )

        if shares:
            share = shares[0]
            print("Resource share details:")
            print(f"  Name:            {share.get('name')}")
            print(f"  ARN:             {share.get('resourceShareArn')}")
            print(f"  Owning Account:  {share.get('owningAccountId')}")
            print(f"  Status:          {share.get('status')}")
            creation_time = share.get("creationTime")
            if creation_time:
                print(f"  Created:         {creation_time}")
        else:
            print("No resource share found with the specified ARN.")

        q.ask("\nPress Enter to continue...")

    def _step3_add_additional_ou(self) -> None:
        """
        Step 3: Add an additional OU principal to the resource share.
        """
        print("\n--- Step 3: Add an Additional OU to the Resource Share ---\n")

        add_ou = q.ask(
            "Would you like to add a second OU to the resource share? (y/n) ",
            q.is_yesno,
        )
        if not add_ou:
            print("Skipping additional OU association.")
            return

        if self.suggested_second_ou_arn:
            self.second_ou_arn = self.suggested_second_ou_arn
            print(f"Using the second OU created for this scenario:\n  {self.second_ou_arn}")
        else:
            self.second_ou_arn = q.ask(
                "\nEnter the ARN of the second OU\n"
                "(e.g., arn:aws:organizations::123456789012:ou/o-orgid/ou-rootid-secondouid): ",
                q.non_empty,
            )

        print(f"\nAssociating OU '{self.second_ou_arn}' with the resource share...")
        associations = self.ram_wrapper.associate_resource_share(
            resource_share_arn=self.resource_share_arn,
            principals=[self.second_ou_arn],
        )

        for assoc in associations:
            print(f"  Association type: {assoc.get('associationType')}")
            print(f"  Status:           {assoc.get('status')}")
            print(f"  Entity:           {assoc.get('associatedEntity')}")

        q.ask("\nPress Enter to continue...")

    def _step4_verify_ram_principals(self) -> None:
        """
        Step 4: Verify the resource share from RAM after adding principals.

        A document shared through RAM is managed by RAM. SSM rejects
        DescribeDocumentPermission and ModifyDocumentPermission for it with an
        InvalidDocumentOperation error, so the sharing state is read from RAM.
        """
        print("\n--- Step 4: Verify the Resource Share from RAM ---\n")
        print(
            "A document shared through RAM is managed by RAM, so SSM's\n"
            "account-level permission APIs don't apply to it. RAM reports\n"
            "the sharing state instead.\n"
        )

        shares = self.ram_wrapper.get_resource_shares(
            resource_owner="SELF",
            resource_share_arns=[self.resource_share_arn],
        )
        if shares:
            share = shares[0]
            print(f"Resource share '{share.get('name')}':")
            print(f"  Status:                    {share.get('status')}")
            print(
                f"  Allows external principals: "
                f"{share.get('allowExternalPrincipals')}"
            )
            print(f"  Last updated:              {share.get('lastUpdatedTime')}")
        else:
            print("No resource share found with the specified ARN.")

        q.ask("\nPress Enter to continue...")

    def _step5_direct_account_sharing(self) -> None:
        """
        Step 5: Demonstrate traditional account-level sharing for comparison,
        using a second document that is not shared through RAM.
        """
        print("\n--- Step 5: Direct Account-Level Sharing (Comparison) ---\n")
        print(
            "For comparison, this step shows the traditional method of sharing\n"
            "with individual accounts using ModifyDocumentPermission. This method\n"
            "is limited to 1,000 accounts total (20 per API call), while RAM can\n"
            "share with entire organizations or OUs.\n"
            "\nA document shared through RAM can't also be shared this way, so\n"
            "this step creates a second document.\n"
        )

        do_direct = q.ask(
            "Would you like to demonstrate direct account sharing? (y/n) ",
            q.is_yesno,
        )
        if not do_direct:
            print("Skipping direct account sharing demonstration.")
            return

        self.direct_document_name = f"{self.document_name}-direct"
        print(f"\nCreating SSM Automation document '{self.direct_document_name}'...")
        self.ssm_wrapper.create_document(
            name=self.direct_document_name,
            content=SAMPLE_AUTOMATION_CONTENT,
            document_type="Automation",
            document_format="YAML",
        )

        self.direct_share_account_id = q.ask(
            "\nEnter an AWS account ID to share with (e.g., 444455556666): ",
            q.non_empty,
        )

        print(
            f"\nSharing document '{self.direct_document_name}' with account "
            f"'{self.direct_share_account_id}'..."
        )
        self.ssm_wrapper.modify_document_permission(
            name=self.direct_document_name,
            permission_type="Share",
            account_ids_to_add=[self.direct_share_account_id],
        )
        print("Direct sharing applied successfully.")

        print("\nVerifying permissions with DescribeDocumentPermission...")
        permissions = self.ssm_wrapper.describe_document_permission(
            name=self.direct_document_name, permission_type="Share"
        )
        account_ids = permissions.get("AccountIds", list())
        print(f"Document is now shared with accounts: {account_ids}")

        q.ask("\nPress Enter to continue...")
    def _step6_list_documents(self) -> None:
        """
        Step 6: List documents to confirm visibility.
        """
        print("\n--- Step 6: List Documents to Confirm Visibility ---\n")

        filters = [{"Key": "Owner", "Values": ["Self"]}]
        documents = self.ssm_wrapper.list_documents(filters=filters)

        found = False
        print("Your SSM documents (owned by Self):")
        for doc in documents:
            doc_name = doc.get("Name", "")
            doc_type = doc.get("DocumentType", "")
            doc_owner = doc.get("Owner", "")
            if doc_name == self.document_name:
                print(f"  *** {doc_name} (Type: {doc_type}, Owner: {doc_owner}) ***")
                found = True
            else:
                print(f"      {doc_name} (Type: {doc_type}, Owner: {doc_owner})")

        if found:
            print(f"\nDocument '{self.document_name}' confirmed in list.")
        else:
            print(f"\nWarning: Document '{self.document_name}' not found in list.")

        q.ask("\nPress Enter to continue...")

    def _cleanup(self) -> None:
        """
        Cleans up all resources in the correct dependency order.
        """
        print("\n--- Cleanup ---\n")

        # 1. Remove additional OU from resource share (if added)
        if self.second_ou_arn and self.resource_share_arn:
            print(f"Removing OU '{self.second_ou_arn}' from resource share...")
            try:
                associations = self.ram_wrapper.disassociate_resource_share(
                    resource_share_arn=self.resource_share_arn,
                    principals=[self.second_ou_arn],
                )
                for assoc in associations:
                    print(f"  Disassociation status: {assoc.get('status')}")
                time.sleep(3)
            except ClientError as err:
                logger.error("Error disassociating OU: %s", err)

        # 2. Delete the RAM resource share
        if self.resource_share_arn:
            print(f"\nDeleting RAM resource share '{self.resource_share_arn}'...")
            try:
                success = self.ram_wrapper.delete_resource_share(self.resource_share_arn)
                print(f"  Deletion successful: {success}")
                time.sleep(3)
            except ClientError as err:
                logger.error("Error deleting resource share: %s", err)

        # 3. Remove direct account-level sharing and delete the second document
        if self.direct_document_name:
            if self.direct_share_account_id:
                print(
                    f"\nRemoving direct sharing of '{self.direct_document_name}' "
                    f"with account '{self.direct_share_account_id}'..."
                )
                try:
                    self.ssm_wrapper.modify_document_permission(
                        name=self.direct_document_name,
                        permission_type="Share",
                        account_ids_to_remove=[self.direct_share_account_id],
                    )
                    print("  Direct sharing removed.")
                except ClientError as err:
                    logger.error("Error removing direct sharing: %s", err)
            print(f"\nDeleting SSM document '{self.direct_document_name}'...")
            try:
                self.ssm_wrapper.delete_document(self.direct_document_name)
                print("  Document deleted successfully.")
            except ClientError as err:
                logger.error("Error deleting document: %s", err)

        # 4. Delete the SSM document
        if self.document_name:
            print(f"\nDeleting SSM document '{self.document_name}'...")
            try:
                self.ssm_wrapper.delete_document(self.document_name)
                print("  Document deleted successfully.")
            except ClientError as err:
                logger.error("Error deleting document: %s", err)

        # 5. Delete the stack that created the organization resources (if any)
        if self.stack_name:
            contents = (
                "the OUs and the organization"
                if self.created_organization
                else "the OUs"
            )
            print(
                f"\nDeleting stack '{self.stack_name}', which removes {contents}. "
                "This can take a few minutes..."
            )
            try:
                self.org_setup.delete_stack(self.stack_name)
                print("  Stack deleted successfully.")
            except (ClientError, WaiterError) as err:
                logger.error(
                    "Error deleting stack %s. Delete it manually in the "
                    "AWS CloudFormation console: %s",
                    self.stack_name,
                    err,
                )

        print("\nCleanup complete.")


# snippet-end:[python.example_code.ssm.ShareDocumentsScenario]


def main() -> None:
    """
    Entry point for the SSM Document Sharing scenario.
    """
    logging.basicConfig(level=logging.INFO, format="%(levelname)s: %(message)s")

    ssm_wrapper = SsmDocumentWrapper.from_client()
    ram_wrapper = RamWrapper.from_client()
    sts_client = boto3.client("sts")

    scenario = ShareSsmDocumentsScenario(
        ssm_wrapper, ram_wrapper, sts_client, OrganizationSetup.from_client()
    )
    scenario.run_scenario()


if __name__ == "__main__":
    main()
