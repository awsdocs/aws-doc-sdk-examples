<?php

// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#snippet-start:[php.example_code.auto-scaling.service]
namespace AutoScaling;

use Aws\AutoScaling\AutoScalingClient;
use Aws\AutoScaling\Exception\AutoScalingException;
use AwsUtilities\AWSServiceClass;
use Exception;

class AutoScalingService extends AWSServiceClass
{
    protected AutoScalingClient $autoScalingClient;

    public function __construct($autoScalingClient)
    {
        $this->autoScalingClient = $autoScalingClient;
    }

    #snippet-start:[php.example_code.auto-scaling.service.createAutoScalingGroup]
    public function createAutoScalingGroup(
        $autoScalingGroupName,
        $availabilityZones,
        $minSize,
        $maxSize,
        $launchTemplateId
    ) {
        return $this->autoScalingClient->createAutoScalingGroup([
            'AutoScalingGroupName' => $autoScalingGroupName,
            'AvailabilityZones' => $availabilityZones,
            'MinSize' => $minSize,
            'MaxSize' => $maxSize,
            'LaunchTemplate' => [
                'LaunchTemplateId' => $launchTemplateId,
            ],
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.createAutoScalingGroup]

    #snippet-start:[php.example_code.auto-scaling.service.updateAutoScalingGroup]
    public function updateAutoScalingGroup($autoScalingGroupName, $args)
    {
        if (array_key_exists('MaxSize', $args)) {
            $maxSize = ['MaxSize' => $args['MaxSize']];
        } else {
            $maxSize = [];
        }
        if (array_key_exists('MinSize', $args)) {
            $minSize = ['MinSize' => $args['MinSize']];
        } else {
            $minSize = [];
        }
        $parameters = ['AutoScalingGroupName' => $autoScalingGroupName];
        $parameters = array_merge($parameters, $minSize, $maxSize);
        return $this->autoScalingClient->updateAutoScalingGroup($parameters);
    }
    #snippet-end:[php.example_code.auto-scaling.service.updateAutoScalingGroup]

    #snippet-start:[php.example_code.auto-scaling.service.deleteAutoScalingGroup]
    public function deleteAutoScalingGroup($autoScalingGroupName)
    {
        return $this->autoScalingClient->deleteAutoScalingGroup([
            'AutoScalingGroupName' => $autoScalingGroupName,
            'ForceDelete' => true,
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.deleteAutoScalingGroup]

    #snippet-start:[php.example_code.auto-scaling.service.describeAutoScalingGroups]
    public function describeAutoScalingGroups($autoScalingGroupNames)
    {
        return $this->autoScalingClient->describeAutoScalingGroups([
            'AutoScalingGroupNames' => $autoScalingGroupNames
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.describeAutoScalingGroups]

    public function waitUntilGroupInService($autoScalingGroupNames)
    {
        $this->autoScalingClient->waitUntil('GroupInService', ['AutoScalingGroupNames' => $autoScalingGroupNames]);
    }

    #snippet-start:[php.example_code.auto-scaling.service.terminateInstanceInAutoScalingGroup]
    public function terminateInstanceInAutoScalingGroup(
        $instanceId,
        $shouldDecrementDesiredCapacity = true,
        $attempts = 0
    ) {
        try {
            return $this->autoScalingClient->terminateInstanceInAutoScalingGroup([
                'InstanceId' => $instanceId,
                'ShouldDecrementDesiredCapacity' => $shouldDecrementDesiredCapacity,
            ]);
        } catch (AutoScalingException $exception) {
            if ($exception->getAwsErrorCode() == "ScalingActivityInProgress" && $attempts < 5) {
                error_log("Cannot terminate an instance while it is still pending. Waiting then trying again.");
                sleep(5 * (1 + $attempts));
                return $this->terminateInstanceInAutoScalingGroup(
                    $instanceId,
                    $shouldDecrementDesiredCapacity,
                    ++$attempts
                );
            } else {
                throw $exception;
            }
        }
    }
    #snippet-end:[php.example_code.auto-scaling.service.terminateInstanceInAutoScalingGroup]

    /**
     * @throws Exception
     */
    public function terminateAllInstancesInAutoScalingGroup(
        $autoScalingGroupName,
        $shouldDecrementDesiredCapacity = true,
        $attempts = 0
    ) {
        $instances = $this->describeAutoScalingGroups([$autoScalingGroupName])['AutoScalingGroups'][0]['Instances'];
        foreach ($instances as $instance) {
            $this->terminateInstanceInAutoScalingGroup(
                $instance['InstanceId'],
                $shouldDecrementDesiredCapacity,
                $attempts
            );
        }
        $tries = 0;
        do {
            $autoScalingGroups = $this->describeAutoScalingGroups([$autoScalingGroupName]);
            sleep(10 * $tries++);
            if ($tries > 10) {
                throw new Exception("Terminating instances took too long.");
            }
        } while (count($autoScalingGroups['AutoScalingGroups'][0]['Instances']) > 0);
    }

    #snippet-start:[php.example_code.auto-scaling.service.setDesiredCapacity]
    public function setDesiredCapacity($autoScalingGroupName, $desiredCapacity)
    {
        return $this->autoScalingClient->setDesiredCapacity([
            'AutoScalingGroupName' => $autoScalingGroupName,
            'DesiredCapacity' => $desiredCapacity,
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.setDesiredCapacity]

    #snippet-start:[php.example_code.auto-scaling.service.describeAutoScalingInstances]
    public function describeAutoScalingInstances($instanceIds)
    {
        return $this->autoScalingClient->describeAutoScalingInstances([
            'InstanceIds' => $instanceIds
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.describeAutoScalingInstances]

    #snippet-start:[php.example_code.auto-scaling.service.describeScalingActivities]
    public function describeScalingActivities($autoScalingGroupName)
    {
        return $this->autoScalingClient->describeScalingActivities([
            'AutoScalingGroupName' => $autoScalingGroupName,
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.describeScalingActivities]

    #snippet-start:[php.example_code.auto-scaling.service.enableMetricsCollection]
    public function enableMetricsCollection($autoScalingGroupName, $granularity)
    {
        return $this->autoScalingClient->enableMetricsCollection([
            'AutoScalingGroupName' => $autoScalingGroupName,
            'Granularity' => $granularity,
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.enableMetricsCollection]

    #snippet-start:[php.example_code.auto-scaling.service.disableMetricsCollection]
    public function disableMetricsCollection($autoScalingGroupName)
    {
        return $this->autoScalingClient->disableMetricsCollection([
            'AutoScalingGroupName' => $autoScalingGroupName,
        ]);
    }
    #snippet-end:[php.example_code.auto-scaling.service.disableMetricsCollection]

    #snippet-start:[php.example_code.auto-scaling.service.describeAccountLimits]
    public function describeAccountLimits()
    {
        return $this->autoScalingClient->describeAccountLimits();
    }
    #snippet-end:[php.example_code.auto-scaling.service.describeAccountLimits]
}
#snippet-end:[php.example_code.auto-scaling.service]
