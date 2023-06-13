import { BundlingOutput, DockerImage } from "aws-cdk-lib";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { BucketDeployment, Source } from "aws-cdk-lib/aws-s3-deployment";
import { Construct } from "constructs";
import { writeFileSync } from "fs";
import { join } from "path";

interface CloudFrontWebsiteAssetsProps {
  assetPath: string;
  bucket: Bucket;
  envVars: {
    apiGatewayUrl: string;
    cloudfrontDistributionUrl: string;
    cognitoUserPoolId: string;
    cognitoAppClientId: string;
    cognitoUserPoolBaseUrl: string;
  };
}

export class CloudFrontWebsiteAssets extends Construct {
  readonly deployment: BucketDeployment;

  constructor(
    scope: Construct,
    id: string,
    readonly props: CloudFrontWebsiteAssetsProps
  ) {
    super(scope, id);
    this.makeEnvFile();
    this.deployment = this.makeBucketDeployment({ bucket: props.bucket });
  }

  private makeBucketDeployment({ bucket }: { bucket: Bucket }) {
    return new BucketDeployment(this, "website-assets-deployment", {
      destinationBucket: bucket,
      sources: [
        Source.asset(this.props.assetPath, {
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

  private makeEnvFile() {
    const envVars = this.props.envVars;
    const body = [
      `VITE_API_GATEWAY_BASE_URL=${envVars.apiGatewayUrl}`,
      `VITE_COGNITO_SIGN_IN_URL=${envVars.cognitoUserPoolBaseUrl}/oauth2/authorize?response_type=token&client_id=${envVars.cognitoAppClientId}&redirect_uri=https://${envVars.cloudfrontDistributionUrl}`,
      `VITE_COGNITO_SIGN_OUT_URL=${envVars.cognitoUserPoolBaseUrl}/logout?client_id=${envVars.cognitoAppClientId}&logout_uri=https://${envVars.cloudfrontDistributionUrl}`,
    ].join("\n");

    writeFileSync(join(this.props.assetPath, ".env"), body);
  }
}
