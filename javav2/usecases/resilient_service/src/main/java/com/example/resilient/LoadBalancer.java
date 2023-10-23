/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.resilient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.model.Subnet;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.Action;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.CreateListenerRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.CreateLoadBalancerRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.CreateLoadBalancerResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.CreateTargetGroupRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.CreateTargetGroupResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeLoadBalancersResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetGroupsResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeTargetHealthResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.ElasticLoadBalancingV2Exception;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.TargetHealthDescription;
import software.amazon.awssdk.services.elasticloadbalancingv2.waiters.ElasticLoadBalancingV2Waiter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// snippet-start:[javav2.example_code.workflow.ResilientService_LoadBalancer]
public class LoadBalancer {
    public ElasticLoadBalancingV2Client elasticLoadBalancingV2Client;

    public ElasticLoadBalancingV2Client getLoadBalancerClient() {
        if (elasticLoadBalancingV2Client == null) {
            elasticLoadBalancingV2Client = ElasticLoadBalancingV2Client.builder()
                .region(Region.US_EAST_1)
                .build();
        }

        return elasticLoadBalancingV2Client;
    }

    // snippet-start:[javav2.cross_service.resilient_service.elbv2.DescribeTargetHealth]
    // Checks the health of the instances in the target group.
    public List<TargetHealthDescription> checkTargetHealth(String targetGroupName) {
        DescribeTargetGroupsRequest targetGroupsRequest = DescribeTargetGroupsRequest.builder()
            .names(targetGroupName)
            .build();

        DescribeTargetGroupsResponse tgResponse = getLoadBalancerClient().describeTargetGroups(targetGroupsRequest);

        DescribeTargetHealthRequest healthRequest = DescribeTargetHealthRequest.builder()
            .targetGroupArn(tgResponse.targetGroups().get(0).targetGroupArn())
            .build();

        DescribeTargetHealthResponse healthResponse = getLoadBalancerClient().describeTargetHealth(healthRequest);
        return healthResponse.targetHealthDescriptions();
    }
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.DescribeTargetHealth]


    // Gets the HTTP endpoint of the load balancer.
    public String getEndpoint(String lbName){
        DescribeLoadBalancersResponse res = getLoadBalancerClient().describeLoadBalancers(describe -> describe.names(lbName));
        return res.loadBalancers().get(0).dnsName();
    }

    // snippet-start:[javav2.cross_service.resilient_service.elbv2.DeleteLoadBalancer]
    // Deletes a load balancer.
    public void deleteLoadBalancer(String lbName) {
        try {
            // Use a waiter to delete the Load Balancer.
            DescribeLoadBalancersResponse res = getLoadBalancerClient().describeLoadBalancers(describe -> describe.names(lbName));
            ElasticLoadBalancingV2Waiter loadBalancerWaiter = getLoadBalancerClient().waiter();
            DescribeLoadBalancersRequest request = DescribeLoadBalancersRequest.builder()
                .loadBalancerArns(res.loadBalancers().get(0).loadBalancerArn())
                .build();

            getLoadBalancerClient().deleteLoadBalancer(builder -> builder.loadBalancerArn(res.loadBalancers().get(0).loadBalancerArn()));
            WaiterResponse<DescribeLoadBalancersResponse> waiterResponse = loadBalancerWaiter.waitUntilLoadBalancersDeleted(request);
            waiterResponse.matched().response().ifPresent(System.out::println);

        } catch (ElasticLoadBalancingV2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        System.out.println(lbName +" was deleted.");
    }
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.DeleteLoadBalancer]

    // snippet-start:[javav2.cross_service.resilient_service.elbv2.DeleteTargetGroup]
    // Deletes the target group.
    public void deleteTargetGroup(String targetGroupName) {
        try {
            DescribeTargetGroupsResponse res = getLoadBalancerClient().describeTargetGroups(describe -> describe.names(targetGroupName));
            getLoadBalancerClient().deleteTargetGroup(builder -> builder.targetGroupArn(res.targetGroups().get(0).targetGroupArn()));
        } catch (ElasticLoadBalancingV2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }
        System.out.println(targetGroupName +" was deleted.");
    }
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.DeleteTargetGroup]

    // Verify this computer can successfully send a GET request to the load balancer endpoint.
    public boolean verifyLoadBalancerEndpoint(String elbDnsName) throws IOException, InterruptedException {
        boolean success = false;
        int retries = 3;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create an HTTP GET request to the ELB.
        HttpGet httpGet = new HttpGet("http://" + elbDnsName);
        try {
            while ((!success) && (retries > 0)) {
                // Execute the request and get the response.
                HttpResponse response = httpClient.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                System.out.println("HTTP Status Code: " + statusCode);
                if (statusCode == 200) {
                    success = true ;
                } else {
                    retries-- ;
                    System.out.println("Got connection error from load balancer endpoint, retrying...");
                    TimeUnit.SECONDS.sleep(15);
                }
            }

        } catch (org.apache.http.conn.HttpHostConnectException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Status.." + success);
        return success;
    }

    // snippet-start:[javav2.cross_service.resilient_service.elbv2.CreateTargetGroup]
    /*
        Creates an Elastic Load Balancing target group. The target group specifies how
        the load balancer forward requests to instances in the group and how instance
        health is checked.
     */
    public String createTargetGroup(String protocol, int port, String vpcId, String targetGroupName) {
        CreateTargetGroupRequest targetGroupRequest = CreateTargetGroupRequest.builder()
            .healthCheckPath("/healthcheck")
            .healthCheckTimeoutSeconds(5)
            .port(port)
            .vpcId(vpcId)
            .name(targetGroupName)
            .protocol(protocol)
            .build();

        CreateTargetGroupResponse targetGroupResponse = getLoadBalancerClient().createTargetGroup(targetGroupRequest);
        String targetGroupArn = targetGroupResponse.targetGroups().get(0).targetGroupArn();
        String targetGroup = targetGroupResponse.targetGroups().get(0).targetGroupName();
        System.out.println("The " + targetGroup + " was created with ARN" + targetGroupArn);
        return targetGroupArn;
    }
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.CreateTargetGroup]

    // snippet-start:[javav2.cross_service.resilient_service.elbv2.CreateLoadBalancer]
    // snippet-start:[javav2.cross_service.resilient_service.elbv2.CreateListener]
    /*
        Creates an Elastic Load Balancing load balancer that uses the specified subnets
        and forwards requests to the specified target group.
     */
    public String createLoadBalancer(List<Subnet> subnetIds, String targetGroupARN, String lbName, int port, String protocol) {
        try {
            List<String> subnetIdStrings = subnetIds.stream()
                .map(Subnet::subnetId)
                .collect(Collectors.toList());

            CreateLoadBalancerRequest balancerRequest = CreateLoadBalancerRequest.builder()
                .subnets(subnetIdStrings)
                .name(lbName)
                .scheme("internet-facing")
                .build();

            // Create and wait for the load balancer to become available.
            CreateLoadBalancerResponse lsResponse = getLoadBalancerClient().createLoadBalancer(balancerRequest);
            String lbARN = lsResponse.loadBalancers().get(0).loadBalancerArn();

            ElasticLoadBalancingV2Waiter loadBalancerWaiter = getLoadBalancerClient().waiter();
            DescribeLoadBalancersRequest request = DescribeLoadBalancersRequest.builder()
                .loadBalancerArns(lbARN)
                .build();

            System.out.println("Waiting for Load Balancer " + lbName + " to become available.");
            WaiterResponse<DescribeLoadBalancersResponse> waiterResponse = loadBalancerWaiter.waitUntilLoadBalancerAvailable(request);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.println("Load Balancer " + lbName + " is available.");

            // Get the DNS name (endpoint) of the load balancer.
            String lbDNSName = lsResponse.loadBalancers().get(0).dnsName();
            System.out.println("*** Load Balancer DNS Name: " + lbDNSName);

            // Create a listener for the load balance.
            Action action = Action.builder()
                .targetGroupArn(targetGroupARN)
                .type("forward")
                .build();

            CreateListenerRequest listenerRequest = CreateListenerRequest.builder()
                .loadBalancerArn(lsResponse.loadBalancers().get(0).loadBalancerArn())
                .defaultActions(action)
                .port(port)
                .protocol(protocol)
                .defaultActions(action)
                .build();

            getLoadBalancerClient().createListener(listenerRequest);
            System.out.println( "Created listener to forward traffic from load balancer " + lbName + " to target group " + targetGroupARN);

            // Return the load balancer DNS name.
            return lbDNSName;

        } catch (ElasticLoadBalancingV2Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.CreateListener]
    // snippet-end:[javav2.cross_service.resilient_service.elbv2.CreateLoadBalancer]
}
// snippet-end:[javav2.example_code.workflow.ResilientService_LoadBalancer]