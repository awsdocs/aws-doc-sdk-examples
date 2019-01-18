<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
// snippet-start:[rds.php.create_db_instance.complete]
// snippet-start:[rds.php.create_db_instance.import]

require 'vendor/autoload.php';

use Aws\Rds\RdsClient; 
use Aws\Exception\AwsException;
// snippet-end:[rds.php.create_db_instance.import]

// snippet-start:[rds.php.create_db_instance.main]
//Create a RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-2'
]);

$dbIdentifier = '<<{{db-identifier}}>>';
$dbClass = 'db.t2.micro';
$storage = 5;
$engine = 'MySQL';
$username = 'MyUser';
$password =  'MyPassword';
]);
try {
    $result = $rdsClient->createDBInstance([
        'DBInstanceIdentifier' => $dbIdentifier,
        'DBInstanceClass' => $dbClass ,
        'AllocatedStorage' => $storage,
        'Engine' => $engine,
        'MasterUsername' => $username,
        'MasterUserPassword' => $password,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
} 
// snippet-end:[rds.php.create_db_instance.main]
// snippet-end:[rds.php.create_db_instance.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateDBInstance.php demonstrates how to create a MySQL database with storage capacity of 5GB.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Relational Database Service]
// snippet-service:[rds]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-12-09]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

