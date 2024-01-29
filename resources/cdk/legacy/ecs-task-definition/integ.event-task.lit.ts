// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

//
// This file is licensed under the Apache License, Version 2.0 (the "License").
// You may not use this file except in compliance with the License. A copy of the
// License is located at
//
// http://aws.amazon.com/apache2.0/
//
// This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
// OF ANY KIND, either express or implied. See the License for the specific
// language governing permissions and limitations under the License.
// snippet-start:[cdk.typescript.start_ecs_task]
// Create a Task Definition for the container to start
const taskDefinition = new ecs.Ec2TaskDefinition(this, "TaskDef");
taskDefinition.addContainer("TheContainer", {
  image: ecs.ContainerImage.fromAsset(this, "EventImage", {
    directory: "eventhandler-image",
  }),
  memoryLimitMiB: 256,
  logging: new ecs.AwsLogDriver(this, "TaskLogging", {
    streamPrefix: "EventDemo",
  }),
});

// An EventRule that describes the event trigger (in this case a scheduled run)
const rule = new events.EventRule(this, "Rule", {
  scheduleExpression: "rate(1 minute)",
});

// Use Ec2TaskEventRuleTarget as the target of the EventRule
const target = new ecs.Ec2EventRuleTarget(this, "EventTarget", {
  cluster,
  taskDefinition,
  taskCount: 1,
});

// Pass an environment variable to the container 'TheContainer' in the task
rule.addTarget(target, {
  jsonTemplate: JSON.stringify({
    containerOverrides: [
      {
        name: "TheContainer",
        environment: [
          { name: "I_WAS_TRIGGERED", value: "From CloudWatch Events" },
        ],
      },
    ],
  }),
});
// snippet-end:[cdk.typescript.start_ecs_task]
