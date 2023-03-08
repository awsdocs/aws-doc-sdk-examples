# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

from aws_cdk import (
    aws_apigateway as apigateway,
    aws_cognito as cognito,
    aws_dynamodb as ddb,
    aws_iam as iam,
    aws_lambda as lambda_cdk,
    aws_s3 as s3,
    aws_s3_deployment as s3_deployment,
    aws_s3_notifications as s3_notifications,
    BundlingOptions,
    BundlingOutput,
    DockerVolume,
    Duration,
    RemovalPolicy,
    Stack,
)
from constructs import Construct
from pathlib import Path
from typing import Optional

import os

from . import models


ELROS_PATH = "./website"


Lambdas = dict[str, lambda_cdk.Function]


class RekognitionPhotoAnalyzerStack(Stack):
    def __init__(
        self, scope: Construct, lang: str, name="", email="", **kwargs
    ) -> None:
        self.email = email
        self.lang = lang

        id = f"{name}-{lang}-PAM"

        super().__init__(scope, id, **kwargs)

        (user, group) = self._iam()
        self.user = user
        self.group = group

        (storage_bucket, working_bucket) = self._s3()
        self.storage_bucket = storage_bucket
        self.working_bucket = working_bucket

        (labels_table, jobs_table) = self._dynamodb()
        self.labels_table = labels_table
        self.jobs_table = jobs_table

        (cognito_pool, cognito_user, app_client) = self._cognito()
        self.cognito_pool = cognito_pool
        self.cognito_user = cognito_user

        self.lambdas = self._lambdas()

        self.gateway = self._api_gateway(self.lambdas, cognito_pool)
        self._s3_website(self.gateway, app_client)

        self._permissions()

    def _iam(self):
        # create new IAM group and user
        group = iam.Group(self, f"AppGroup")
        user = iam.User(self, f"AppUser")

        # add IAM user to the new group
        user.add_to_group(group)

        return (user, group)

    def _s3(self) -> tuple[s3.Bucket, s3.Bucket]:
        # give new user access to the bucket
        storage_bucket = s3.Bucket(
            self, f"storage-bucket", removal_policy=RemovalPolicy.DESTROY
        )
        storage_bucket.grant_read_write(self.user)

        # Policy for Glacier storage class for objects with tag rekognition: complete
        storage_bucket.add_lifecycle_rule(
            tag_filters={"rekognition": "complete"},
            transitions=[
                s3.Transition(
                    storage_class=s3.StorageClass.GLACIER,
                    transition_after=Duration.days(1),
                )
            ],
        )

        working_bucket = s3.Bucket(
            self, f"working-bucket", removal_policy=RemovalPolicy.DESTROY
        )
        working_bucket.grant_read_write(self.user)

        # Add 24-hour deletion policy
        working_bucket.add_lifecycle_rule(expiration=Duration.days(1))

        return (storage_bucket, working_bucket)

    def _dynamodb(self) -> tuple[ddb.Table, ddb.Table]:
        # create DynamoDB table to hold Rekognition results
        labels_table = ddb.Table(
            self,
            f"LabelsTable",
            partition_key=ddb.Attribute(name="Label", type=ddb.AttributeType.STRING),
            removal_policy=RemovalPolicy.DESTROY,
        )

        jobs_table = ddb.Table(
            self,
            f"JobsTable",
            partition_key=ddb.Attribute(name="JobId", type=ddb.AttributeType.STRING),
            removal_policy=RemovalPolicy.DESTROY,
        )

        return (labels_table, jobs_table)

    def _cognito(
        self,
    ) -> tuple[cognito.UserPool, cognito.CfnUserPoolClient, cognito.UserPoolClient]:
        cognito_pool = cognito.UserPool(
            self,
            f"UserPool",
            password_policy=cognito.PasswordPolicy(
                # Password is 6 characters minimum length and no complexity requirements,
                min_length=6,
                require_digits=False,
                require_lowercase=False,
                require_symbols=False,
                require_uppercase=False,
            ),
            mfa=cognito.Mfa.OFF,  # no MFA,
            # no self-service account recovery,
            account_recovery=cognito.AccountRecovery.NONE,
            self_sign_up_enabled=False,  # no self-registration,
            # no assisted verification,
            # no required or custom attributes,
            # send email with cognito.
            removal_policy=RemovalPolicy.DESTROY,
        )
        cognito_app_client = cognito_pool.add_client(f"AppClient")
        cognito_user = cognito.CfnUserPoolUser(
            self,
            f"UserPool-DefaultUser",
            user_pool_id=cognito_pool.user_pool_id,
            user_attributes=[
                cognito.CfnUserPoolUser.AttributeTypeProperty(
                    name="email", value=self.email
                )
            ],
            username=self.email,
        )
        return (cognito_pool, cognito_user, cognito_app_client)

    def _s3_website(
        self, gateway: apigateway.RestApi, app_client: cognito.UserPoolClient
    ):
        website_bucket = s3.Bucket(
            self,
            f"website",
            # public_read_access=True,
            website_index_document="index.html",
            removal_policy=RemovalPolicy.DESTROY,
        )
        deployment = s3_deployment.BucketDeployment(
            self,
            f"Website",
            sources=[s3_deployment.Source.asset(ELROS_PATH)],
            destination_bucket=website_bucket,
        )

        # TODO: Embed app client id & gateway endpoint.

        return (website_bucket, deployment)

    def _api_gateway(
        self, lambdas: Lambdas, cognito_pool: cognito.UserPool
    ) -> apigateway.RestApi:
        self.api_auth = None
        # self.api_auth = apigateway.CognitoUserPoolsAuthorizer(
        #     self, f"Authorizer", cognito_user_pools=[cognito_pool],
        # )

        self.api = apigateway.RestApi(
            self,
            f"RestApi",
            rest_api_name=self.stack_name,
            default_cors_preflight_options=apigateway.CorsOptions(
                # TODO: Limit this to the s3Deployment bucket domain?
                allow_origins=apigateway.Cors.ALL_ORIGINS,
                allow_credentials=True,
            ),
        )

        self.empty = models.Empty(self)

        # self._api_gateway_route(
        #     "archive",
        #     lambdas["Archive"],
        #     "PUT",
        #     request_model=models.ArchiveRequestModel(self),
        # )

        self._api_gateway_route(
            "labels",
            lambdas["Labels"],
            "GET",
            response_model=models.LabelsResponseModel(self),
        )

        self._api_gateway_route(
            "upload",
            lambdas["Upload"],
            "PUT",
            request_model=models.UploadRequestModel(self),
            response_model=models.UploadResponseModel(self),
        )

        self._api_gateway_route(
            "s3_copy",
            lambdas["Copy"],
            "PUT",
            request_model=models.CopyRequestModel(self),
            response_model=models.CopyResponseModel(self),
        )

        self._api_gateway_route(
            "restore",
            lambdas["Download"],
            "PUT",
            request_model=models.DownloadRequestModel(self),
        )

        return self.api

    def _api_gateway_route(
        self,
        path: str,
        lambda_fn: lambda_cdk.Function,
        method: str,
        request_model: Optional[apigateway.Model] = None,
        response_model: Optional[apigateway.Model] = None,
    ) -> None:
        if request_model == None:
            request_model = self.empty
        if response_model == None:
            response_model = self.empty
        resource = self.api.root.add_resource(path)
        resource.add_method(
            method,
            apigateway.LambdaIntegration(lambda_fn),
            request_models={"application/json": request_model},
            method_responses=[
                apigateway.MethodResponse(
                    status_code="200",
                    response_models={"application/json": response_model},
                )
            ]
            # TODO: Uncomment after testing
            # authorizer=self.auth,
            # authorization_type=apigateway.AuthorizationType.COGNITO,
        )

    def _permissions(self):
        # DetectLabelsFn
        fn = self.lambdas["DetectLabels"]
        # create trigger for Lambda function with image type suffixes
        notification = s3_notifications.LambdaDestination(fn)
        self.storage_bucket.add_object_created_notification(
            notification, s3.NotificationKeyFilter(suffix=".jpg")
        )
        self.storage_bucket.add_object_created_notification(
            notification, s3.NotificationKeyFilter(suffix=".jpeg")
        )

        # add Rekognition permissions
        fn.role.add_to_principal_policy(
            iam.PolicyStatement(actions=["rekognition:DetectLabels"], resources=["*"])
        )

        # grant permissions for DetectLabels to read/write to DynamoDB table and bucket
        self.labels_table.grant_read_write_data(fn)
        self.storage_bucket.grant_read_write(fn)
        del fn

        # ZipArchiveFn
        fn = self.lambdas["ZipArchive"]
        # notification = s3_notifications.LambdaDestination(fn)
        # notification.bind(self, self.working_bucket)
        # self.working_bucket.add_object_created_notification(
        #     notification, s3.NotificationKeyFilter(prefix='job-', suffix='/report.csv'))

        # # grant permissions for lambda to read/write to DynamoDB table and bucket
        # self.jobs_table.grant_read_write_data(fn)
        # self.working_bucket.grant_read_write(fn)
        del fn

        # LabelsFn
        fn = self.lambdas["Labels"]
        self.labels_table.grant_read_data(fn)
        del fn

        # UploadFn
        fn = self.lambdas["Upload"]
        self.storage_bucket.grant_put(fn)
        del fn

        # DownloadFn
        fn = self.lambdas["Download"]
        self.labels_table.grant_read_data(fn)
        self.jobs_table.grant_write_data(fn)
        self.working_bucket.grant_put(fn)
        fn.role.add_to_principal_policy(
            iam.PolicyStatement(actions=["sns:subscribe"], resources=["*"])
        )
        del fn

        # CopyFn
        fn = self.lambdas["Copy"]
        self.storage_bucket.grant_put(fn)
        del fn

        # ArchiveFn
        fn = self.lambdas["Archive"]
        # self.storage_bucket.grant_put(fn)
        # self.storage_bucket.grant
        del fn

    def _lambdas(self) -> Lambdas:
        self.lambda_environment = {
            "WORKING_BUCKET_NAME": self.working_bucket.bucket_name,
            "STORAGE_BUCKET_NAME": self.storage_bucket.bucket_name,
            "JOBS_TABLE_NAME": self.jobs_table.table_name,
            "LABELS_TABLE_NAME": self.labels_table.table_name,
        }

        self.lambda_code = self.lambda_code_asset()

        lambda_DetectLabels = self._lambda(
            "DetectLabelsFn", self.lambda_DetectLabels_handler()
        )
        # lambda_ZipArchive = self._lambda(
        #     'ZipArchiveFn', self.lambda_ZipArchive_handler())
        lambda_Labels = self._lambda("LabelsFn", self.lambda_Labels_handler())
        lambda_Upload = self._lambda("UploadFn", self.lambda_Upload_handler())
        lambda_Copy = self._lambda("CopyFn", self.lambda_Copy_handler())
        lambda_Download = self._lambda("DownloadFn", self.lambda_Download_handler())
        # lambda_Archive = self._lambda(
        #     'ArchiveFn', self.lambda_Archive_handler())

        return dict(
            DetectLabels=lambda_DetectLabels,
            ZipArchive=None,
            # ZipArchive=lambda_ZipArchive,
            Labels=lambda_Labels,
            Upload=lambda_Upload,
            Copy=lambda_Copy,
            Download=lambda_Download,
            Archive=None,
            # Archive=lambda_Archive,
        )

    def lambda_runtime(self) -> lambda_cdk.Runtime:
        raise NotImplementedError()

    def lambda_code_asset(self) -> lambda_cdk.Code:
        raise NotImplementedError()

    def lambda_DetectLabels_handler(self) -> str:
        raise NotImplementedError()

    def lambda_Labels_handler(self) -> str:
        raise NotImplementedError()

    def lambda_ZipArchive_handler(self) -> str:
        raise NotImplementedError()

    def lambda_Upload_handler(self) -> str:
        raise NotImplementedError()

    def lambda_Copy_handler(self) -> str:
        raise NotImplementedError()

    def lambda_Download_handler(self) -> str:
        raise NotImplementedError()

    def lambda_Archive_handler(self) -> str:
        raise NotImplementedError()

    def _lambda(self, name: str, handler: str):
        return lambda_cdk.Function(
            self,
            name,
            runtime=self.lambda_runtime(),
            handler=handler,
            # This will ensure one copy of the code is shared by all the lambdas
            code=self.lambda_code,
            environment=self.lambda_environment,
        )


