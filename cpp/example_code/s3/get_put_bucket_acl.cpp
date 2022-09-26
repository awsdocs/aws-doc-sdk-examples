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
#include <aws/s3/model/PutBucketAclRequest.h>
#include <aws/s3/model/GetBucketAclRequest.h>
#include <awsdoc/s3/s3_examples.h>

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/AmazonS3/latest/userguide/GetStartedWithS3.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to get and put the access control list (ACL) for an S3 bucket.
 *
 */

static bool PutBucketAcl(const Aws::String &bucketName,
                         const Aws::String &ownerID,
                         const Aws::String &granteePermission,
                         const Aws::String &granteeType,
                         const Aws::String &granteeID,
                         const Aws::Client::ClientConfiguration &clientConfig,
                         const Aws::String &granteeDisplayName = "",
                         const Aws::String &granteeEmailAddress = "",
                         const Aws::String &granteeURI = "");

static bool GetBucketAcl(const Aws::String &bucketName,
                         const Aws::Client::ClientConfiguration &clientConfig);

static Aws::String GetGranteeTypeString(const Aws::S3::Model::Type &type);

static Aws::String GetPermissionString(const Aws::S3::Model::Permission &permission);

Aws::S3::Model::Permission SetGranteePermission(const Aws::String &access);

Aws::S3::Model::Type SetGranteeType(const Aws::String &type);


// snippet-start:[s3.cpp.get_put_bucket_acl.code]

//! Routine which demonstrates setting the ACL for an S3 bucket.
/*!
  \sa GetPutBucketAcl()
  \param bucketName Name of a bucket.
  \param ownerID The canonical ID of the bucket owner.
   See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
  \param granteePermission The access level to enable for the grantee.
  \param granteeType The type of grantee.
  \param granteeID The canonical ID of the grantee.
  \param clientConfig Aws client configuration.
  \param granteeDisplayName The display name of the grantee.
  \param granteeEmailAddress The email address associated with the grantee's AWS account.
  \param granteeURI The URI of a built-in access group.
*/

bool AwsDoc::S3::GetPutBucketAcl(const Aws::String &bucketName,
                                 const Aws::String &ownerID,
                                 const Aws::String &granteePermission,
                                 const Aws::String &granteeType,
                                 const Aws::String &granteeID,
                                 const Aws::Client::ClientConfiguration &clientConfig,
                                 const Aws::String &granteeDisplayName,
                                 const Aws::String &granteeEmailAddress,
                                 const Aws::String &granteeURI) {
    bool result = ::PutBucketAcl(bucketName, ownerID, granteePermission, granteeType,
                                 granteeID, clientConfig, granteeDisplayName,
                                 granteeEmailAddress,
                                 granteeURI);
    if (result) {
        result = ::GetBucketAcl(bucketName, clientConfig);
    }

    return result;
}

//! Routine which demonstrates setting the ACL for an S3 bucket.
/*!
  \sa PutBucketAcl()
  \param bucketName Name of from bucket.
  \param ownerID The canonical ID of the bucket owner.
   See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
  \param granteePermission The access level to enable for the grantee.
  \param granteeType The type of grantee.
  \param granteeID The canonical ID of the grantee.
  \param clientConfig Aws client configuration.
  \param granteeDisplayName The display name of the grantee.
  \param granteeEmailAddress The email address associated with the grantee's AWS account.
  \param granteeURI The URI of a built-in access group.
*/

