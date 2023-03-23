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
  CLOUDFRONT_DISTRIBUTION_NAME,
  COGNITO_APP_CLIENT_ID_NAME,
  COGNITO_USER_POOL_BASE_URL,
  COGNITO_USER_POOL_ID_NAME,
  PAM_LANG,
  PAM_STACK_NAME,
  PAM_FE_INFRA_STACK_NAME,
  PAM_EMAIL,
  PAM_FE_ASSET_STACK_NAME,
} from "../lib/common";
import { PamFrontEndAssetStack } from "../lib/frontend/asset-stack";
import { PamFrontEndInfraStack } from "../lib/frontend/infra-stack";

const cfnClient = new CloudFormationClient({});
cfnClient.send(new ListExportsCommand({})).then(({ Exports = [] }) => {
  const exports = {
    ...getExportsForStack(PAM_STACK_NAME, Exports),
    ...getExportsForStack(PAM_FE_INFRA_STACK_NAME, Exports),
  };
  const app = new App();

  // Front-end stack.
  const infraStack = new PamFrontEndInfraStack(app, PAM_FE_INFRA_STACK_NAME);

  const cloudfrontDistributionUrl: string =
    exports[CLOUDFRONT_DISTRIBUTION_NAME];

  if (cloudfrontDistributionUrl) {
    // Back-end stack.
    new PamStack(app, PAM_STACK_NAME, {
      strategy: getStrategy(PAM_LANG),
      email: PAM_EMAIL,
      cloudfrontDistributionUrl
    });
  }

  const cognitoUserPoolId: string = exports[COGNITO_USER_POOL_ID_NAME];
  const cognitoAppClientId: string = exports[COGNITO_APP_CLIENT_ID_NAME];
  const apiGatewayUrl: string = exports[API_GATEWAY_URL_NAME];
  const cognitoUserPoolBaseUrl: string = exports[COGNITO_USER_POOL_BASE_URL];

  // Front-end assets.
  if (
    cognitoAppClientId &&
    cognitoUserPoolId &&
    apiGatewayUrl &&
    cognitoUserPoolBaseUrl &&
    cloudfrontDistributionUrl
  ) {
    new PamFrontEndAssetStack(app, PAM_FE_ASSET_STACK_NAME, {
      apiGatewayUrl,
      bucket: infraStack.bucket,
      cloudfrontDistributionUrl,
      cognitoAppClientId,
      cognitoUserPoolBaseUrl,
      cognitoUserPoolId,
    });
  }
});

function makeStackExportsMap(exports: Export[]) {
  return exports.reduce((stackExports, nextExport) => {
    if (nextExport.ExportingStackId && nextExport.Name && nextExport.Value) {
      const nextStackId = nextExport.ExportingStackId;
      const nextStackName = nextStackId.split("/")[1];
      return {
        ...stackExports,
        [nextStackName]: {
          ...(stackExports[nextStackName] || {}),
          [nextExport.Name]: nextExport.Value,
        },
      };
    } else return stackExports;
  }, {} as Record<string, Record<string, string>>);
}

function getExportsForStack(stackName: string, exports: Export[]) {
  return makeStackExportsMap(exports)[stackName];
}
