// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0 

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/Permission.h>
#include <aws/s3/model/Type.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Owner.h>
#include <aws/s3/model/Grantee.h>
#include <aws/s3/model/Grant.h>
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/PutBucketAclRequest.h>
#include <aws/s3/model/GetBucketAclRequest.h>
#include "s3_examples.h"

static Aws::S3::Model::Permission setGranteePermission(const Aws::String &access);

static Aws::S3::Model::Type setGranteeType(const Aws::String &type);

/**
* Before running this C++ code example, set up your development environment, including your credentials.
*
* For more information, see the following documentation topic:
*
* https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
*
* Purpose
*
* Demonstrates using the AWS SDK for C++ to set the access control list (ACL) for an S3 bucket.
*
*/

//! Routine which demonstrates setting the ACL for an S3 bucket.
/*!
  \param bucketName Name of from bucket.
  \param ownerID The canonical ID of the bucket owner.
   For more information see https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html.
  \param granteePermission The access level to enable for the grantee.
  \param granteeType The type of grantee.
  \param granteeID The canonical ID of the grantee.
  \param granteeEmailAddress The email address associated with the grantee's AWS account.
  \param granteeURI The URI of a built-in access group.
  \param clientConfig Aws client configuration.
  \return bool: Function succeeded.
*/
// snippet-start:[s3.cpp.put_bucket_acl.code]
bool AwsDoc::S3::putBucketAcl(const Aws::String &bucketName, const Aws::String &ownerID,
                              const Aws::String &granteePermission,
                              const Aws::String &granteeType, const Aws::String &granteeID,
                              const Aws::String &granteeEmailAddress,
                              const Aws::String &granteeURI, const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::Owner owner;
    owner.SetID(ownerID);

    Aws::S3::Model::Grantee grantee;
    grantee.SetType(setGranteeType(granteeType));

    if (!granteeEmailAddress.empty()) {
        grantee.SetEmailAddress(granteeEmailAddress);
    }

    if (!granteeID.empty()) {
        grantee.SetID(granteeID);
    }

    if (!granteeURI.empty()) {
        grantee.SetURI(granteeURI);
    }

    Aws::S3::Model::Grant grant;
    grant.SetGrantee(grantee);
    grant.SetPermission(setGranteePermission(granteePermission));

    Aws::Vector<Aws::S3::Model::Grant> grants;
    grants.push_back(grant);

    Aws::S3::Model::AccessControlPolicy acp;
    acp.SetOwner(owner);
    acp.SetGrants(grants);

    Aws::S3::Model::PutBucketAclRequest request;
    request.SetAccessControlPolicy(acp);
    request.SetBucket(bucketName);

    Aws::S3::Model::PutBucketAclOutcome outcome =
            s3_client.PutBucketAcl(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &error = outcome.GetError();

        std::cerr << "Error: putBucketAcl: " << error.GetExceptionName()
                  << " - " << error.GetMessage() << std::endl;
    } else {
        std::cout << "Successfully added an ACL to the bucket '" << bucketName
                  << "'." << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which converts a human-readable string to a built-in type enumeration.
/*!
 \param access Human readable string.
*/

Aws::S3::Model::Permission setGranteePermission(const Aws::String &access) {
    if (access == "FULL_CONTROL")
        return Aws::S3::Model::Permission::FULL_CONTROL;
    if (access == "WRITE")
        return Aws::S3::Model::Permission::WRITE;
    if (access == "READ")
        return Aws::S3::Model::Permission::READ;
    if (access == "WRITE_ACP")
        return Aws::S3::Model::Permission::WRITE_ACP;
    if (access == "READ_ACP")
        return Aws::S3::Model::Permission::READ_ACP;
    return Aws::S3::Model::Permission::NOT_SET;
}

//! Routine which converts a human-readable string to a built-in type enumeration.
/*!
 \param type Human readable string.
 \return Type: Type enumeration
*/

Aws::S3::Model::Type setGranteeType(const Aws::String &type) {
    if (type == "Amazon customer by email")
        return Aws::S3::Model::Type::AmazonCustomerByEmail;
    if (type == "Canonical user")
        return Aws::S3::Model::Type::CanonicalUser;
    if (type == "Group")
        return Aws::S3::Model::Type::Group;
    return Aws::S3::Model::Type::NOT_SET;
}
// snippet-end:[s3.cpp.put_bucket_acl.code]

/*
 *
 *  main function
 *
 *  usage: 'get_put_bucket_acl.out <bucket_name> <owner_id> <grantee_permission> <grantee_type> <grantee_data>'
 *
 *  where:
 *      bucket_name - The name of the bucket to set the access control list (ACL) for.
 *      owner_id - The canonical ID of the bucket owner.
 *      grantee_permission - The permission to grant the grantee (e.g., "READ").
 *      grantee_type - The type of grantee: "Canonical user"|"Amazon customer by email"|"Group".
 *      grantee_data - Extra data dependent on grantee type.
 *              For Canonical user:  The canonical ID of the grantee.
 *              For Amazon customer by email: The email address of the grantee.
 *              For Group: The URI of the grantee group.
 *
 *  Prerequisites: Create one S3 bucket.
 *
 */

#ifndef TESTING_BUILD

static void usage() {
    std::cout << R"(
Usage:
    run_get_put_bucket_acl <bucket_name> <owner_id> <grantee_permission> <grantee_type> <grantee_data>
Where:
    bucket_name - The name of the bucket to set the access control list (ACL) for.
    owner_id - The canonical ID of the bucket owner.
    grantee_permission - The permission to grant the grantee (e.g., "READ").
    grantee_type - The type of grantee: "Canonical user"|"Amazon customer by email"|"Group".
    grantee_data - Extra data dependent on grantee type.
            For Canonical user:  The canonical ID of the grantee.
            For Amazon customer by email: The email address of the grantee.
            For Group: The URI of the grantee group.
)" << std::endl;
}


int main(int argc, char* argv[]) {
    Aws::SDKOptions options;
    Aws::InitAPI(options);


    if (argc != 6) {
        usage();
        return 1;
    }

    Aws::String bucketName = argv[1];
    Aws::String ownerId = argv[2];
    Aws::String granteePermission = argv[3];
    Aws::String granteeType = argv[4];
    Aws::String granteeID;  // Used for grantee type canonical user.
    Aws::String granteeEmailAddress; //  = "topplop@gmail.com"; // Used for grantee type Amazon customer by email.
    Aws::String granteeURI; // Used for grantee type group.

   if (granteeType == "Canonical user") {
        granteeID = argv[5];
     }
    else if (granteeType == "Amazon customer by email") {
        granteeEmailAddress = argv[5];
    }
    else if (granteeType == "Group") {
        granteeURI = argv[5];
    }

    {
        Aws::S3::S3ClientConfiguration clientConfig;
        // Optional: Set to your AWS region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::putBucketAcl(bucketName,
                                 ownerId,
                                 granteePermission,
                                 granteeType,
                                 granteeID,
                                 granteeEmailAddress,
                                 granteeURI,
                                 clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
