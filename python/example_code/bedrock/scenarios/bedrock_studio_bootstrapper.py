# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

import json
import logging

from dataclasses import dataclass, field

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s %(levelname)s: %(message)s",
    datefmt="%Y-%m-%d %H:%M:%S",
)
logger = logging.getLogger(__name__)

try:
    import boto3
    from botocore.exceptions import NoCredentialsError
except ModuleNotFoundError:
    logger.error(
        "The required library 'boto3' is not installed. "
        "Please install it using `pip install boto3`"
    )
    exit(1)


def how_to_use_the_bootstrapper():
    """
    Demonstrates how to use the Bedrock Studio bootstrapper.

    This function shows examples of how to initialize and run the bootstrapper with default
    and custom configurations.

    By default, the bootstrapper creates a provisioning role, a service role, and a set of
    permission boundaries.

    Optionally, you can enable the creation of a KMS key and an OpenSearch encryption policy
    by providing a custom configuration.

    Examples:
    1. Default configuration:
       The default configuration creates the provisioning role, service role, and permission
       boundaries with default names.

       To use the default configuration, simply initialize the bootstrapper without providing
       a configuration object.

       default_bootstrapper = BedrockStudioBootstrapper(region_name="us-east-1")
       default_bootstrapper.run()

    2. Custom configuration:
       To customize the configuration, create a BootstrapConfiguration object and set the desired
       properties. You can specify custom names for the provisioning and service roles, enable the
       creation of a KMS key, and configure the OpenSearch encryption policy.

       custom_configuration = BootstrapConfiguration(
           provisioning_role_name="NEW_PROVISIONING_ROLE_NAME",
           service_role_name="NEW_SERVICE_ROLE_NAME",
           kms_config=KmsConfiguration(
               enabled=True,
               key_alias="NEW_KEY_ALIAS"
           ),
           opensearch_config=OpenSearchConfiguration(
               enabled=True,
               domain_id="FIRST_SEVEN_DIGITS_OF_YOUR_OPENSEARCH_DOMAIN"
           )
       )

       custom_bootstrapper = BedrockStudioBootstrapper(
           region_name="us-east-1",
           config=custom_configuration
       )

       custom_bootstrapper.run()
    """

    region = "us-east-1"

    # Initialize the Bedrock Studio Bootstrapper with the default configuration.
    default_bootstrapper = BedrockStudioBootstrapper(region=region)

    # Run the bootstrapper with default configuration.
    # This will create the provisioning role, service role, and permission boundaries with default names
    default_bootstrapper.run()

    # # Alternatively, you can define your own custom configuration
    # custom_configuration = BootstrapConfiguration(
    #     # Set custom names for the provisioning and service roles
    #     provisioning_role_name="CustomProvisioningRoleName",
    #     service_role_name="CustomServiceRoleName",
    #
    #     # Enable the creation of a KMS key with a custom alias
    #     kms_config=KmsConfiguration(
    #         enabled=True,
    #         key_alias="CustomKmsKeyAlias"
    #     ),
    #
    #     # Enable the creation of an OpenSearch encryption policy with a custom domain ID
    #     opensearch_config=OpenSearchConfiguration(
    #         enabled=True,
    #         domain_id="1234567"  # Replace with the first seven digits of your OpenSearch domain ID
    #     )
    # )
    # # #
    # # # Initialize the Bedrock Studio Bootstrapper with the custom configuration
    # custom_bootstrapper = BedrockStudioBootstrapper(
    #     region=region,
    #     config=custom_configuration
    # )
    # #
    # # Run the bootstrapper with custom configuration This will create the provisioning role, service role,
    # # permission boundaries, KMS key, and OpenSearch encryption policy based on the custom configuration
    # custom_bootstrapper.run()


@dataclass
class KmsConfiguration:
    """
    Configuration for the KMS key used by Bedrock Studio.
    """

    enabled: bool = False
    key_alias: str = "bedrock-studio-key"


@dataclass
class OpenSearchConfiguration:
    """
    Configuration for the OpenSearch domain used by Bedrock Studio.
    """

    enabled: bool = False
    domain_id: str = None


@dataclass
class BootstrapConfiguration:
    """
    Configuration for the Bedrock Studio bootstrapper.
    """

    provisioning_role_name: str = "DataZoneBedrockProvisioningRole"
    service_role_name: str = "DataZoneBedrockServiceRole"
    kms_config: KmsConfiguration = field(default_factory=KmsConfiguration)
    opensearch_config: OpenSearchConfiguration = field(
        default_factory=OpenSearchConfiguration
    )


