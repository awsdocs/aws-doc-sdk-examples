import {
  AwsIntegration,
  IAuthorizer,
  LambdaIntegration,
  Model,
  PassthroughBehavior,
  Resource,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { Empty } from "./app-api-models";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { PolicyStatement, Role, ServicePrincipal } from "aws-cdk-lib/aws-iam";

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

export interface DirectS3RouteProps extends RouteProps {
  bucket: Bucket;
  // For example, ["s3:PutObject", "s3:GetObject"]
  allowActions: string[];
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

  addDirectS3Route({
    path,
    method,
    authorizer,
    allowActions,
    model,
    bucket,
  }: DirectS3RouteProps) {
    const resource = this.apiRoot.addResource(path);
    const objectKeyResource = resource.addResource("{item}");
    this.role.addToPolicy(
      new PolicyStatement({
        actions: allowActions,
        resources: [`${bucket.bucketArn}/*`],
      })
    );

    objectKeyResource.addMethod(
      method,
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
        requestParameters: {
          "method.request.path.item": true,
        },
        requestModels: {
          "image/jpeg": model.request ?? this.emptyModel,
          "image/png": model.request ?? this.emptyModel,
        },
        methodResponses: [getResponse(model.response)],
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
