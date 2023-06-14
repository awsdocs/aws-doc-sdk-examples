import {
  BundlingOutput,
  DockerImage,
  RemovalPolicy,
} from "aws-cdk-lib";
import {
  CfnDistribution,
  CfnOriginAccessControl,
  Distribution,
} from "aws-cdk-lib/aws-cloudfront";
import { S3Origin } from "aws-cdk-lib/aws-cloudfront-origins";
import {
  AccountRootPrincipal,
  Effect,
  PolicyStatement,
  ServicePrincipal,
} from "aws-cdk-lib/aws-iam";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { BucketDeployment, Source } from "aws-cdk-lib/aws-s3-deployment";
import { Construct } from "constructs";

export interface CloudFrontWebsiteProps {
  assetPath: string;
  distribution: Distribution;
  cognitoUserPoolBaseUrl: string;
  cognitoAppClientId: string;
}

export class CloudFrontWebsite extends Construct {
  readonly bucket: Bucket;

  constructor(scope: Construct, id: string, props: CloudFrontWebsiteProps) {
    super(scope, id);

    this.bucket = new Bucket(this, "website-bucket", {
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    const s3Origin = new S3Origin(this.bucket);

    props.distribution.addBehavior("home", s3Origin);

    const oac = new CfnOriginAccessControl(this, "website-bucket-oac", {
      originAccessControlConfig: {
        name: `${id}-website-bucket-oac`,
        originAccessControlOriginType: "s3",
        signingBehavior: "always",
        signingProtocol: "sigv4",
      },
    });

    const cfnDistribution = props.distribution.node
      .defaultChild as CfnDistribution;

    cfnDistribution.addPropertyOverride(
      "DistributionConfig.Origins.0.S3OriginConfig.OriginAccessIdentity",
      ""
    );
    cfnDistribution.addPropertyOverride(
      "DistributionConfig.Origins.0.OriginAccessControlId",
      oac.getAtt("Id")
    );

    const bucketPolicyAllowCloudFront = new PolicyStatement({
      principals: [new ServicePrincipal("cloudfront.amazonaws.com")],
      actions: ["s3:GetObject"],
      effect: Effect.ALLOW,
      conditions: {
        StringEquals: {
          "AWS:SourceArn": `arn:aws:cloudfront::${
            new AccountRootPrincipal().accountId
          }:distribution/${props.distribution.distributionId}`,
        },
      },
      resources: [this.bucket.arnForObjects("*")],
    });

    this.bucket.addToResourcePolicy(bucketPolicyAllowCloudFront);

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
}