class BedrockStudioBootstrapper:
    """
    Bootstrapper for setting up roles and resources for Bedrock Studio.
    """

    def __init__(self, region="us-east-1", config=None):
        logger.info("=" * 54)
        logger.info("Preparing the Bootstrapper for Bedrock Studio")
        logger.info("=" * 54)

        self._initialize_session(region)

        self._process_configuration(config)

        self._kms_key_arn = None
        self._iam_client = self._session.client("iam")
        self._permission_boundary_policy_name = (
            "AmazonDataZoneBedrockPermissionsBoundary"
        )

    def run(self):
        logger.info("=" * 80)
        logger.info("Running Bootstrapper for Bedrock Studio ...")

        # Provision the minimum requirements
        self._create_provisioning_role()
        self._create_service_role()
        self._create_permission_boundary()

        # Provision optional resources according to the configuration
        if self._config.kms_config.enabled:
            self._create_kms_key()

        if self._config.opensearch_config.enabled:
            self._create_opensearch_encryption_policy()

        logger.info("=" * 80)
        logger.info("All resources have been created.")

    def _initialize_session(self, region):
        logger.info("Initializing AWS session...")
        logger.info("-" * 80)

        self._region = region
        self._session = boto3.Session(region_name=self._region)

        try:
            caller_identity = self._session.client("sts").get_caller_identity()

            self._account_id = caller_identity["Account"]

            caller_arn = caller_identity.get("Arn")
            caller_arn = (
                caller_arn.rsplit("/", 1)[0]
                if caller_arn.count("/") > 1
                else caller_arn
            )
            caller_arn = caller_arn.replace("assumed-role", "role")
            self._current_principal_arn = caller_arn

            logger.info("Account ID: " + self._account_id)
            logger.info("AWS Region: " + self._region)
            logger.info("User/Role ARN: " + self._current_principal_arn)

        except NoCredentialsError:
            logger.error(
                "No AWS credentials available. "
                "Please refer to the Boto3 documentation for setup instructions: "
                "https://boto3.amazonaws.com/v1/documentation/api/latest/guide/quickstart.html#configuration"
            )
            exit(1)

    def _process_configuration(self, config):
        logger.info("=" * 54)

        if config is not None:
            logger.info("Using custom configuration:")
            self._validate_custom_configuration(config)
            self._config = config
        else:
            logger.info("Using default configuration:")
            self._config = BootstrapConfiguration()

        logger.info("-" * 54)
        logger.info(f"- Provisioning role name: {self._config.provisioning_role_name}")
        logger.info(f"- Service role name: {self._config.service_role_name}")

        if self._config.kms_config is not None and self._config.kms_config.enabled:
            logger.info(
                f"- KMS key configuration: Enabled. KMS key alias: '{self._config.kms_config.key_alias}'"
            )

        if (
            self._config.opensearch_config is not None
            and self._config.opensearch_config.enabled
        ):
            logger.info(
                f"- OpenSearch configuration: Enabled. OpenSearch domain ID: '{self._config.opensearch_config.domain_id}'"
            )

    @staticmethod
    def _validate_custom_configuration(config):
        if not bool(config.provisioning_role_name):
            logger.error(
                "The provisioning role name is not defined. Please provide a valid provisioning role name."
            )
            exit(1)

        if not bool(config.service_role_name):
            logger.error(
                "The service role name is not defined. Please provide a valid service role name."
            )
            exit(1)

        kms_config = config.kms_config
        if (
            kms_config is not None
            and kms_config.enabled
            and not bool(kms_config.key_alias)
        ):
            logger.error(
                "The KMS alias is not defined. Please provide a valid alias name."
            )
            exit(1)

        opensearch_config = config.opensearch_config
        if (
            opensearch_config is not None
            and opensearch_config.enabled
            and (
                not bool(opensearch_config.domain_id)
                or len(opensearch_config.domain_id) != 7
            )
        ):
            logger.error(
                "Invalid OpenSearch domain ID. Please use the first seven digits of the domain ID. "
                "(Example: For dzd_123456789 this is 1234567)"
            )
            exit(1)

    def _create_provisioning_role(self):
        logger.info("=" * 54)
        logger.info("Step 1: Create Provisioning Role.")
        logger.info("-" * 54)

        self._create_role(
            self._config.provisioning_role_name,
            self._get_provisioning_role_trust_policy(),
            self._get_provisioning_role_policy(),
        )

    def _create_service_role(self):
        logger.info("=" * 54)
        logger.info("Step 2: Create Service Role.")
        logger.info("-" * 54)

        self._create_role(
            self._config.service_role_name,
            self._get_service_role_trust_policy(),
            self._get_service_role_policy(),
        )

    def _create_permission_boundary(self):
        logger.info("=" * 80)
        logger.info("Step 3: Create Permission Boundary Policy.")
        logger.info("-" * 80)

        self._create_policy(
            self._permission_boundary_policy_name, self._get_permission_boundary()
        )

    def _create_role(self, role_name, trust_policy, role_policy):
        inline_policy_name = "InlinePolicy"

        logger.info(f"Creating role: '{role_name}'...")
        try:
            response = self._iam_client.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=trust_policy
            )
            role_arn = response["Role"]["Arn"]
            logger.info(f"Role created: {role_arn}")
        except self._iam_client.exceptions.EntityAlreadyExistsException:
            logger.warning(f"Role with name '{role_name}' already exists.")
            try:
                self._iam_client.get_role_policy(
                    RoleName=role_name,
                    PolicyName=inline_policy_name,
                )
                confirm_input = input(
                    f"Proceed to replace the existing inline policy named '{inline_policy_name}'?"
                    " (yes/no, default: yes) "
                ).lower()
                if not (confirm_input in ["", "y" "yes"]):
                    logger.warning(f"Not updating existing '{role_name}' role.")
                    return
            except self._iam_client.exceptions.NoSuchEntityException:
                pass

        logger.info(f"Attaching inline policy to '{role_name}'...")
        self._iam_client.put_role_policy(
            RoleName=role_name,
            PolicyName=inline_policy_name,
            PolicyDocument=role_policy,
        )
        logger.info(f"Successfully attached inline policy to '{role_name}'.")

    def _create_policy(self, policy_name, policy_document):
        policy_arn = f"arn:aws:iam::{self._account_id}:policy/{self._permission_boundary_policy_name}"

        logger.info(f"Creating policy: '{policy_name}'...")
        try:
            self._iam_client.create_policy(
                PolicyName=policy_name,
                PolicyDocument=policy_document,
            )
            logger.info(f"Policy created: {policy_arn}")
        except self._iam_client.exceptions.EntityAlreadyExistsException:
            logger.info(f"Policy with name '{policy_name}' already exists.")

            policy_versions = self._iam_client.list_policy_versions(
                PolicyArn=policy_arn
            )["Versions"]
            if len(policy_versions) >= 5:
                logger.warning(
                    f"Cannot create more than 5 versions of '{policy_name}' policy."
                )
                sorted_policy_versions = sorted(
                    policy_versions, key=lambda x: x["CreateDate"]
                )
                oldest_non_default_version_id = next(
                    filter(lambda x: not x["IsDefaultVersion"], sorted_policy_versions)
                )["VersionId"]
                confirm_input = input(
                    f"Proceed to delete the oldest non-default version '{oldest_non_default_version_id}'?"
                    " (yes/no, default: yes) "
                ).lower()
                if confirm_input in ["", "y" "yes"]:
                    logger.info(
                        f"Deleting '{oldest_non_default_version_id}' version of '{policy_name}' policy..."
                    )
                    self._iam_client.delete_policy_version(
                        PolicyArn=policy_arn,
                        VersionId=oldest_non_default_version_id,
                    )
                else:
                    logger.warning(f"Not updating existing '{policy_name}' policy.")
                    return

            logger.info(
                f"Creating new default version of existing '{policy_name}' policy..."
            )
            self._iam_client.create_policy_version(
                PolicyArn=policy_arn,
                PolicyDocument=policy_document,
                SetAsDefault=True,
            )
            logger.info(f"Successfully updated '{policy_name}' policy.")

    def _create_kms_key(self):
        logger.info("=" * 80)
        logger.info("Custom Step: Create KMS Key.")
        logger.info("-" * 80)

        kms_client = self._session.client("kms")
        key_alias = self._config.kms_config.key_alias

        try:
            logger.info("Checking for existing alias ...")
            existing_aliases = kms_client.list_aliases()["Aliases"]

            alias_with_path = f"alias/{key_alias}"

            if alias_with_path in [alias["AliasName"] for alias in existing_aliases]:
                logger.info(
                    f"KMS key with alias '{key_alias}' already exists. Skipping key creation."
                )
                self._kms_key_arn = kms_client.describe_key(KeyId=alias_with_path)[
                    "KeyMetadata"
                ]["Arn"]
                return

            logger.info("Creating KMS key ...")
            response = kms_client.create_key(
                Policy=self._get_kms_key_policy(),
                Description="KMS key for Amazon Bedrock Studio",
                KeyUsage="ENCRYPT_DECRYPT",
            )
            key_id = response["KeyMetadata"]["KeyId"]
            self._kms_key_arn = response["KeyMetadata"]["Arn"]
            logger.info(f"KMS key created: {self._kms_key_arn}")
        except Exception as e:
            logger.error(f"Couldn't create KMS key: {e}")
            return

        logger.info(f"Attaching alias '{key_alias}'...")
        try:
            kms_client.create_alias(AliasName=f"alias/{key_alias}", TargetKeyId=key_id)
            logger.info(f"Alias attached to key '{key_id}'.")
        except kms_client.exceptions.AlreadyExistsException:
            logger.warning(f"Alias '{key_alias}' already exists")
        except Exception as e:
            logger.error(f"Couldn't create KMS key alias: {e}")
            return

    def _create_opensearch_encryption_policy(self):
        logger.info("=" * 80)
        logger.info("Custom Step: Create OpenSearch Encryption Policy.")
        logger.info("-" * 80)

        domain_seven_char = self._config.opensearch_config.domain_id
        policy_name = f"br-studio-{domain_seven_char}"

        logger.info(f"Creating OpenSearch encryption policy: '{policy_name}'...")

        policy = {
            "Rules": [
                {
                    "ResourceType": "collection",
                    "Resource": [f"collection/br-studio-{domain_seven_char}*"],
                }
            ]
        }

        if self._kms_key_arn is not None:
            policy["KmsARN"] = self._kms_key_arn
            policy["AWSOwnedKey"] = False
        else:
            policy["AWSOwnedKey"] = True

        opensearch_client = self._session.client("opensearchserverless")

        try:
            response = opensearch_client.create_security_policy(
                name=policy_name,
                type="encryption",
                policy=json.dumps(policy),
            )
            policy_name = response["securityPolicyDetail"]["name"]
            logger.info(f"Security policy created: {policy_name}")
        except opensearch_client.exceptions.ConflictException:
            logger.warning(f"Security policy with name '{policy_name}' already exists")
        except Exception as e:
            logger.error(f"Couldn't create security policy: {e}")

    def _get_provisioning_role_trust_policy(self):
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"Service": "datazone.amazonaws.com"},
                        "Action": "sts:AssumeRole",
                        "Condition": {
                            "StringEquals": {"aws:SourceAccount": self._account_id}
                        },
                    }
                ],
            }
        )

    def _get_provisioning_role_policy(self):
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "CreateStacks",
                        "Effect": "Allow",
                        "Action": [
                            "cloudformation:CreateStack",
                            "cloudformation:TagResource",
                        ],
                        "Resource": "arn:aws:cloudformation:*:*:stack/DataZone*",
                        "Condition": {
                            "ForAnyValue:StringEquals": {
                                "aws:TagKeys": "AmazonDataZoneEnvironment"
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "ManageStacks",
                        "Effect": "Allow",
                        "Action": [
                            "cloudformation:DescribeStacks",
                            "cloudformation:DescribeStackEvents",
                            "cloudformation:UpdateStack",
                            "cloudformation:DeleteStack",
                        ],
                        "Resource": "arn:aws:cloudformation:*:*:stack/DataZone*",
                    },
                    {
                        "Sid": "DenyOtherActionsNotViaCloudFormation",
                        "Effect": "Deny",
                        "NotAction": [
                            "cloudformation:DescribeStacks",
                            "cloudformation:DescribeStackEvents",
                            "cloudformation:CreateStack",
                            "cloudformation:UpdateStack",
                            "cloudformation:DeleteStack",
                            "cloudformation:TagResource",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringNotEqualsIfExists": {
                                "aws:CalledViaFirst": "cloudformation.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "ListResources",
                        "Effect": "Allow",
                        "Action": [
                            "iam:ListRoles",
                            "s3:ListAllMyBuckets",
                            "aoss:ListCollections",
                            "aoss:BatchGetCollection",
                            "aoss:ListAccessPolicies",
                            "aoss:ListSecurityPolicies",
                            "aoss:ListTagsForResource",
                            "bedrock:ListAgents",
                            "bedrock:ListKnowledgeBases",
                            "bedrock:ListGuardrails",
                            "bedrock:ListPrompts",
                            "bedrock:ListFlows",
                            "bedrock:ListTagsForResource",
                            "lambda:ListFunctions",
                            "logs:DescribeLogGroups",
                            "secretsmanager:ListSecrets",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "GetRoles",
                        "Effect": "Allow",
                        "Action": "iam:GetRole",
                        "Resource": [
                            "arn:aws:iam::*:role/DataZoneBedrockProject*",
                            "arn:aws:iam::*:role/AmazonBedrockExecution*",
                            "arn:aws:iam::*:role/BedrockStudio*",
                        ],
                    },
                    {
                        "Sid": "CreateRoles",
                        "Effect": "Allow",
                        "Action": [
                            "iam:CreateRole",
                            "iam:PutRolePolicy",
                            "iam:AttachRolePolicy",
                            "iam:DeleteRolePolicy",
                            "iam:DetachRolePolicy",
                        ],
                        "Resource": [
                            "arn:aws:iam::*:role/DataZoneBedrockProject*",
                            "arn:aws:iam::*:role/AmazonBedrockExecution*",
                            "arn:aws:iam::*:role/BedrockStudio*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "ManageRoles",
                        "Effect": "Allow",
                        "Action": [
                            "iam:UpdateRole",
                            "iam:DeleteRole",
                            "iam:ListRolePolicies",
                            "iam:GetRolePolicy",
                            "iam:ListAttachedRolePolicies",
                        ],
                        "Resource": [
                            "arn:aws:iam::*:role/DataZoneBedrockProject*",
                            "arn:aws:iam::*:role/AmazonBedrockExecution*",
                            "arn:aws:iam::*:role/BedrockStudio*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "PassRoleToBedrockService",
                        "Effect": "Allow",
                        "Action": "iam:PassRole",
                        "Resource": [
                            "arn:aws:iam::*:role/AmazonBedrockExecution*",
                            "arn:aws:iam::*:role/BedrockStudio*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "iam:PassedToService": "bedrock.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "PassRoleToLambdaService",
                        "Effect": "Allow",
                        "Action": "iam:PassRole",
                        "Resource": "arn:aws:iam::*:role/BedrockStudio*",
                        "Condition": {
                            "StringEquals": {
                                "iam:PassedToService": "lambda.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "CreateRoleForOpenSearchServerless",
                        "Effect": "Allow",
                        "Action": "iam:CreateServiceLinkedRole",
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "iam:AWSServiceName": "observability.aoss.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "GetDataZoneBlueprintCfnTemplates",
                        "Effect": "Allow",
                        "Action": "s3:GetObject",
                        "Resource": "*",
                        "Condition": {
                            "StringNotEquals": {
                                "s3:ResourceAccount": "${aws:PrincipalAccount}"
                            }
                        },
                    },
                    {
                        "Sid": "CreateAndAccessS3Buckets",
                        "Effect": "Allow",
                        "Action": [
                            "s3:CreateBucket",
                            "s3:DeleteBucket",
                            "s3:GetBucketPolicy",
                            "s3:PutBucketPolicy",
                            "s3:DeleteBucketPolicy",
                            "s3:PutBucketTagging",
                            "s3:PutBucketCORS",
                            "s3:PutBucketLogging",
                            "s3:PutBucketVersioning",
                            "s3:PutBucketPublicAccessBlock",
                            "s3:PutEncryptionConfiguration",
                            "s3:PutLifecycleConfiguration",
                            "s3:GetObject",
                            "s3:GetObjectVersion",
                        ],
                        "Resource": "arn:aws:s3:::br-studio-*",
                    },
                    {
                        "Sid": "ManageOssAccessPolicies",
                        "Effect": "Allow",
                        "Action": [
                            "aoss:GetAccessPolicy",
                            "aoss:CreateAccessPolicy",
                            "aoss:DeleteAccessPolicy",
                            "aoss:UpdateAccessPolicy",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringLikeIfExists": {
                                "aoss:collection": "br-studio-*",
                                "aoss:index": "br-studio-*",
                            }
                        },
                    },
                    {
                        "Sid": "ManageOssSecurityPolicies",
                        "Effect": "Allow",
                        "Action": [
                            "aoss:GetSecurityPolicy",
                            "aoss:CreateSecurityPolicy",
                            "aoss:DeleteSecurityPolicy",
                            "aoss:UpdateSecurityPolicy",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringLikeIfExists": {"aoss:collection": "br-studio-*"}
                        },
                    },
                    {
                        "Sid": "ManageOssCollections",
                        "Effect": "Allow",
                        "Action": [
                            "aoss:CreateCollection",
                            "aoss:UpdateCollection",
                            "aoss:DeleteCollection",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "GetBedrockResources",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:GetAgent",
                            "bedrock:GetKnowledgeBase",
                            "bedrock:GetGuardrail",
                            "bedrock:GetPrompt",
                            "bedrock:GetFlow",
                            "bedrock:GetFlowAlias",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "ManageBedrockResources",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:CreateAgent",
                            "bedrock:UpdateAgent",
                            "bedrock:PrepareAgent",
                            "bedrock:DeleteAgent",
                            "bedrock:ListAgentAliases",
                            "bedrock:GetAgentAlias",
                            "bedrock:CreateAgentAlias",
                            "bedrock:UpdateAgentAlias",
                            "bedrock:DeleteAgentAlias",
                            "bedrock:ListAgentActionGroups",
                            "bedrock:GetAgentActionGroup",
                            "bedrock:CreateAgentActionGroup",
                            "bedrock:UpdateAgentActionGroup",
                            "bedrock:DeleteAgentActionGroup",
                            "bedrock:ListAgentKnowledgeBases",
                            "bedrock:GetAgentKnowledgeBase",
                            "bedrock:AssociateAgentKnowledgeBase",
                            "bedrock:DisassociateAgentKnowledgeBase",
                            "bedrock:UpdateAgentKnowledgeBase",
                            "bedrock:CreateKnowledgeBase",
                            "bedrock:UpdateKnowledgeBase",
                            "bedrock:DeleteKnowledgeBase",
                            "bedrock:ListDataSources",
                            "bedrock:GetDataSource",
                            "bedrock:CreateDataSource",
                            "bedrock:UpdateDataSource",
                            "bedrock:DeleteDataSource",
                            "bedrock:CreateGuardrail",
                            "bedrock:UpdateGuardrail",
                            "bedrock:DeleteGuardrail",
                            "bedrock:CreateGuardrailVersion",
                            "bedrock:CreatePrompt",
                            "bedrock:UpdatePrompt",
                            "bedrock:DeletePrompt",
                            "bedrock:CreatePromptVersion",
                            "bedrock:CreateFlow",
                            "bedrock:UpdateFlow",
                            "bedrock:PrepareFlow",
                            "bedrock:DeleteFlow",
                            "bedrock:ListFlowAliases",
                            "bedrock:GetFlowAlias",
                            "bedrock:CreateFlowAlias",
                            "bedrock:UpdateFlowAlias",
                            "bedrock:DeleteFlowAlias",
                            "bedrock:ListFlowVersions",
                            "bedrock:GetFlowVersion",
                            "bedrock:CreateFlowVersion",
                            "bedrock:DeleteFlowVersion",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "TagBedrockAgentAliases",
                        "Effect": "Allow",
                        "Action": "bedrock:TagResource",
                        "Resource": "arn:aws:bedrock:*:*:agent-alias/*",
                        "Condition": {
                            "StringEquals": {
                                "aws:RequestTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "TagBedrockFlowAliases",
                        "Effect": "Allow",
                        "Action": "bedrock:TagResource",
                        "Resource": "arn:aws:bedrock:*:*:flow/*/alias/*",
                        "Condition": {
                            "Null": {
                                "aws:RequestTag/AmazonDataZoneEnvironment": "false"
                            }
                        },
                    },
                    {
                        "Sid": "CreateFunctions",
                        "Effect": "Allow",
                        "Action": [
                            "lambda:GetFunction",
                            "lambda:CreateFunction",
                            "lambda:InvokeFunction",
                            "lambda:DeleteFunction",
                            "lambda:UpdateFunctionCode",
                            "lambda:GetFunctionConfiguration",
                            "lambda:UpdateFunctionConfiguration",
                            "lambda:ListVersionsByFunction",
                            "lambda:PublishVersion",
                            "lambda:GetPolicy",
                            "lambda:AddPermission",
                            "lambda:RemovePermission",
                            "lambda:ListTags",
                        ],
                        "Resource": "arn:aws:lambda:*:*:function:br-studio-*",
                    },
                    {
                        "Sid": "ManageLogGroups",
                        "Effect": "Allow",
                        "Action": [
                            "logs:CreateLogGroup",
                            "logs:DeleteLogGroup",
                            "logs:PutRetentionPolicy",
                            "logs:DeleteRetentionPolicy",
                            "logs:GetDataProtectionPolicy",
                            "logs:PutDataProtectionPolicy",
                            "logs:DeleteDataProtectionPolicy",
                            "logs:AssociateKmsKey",
                            "logs:DisassociateKmsKey",
                            "logs:ListTagsLogGroup",
                            "logs:ListTagsForResource",
                        ],
                        "Resource": "arn:aws:logs:*:*:log-group:/aws/lambda/br-studio-*",
                    },
                    {
                        "Sid": "GetRandomPasswordForSecret",
                        "Effect": "Allow",
                        "Action": "secretsmanager:GetRandomPassword",
                        "Resource": "*",
                    },
                    {
                        "Sid": "ManageSecrets",
                        "Effect": "Allow",
                        "Action": [
                            "secretsmanager:CreateSecret",
                            "secretsmanager:DescribeSecret",
                            "secretsmanager:UpdateSecret",
                            "secretsmanager:DeleteSecret",
                            "secretsmanager:GetResourcePolicy",
                            "secretsmanager:PutResourcePolicy",
                            "secretsmanager:DeleteResourcePolicy",
                        ],
                        "Resource": "arn:aws:secretsmanager:*:*:secret:br-studio/*",
                    },
                    {
                        "Sid": "UseCustomerManagedKmsKey",
                        "Effect": "Allow",
                        "Action": [
                            "kms:DescribeKey",
                            "kms:Encrypt",
                            "kms:Decrypt",
                            "kms:GenerateDataKey",
                            "kms:CreateGrant",
                            "kms:RetireGrant",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {"aws:ResourceTag/EnableBedrock": "true"}
                        },
                    },
                    {
                        "Sid": "TagResources",
                        "Effect": "Allow",
                        "Action": [
                            "iam:TagRole",
                            "iam:UntagRole",
                            "aoss:TagResource",
                            "aoss:UntagResource",
                            "bedrock:TagResource",
                            "bedrock:UntagResource",
                            "lambda:TagResource",
                            "lambda:UntagResource",
                            "logs:TagLogGroup",
                            "logs:UntagLogGroup",
                            "logs:TagResource",
                            "logs:UntagResource",
                            "secretsmanager:TagResource",
                            "secretsmanager:UntagResource",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                ],
            }
        )

    def _get_service_role_trust_policy(self):
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {"Service": "datazone.amazonaws.com"},
                        "Action": ["sts:AssumeRole", "sts:TagSession"],
                        "Condition": {
                            "StringEquals": {"aws:SourceAccount": self._account_id},
                            "ForAllValues:StringLike": {"aws:TagKeys": "datazone*"},
                        },
                    }
                ],
            }
        )

    def _get_service_role_policy(self):
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "GetDataZoneDomain",
                        "Effect": "Allow",
                        "Action": "datazone:GetDomain",
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/AmazonBedrockManaged": "true"
                            }
                        },
                    },
                    {
                        "Sid": "ManageDataZoneResources",
                        "Effect": "Allow",
                        "Action": [
                            "datazone:ListProjects",
                            "datazone:GetProject",
                            "datazone:CreateProject",
                            "datazone:UpdateProject",
                            "datazone:DeleteProject",
                            "datazone:ListProjectMemberships",
                            "datazone:CreateProjectMembership",
                            "datazone:DeleteProjectMembership",
                            "datazone:ListEnvironments",
                            "datazone:GetEnvironment",
                            "datazone:CreateEnvironment",
                            "datazone:UpdateEnvironment",
                            "datazone:DeleteEnvironment",
                            "datazone:ListEnvironmentBlueprints",
                            "datazone:GetEnvironmentBlueprint",
                            "datazone:ListEnvironmentBlueprintConfigurations",
                            "datazone:GetEnvironmentBlueprintConfiguration",
                            "datazone:ListEnvironmentProfiles",
                            "datazone:GetEnvironmentProfile",
                            "datazone:CreateEnvironmentProfile",
                            "datazone:UpdateEnvironmentProfile",
                            "datazone:DeleteEnvironmentProfile",
                            "datazone:GetEnvironmentCredentials",
                            "datazone:ListGroupsForUser",
                            "datazone:SearchUserProfiles",
                            "datazone:SearchGroupProfiles",
                            "datazone:GetUserProfile",
                            "datazone:GetGroupProfile",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "GetResourceShareAssociations",
                        "Effect": "Allow",
                        "Action": "ram:GetResourceShareAssociations",
                        "Resource": "*",
                    },
                    {
                        "Sid": "InvokeBedrockModels",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:GetFoundationModelAvailability",
                            "bedrock:InvokeModel",
                            "bedrock:InvokeModelWithResponseStream",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "UseCustomerManagedKmsKey",
                        "Effect": "Allow",
                        "Action": [
                            "kms:DescribeKey",
                            "kms:GenerateDataKey",
                            "kms:Decrypt",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {"aws:ResourceTag/EnableBedrock": "true"}
                        },
                    },
                ],
            }
        )

    def _get_kms_key_policy(self):
        account_id = self._account_id
        region = self._region
        provisioning_role_name = self._config.provisioning_role_name
        admin_arn = self._current_principal_arn
        logger.info(admin_arn)

        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "Enable IAM User Permissions Based on Tags",
                        "Effect": "Allow",
                        "Principal": {"AWS": "*"},
                        "Action": [
                            "kms:Decrypt",
                            "kms:GenerateDataKey",
                            "kms:GenerateDataKeyPair",
                            "kms:GenerateDataKeyPairWithoutPlaintext",
                            "kms:GenerateDataKeyWithoutPlaintext",
                            "kms:Encrypt",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:PrincipalTag/AmazonBedrockManaged": "true",
                                "kms:CallerAccount": account_id,
                            },
                            "StringLike": {
                                "aws:PrincipalTag/AmazonDataZoneEnvironment": "*"
                            },
                        },
                    },
                    {
                        "Sid": "Allow Amazon Bedrock to encrypt and decrypt Agent resources on behalf of authorized users",
                        "Effect": "Allow",
                        "Principal": {"Service": "bedrock.amazonaws.com"},
                        "Action": ["kms:GenerateDataKey", "kms:Decrypt"],
                        "Resource": "*",
                        "Condition": {
                            "StringLike": {
                                "kms:EncryptionContext:aws:bedrock:arn": f"arn:aws:bedrock:{region}:{account_id}:agent/*"
                            }
                        },
                    },
                    {
                        "Sid": "Allows AOSS list keys",
                        "Effect": "Allow",
                        "Principal": {"Service": "aoss.amazonaws.com"},
                        "Action": "kms:ListKeys",
                        "Resource": "*",
                    },
                    {
                        "Sid": "Allows AOSS to create grants",
                        "Effect": "Allow",
                        "Principal": {"Service": "aoss.amazonaws.com"},
                        "Action": ["kms:DescribeKey", "kms:CreateGrant"],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "kms:ViaService": f"aoss.{region}.amazonaws.com"
                            },
                            "Bool": {"kms:GrantIsForAWSResource": "true"},
                        },
                    },
                    {
                        "Sid": "Enable Decrypt, GenerateDataKey for DZ execution role",
                        "Effect": "Allow",
                        "Principal": {"AWS": f"arn:aws:iam::{account_id}:root"},
                        "Action": ["kms:Decrypt", "kms:GenerateDataKey"],
                        "Resource": "*",
                        "Condition": {
                            "StringLike": {
                                "kms:EncryptionContext:aws:datazone:domainId": "*"
                            }
                        },
                    },
                    {
                        "Sid": "Allow attachment of persistent resources",
                        "Effect": "Allow",
                        "Principal": {"Service": "bedrock.amazonaws.com"},
                        "Action": [
                            "kms:CreateGrant",
                            "kms:ListGrants",
                            "kms:RetireGrant",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringLike": {"kms:CallerAccount": account_id},
                            "Bool": {"kms:GrantIsForAWSResource": "true"},
                        },
                    },
                    {
                        "Sid": "Allow Permission For Encrypted Guardrails On Provisioning Role",
                        "Effect": "Allow",
                        "Principal": {
                            "AWS": f"arn:aws:iam::{account_id}:role/{provisioning_role_name}"
                        },
                        "Action": [
                            "kms:GenerateDataKey",
                            "kms:Decrypt",
                            "kms:DescribeKey",
                            "kms:CreateGrant",
                            "kms:Encrypt",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "Allow use of CMK to encrypt logs in their account",
                        "Effect": "Allow",
                        "Principal": {"Service": f"logs.{region}.amazonaws.com"},
                        "Action": [
                            "kms:Encrypt",
                            "kms:Decrypt",
                            "kms:ReEncryptFrom",
                            "kms:ReEncryptTo",
                            "kms:GenerateDataKey",
                            "kms:GenerateDataKeyPair",
                            "kms:GenerateDataKeyPairWithoutPlaintext",
                            "kms:GenerateDataKeyWithoutPlaintext",
                            "kms:DescribeKey",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "ArnLike": {
                                "kms:EncryptionContext:aws:logs:arn": f"arn:aws:logs:{region}:{account_id}:log-group:*"
                            }
                        },
                    },
                    {
                        "Sid": "Allow access for Key Administrators",
                        "Effect": "Allow",
                        "Principal": {"AWS": f"{admin_arn}"},
                        "Action": [
                            "kms:Create*",
                            "kms:Describe*",
                            "kms:Enable*",
                            "kms:List*",
                            "kms:Put*",
                            "kms:Update*",
                            "kms:Revoke*",
                            "kms:Disable*",
                            "kms:Get*",
                            "kms:Delete*",
                            "kms:TagResource",
                            "kms:UntagResource",
                            "kms:ScheduleKeyDeletion",
                            "kms:CancelKeyDeletion",
                        ],
                        "Resource": "*",
                    },
                ],
            }
        )

    def _get_permission_boundary(self):
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "AccessS3Buckets",
                        "Effect": "Allow",
                        "Action": [
                            "s3:ListBucket",
                            "s3:ListBucketVersions",
                            "s3:GetObject",
                            "s3:PutObject",
                            "s3:DeleteObject",
                            "s3:GetObjectVersion",
                            "s3:DeleteObjectVersion",
                        ],
                        "Resource": "arn:aws:s3:::br-studio-${aws:PrincipalAccount}-*",
                        "Condition": {
                            "StringEquals": {
                                "s3:ResourceAccount": "${aws:PrincipalAccount}"
                            }
                        },
                    },
                    {
                        "Sid": "AccessOssCollections",
                        "Effect": "Allow",
                        "Action": "aoss:APIAccessAll",
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}"
                            }
                        },
                    },
                    {
                        "Sid": "InvokeBedrockModels",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:InvokeModel",
                            "bedrock:InvokeModelWithResponseStream",
                            "bedrock:RetrieveAndGenerate",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "AccessBedrockResources",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:InvokeAgent",
                            "bedrock:Retrieve",
                            "bedrock:StartIngestionJob",
                            "bedrock:GetIngestionJob",
                            "bedrock:ListIngestionJobs",
                            "bedrock:ApplyGuardrail",
                            "bedrock:ListPrompts",
                            "bedrock:GetPrompt",
                            "bedrock:CreatePrompt",
                            "bedrock:DeletePrompt",
                            "bedrock:CreatePromptVersion",
                            "bedrock:InvokeFlow",
                            "bedrock:ListTagsForResource",
                            "bedrock:TagResource",
                            "bedrock:UntagResource",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                                "aws:ResourceTag/AmazonBedrockManaged": "true",
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "InvokeBedrockFlows",
                        "Effect": "Allow",
                        "Action": "bedrock:InvokeFlow",
                        "Resource": "arn:aws:bedrock:*:*:flow/*/alias/*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneProject": "false",
                            },
                        },
                    },
                    {
                        "Sid": "WriteLogs",
                        "Effect": "Allow",
                        "Action": [
                            "logs:CreateLogGroup",
                            "logs:CreateLogStream",
                            "logs:PutLogEvents",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                                "aws:ResourceTag/AmazonBedrockManaged": "true",
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "InvokeLambdaFunctions",
                        "Effect": "Allow",
                        "Action": "lambda:InvokeFunction",
                        "Resource": "arn:aws:lambda:*:*:function:br-studio-*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                                "aws:ResourceTag/AmazonBedrockManaged": "true",
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "AccessSecretsManagerSecrets",
                        "Effect": "Allow",
                        "Action": [
                            "secretsmanager:DescribeSecret",
                            "secretsmanager:GetSecretValue",
                            "secretsmanager:PutSecretValue",
                        ],
                        "Resource": "arn:aws:secretsmanager:*:*:secret:br-studio/*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                                "aws:ResourceTag/AmazonBedrockManaged": "true",
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "UseCustomerManagedKmsKey",
                        "Effect": "Allow",
                        "Action": ["kms:Decrypt", "kms:GenerateDataKey"],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}",
                                "aws:ResourceTag/EnableBedrock": "true",
                            }
                        },
                    },
                ],
            }
        )


if __name__ == "__main__":
    how_to_use_the_bootstrapper()