class PythonRekognitionPhotoAnalyzerStack(RekognitionPhotoAnalyzerStack):
    def __init__(self, scope: Construct, name, email, **kwargs) -> None:
        super().__init__(scope, "Python", name, email, **kwargs)

    def lambda_runtime(self):
        return lambda_cdk.Runtime.PYTHON_3_8

    def lambda_code_asset(self):
        return lambda_cdk.Code.from_asset("rekognition_photo_analyzer/lambda")

    def lambda_DetectLabels_handler(self):
        return "rekfunction.handler"

    def lambda_Labels_handler(self):
        return "rek.func"

    def lambda_ZipArchive_handler(self):
        return "rek.func"

    def lambda_Upload_handler(self):
        return "rek.func"

    def lambda_Copy_handler(self):
        return "rek.func"

    def lambda_Download_handler(self):
        return "rek.func"

    def lambda_Archive_handler(self):
        return "rek.func"


class JavaRekognitionPhotoAnalyzerStack(RekognitionPhotoAnalyzerStack):
    def __init__(self, scope: Construct, name, email, **kwargs) -> None:
        super().__init__(scope, "Java", name, email, **kwargs)

    def lambda_runtime(self):
        return lambda_cdk.Runtime.JAVA_11

    def lambda_code_asset(self):
        return lambda_cdk.Code.from_asset(
            str(Path(__file__) / "../../../../../javav2/usecases/pam_source_files/"),
            bundling=BundlingOptions(
                command=[
                    "/bin/sh",
                    "-c",
                    "mvn install && \
                            cp /asset-input/target/PhotoAssetRestSDK-1.0-SNAPSHOT.jar /asset-output/",
                ],
                image=self.lambda_runtime().bundling_image,
                user="root",
                output_type=BundlingOutput.ARCHIVED,
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

    def lambda_DetectLabels_handler(self):
        return "com.example.photo.handlers.S3Trigger"

    def lambda_Labels_handler(self):
        return "com.example.photo.handlers.GetHandler"

    def lambda_ZipArchive_handler(self):
        return "com.example.photo.handlers.ZipArchiveHandler"

    def lambda_Upload_handler(self):
        return "com.example.photo.handlers.UploadHandler"

    def lambda_Copy_handler(self):
        return "com.example.photo.handlers.S3Copy"

    def lambda_Download_handler(self):
        return "com.example.photo.handlers.Restore"

    def lambda_Archive_handler(self):
        return ""
