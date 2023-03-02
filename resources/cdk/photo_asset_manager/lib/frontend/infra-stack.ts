import { Arn, CfnOutput, RemovalPolicy, Stack } from "aws-cdk-lib";
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
import { Construct } from "constructs";
import {
  CLOUDFRONT_DISTRIBUTION_NAME,
  PAM_FE_INFRA_STACK_NAME,
} from "../common";

export class PamFrontEndInfraStack extends Stack {
  readonly bucket: Bucket;
  readonly distribution: Distribution;

  constructor(scope: Construct, id: string) {
    super(scope, id);

    this.bucket = new Bucket(this, "website-bucket", {
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    this.distribution = new Distribution(this, "website-distribution", {
      defaultBehavior: {
        origin: new S3Origin(this.bucket),
      },
      defaultRootObject: "index.html",
    });

    const oac = new CfnOriginAccessControl(this, "website-bucket-oac", {
      originAccessControlConfig: {
        name: `${PAM_FE_INFRA_STACK_NAME}-website-bucket-oac`,
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
  }
}
