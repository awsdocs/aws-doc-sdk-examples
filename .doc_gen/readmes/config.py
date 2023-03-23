# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

readme = 'README.md'
saved_readme = 'README.old.md'
doc_base_url = 'https://docs.aws.amazon.com'
categories = {
    'hello': 'Hello',
    'scenarios': 'Scenarios',
}
entities = {
    '&AWS;': 'AWS',
    '&aws_sec_sdk_use-federation-warning;': '',
    '&ASH;': 'Security Hub',
    '&DAX;': 'DynamoDB Accelerator',
    '&GLUDCLong;': 'AWS Glue Data Catalog',
    '&GLUDC;': 'Data Catalog',
    '&IAM-user;': 'IAM user',
    '&IAM-users;': 'IAM users',
    '&kms-key;': 'KMS key',
    '&kms-keys;': 'KMS keys',
    '&SLN;': 'Amazon States Language',
}
language = {
    'C++': {
        1: {
            'base_folder': 'cpp',
            'service_folder': 'cpp/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://sdk.amazonaws.com/cpp/api/LATEST/aws-cpp-sdk-{{service["name"]}}/html/annotated.html',
        }
    },
    'Go': {
        2: {
            'base_folder': 'gov2',
            'service_folder': 'gov2/{{service["name"]}}',
            'sdk_api_ref': 'https://pkg.go.dev/github.com/aws/aws-sdk-go-v2/service/{{service["name"]}}',
        }
    },
    'Java': {
        2: {
            'base_folder': 'javav2',
            'service_folder': 'javav2/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/services/{{service["name"]}}/package-summary.html',
        }
    },
    'JavaScript': {
        2: {
            'base_folder': 'javascript',
            'service_folder': 'javascript/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/{{service["name"] | capitalize}}.html',
        },
        3: {
            'base_folder': 'javascriptv3',
            'service_folder': 'javascriptv3/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-{{service["name"]}}/index.html',
        }
    },
    'Kotlin': {
        1: {
            'base_folder': 'kotlin',
            'service_folder': 'kotlin/services/{{service["name"]}}',
            'sdk_api_ref': 'https://sdk.amazonaws.com/kotlin/api/latest/{{service["name"]}}/index.html',
        }
    },
    '.NET': {
        3: {
            'base_folder': 'dotnetv3',
            'service_folder': 'dotnetv3/{{service["name"] | capitalize}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/sdkfornet/v3/apidocs/items/{{service["name"] | capitalize}}/N{{service["name"] | capitalize}}.html',
        }
    },
    'PHP': {
        3: {
            'base_folder': 'php',
            'service_folder': 'php/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/aws-sdk-php/v3/api/namespace-Aws.{{service["name"] | capitalize}}.html',
        }
    },
    'Python': {
        3: {
            'base_folder': 'python',
            'service_folder': 'python/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/{{service["name"]}}.html',
        }
    },
    'Ruby': {
        3: {
            'base_folder': 'ruby',
            'service_folder': 'ruby/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/sdk-for-ruby/v3/api/Aws/{{service["name"] | capitalize}}.html',
        }
    },
    'Rust': {
        1: {
            'base_folder': 'rust_dev_preview',
            'service_folder': 'rust_dev_preview/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.rs/aws-sdk-{{service["name"]}}/latest/aws_sdk_{{service["name"]}}/',
        }
    },
    'SAP ABAP': {
        1: {
            'base_folder': 'sap-abap',
            'service_folder': 'sap-abap/services/{{service["name"]}}',
            'sdk_api_ref': 'https://docs.aws.amazon.com/sdk-for-sap-abap/v1/api/latest/{{service["name"]}}/index.html',
        }
    },
    'Swift': {
        1: {
            'base_folder': 'swift',
            'service_folder': 'swift/example_code/{{service["name"]}}',
            'sdk_api_ref': 'https://awslabs.github.io/aws-sdk-swift/reference/0.x/AWS{{service["name"] | capitalize}}/Home',
        }
    },
}
