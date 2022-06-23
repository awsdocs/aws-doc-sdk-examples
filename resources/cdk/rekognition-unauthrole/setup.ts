import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {CfnOutput} from '@aws-cdk/core';
import * as cognito from "@aws-cdk/aws-cognito";
import * as iam from "@aws-cdk/aws-iam";
import {Role, ServicePrincipal, Effect, PolicyStatement} from '@aws-cdk/aws-iam';


export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    let rekognition = new ServicePrincipal('rekognition.amazonaws.com');


    let role = new Role(this, 'rekognition-video-analyzer-role', {
      assumedBy: rekognition
    });

    const myIdentityPool = new cognito.CfnIdentityPool(
        this,
        "ExampleIdentityPool",
        {
          allowUnauthenticatedIdentities: true,
        }
    );
    const unauthenticatedRole = new iam.Role(
        this,
        "CognitoDefaultUnauthenticatedRole",
        {
          assumedBy: new iam.FederatedPrincipal(
              "cognito-identity.amazonaws.com",
              {
                StringEquals: {
                  "cognito-identity.amazonaws.com:aud": myIdentityPool.ref,
                },
                "ForAnyValue:StringLike": {
                  "cognito-identity.amazonaws.com:amr": "unauthenticated",
                },
              },
              "sts:AssumeRoleWithWebIdentity"
          ),
        }
    );
    unauthenticatedRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["mobileanalytics:PutEvents", "cognito-sync:*"],
          resources: ["*"],
        })
    );
    unauthenticatedRole.addToPolicy(
        new PolicyStatement({
          effect: Effect.ALLOW,
          actions: ["rekognition:DetectFaces"],
          resources: ["*"],
        })
    );
    const defaultPolicy = new cognito.CfnIdentityPoolRoleAttachment(
        this,
        "DefaultValid",
        {
          identityPoolId: myIdentityPool.ref,
          roles: {
            unauthenticated: unauthenticatedRole.roleArn,
          },
        }
    );
    new CfnOutput(this, "Identity pool id", { value: myIdentityPool.ref });
  }
}

const stackName = 'estimate-age'

const app = new cdk.App();

new SetupStack(app, stackName);
