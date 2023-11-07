/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
import axios from "axios";

import {
  ScenarioAction,
  ScenarioOutput,
} from "@aws-sdk-examples/libs/scenario/scenario.js";

import { MESSAGES, NAMES } from "./constants.js";
import { findLoadBalancer } from "./shared.js";
import {
  DescribeTargetGroupsCommand,
  DescribeTargetHealthCommand,
  ElasticLoadBalancingV2Client,
} from "@aws-sdk/client-elastic-load-balancing-v2";

const getRecommendation = new ScenarioAction("getRecommendation", async (c) => {
  const lb = await findLoadBalancer(NAMES.loadBalancerName);
  if (lb) {
    c.lbDnsName = lb.DNSName;
    c.recommendation = (await axios.get(`http://${c.lbDnsName}`)).data;
  } else {
    throw new Error(MESSAGES.demoFindLbError);
  }
});

const getRecommendationResult = new ScenarioOutput(
  "getRecommendationResult",
  (c) => `Recommendation:\n${JSON.stringify(c.recommendation, null, 2)}`,
  { preformatted: true },
);

const getHealthCheck = new ScenarioAction("getHealthCheck", async (c) => {
  const client = new ElasticLoadBalancingV2Client({});
  const { TargetGroups } = await client.send(
    new DescribeTargetGroupsCommand({
      Names: [NAMES.loadBalancerTargetGroupName],
    }),
  );

  const { TargetHealthDescriptions } = await client.send(
    new DescribeTargetHealthCommand({
      TargetGroupArn: TargetGroups[0].TargetGroupArn,
    }),
  );

  c.targetHealthDescriptions = TargetHealthDescriptions;
});

const getHealthCheckResult = new ScenarioOutput(
  "getHealthCheckResult",
  /**
   * @param {{ targetHealthDescriptions: import('@aws-sdk/client-elastic-load-balancing-v2').TargetHealthDescription[]}} c
   */
  (c) => {
    const status = c.targetHealthDescriptions
      .map((th) => `${th.Target.Id}: ${th.TargetHealth.State}`)
      .join("\n");
    return `Health check:\n${status}`;
  },
  { preformatted: true },
);

/**
 * @type {import('@aws-sdk-examples/libs/scenario.js').Step[]}
 */
export const demoSteps = [
  new ScenarioOutput("demoHeader", MESSAGES.demoHeader, { header: true }),
  new ScenarioOutput("sanityCheck", MESSAGES.demoSanityCheck),
  getRecommendation,
  getRecommendationResult,
  getHealthCheck,
  getHealthCheckResult,
  new ScenarioAction("brokenDependency", () => {}),
  new ScenarioAction("staticResponse", () => {}),
  new ScenarioAction("badCredentials", () => {}),
  new ScenarioAction("deepHealthcheck", () => {}),
  new ScenarioAction("killInstance", () => {}),
  new ScenarioAction("failOpen", () => {}),
];
