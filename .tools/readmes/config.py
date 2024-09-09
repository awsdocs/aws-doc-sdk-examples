# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

readme = "README.md"
saved_readme = "README.old.md"
doc_base_url = "https://docs.aws.amazon.com"
categories = {
    "hello": "Hello",
    "basics": "Basics",
    "scenarios": "Scenarios",
    "actions": "Api",
    "cross": "Cross-service examples",
}
basics_title_abbrev = "Learn the basics"
entities = {
    "&AWS;": "AWS",
    "&aws_sec_sdk_use-federation-warning;": "",
    "&ASH;": "Security Hub",
    "&DAX;": "DynamoDB Accelerator",
    "&EC2long;": "Amazon Elastic Compute Cloud",
    "&ELB;": "Elastic Load Balancing",
    "&ELBlong;": "Elastic Load Balancing",
    "&GLUDCLong;": "AWS Glue Data Catalog",
    "&GLUDC;": "Data Catalog",
    "&IAM-user;": "IAM user",
    "&IAM-users;": "IAM users",
    "&kms-key;": "KMS key",
    "&kms-keys;": "KMS keys",
    "&S3long;": "Amazon Simple Storage Service",
    "&SLN;": "Amazon States Language",
}
language = {
    "C++": {
        1: {
            "base_folder": "cpp",
            "service_folder": 'cpp/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-{{service["name"]}}/html/annotated.html',
        }
    },
    "Go": {
        2: {
            "base_folder": "gov2",
            "service_folder": 'gov2/{{service["name"]}}',
            "sdk_api_ref": 'https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/{{service["name"]}}',
            "service_folder_overrides": {
                "cognito-identity-provider": "gov2/cognito",
            },
        },
    },
    "Java": {
        2: {
            "base_folder": "javav2",
            "service_folder": 'javav2/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/{{service["name"]}}/package-summary.html',
            "service_folder_overrides": {
                "medical-imaging": "javav2/example_code/medicalimaging",
            },
        },
    },
    "JavaScript": {
        3: {
            "base_folder": "javascriptv3",
            "service_folder": 'javascriptv3/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/client/{{service["name"]}}',
        },
    },
    "Kotlin": {
        1: {
            "base_folder": "kotlin",
            "service_folder": 'kotlin/services/{{service["name"]}}',
            "sdk_api_ref": 'https://sdk.amazonaws.com/kotlin/api/latest/{{service["name"]}}/index.html',
        }
    },
    ".NET": {
        3: {
            "base_folder": "dotnetv3",
            "service_folder": 'dotnetv3/{{service["name"] | capitalize}}',
            "sdk_api_ref": 'https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/{{service["name"] | capitalize}}/N{{service["name"] | capitalize}}.html',
            "service_folder_overrides": {
                "acm": "dotnetv3/ACM",
                "aurora": "dotnetv3/Aurora",
                "auto-scaling": "dotnetv3/AutoScaling",
                "cloudformation": "dotnetv3/CloudFormation",
                "cloudwatch": "dotnetv3/CloudWatch",
                "cloudwatch-logs": "dotnetv3/CloudWatchLogs",
                "cognito-identity-provider": "dotnetv3/Cognito",
                "comprehend": "dotnetv3/Comprehend",
                "dynamodb": "dotnetv3/dynamodb",
                "ec2": "dotnetv3/EC2",
                "ecs": "dotnetv3/ECS",
                "eventbridge": "dotnetv3/EventBridge",
                "glacier": "dotnetv3/Glacier",
                "glue": "dotnetv3/Glue",
                "iam": "dotnetv3/IAM",
                "keyspaces": "dotnetv3/Keyspaces",
                "kinesis": "dotnetv3/Kinesis",
                "kms": "dotnetv3/KMS",
                "lambda": "dotnetv3/Lambda",
                "mediaconvert": "dotnetv3/MediaConvert",
                "organizations": "dotnetv3/Organizations",
                "polly": "dotnetv3/Polly",
                "rds": "dotnetv3/RDS",
                "rekognition": "dotnetv3/Rekognition",
                "route-53": "dotnetv3/Route53",
                "s3": "dotnetv3/S3",
                "sagemaker": "dotnetv3/SageMaker",
                "scheduler": "dotnetv3/EventBridge Scheduler",
                "secrets-manager": "dotnetv3/SecretsManager",
                "ses": "dotnetv3/SES",
                "sesv2": "dotnetv3/SESv2",
                "sns": "dotnetv3/SNS",
                "sqs": "dotnetv3/SQS",
                "sfn": "dotnetv3/StepFunctions",
                "sts": "dotnetv3/STS",
                "support": "dotnetv3/Support",
                "transcribe": "dotnetv3/Transcribe",
                "translate": "dotnetv3/Translate",
            },
        }
    },
    "PHP": {
        3: {
            "base_folder": "php",
            "service_folder": 'php/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.{{service["name"] | capitalize}}.html',
        }
    },
    "Python": {
        3: {
            "base_folder": "python",
            "service_folder": 'python/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/{{service["name"]}}.html',
            "service_folder_overrides": {
                "cognito-identity-provider": "python/example_code/cognito",
                "config-service": "python/example_code/config",
                "device-farm": "python/example_code/devicefarm",
                "elastic-load-balancing-v2": "python/example_code/elastic-load-balancing",
                "secrets-manager": "python/example_code/secretsmanager",
                "dynamodb": "python/example_code/dynamodb",
            },
        }
    },
    "Ruby": {
        3: {
            "base_folder": "ruby",
            "service_folder": 'ruby/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/{{service["name"] | capitalize}}.html',
            "service_folder_overrides": {
                "cognito-identity-provider": "ruby/example_code/cognito",
            },
        }
    },
    "Rust": {
        1: {
            "base_folder": "rustv1",
            "service_folder": 'rustv1/examples/{{service["name"]}}',
            "sdk_api_ref": 'https://docs.rs/aws-sdk-{{service["name"]}}/latest/aws_sdk_{{service["name"]}}/',
            "service_folder_overrides": {
                "api-gateway": "rustv1/examples/apigateway",
                "apigatewaymanagementapi": "rustv1/examples/apigatewaymanagement",
                "application-auto-scaling": "rustv1/examples/applicationautoscaling",
                "cognito-identity-provider": "rustv1/examples/cognitoidentityprovider",
                "cognito-sync": "rustv1/examples/cognitosync",
                "rds-data": "rustv1/examples/rdsdata",
                "route-53": "rustv1/examples/route53",
                "secrets-manager": "rustv1/examples/secretsmanager",
                "sesv2": "rustv1/examples/ses",
                "ses": "rustv1/examples/_NONE_",
            },
        }
    },
    "SAP ABAP": {
        1: {
            "base_folder": "sap-abap",
            "service_folder": 'sap-abap/services/{{service["name"]}}',
            "sdk_api_ref": 'https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/{{service["name"]}}/index.html',
            "service_folder_overrides": {
                "bedrock-runtime": "sap-abap/services/bdr",
                "dynamodb": "sap-abap/services/dyn",
            },
        }
    },
    "Swift": {
        1: {
            "base_folder": "swift",
            "service_folder": 'swift/example_code/{{service["name"]}}',
            "sdk_api_ref": 'https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWS{{service["name"] | capitalize}}/Home',
        }
    },
    "Bash": {
        2: {
            "base_folder": "aws-cli",
            "service_folder": 'aws-cli/bash-linux/{{service["name"]}}',
            "sdk_api_ref": 'https://awscli.amazonaws.com/v2/documentation/api/latest/reference/{{service["name"]}}/index.html',
        }
    },
}
