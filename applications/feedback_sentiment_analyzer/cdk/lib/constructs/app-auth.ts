import { RemovalPolicy } from "aws-cdk-lib";
import {
  AccountRecovery,
  CfnUserPoolUser,
  Mfa,
  OAuthScope,
  UserPool,
  UserPoolClient,
  UserPoolDomain,
} from "aws-cdk-lib/aws-cognito";
import { Construct } from "constructs";

export interface AppAuthProps {
  email: string;
  callbackDomain: string;
}

export class AppAuth extends Construct {
  readonly userPool: UserPool;
  readonly userPoolDomain: UserPoolDomain;
  readonly appClient: UserPoolClient;
  readonly user: CfnUserPoolUser;

  constructor(scope: Construct, id: string, props: AppAuthProps) {
    super(scope, id);
    this.userPool = new UserPool(this, "login-pool", {
      passwordPolicy: {
        minLength: 6,
        requireDigits: false,
        requireLowercase: false,
        requireSymbols: false,
        requireUppercase: false,
      },
      mfa: Mfa.OFF,
      accountRecovery: AccountRecovery.NONE,
      selfSignUpEnabled: false,
      removalPolicy: RemovalPolicy.DESTROY,
    });

    this.userPoolDomain = this.userPool.addDomain("login-domain", {
      cognitoDomain: {
        domainPrefix: id,
      },
    });

    const callbackUrl = `https://${props.callbackDomain}`;
    this.appClient = this.userPool.addClient("AppClient", {
      oAuth: {
        callbackUrls: [callbackUrl],
        logoutUrls: [callbackUrl],
        flows: { implicitCodeGrant: true },
        scopes: [OAuthScope.PROFILE, OAuthScope.OPENID],
      },
    });

    this.user = new CfnUserPoolUser(this, "default-user", {
      userPoolId: this.userPool.userPoolId,
      userAttributes: [
        {
          name: "email",
          value: props.email,
        },
      ],
      username: props.email,
    });
  }
}
