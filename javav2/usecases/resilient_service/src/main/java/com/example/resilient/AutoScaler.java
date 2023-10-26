/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 * This package contains classes related to XYZ functionality.
 * It provides implementations for ABC and XYZ operations.
 */

package com.example.resilient;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.autoscaling.model.AttachLoadBalancerTargetGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingException;
import software.amazon.awssdk.services.autoscaling.model.AutoScalingGroup;
import software.amazon.awssdk.services.autoscaling.model.CreateAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DeleteAutoScalingGroupRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import software.amazon.awssdk.services.autoscaling.model.DescribeAutoScalingGroupsResponse;
import software.amazon.awssdk.services.autoscaling.model.TerminateInstanceInAutoScalingGroupRequest;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesResponse;
import software.amazon.awssdk.services.ec2.model.DescribeIamInstanceProfileAssociationsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeIamInstanceProfileAssociationsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsRequest;
import software.amazon.awssdk.services.ec2.model.DescribeSubnetsResponse;
import software.amazon.awssdk.services.ec2.model.DescribeVpcsResponse;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.IpRange;
import software.amazon.awssdk.services.ec2.model.RebootInstancesRequest;
import software.amazon.awssdk.services.ec2.model.ReplaceIamInstanceProfileAssociationRequest;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.iam.model.AttachedPolicy;
import software.amazon.awssdk.services.iam.model.DeleteInstanceProfileRequest;
import software.amazon.awssdk.services.iam.model.DeletePolicyRequest;
import software.amazon.awssdk.services.iam.model.DeleteRoleRequest;
import software.amazon.awssdk.services.iam.model.DetachRolePolicyRequest;
import software.amazon.awssdk.services.iam.model.GetInstanceProfileResponse;
import software.amazon.awssdk.services.iam.model.IamException;
import software.amazon.awssdk.services.iam.model.ListAttachedRolePoliciesResponse;
import software.amazon.awssdk.services.iam.model.ListEntitiesForPolicyResponse;
import software.amazon.awssdk.services.iam.model.ListInstanceProfilesForRoleRequest;
import software.amazon.awssdk.services.iam.model.ListInstanceProfilesForRoleResponse;
import software.amazon.awssdk.services.iam.model.ListPoliciesRequest;
import software.amazon.awssdk.services.autoscaling.AutoScalingClient;
import software.amazon.awssdk.services.autoscaling.model.LaunchTemplateSpecification;
import software.amazon.awssdk.services.iam.model.ListPoliciesResponse;
import software.amazon.awssdk.services.iam.model.Policy;
import software.amazon.awssdk.services.iam.model.RemoveRoleFromInstanceProfileRequest;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.DescribeInstanceInformationResponse;
import software.amazon.awssdk.services.ssm.model.InstanceInformation;
import software.amazon.awssdk.services.ssm.model.SendCommandRequest;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// snippet-start:[javav2.example_code.workflow.ResilientService_AutoScaler]
public class AutoScaler {

    private static Ec2Client ec2Client;
    private static AutoScalingClient autoScalingClient;
    private static IamClient iamClient;

    private static SsmClient ssmClient;

