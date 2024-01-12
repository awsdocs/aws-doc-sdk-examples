// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  AwsIntegration,
  ContentHandling,
  IAuthorizer,
  LambdaIntegration,
  Model,
  PassthroughBehavior,
  Resource,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { DownloadModel, Empty, UploadModel } from "./app-api-models";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";

export interface AppRoutesProps {
  api: RestApi;
}

export interface RouteProps {
  path: string;
  method: string;
  authorizer?: IAuthorizer;
  model: {
    request?: Model;
    response?: Model;
    event?: boolean;
  };
}

export interface LambdaRouteProps extends RouteProps {
  fn: Function;
}

export interface DirectS3RouteProps
  extends Omit<RouteProps, "method" | "model"> {
  bucket: Bucket;
}

export class AppRoutes extends Construct {
  apiRoot: Resource;
  emptyModel: Empty;
  role: Role;

  constructor(
    scope: Construct,
    id: string,
    private readonly props: AppRoutesProps
  ) {
    super(scope, id);
    this.apiRoot = this.props.api.root.addResource("api");
    this.emptyModel = new Empty(this, { restApi: this.props.api });
    this.role = new Role(this, "routes-role", {
      assumedBy: new ServicePrincipal("apigateway.amazonaws.com"),
    });
  }

  addLambdaRoute({ path, fn, method, authorizer, model }: LambdaRouteProps) {
    const resource = this.apiRoot.addResource(path);

    resource.addMethod(
      method,
      new LambdaIntegration(fn, {
        ...(model.event && EVENT_INTEGRATION_OPTIONS),
      }),
      {
        requestModels: {
          "application/json": model.request ?? this.emptyModel,
        },
        methodResponses: [getResponse(model.response)],
        ...(authorizer ? { authorizer } : {}),
      }
    );
  }

  addDirectS3Route({ path, authorizer, bucket }: DirectS3RouteProps) {
    const resource = this.apiRoot.addResource(path);
    const objectKeyResource = resource.addResource("{item}");
    bucket.grantRead(this.role);
    bucket.grantWrite(this.role);

    const uploadModel = new UploadModel(this, { restApi: this.props.api });
    const downloadModel = new DownloadModel(this, { restApi: this.props.api });

    objectKeyResource.addMethod(
      "PUT",
      new AwsIntegration({
        service: "s3",
        integrationHttpMethod: "PUT",
        path: `${bucket.bucketName}/{item}`,
        options: {
          credentialsRole: this.role,
          requestParameters: {
            "integration.request.path.item": "method.request.path.item",
          },
          integrationResponses: [{ statusCode: "200" }],
        },
      }),
      {
        ...(authorizer ? { authorizer } : {}),
        requestParameters: {
          "method.request.path.item": true,
        },
        requestModels: {
          "image/jpeg": uploadModel,
          "image/png": uploadModel,
        },
        methodResponses: [getResponse(uploadModel)],
      }
    );

    objectKeyResource.addMethod(
      "GET",
      new AwsIntegration({
        service: "s3",
        integrationHttpMethod: "GET",
        path: `${bucket.bucketName}/{item}`,
        options: {
          credentialsRole: this.role,
          requestParameters: {
            "integration.request.path.item": "method.request.path.item",
          },
          integrationResponses: [
            {
              statusCode: "200",
              contentHandling: ContentHandling.CONVERT_TO_BINARY,
            },
          ],
        },
      }),
      {
        ...(authorizer ? { authorizer } : {}),
        requestParameters: {
          "method.request.path.item": true,
        },
        methodResponses: [
          {
            statusCode: "200",
            responseModels: { "audio/mp3": downloadModel },
          },
        ],
      }
    );
  }
}

function getResponse(responseModel?: Model) {
  return responseModel
    ? {
        statusCode: "200",
        responseModels: { "application/json": responseModel },
      }
    : { statusCode: "200" };
}

const EVENT_INTEGRATION_OPTIONS = {
  proxy: false,
  passthroughBehavior: PassthroughBehavior.WHEN_NO_TEMPLATES,
  requestParameters: {
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
};
