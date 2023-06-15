import { Stack, CfnOutput } from "aws-cdk-lib";
import { Construct } from "constructs";

import { APP_LANG, APP_EMAIL, PREFIX } from "./env";
import { AppLambdas } from "./constructs/app-lambdas";
import { getFunctions as getFunctionConfigs } from "./functions";
import { AppStateMachine } from "./constructs/app-state-machine";
import { AppCloudFrontWebsite } from "./constructs/app-cloud-front-website";
import { AppEnvLambda } from "./constructs/app-env-lambda";
import {
  AccessLogFormat,
  Cors,
  JsonSchemaType,
  JsonSchemaVersion,
  LogGroupLogDestination,
  Model,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import { Distribution } from "aws-cdk-lib/aws-cloudfront";
import { RestApiOrigin } from "aws-cdk-lib/aws-cloudfront-origins";
import { LogGroup } from "aws-cdk-lib/aws-logs";
import { AppAuth } from "./constructs/app-auth";
import { AppRoutes } from "./constructs/app-routes";
import { Empty } from "./constructs/app-api-models";

export class AppStack extends Stack {
  constructor(scope: Construct) {
    const prefix = `fsa-${PREFIX}`;
    super(scope, prefix);

    // Create API
    const logGroup = new LogGroup(this, `api-log-group`);
    const api = new RestApi(this, `${prefix}-api`, {
      defaultCorsPreflightOptions: {
        allowOrigins: Cors.ALL_ORIGINS,
        allowCredentials: true,
      },
      deployOptions: {
        accessLogDestination: new LogGroupLogDestination(logGroup),
        accessLogFormat: AccessLogFormat.jsonWithStandardFields(),
      },
    });

    // Create CloudFront distribution with API Gateway as the default behavior.
    const distribution = new Distribution(this, "website-distribution", {
      defaultBehavior: {
        origin: new RestApiOrigin(api),
      },
    });

    // Create Cognito user pool and client.
    const auth = new AppAuth(this, `${prefix}-auth`, {
      email: APP_EMAIL,
      callbackDomain: distribution.domainName,
    });

    // Create AWS Lambda functions.
    const fnConfigs = getFunctionConfigs(APP_LANG);
    const appLambdas = new AppLambdas(this, "fn", fnConfigs);

    // Create state machine.
    new AppStateMachine(this, prefix, appLambdas.functions);

    // Create API routes.
    // const userPoolAuthorizer = new CognitoUserPoolsAuthorizer(
    //   this,
    //   "pool-authorizer",
    //   { cognitoUserPools: [auth.userPool] }
    // );

    const routes = new AppRoutes(this, `${prefix}-routes`, {
      api,
    });

    // Add env route.
    routes.addLambdaRoute({
      path: "env",
      method: "GET",
      fn: new AppEnvLambda(this, {
        variables: {
          // COGNITO_USER_POOL_BASE_URL: auth.userPoolDomain.baseUrl(),
          // COGNITO_APP_CLIENT_ID: auth.appClient.userPoolClientId,
        },
      }).fn,
      model: {
        request: new Empty(this, { restApi: api }),
        response: new Model(this, "env-response-model", {
          restApi: api,
          schema: {
            schema: JsonSchemaVersion.DRAFT4,
            type: JsonSchemaType.STRING,
          },
        }),
      },
    });

    // Create static S3 website behind a CloudFront distribution.
    new AppCloudFrontWebsite(this, "client", {
      distribution,
      sitePath: "home/*",
      assetPath: "../client",
    });

    new CfnOutput(this, `${prefix}-website-url`, {
      value: `https://${distribution.domainName}/home/index.html`,
    });
  }
}