    private IamClient getIAMClient() {
        if (iamClient == null) {
            iamClient = IamClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return iamClient;
    }

    private SsmClient getSSMClient() {
        if (ssmClient == null) {
            ssmClient = SsmClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return ssmClient;
    }

    private Ec2Client getEc2Client() {
        if (ec2Client == null) {
            ec2Client = Ec2Client.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return ec2Client;
    }

    private AutoScalingClient getAutoScalingClient() {
        if (autoScalingClient == null) {
            autoScalingClient = AutoScalingClient.builder()
                .region(Region.US_EAST_1)
                .build();
        }
        return autoScalingClient;
    }

    // snippet-start:[javav2.cross_service.resilient_service.auto-scaling.DeleteAutoScalingGroup]
    /**
     *  Terminates and instances in an EC2 Auto Scaling group. After an instance is
     *  terminated, it can no longer be accessed.
     */
    public void terminateInstance(String instanceId) {
        TerminateInstanceInAutoScalingGroupRequest terminateInstanceIRequest = TerminateInstanceInAutoScalingGroupRequest.builder()
            .instanceId(instanceId)
            .shouldDecrementDesiredCapacity(false)
            .build();

        getAutoScalingClient().terminateInstanceInAutoScalingGroup(terminateInstanceIRequest);
        System.out.format("Terminated instance %s.", instanceId);
    }
    // snippet-end:[javav2.cross_service.resilient_service.auto-scaling.DeleteAutoScalingGroup]

    // snippet-start:[javav2.cross_service.resilient_service.ec2.ReplaceIamInstanceProfileAssociation]
    /**
     * Replaces the profile associated with a running instance. After the profile is
     * replaced, the instance is rebooted to ensure that it uses the new profile. When
     * the instance is ready, Systems Manager is used to restart the Python web server.
     */
    public void replaceInstanceProfile(String instanceId, String newInstanceProfileName, String profileAssociationId) throws InterruptedException {
        // Create an IAM instance profile specification.
        software.amazon.awssdk.services.ec2.model.IamInstanceProfileSpecification iamInstanceProfile = software.amazon.awssdk.services.ec2.model.IamInstanceProfileSpecification.builder()
            .name(newInstanceProfileName) // Make sure 'newInstanceProfileName' is a valid IAM Instance Profile name.
            .build();

        // Replace the IAM instance profile association for the EC2 instance.
        ReplaceIamInstanceProfileAssociationRequest replaceRequest = ReplaceIamInstanceProfileAssociationRequest.builder()
            .iamInstanceProfile(iamInstanceProfile)
            .associationId(profileAssociationId) // Make sure 'profileAssociationId' is a valid association ID.
            .build();

        try {
            getEc2Client().replaceIamInstanceProfileAssociation(replaceRequest);
            // Handle the response as needed.
        } catch (Ec2Exception e) {
            // Handle exceptions, log, or report the error.
            System.err.println("Error: " + e.getMessage());
        }
        System.out.format("Replaced instance profile for association %s with profile %s.", profileAssociationId, newInstanceProfileName);
        TimeUnit.SECONDS.sleep(15);
        boolean instReady = false;
        int tries = 0;

        // Reboot after 60 seconds
        while (!instReady) {
            if (tries % 6 == 0) {
                getEc2Client().rebootInstances(RebootInstancesRequest.builder()
                    .instanceIds(instanceId)
                    .build());
                System.out.println("Rebooting instance " + instanceId + " and waiting for it to be ready.");
            }
            tries++;
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DescribeInstanceInformationResponse informationResponse = getSSMClient().describeInstanceInformation();
            List<InstanceInformation> instanceInformationList = informationResponse.instanceInformationList();
            for (InstanceInformation info : instanceInformationList) {
                if (info.instanceId().equals(instanceId)) {
                    instReady = true;
                    break;
                }
            }
        }

        SendCommandRequest sendCommandRequest = SendCommandRequest.builder()
            .instanceIds(instanceId)
            .documentName("AWS-RunShellScript")
            .parameters(Collections.singletonMap("commands", Collections.singletonList("cd / && sudo python3 server.py 80")))
            .build();

        getSSMClient().sendCommand(sendCommandRequest);
        System.out.println("Restarted the Python web server on instance " + instanceId + ".");
    }
    // snippet-end:[javav2.cross_service.resilient_service.ec2.ReplaceIamInstanceProfileAssociation]


    // snippet-start:[javav2.cross_service.resilient_service.ec2.AuthorizeSecurityGroupIngress]
    public void openInboundPort(String secGroupId, String port, String ipAddress) {
        AuthorizeSecurityGroupIngressRequest ingressRequest = AuthorizeSecurityGroupIngressRequest.builder()
            .groupName(secGroupId)
            .cidrIp(ipAddress)
            .fromPort(Integer.parseInt(port))
            .build();

        getEc2Client().authorizeSecurityGroupIngress(ingressRequest);
        System.out.format("Authorized ingress to %s on port %s from %s.", secGroupId, port, ipAddress);
    }
    // snippet-end:[javav2.cross_service.resilient_service.ec2.AuthorizeSecurityGroupIngress]

    //snippet-start:[javav2.cross_service.resilient_service.iam.DeleteInstanceProfile]
    /**
     *  Detaches a role from an instance profile, detaches policies from the role,
     *  and deletes all the resources.
     */
    public void deleteInstanceProfile(String roleName, String profileName){
        try {
            software.amazon.awssdk.services.iam.model.GetInstanceProfileRequest getInstanceProfileRequest = software.amazon.awssdk.services.iam.model.GetInstanceProfileRequest.builder()
                .instanceProfileName(profileName)
                .build();

            GetInstanceProfileResponse response = getIAMClient().getInstanceProfile(getInstanceProfileRequest);
            String name = response.instanceProfile().instanceProfileName();
            System.out.println(name);

            RemoveRoleFromInstanceProfileRequest profileRequest = RemoveRoleFromInstanceProfileRequest.builder()
                .instanceProfileName(profileName)
                .roleName(roleName)
                .build();

            getIAMClient().removeRoleFromInstanceProfile(profileRequest);
            DeleteInstanceProfileRequest deleteInstanceProfileRequest = DeleteInstanceProfileRequest.builder()
                .instanceProfileName(profileName)
                .build();

            getIAMClient().deleteInstanceProfile(deleteInstanceProfileRequest);
            System.out.println("Deleted instance profile " + profileName);

            DeleteRoleRequest deleteRoleRequest = DeleteRoleRequest.builder()
                .roleName(roleName)
                .build();

            // List attached role policies.
            ListAttachedRolePoliciesResponse rolesResponse = getIAMClient().listAttachedRolePolicies(role -> role.roleName(roleName));
            List<AttachedPolicy> attachedPolicies = rolesResponse.attachedPolicies();
            for (AttachedPolicy attachedPolicy : attachedPolicies) {
                DetachRolePolicyRequest request = DetachRolePolicyRequest.builder()
                    .roleName(roleName)
                    .policyArn(attachedPolicy.policyArn())
                    .build();

                getIAMClient().detachRolePolicy(request);
                System.out.println("Detached and deleted policy " + attachedPolicy.policyName());
            }

            getIAMClient().deleteRole(deleteRoleRequest);
            System.out.println("Instance profile and role deleted.");

        } catch (IamException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    //snippet-end:[javav2.cross_service.resilient_service.iam.DeleteInstanceProfile]

    //snippet-start:[javav2.cross_service.resilient_service.ec2.DeleteLaunchTemplate]
    public void deleteTemplate(String templateName) {
        getEc2Client().deleteLaunchTemplate(name->name.launchTemplateName(templateName));
        System.out.format(templateName +" was deleted.");
    }
    //snippet-end:[javav2.cross_service.resilient_service.ec2.DeleteLaunchTemplate]


    public void deleteAutoScaleGroup(String groupName) {
        DeleteAutoScalingGroupRequest deleteAutoScalingGroupRequest = DeleteAutoScalingGroupRequest.builder()
            .autoScalingGroupName(groupName)
            .forceDelete(true)
            .build();

        getAutoScalingClient().deleteAutoScalingGroup(deleteAutoScalingGroupRequest);
        System.out.println(groupName +" was deleted.");
    }

    // snippet-start:[javav2.cross_service.resilient_service.ec2.DescribeSecurityGroups]
    /*
       Verify the default security group of the specified VPC allows ingress from this
        computer. This can be done by allowing ingress from this computer's IP
        address. In some situations, such as connecting from a corporate network, you
        must instead specify a prefix list ID. You can also temporarily open the port to
        any IP address while running this example. If you do, be sure to remove public
        access when you're done.

     */
    public GroupInfo verifyInboundPort(String VPC, int port, String ipAddress) {
        boolean portIsOpen = false;
        GroupInfo groupInfo = new GroupInfo();
        try {
            Filter filter = Filter.builder()
                .name("group-name")
                .values("default")
                .build();

            Filter filter1 = Filter.builder()
                .name("vpc-id")
                .values(VPC)
                .build();

            DescribeSecurityGroupsRequest securityGroupsRequest = DescribeSecurityGroupsRequest.builder()
                .filters(filter, filter1)
                .build();

            DescribeSecurityGroupsResponse securityGroupsResponse = getEc2Client().describeSecurityGroups(securityGroupsRequest);
            String securityGroup = securityGroupsResponse.securityGroups().get(0).groupName();
            groupInfo.setGroupName(securityGroup);

            for (SecurityGroup secGroup : securityGroupsResponse.securityGroups()) {
                System.out.println("Found security group: " + secGroup.groupId());

                for (IpPermission ipPermission : secGroup.ipPermissions()) {
                    if (ipPermission.fromPort() == port) {
                        System.out.println("Found inbound rule: " + ipPermission);
                        for (IpRange ipRange : ipPermission.ipRanges()) {
                            String cidrIp = ipRange.cidrIp();
                            if (cidrIp.startsWith(ipAddress) || cidrIp.equals("0.0.0.0/0")) {
                                System.out.println(cidrIp +" is applicable");
                                portIsOpen = true;
                            }
                        }

                        if (!ipPermission.prefixListIds().isEmpty()) {
                            System.out.println("Prefix lList is applicable");
                            portIsOpen = true;
                        }

                        if (!portIsOpen) {
                            System.out.println("The inbound rule does not appear to be open to either this computer's IP,"
                                + " all IP addresses (0.0.0.0/0), or to a prefix list ID.");
                        } else {
                            break;
                        }
                    }
                }
            }

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        groupInfo.setPortOpen(portIsOpen);
        return groupInfo;
    }
    // snippet-end:[javav2.cross_service.resilient_service.ec2.DescribeSecurityGroups]

    // snippet-start:[javav2.cross_service.resilient_service.auto-scaling.AttachLoadBalancerTargetGroups]
    /*
        Attaches an Elastic Load Balancing (ELB) target group to this EC2 Auto Scaling group.
        The target group specifies how the load balancer forward requests to the instances
        in the group.
     */
    public void attachLoadBalancerTargetGroup(String asGroupName, String targetGroupARN) {
        try {
            AttachLoadBalancerTargetGroupsRequest targetGroupsRequest = AttachLoadBalancerTargetGroupsRequest .builder()
                .autoScalingGroupName(asGroupName)
                .targetGroupARNs(targetGroupARN)
                .build();

            getAutoScalingClient().attachLoadBalancerTargetGroups(targetGroupsRequest);
            System.out.println("Attached load balancer to "+asGroupName);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[javav2.cross_service.resilient_service.auto-scaling.AttachLoadBalancerTargetGroups]

    //snippet-start:[javav2.cross_service.resilient_service.auto-scaling.CreateAutoScalingGroup]
    // Creates an EC2 Auto Scaling group with the specified size.
    public String[] createGroup(int groupSize, String templateName, String autoScalingGroupName ) {

        // Get availability zones.
        software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesRequest zonesRequest = software.amazon.awssdk.services.ec2.model.DescribeAvailabilityZonesRequest.builder()
            .build();

        DescribeAvailabilityZonesResponse zonesResponse = getEc2Client().describeAvailabilityZones(zonesRequest);
        List<String> availabilityZoneNames = zonesResponse.availabilityZones().stream()
            .map(software.amazon.awssdk.services.ec2.model.AvailabilityZone::zoneName)
            .collect(Collectors.toList());

        String availabilityZones = String.join(",", availabilityZoneNames);
        LaunchTemplateSpecification specification = LaunchTemplateSpecification.builder()
            .launchTemplateName(templateName)
            .version("$Default")
            .build();

        String[] zones = availabilityZones.split(",");
        CreateAutoScalingGroupRequest groupRequest = CreateAutoScalingGroupRequest.builder()
            .launchTemplate(specification)
            .availabilityZones(zones)
            .maxSize(groupSize)
            .minSize(groupSize)
            .autoScalingGroupName(autoScalingGroupName)
            .build();

        try {
            getAutoScalingClient().createAutoScalingGroup(groupRequest);

        } catch (AutoScalingException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        System.out.println("Created an EC2 Auto Scaling group named "+ autoScalingGroupName);
        return zones;
    }
    //snippet-end:[javav2.cross_service.resilient_service.auto-scaling.CreateAutoScalingGroup]

    public String getDefaultVPC() {
        // Define the filter.
        Filter defaultFilter = Filter.builder()
            .name("is-default")
            .values("true")
            .build();

        software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest request = software.amazon.awssdk.services.ec2.model.DescribeVpcsRequest.builder()
            .filters(defaultFilter)
            .build();

        DescribeVpcsResponse response = getEc2Client().describeVpcs(request);
        return response.vpcs().get(0).vpcId();
    }

    // Gets the default subnets in a VPC for a specified list of Availability Zones.
    public List<Subnet> getSubnets(String vpcId, String[] availabilityZones) {
        List<Subnet> subnets = null;
        Filter vpcFilter = Filter.builder()
            .name("vpc-id")
            .values(vpcId)
            .build();

        Filter azFilter = Filter.builder()
            .name("availability-zone")
                                .values(availabilityZones)
            .build();

        Filter defaultForAZ = Filter.builder()
            .name("default-for-az")
            .values("true")
            .build();

        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder()
            .filters(vpcFilter, azFilter, defaultForAZ)
            .build();

        DescribeSubnetsResponse response = getEc2Client().describeSubnets(request);
        subnets = response.subnets();
        return subnets;
    }

    // Gets data about the instances in the EC2 Auto Scaling group.
    public String getBadInstance(String groupName) {
        DescribeAutoScalingGroupsRequest request = DescribeAutoScalingGroupsRequest.builder()
            .autoScalingGroupNames(groupName)
            .build();

        DescribeAutoScalingGroupsResponse response = getAutoScalingClient().describeAutoScalingGroups(request);
        AutoScalingGroup autoScalingGroup = response.autoScalingGroups().get(0);
        List<String> instanceIds = autoScalingGroup.instances().stream()
            .map(instance -> instance.instanceId())
            .collect(Collectors.toList());

        String[] instanceIdArray = instanceIds.toArray(new String[0]);
        for (String instanceId : instanceIdArray) {
            System.out.println("Instance ID: " + instanceId);
            return instanceId;
        }
        return "";
    }

    // snippet-start:[javav2.cross_service.resilient_service.ec2.DescribeIamInstanceProfileAssociations]
    // Gets data about the profile associated with an instance.
    public String getInstanceProfile(String instanceId) {
        Filter filter = Filter.builder()
            .name("instance-id")
            .values(instanceId)
            .build();

        DescribeIamInstanceProfileAssociationsRequest associationsRequest = DescribeIamInstanceProfileAssociationsRequest.builder()
            .filters(filter)
            .build();

        DescribeIamInstanceProfileAssociationsResponse response = getEc2Client().describeIamInstanceProfileAssociations(associationsRequest);
        return response.iamInstanceProfileAssociations().get(0).associationId();
    }
    // snippet-end:[javav2.cross_service.resilient_service.ec2.DescribeIamInstanceProfileAssociations]

    public void deleteRolesPolicies(String policyName, String roleName, String InstanceProfile ) {
        ListPoliciesRequest listPoliciesRequest = ListPoliciesRequest.builder().build();
        ListPoliciesResponse listPoliciesResponse = getIAMClient().listPolicies(listPoliciesRequest);
        for (Policy policy : listPoliciesResponse.policies()) {
            if (policy.policyName().equals(policyName)) {
                // List the entities (users, groups, roles) that are attached to the policy.
                software.amazon.awssdk.services.iam.model.ListEntitiesForPolicyRequest listEntitiesRequest = software.amazon.awssdk.services.iam.model.ListEntitiesForPolicyRequest.builder()
                    .policyArn(policy.arn())
                    .build();
                ListEntitiesForPolicyResponse listEntitiesResponse = iamClient.listEntitiesForPolicy(listEntitiesRequest);
                if (!listEntitiesResponse.policyGroups().isEmpty() || !listEntitiesResponse.policyUsers().isEmpty() || !listEntitiesResponse.policyRoles().isEmpty()) {
                    // Detach the policy from any entities it is attached to.
                    DetachRolePolicyRequest detachPolicyRequest = DetachRolePolicyRequest.builder()
                        .policyArn(policy.arn())
                        .roleName(roleName) // Specify the name of the IAM role
                        .build();

                    getIAMClient().detachRolePolicy(detachPolicyRequest);
                    System.out.println("Policy detached from entities.");
                }

                // Now, you can delete the policy.
                DeletePolicyRequest deletePolicyRequest = DeletePolicyRequest.builder()
                    .policyArn(policy.arn())
                    .build();

                getIAMClient().deletePolicy(deletePolicyRequest);
                System.out.println("Policy deleted successfully.");
                break;
            }
        }

        // List the roles associated with the instance profile
        ListInstanceProfilesForRoleRequest listRolesRequest = ListInstanceProfilesForRoleRequest .builder()
            .roleName(roleName)
            .build();

        // Detach the roles from the instance profile
        ListInstanceProfilesForRoleResponse listRolesResponse = iamClient.listInstanceProfilesForRole(listRolesRequest);
        for (software.amazon.awssdk.services.iam.model.InstanceProfile profile : listRolesResponse.instanceProfiles()) {
            RemoveRoleFromInstanceProfileRequest removeRoleRequest = RemoveRoleFromInstanceProfileRequest.builder()
                .instanceProfileName(InstanceProfile)
                .roleName(roleName) // Remove the extra dot here
                .build();

            getIAMClient().removeRoleFromInstanceProfile(removeRoleRequest);
            System.out.println("Role " + roleName + " removed from instance profile " + InstanceProfile);
        }

        // Delete the instance profile after removing all roles
        DeleteInstanceProfileRequest deleteInstanceProfileRequest = DeleteInstanceProfileRequest.builder()
            .instanceProfileName(InstanceProfile)
            .build();


        getIAMClient().deleteInstanceProfile(r->r.instanceProfileName(InstanceProfile));
        System.out.println(InstanceProfile +" Deleted");
        System.out.println("All roles and policies are deleted.");
    }
}
// snippet-end:[javav2.example_code.workflow.ResilientService_AutoScaler]
