import { Construct } from "constructs";
import { readFileSync } from "fs";
import { join } from "path";
import { Function, Runtime, Code } from "aws-cdk-lib/aws-lambda";

interface AppEnvLambdaProps {
  variables: Record<string, string>;
}

export class AppEnvLambda extends Construct {
  readonly fn: Function;

  constructor(scope: Construct, props: AppEnvLambdaProps) {
    super(scope, "env-function");

    this.fn = new Function(this, "env-lambda", {
      runtime: Runtime.NODEJS_18_X,
      environment: props.variables,

      code: Code.fromInline(
        readFileSync(join(__dirname, "website-env-fn.js")).toString()
      ),
      handler: "index.handler",
    });
  }
}
