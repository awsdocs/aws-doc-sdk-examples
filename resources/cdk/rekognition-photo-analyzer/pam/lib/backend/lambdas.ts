import { Code, Function, Runtime } from "aws-cdk-lib/aws-lambda";
import { Construct } from "constructs";
import { PamBuckets, PamTables } from "./resources";

export interface PamLambdasStrategyHandlers {
  detectLabels: string;
  labels: string;
  // zipArchive: string;
  upload: string;
  copy: string;
  download: string;
  // archive: string;
}

export interface PamLambdasStrategy {
  runtime: Runtime;
  codeAsset: () => Code;
  handlers: PamLambdasStrategyHandlers;
}

export interface PamLambdasProps {
  buckets: PamBuckets;
  tables: PamTables;
  strategy: PamLambdasStrategy;
}

export class PamLambda extends Construct {
  readonly fns: { readonly [k in keyof PamLambdasStrategyHandlers]: Function };
  constructor(scope: Construct, id: string, props: PamLambdasProps) {
    super(scope, id);

    const environment = {
      JOBS_TABLE_NAME: props.tables.jobs.tableName,
      LABELS_TABLE_NAME: props.tables.labels.tableName,
      STORAGE_BUCKET_NAME: props.buckets.storage.bucketName,
      WORKING_BUCKET_NAME: props.buckets.working.bucketName,
    };

    const code = props.strategy.codeAsset();
    const runtime = props.strategy.runtime;
    const handlers = props.strategy.handlers;
    const makeLambda = (name: string, handler: string): Function =>
      new Function(this, name, { runtime, handler, code, environment });

    const detectLabels = makeLambda("DetectLabelsFn", handlers.detectLabels);
    // const zipArchive = makeLambda("ZipArchiveFn", handlers.zipArchive);
    const labels = makeLambda("LabelsFn", handlers.labels);
    const upload = makeLambda("UploadFn", handlers.upload);
    const copy = makeLambda("CopyFn", handlers.copy);
    const download = makeLambda("DowloadFn", handlers.download);
    // const archive = makeLambda("ArchiveFn", handlers.archive);

    this.fns = {
      detectLabels,
      // zipArchive,
      labels,
      upload,
      copy,
      download,
      // archive,
    };
  }
}
