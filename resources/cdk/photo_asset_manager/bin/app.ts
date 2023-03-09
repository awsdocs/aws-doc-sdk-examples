import {
  CloudFormationClient,
  Export,
  ListExportsCommand,
} from "@aws-sdk/client-cloudformation";
import { App } from "aws-cdk-lib";
import { PamStack } from "../lib/backend/stack";
import { getStrategy } from "../lib/backend/strategies";
import {
  API_GATEWAY_URL_NAME,
  COGNITO_APP_CLIENT_ID_NAME,
  COGNITO_USER_POOL_ID_NAME,
  PAM_EMAIL,
  PAM_LANG,
  PAM_NAME,
  PAM_STACK_NAME,
} from "../lib/common";
import { PamFrontendStack } from "../lib/frontend/stack";

const cfnClient = new CloudFormationClient({});
cfnClient.send(new ListExportsCommand({})).then(({ Exports = [] }) => {
  const exports = filterExportsForStack(Exports);

  const app = new App();

  // BE Stack
  new PamStack(app, PAM_STACK_NAME, {
    email: PAM_EMAIL,
    strategy: getStrategy(PAM_LANG),
  });

  // FE Stack, maybe
  const cognitoUserPoolId: string = exports[COGNITO_USER_POOL_ID_NAME];
  const cognitoAppClientId: string = exports[COGNITO_APP_CLIENT_ID_NAME];
  const apiGatewayUrl: string = exports[API_GATEWAY_URL_NAME];

  if (cognitoAppClientId && cognitoUserPoolId && apiGatewayUrl) {
    new PamFrontendStack(app, `${PAM_NAME}-FE-PAM`, {
      cognitoAppClientId,
      cognitoUserPoolId,
      apiGatewayUrl,
    });
  }
});

function filterExportsForStack(exports: Export[]): Record<string, string> {
  return exports
    .filter(
      (exp) =>
        exp.ExportingStackId?.includes(PAM_STACK_NAME) &&
        exp.Name !== undefined &&
        exp.Value !== undefined
    )
    .reduce(
      (values, { Name, Value }): Record<string, string> => ({
        ...values,
        [Name!]: Value!,
      }),
      {} as Record<string, string>
    );
}
