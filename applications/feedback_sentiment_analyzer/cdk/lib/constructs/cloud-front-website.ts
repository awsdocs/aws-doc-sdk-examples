import { readFileSync } from "node:fs";
import { join } from "node:path";
import {
  BundlingOutput,
  CfnOutput,
  DockerImage,
  Fn,
  RemovalPolicy,
} from "aws-cdk-lib";
import {
  CfnDistribution,
  CfnOriginAccessControl,
  Distribution,
  ResponseHeadersPolicy,
} from "aws-cdk-lib/aws-cloudfront";
import { HttpOrigin, S3Origin } from "aws-cdk-lib/aws-cloudfront-origins";
import {
  AccountRootPrincipal,
  Effect,
  PolicyStatement,
  Role,
  ServicePrincipal,
} from "aws-cdk-lib/aws-iam";
import {
  Code,
  Function,
  FunctionUrl,
  FunctionUrlAuthType,
  Runtime,
} from "aws-cdk-lib/aws-lambda";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { BucketDeployment, Source } from "aws-cdk-lib/aws-s3-deployment";
import { Construct } from "constructs";

export interface CloudFrontWebsiteProps {
  assetPath: string;
  apiGatewayBaseUrl: string;
  cognitoUserPoolBaseUrl: string;
  cognitoAppClientId: string;
}

export class CloudFrontWebsite extends Construct {
  readonly bucket: Bucket;
  readonly distribution: Distribution;

  constructor(scope: Construct, id: string, props: CloudFrontWebsiteProps) {
    super(scope, id);

    this.bucket = new Bucket(this, "website-bucket", {
      removalPolicy: RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    const envLambda = new Function(this, "env-lambda", {
      runtime: Runtime.NODEJS_18_X,
      environment: {
        API_GATEWAY_BASE_URL: props.apiGatewayBaseUrl,
        COGNITO_USER_POOL_BASE_URL: props.cognitoUserPoolBaseUrl,
        COGNITO_APP_CLIENT_ID: props.cognitoAppClientId,
      },
      code: Code.fromInline(
        readFileSync(join(__dirname, "website-env-fn.js")).toString()
      ),
      handler: "index.handler",
    });

    const envLambdaUrl = new FunctionUrl(this, "env-lambda-url", {
      function: envLambda,
      authType: FunctionUrlAuthType.NONE,
    });

    /**
     * BEGIN: Possible construct for S3 distribution.
     */
    this.distribution = new Distribution(this, "website-distribution", {
      defaultBehavior: {
        origin: new S3Origin(this.bucket),
      },
      defaultRootObject: "index.html",
    });

    const oac = new CfnOriginAccessControl(this, "website-bucket-oac", {
      originAccessControlConfig: {
        name: `${id}-website-bucket-oac`,
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

    const responseHeadersPolicy = new ResponseHeadersPolicy(
      this,
      "env-headers-policy",
      {
        customHeadersBehavior: {
          customHeaders: [
            {
              header: "Content-Type",
              value: "application/javascript",
              override: true,
            },
          ],
        },
      }
    );

    const envLambdaOrigin = new HttpOrigin(
      Fn.select(2, Fn.split("/", envLambdaUrl.url))
    );

    this.distribution.addBehavior(
      "env.js",
      // https://github.com/aws/aws-cdk/issues/20254#issuecomment-1292253502
      envLambdaOrigin,
      {
        responseHeadersPolicy: {
          responseHeadersPolicyId:
            responseHeadersPolicy.responseHeadersPolicyId,
        },
      }
    );

    /**
     * END: Possible construct.
     */

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

    new CfnOutput(this, "output", {
      exportName: `${id}-domain`,
      value: this.distribution.domainName,
    });
  }
}