bool PutBucketAcl(const Aws::String &bucketName,
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

    Aws::S3::Model::PutBucketAclRequest request;
    request.SetAccessControlPolicy(acp);
    request.SetBucket(bucketName);

    Aws::S3::Model::PutBucketAclOutcome outcome =
            s3_client.PutBucketAcl(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &error = outcome.GetError();

        std::cerr << "Error: PutBucketAcl: " << error.GetExceptionName()
                  << " - " << error.GetMessage() << std::endl;
    }
    else {
        std::cout << "Successfully added an ACL to the bucket '" << bucketName
                  << "'." << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine which demonstrates getting the ACL for an S3 bucket.
/*!
  \sa GetBucketAcl()
  \param bucketName Name of the s3 bucket.
  \param clientConfig Aws client configuration.
*/

bool GetBucketAcl(const Aws::String &bucketName,
                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::S3::S3Client s3_client(clientConfig);

    Aws::S3::Model::GetBucketAclRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketAclOutcome outcome =
            s3_client.GetBucketAcl(request);

    if (!outcome.IsSuccess()) {
        const Aws::S3::S3Error &err = outcome.GetError();
        std::cerr << "Error: GetBucketAcl: "
                  << err.GetExceptionName() << ": " << err.GetMessage() << std::endl;
    }
    else {
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
                      GetPermissionString(grant.GetPermission()) <<
                      std::endl << std::endl;
        }
    }

    return outcome.IsSuccess();
}

//! Routine which converts a built-in type enumeration to a human-readable string.
/*!
 \sa GetPermissionString()
 \param permission Permission enumeration.
*/

Aws::String GetPermissionString(const Aws::S3::Model::Permission &permission) {
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

    return "Permission unknown";
}

//! Routine which converts a human-readable string to a built-in type enumeration
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

//! Routine which converts a built-in type enumeration to a human-readable string.
/*!
 \sa GetGranteeTypeString()
 \param type Type enumeration.
*/

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

Aws::S3::Model::Type SetGranteeType(const Aws::String &type) {
    if (type == "Amazon customer by email")
        return Aws::S3::Model::Type::AmazonCustomerByEmail;
    if (type == "Canonical user")
        return Aws::S3::Model::Type::CanonicalUser;
    if (type == "Group")
        return Aws::S3::Model::Type::Group;
    return Aws::S3::Model::Type::NOT_SET;
}
// snippet-end:[s3.cpp.get_put_bucket_acl.code]

/*
 *
 *  main function
 *
 * Prerequisites: Create one S3 bucket.
 *
 * TODO(user): items: Set the following variables
 * - bucketName: Change bucketName to the name of a bucket in your account.
 * - ownerId: Set the ACL's owner information (if it is your bucket, use your canonical id).
 * - toBucket: The name of the bucket to copy the object to.
 * - Select which form of grantee you want to specify, and update the corresponding data.
 * - Uncomment additional parameters if necessary.
 *
 */

#ifndef TESTING_BUILD

int main() {
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO(user): Change bucketName to the name of a bucket in your account.
        //If the bucket is not in your account, you will get one of two errors:
        //AccessDenied if the bucket exists in some other account, or NoSuchBucket
        //if the bucket does not exist in any account.
        const Aws::String bucketName = "<Enter bucket name>";

        //TODO(user): Set the ACL's owner information (if it is your bucket, use your canonical id).
        //For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html.
        const Aws::String ownerId =
                "<Enter owner canonical id>";

        // Set the ACL's grantee information.
        const Aws::String grantee_permission = "READ"; //Give the grantee Read permissions.

        //TODO(user): Select which form of grantee to specify, and update the
        // corresponding data. If the grantee is by canonical user, then you must
        // specify either the user's ID or the display name.
        const Aws::String grantee_type = "Canonical user";
        const Aws::String grantee_id =
                "<Enter owner canonical id>";
        // const Aws::String grantee_display_name = "janedoe";

        // If the grantee is by Amazon customer by email, then the email
        // address must be specified:
        // const Aws::String grantee_type = "Amazon customer by email";
        // const Aws::String grantee_email_address = "janedoe@example.com";

        // If the grantee is by group, then the predefined group URI must
        // be specified:
        // const Aws::String grantee_type = "Group";
        // const Aws::String grantee_uri =
        //     "http://acs.amazonaws.com/groups/global/AuthenticatedUsers";

        // Set the bucket's ACL.
        //TODO(User): If you previously chose to use a grantee type other than
        // canonical user, update this method to uncomment the additional parameters.
        // This supplies the information necessary for the grantee type that you
        // selected (such as name and email address).

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created
        // (overrides config file).
        // clientConfig.region = "us-east-1";
        AwsDoc::S3::GetPutBucketAcl(bucketName,
                                    ownerId,
                                    grantee_permission,
                                    grantee_type,
                                    grantee_id,
                                    clientConfig);
        // grantee_display_name,
        // grantee_email_address,
        // grantee_uri));
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
