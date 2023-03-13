import { CfnOutput, Stack, StackProps } from "aws-cdk-lib";
import { PolicyStatement } from "aws-cdk-lib/aws-iam";
import { LambdaDestination } from "aws-cdk-lib/aws-s3-notifications";
import { Construct } from "constructs";
import {
  API_GATEWAY_URL_NAME,
  COGNITO_APP_CLIENT_ID_NAME,
  COGNITO_USER_POOL_ID_NAME,
  COGNITO_USER_POOL_BASE_URL,
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
  readonly api: PamApi;

  constructor(scope: Construct, id: string, props: PamStackProps) {
    super(scope, id, props);
    this.tables = new PamTables(this, "PamTables");
    this.buckets = new PamBuckets(this, "PamBuckets");
    this.lambdas = new PamLambda(this, "PamLambdas", {
      buckets: this.buckets,
      tables: this.tables,
      strategy: props.strategy,
    });
    this.api = new PamApi(this, "PamApi", {
      lambdas: this.lambdas,
      email: props.email,
      cloudfrontDistributionUrl: props.cloudfrontDistributionUrl
    });

    this.permissions();
    this.outputs();
  }

  private permissions() {
    // DetectLabelsFn
    {
      const fn = this.lambdas.fns.detectLabels;
      // create trigger for Lambda function with image type suffixes
      const destination = new LambdaDestination(fn);
      this.buckets.storage.addObjectCreatedNotification(destination, {
        suffix: ".jpg",
      });
      this.buckets.storage.addObjectCreatedNotification(destination, {
        suffix: ".jpeg",
      });

      // add Rekognition permissions
      fn.role?.addToPrincipalPolicy(
        new PolicyStatement({
          actions: ["rekognition:DetectLabels"],
          resources: ["*"],
        })
      );

      // grant permissions for DetectLabels to read/write to DynamoDB table and bucket
      this.tables.labels.grantReadWriteData(fn);
      this.buckets.storage.grantReadWrite(fn);
    }

    // ZipArchiveFn
    {
      // const fn = this.lambdas.fns.zipArchive;
      // this.buckets.working.addObjectCreatedNotification(
      //   new LambdaDestination(fn),
      //   { prefix: "job-", suffix: "/report.csv" }
      // );
      // // grant permissions for lambda to read/write to DynamoDB table and bucket
      // this.tables.jobs.grantReadWriteData(fn);
      // this.buckets.working.grantReadWrite(fn);
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
      this.tables.jobs.grantWriteData(fn);
      this.buckets.working.grantPut(fn);
      fn.role?.addToPrincipalPolicy(
        new PolicyStatement({ actions: ["sns:subscribe"], resources: ["*"] })
      );
    }

    // CopyFn
    {
      const fn = this.lambdas.fns.copy;
      this.buckets.storage.grantPut(fn);
    }

    // ArchiveFn
    {
      // const fn = this.lambdas.fns.archive;
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
