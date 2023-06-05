import { CfnOutput, Stack, StackProps } from "aws-cdk-lib";
import { PolicyStatement } from "aws-cdk-lib/aws-iam";
import { LambdaDestination } from "aws-cdk-lib/aws-s3-notifications";
import { Topic } from "aws-cdk-lib/aws-sns";
import { EmailSubscription } from "aws-cdk-lib/aws-sns-subscriptions";
import { Construct } from "constructs";
import {
  API_GATEWAY_URL_NAME,
  COGNITO_APP_CLIENT_ID_NAME,
  COGNITO_USER_POOL_ID_NAME,
  COGNITO_USER_POOL_BASE_URL,
  PAM_EMAIL,
} from "../common";
import { PamApi } from "./api";
import { PamLambda, PamLambdasStrategy } from "./lambdas";
import { PamBuckets, PamTables } from "./resources";

export interface PamStackProps extends StackProps {
  // name: string;
  email: string;
  strategy: PamLambdasStrategy;
  cloudfrontDistributionUrl: string;
}

export class PamStack extends Stack {
  readonly tables: PamTables;
  readonly buckets: PamBuckets;
  readonly lambdas: PamLambda;
  readonly topic: Topic;
  readonly api: PamApi;

  constructor(scope: Construct, id: string, props: PamStackProps) {
    super(scope, id, props);
    this.tables = new PamTables(this, "PamTables");
    this.buckets = new PamBuckets(this, "PamBuckets");
    this.topic = new Topic(this, "PamNotifier");
    this.topic.addSubscription(new EmailSubscription(PAM_EMAIL));
    this.lambdas = new PamLambda(this, "PamLambdas", {
      buckets: this.buckets,
      tables: this.tables,
      topic: this.topic,
      strategy: props.strategy,
    });
    this.api = new PamApi(this, "PamApi", {
      lambdas: this.lambdas,
      email: props.email,
      cloudfrontDistributionUrl: props.cloudfrontDistributionUrl,
    });

    this.permissions();
    this.outputs();
  }

  private permissions() {
    // DetectLabelsFn
    {
      const fn = this.lambdas.fns.detectLabels;
      // Create trigger for AWS Lambda function with image type suffixes.
      const destination = new LambdaDestination(fn);
      this.buckets.storage.addObjectCreatedNotification(destination, {
        suffix: ".jpg",
      });
      this.buckets.storage.addObjectCreatedNotification(destination, {
        suffix: ".jpeg",
      });

      // Add Amazon Rekognition permissions.
      fn.role?.addToPrincipalPolicy(
        new PolicyStatement({
          actions: ["rekognition:DetectLabels"],
          resources: ["*"],
        })
      );

      // Grant permissions for DetectLabels to read/write to an Amazon DynamoDB table and
      // Amazon Simple Storage Service (Amazon S3) bucket.
      this.tables.labels.grantReadWriteData(fn);
      this.buckets.storage.grantReadWrite(fn);
    }

    // LabelsFn
    {
      const fn = this.lambdas.fns.labels;
      this.tables.labels.grantReadData(fn);
    }

    // UploadFn
    {
      const fn = this.lambdas.fns.upload;
      this.buckets.storage.grantPut(fn);
    }

    // DownloadFn
    {
      const fn = this.lambdas.fns.download;
      this.tables.labels.grantReadData(fn);
      this.buckets.storage.grantRead(fn);
      this.buckets.working.grantReadWrite(fn);
      this.topic.grantPublish(fn);
      fn.role?.addToPrincipalPolicy(
        new PolicyStatement({ actions: ["sns:subscribe"], resources: ["*"] })
      );
    }

  }

  private outputs() {
    [
      [COGNITO_USER_POOL_ID_NAME, this.api.auth.userPool.userPoolId],
      [COGNITO_USER_POOL_BASE_URL, this.api.auth.userPoolDomain.baseUrl()],
      [COGNITO_APP_CLIENT_ID_NAME, this.api.auth.appClient.userPoolClientId],
      [API_GATEWAY_URL_NAME, this.api.restApi.url],
    ].forEach(([exportName, value]) => {
      new CfnOutput(this, exportName, { value, exportName });
    });
  }
}
