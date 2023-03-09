import {
  BundlingOutput,
  CfnOutput,
  DockerImage,
  RemovalPolicy,
  Stack,
  StackProps,
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
import { writeFileSync } from "fs";
import { join } from "path";
import { CLOUDFRONT_DISTRIBUTION_NAME, ELROS_PATH } from "../common";

export interface PamFrontendStackProps extends StackProps {
  cognitoUserPoolId: string;
  cognitoAppClientId: string;
  apiGatewayUrl: string;
}

export class PamFrontendStack extends Stack {
  readonly bucket: Bucket;
  readonly deployment: BucketDeployment;
  readonly distribution: Distribution;

  constructor(scope: Construct, id: string, props: PamFrontendStackProps) {
    super(scope, id, props);

    const env = this.makeEnvFile(props);
    writeFileSync(join(ELROS_PATH, ".env"), env);

    this.bucket = new Bucket(this, "website-bucket", {
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true
    });

    this.distribution = new Distribution(this, "website-distribution", {
      defaultBehavior: {
        origin: new S3Origin(this.bucket),
      },
      defaultRootObject: "index.html",
    });

    const oac = new CfnOriginAccessControl(this, "website-bucket-oac", {
      originAccessControlConfig: {
        name: "website-bucket-oac",
        originAccessControlOriginType: "s3",
        signingBehavior: "always",
        signingProtocol: "sigv4",
      },
    });

    const cfnDistribution = this.distribution.node
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
          }:distribution/${this.distribution.distributionId}`,
        },
      },
      resources: [this.bucket.arnForObjects("*")],
    });

    this.bucket.addToResourcePolicy(bucketPolicyAllowCloudFront);

    new CfnOutput(this, CLOUDFRONT_DISTRIBUTION_NAME, {
      exportName: CLOUDFRONT_DISTRIBUTION_NAME,
      value: this.distribution.domainName,
    });

    this.deployment = new BucketDeployment(this, "PamWebsiteDeployment", {
      destinationBucket: this.bucket,
      sources: [
        Source.asset(ELROS_PATH, {
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

  private makeEnvFile(props: PamFrontendStackProps) {
    return [
      `VITE_COGNITO_USER_POOL_ID=${props.cognitoUserPoolId}`,
      `VITE_COGNITO_USER_POOL_CLIENT_ID=${props.cognitoAppClientId}`,
      `VITE_API_GATEWAY_BASE_URL=${props.apiGatewayUrl}`,
    ].join("\n");
  }
}
