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

    def _create_role(self, role_name, trust_policy, role_policy):
        logger.info(f"Creating role: '{role_name}'...")
        try:
            response = self._iam_client.create_role(
                RoleName=role_name, AssumeRolePolicyDocument=trust_policy
            )
            role_arn = response["Role"]["Arn"]
            logger.info(f"Role created: {role_arn}")
        except self._iam_client.exceptions.EntityAlreadyExistsException:
            logger.warning(f"Role with name '{role_name}' already exists.")

        logger.info(f"Attaching inline policy to '{role_name}'...")
        try:
            self._iam_client.put_role_policy(
                RoleName=role_name,
                PolicyName="InlinePolicy",
                PolicyDocument=role_policy,
            )
            logger.info(f"Inline policy successfully attached.")
        except self._iam_client.exceptions.EntityAlreadyExistsException:
            logger.warning("Inline policy already exists.")

    def _create_permission_boundary(self):
        logger.info("=" * 80)
        logger.info("Step 3: Create Permission Boundary.")
        logger.info("-" * 80)

        logger.info(
            f"Creating permission boundary: '{self._permission_boundary_policy_name}'..."
        )

        try:
            self._iam_client.create_policy(
                PolicyName=self._permission_boundary_policy_name,
                PolicyDocument=self._get_permission_boundary(),
            )
            logger.info(f"Permission boundary policy created.")
        except self._iam_client.exceptions.EntityAlreadyExistsException:
            logger.warning(
                f"Policy with name '{self._permission_boundary_policy_name}' already exists."
            )

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
                        "Principal": {"Service": ["datazone.amazonaws.com"]},
                        "Action": ["sts:AssumeRole"],
                        "Condition": {
                            "StringEquals": {"aws:SourceAccount": self._account_id}
                        },
                    }
                ],
            }
        )

    def _get_provisioning_role_policy(self):
        account_id = self._account_id
        region = self._region
        return json.dumps(
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Sid": "AmazonDataZonePermissionsToCreateEnvironmentRole",
                        "Effect": "Allow",
                        "Action": [
                            "iam:CreateRole",
                            "iam:GetRolePolicy",
                            "iam:DetachRolePolicy",
                            "iam:AttachRolePolicy",
                            "iam:UpdateAssumeRolePolicy",
                        ],
                        "Resource": "arn:aws:iam::*:role/DataZoneBedrockProjectRole*",
                        "Condition": {
                            "StringEquals": {
                                "iam:PermissionsBoundary": f"arn:aws:iam::{account_id}:policy/{self._permission_boundary_policy_name}",
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"],
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZonePermissionsToServiceRole",
                        "Effect": "Allow",
                        "Action": [
                            "iam:CreateRole",
                            "iam:GetRolePolicy",
                            "iam:DetachRolePolicy",
                            "iam:AttachRolePolicy",
                            "iam:UpdateAssumeRolePolicy",
                        ],
                        "Resource": [
                            "arn:aws:iam::*:role/BedrockStudio*",
                            "arn:aws:iam::*:role/AmazonBedrockExecution*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "IamPassRolePermissionsForBedrock",
                        "Effect": "Allow",
                        "Action": ["iam:PassRole"],
                        "Resource": "arn:aws:iam::*:role/AmazonBedrockExecution*",
                        "Condition": {
                            "StringEquals": {
                                "iam:PassedToService": ["bedrock.amazonaws.com"],
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"],
                            }
                        },
                    },
                    {
                        "Sid": "IamPassRolePermissionsForLambda",
                        "Effect": "Allow",
                        "Action": ["iam:PassRole"],
                        "Resource": ["arn:aws:iam::*:role/BedrockStudio*"],
                        "Condition": {
                            "StringEquals": {
                                "iam:PassedToService": ["lambda.amazonaws.com"],
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"],
                            }
                        },
                    },
                    {
                        "Sid": "AmazonDataZonePermissionsToManageCreatedEnvironmentRole",
                        "Effect": "Allow",
                        "Action": [
                            "iam:DeleteRole",
                            "iam:GetRole",
                            "iam:DetachRolePolicy",
                            "iam:GetPolicy",
                            "iam:DeleteRolePolicy",
                            "iam:PutRolePolicy",
                        ],
                        "Resource": [
                            "arn:aws:iam::*:role/DataZoneBedrockProjectRole*",
                            "arn:aws:iam::*:role/AmazonBedrock*",
                            "arn:aws:iam::*:role/BedrockStudio*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneCFStackCreationForEnvironments",
                        "Effect": "Allow",
                        "Action": [
                            "cloudformation:CreateStack",
                            "cloudformation:UpdateStack",
                            "cloudformation:TagResource",
                        ],
                        "Resource": ["arn:aws:cloudformation:*:*:stack/DataZone*"],
                        "Condition": {
                            "ForAnyValue:StringLike": {
                                "aws:TagKeys": "AmazonDataZoneEnvironment"
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneCFStackManagementForEnvironments",
                        "Effect": "Allow",
                        "Action": [
                            "cloudformation:DeleteStack",
                            "cloudformation:DescribeStacks",
                            "cloudformation:DescribeStackEvents",
                        ],
                        "Resource": ["arn:aws:cloudformation:*:*:stack/DataZone*"],
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentBedrockGetViaCloudformation",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:GetAgent",
                            "bedrock:GetAgentActionGroup",
                            "bedrock:GetAgentAlias",
                            "bedrock:GetAgentKnowledgeBase",
                            "bedrock:GetKnowledgeBase",
                            "bedrock:GetDataSource",
                            "bedrock:GetGuardrail",
                            "bedrock:DeleteGuardrail",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentBedrockAgentPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:CreateAgent",
                            "bedrock:UpdateAgent",
                            "bedrock:DeleteAgent",
                            "bedrock:ListAgents",
                            "bedrock:CreateAgentActionGroup",
                            "bedrock:UpdateAgentActionGroup",
                            "bedrock:DeleteAgentActionGroup",
                            "bedrock:ListAgentActionGroups",
                            "bedrock:CreateAgentAlias",
                            "bedrock:UpdateAgentAlias",
                            "bedrock:DeleteAgentAlias",
                            "bedrock:ListAgentAliases",
                            "bedrock:AssociateAgentKnowledgeBase",
                            "bedrock:DisassociateAgentKnowledgeBase",
                            "bedrock:UpdateAgentKnowledgeBase",
                            "bedrock:ListAgentKnowledgeBases",
                            "bedrock:PrepareAgent",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentOpenSearch",
                        "Effect": "Allow",
                        "Action": [
                            "aoss:CreateAccessPolicy",
                            "aoss:DeleteAccessPolicy",
                            "aoss:UpdateAccessPolicy",
                            "aoss:GetAccessPolicy",
                            "aoss:ListAccessPolicies",
                            "aoss:CreateSecurityPolicy",
                            "aoss:DeleteSecurityPolicy",
                            "aoss:UpdateSecurityPolicy",
                            "aoss:GetSecurityPolicy",
                            "aoss:ListSecurityPolicies",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentOpenSearchPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "aoss:UpdateCollection",
                            "aoss:DeleteCollection",
                            "aoss:BatchGetCollection",
                            "aoss:ListCollections",
                            "aoss:CreateCollection",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentBedrockKnowledgeBasePermissions",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:CreateKnowledgeBase",
                            "bedrock:UpdateKnowledgeBase",
                            "bedrock:DeleteKnowledgeBase",
                            "bedrock:CreateDataSource",
                            "bedrock:UpdateDataSource",
                            "bedrock:DeleteDataSource",
                            "bedrock:ListKnowledgeBases",
                            "bedrock:ListDataSources",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentBedrockGuardrailPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:CreateGuardrail",
                            "bedrock:CreateGuardrailVersion",
                            "bedrock:DeleteGuardrail",
                            "bedrock:ListGuardrails",
                            "bedrock:ListTagsForResource",
                            "bedrock:TagResource",
                            "bedrock:UntagResource",
                            "bedrock:UpdateGuardrail",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"},
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentLambdaPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "lambda:AddPermission",
                            "lambda:CreateFunction",
                            "lambda:ListFunctions",
                            "lambda:UpdateFunctionCode",
                            "lambda:UpdateFunctionConfiguration",
                            "lambda:InvokeFunction",
                            "lambda:ListVersionsByFunction",
                            "lambda:PublishVersion",
                        ],
                        "Resource": [
                            f"arn:aws:lambda:{region}:{account_id}:function:br-studio*",
                            f"arn:aws:lambda:{region}:{account_id}:function:OpensearchIndexLambda*",
                            f"arn:aws:lambda:{region}:{account_id}:function:IngestionTriggerLambda*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentLambdaManagePermissions",
                        "Effect": "Allow",
                        "Action": [
                            "lambda:GetFunction",
                            "lambda:DeleteFunction",
                            "lambda:RemovePermission",
                        ],
                        "Resource": [
                            f"arn:aws:lambda:{region}:{account_id}:function:br-studio*",
                            f"arn:aws:lambda:{region}:{account_id}:function:OpensearchIndexLambda*",
                            f"arn:aws:lambda:{region}:{account_id}:function:IngestionTriggerLambda*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "ManageLogGroups",
                        "Effect": "Allow",
                        "Action": [
                            "logs:CreateLogGroup",
                            "logs:PutRetentionPolicy",
                            "logs:DeleteLogGroup",
                        ],
                        "Resource": [
                            "arn:aws:logs:*:*:log-group:/aws/lambda/br-studio-*",
                            "arn:aws:logs:*:*:log-group:datazone-*",
                        ],
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": "cloudformation.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "ListTags",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:ListTagsForResource",
                            "aoss:ListTagsForResource",
                            "lambda:ListTags",
                            "iam:ListRoleTags",
                            "iam:ListPolicyTags",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": "cloudformation.amazonaws.com"
                            }
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentTagsCreationPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "iam:TagRole",
                            "iam:TagPolicy",
                            "iam:UntagRole",
                            "iam:UntagPolicy",
                            "logs:TagLogGroup",
                            "bedrock:TagResource",
                            "bedrock:UntagResource",
                            "bedrock:ListTagsForResource",
                            "aoss:TagResource",
                            "aoss:UnTagResource",
                            "aoss:ListTagsForResource",
                            "lambda:TagResource",
                            "lambda:UnTagResource",
                            "lambda:ListTags",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "ForAnyValue:StringLike": {
                                "aws:TagKeys": "AmazonDataZoneEnvironment"
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentBedrockTagResource",
                        "Effect": "Allow",
                        "Action": ["bedrock:TagResource"],
                        "Resource": f"arn:aws:bedrock:{region}:{account_id}:agent-alias/*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "ForAnyValue:StringLike": {
                                "aws:TagKeys": "AmazonDataZoneEnvironment"
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneEnvironmentKMSPermissions",
                        "Effect": "Allow",
                        "Action": [
                            "kms:GenerateDataKey",
                            "kms:Decrypt",
                            "kms:DescribeKey",
                            "kms:CreateGrant",
                            "kms:Encrypt",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:ResourceTag/EnableBedrock": "true",
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"],
                            }
                        },
                    },
                    {
                        "Sid": "PermissionsToGetAmazonDataZoneEnvironmentBlueprintTemplates",
                        "Effect": "Allow",
                        "Action": "s3:GetObject",
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "StringNotEquals": {
                                "aws:ResourceAccount": "${aws:PrincipalAccount}"
                            },
                        },
                    },
                    {
                        "Sid": "PermissionsToManageSecrets",
                        "Effect": "Allow",
                        "Action": ["secretsmanager:GetRandomPassword"],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "PermissionsToStoreSecrets",
                        "Effect": "Allow",
                        "Action": [
                            "secretsmanager:CreateSecret",
                            "secretsmanager:TagResource",
                            "secretsmanager:UntagResource",
                            "secretsmanager:PutResourcePolicy",
                            "secretsmanager:DeleteResourcePolicy",
                            "secretsmanager:DeleteSecret",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            },
                            "Null": {
                                "aws:ResourceTag/AmazonDataZoneEnvironment": "false"
                            },
                        },
                    },
                    {
                        "Sid": "AmazonDataZoneManageProjectBuckets",
                        "Effect": "Allow",
                        "Action": [
                            "s3:CreateBucket",
                            "s3:DeleteBucket",
                            "s3:PutBucketTagging",
                            "s3:PutEncryptionConfiguration",
                            "s3:PutBucketVersioning",
                            "s3:PutBucketCORS",
                            "s3:PutBucketPublicAccessBlock",
                            "s3:PutBucketPolicy",
                            "s3:PutLifecycleConfiguration",
                            "s3:DeleteBucketPolicy",
                        ],
                        "Resource": "arn:aws:s3:::br-studio-*",
                        "Condition": {
                            "StringEquals": {
                                "aws:CalledViaFirst": ["cloudformation.amazonaws.com"]
                            }
                        },
                    },
                    {
                        "Sid": "CreateServiceLinkedRoleForOpenSearchServerless",
                        "Effect": "Allow",
                        "Action": "iam:CreateServiceLinkedRole",
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {
                                "iam:AWSServiceName": "observability.aoss.amazonaws.com",
                                "aws:CalledViaFirst": "cloudformation.amazonaws.com",
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
                        "Principal": {"Service": ["datazone.amazonaws.com"]},
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
                        "Sid": "DomainExecutionRoleStatement",
                        "Effect": "Allow",
                        "Action": [
                            "datazone:GetDomain",
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
                            "datazone:CreateEnvironmentBlueprint",
                            "datazone:UpdateEnvironmentBlueprint",
                            "datazone:DeleteEnvironmentBlueprint",
                            "datazone:ListEnvironmentBlueprintConfigurations",
                            "datazone:ListEnvironmentBlueprintConfigurationSummaries",
                            "datazone:ListEnvironmentProfiles",
                            "datazone:GetEnvironmentProfile",
                            "datazone:CreateEnvironmentProfile",
                            "datazone:UpdateEnvironmentProfile",
                            "datazone:DeleteEnvironmentProfile",
                            "datazone:UpdateEnvironmentDeploymentStatus",
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
                        "Sid": "RAMResourceShareStatement",
                        "Effect": "Allow",
                        "Action": "ram:GetResourceShareAssociations",
                        "Resource": "*",
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:InvokeModel",
                            "bedrock:InvokeModelWithResponseStream",
                            "bedrock:GetFoundationModelAvailability",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Effect": "Allow",
                        "Action": [
                            "kms:DescribeKey",
                            "kms:GenerateDataKey",
                            "kms:Decrypt",
                        ],
                        "Resource": "*",
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
                        "Sid": "BedrockEnvironmentRoleKMSDecryptPermissions",
                        "Effect": "Allow",
                        "Action": ["kms:Decrypt", "kms:GenerateDataKey"],
                        "Resource": "*",
                        "Condition": {
                            "StringEquals": {"aws:ResourceTag/EnableBedrock": "true"}
                        },
                    },
                    {
                        "Sid": "BedrockRuntimeAgentPermissions",
                        "Effect": "Allow",
                        "Action": ["bedrock:InvokeAgent"],
                        "Resource": "*",
                        "Condition": {
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"}
                        },
                    },
                    {
                        "Sid": "BedrockRuntimeModelsAndJobsRole",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:InvokeModel",
                            "bedrock:InvokeModelWithResponseStream",
                            "bedrock:RetrieveAndGenerate",
                        ],
                        "Resource": "*",
                    },
                    {
                        "Sid": "BedrockApplyGuardrails",
                        "Effect": "Allow",
                        "Action": ["bedrock:ApplyGuardrail"],
                        "Resource": "*",
                        "Condition": {
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"}
                        },
                    },
                    {
                        "Sid": "BedrockRuntimePermissions",
                        "Effect": "Allow",
                        "Action": [
                            "bedrock:Retrieve",
                            "bedrock:StartIngestionJob",
                            "bedrock:GetIngestionJob",
                            "bedrock:ListIngestionJobs",
                        ],
                        "Resource": "*",
                        "Condition": {
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"}
                        },
                    },
                    {
                        "Sid": "BedrockFunctionsPermissions",
                        "Action": ["secretsmanager:PutSecretValue"],
                        "Resource": "arn:aws:secretsmanager:*:*:secret:br-studio/*",
                        "Effect": "Allow",
                        "Condition": {
                            "Null": {"aws:ResourceTag/AmazonDataZoneProject": "false"}
                        },
                    },
                    {
                        "Sid": "BedrockS3ObjectsHandlingPermissions",
                        "Action": [
                            "s3:GetObject",
                            "s3:PutObject",
                            "s3:GetObjectVersion",
                            "s3:ListBucketVersions",
                            "s3:DeleteObject",
                            "s3:DeleteObjectVersion",
                            "s3:ListBucket",
                        ],
                        "Resource": [f"arn:aws:s3:::br-studio-{self._account_id}-*"],
                        "Effect": "Allow",
                    },
                ],
            }
        )


if __name__ == "__main__":
    how_to_use_the_bootstrapper()
