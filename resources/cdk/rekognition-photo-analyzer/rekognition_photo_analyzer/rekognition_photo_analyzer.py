# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: MIT-0

from aws_cdk import (
    aws_apigateway as apigateway,
    aws_cognito as cognito,
    aws_dynamodb as ddb,
    aws_iam as iam,
    aws_lambda as lambda_cdk,
    aws_s3 as s3,
    aws_s3_notifications as s3_notifications,
    aws_s3_deployment as s3_deployment,
    Stack
)
from constructs import Construct


ELROS_PATH = "./website"


class RekognitionPhotoAnalyzerStack(Stack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        if "name" in kwargs:
            self.name = kwargs["name"]
            del kwargs["name"]
        else:
            raise RuntimeError("Missing name for PAM stack")

        if "email" in kwargs:
            self.email = kwargs["email"]
            del kwargs["email"]
        else:
            raise RuntimeError("Missing email for PAM stack")

        super().__init__(scope, id, **kwargs)

        self.dns_prefix = f"{self.name}-sdk-code-examples-pam"
        self.prefix = f"{self.name}-SDKCodeExamplesPAM"

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

        lambdas = self._lambdas()

        gateway = self._api_gateway(lambdas, cognito_pool)
        self._s3_website(gateway, app_client)

    def _iam(self):
        # create new IAM group and user
        group = iam.Group(self, f"{self.prefix}-Group")
        user = iam.User(self, f"{self.prefix}-User")

        # add IAM user to the new group
        user.add_to_group(group)

        return (user, group)

    def _s3(self):
        # give new user access to the bucket
        storage_bucket = s3.Bucket(
            self, f"{self.dns_prefix}-storage-bucket")
        storage_bucket.grant_read_write(self.user)

        # TODO: Policy for Glacier storage class for objects with tag rekognition: complete

        working_bucket = s3.Bucket(
            self, f"{self.dns_prefix}-working-bucket")
        working_bucket.grant_read_write(self.user)

        # TODO: Add 24-hour deletion policy

        return (storage_bucket, working_bucket)

    def _dynamodb(self):
        # create DynamoDB table to hold Rekognition results
        labels_table = ddb.Table(
            self, f"{self.prefix}-LabelsTable",
            partition_key=ddb.Attribute(
                name='Label', type=ddb.AttributeType.STRING)
        )

        jobs_table = ddb.Table(
            self, f"{self.prefix}-JobsTable",
            partition_key=ddb.Attribute(
                name='JobId', type=ddb.AttributeType.STRING)
        )

        return (labels_table, jobs_table)

    def _cognito(self):
        cognito_pool = cognito.UserPool(
            self, f"{self.prefix}-UserPool",
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
        )
        cognito_app_client = cognito_pool.add_client(
            f"{self.prefix}-AppClient")
        cognito_user = cognito.CfnUserPoolUser(
            self, f"{self.prefix}-UserPool-DefaultUser",
            user_pool_id=cognito_pool.user_pool_id,
            user_attributes=[cognito.CfnUserPoolUser.AttributeTypeProperty(
                name="email",
                value=self.email
            )],
            username=self.email,
        )
        return (cognito_pool, cognito_user, cognito_app_client)

    def _s3_website(self, gateway, app_client):
        website_bucket = s3.Bucket(
            self, f"{self.dns_prefix}-website",
            public_read_access=True,
            website_index_document="index.html"
        )
        deployment = s3_deployment.BucketDeployment(
            self, f"{self.prefix}-DeployWebsite",
            sources=[
                s3_deployment.Source.asset(ELROS_PATH)],
            destination_bucket=website_bucket
        )

        # TODO: Embed app client id & gateway endpoint.

        return (website_bucket, deployment)

    def _api_gateway(self, lambdas, cognito_pool):
        api = apigateway.RestApi(
            self, f"{self.prefix}-RestApi",
            rest_api_name="PAM",
            default_cors_preflight_options=apigateway.CorsOptions(
                # TODO: Limit this to the s3Deployment bucket domain?
                allow_origins=apigateway.Cors.ALL_ORIGINS,
                allow_credentials=True
            )
        )

        auth = apigateway.CognitoUserPoolsAuthorizer(
            self, f"{self.prefix}-Authorizer",
            cognito_user_pools=[cognito_pool]
        )

        self._api_gateway_route(api, 'labels', lambdas['Labels'], 'GET', auth)
        # self._api_gateway_route(api, 'upload', lambdas['Upload'], 'PUT', auth)
        # self._api_gateway_route(api, 's3_copy', lambdas['Copy'], 'PUT', auth)
        # self._api_gateway_route(
        #     api, 'download', lambdas['Download'], 'PUT', auth)
        # self._api_gateway_route(
        #     api, 'archive', lambdas['Archive'], 'PUT', auth)

    def _api_gateway_route(self, api, resource, lambda_fn, method, auth):
        resource = api.root.add_resource(resource)
        resource.add_method(
            method,
            apigateway.LambdaIntegration(lambda_fn),
            authorizer=auth,
            authorization_type=apigateway.AuthorizationType.COGNITO,
        )

    def _lambdas(self):
        self.layer = lambda_cdk.LayerVersion(
            self, f"{self.prefix}_LibraryLayer", code=self.lambda_code_asset())
        lambda_DetectLabels = self._lambda_DetectLabels(
            self.storage_bucket, self.labels_table)
        lambda_ZipArchive = self._lambda_ZipArchive(
            self.working_bucket, self.jobs_table)
        lambda_Labels = self._lambda_Labels(self.labels_table)
        return dict(
            DetectLabels=lambda_DetectLabels,
            ZipArchive=lambda_ZipArchive,
            Labels=lambda_Labels,
            # TODO
            Upload=None,
            Copy=None,
            Download=None,
            Archive=None,
        )

    def lambda_runtime(self):
        raise NotImplementedError()

    def lambda_code_asset(self):
        raise NotImplementedError()

    def lambda_DetectLabels_handler(self):
        raise NotImplementedError()

    def lambda_Labels_handler(self):
        raise NotImplementedError()

    def lambda_ZipArchive_handler(self):
        raise NotImplementedError()

    def lambda_Upload_handler(self):
        raise NotImplementedError()

    def lambda_Copy_handler(self):
        raise NotImplementedError()

    def lambda_Download_handler(self):
        raise NotImplementedError()

    def lambda_Archive_handler(self):
        raise NotImplementedError()

    def _lambda_DetectLabels(self, storage_bucket, labels_table):
        # create Lambda function
        lambda_function = lambda_cdk.Function(
            self, f'{self.prefix}-DetectLabelsFn',
            # runtime=self.lambda_runtime(),
            runtime=self.lambda_runtime(),
            handler=self.lambda_DetectLabels_handler(),
            code=self.lambda_code_asset(),
            environment={
                'STORAGE_BUCKET_NAME': storage_bucket.bucket_name,
                'LABELS_TABLE_NAME': labels_table.table_name
            }
        )

        # add Rekognition permissions for Lambda function
        statement = iam.PolicyStatement()
        statement.add_actions("rekognition:DetectLabels")
        statement.add_resources(lambda_function.function_arn)
        lambda_function.add_to_role_policy(statement)

        # create trigger for Lambda function with image type suffixes
        notification = s3_notifications.LambdaDestination(lambda_function)
        notification.bind(self, storage_bucket)
        notification, s3.NotificationKeyFilter(suffix='
           .jpg'))
        storage_bucket.add_object_created_notification(
            notification, s3.NotificationKeyFilter(suffix='.jpeg'))

        # grant permissions for lambda to read/write to DynamoDB table and bucket
        labels_table.grant_read_write_data(lambda_function)
        storage_bucket.grant_read_write(lambda_function)

        return lambda_function

    def _lambda_ZipArchive(self, working_bucket, jobs_table):
        # create Lambda function
        lambda_function=lambda_cdk.Function(
            self, f'{self.prefix}-ZipArchiveFn',
            runtime = self.lambda_runtime(),
            handler = self.lambda_ZipArchive_handler(),
            code = self.lambda_code_asset(),
            environment = {
                'WORKING_BUCKET_NAME': working_bucket.bucket_name,
                'JOBS_TABLE_NAME': jobs_table.table_name
            }
        )

        # create trigger for Lambda function on job report suffix
        notification=s3_notifications.LambdaDestination(lambda_function)
        notification.bind(self, working_bucket)
        working_bucket.add_object_created_notification(
            notification, s3.NotificationKeyFilter(prefix='job-', suffix='/report.csv'))

        # grant permissions for lambda to read/write to DynamoDB table and bucket
        jobs_table.grant_read_write_data(lambda_function)
        working_bucket.grant_read_write(lambda_function)

        return lambda_function

    def _lambda_Labels(self, labels_table):
        # create Lambda function
        lambda_function=lambda_cdk.Function(
            self, f'{self.prefix}-LabelsFn',
            runtime = self.lambda_runtime(),
            handler = self.lambda_Labels_handler(),
            code = self.lambda_code_asset(),
            environment = {
                'LABELS_TABLE_NAME': labels_table.table_name
            }
        )

        labels_table.grant_read_data(lambda_function)
        return lambda_function


class PythonRekognitionPhotoAnalyzerStack(RekognitionPhotoAnalyzerStack):
    def __init__(self, scope: Construct, id: str, **kwargs) -> None:
        super().__init__(scope, id, **kwargs)

    def lambda_runtime(self):
        return lambda_cdk.Runtime.PYTHON_3_8

    def lambda_code_asset(self):
        return lambda_cdk.Code.from_asset('rekognition_photo_analyzer/lambda')

    def lambda_DetectLabels_handler(self):
        return 'rekfunction.handler'

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
