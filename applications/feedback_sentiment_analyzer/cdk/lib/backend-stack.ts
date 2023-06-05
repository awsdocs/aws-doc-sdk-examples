import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";

import { APP_LANG, PREFIX } from "./env";
import { AppLambdas } from "./constructs/app-lambdas";
import { getFunctions as getFunctionConfigs } from "./functions";
import { AppStateMachine } from "./constructs/app-state-machine";

export class BackendStack extends cdk.Stack {
  constructor(scope: Construct) {
    const prefix = `fsa-${PREFIX}-be`;
    super(scope, prefix);
    
    // Create AWS Lambda functions.
    const fnConfigs = getFunctionConfigs(APP_LANG);
    const appLambdas = new AppLambdas(this, "fn", fnConfigs);

    // Create state machine.
    new AppStateMachine(
      this,
      prefix,
      appLambdas.functions
    );
  }
}
