// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

// snippet-start:[s3.cpp.delete_object.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <awsdoc/s3/s3_examples.h>
// snippet-end:[s3.cpp.delete_object.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Purpose: Deletes an object from a bucket in Amazon S3.
 *
 * Prerequisites: The bucket containing the object to be deleted.
 *
 * Inputs:
 * - objectKey: The name of the object to delete.
 * - fromBucket: The name of the bucket to delete the object from.
 *
 * Outputs: true if the object was deleted; otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.delete_object.code]
bool AwsDoc::S3::DeleteObject(const Aws::String& objectKey, 
    const Aws::String& fromBucket,const Aws::String& region)
{
    Aws::S3::S3Client s3_client;
    Aws::S3::Model::DeleteObjectRequest request;

    request.WithKey(objectKey)
        .WithBucket(fromBucket);

    Aws::S3::Model::DeleteObjectOutcome outcome = 
        s3_client.DeleteObject(request);

    if (!outcome.IsSuccess())
    {
        auto err = outcome.GetError();
        std::cout << "Error: DeleteObject: " <<
            err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }
    else
    {
        return true;
    }
}

int main()
{
    Aws::String object_key = "my-key";
    Aws::String from_bucket = "my-bucket";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (AwsDoc::S3::DeleteObject(object_key, from_bucket))
        {
            std::cout << "Deleted object " << object_key <<
                " from " << from_bucket << "." << std::endl;
        }
    }
    ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.delete_object.code]