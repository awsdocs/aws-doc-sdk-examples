import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";

import { APP_LANG, PREFIX } from "./env";
import { AppLambdas } from "./constructs/app-lambdas";
import { getFunctions as getFunctionConfigs } from "./functions";
import { AppStateMachine } from "./constructs/app-state-machine";
import { CloudFrontWebsite } from "./constructs/cloud-front-website";
import { EnvFunction } from "./constructs/env-lambda";

export class AppStack extends cdk.Stack {
  constructor(scope: Construct) {
    const prefix = `fsa-${PREFIX}`;
    super(scope, prefix);

    // Create AWS Lambda functions.
    const fnConfigs = getFunctionConfigs(APP_LANG);
    const appLambdas = new AppLambdas(this, "fn", fnConfigs);

    // Create state machine.
    new AppStateMachine(this, prefix, appLambdas.functions);

    // Create function to get environment variables as a script.
    new EnvFunction(this, {
      variables: {
        COGNITO_USER_POOL_BASE_URL: "x",
        COGNITO_APP_CLIENT_ID: "x",
      }
    })

    // Create API

    // Create CloudFront distribution with API Gateway as the default behavior.

    // Create static S3 website behind a CloudFront distribution.
    new CloudFrontWebsite(this, "client", {
      distribution,
      assetPath: "../client",
      cognitoAppClientId: "a",
      cognitoUserPoolBaseUrl: "a",
    });
  }
}
