from aws_cdk import (
    Stack,
    aws_ecr as ecr
)
from constructs import Construct

class PublicEcrRepositoriesStack(Stack):

    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        languages = [
            'ruby',
            'javav2',
            'javascriptv3',
            'gov2',
            'python',
            'dotnetv3',
            'kotlin',
            'rust_dev_preview',
            'swift',
            'cpp',
            'sap-abap'
        ]

        for language in languages:

            usage_text = f'This image provides a pre-built SDK for {language} environment and is recommended for local testing of SDK for {language} example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/{language}/README.md#docker-image-beta.'
            repository_description = f'This image provides a pre-built for SDK for {language} environment and is recommended for local testing of SDK for {language} example code."'
            ecr.CfnPublicRepository(self, f"{language}",
                repository_name=language,
                repository_catalog_data={
                    "UsageText": usage_text,
                    "OperatingSystems": ["Linux"],
                    "Architectures": ["x86", "ARM"],
                    "RepositoryDescription": repository_description
                }
            )
