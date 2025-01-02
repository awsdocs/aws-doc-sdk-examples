import * as cdk from 'aws-cdk-lib';
import * as events from 'aws-cdk-lib/aws-events';
import * as targets from 'aws-cdk-lib/aws-events-targets';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as s3assets from 'aws-cdk-lib/aws-s3-assets';
import * as s3deploy from 'aws-cdk-lib/aws-s3-deployment'
import * as path from 'path';
import * as lambda from "aws-cdk-lib/aws-lambda";
import * as python from "@aws-cdk/aws-lambda-python-alpha";
import * as fs from 'fs';

export interface NukeStackProps extends cdk.StackProps {
  awsNukeDryRunFlag?: string;
  awsNukeVersion?: string;
  owner?: string;
}

class NukeStack extends cdk.Stack {
  constructor(scope: cdk.App, id: string, props: NukeStackProps) {
    super(scope, id, props);

    // Applying default props
    props = {
      ...props,
      awsNukeDryRunFlag: props.awsNukeDryRunFlag ?? 'true',
      awsNukeVersion: props.awsNukeVersion ?? '2.21.2',
      owner: props.owner ?? 'OpsAdmin',
    };

    // S3 Bucket for storing AWS Nuke binary and configuration
    const nukeS3Bucket = new s3.Bucket(this, 'NukeS3Bucket', {
      bucketName: `nuke-config-bucket-${this.account}-${this.region}`,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
      blockPublicAccess: s3.BlockPublicAccess.BLOCK_ALL,
    });

    const awsNukeConfig = new s3deploy.BucketDeployment(this, 'DeployFile', {
      sources: [s3deploy.Source.data('nuke_generic_config.yaml', fs.readFileSync('nuke_generic_config.yaml', 'utf-8'))],
      destinationBucket: nukeS3Bucket,
      destinationKeyPrefix: 'nuke_generic_config.yaml'
    })
  
    // AWS Lambda Function
    const nukeLambdaRole = new iam.Role(this, 'NukeLambdaRole', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AWSLambdaBasicExecutionRole'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonS3ReadOnlyAccess'),
      ],
    });

    const nukeLambda = new lambda.Function(this, 'NukeLambda', {
      runtime: lambda.Runtime.PYTHON_3_9,
      handler: 'index.handler',
      code: lambda.Code.fromAsset(path.join(__dirname, 'lambda')),
      role: nukeLambdaRole,
      timeout: cdk.Duration.minutes(15),
      environment: {
        AWS_NUKE_DRY_RUN: props.awsNukeDryRunFlag ?? 'true',
        AWS_NUKE_VERSION: props.awsNukeVersion ?? '2.21.2',
        NUKE_S3_BUCKET: nukeS3Bucket.bucketName,
        NUKE_CONFIG_KEY: cdk.Fn.select(0, awsNukeConfig.objectKeys),
      },
    });

    // Grant permissions for the Lambda function to access the S3 bucket
    nukeS3Bucket.grantRead(nukeLambda);

    // EventBridge Rule
    const eventBridgeRule = new events.Rule(this, 'NukeScheduleRule', {
      schedule: events.Schedule.cron({ minute: '0', hour: '7', // Change the schedule as needed
      }),
    });

    eventBridgeRule.addTarget(new targets.LambdaFunction(nukeLambda));
  }
}

const app = new cdk.App();
new NukeStack(app, 'NukeStack', {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT,
    region: process.env.CDK_DEFAULT_REGION,
  },
});