# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

from urllib.request import urlopen
import json

# Only files with these extensions are scanned.
EXT_LOOKUP = {
    ".c": "C",
    ".cpp": "C++",
    ".cs": "C#",
    ".go": "Go",
    ".html": "JavaScript",
    ".java": "Java",
    ".js": "JavaScript",
    ".kt": "Kotlin",
    ".php": "PHP",
    ".py": "Python",
    ".rb": "Ruby",
    ".rs": "Rust",
    ".swift": "Swift",
    ".ts": "TypeScript",
    ".sh": "AWS-CLI",
    ".cmd": "AWS-CLI",
    ".json": "JSON",
    ".yml": "YAML",
    ".yaml": "YAML",
    ".md": "Markdown",
}


def skip(path):
    return path.suffix.lower() not in EXT_LOOKUP or path.name in IGNORE_FILES


# If you get a lot of false-flagged 40-character errors
# in specific folders or files, you can omit them from
# these scans by adding them to the following lists.
# However, because this script is mostly run as a GitHub
# action in a clean environment (aside from testing),
# exhaustive ignore lists shouldn't be necessary.

# Files to skip.
IGNORE_FILES = {
    ".moviedata.json",
    ".travis.yml",
    "AssemblyInfo.cs",
    "moviedata.json",
    "movies.json",
    "movies_5.json",
    "package-lock.json",
}

GOOD_WORDS = {
    "crash",
    "dp",
    "dummy",
    "jerry",
    "throat",
}

DATA = urlopen(
    "https://raw.githubusercontent.com/zacanger/profane-words/5ad6c62fa5228293bc610602eae475d50036dac2/words.json"
)
WORDS = set(json.load(DATA)).difference(GOOD_WORDS)

# List of words that should never be in code examples.
DENY_LIST = {"alpha-docs-aws.amazon.com", "integ-docs-aws.amazon.com"}.union(WORDS)

