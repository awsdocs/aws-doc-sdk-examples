/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { resolve, join } from "node:path";

export const PROJECT_ROOT_PATH = resolve("../../../../");
export const RESOURCES_PATH = join(
  PROJECT_ROOT_PATH,
  "workflows/resilient_service/resources/",
);
export const ROOT = resolve("./");

export const MESSAGES = {
  introduction: `Welcome to the demonstration of How to Build and Manage a Resilient Service!
For this demo, we'll use an AWS SDK to create several AWS resources to set up a load-balanced web service endpoint and explore some ways to make it resilient against various kinds of failures.
Some of the resources create by this demo are:
  - A DynamoDB table that the web service depends on to provide book, movie, and song recommendations.
  - An EC2 launch template that defines EC2 instances that each contain a Python web server.
  - An EC2 Auto Scaling group that manages EC2 instances across several Availability Zones.
  - An Elastic Load Balancing (ELB) load balancer that targets the Auto Scaling group to distribute requests.`,
  confirmContinue: "Continue?",
  confirmDeployment: "Are you ready to deploy resources?",
  creatingTable: "Creating table: ${TABLE_NAME}.",
  createdTable: "Created table: ${TABLE_NAME}.",
  populatingTable: "Populating table: ${TABLE_NAME}.",
  populatedTable: "Populated table: ${TABLE_NAME}.",
  creatingKeyPair:
    "Creating key pair: ${KEY_PAIR_NAME}. This allows you to SSH into the EC2 instances.",
  createdKeyPair:
    "Created key pair: ${KEY_PAIR_NAME}.pem and stored it locally.",
  creatingInstancePolicy:
    "Creating instance policy: ${INSTANCE_POLICY_NAME}. The policy is added to an instance profile.",
  createdInstancePolicy:
    "Created instance policy: ${INSTANCE_POLICY_NAME}. ARN: ${INSTANCE_POLICY_ARN}.",
  creatingInstanceRole: "Creating instance role: ${INSTANCE_ROLE_NAME}.",
  createdInstanceRole: "Instance role created: ${INSTANCE_ROLE_NAME}.",
  attachingPolicyToRole:
    "Attaching policy ${INSTANCE_POLICY_NAME} to role ${INSTANCE_ROLE_NAME}.",
  attachedPolicyToRole:
    "Attached policy ${INSTANCE_POLICY_NAME} to role ${INSTANCE_ROLE_NAME}.",
  creatingInstanceProfile:
    "Creating instance profile: ${INSTANCE_PROFILE_NAME}.",
  createdInstanceProfile:
    "Created instance profile: ${INSTANCE_PROFILE_NAME}. ARN: ${INSTANCE_PROFILE_ARN}.",
  addingRoleToInstanceProfile:
    "Adding role ${INSTANCE_ROLE_NAME} to profile ${INSTANCE_PROFILE_NAME}.",
  addedRoleToInstanceProfile:
    "Added role ${INSTANCE_ROLE_NAME} to profile ${INSTANCE_PROFILE_NAME}.",
  creatingLaunchTemplate: `Creating launch template. The launch template is configured with the instance profile, an instance type, an image id, a key pair, and a startup script. This script starts a Python web server defined in the "server.py" script. The web server listens to HTTP requests on port 80 and responds to requests to '/' and to '/healthcheck'. For demo purposes, this server is run as the root user. In production, the best practice is to run a web server, such as Apache, with least-privileged credentials. The template also defines an IAM policy that each instance uses to assume a role that grants permissions to access the DynamoDB recommendation table and Systems Manager parameters that control the flow of the demo.`,
  createdLaunchTemplate: "Created launch template: ${LAUNCH_TEMPLATE_NAME}.",
  creatingAutoScalingGroup:
    "Creating an EC2 Auto Scaling group, ${AUTO_SCALING_GROUP_NAME}, that maintains three EC2 instances, each in a different Availability Zone.",
  createdAutoScalingGroup:
    "Created EC2 Auto Scaling group ${AUTO_SCALING_GROUP_NAME} with availability zones: ${AVAILABILITY_ZONE_NAMES}. At this point, you have EC2 instances created. After each instance starts, it listens for HTTP requests. You can see these instances in the console or continue with the demo.",
  loadBalancer:
    "Creating an Elastic Load Balancing target group and load balancer. The target group defines how the load balancer connects to instances. The load balancer provides a single endpoint where clients connect and dispatches requests to instances in the group.",
  gettingVpc: "Getting default VPC.",
  gotVpc: "Got default VPC: ${VPC_ID}.",
  gettingSubnets: "Getting subnets for default VPC.",
  gotSubnets: "Subnets for default VPC: ${SUBNETS}",
  creatingLBTargetGroup:
    "Creating Elastic Load Balancer target group ${TARGET_GROUP_NAME}.",
  createdLBTargetGroup:
    "Created Elastic Load Balancer target group ${TARGET_GROUP_NAME}.",
  creatingLoadBalancer: "Creating load balancer ${LB_NAME}.",
  createdLoadBalancer:
    "Created load balancer ${LB_NAME}. DNS name: ${DNS_NAME}.",
  creatingLoadBalancerListener:
    "Creating listener to forward traffic from load balancer ${LB_NAME} to target group ${TARGET_GROUP_NAME}.",
  createdLoadBalancerListener:
    "Created load balancer listener ${LB_LISTENER_ARN}.",
  attachingLoadBalancerTargetGroup:
    "Attaching load balancer target group ${TARGET_GROUP_NAME} to auto scaling group ${AUTO_SCALING_GROUP_NAME}.",
  attachedLoadBalancerTargetGroup:
    "Attached load balancer target group to autoscaling group.",
  verifyingEndpoint:
    "Verifying access to the load balancer endpoint ${DNS_NAME}.",
  verifiedEndpoint: "Endpoint reached:\n${ENDPOINT_RESPONSE}.",
  verifyingInboundPort:
    "Verifying that the default security group of the specified VPC allows ingress from this computer. This can be done by allowing ingress from this computer's IP address. In some situations, such as connecting from a corporate network, you must instead specify a prefix list ID. You can also temporarily open the port to any IP address while running this example. If you do, be sure to remove public access when you're done",
  noSecurityGroups: "No security groups found.",
  foundIpRules:
    "Found the following inbound rules for the security group: ${IP_RULES}.",
  noIpRules:
    "No inbound rules matching your IP address were found. Would you like to add one?",
  addedInboundRule: "Added rule allowing inbound requests from ${IP_ADDRESS}.",
  demoHeader:
    "The demo phase of this example cycles through several stages, setting Systems Manager parameters along the way to simulate failures and instruct the web server to take increasingly resilient actions.",
  demoSanityCheck:
    "First, here's the output from the recommendation service and healthcheck endpoints.",
  demoFindLbError: "Load balancer not found. Did you deploy?",
  demoBrokenDependencyConfirmation:
    "The web service running on the EC2 instances gets recommendations by querying a DynamoDB table. The table name is contained in a Systems Manager parameter named 'doc-example-resilient-architecture-table'. To simulate a failure of the recommendation service, This parameter is replaced with the name of a non-existent table. Do you want to continue?",
  demoTestBrokenDependency:
    "Setting demo parameter doc-example-resilient-architecture-table to ${TABLE_NAME}. Now, sending a GET request to the load balancer endpoint returns a failure code. But, the service reports as healthy to the load balancer because shallow health checks don't check for failure of the recommendation service.",
  demoStaticResponseConfirmation:
    "Instead of failing when the recommendation service fails, the web service can return a static response. While this is not a perfect solution, it presents the customer with a somewhat better experience than failure. Do you want to continue?",
  demoTestStaticResponse:
    "Setting the parameter 'doc-example-resilient-architecture-failure-response' to 'static'. Now, sending a GET request to the load balancer endpoint returns a static response. The service still reports as healthy because health checks are still shallow.",
  demoBadCredentialsConfirmation:
    "The next step is to substitute bad credentials for one of the instances in the target group so that it can't access the DynamoDB recommendation table. The correct DynamoDB table name will be restored. A new instance profile with bad credentials will be created and attached to an instance. Do you want to continue?",
  demoTestBadCredentials:
    "A new IAM role and policy have been created and attached to a new instance profile. The new instance profile replaced the profile for ${INSTANCE_ID}. Now, when the load balancer hits the instance with bad credentials, the instance will return the static response.",
  lbCheck: "Make a GET request to the load balancer?",
  destroy: "Destroy resources?",
  deletedTable: "Deleted table: ${TABLE_NAME}.",
  deleteTableError: "Error deleting table: ${TABLE_NAME}.",
  deletedKeyPair: "Deleted key pair ${KEY_PAIR_NAME}.",
  deleteKeyPairError: "Error deleting key pair: ${KEY_PAIR_NAME}.",
  detachedPolicyFromRole:
    "Detached policy ${INSTANCE_POLICY_NAME} from role ${INSTANCE_ROLE_NAME}.",
  detachPolicyFromRoleError:
    "Error detaching policy ${INSTANCE_POLICY_NAME} from role ${INSTANCE_ROLE_NAME}.",
  deletedPolicy: "Deleted policy ${INSTANCE_POLICY_NAME}.",
  deletePolicyError: "Error deleting policy ${INSTANCE_POLICY_NAME}.",
  deletedInstanceRole: "Deleted role ${INSTANCE_ROLE_NAME}.",
  deleteInstanceRoleError: "Error deleting role ${INSTANCE_ROLE_NAME}.",
  deletedInstanceProfile: "Deleted instance profile ${INSTANCE_PROFILE_NAME}.",
  deleteInstanceProfileError:
    "Error deleting instance profile ${INSTANCE_PROFILE_NAME}.",
  removedRoleFromInstanceProfile:
    "Removed role ${INSTANCE_ROLE_NAME} from instance profile ${INSTANCE_PROFILE_NAME}.",
  removeRoleFromInstanceProfileError:
    "Error removing role ${INSTANCE_ROLE_NAME} from instance profile ${INSTANCE_PROFILE_NAME}.",
  deletedLaunchTemplate: "Deleted launch template ${LAUNCH_TEMPLATE_NAME}.",
  deleteLaunchTemplateError:
    "Error deleting launch template ${LAUNCH_TEMPLATE_NAME}.",
  deletedAutoScalingGroup:
    "Deleted Auto Scaling group ${AUTO_SCALING_GROUP_NAME}.",
  deleteAutoScalingGroupError:
    "Error deleting Auto Scaling group ${AUTO_SCALING_GROUP_NAME}.",
  deletedLB: "Deleted load balancer ${LB_NAME}.",
  deleteLBError: "Error deleting load balancer ${LB_NAME}.",
  deletedLBTargetGroup:
    "Deleted Load Balancer target group ${TARGET_GROUP_NAME}.",
  deleteLBTargetGroupError:
    "Error deleting Load Balancer target group ${TARGET_GROUP_NAME}.",
  detachedSsmOnlyCustomRolePolicy:
    "Detached SSM only role ${ROLE_NAME} from policy ${POLICY_NAME}.",
  detachSsmOnlyCustomRolePolicyError:
    "Error detaching SSM only role ${ROLE_NAME} from policy ${POLICY_NAME}.",
  detachedSsmOnlyAWSRolePolicy:
    "Detached SSM only role ${ROLE_NAME} from policy ${POLICY_NAME}.",
  detachSsmOnlyAWSRolePolicyError:
    "Detached SSM only role ${ROLE_NAME} from policy ${POLICY_NAME}.",
  deletedSsmOnlyInstanceProfile:
    "Deleted SSM only instance profile ${INSTANCE_PROFILE_NAME}.",
  deleteSsmOnlyInstanceProfileError:
    "Error deleting SSM only instance profile ${INSTANCE_PROFILE_NAME}.",
  deletedSsmOnlyPolicy: "Deleted SSM only policy ${POLICY_NAME}.",
  deleteSsmOnlyPolicyError: "Error deleting SSM only policy ${POLICY_NAME}.",
  deletedSsmOnlyRole: "Deleted SSM only role ${ROLE_NAME}.",
  deleteSsmOnlyRoleError: "Error deleting SSM only role ${ROLE_NAME}.",
  detachedSsmOnlyRoleFromProfile:
    "Detached SSM only role ${ROLE_NAME} from profile ${PROFILE_NAME}.",
  detachSsmOnlyRoleFromProfileError:
    "Error detaching SSM only role ${ROLE_NAME} from profile ${PROFILE_NAME}.",
};

export const PREFIX = "resilient-wkflw-";

export const NAMES = {
  autoScalingGroupName: `${PREFIX}auto-scaling-group`,
  tableName: `doc-example-recommendation-service`,
  keyPairName: `${PREFIX}key-pair`,
  instancePolicyName: `${PREFIX}instance-policy`,
  instanceProfileName: `${PREFIX}instance-profile`,
  instanceRoleName: `${PREFIX}instance-role`,
  launchTemplateName: `${PREFIX}launch-template`,
  loadBalancerTargetGroupName: `${PREFIX}target-group`,
  loadBalancerName: `${PREFIX}lb`,
  ssmOnlyPolicyName: `${PREFIX}ssm-only-instance-policy`,
  ssmOnlyRoleName: `${PREFIX}ssm-only-role`,
  ssmOnlyInstanceProfileName: `${PREFIX}ssm-i-profile`,
  ssmTableNameKey: "doc-example-resilient-architecture-table",
  ssmFailureResponseKey: "doc-example-resilient-architecture-failure-response",
  ssmHealthCheckKey: "doc-example-resilient-architecture-health-check",
};
