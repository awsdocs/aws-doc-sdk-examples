import { Duration, RemovalPolicy } from "aws-cdk-lib";
import { Code, Function, Runtime } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { PamBuckets, PamTables } from "./resources";
import { Topic } from "aws-cdk-lib/aws-sns";

export interface PamLambdasStrategyHandlers {
  detectLabels: string;
  labels: string;
  upload: string;
  download: string;
}

export interface PamLambdasStrategy {
  timeout?: Duration;
  memorySize?: number; // In megabytes.
  runtime: Runtime;
  codeAsset: () => Code;
  handlers: PamLambdasStrategyHandlers;
}

export interface PamLambdasProps {
  buckets: PamBuckets;
  tables: PamTables;
  strategy: PamLambdasStrategy;
  topic: Topic;
}

export class PamLambda extends Construct {
  readonly fns: { readonly [k in keyof PamLambdasStrategyHandlers]: Function };
  constructor(scope: Construct, id: string, props: PamLambdasProps) {
    super(scope, id);

    const environment = {
      LABELS_TABLE_NAME: props.tables.labels.tableName,
      STORAGE_BUCKET_NAME: props.buckets.storage.bucketName,
      WORKING_BUCKET_NAME: props.buckets.working.bucketName,
      NOTIFICATION_TOPIC: props.topic.topicArn,
    };

    const code = props.strategy.codeAsset();
    const { runtime, handlers, timeout, memorySize } = props.strategy;
    const makeLambda = (name: string, handler: string): Function =>
      new Function(this, name, {
        runtime,
        handler,
        code,
        environment,
        timeout,
        memorySize,
      });

    const detectLabels = makeLambda("DetectLabelsFn", handlers.detectLabels);
    const labels = makeLambda("LabelsFn", handlers.labels);
    const upload = makeLambda("UploadFn", handlers.upload);
    const download = makeLambda("PrepareDownloadFn", handlers.download);

    this.fns = {
      detectLabels,
      labels,
      upload,
      download,
    };
  }
}
