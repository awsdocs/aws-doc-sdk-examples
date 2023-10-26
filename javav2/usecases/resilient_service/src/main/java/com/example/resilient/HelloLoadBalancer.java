// snippet-sourcedescription:[DisplayFacesFrame.java demonstrates how to retrieve load balancers or all of your load balancers.]
//snippet-keyword:[AWS SDK for Java v2]
// snippet-service:[Elastic Load Balancing]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.resilient;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.elasticloadbalancingv2.ElasticLoadBalancingV2Client;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.DescribeLoadBalancersResponse;
import software.amazon.awssdk.services.elasticloadbalancingv2.model.LoadBalancer;
import java.util.List;

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

// snippet-start:[javav2.example_code.elbv2.Hello]
public class HelloLoadBalancer {

    public static void main(String[]args){
        ElasticLoadBalancingV2Client loadBalancingV2Client = ElasticLoadBalancingV2Client.builder()
            .region(Region.US_EAST_1)
            .build();

        DescribeLoadBalancersResponse loadBalancersResponse = loadBalancingV2Client.describeLoadBalancers(r->r.pageSize(10));
        List<LoadBalancer> loadBalancerList = loadBalancersResponse.loadBalancers();
        for (LoadBalancer lb : loadBalancerList)
            System.out.println("Load Balancer DNS name = " + lb.dnsName());
    }
}
// snippet-end:[javav2.example_code.elbv2.Hello]
