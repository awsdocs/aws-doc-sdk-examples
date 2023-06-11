exports.handler = async function () {
  return `
     window.APP_ENV = {
      API_GATEWAY_BASE_URL: "${process.env.API_GATEWAY_BASE_URL}",
      COGNITO_SIGN_IN_URL: "${process.env.COGNITO_USER_POOL_BASE_URL}/oauth2/authorize?response_type=token&client_id=${process.env.COGNITO_APP_CLIENT_ID}",
      COGNITO_SIGN_OUT_URL: "${process.env.COGNITO_USER_POOL_BASE_URL}/logout?client_id=${process.env.COGNITO_APP_CLIENT_ID}"
     };`
};
