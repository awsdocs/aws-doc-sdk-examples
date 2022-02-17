import 'source-map-support/register';
import * as cdk from '@aws-cdk/core';
import {CfnOutput} from '@aws-cdk/core';
import * as cognito from "@aws-cdk/aws-cognito";
import {Bucket, HttpMethods} from '@aws-cdk/aws-s3';
import * as iam from "@aws-cdk/aws-iam";
import {Role, ServicePrincipal, Effect, PolicyStatement} from '@aws-cdk/aws-iam';
import {Topic} from '@aws-cdk/aws-sns';

export class SetupStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    let rekognition = new ServicePrincipal('rekognition.amazonaws.com');

    let bucket = new Bucket(this, 'rekognition-demo-bucket', {
      cors: [{
        allowedHeaders: ["*"],
        allowedMethods: [HttpMethods.GET, HttpMethods.PUT, HttpMethods.DELETE],
        allowedOrigins: ["*"],
        exposedHeaders: ["ETag", "x-amz-meta-custom-header"]
      }],
      removalPolicy: cdk.RemovalPolicy.DESTROY
    });
    bucket.grantReadWrite(rekognition);

    let topic = new Topic(this, 'rekognition-demo-topic', {});

    let role = new Role(this, 'rekognition-video-analyzer-role', {
      assumedBy: rekognition
    });
    topic.grantPublish(role);
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
          actions: ["rekognition:StartFaceDetection", "rekognition:GetFaceDetection"],
          resources: ["*"],
        })
    );
      unauthenticatedRole.addToPolicy(
          new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["s3:PutObject", "s3:DeleteObject", "s3:ListBucket", "s3:GetObject"],
              resources: ["*"],
          })
      );
      unauthenticatedRole.addToPolicy(
          new PolicyStatement({
              effect: Effect.ALLOW,
              actions: ["ses:SendEmail"],
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
    new CfnOutput(this, 'BucketName', {value: bucket.bucketName});
    new CfnOutput(this, 'TopicArn', {value: topic.topicArn});
    new CfnOutput(this, "Identity pool id", { value: myIdentityPool.ref });
    new CfnOutput(this, "IAM Role ARN", { value: unauthenticatedRole.roleArn});
  }
}

const stackName = 'video-analyzer'

const app = new cdk.App();

new SetupStack(app, stackName);
