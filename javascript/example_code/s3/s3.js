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

// snippet-sourcedescription:[s3.js demonstrates how to list, create, and delete a bucket in Amazon S3.]
// snippet-service:[s3]
// snippet-keyword:[JavaScript]
// snippet-sourcesyntax:[javascript]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[listBuckets]
// snippet-keyword:[createBucket]
// snippet-keyword:[deleteBucket]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2017-01-24]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.javascript.bucket_operations.list_create_delete]
if (process.argv.length < 4) {
    console.log('Usage: node s3.js <the bucket name> <the AWS Region to use>\n' +
      'Example: node s3.js my-test-bucket us-east-2');
    process.exit(1);
  }
  
  var AWS = require('aws-sdk'); // To set the AWS credentials and region.
  var async = require('async'); // To call AWS operations asynchronously.
  
  AWS.config.update({
    region: region
  });
  
  var s3 = new AWS.S3({apiVersion: '2006-03-01'});
  var bucket_name = process.argv[2];
  var region = process.argv[3];
  
  var create_bucket_params = {
    Bucket: bucket_name,
    CreateBucketConfiguration: {
      LocationConstraint: region
    }
  };
  
  var delete_bucket_params = {Bucket: bucket_name};
  
  // List all of your available buckets in this AWS Region.
  function listMyBuckets(callback) {
    s3.listBuckets(function(err, data) {
      if (err) {
  
      } else {
        console.log("My buckets now are:\n");
  
        for (var i = 0; i < data.Buckets.length; i++) {
          console.log(data.Buckets[i].Name);
        }
      }
  
      callback(err);
    });
  }
  
  // Create a bucket in this AWS Region.
  function createMyBucket(callback) {
    console.log('\nCreating a bucket named ' + bucket_name + '...\n');
  
    s3.createBucket(create_bucket_params, function(err, data) {
      if (err) {
        console.log(err.code + ": " + err.message);
      }
  
      callback(err);
    });
  }
  
  // Delete the bucket you just created.
  function deleteMyBucket(callback) {
    console.log('\nDeleting the bucket named ' + bucket_name + '...\n');
  
    s3.deleteBucket(delete_bucket_params, function(err, data) {
      if (err) {
        console.log(err.code + ": " + err.message);
      }
  
      callback(err);
    });
  }
  
  // Call the AWS operations in the following order.
  async.series([
    listMyBuckets,
    createMyBucket,
    listMyBuckets,
    deleteMyBucket,
    listMyBuckets
  ]);
  // snippet-end:[s3.javascript.bucket_operations.list_create_delete]