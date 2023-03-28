import { ok } from "assert";
import { resolve } from "path";

export const PAM_NAME: string = process.env["PAM_NAME"] as string;
export const PAM_EMAIL: string = process.env["PAM_EMAIL"] as string;

ok(PAM_NAME, "Missing PAM_NAME");
ok(PAM_EMAIL, "Missing PAM_EMAIL");

export const PAM_LANG: string = process.env["PAM_LANG"] ?? "Java";
export const PAM_STACK_NAME = `${PAM_NAME}-${PAM_LANG}-PAM`;
export const PAM_FE_INFRA_STACK_NAME = `${PAM_NAME}-FE-Infra-PAM`;
export const PAM_FE_ASSET_STACK_NAME = `${PAM_NAME}-FE-Assets-PAM`

// Cloud Function output names are unique _per account region_, so prefix the stack name.
export const COGNITO_USER_POOL_ID_NAME = `${PAM_STACK_NAME}-CognitoUserPoolId`;
export const COGNITO_APP_CLIENT_ID_NAME = `${PAM_STACK_NAME}-CognitoAppClientID`;
export const COGNITO_USER_POOL_BASE_URL = `${PAM_STACK_NAME}-CognitoUserPoolBaseUrl`;
export const API_GATEWAY_URL_NAME = `${PAM_STACK_NAME}-ApiGatewayUrl`;
export const CLOUDFRONT_DISTRIBUTION_NAME = `${PAM_NAME}-FE-Distribution`;

export const ELROS_PATH = resolve("../../clients/react/elros-pam");
