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
// snippet-start:[rds.php.create_db_replica.complete]
// snippet-start:[rds.php.create_db_replica.import]

require 'vendor/autoload.php';

use Aws\Rds\RdsClient; 
use Aws\Exception\AwsException;
// snippet-end:[rds.php.create_db_replica.import]

// snippet-start:[rds.php.create_db_replica.main]
//Create a RDSClient
$rdsClient = new Aws\Rds\RdsClient([
    'profile' => 'default',
    'version' => '2014-10-31',
    'region' => 'us-east-2'
]);

$replicadbIdentifier = '<<{{name-for-db-replica}}>>';
$sourcedbIdentifier = '<<{{db-identifier-of-db-to-replicate}}>>';

try {
    $result = $rdsClient->createDBInstanceReadReplica([
        'DBInstanceIdentifier' => $replicadbIdentifier,
        'SourceDBInstanceIdentifier' => $sourcedbIdentifier,
    ]);
    var_dump($result);
} catch (AwsException $e) {
    // output error message if fails
    echo $e->getMessage();
    echo "\n";
} 
// snippet-end:[rds.php.create_db_replica.main]
// snippet-end:[rds.php.create_db_replica.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[CreateDBInstanceReadReplica.php demonstrates how to create read replica for an existing DB instance running MySQL, MariaDB, or PostgreSQL.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Relational Database Service]
// snippet-keyword:[createDBInstanceReadReplica]
// snippet-service:[rds]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-21]
// snippet-sourceauthor:[jschwarzwalder (AWS)]

