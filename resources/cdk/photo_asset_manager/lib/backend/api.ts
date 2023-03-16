/**
 * Cognito User Pool
 * API Gateway
 */

import {
  AuthorizationType,
  CognitoUserPoolsAuthorizer,
  Cors,
  LambdaIntegration,
  LogGroupLogDestination,
  MethodLoggingLevel,
  Model,
  PassthroughBehavior,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { PamAuth } from "./auth";
import { PamLambda } from "./lambdas";
import * as models from "./api_models";
import { PAM_STACK_NAME } from "../common";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { LogGroup } from "aws-cdk-lib/aws-logs";
import { LogFormat } from "aws-cdk-lib/aws-ec2";

export interface PamApiProps {
  lambdas: PamLambda;
  email: string;
  cloudfrontDistributionUrl: string;
}

export class PamApi extends Construct {
  readonly auth: PamAuth;
  readonly restApi: RestApi;
  readonly apigAuthorizer: CognitoUserPoolsAuthorizer;
  private readonly empty: Model;
  constructor(scope: Construct, id: string, props: PamApiProps) {
    super(scope, id);

    this.auth = new PamAuth(this, "PamAuth", {
      email: props.email,
      cloudfrontDistributionUrl: props.cloudfrontDistributionUrl,
    });

    const access = new LogGroup(this, "RestApiAccess");

    this.apigAuthorizer = new CognitoUserPoolsAuthorizer(
      this,
      "PamRestApiAuthorizer",
      { cognitoUserPools: [this.auth.userPool] }
    );

    const restApi = (this.restApi = new RestApi(
      this,
      `${PAM_STACK_NAME}-RestApi`,
      {
        defaultCorsPreflightOptions: {
          allowOrigins: Cors.ALL_ORIGINS,
          allowCredentials: true,
        },
        deployOptions: {
          accessLogDestination: new LogGroupLogDestination(access),
          loggingLevel: MethodLoggingLevel.INFO,
          dataTraceEnabled: true,
        },
      }
    ));

    const lambdas = props.lambdas.fns;
    this.empty = new models.Empty(this, { restApi });
    this.route("labels", lambdas.labels, "GET", {
      response: new models.LabelsResponseModel(this, { restApi }),
    });

    this.route("upload", lambdas.upload, "PUT", {
      request: new models.UploadRequestModel(this, { restApi }),
      response: new models.UploadResponseModel(this, { restApi }),
    });

    this.route("s3_copy", lambdas.copy, "PUT", {
      request: new models.CopyRequestModel(this, { restApi }),
      response: new models.CopyResponseModel(this, { restApi }),
    });

    this.route("download", lambdas.download, "PUT", {
      event: true,
      request: new models.DownloadRequestModel(this, { restApi }),
    });
  }

  private route(
    path: string,
    fn: Function,
    method: string,
    {
      request = this.empty,
      response = this.empty,
      event = false,
    }: {
      request?: Model;
      response?: Model;
      event?: boolean;
    }
  ) {
    const resource = this.restApi.root.addResource(path);
    resource.addMethod(
      method,
      new LambdaIntegration(fn, {
        ...(event
          ? {
              proxy: false,
              passthroughBehavior: PassthroughBehavior.WHEN_NO_TEMPLATES,
              requestParameters: {
                // "integration.request.body.tags": "method.request.body.tags",
                "integration.request.header.X-Amz-Invocation-Type": "'Event'",
              },
              integrationResponses: [
                {
                  statusCode: "200",
                  responseParameters: {
                    "method.response.header.Access-Control-Allow-Origin": "'*'",
                  },
                },
              ],
            }
          : {}),
      }),
      {
        requestModels: { "application/json": request },
        methodResponses: [
          {
            statusCode: "200",
            responseParameters: {
              "method.response.header.Access-Control-Allow-Origin": true,
            },
            responseModels: { "application/json": response },
          },
        ],
        authorizer: this.apigAuthorizer,
        authorizationType: AuthorizationType.COGNITO,
      }
    );
  }
}
