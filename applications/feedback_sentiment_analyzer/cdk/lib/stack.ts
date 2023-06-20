import { Stack, CfnOutput } from "aws-cdk-lib";
import { Construct } from "constructs";

import { APP_LANG, APP_EMAIL, PREFIX } from "./env";
import { AppLambdas } from "./constructs/app-lambdas";
import { getFunctions as getFunctionConfigs } from "./functions";
import { AppStateMachine } from "./constructs/app-state-machine";
import { AppCloudFrontWebsite } from "./constructs/app-cloud-front-website";
import { AppEnvLambda } from "./constructs/app-env-lambda";
import { Cors, RestApi } from "aws-cdk-lib/aws-apigateway";
import {
  CachePolicy,
  Distribution,
  OriginRequestPolicy,
  ResponseHeadersPolicy,
  ViewerProtocolPolicy,
} from "aws-cdk-lib/aws-cloudfront";
import { RestApiOrigin, S3Origin } from "aws-cdk-lib/aws-cloudfront-origins";
import { AppAuth } from "./constructs/app-auth";
import { AppRoutes } from "./constructs/app-routes";
import { Empty, EnvModel } from "./constructs/app-api-models";

export class AppStack extends Stack {
  constructor(scope: Construct) {
    const prefix = `fsa-${PREFIX}`;
    super(scope, prefix);

    // Create AWS Lambda functions.
    const fnConfigs = getFunctionConfigs(APP_LANG);
    const appLambdas = new AppLambdas(this, "fn", fnConfigs);

    // Create state machine.
    const appStateMachine = new AppStateMachine(
      this,
      prefix,
      appLambdas.functions
    );

    // Create API
    // const logGroup = new LogGroup(this, `api-log-group`);
    const api = new RestApi(this, `${prefix}-api`, {
      defaultCorsPreflightOptions: {
        allowOrigins: Cors.ALL_ORIGINS,
        allowCredentials: true,
      },
      deployOptions: {
        // These settings require extra permissions. See https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-logging.html
        // accessLogDestination: new LogGroupLogDestination(logGroup),
        // accessLogFormat: AccessLogFormat.jsonWithStandardFields(),
      },
    });

    // Create API routes.
    const routes = new AppRoutes(this, `${prefix}-routes`, {
      api,
    });

    // Create static S3 website behind a CloudFront distribution.
    const website = new AppCloudFrontWebsite(this, "client", {
      assetPath: "../client",
    });

    // Create CloudFront distribution
    const s3Origin = new S3Origin(website.bucket);
    const s3OriginConfig = {
      origin: s3Origin,
      viewerProtocolPolicy: ViewerProtocolPolicy.ALLOW_ALL,
      cachePolicy: CachePolicy.CACHING_OPTIMIZED,
      originRequestPolicy: OriginRequestPolicy.ALL_VIEWER_EXCEPT_HOST_HEADER,
    };
    const distribution = new Distribution(this, "website-distribution", {
      defaultRootObject: "index.html",
      defaultBehavior: s3OriginConfig,
      additionalBehaviors: {
        "/*.js": {
          ...s3OriginConfig,
          responseHeadersPolicy: new ResponseHeadersPolicy(this, "js-content", {
            customHeadersBehavior: {
              customHeaders: [
                {
                  header: "Content-Type",
                  value: "application/javascript",
                  override: true,
                },
              ],
            },
          }),
        },
        "/api/*": {
          origin: new RestApiOrigin(api),
          viewerProtocolPolicy: ViewerProtocolPolicy.ALLOW_ALL,
          cachePolicy: CachePolicy.CACHING_DISABLED,
          originRequestPolicy: OriginRequestPolicy.CORS_S3_ORIGIN,
        },
      },
    });
    website.attachPolicy(distribution);

    // Create Cognito user pool and client.
    const auth = new AppAuth(this, `${prefix}-auth`, {
      email: APP_EMAIL,
      callbackDomain: distribution.domainName,
    });

    // const userPoolAuthorizer = new CognitoUserPoolsAuthorizer(
    //   this,
    //   "pool-authorizer",
    //   { cognitoUserPools: [auth.userPool] }
    // );

    // Create env lambda.
    const variables = {
      COGNITO_USER_POOL_BASE_URL: auth.userPoolDomain.baseUrl(),
      COGNITO_USER_POOL_ID: auth.userPool.userPoolId,
    };

    const envLambda = new AppEnvLambda(this, { variables });
    auth.userPool.grant(envLambda.fn, "cognito-idp:ListUserPoolClients");

    // Add env route.
    routes.addLambdaRoute({
      path: "env",
      method: "GET",
      fn: envLambda.fn,
      model: {
        request: new Empty(this, { restApi: api }),
        response: new EnvModel(this, { restApi: api }),
      },
    });

    new CfnOutput(this, `${prefix}-website-url`, {
      value: `https://${distribution.domainName}/`,
    });
  }
}
