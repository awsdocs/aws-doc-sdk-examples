# Golang: AWS App Runner with ECR via CDK

This is an example of building Golang applications on top of AWS AppRunner.

AppRunner does not natively support Go, however it is possible to automate the deployment of a Go application using the
Elastic Container Registry. Here, a simple application written in Go, is packaged into a Docker container.

* The docker container is built in two passes: The first builds the Golang application for the target linux-amd64 environment.
  The second stage assembles the final container used by the application: A barebones Go application and supporting environment.
* This application is built using [fiber](://gofiber.io/) however any HTTP framework will do. 
* App Runner automatically procures HTTPS certificates for both the default certificate and any custom domains you may associate with your service.


## ⚠️ Important

* While this sample has been tested in multiple AWS Regions, you will need to verify that AppRunner and the Elastic Container Registry are available in the Regions you wish to deploy to.
* Running the sample may incur charges upon your account. Unless you clean up the AppRunner instance, it will continue to incur charges. To avoid future charges, use `cdk destroy` to clean up any resources that were created as a part of this example.

## Running the example

To run this example, make sure you have the AWS Clould Development Kit (AWS CDK) installed.

If you have not already done so, you will need to bootstrap the AWS CDK:
```
cdk bootstrap
```

Next, use `cdk deploy` to deploy the application:
```
$ cdk deploy

This deployment will make potentially sensitive changes according to your current security approval level (--require-approval broadening).
Please confirm you intend to make the following modifications:

IAM Statement Changes
┌───┬────────────────────────────────────────┬────────┬────────────────────────────────────────┬──────────────────────────────────────────┬───────────┐
│   │ Resource                               │ Effect │ Action                                 │ Principal                                │ Condition │
├───┼────────────────────────────────────────┼────────┼────────────────────────────────────────┼──────────────────────────────────────────┼───────────┤
│ + │ ${AppRunnerInstanceRole.Arn}           │ Allow  │ sts:AssumeRole                         │ Service:tasks.apprunner.amazonaws.com    │           │
├───┼────────────────────────────────────────┼────────┼────────────────────────────────────────┼──────────────────────────────────────────┼───────────┤
│ + │ ${AppRunnerRole.Arn}                   │ Allow  │ sts:AssumeRole                         │ Service:build.apprunner.amazonaws.com    │           │
├───┼────────────────────────────────────────┼────────┼────────────────────────────────────────┼──────────────────────────────────────────┼───────────┤
│ + │ *                                      │ Allow  │ ecr:GetAuthorizationToken              │ AWS:${AppRunnerRole}                     │           │
├───┼────────────────────────────────────────┼────────┼────────────────────────────────────────┼──────────────────────────────────────────┼───────────┤
│ + │ arn:${AWS::Partition}:ecr:${AWS::Regio │ Allow  │ ecr:BatchCheckLayerAvailability        │ AWS:${AppRunnerRole}                     │           │
│   │ n}:${AWS::AccountId}:repository/aws-cd │        │ ecr:BatchGetImage                      │                                          │           │
│   │ k/assets                               │        │ ecr:GetDownloadUrlForLayer             │                                          │           │
└───┴────────────────────────────────────────┴────────┴────────────────────────────────────────┴──────────────────────────────────────────┴───────────┘
(NOTE: There may be security-related changes not in this list. See https://github.com/aws/aws-cdk/issues/1299)

Do you wish to deploy these changes (y/n)? y

GolangAppRunnerExampleStack: deploying...
[0%] start: Publishing 0f380582847cffcb6a5ce66113c5f929c8a2ab63ef3e3b0b44efc0e506e12a9c:current
#1 [internal] load build definition from Dockerfile

...

GolangAppRunnerExampleStack: creating CloudFormation changeset...
[█████████▋················································] (1/6)

11:51:29 AM | CREATE_IN_PROGRESS   | AWS::CloudFormation::Stack | GolangAppRunnerExampleStack
11:51:56 AM | CREATE_IN_PROGRESS   | AWS::IAM::Role          | AppRunnerInstanceRole
11:51:56 AM | CREATE_IN_PROGRESS   | AWS::IAM::Role          | AppRunnerRole

```

This will take 5-10 minutes.

When you're done, use `aws apprunner list-services` to show the service you've created:

```
$ aws apprunner list-services
ServiceSummaryList:
- CreatedAt: '2021-07-12T18:17:03-07:00'
  ServiceArn: arn:aws:apprunner:us-west-2:111222333444:service/AppRunner-exampleAbcd1/cb779af9f12f420bbe7b2649caff2c66
  ServiceId: cb779af9f12f420bbe7b2649caff2c66
  ServiceName: AppRunner-exampleAbcd1
  ServiceUrl: example123.us-west-2.awsapprunner.com
  Status: RUNNING
  UpdatedAt: '2021-07-12T18:17:03-07:00'
```

the application will be running happily:

```
$ curl https://example123.us-west-2.awsapprunner.com
Hello, World!
```

## Useful commands

 * `cdk deploy`      deploy this stack to your default AWS account/region
 * `cdk diff`        compare deployed stack with current state
 * `cdk synth`       emits the synthesized CloudFormation template
