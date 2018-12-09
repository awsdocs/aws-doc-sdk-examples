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
 *
 *
 *
 */

require 'vendor/autoload.php';

use Aws\Rds\RdsClient;
use Aws\Exception\AwsException;


//Create a RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-2'
]);

try {
    $result = $rdsClient->describeDBInstances([

    ]);
    foreach ($result['DBInstances'] as $instance) {
        print('<p>DB Identifier: ' . $instance['DBInstanceIdentifier']);
        print('<br />Endpoint: ' . $instance['Endpoint']["Address"]
            . ':' . $instance['Endpoint']["Port"]);
        print('<br />Current Status: ' . $instance["DBInstanceStatus"]);
        print('</p>');
    }
    print(" Raw Result ");
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
}
//snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
//snippet-sourcedescription:[DescribeInstance.php demonstrates how to list your RDS database instances and obtain connection endpoints.]
//snippet-keyword:[PHP]
//snippet-keyword:[AWS SDK for PHP v3]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Relational Database Service]
//snippet-service:[rds]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-12-09]
//snippet-sourceauthor:[jschwarzwalder (AWS)]
