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
  readonly functions: Record<string, AppFunction> = {};

  constructor(scope: Construct, id: string, appFunctions: AppFunctionConfig[]) {
    super(scope, id);

    this.functions = appFunctions.reduce((fns, nextFn) => {
      fns[nextFn.name] = {
        ...nextFn,
        fn: new Function(this, nextFn.name, {
          ...nextFn,
          code: nextFn.codeAsset(),
        }),
      };
      return fns;
    }, this.functions);
  }

  addPermission(
    fnName: string,
    ...permissions: Parameters<Function["addPermission"]>
  ) {
    this.functions[fnName].fn.addPermission(...permissions);
  }

  grantInvokeAll(...grantee: Parameters<Function["grantInvoke"]>) {
    Object.values(this.functions).forEach((appFn) => appFn.fn.grantInvoke(...grantee));
  }
}
