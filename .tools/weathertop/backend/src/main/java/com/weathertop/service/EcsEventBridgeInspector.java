// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.weathertop.service;

import software.amazon.awssdk.services.ec2.model.NetworkInterface;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awssdk.services.eventbridge.model.AwsVpcConfiguration;
import java.util.*;
import java.util.stream.Collectors;

public class EcsEventBridgeInspector {
    private final String clusterName;
    private final String taskDefinitionFamily;
    private final String logGroup;
    private final String ruleName;  // changed from rulePrefix

    private final EcsClient ecs = EcsClient.create();
    private final Ec2Client ec2 = Ec2Client.create();
    private final EventBridgeClient events = EventBridgeClient.create();

    public EcsEventBridgeInspector(String clusterName, String taskDefinitionFamily, String logGroup, String ruleName) {
        this.clusterName = clusterName;
        this.taskDefinitionFamily = taskDefinitionFamily;
        this.logGroup = logGroup;
        this.ruleName = ruleName;
    }

    /**
     * Inspects various components related to ECS (Elastic Container Service) and EventBridge.
     * This method performs the following operations:
     *
     * 1. Lists the task definitions for a given family, retrieves the latest one, and fetches its details.
     * 2. Describes the ECS cluster specified by the cluster name.
     * 3. Lists and describes the running tasks for the specified task definition family within the cluster.
     * 4. Retrieves details about a specific EventBridge rule, including its targets and ECS parameters if applicable.
     *
     * The method returns a Map containing the following keys:
     * - "latestTaskDefinitionArn": The ARN of the latest task definition.
     * - "taskDefinitionMetadata": A Map containing metadata about the task definition, including taskRoleArn, executionRoleArn, networkMode, cpu, and memory.
     * - "clusterArn": The ARN of the ECS cluster.
     * - "tasks": A List of Maps, each representing a running task with details like taskArn, lastStatus, desiredStatus, and networkInterface information.
     * - "taskOrService": A String indicating whether the running task is a standalone Task or part of a Service.
     * - "eventBridgeRule": A Map containing details about the EventBridge rule, including its name, scheduleExpression, state, description, human-readable schedule, and a list of targets with their ECS parameters if applicable.
     *
     * In case of errors (e.g., no task definitions found, cluster not found, unable to retrieve EventBridge rule),
     * the response Map will contain an "error" key with the corresponding error message.
     *
     * @return A Map<String, Object> containing the inspection results and metadata.
     */
    public Map<String, Object> inspect() {
        Map<String, Object> response = new LinkedHashMap<>();

        // List task definitions, get latest
        ListTaskDefinitionsResponse defsResp = ecs.listTaskDefinitions(ListTaskDefinitionsRequest.builder()
                .familyPrefix(taskDefinitionFamily)
                .sort(SortOrder.DESC)
                .maxResults(1)
                .build());

        List<String> arns = defsResp.taskDefinitionArns();
        if (arns.isEmpty()) {
            response.put("error", "No task definitions found.");
            return response;
        }

        String latestArn = arns.get(0);
        response.put("latestTaskDefinitionArn", latestArn);

        DescribeTaskDefinitionResponse defDetail = ecs.describeTaskDefinition(
                DescribeTaskDefinitionRequest.builder()
                        .taskDefinition(latestArn).build());

        TaskDefinition td = defDetail.taskDefinition();
        Map<String, Object> taskDefData = new LinkedHashMap<>();
        taskDefData.put("taskRoleArn", td.taskRoleArn());
        taskDefData.put("executionRoleArn", td.executionRoleArn());
        taskDefData.put("networkMode", td.networkModeAsString());
        taskDefData.put("cpu", td.cpu());
        taskDefData.put("memory", td.memory());
        response.put("taskDefinitionMetadata", taskDefData);

        // Describe ECS cluster
        DescribeClustersResponse clusResp = ecs.describeClusters(
                DescribeClustersRequest.builder().clusters(clusterName).build());
        if (clusResp.clusters().isEmpty()) {
            response.put("error", "Cluster not found: " + clusterName);
            return response;
        }

        String clusterArn = clusResp.clusters().get(0).clusterArn();
        response.put("clusterArn", clusterArn);

        // List running tasks for the family
        ListTasksResponse tasksResp = ecs.listTasks(ListTasksRequest.builder()
                .cluster(clusterName)
                .family(taskDefinitionFamily)
                .desiredStatus(DesiredStatus.RUNNING)
                .maxResults(5)
                .build());

        List<Map<String, Object>> tasksList = new ArrayList<>();
        String taskOrServiceType = "Unknown";

        if (tasksResp.taskArns().isEmpty()) {
            response.put("tasks", tasksList);
            response.put("message", "No RUNNING ECS tasks found.");
        } else {
            DescribeTasksResponse dtResp = ecs.describeTasks(
                    DescribeTasksRequest.builder()
                            .cluster(clusterName)
                            .tasks(tasksResp.taskArns())
                            .build());

            for (Task t : dtResp.tasks()) {
                Map<String, Object> taskData = new LinkedHashMap<>();
                taskData.put("taskArn", t.taskArn());
                taskData.put("lastStatus", t.lastStatus());
                taskData.put("desiredStatus", t.desiredStatus());

                // Detect type (Task or Service)
                String group = t.group();
                if (group != null && group.startsWith("service:")) {
                    taskOrServiceType = "Service";
                } else {
                    taskOrServiceType = "Task";
                }

                if ("RUNNING".equalsIgnoreCase(t.lastStatus())) {
                    Optional<String> eniIdOpt = t.attachments().stream()
                            .filter(a -> "ElasticNetworkInterface".equals(a.type()))
                            .flatMap(a -> a.details().stream())
                            .filter(d -> "networkInterfaceId".equals(d.name()))
                            .map(d -> d.value())
                            .findFirst();

                    if (eniIdOpt.isPresent()) {
                        String eniId = eniIdOpt.get();
                        try {
                            DescribeNetworkInterfacesResponse eniResp = ec2.describeNetworkInterfaces(
                                    DescribeNetworkInterfacesRequest.builder()
                                            .networkInterfaceIds(eniId).build());
                            if (!eniResp.networkInterfaces().isEmpty()) {
                                NetworkInterface eni = eniResp.networkInterfaces().get(0);
                                Map<String, Object> eniData = new LinkedHashMap<>();
                                eniData.put("eniId", eni.networkInterfaceId());
                                eniData.put("subnetId", eni.subnetId());
                                eniData.put("securityGroups", eni.groups().stream()
                                        .map(GroupIdentifier::groupId)
                                        .collect(Collectors.toList()));
                                eniData.put("vpcId", eni.vpcId());
                                taskData.put("networkInterface", eniData);
                            } else {
                                taskData.put("networkInterface", "No network interfaces found for ENI ID: " + eniId);
                            }
                        } catch (Exception e) {
                            taskData.put("networkInterfaceError", "Could not retrieve ENI '" + eniId + "': " + e.getMessage());
                        }
                    } else {
                        taskData.put("networkInterface", "No ENI found on the running task.");
                    }
                } else {
                    taskData.put("networkInterface", "Skipped because task is not RUNNING.");
                }

                tasksList.add(taskData);
            }

            response.put("tasks", tasksList);
        }

        response.put("taskOrService", taskOrServiceType);

        // --- EventBridge rule by specific name ---
        Map<String, Object> ruleData = new LinkedHashMap<>();
        try {
            DescribeRuleResponse ruleResp = events.describeRule(
                    DescribeRuleRequest.builder()
                            .name(ruleName)
                            .build());

            ruleData.put("name", ruleResp.name());
            ruleData.put("scheduleExpression", ruleResp.scheduleExpression());
            ruleData.put("state", ruleResp.stateAsString());
            ruleData.put("description", ruleResp.description());
            ruleData.put("humanReadableSchedule", humanTime(ruleResp.scheduleExpression()));

            ListTargetsByRuleResponse targetsResp = events.listTargetsByRule(
                    ListTargetsByRuleRequest.builder()
                            .rule(ruleName)
                            .build());

            List<Map<String, Object>> targetsList = new ArrayList<>();
            for (Target target : targetsResp.targets()) {
                Map<String, Object> targetData = new LinkedHashMap<>();
                targetData.put("id", target.id());
                targetData.put("arn", target.arn());

                if (target.ecsParameters() != null) {
                    EcsParameters p = target.ecsParameters();
                    Map<String, Object> ecsParams = new LinkedHashMap<>();
                    ecsParams.put("taskDefinitionArn", p.taskDefinitionArn());
                    ecsParams.put("launchType", p.launchTypeAsString());

                    if (p.networkConfiguration() != null && p.networkConfiguration().awsvpcConfiguration() != null) {
                        AwsVpcConfiguration awsvpc = p.networkConfiguration().awsvpcConfiguration();
                        ecsParams.put("subnets", awsvpc.subnets());
                        ecsParams.put("securityGroups", awsvpc.securityGroups());
                        ecsParams.put("assignPublicIp", awsvpc.assignPublicIpAsString());
                    }
                    targetData.put("ecsParameters", ecsParams);
                }
                targetsList.add(targetData);
            }
            ruleData.put("targets", targetsList);

        } catch (EventBridgeException e) {
            ruleData.put("error", "Could not retrieve rule '" + ruleName + "': " + e.getMessage());
        }

        response.put("eventBridgeRule", ruleData);

        return response;
    }

    private String humanTime(String expr) {
        if (expr != null && expr.startsWith("cron(") && expr.endsWith(")")) {
            String[] parts = expr.substring(5, expr.length() - 1).split(" ");
            Map<String, String> map = new HashMap<>();
            map.put("1", "Sunday");
            map.put("2", "Monday");
            map.put("3", "Tuesday");
            map.put("4", "Wednesday");
            map.put("5", "Thursday");
            map.put("6", "Friday");
            map.put("7", "Saturday");
            String dow = map.getOrDefault(parts[4], parts[4]);
            int hr = Integer.parseInt(parts[1]);
            return "Every " + dow + " at " + (hr % 12 == 0 ? 12 : hr % 12) + ":00 " + (hr < 12 ? "AM" : "PM") + " UTC";
        } else if (expr != null && expr.startsWith("rate(")) {
            return "Rate schedule: " + expr.substring(5, expr.length() - 1);
        }
        return "Schedule: " + expr;
    }
}

