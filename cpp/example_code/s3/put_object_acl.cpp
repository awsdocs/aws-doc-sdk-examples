/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/Permission.h>
#include <aws/s3/model/Type.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Owner.h>
#include <aws/s3/model/Grantee.h>
#include <aws/s3/model/Grant.h>
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/PutObjectAclRequest.h>
#include <awsdoc/s3/s3_examples.h>

/*
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to set the access control list (ACL) for an object
 * in an S3 bucket.
 *
 */

static Aws::S3::Model::Permission SetGranteePermission(const Aws::String &access);

static Aws::S3::Model::Type SetGranteeType(const Aws::String &type);

//! Routine which demonstrates setting the ACL for an object in an S3 bucket.
/*!
  \sa PutObjectAcl()
  \param bucketName Name of from bucket.
  \param objectKey Name of object in the bucket.
  \param ownerID The canonical ID of the bucket owner.
   For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html.
  \param granteePermission The access level to enable for the grantee.
  \param granteeType The type of grantee.
  \param granteeID The canonical ID of the grantee.
  \param clientConfig Aws client configuration.
  \param granteeDisplayName The display name of the grantee.
  \param granteeEmailAddress The email address associated with the grantee's AWS account.
  \param granteeURI The URI of a built-in access group.
*/

// snippet-start:[s3.cpp.put_object_acl.code]
bool AwsDoc::S3::PutObjectAcl(const Aws::String &bucketName,
                              const Aws::String &objectKey,
                              const Aws::String &ownerID,
                              const Aws::String &granteePermission,
                              const Aws::String &granteeType,
                              const Aws::String &granteeID,
                              const Aws::Client::ClientConfiguration &clientConfig,
                              const Aws::String &granteeDisplayName,
                              const Aws::String &granteeEmailAddress,
                              const Aws::String &granteeURI) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::Owner owner;
    owner.SetID(ownerID);

    Aws::S3::Model::Grantee grantee;
    grantee.SetType(SetGranteeType(granteeType));

    if (!granteeEmailAddress.empty()) {
        grantee.SetEmailAddress(granteeEmailAddress);
    }

    if (!granteeID.empty()) {
        grantee.SetID(granteeID);
    }

    if (!granteeDisplayName.empty()) {
        grantee.SetDisplayName(granteeDisplayName);
    }

    if (!granteeURI.empty()) {
        grantee.SetURI(granteeURI);
    }

    Aws::S3::Model::Grant grant;
    grant.SetGrantee(grantee);
    grant.SetPermission(SetGranteePermission(granteePermission));

    Aws::Vector<Aws::S3::Model::Grant> grants;
    grants.push_back(grant);

    Aws::S3::Model::AccessControlPolicy acp;
    acp.SetOwner(owner);
    acp.SetGrants(grants);

    Aws::S3::Model::PutObjectAclRequest request;
    request.SetAccessControlPolicy(acp);
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::PutObjectAclOutcome outcome =
            s3_client.PutObjectAcl(request);

    if (!outcome.IsSuccess()) {
        auto error = outcome.GetError();
        std::cerr << "Error: PutObjectAcl: " << error.GetExceptionName()
                  << " - " << error.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully added an ACL to the object '" << objectKey
                  << "' in the bucket '" << bucketName << "'." << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which converts a human-readable string to a built-in type enumeration.
/*!
 \sa SetGranteePermission()
 \param access Human readable string.
*/

Aws::S3::Model::Permission SetGranteePermission(const Aws::String &access) {
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
 \sa SetGranteeType()
 \param type Human readable string.
*/

Aws::S3::Model::Type SetGranteeType(const Aws::String &type) {
    if (type == "Amazon customer by email")
        return Aws::S3::Model::Type::AmazonCustomerByEmail;
    if (type == "Canonical user")
        return Aws::S3::Model::Type::CanonicalUser;
    if (type == "Group")
        return Aws::S3::Model::Type::Group;
    return Aws::S3::Model::Type::NOT_SET;
}

// snippet-end:[s3.cpp.put_object_acl.code]

/*
 *
 * main function
 *
 * Prerequisites: An existing S3 bucket that contains the object for which to get
 * ACL information.
 *
 * TODO(user): items: Set the following variables:
 * - bucketName: The name of the bucket. For example,
 *   "DOC-EXAMPLE-BUCKET".
 * - objectKey: The name of the object to get ACL information
 *   about. For example, "my-file.txt".
 * - owner_id: Canonical ID for owner.
 * - Select which form of grantee you want to specify, and update the corresponding data.
 * - Uncomment additional parameters if necessary.
*
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(user): Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "<Enter a bucket name>";
        //TODO(user): Name of object in bucket.
        const Aws::String object_name = "<Enter object name>";


        //TODO(user): Set owner_id to your canonical id. It is your bucket so you are the ACL owner.
        //For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html.
        const Aws::String owner_id =
                "<Enter owner canonical id>";

        // Set the ACL's grantee information.
        const Aws::String grantee_permission = "READ"; //Give the grantee Read permissions.

        //TODO(User): Select which form of grantee (grantee_type) you want. Then, you must update the data
        // that corresponds to the selected type.

        // If the grantee is by canonical user, then you must specify either the user's ID or 
        // grantee_display_name:
        const Aws::String grantee_type = "Canonical user";
        const Aws::String grantee_id =
                "<Enter owner canonical id>";
        // const Aws::String grantee_display_name = "janedoe";

        // If the grantee is by Amazon customer by email, then you must specify the email 
        // address:
        // const Aws::String grantee_type = "Amazon customer by email";
        // const Aws::String grantee_email_address = "janedoe@example.com";

        // If the grantee is by group, then you must specify the predefined group URI:
        //const Aws::String grantee_type = "Group";
        // This example uses one of the Amazon S3 predefined groups: Authenticated Users group
        // (Access permission to this group allows any AWS account to access the resource).
        // For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl-overview.html#specifying-grantee.
        // const Aws::String grantee_uri = 
        //     "http://acs.amazonaws.com/groups/global/AllUsers";

        // Set the object's ACL.
        //TODO(User): If you previously chose to use a grantee type other than canonical user,
        // update this method to uncomment the additional parameters. This supplies the
        // information necessary for the grantee type you selected (such as name and email address).

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::PutObjectAcl(bucket_name,
                                 object_name,
                                 owner_id,
                                 grantee_permission,
                                 grantee_type,
                                 grantee_id,
                                 clientConfig);
        // grantee_display_name,
        // grantee_email_address,
        // grantee_uri);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif  //  TESTING_BUILD
