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
*/

// snippet-sourcedescription:[s3.php demonstrates how to list, create, and delete a bucket in Amazon S3.]
// snippet-service:[s3]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[listBucketsAsync]
// snippet-keyword:[createBucketAsync]
// snippet-keyword:[deleteBucketAsync]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2017-02-01]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.php.bucket_operations.list_create_delete]
  require './vendor/autoload.php';

  if ($argc < 4) {
    exit("Usage: php s3.php <the time zone> <the bucket name> <the AWS Region to use>\n" .
      "Example: php s3.php America/Los_Angeles my-test-bucket us-east-2");
  }

  $timeZone = $argv[1];
  $bucketName = $argv[2];
  $region = $argv[3];

  date_default_timezone_set($timeZone);

  $s3 = new Aws\S3\S3Client([
    'region' => $region,
    'version' => '2006-03-01'
  ]);

  # Lists all of your available buckets in this AWS Region.
  function listMyBuckets($s3) {
    print("\nMy buckets now are:\n");

    $promise = $s3->listBucketsAsync();

    $result = $promise->wait();

    foreach ($result['Buckets'] as $bucket) {
      print("\n");
      print($bucket['Name']);
    }
  }

  listMyBuckets($s3);

  # Create a new bucket.
  print("\n\nCreating a new bucket named '$bucketName'...\n");

  try {
    $promise = $s3->createBucketAsync([
      'Bucket' => $bucketName,
      'CreateBucketConfiguration' => [
        'LocationConstraint' => $region
      ]
    ]);

    $promise->wait();

  } catch (Exception $e) {
    if ($e->getCode() == 'BucketAlreadyExists') {
      exit("\nCannot create the bucket. " .
        "A bucket with the name '$bucketName' already exists. Exiting.");
    }
  }

  listMyBuckets($s3);

  # Delete the bucket you just created.
  print("\n\nDeleting the bucket named '$bucketName'...\n");

  $promise = $s3->deleteBucketAsync([
    'Bucket' => $bucketName
  ]);

  $promise->wait();

  listMyBuckets($s3);
// snippet-end:[s3.php.bucket_operations.list_create_delete]
?>