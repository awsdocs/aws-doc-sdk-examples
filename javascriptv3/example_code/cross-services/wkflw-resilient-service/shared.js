// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import {
  ElasticLoadBalancingV2Client,
  paginateDescribeLoadBalancers,
} from "@aws-sdk/client-elastic-load-balancing-v2";

export async function findLoadBalancer(loadBalancerName) {
  const client = new ElasticLoadBalancingV2Client({});
  const paginatedLoadBalancers = paginateDescribeLoadBalancers(
    { client },
    {
      Names: [loadBalancerName],
    },
  );

  try {
    for await (const page of paginatedLoadBalancers) {
      const loadBalancer = page.LoadBalancers.find(
        (l) => l.LoadBalancerName === loadBalancerName,
      );
      if (loadBalancer) {
        return loadBalancer;
      }
    }
  } catch (e) {
    if (e.name === "LoadBalancerNotFoundException") {
      return undefined;
    }
    throw e;
  }
}
