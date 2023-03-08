from aws_cdk import (
    Stack,
    aws_s3_deployment as s3_deployment,
    aws_s3 as s3,
    BundlingOptions,
    BundlingOutput,
    DockerImage,
    DockerVolume
)
from constructs import Construct
from pathlib import Path
import boto3
import os

class FrontendStack(Stack):
    def __init__(self, scope: Construct, name, backend_stack_id, **kwargs) -> None:
        super().__init__(scope, f"{name}-FrontEnd-PAM", **kwargs)
        self._s3_website(backend_stack_id)

    def get_export_value(self, exports, stack_id, export_name):
        for export in exports:
            if export["ExportingStackId"] == stack_id and export["Name"] == export_name:
                return export["Value"]
        return None

    def _s3_website(self, backend_stack_id):
        cfn_client = boto3.client("cloudformation")
        export_values = cfn_client.list_exports()['Exports']
        elros_path = "../../../clients/react/elros-pam"
        website_bucket = s3.Bucket(
            self,
            f"website",
            # public_read_access=True,
            website_index_document="index.html",
        )
        with open(Path(f"{elros_path}/.env"), "w") as file:
            file.writelines(
                [
                    f"VITE_COGNITO_USER_POOL_ID={self.get_export_value(export_values, backend_stack_id, 'CognitoUserPoolId')}\n",
                    f"VITE_COGNITO_USER_POOL_CLIENT_ID={self.get_export_value(export_values, backend_stack_id, 'CognitoAppClientId')}\n",
                    f"VITE_API_GATEWAY_BASE_URL={self.get_export_value(export_values, backend_stack_id, 'ApiGatewayUrl')}\n",
                ]
            )
        s3_deployment.BucketDeployment(
            self,
            f"Website",
            sources=[
                s3_deployment.Source.asset(
                    elros_path,
                    bundling=BundlingOptions(
                        command=[
                            "/bin/sh",
                            "-c",
                            f"npm i && \
                                npm run build && \
                                cp -r /asset-input/dist/* /asset-output/",
                        ],
                        environment={},
                        image=DockerImage("node"),
                        user="root",
                        output_type=BundlingOutput.NOT_ARCHIVED,
                        volumes=[
                            # This shares the maven repo between host & container,
                            # which both speeds up and
                            DockerVolume(
                                host_path=f"{os.getenv('HOME')}/.m2/",
                                container_path="/root/.m2",
                            )
                        ],
                    ),
                )
            ],
            destination_bucket=website_bucket,
        )
