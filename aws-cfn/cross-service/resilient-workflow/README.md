# Build and manage a resilient service using CloudFormation

## Overview

This example shows how to use AWS CloudFormation to create a load-balanced
web service that returns book, movie, and song recommendations. It shows
how the service responds to failures, and how to restructure the service for
more resilience when failures occur.

Several components are used to demonstrate the resilience of the example web service:

- [Amazon EC2 Auto Scaling](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
  is used to create
  [Amazon Elastic Compute Cloud (Amazon EC2)](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
  instances based on a launch template and to keep the number of instances
  in a specified range.
- [Elastic Load Balancing](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html)
  handles HTTP requests, monitors the health of instances in the Auto Scaling group, and
  dispatches requests to healthy instances.
- A Python web server runs on each EC2 instance to handle HTTP requests. It responds
  with recommendations and health checks.
- An [Amazon DynamoDB](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Introduction.html)
  table simulates a recommendation service that the web server depends on to get recommendations.
- A set of [AWS Systems Manager](https://docs.aws.amazon.com/systems-manager/latest/userguide/what-is-systems-manager.html)
  parameters control web server response to requests and health checks to
  simulate failures and demonstrate resiliency.

Each of these components is created and managed with the SDK for Python as part of
an interactive demo that runs at a command prompt.

### Amazon EC2 Auto Scaling and EC2 instances

An Auto Scaling group starts EC2 instances in a specified set of Availability Zones.
This example uses an Auto Scaling group to keep the number of running instances
within a specified range and to make them available across multiple Availability Zones. The Auto Scaling group
is set as a load balancer target so that HTTP requests are handled by a single endpoint
and dispatched equally to the instances in the group.

An [AWS Identity and Access Management (IAM)](https://docs.aws.amazon.com/IAM/latest/UserGuide/introduction.html)
instance profile specifies permissions that are granted to EC2 instances created
during the demo. When you associate an instance profile with an instance, AWS SDK code
that runs on the instance can assume the profile's role to get the permissions that are specified
by the role's attached policies.

An Amazon EC2 launch template specifies how instances are created. This example creates
a launch template that specifies the instance type, Amazon Machine Image (AMI), instance
profile, and a Bash script that runs when the instance is started. The Bash
script installs required Python packages and starts a demo Python web server that listens
for HTTP requests on port 80. The Python web server uses the SDK for Python to get
recommendation data from a DynamoDB table and to get parameter values from Systems
Manager to control the flow of the demonstration.

### Elastic Load Balancing

Elastic Load Balancing is used to distribute incoming HTTP traffic across multiple instances.
This example creates an
[Application Load Balancer](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html).
It also adds a listener that forwards requests
from the load balancer endpoint to the EC2 instances that are managed by the Auto Scaling
group. The target group performs health checks on the instances and pulls unhealthy
instances out of the rotation.

## ⚠ Important

- Running this code might result in charges to your AWS account.
- Running the tests might result in charges to your AWS account.
- We recommend that you grant your code least privilege. At most, grant only the minimum permissions required to perform the task. For more information, see [Grant least privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege).
- This code is not tested in every AWS Region. For more information, see [AWS Regional Services](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).

## Scenario

### Prerequisites

All the components used in this example need to access one another. Therefore, you must
create all components in the same virtual private cloud (VPC). A VPC is a logically isolated network provided by
[Amazon Virtual Private Cloud (Amazon VPC)](https://docs.aws.amazon.com/vpc/latest/userguide/what-is-amazon-vpc.html).
You can use the default VPC that's included with your account, or
[create a new VPC](https://docs.aws.amazon.com/vpc/latest/userguide/create-vpc.html).

To access the load balancer endpoint, you must allow inbound traffic
on port 80 from your computer's IP address to your VPC. If this rule doesn't exist, the
example tries to add it. Alternately, you can
[add a rule to the default security group for your VPC](https://docs.aws.amazon.com/vpc/latest/userguide/security-group-rules.html)
and specify your computer's IP address as a source.

This example is deployed as a CloudFormation stack. Achieve this by
using the [CloudFormation console](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-using-console.html)
or the [AWS Command Line Interface](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-using-cli.html) (AWS CLI).
This guide presents step-by-step instructions for the AWS CLI.

> NOTE: The instructions in this guide use single-quote `'` for parameters, which is consistent with a Unix or GNU/Linux shell like Bash or ZSH. You may need to change the quote character for other platforms. Windows CMD users should use `"`, for instance. Also, CMD uses `^` as a line continuation character, instead of `\` used in this guide.

<!-- Editor's note: This is because JMESPath uses the ` character for literal values, which in a Unix environment using " tries to execute the literal as a command. -->

### Instructions

#### Build and manage a resilient service

##### Deploy resources

Use AWS CloudFormation to create the following AWS resources:

1. A DynamoDB table that acts as a service that recommends books, movies, and songs.
2. An instance profile and an associated role and policy that grants permission to
   instances to access DynamoDB and Systems Manager.
3. A launch template that specifies the instance profile and a startup script
   that starts a Python web server on each instance.
4. An Auto Scaling group that starts EC2 instances, one in each of three
   Availability Zones.
5. An Application Load Balancer that handles HTTP requests to a single endpoint.
6. A target group that connects the load balancer to instances in the Auto Scaling group.
7. A listener that is added to the load balancer and forwards requests to the target group.

To deploy these resources, you will need to provide information about your AWS account.

> NOTE: This example assumes your account has a [default VPC](https://docs.aws.amazon.com/vpc/latest/userguide/default-vpc.html) in the current Region.

| Parameter | AWS CLI                                                                                                      |
| --------- | ------------------------------------------------------------------------------------------------------------ |
| `VPCId`   | `aws ec2 describe-vpcs --filters Name=is-default,Values=true --query 'Vpcs[0].VpcId'`                        |
| `SGId`    | `aws ec2 describe-security-groups --group-names default --query 'SecurityGroups[0].GroupId'`                 |
| `Subnets` | `aws ec2 describe-subnets --filters Name=vpc-id,Values='vpc-00000000000000000' --query 'Subnets[].SubnetId'` |

After running these commands, replace the placeholders in `params.json` with the returned values. Remember to replace the VPC ID in the `describe-subnets` command with the ID returned from the first `describe-vpcs` command.

```
aws cloudformation create-stack \
  --stack-name resilience-demo \
  --template-body file://./resilient-service.yaml \
  --parameters file://./params.json \
  --capabilities CAPABILITY_NAMED_IAM
```

After creating the stack, you can find the URL for the load balancer in the stack's parameters.

```
aws cloudformation describe-stacks --stack-name resilience-demo --query 'Stacks[0].Outputs[?OutputKey==`LB`].OutputValue | [0]'

# "doc-example-resilience-lb-0123456789.us-east-1.elb.amazonaws.com"
```

You can then access the URL in your browser or by using `curl`.

> NOTE: This command may return `null` immediately after running the create-stack command. You must wait for the stack to finish being created. This is easiest to watch from the CloudFormation page in the console.

At any time after the stack has been created, you can check the health of the target group.

```
aws cloudformation describe-stacks --stack-name resilience-demo --query 'Stacks[0].Outputs[?OutputKey==`TGArn`].OutputValue | [0]'
# "arn:aws:elasticloadbalancing:us-east-1:000000000000:targetgroup/doc-example-resilience-tg/dc038043cedce18c"
aws elbv2 describe-target-health --target-group-arn "arn:aws:elasticloadbalancing:us-east-1:000000000000:targetgroup/doc-example-resilience-tg/0123456789abcdef"
```

All stack outputs:

| OutputKey | OutputValue                                | Usage                                             |
| --------- | ------------------------------------------ | ------------------------------------------------- |
| `LB`      | The DNS Name of the primary load balancer  | `curl` or Browser                                 |
| `Key`     | The ID of a .pem format private key in SSM | `ssh` after downloading from SSM                  |
| `TGArn`   | The ARN of the target group of instances   | Check various additional information from the CLI |

#### Demonstrate resiliency

This part of the example demonstrates resiliency by simulating several kinds of failures.
It uses Systems Manager parameters to update how the web server responds to requests. It
also uses health checks to show how to make your web server more resilient to failure.

Along with recommendations returned by the DynamoDB table, the web service includes the
instance ID and Availability Zone so you can see how the load balancer distributes
requests among the instances in the Auto Scaling group.

The scenario takes the following steps. After editing the `params.json` file for each step, update the stack
to see the changes by running this `update-stack` command.

```
aws cloudformation update-stack \
  --stack-name resilience-demo \
  --template-body file://./resilient-service.yaml \
  --parameters file://./params.json \
  --capabilities CAPABILITY_NAMED_IAM
```

1. **Initial state: healthy** — Sends requests to the endpoint to get recommendations and verify that instances
   are healthy.

2. **Broken dependency** — Sets a parameter that specifies a nonexistent DynamoDB table name. This simulates a
   failure of the recommendation service. Requests for recommendations now return a failure
   code. All instances still report as healthy because they only implement shallow health checks. For this
   example, a shallow health check means the web server always reports itself as healthy as long as the
   load balancer can connect to it.

   Edit `params.json`. Add a new entry with `ParameterKey` as `SSMTableName` and `ParameterValue` as `unknown`.
   After updating, the service should report healthy but return `502` responses.

3. **Static response** — Updates a parameter that prompts the web server to return a static response when the
   recommendation service fails. Requests for recommendations now return a static response,
   which is a better customer experience.

   Edit `params.json`. Add a new entry with `ParameterKey` as `SSMFailure` and set `ParameterValue` to `static`.

4. **Bad credentials** — Sets the table name parameter so the recommendations service succeeds, but also
   updates one of the instances to use an instance profile without appropriate credentials to access the DynamoDB table.
   Now, when the load balancer selects the bad instance to serve a request, it returns
   a static response because it cannot access the recommendation service, but the other
   instances return real recommendations.

   Edit `params.json` and remove the `SSMTableName` entry from step 2. Update the stack. Remove the IAM instance profile from one instance in the target group. This instance won't be able to access the DynamoDB table anymore, and should now remain healthy but only return the static response.

   To remove an IAM instance profile by using the AWS CLI, follow these steps.

   1. Find the ARN of the target group with `` aws cloudformation describe-stacks --stack-name resilience-demo --query 'Stacks[0].Outputs[?OutputKey==`TGArn`].OutputValue | [0]' ``.
   2. With this target group ARN, query for the specific instances using `aws elbv2 describe-target-health --query 'TargetHealthDescriptions[*].Target.Id' --target-group-arn arn:aws:elasticloadbalancing:us-east-1:000000000000:targetgroup/doc-example-resilience-tg/exampleexample`.
   3. Choose one ID from this list, and find the instance profile association id with `aws ec2 describe-iam-instance-profile-associations --query 'IamInstanceProfileAssociations[0].AssociationId' --filters Name=instance-id,Values=i-0123456789`.
   4. Remove the association with `aws ec2 disassociate-iam-instance-profile --association-id iip-assoc-00000000000000000`.
   5. Restart the instance with `aws ec2 reboot-instances --instance-ids i-0123456789`.

   > Note: The preceding steps are for the purpose of this example only. In most cases, you shouldn't use them in production environments.
   > Always use CloudFormation templates and Infrastructure as Code (IaC) to modify resources in production environments.

5. **Deep health checks** — Sets a parameter that instructs the web server to perform a deep health check.
   For this example, a deep health check means that the web server reports itself as unhealthy if it can't
   access the recommendations service. The instance with bad credentials reports as unhealthy and the load
   balancer takes it out of rotation. Now, requests are forwarded only to healthy instances.

   Edit `params.json`. Change the entry with `ParameterKey` as `SSMHealthCheck` and set `ParameterValue` to `deep`.

6. **Replace the failing instance** — Terminates the unhealthy instance and lets Amazon EC2 Auto Scaling start
   a new instance in its place. During this process, the stopping and starting instances are unhealthy so they
   don't receive any requests, but the load balancer continues to forward requests to healthy
   instances. When the new instance is ready, it is added to the rotation and starts receiving
   requests.

   Using the AWS Management Console, open the CloudFormation page. Navigate to the `resilience-demo` stack. Choose the `Resources`
   tab. Find the `DocExampleRecommendationServiceTargetGroup` line. Choose the `Physical Resource ID` link.
   From this EC2 page, find the list of instances in the target group. Select one and navigate to it. Choose `Actions`, `Terminate instance`.
   See EC2 terminate the instance, and watch the Auto Scaling group start a new instance.

7. **Fail open** — Sets the table name parameter so the recommendations service fails for all instances.
   Because all instances are using deep health checks, they all report as unhealthy. In this
   case, the load balancer continues to forward requests to all instances. This lets the
   system fail open and lets the instances return static responses, rather than fail closed
   and report failure.

   Edit `params.json`. Add a new entry with `ParameterKey` as `SSMTableName` and `ParameterValue` as `unknown`.
   After updating, the service should report unhealthy but return static responses.

8. **Rolling Update** If necessary, you can trigger rolling updates to all instances by changing the Launch Template.
   To change a non-functional aspect of the Launch Template, which will trigger a rolling update without needing to modify any functional configuration, change the `LaunchTemplateVersion` parameter.
   This has a default value of `1.0.0`, but can be any string. Any change to this string will trigger an `AutoScalingRollingUpdate` in the `DocExampleRecommendationServiceAutoScalingGroup`.

##### Destroy resources

Use AWS CloudFormation to clean up all resources created for this example.

1. Delete the load balancer and target group.
2. Stop all instances and delete the Auto Scaling group.
3. Delete the launch template and instance profile.
4. Delete the DynamoDB recommendations table.

```
aws cloudformation delete-stack \
  --stack-name resilience-demo
```

## Additional resources

- [AWS CLI CloudFormation Command Reference](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/cloudformation/index.html)
- [User Guide for Application Load Balancers](https://docs.aws.amazon.com/elasticloadbalancing/latest/application/introduction.html)
- [Amazon EC2 Auto Scaling User Guide](https://docs.aws.amazon.com/autoscaling/ec2/userguide/what-is-amazon-ec2-auto-scaling.html)
- [Amazon Elastic Compute Cloud User Guide for Linux Instances](https://docs.aws.amazon.com/AWSEC2/latest/UserGuide/concepts.html)
- [Elastic Load Balancing V2 reference in the AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/AWS_ElasticLoadBalancingV2.html)
- [Amazon EC2 Auto Scaling reference in the AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/AWS_AutoScaling.html)
- [Amazon EC2 reference in the AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/AWS_EC2.html)

---

Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
