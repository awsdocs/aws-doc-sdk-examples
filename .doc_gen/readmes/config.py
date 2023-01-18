# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

doc_base_url = 'https://docs.aws.amazon.com'
categories = {
    'hello': 'Hello',
    'scenarios': 'Scenarios',
}
entities = {
    '&AWS;': 'AWS',
    '&ASH;': 'Amazon Security Hub'
}
language = {
    'C++': {
        1: {
            'base_folder': 'cpp',
            'service_folder': 'cpp/example_code/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
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
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    'JavaScript': {
        2: {
            'base_folder': 'javascript',
            'service_folder': 'javascript/example_code/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        },
        3: {
            'base_folder': 'javascriptv3',
            'service_folder': 'javascriptv3/example_code/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    'Kotlin': {
        1: {
            'base_folder': 'kotlin',
            'service_folder': 'kotlin/services/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    '.NET': {
        3: {
            'base_folder': 'dotnetv3',
            'service_folder': 'dotnetv3/{{service["name"] | capitalize}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    'PHP': {
        3: {
            'base_folder': 'php',
            'service_folder': 'php/example_code/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
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
            'sdk_api_ref': 'https://docs.aws.amazon.com/sdk-for-ruby/v3/api/{{service["sort"]}}',
        }
    },
    'Rust': {
        1: {
            'base_folder': 'rust_dev_preview',
            'service_folder': 'rust_dev_preview/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    'SAP ABAP': {
        1: {
            'base_folder': 'sap-abap',
            'service_folder': 'sap-abap/services/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
    'Swift': {
        1: {
            'base_folder': 'swift',
            'service_folder': 'swift/example_code/{{service["name"]}}',
            'sdk_api_ref': 'PLACEHOLDER',
        }
    },
}