# Allowlist of 20- or 40-character strings to allow.
ALLOW_LIST = {
    "AGPAIFFQAVRFFEXAMPLE",
    "AKIA111111111EXAMPLE",
    "AKIA6OHTTRXXTEXAMPLE",
    "AKIAEXAMPLEACCESSKEY",
    "AKIAIOSFODNN7EXAMPLE",
    "APKAEIBAERJR2EXAMPLE",
    "AWSEC2/latest/APIReference/OperationList",
    "AppStreamUsageReportsCFNGlueAthenaAccess",
    "CancelExportTaskExample/CancelExportTask",
    "CertificateTransparencyLoggingPreference",
    "ChangeMessageVisibilityBatchRequestEntry",
    "CreateCollectionExample/CreateCollection",
    "CreateExportTaskExample/CreateExportTask",
    "DeleteCollectionExample/DeleteCollection",
    "DescribeDbClusterParameterGroupsResponse",
    "DescribeOrderableDBInstanceOptionsOutput",
    "DynamodbRubyExampleCreateUsersTableStack",
    "GetIdentityVerificationAttributesRequest",
    "KMSWithContextEncryptionMaterialsExample",
    "KinesisStreamSourceConfiguration=kinesis",
    "ListOrganizationalUnitsForParentResponse",
    "ListTagsExample/ListTagsExample/ListTags",
    "ListTagsForVaultExample/ListTagsForVault",
    "SynthesizeSpeechExample/SynthesizeSpeech",
    "TargetTrackingScalingPolicyConfiguration",
    "TerminateInstanceInAutoScalingGroupAsync",
    "VectorEnrichmentJobDataSourceConfigInput",
    "amazondynamodb/latest/developerguide/DAX",
    "apigateway/latest/developerguide/welcome",
    "aws/acm/model/DescribeCertificateRequest",
    "aws/cloudtrail/model/LookupEventsRequest",
    "aws/codebuild/model/BatchGetBuildsResult",
    "aws/codecommit/model/DeleteBranchRequest",
    "aws/codecommit/model/ListBranchesRequest",
    "aws/dynamodb/model/BatchWriteItemRequest",
    "aws/dynamodb/model/ProvisionedThroughput",
    "aws/ec2/model/CreateSecurityGroupRequest",
    "aws/ec2/model/DeleteSecurityGroupRequest",
    "aws/ec2/model/UnmonitorInstancesResponse",
    "aws/email/model/CreateReceiptRuleRequest",
    "aws/email/model/DeleteReceiptRuleRequest",
    "aws/email/model/ListReceiptFiltersResult",
    "aws/email/model/SendTemplatedEmailResult",
    "aws/guardduty/model/ListDetectorsRequest",
    "aws/iam/model/GetAccessKeyLastUsedResult",
    "aws/iam/model/GetServerCertificateResult",
    "aws/kinesis/model/GetShardIteratorResult",
    "aws/kinesis/model/PutRecordsRequestEntry",
    "aws/kms/model/ScheduleKeyDeletionRequest",
    "aws/monitoring/model/DeleteAlarmsRequest",
    "aws/neptune/model/CreateDBClusterRequest",
    "aws/neptune/model/DeleteDBClusterRequest",
    "aws/neptune/model/ModifyDBClusterRequest",
    "aws/rds/model/DescribeDBInstancesRequest",
    "aws/rds/model/DescribeDBSnapshotsRequest",
    "cloudwatch/commands/PutMetricDataCommand",
    "com/amazondynamodb/latest/developerguide",
    "com/apigateway/latest/developerguide/set",
    "com/autoscaling/ec2/APIReference/Welcome",
    "com/awssupport/latest/APIReference/index",
    "com/firehose/latest/APIReference/Welcome",
    "com/greengrass/latest/developerguide/lra",
    "com/greengrass/latest/developerguide/sns",
    "com/kotlin/api/latest/mediaconvert/index",
    "com/pinpoint/latest/apireference/welcome",
    "com/redshift/latest/APIReference/Welcome",
    "com/rekognition/latest/dg/considerations",
    "com/samples/JobStatusNotificationsSample",
    "com/transcribe/latest/APIReference/index",
    "com/v1/documentation/api/latest/guide/s3",
    "com/workdocs/latest/APIReference/Welcome",
    "devicefarm/latest/developerguide/welcome",
    "examples/blob/main/applications/feedback",
    "generate_presigned_url_and_upload_object",
    "iam/commands/GetAccessKeyLastUsedCommand",
    "iam/commands/GetServerCertificateCommand",
    "nFindProductsWithNegativePriceWithConfig",
    "preview/examples/cognitoidentityprovider",
    "preview/examples/lambda/src/bin/scenario",
    "role/AmazonSageMakerGeospatialFullAccess",
    "s3_client_side_encryption_sym_master_key",
    "serial/CORE_THING_NAME/write/dev/serial1",
    "service/FeedbackSentimentAnalyzer/README",
    "ses/commands/CreateReceiptRuleSetCommand",
    "ses/commands/DeleteReceiptRuleSetCommand",
    "ses/commands/VerifyDomainIdentityCommand",
    "src/main/java/com/example/dynamodb/Query",
    "src/main/java/com/example/iam/CreateRole",
    "src/main/java/com/example/iam/CreateUser",
    "src/main/java/com/example/iam/DeleteUser",
    "src/main/java/com/example/iam/UpdateUser",
    "src/main/java/com/example/kms/ListGrants",
    "src/main/java/com/example/s3/ListObjects",
    "src/main/java/com/example/s3/S3BucketOps",
    "src/main/java/com/example/sns/ListOptOut",
    "src/main/java/com/example/sns/ListTopics",
    "src/main/java/com/example/sqs/SQSExample",
    "targetTrackingScalingPolicyConfiguration",
    "upload_files_using_managed_file_uploader",
    "videoMetaData=celebrityRecognitionResult",
    "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
    "cloudsearch/latest/developerguide/search",
    "cloudsearch/latest/developerguide/search",
    "codeartifact/latest/APIReference/Welcome",
    "codepipeline/latest/APIReference/Welcome",
    "datapipeline/latest/APIReference/Welcome",
    "imagebuilder/latest/APIReference/Welcome",
    "iotanalytics/latest/APIReference/Welcome",
    "mediaconnect/latest/APIReference/Welcome",
    "cryptography/latest/APIReference/Welcome",
    "cloudsearch/latest/developerguide/search",
    "cloudsearch/latest/developerguide/search",
    "codeartifact/latest/APIReference/Welcome",
    "codepipeline/latest/APIReference/Welcome",
    "datapipeline/latest/APIReference/Welcome",
    "imagebuilder/latest/APIReference/Welcome",
    "iotanalytics/latest/APIReference/Welcome",
    "mediaconnect/latest/APIReference/Welcome",
    "cryptography/latest/APIReference/Welcome",
    "com/AmazonCloudWatch/latest/logs/Working",
}


# Sample files.
EXPECTED_SAMPLE_FILES = {
    "README.md",
    "chat_sfn_state_machine.json",
    "market_2.jpg",
    "movies.json",
    "sample_cert.pem",
    "sample_private_key.pem",
    "sample_saml_metadata.xml",
    "speech_sample.mp3",
    "spheres_2.jpg",
}

# Media file types.
MEDIA_FILE_TYPES = {"mp3", "wav"}
