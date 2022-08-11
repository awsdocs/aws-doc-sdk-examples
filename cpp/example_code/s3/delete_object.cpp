//snippet-sourcedescription:[delete_object.cpp demonstrates how to delete an object from an Amazon Simple Storage Service (Amazon S3) bucket.]
//snippet-keyword:[AWS SDK for C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon S3]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[12/15/2021]
//snippet-sourceauthor:[scmacdon - aws]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

// snippet-start:[s3.cpp.delete_object.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.delete_object.inc]

/* 
 * 
 * Prerequisites: The bucket containing the object to delete.
 *
 * Inputs:
 * - objectKey: The name of the object to delete.
 * - fromBucket: The name of the bucket to delete the object from.
 * - region: The AWS Region to create the bucket in.
 *
 *  To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 */

// snippet-start:[s3.cpp.delete_object.code]
bool AwsDoc::S3::DeleteObject(const Aws::String &objectKey, const Aws::String &fromBucket, const Aws::String &region) {
    Aws::Client::ClientConfiguration clientConfig;
    if (!region.empty()) {
        clientConfig.region = region;
    }

    Aws::S3::S3Client client(clientConfig);
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectKey)
            .WithBucket(fromBucket);

    Aws::S3::Model::DeleteObjectOutcome outcome =
            client.DeleteObject(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteObject: " <<
                  err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
        return false;
    }
    else
    {
        std::cout << "Successfully deleted the object." << std::endl;
        return true;
    }
}

int main()
{
    //TODO: The object_key is the unique identifier for the object in the bucket. In this example set,
    //it is the filename you added in put_object.cpp.
    Aws::String objectKey = "<Enter object key>";
    //TODO: Change from_bucket to the name of a bucket in your account.
    Aws::String fromBucket = "<Enter bucket name>";
    //TODO: Set to the AWS Region in which the bucket was created.
    Aws::String region = "us-east-1";

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    AwsDoc::S3::DeleteObject(objectKey, fromBucket, region);

    ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.delete_object.code]
