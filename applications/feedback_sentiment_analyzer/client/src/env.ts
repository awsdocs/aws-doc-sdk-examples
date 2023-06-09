interface AppEnv extends Window {
  APP_ENV: {
    API_GATEWAY_BASE_URL: string;
    COGNITO_SIGN_IN_URL: string;
    COGNITO_SIGN_OUT_URL: string;
  };
}

export const {
  API_GATEWAY_BASE_URL,
  COGNITO_SIGN_IN_URL,
  COGNITO_SIGN_OUT_URL,
} = (window as unknown as AppEnv).APP_ENV;
