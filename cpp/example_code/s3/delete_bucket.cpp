/*
   Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteBucketRequest.h>

/**
 * Delete an Amazon S3 bucket.
 *
 * ++ Warning ++ This code will actually delete the bucket that you specify!
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);

    if(argc < 2) {
        std::cout << std::endl <<
            "To run this example, supply the name of a bucket to delete!" <<
            std::endl << "Ex: delete_bucket <bucket-name>" << std::endl
            << std::endl;
        exit(1);
    }

    const Aws::String bucket_name = argv[1];
    std::cout << "Deleting S3 bucket: " << bucket_name << std::endl;

    Aws::S3::S3Client s3_client;

    Aws::S3::Model::DeleteBucketRequest bucket_request;
    bucket_request.WithBucket(bucket_name);

    auto delete_bucket_outcome = s3_client.DeleteBucket(bucket_request);

    if(delete_bucket_outcome.IsSuccess()) {
        std::cout << "Done!" << std::endl;
    }
    else {
        std::cout << "DeleteBucket error: " <<
            delete_bucket_outcome.GetError().GetExceptionName() << std::endl <<
            delete_bucket_outcome.GetError().GetMessage() << std::endl;
    }

    Aws::ShutdownAPI(options);
}

