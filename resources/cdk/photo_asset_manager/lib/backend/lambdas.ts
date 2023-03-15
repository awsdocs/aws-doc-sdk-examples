import { Duration, RemovalPolicy } from "aws-cdk-lib";
import { Code, Function, Runtime } from "aws-cdk-lib/aws-lambda";
import { CfnSchema } from "aws-cdk-lib/aws-eventschemas";
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
  timeout: Duration;
  memorySize: number; // In megabytes
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

  private makeTests() {
    const uploadTests = new CfnSchema(this, "PAMUploadFnTests", {
      registryName: "lambda-testevent-schemas",
      // schemaName: `_${upload.functionName}-schema`,
      schemaName: ``,
      type: "OpenApi3",
      content: JSON.stringify({
        openapi: "3.0.0",
        info: {
          version: "1.0.0",
          title: "Event",
        },
        paths: {},
        components: {
          schemas: {
            Event: {
              type: "object",
              required: ["path", "resource", "body", "httpMethod"],
              properties: {
                body: {
                  type: "string",
                },
                httpMethod: {
                  type: "string",
                },
                path: {
                  type: "string",
                },
              },
            },
          },
          examples: {
            UploadLake: {
              value: {
                body: '{"file_name":"lake.jpeg"}',
                path: "/upload",
                httpMethod: "PUT",
              },
            },
          },
        },
      }),
    });
    uploadTests.applyRemovalPolicy(RemovalPolicy.DESTROY);
  }
}
