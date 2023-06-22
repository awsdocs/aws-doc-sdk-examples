import { BundlingOutput, DockerImage, RemovalPolicy } from "aws-cdk-lib";
import { Distribution } from "aws-cdk-lib/aws-cloudfront";
import {
  AccountRootPrincipal,
  Effect,
  PolicyStatement,
  ServicePrincipal,
} from "aws-cdk-lib/aws-iam";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { BucketDeployment, Source } from "aws-cdk-lib/aws-s3-deployment";
import { Construct } from "constructs";

export interface AppS3WebsiteProps {
  /**
   * The local path to the assets to be deployed to the S3 bucket.
   */
  assetPath: string;
}

export class AppS3Website extends Construct {
  readonly bucket: Bucket;

  constructor(scope: Construct, id: string, props: AppS3WebsiteProps) {
    super(scope, id);

    this.bucket = new Bucket(this, "website-bucket", {
      enforceSSL: true,
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    new BucketDeployment(this, "website-assets-deployment", {
      destinationBucket: this.bucket,
      sources: [
        Source.asset(props.assetPath, {
          bundling: {
            image: new DockerImage("node:18"),
            command: [
              "/bin/sh",
              "-c",
              "npm i && npm run build && cp -r /asset-input/dist/* /asset-output/",
            ],
            user: "root",
            outputType: BundlingOutput.NOT_ARCHIVED,
          },
        }),
      ],
    });
  }

  grantDistributionRead(distribution: Distribution) {
    const { accountId } = new AccountRootPrincipal();
    const { distributionId } = distribution;
    const distributionArn = `arn:aws:cloudfront::${accountId}:distribution/${distributionId}`;
    const bucketPolicyAllowCloudFront = new PolicyStatement({
      principals: [new ServicePrincipal("cloudfront.amazonaws.com")],
      actions: ["s3:GetObject"],
      effect: Effect.ALLOW,
      conditions: {
        StringEquals: {
          "AWS:SourceArn": distributionArn,
        },
      },
      resources: [
        this.bucket.arnForObjects("*"),
        this.bucket.arnForObjects("assets/*"),
      ],
    });

    this.bucket.addToResourcePolicy(bucketPolicyAllowCloudFront);
  }
}
