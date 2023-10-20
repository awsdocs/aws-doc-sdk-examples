import yaml
from aws_cdk import Stack
from aws_cdk import aws_ecr as ecr
from constructs import Construct


class PublicEcrRepositoriesStack(Stack):
    def __init__(self, scope: Construct, construct_id: str, **kwargs) -> None:
        super().__init__(scope, construct_id, **kwargs)

        target_accts = self.get_yaml_config("../../config/targets.yaml")

        for language in target_accts.keys():
            usage_text = f"This image provides a pre-built SDK for {language} environment and is recommended for local testing of SDK for {language} example code. It is not intended for production usage. For detailed and up-to-date steps on running this image, see https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/{language}/README.md#docker-image-beta."
            repository_description = f'This image provides a pre-built for SDK for {language} environment and is recommended for local testing of SDK for {language} example code."'
            ecr.CfnPublicRepository(
                self,
                f"{language}",
                repository_name=language,
                repository_catalog_data={
                    "UsageText": usage_text,
                    "OperatingSystems": ["Linux"],
                    "Architectures": ["x86", "ARM"],
                    "RepositoryDescription": repository_description,
                },
            )

    def get_yaml_config(self, filepath):
        with open(filepath, "r") as file:
            data = yaml.safe_load(file)
        return data
