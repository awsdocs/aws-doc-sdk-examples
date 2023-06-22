import { Stack, CfnOutput, RemovalPolicy } from "aws-cdk-lib";
import { Construct } from "constructs";

import { APP_LANG, APP_EMAIL, PREFIX } from "./env";
import { AppLambdas } from "./constructs/app-lambdas";
import { getFunctions as getFunctionConfigs } from "./functions";
import { AppStateMachine } from "./constructs/app-state-machine";
import { AppS3Website } from "./constructs/app-s3-website";
import { AppEnvLambda } from "./constructs/app-env-lambda";
import {
  CognitoUserPoolsAuthorizer,
  Cors,
  RestApi,
} from "aws-cdk-lib/aws-apigateway";
import {
  AllowedMethods,
  CachePolicy,
  Distribution,
  OriginRequestPolicy,
  ResponseHeadersPolicy,
  ViewerProtocolPolicy,
} from "aws-cdk-lib/aws-cloudfront";
import { RestApiOrigin, S3Origin } from "aws-cdk-lib/aws-cloudfront-origins";
import { AppAuth } from "./constructs/app-auth";
import { AppRoutes } from "./constructs/app-routes";
import { Empty, EnvModel, UploadModel } from "./constructs/app-api-models";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { Rule } from "aws-cdk-lib/aws-events";
import { SfnStateMachine } from "aws-cdk-lib/aws-events-targets";

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
      binaryMediaTypes: ["image/jpeg", "image/png"],
      deployOptions: {
        // These settings require extra permissions. See https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-logging.html
        // accessLogDestination: new LogGroupLogDestination(logGroup),
        // accessLogFormat: AccessLogFormat.jsonWithStandardFields(),
      },
    });

    // Create static S3 website behind a CloudFront distribution.
    const website = new AppS3Website(this, "client", {
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
          allowedMethods: AllowedMethods.ALLOW_ALL,
        },
      },
    });
    website.grantDistributionRead(distribution);

    // Create Cognito user pool and client.
    const auth = new AppAuth(this, `${prefix}-auth`, {
      email: APP_EMAIL,
      callbackDomain: distribution.domainName,
    });

    // Create env lambda.
    const variables = {
      COGNITO_USER_POOL_BASE_URL: auth.userPoolDomain.baseUrl(),
      COGNITO_USER_POOL_ID: auth.userPool.userPoolId,
    };

    const envLambda = new AppEnvLambda(this, { variables });
    auth.userPool.grant(envLambda.fn, "cognito-idp:ListUserPoolClients");

    // Create API routes.
    const routes = new AppRoutes(this, `${prefix}-routes`, {
      api,
    });

    // Create authorizer.
    const userPoolAuthorizer = new CognitoUserPoolsAuthorizer(
      this,
      "pool-authorizer",
      { cognitoUserPools: [auth.userPool] }
    );

    // Add env route.
    routes.addLambdaRoute({
      path: "env",
      method: "GET",
      fn: envLambda.fn,
      model: {
        response: new EnvModel(this, { restApi: api }),
      },
    });

    // Add direct S3 upload route.
    const uploadBucket = new Bucket(this, "upload-bucket", {
      enforceSSL: true,
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
      eventBridgeEnabled: true,
    });

    routes.addDirectS3Route({
      path: "upload",
      method: "PUT",
      bucket: uploadBucket,
      allowActions: ["s3:PutObject"],
      authorizer: userPoolAuthorizer,
      model: {
        request: new UploadModel(this, { restApi: api }),
      },
    });

    // Register Amazon EventBridge rule to trigger state machine.
    new Rule(this, "s3-put-start-step-function", {
      eventPattern: {
        source: ["aws.s3"],
        detailType: ["Object Created"],
        detail: {
          bucket: {
            name: [uploadBucket.bucketName],
          },
        },
      },
      targets: [new SfnStateMachine(appStateMachine.stateMachine)],
    });

    // Output useful values.
    new CfnOutput(this, `${prefix}-website-url`, {
      value: `https://${distribution.domainName}/`,
    });
  }
}
