import * as cdk from "aws-cdk-lib";
import { Construct } from "constructs";
import * as batch from "aws-cdk-lib/aws-batch";
import * as ec2 from "aws-cdk-lib/aws-ec2";
import * as iam from "aws-cdk-lib/aws-iam";

export class BatchFargateStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const vpc = new ec2.Vpc(this, "BatchEnvironmentVPC", {
      cidr: "10.0.0.0/16",
    });

    const selection = vpc.selectSubnets({
      subnetType: ec2.SubnetType.PRIVATE_WITH_EGRESS,
    });

    const internalSecurityGroup = new ec2.SecurityGroup(
      this,
      "BatchFargateInternalSecurityGroup",
      {
        vpc,
        allowAllOutbound: true,
      }
    );
    // internalSecurityGroup.addEgressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80));
    // internalSecurityGroup.addEgressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(80));
    // internalSecurityGroup.addEgressRule(ec2.Peer.anyIpv4(), ec2.Port.tcp(443));
    // internalSecurityGroup.addEgressRule(ec2.Peer.anyIpv6(), ec2.Port.tcp(443));

    const executionRole = new iam.Role(this, "BatchFargateTaskExecutionRole", {
      assumedBy: new iam.ServicePrincipal("ecs-tasks.amazonaws.com"),
      description: "Role for ECS Task executor",
      managedPolicies: [
        {
          managedPolicyArn:
            "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
        },
      ],
    });

    const computeEnvironment = new batch.CfnComputeEnvironment(
      this,
      "BatchFargateComputeEnvironment",
      {
        type: "MANAGED",
        computeResources: {
          type: "FARGATE",
          subnets: selection.subnets.map((subnet) => subnet.subnetId),
          creationStack: [],
          maxvCpus: 256,
          securityGroupIds: [internalSecurityGroup.uniqueId],
        },
      }
    );

    new batch.CfnJobQueue(this, "BatchFargateJobQueue", {
      computeEnvironmentOrder: [
        {
          computeEnvironment: computeEnvironment.ref,
          order: 0,
        },
      ],
      priority: 0,
    });

    new batch.CfnJobDefinition(this, "BatchFargateHelloJob", {
      type: "container",
      platformCapabilities: ["FARGATE"],
      containerProperties: {
        fargatePlatformConfiguration: { platformVersion: "1.4.0" },
        executionRoleArn: executionRole.roleArn,
        image: "public.ecr.aws/amazonlinux/amazonlinux:latest",
        command: ["echo", "hello world"],
        resourceRequirements: [
          {
            type: "VCPU",
            value: "0.25",
          },
          {
            type: "MEMORY",
            value: "512",
          },
        ],
      },
    });
  }
}
