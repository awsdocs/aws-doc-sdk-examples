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

// snippet-sourcedescription:[s3-demo.cpp demonstrates how to list, create, and delete a bucket in Amazon S3.]
// snippet-service:[s3]
// snippet-keyword:[C++]
// snippet-sourcesyntax:[cpp]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[ListBuckets]
// snippet-keyword:[CreateBucket]
// snippet-keyword:[DeleteBucket]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-01-11]
// snippet-sourceauthor:[AWS]
// snippet-start:[s3.cpp.bucket_operations.list_create_delete]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Bucket.h>
#include <aws/s3/model/CreateBucketConfiguration.h>
#include <aws/s3/model/CreateBucketRequest.h>
#include <aws/s3/model/DeleteBucketRequest.h>
#include <string>

bool ListMyBuckets(Aws::S3::S3Client s3_client);
bool CreateMyBucket(Aws::S3::S3Client s3_client, Aws::String bucket_name,
  Aws::S3::Model::BucketLocationConstraint region);
bool DeleteMyBucket(Aws::S3::S3Client s3_client, Aws::String bucket_name);
void Cleanup(Aws::SDKOptions options);

int main(int argc, char** argv) {

  if (argc < 3) {
    std::cout << "Usage: ./s3-demo <the bucket name> <the AWS Region to use>" << std::endl
              << "Example: ./s3-demo my-test-bucket us-west-1" << std::endl;
    return false;
  }

  Aws::String bucket_name = argv[1];
  Aws::Client::ClientConfiguration client_configuration;
  Aws::S3::Model::BucketLocationConstraint region;

  // Set the AWS Region to use, based on the user's AWS Region input ID.
  if (strcmp(argv[2], "ap-northeast-1") == 0) {
    client_configuration.region = Aws::Region::AP_NORTHEAST_1;
    region = Aws::S3::Model::BucketLocationConstraint::ap_northeast_1;
  } else if (strcmp(argv[2], "ap-northeast-2") == 0) {
    client_configuration.region = Aws::Region::AP_NORTHEAST_2;
    region = Aws::S3::Model::BucketLocationConstraint::ap_northeast_2;
  } else if (strcmp(argv[2], "ap-south-1") == 0) {
    client_configuration.region = Aws::Region::AP_SOUTH_1;
    region = Aws::S3::Model::BucketLocationConstraint::ap_south_1;
  } else if (strcmp(argv[2], "ap-southeast-1") == 0) {
    client_configuration.region = Aws::Region::AP_SOUTHEAST_1;
    region = Aws::S3::Model::BucketLocationConstraint::ap_southeast_1;
  } else if (strcmp(argv[2], "ap-southeast-2") == 0) {
    client_configuration.region = Aws::Region::AP_SOUTHEAST_2;
    region = Aws::S3::Model::BucketLocationConstraint::ap_southeast_2;
  } else if (strcmp(argv[2], "cn-north-1") == 0) {
    client_configuration.region = Aws::Region::CN_NORTH_1;
    region = Aws::S3::Model::BucketLocationConstraint::cn_north_1;
  } else if (strcmp(argv[2], "eu-central-1") == 0) {
    client_configuration.region = Aws::Region::EU_CENTRAL_1;
    region = Aws::S3::Model::BucketLocationConstraint::eu_central_1;
  } else if (strcmp(argv[2], "eu-west-1") == 0) {
    client_configuration.region = Aws::Region::EU_WEST_1;
    region = Aws::S3::Model::BucketLocationConstraint::eu_west_1;
  } else if (strcmp(argv[2], "sa-east-1") == 0) {
    client_configuration.region = Aws::Region::SA_EAST_1;
    region = Aws::S3::Model::BucketLocationConstraint::sa_east_1;
  } else if (strcmp(argv[2], "us-west-1") == 0) {
    client_configuration.region = Aws::Region::US_WEST_1;
    region = Aws::S3::Model::BucketLocationConstraint::us_west_1;
  } else if (strcmp(argv[2], "us-west-2") == 0) {
    client_configuration.region = Aws::Region::US_WEST_2;
    region = Aws::S3::Model::BucketLocationConstraint::us_west_2;
  } else {
    std::cout << "Unrecognized AWS Region ID '" << argv[2] << "'" << std::endl;
    return false;
  }

  Aws::SDKOptions options;

  Aws::InitAPI(options);
  {
    Aws::S3::S3Client s3_client(client_configuration);

    if (!ListMyBuckets(s3_client)) {
      Cleanup(options);
    }

    if (!CreateMyBucket(s3_client, bucket_name, region)) {
      Cleanup(options);
    }

    if (!ListMyBuckets(s3_client)) {
      Cleanup(options);
    }

    if (!DeleteMyBucket(s3_client, bucket_name)) {
      Cleanup(options);
    }

    if (!ListMyBuckets(s3_client)) {
      Cleanup(options);
    }
  }
  Cleanup(options);
}

// List all of your available buckets.
bool ListMyBuckets(Aws::S3::S3Client s3_client) {
  auto outcome = s3_client.ListBuckets();

  if (outcome.IsSuccess()) {
    std::cout << "My buckets now are:" << std::endl << std::endl;

    Aws::Vector<Aws::S3::Model::Bucket> bucket_list =
      outcome.GetResult().GetBuckets();

    for (auto const &bucket: bucket_list) {
      std::cout << bucket.GetName() << std::endl;
    }

    std::cout << std::endl;
    return true;
  } else {
    std::cout << "ListBuckets error: "
              << outcome.GetError().GetExceptionName() << std::endl
              << outcome.GetError().GetMessage() << std::endl;

    return false;
  }
}

// Create a bucket in this AWS Region.
bool CreateMyBucket(Aws::S3::S3Client s3_client, Aws::String bucket_name,
    Aws::S3::Model::BucketLocationConstraint region) {
  std::cout << "Creating a new bucket named '"
            << bucket_name
            << "'..." << std::endl << std::endl;

  Aws::S3::Model::CreateBucketConfiguration bucket_configuration;
  bucket_configuration.WithLocationConstraint(region);

  Aws::S3::Model::CreateBucketRequest bucket_request;
  bucket_request.WithBucket(bucket_name).WithCreateBucketConfiguration(bucket_configuration);

  auto outcome = s3_client.CreateBucket(bucket_request);

  if (outcome.IsSuccess()) {
    return true;
  } else {
    std::cout << "CreateBucket error: "
              << outcome.GetError().GetExceptionName() << std::endl
              << outcome.GetError().GetMessage() << std::endl;

    return false;
  }
}

// Delete the bucket you just created.
bool DeleteMyBucket(Aws::S3::S3Client s3_client, Aws::String bucket_name) {
  std::cout << "Deleting the bucket named '"
            << bucket_name
            << "'..." << std::endl << std::endl;

  Aws::S3::Model::DeleteBucketRequest bucket_request;
  bucket_request.WithBucket(bucket_name);

  auto outcome = s3_client.DeleteBucket(bucket_request);

  if (outcome.IsSuccess()) {
    return true;
  } else {
    std::cout << "DeleteBucket error: "
              << outcome.GetError().GetExceptionName() << std::endl
              << outcome.GetError().GetMessage() << std::endl;

    return false;
  }
}

void Cleanup(Aws::SDKOptions options) {
  Aws::ShutdownAPI(options);
}
// snippet-end:[s3.cpp.bucket_operations.list_create_delete]