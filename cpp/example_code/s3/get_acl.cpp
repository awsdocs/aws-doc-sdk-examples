//snippet-sourcedescription:[get_acl.cpp demonstrates how to get information about an access control list (ACL) for an Amazon Simple Storage Service (Amazon S3) bucket.]
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

// snippet-start:[s3.cpp.get_acl.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetBucketAclRequest.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/Grant.h>
#include <aws/s3/model/Permission.h>
#include "awsdoc/s3/s3_examples.h"
// snippet-end:[s3.cpp.get_acl.inc]

/* 
 *
 * Prerequisites: The bucket to get the ACL information about.
 *
 * Inputs:
 * - bucketName: The name of the bucket to get the ACL information about.
 * - region: The AWS Region for the bucket.
 *
 * To run this C++ code example, ensure that you have setup your development environment, including your credentials.
 *  For information, see this documentation topic:
 *  https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 * 
 */

Aws::String GetGranteeTypeString(const Aws::S3::Model::Type &type);


int main() {
    //TODO: Name of your bucket that already contains "my-file.txt".  
    //See create_bucket.cpp and put_object.cpp to create a bucket and load an object into that bucket.
    Aws::String bucket_name = "<Enter bucket name>";
    //TODO: Name of object already in bucket.
    Aws::String object_name = "<Enter object name>";
    //TODO: Set to the AWS Region in which the bucket was created.
    Aws::String region = "us-east-1";

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        if (!AwsDoc::S3::GetBucketAcl(bucket_name, region)) {
            return 1;
        }

        if (!AwsDoc::S3::GetObjectAcl(bucket_name, object_name, region)) {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

}


Aws::String GetGranteeTypeString(const Aws::S3::Model::Type &type) {
    switch (type) {
        case Aws::S3::Model::Type::AmazonCustomerByEmail:
            return "Email address of an AWS account";
        case Aws::S3::Model::Type::CanonicalUser:
            return "Canonical user ID of an AWS account";
        case Aws::S3::Model::Type::Group:
            return "Predefined Amazon S3 group";
        case Aws::S3::Model::Type::NOT_SET:
            return "Not set";
        default:
            return "Type unknown";
    }
}

Aws::String GetPermissionString(const Aws::String &type,
                                const Aws::S3::Model::Permission &permission) {
    if (type == "bucket") {
        switch (permission) {
            case Aws::S3::Model::Permission::FULL_CONTROL:
                return "Can list objects in this bucket, create/overwrite/delete "
                       "objects in this bucket, and read/write this "
                       "bucket's permissions";
            case Aws::S3::Model::Permission::NOT_SET:
                return "Permission not set";
            case Aws::S3::Model::Permission::READ:
                return "Can list objects in this bucket";
            case Aws::S3::Model::Permission::READ_ACP:
                return "Can read this bucket's permissions";
            case Aws::S3::Model::Permission::WRITE:
                return "Can create, overwrite, and delete objects in this bucket";
            case Aws::S3::Model::Permission::WRITE_ACP:
                return "Can write this bucket's permissions";
            default:
                return "Permission unknown";
        }
    }

    if (type == "object") {
        switch (permission) {
            case Aws::S3::Model::Permission::FULL_CONTROL:
                return "Can read this object's data and its metadata, "
                       "and read/write this object's permissions";
            case Aws::S3::Model::Permission::NOT_SET:
                return "Permission not set";
            case Aws::S3::Model::Permission::READ:
                return "Can read this object's data and its metadata";
            case Aws::S3::Model::Permission::READ_ACP:
                return "Can read this object's permissions";
                // case Aws::S3::Model::Permission::WRITE // Not applicable.
            case Aws::S3::Model::Permission::WRITE_ACP:
                return "Can write this object's permissions";
            default:
                return "Permission unknown";
        }
    }

    return "Permission unknown";
}

// snippet-start:[s3.cpp.get_acl_bucket.code]
bool AwsDoc::S3::GetBucketAcl(const Aws::String &bucketName, const Aws::String &region) {
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetBucketAclRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketAclOutcome outcome =
            s3_client.GetBucketAcl(request);

    if (outcome.IsSuccess()) {
        Aws::Vector<Aws::S3::Model::Grant> grants =
                outcome.GetResult().GetGrants();

        for (auto it = grants.begin(); it != grants.end(); it++) {
            Aws::S3::Model::Grant grant = *it;
            Aws::S3::Model::Grantee grantee = grant.GetGrantee();

            std::cout << "For bucket " << bucketName << ": "
                      << std::endl << std::endl;

            if (grantee.TypeHasBeenSet()) {
                std::cout << "Type:          "
                          << GetGranteeTypeString(grantee.GetType()) << std::endl;
            }

            if (grantee.DisplayNameHasBeenSet()) {
                std::cout << "Display name:  "
                          << grantee.GetDisplayName() << std::endl;
            }

            if (grantee.EmailAddressHasBeenSet()) {
                std::cout << "Email address: "
                          << grantee.GetEmailAddress() << std::endl;
            }

            if (grantee.IDHasBeenSet()) {
                std::cout << "ID:            "
                          << grantee.GetID() << std::endl;
            }

            if (grantee.URIHasBeenSet()) {
                std::cout << "URI:           "
                          << grantee.GetURI() << std::endl;
            }

            std::cout << "Permission:    " <<
                      GetPermissionString("bucket", grant.GetPermission()) <<
                      std::endl << std::endl;
        }
    }
    else {
        auto err = outcome.GetError();
        std::cout << "Error: GetBucketAcl: "
                  << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }

    return true;
}
// snippet-end:[s3.cpp.get_acl_bucket.code]

// snippet-start:[s3.cpp.get_acl_object.code]
bool AwsDoc::S3::GetObjectAcl(const Aws::String &bucketName, const Aws::String &objectKey, const Aws::String &region) {
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetObjectAclRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::GetObjectAclOutcome outcome =
            s3_client.GetObjectAcl(request);

    if (outcome.IsSuccess()) {
        Aws::Vector<Aws::S3::Model::Grant> grants =
                outcome.GetResult().GetGrants();

        for (auto it = grants.begin(); it != grants.end(); it++) {
            std::cout << "For object " << objectKey << ": "
                      << std::endl << std::endl;

            Aws::S3::Model::Grant grant = *it;
            Aws::S3::Model::Grantee grantee = grant.GetGrantee();

            if (grantee.TypeHasBeenSet()) {
                std::cout << "Type:          "
                          << GetGranteeTypeString(grantee.GetType()) << std::endl;
            }

            if (grantee.DisplayNameHasBeenSet()) {
                std::cout << "Display name:  "
                          << grantee.GetDisplayName() << std::endl;
            }

            if (grantee.EmailAddressHasBeenSet()) {
                std::cout << "Email address: "
                          << grantee.GetEmailAddress() << std::endl;
            }

            if (grantee.IDHasBeenSet()) {
                std::cout << "ID:            "
                          << grantee.GetID() << std::endl;
            }

            if (grantee.URIHasBeenSet()) {
                std::cout << "URI:           "
                          << grantee.GetURI() << std::endl;
            }

            std::cout << "Permission:    " <<
                      GetPermissionString("object", grant.GetPermission()) <<
                      std::endl << std::endl;
        }
    }
    else {
        auto err = outcome.GetError();
        std::cout << "Error: GetObjectAcl: "
                  << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;

        return false;
    }

    return true;
}
// snippet-end:[s3.cpp.get_acl_object.code]
