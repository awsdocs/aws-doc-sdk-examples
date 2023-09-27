import { BundlingOutput, DockerImage, Stack, StackProps } from "aws-cdk-lib";
import { Bucket } from "aws-cdk-lib/aws-s3";
import { BucketDeployment, Source } from "aws-cdk-lib/aws-s3-deployment";
import { Construct } from "constructs";
import { writeFileSync } from "fs";
import { join } from "path";

import { ELROS_PATH } from "../common";

interface PamFrontEndAssetStackProps extends StackProps {
  apiGatewayUrl: string;
  bucket: Bucket;
  cloudfrontDistributionUrl: string;
  cognitoUserPoolId: string;
  cognitoAppClientId: string;
  cognitoUserPoolBaseUrl: string;
}

export class PamFrontEndAssetStack extends Stack {
  readonly deployment: BucketDeployment;

  constructor(scope: Construct, id: string, props: PamFrontEndAssetStackProps) {
    super(scope, id, props);
    this.makeEnvFile(props);
    this.deployment = this.makeBucketDeployment({ bucket: props.bucket });
  }

  private makeBucketDeployment({ bucket }: { bucket: Bucket }) {
    return new BucketDeployment(this, "PamWebsiteDeployment", {
      destinationBucket: bucket,
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

  private makeEnvFile(envVars: Omit<PamFrontEndAssetStackProps, "bucket">) {
    const body = [
      `VITE_COGNITO_USER_POOL_ID=${envVars.cognitoUserPoolId}`,
      `VITE_COGNITO_USER_POOL_CLIENT_ID=${envVars.cognitoAppClientId}`,
      `VITE_API_GATEWAY_BASE_URL=${envVars.apiGatewayUrl}`,
      `VITE_COGNITO_SIGN_IN_URL=${envVars.cognitoUserPoolBaseUrl}/oauth2/authorize?response_type=token&client_id=${envVars.cognitoAppClientId}&redirect_uri=https://${envVars.cloudfrontDistributionUrl}`,
      `VITE_COGNITO_SIGN_OUT_URL=${envVars.cognitoUserPoolBaseUrl}/logout?client_id=${envVars.cognitoAppClientId}&logout_uri=https://${envVars.cloudfrontDistributionUrl}`,
    ].join("\n");

    writeFileSync(join(ELROS_PATH, ".env"), body);
  }
}
