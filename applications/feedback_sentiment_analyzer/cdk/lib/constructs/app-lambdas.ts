import { Construct } from "constructs";
import { Code, Function, FunctionProps } from "aws-cdk-lib/aws-lambda";

export interface AppFunctionConfig extends Omit<FunctionProps, "code"> {
  name: string;
  codeAsset(): Code;
}

export interface AppFunction extends AppFunctionConfig {
  fn: Function;
}

export class AppLambdas extends Construct {
  readonly functions: AppFunction[];

  constructor(scope: Construct, id: string, appFunctions: AppFunctionConfig[]) {
    super(scope, id);

    this.functions = appFunctions.map((appFn) => ({
      ...appFn,
      fn: new Function(this, appFn.name, {
        ...appFn,
        code: appFn.codeAsset(),
      }),
    }));
  }
}
