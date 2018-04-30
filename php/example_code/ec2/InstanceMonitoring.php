<?php
/**
 * Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *  ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/aws-sdk-php/v3/guide/examples/ec2-examples-managing-instances.html
 *
 */
require 'vendor/autoload.php';

use Aws\Ec2\Ec2Client;

/**
 * Enable/Disable Instance Monitoring
 *
 * This code expects that you have AWS credentials set up per:
 * http://docs.aws.amazon.com/aws-sdk-php/v3/guide/guide/credentials.html
 */

$ec2Client = new Ec2Client([
    'region' => 'us-west-2',
    'version' => '2016-11-15',
    'profile' => 'default'
]);

$instanceIds = array('InstanceID1', 'InstanceID2');

$monitorInstance = 'ON';

if ($monitorInstance == 'ON') {
    $result = $ec2Client->monitorInstances(array(
        'InstanceIds' => $instanceIds
    ));
} else {
    $result = $ec2Client->unmonitorInstances(array(
        'InstanceIds' => $instanceIds
    ));
}

var_dump($result);
