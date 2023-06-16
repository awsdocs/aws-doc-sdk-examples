exports.handler = async function () {
  return {
    status: 200,
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      COGNITO_SIGN_IN_URL: `${process.env.COGNITO_USER_POOL_BASE_URL}/oauth2/authorize?response_type=token&client_id=${process.env.COGNITO_APP_CLIENT_ID}`,
      COGNITO_SIGN_OUT_URL: `${process.env.COGNITO_USER_POOL_BASE_URL}/logout?client_id=${process.env.COGNITO_APP_CLIENT_ID}`,
    }),
  };
};
