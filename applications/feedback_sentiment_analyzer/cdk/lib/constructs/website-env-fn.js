const {
  CognitoIdentityProviderClient,
  paginateListUserPoolClients,
} = require("@aws-sdk/client-cognito-identity-provider");

/**
 * @type {import("@types/aws-lambda").APIGatewayProxyHandler}
 * @param {import("@types/aws-lambda").APIGatewayProxyEvent} event
 */
async function handler() {
  const idp = new CognitoIdentityProviderClient();

  const clientsPaginator = paginateListUserPoolClients(
    { client: idp },
    { UserPoolId: process.env.COGNITO_USER_POOL_ID }
  );

  /**
   * @type {import("@aws-sdk/client-cognito-identity-provider").UserPoolClientDescription[]}
   */
  let clients = [];

  for await (const client of clientsPaginator) {
    clients.push(...client.UserPoolClients);
  }

  // This should be refactored into something more robust than just grabbing the first client.
  const client = clients[0];
  const cognitoAppClientId = client.ClientId;

  return {
    statusCode: 200,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      COGNITO_SIGN_IN_URL: `${process.env.COGNITO_USER_POOL_BASE_URL}/oauth2/authorize?response_type=token&client_id=${cognitoAppClientId}`,
      COGNITO_SIGN_OUT_URL: `${process.env.COGNITO_USER_POOL_BASE_URL}/logout?client_id=${cognitoAppClientId}`,
    }),
  };
}

exports.handler = handler;
