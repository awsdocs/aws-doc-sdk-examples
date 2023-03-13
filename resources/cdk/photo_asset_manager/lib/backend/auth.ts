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
import { PAM_STACK_NAME } from "../common";

export interface PamAuthProps {
  email: string;
  cloudfrontDistributionUrl: string;
}

export class PamAuth extends Construct {
  readonly userPool: UserPool;
  readonly userPoolDomain: UserPoolDomain;
  readonly appClient: UserPoolClient;
  readonly user: CfnUserPoolUser;
  constructor(scope: Construct, id: string, props: PamAuthProps) {
    super(scope, id);
    this.userPool = new UserPool(this, "UserPool", {
      passwordPolicy: {
        // Password is 6 characters minimum length and no complexity requirements,
        minLength: 6,
        requireDigits: false,
        requireLowercase: false,
        requireSymbols: false,
        requireUppercase: false,
      },
      mfa: Mfa.OFF, // no MFA,
      // no self-service account recovery,
      accountRecovery: AccountRecovery.NONE,
      selfSignUpEnabled: false, // no self-registration,
      // no assisted verification,
      // no required or custom attributes,
      // send email with cognito.
      removalPolicy: RemovalPolicy.DESTROY,
    });

    this.userPoolDomain = this.userPool.addDomain("PamLoginDomain", {
      cognitoDomain: {
        domainPrefix: PAM_STACK_NAME.toLowerCase(),
      },
    });

    this.appClient = this.userPool.addClient("AppClient", {
      oAuth: {
        callbackUrls: [`https://${props.cloudfrontDistributionUrl}`],
        flows: { implicitCodeGrant: true },
        scopes: [OAuthScope.PROFILE, OAuthScope.OPENID]
      },
    });

    this.user = new CfnUserPoolUser(this, "UserPool-DefaultUser", {
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
