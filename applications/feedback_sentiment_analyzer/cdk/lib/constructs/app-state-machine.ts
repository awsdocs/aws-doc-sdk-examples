import { Construct } from "constructs";
import { AppFunction } from "./app-lambdas";
import { LambdaInvoke } from "aws-cdk-lib/aws-stepfunctions-tasks";
import { Chain, StateMachine } from "aws-cdk-lib/aws-stepfunctions";

export class AppStateMachine extends Construct {
  readonly stateMachine: StateMachine;

  constructor(scope: Construct, id: string, functions: AppFunction[]) {
    super(scope, id);
    this.stateMachine = this.createStateMachine(functions);
  }

  private createStateMachine([first, ...rest]: AppFunction[]) {
    if (rest.length < 1) {
      throw new Error(
        "Failed to created state machine. At least 2 functions are required."
      );
    }

    const makeInvoke = ({ name, fn }: AppFunction) =>
      new LambdaInvoke(this, name, {
        lambdaFunction: fn,
        payloadResponseOnly: true,
      });

    const definition = rest.reduce(
      (chain, fn) => chain.next(makeInvoke(fn)),
      Chain.start(makeInvoke(first))
    );

    return new StateMachine(this, "state-machine", { definition });
  }
}
