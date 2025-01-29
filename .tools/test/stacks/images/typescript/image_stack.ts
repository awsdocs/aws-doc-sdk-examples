import "source-map-support/register";
import * as cdk from 'aws-cdk-lib';
import { 
  Stack, 
  StackProps, 
  aws_ecr as ecr, 
  aws_iam as iam,
  RemovalPolicy 
} from "aws-cdk-lib";
import { type Construct } from "constructs";
import { readAccountConfig } from "../../config/targets";

class ImageStack extends Stack {
  constructor(scope: Construct, id: string, props?: StackProps) {
    super(scope, id, props);

    const acctConfig = readAccountConfig("../../config/targets.yaml");

    for (const language of Object.keys(acctConfig)) {
      if (acctConfig[language].status === "enabled") {
        const repository = new ecr.Repository(this, `${language}-examples`, {
          repositoryName: `${language}`,
          imageScanOnPush: true,
          removalPolicy: RemovalPolicy.RETAIN,
        });

        // Add repository policy to allow access from the specified account
        repository.addToResourcePolicy(new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          principals: [
            new iam.AccountPrincipal(acctConfig[language].account_id)
          ],
          actions: [
            "ecr:GetDownloadUrlForLayer",
            "ecr:BatchGetImage",
            "ecr:BatchCheckLayerAvailability",
            "ecr:PutImage",
            "ecr:InitiateLayerUpload",
            "ecr:UploadLayerPart",
            "ecr:CompleteLayerUpload"
          ]
        }));
      }
    }
  }
}

const app = new cdk.App();

new ImageStack(app, "ImageStack", {
  env: {
    account: process.env.CDK_DEFAULT_ACCOUNT!,
    region: process.env.CDK_DEFAULT_REGION!,
  },
  terminationProtection: true
});

app.synth();
