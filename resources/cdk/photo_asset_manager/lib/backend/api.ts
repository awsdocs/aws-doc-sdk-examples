/**
 * Cognito User Pool
 * API Gateway
 */

import {
  AuthorizationType,
  CognitoUserPoolsAuthorizer,
  Cors,
  GatewayResponse,
  LambdaIntegration,
  LogGroupLogDestination,
  MethodLoggingLevel,
  Model,
  PassthroughBehavior,
  ResponseType,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Function } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { PamAuth } from "./auth";
import { PamLambda } from "./lambdas";
import * as models from "./api_models";
import { PAM_STACK_NAME } from "../common";
import { LogGroup } from "aws-cdk-lib/aws-logs";

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
        }
      
   //     deployOptions: {
   //       accessLogDestination: new LogGroupLogDestination(access),
   //       loggingLevel: MethodLoggingLevel.INFO,
   //       dataTraceEnabled: true,
   //     },
      }
    ));

    new GatewayResponse(this, "PamGatewayResponse", {
      restApi,
      type: ResponseType.DEFAULT_4XX,
      responseHeaders: { "Access-Control-Allow-Origin": "'*'" },
    });

    const lambdas = props.lambdas.fns;
    this.empty = new models.Empty(this, { restApi });
    this.route("labels", lambdas.labels, "GET", {
      response: new models.LabelsResponseModel(this, { restApi }),
    });

    this.route("upload", lambdas.upload, "PUT", {
      request: new models.UploadRequestModel(this, { restApi }),
      response: new models.UploadResponseModel(this, { restApi }),
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
      request: requestModel = this.empty,
      response: responseModel = this.empty,
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
      new LambdaIntegration(fn, { ...(event && EVENT_INTEGRATION_OPTIONS) }),
      {
        requestModels: { "application/json": requestModel },
        methodResponses: [statusOkResponse(responseModel)],
        authorizer: this.apigAuthorizer,
        authorizationType: AuthorizationType.COGNITO,
      }
    );
  }
}

function statusOkResponse(response: Model) {
  return {
    statusCode: "200",
    responseParameters: {
      "method.response.header.Access-Control-Allow-Origin": true,
    },
    responseModels: { "application/json": response },
  };
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
