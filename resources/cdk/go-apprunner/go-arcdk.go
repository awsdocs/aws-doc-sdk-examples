package main

import (
	"github.com/aws/aws-cdk-go/awscdk"
	"github.com/aws/aws-cdk-go/awscdk/awsapprunner"
	"github.com/aws/aws-cdk-go/awscdk/awsecrassets"
	"github.com/aws/aws-cdk-go/awscdk/awsiam"
	"github.com/aws/constructs-go/constructs/v3"
	"github.com/aws/jsii-runtime-go"
)

type GolangAppRunnerExampleProps struct {
	awscdk.StackProps
}

func NewGAStack(scope constructs.Construct, id string, props *GolangAppRunnerExampleProps) awscdk.Stack {
	var sprops awscdk.StackProps
	if props != nil {
		sprops = props.StackProps
	}
	stack := awscdk.NewStack(scope, &id, &sprops)

	// Create the Docker image.
	image := awsecrassets.NewDockerImageAsset(stack, jsii.String("ApplicationImage"), &awsecrassets.DockerImageAssetProps{
		Directory: jsii.String("./app/"),
	})

	appRunnerInstanceRole := awsiam.NewRole(stack, jsii.String("AppRunnerInstanceRole"), &awsiam.RoleProps{
		AssumedBy: awsiam.NewServicePrincipal(jsii.String("tasks.apprunner.amazonaws.com"), &awsiam.ServicePrincipalOpts{}),
	})

	// Grant App Runner read access to the Docker container.
	ecrAccessRole := awsiam.NewRole(stack, jsii.String("AppRunnerRole"), &awsiam.RoleProps{
		AssumedBy: awsiam.NewServicePrincipal(jsii.String("build.apprunner.amazonaws.com"), &awsiam.ServicePrincipalOpts{}),
	})
	image.Repository().GrantPull(ecrAccessRole)

	// Create the service.
	awsapprunner.NewCfnService(stack, jsii.String("AppRunner"), &awsapprunner.CfnServiceProps{
		SourceConfiguration: awsapprunner.CfnService_SourceConfigurationProperty{
			ImageRepository: awsapprunner.CfnService_ImageRepositoryProperty{
				ImageIdentifier: image.ImageUri(),
				ImageConfiguration: awsapprunner.CfnService_ImageConfigurationProperty{
					Port: jsii.String("3000"),
				},
				ImageRepositoryType: jsii.String("ECR"),
			},
			AuthenticationConfiguration: awsapprunner.CfnService_AuthenticationConfigurationProperty{
				AccessRoleArn: ecrAccessRole.RoleArn(),
			},
		},
		InstanceConfiguration: awsapprunner.CfnService_InstanceConfigurationProperty{
			InstanceRoleArn: appRunnerInstanceRole.RoleArn(),
		},
	})

	return stack
}

func main() {
	app := awscdk.NewApp(nil)

	NewGAStack(app, "GolangAppRunnerExampleStack", &GolangAppRunnerExampleProps{
		awscdk.StackProps{
			Env: env(),
		},
	})

	app.Synth(nil)
}

// env determines the AWS environment (account+region) in which our stack is to
// be deployed. For more information see: https://docs.aws.amazon.com/cdk/latest/guide/environments.html
func env() *awscdk.Environment {
	// If unspecified, this stack will be "environment-agnostic".
	// Account/Region-dependent features and context lookups will not work, but a
	// single synthesized template can be deployed anywhere.
	//---------------------------------------------------------------------------
	return nil

	// Uncomment if you know exactly what account and region you want to deploy
	// the stack to. This is the recommendation for production stacks.
	//---------------------------------------------------------------------------
	// return &awscdk.Environment{
	//  Account: jsii.String("123456789012"),
	//  Region:  jsii.String("us-east-1"),
	// }

	// Uncomment to specialize this stack for the AWS Account and Region that are
	// implied by the current CLI configuration. This is recommended for dev
	// stacks.
	//---------------------------------------------------------------------------
	// return &awscdk.Environment{
	//  Account: jsii.String(os.Getenv("CDK_DEFAULT_ACCOUNT")),
	//  Region:  jsii.String(os.Getenv("CDK_DEFAULT_REGION")),
	// }
}
