// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

//snippet-start:[s3.cpp.get_put_bucket_acl.inc]
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
//snippet-end:[s3.cpp.get_put_bucket_acl.inc]

/* ////////////////////////////////////////////////////////////////////////////
 * Function: SetGranteePermission
 *
 * Purpose: Converts a human-readable string to a 
 * built-in permission enumeration.
 * 
 * Inputs: A human-readable string.
 *
 * Outputs: A related built-in permission enumeration, if one exists; 
 * otherwise, the enumeration Aws::S3::Model::Permission::NOT_SET.
 * ////////////////////////////////////////////////////////////////////////////
 * Function: GetGranteeType
 *
 * Purpose: Converts a built-in permission enumeration to a 
 * human-readable string.
 *
 * Inputs: A built-in permission enumeration.
 *
 * Outputs: A related human-readable string, if one exists; otherwise, 
 * the string "Not set".
 * ////////////////////////////////////////////////////////////////////////////
 * Function: SetGranteeType
 *
 * Purpose: Converts a human-readable string to a 
 * built-in type enumeration.
 *
 * Prerequisites:
 *
 * Inputs: A human-readable string.
 *
 * Outputs: A related built-in type enumeration, if one exists; otherwise, 
 * the enumeration Aws::S3::Model::Type::NOT_SET.
 * ////////////////////////////////////////////////////////////////////////////
 * Function: PutBucketAcl
 *
 * Purpose: Set the access control list (ACL) for an Amazon S3 bucket.
 *
 * Prerequisites: An existing bucket.
 *
 * Inputs:
 * - bucketName: The name of the bucket to set the ACL for. For example, 
 *   "DOC-EXAMPLE-BUCKET".
 * - region: The AWS Region identifier for the bucket. For example, "us-east-1".
 * - ownerID: The canonical ID of the bucket owner. For example, 
 *   "b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE".
 *   See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
 * - granteePermission: The access level to enable for the grantee. For example:
 *   - "FULL_CONTROL": Can list objects in the bucket, create/overwrite/delete 
 *     objects in the bucket, and read/write the bucket's permissions.
 *   - "WRITE": Can write to the bucket.
 *   - "READ": Can list objects in the bucket.
 *   - "WRITE_ACP": Can write the bucket's permissions.
 *   - "READ_ACP": Can read the bucket's permissions.
 * - granteeType: The type of grantee. For example:
 *   - "Amazon customer by email": A user identified by the email associated with 
 *     their AWS account.
 *   - "Canonical user": A user identified by their canonical ID or display name.
 *   - "Group": A built-in access group. For example, all authenticated users.
 * - granteeID: The canonical ID of the grantee. For example, 
 *   "51ffd418eb142601651cc9d54984604a32b51a23153b4898fd2224772EXAMPLE".
 * - granteeDisplayName: The display name of the grantee. For example, "janedoe".
 * - granteeEmailAddress: The email address associated with the grantee's AWS 
 *   account. For example, "janedoe@example.com".
 * - granteeURI: The URI of a built-in access group. For example, 
 *   "http://acs.amazonaws.com/groups/global/AuthenticatedUsers" 
 *   for all authenticated users.
 *
 * Outputs: true if the ACL was set for the bucket; otherwise, false.
 * ////////////////////////////////////////////////////////////////////////////
 * Function: GetBucketAcl
 *
 * Purpose: Gets information about the access control list (ACL) for an 
 * Amazon S3 bucket.
 *
 * Prerequisites: An existing bucket.
 *
 * Inputs:
 * - bucketName: The name of the bucket to get ACL information for. For example,
 *   "DOC-EXAMPLE-BUCKET".
 * - region: The AWS Region identifier for the bucket. For example, "us-east-1".
 *
 * Outputs: true if ACL information was retrieved for the bucket; 
 * otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

// snippet-start:[s3.cpp.get_put_bucket_acl.code]
Aws::S3::Model::Permission SetGranteePermission(const Aws::String& access)
{
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

Aws::String GetGranteeType(const Aws::S3::Model::Type& type)
{
    if (type == Aws::S3::Model::Type::AmazonCustomerByEmail)
        return "Amazon customer by email";
    if (type == Aws::S3::Model::Type::CanonicalUser)
        return "Canonical user";
    if (type == Aws::S3::Model::Type::Group)
        return "Group";
    return "Not set";
}

Aws::S3::Model::Type SetGranteeType(const Aws::String& type)
{
    if (type == "Amazon customer by email")
        return Aws::S3::Model::Type::AmazonCustomerByEmail;
    if (type == "Canonical user")
        return Aws::S3::Model::Type::CanonicalUser;
    if (type == "Group")
        return Aws::S3::Model::Type::Group;
    return Aws::S3::Model::Type::NOT_SET;
}

bool AwsDoc::S3::PutBucketAcl(const Aws::String& bucketName, 
    const Aws::String& ownerID, 
    const Aws::String& granteePermission, 
    const Aws::String& granteeType, 
    const Aws::String& granteeID, 
    const Aws::String& region,
    const Aws::String& granteeDisplayName,
    const Aws::String& granteeEmailAddress,
    const Aws::String& granteeURI)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::Owner owner; 
    owner.SetID(ownerID);

    Aws::S3::Model::Grantee grantee;
    grantee.SetType(SetGranteeType(granteeType));

    if (granteeEmailAddress != "")
    {
        grantee.SetEmailAddress(granteeEmailAddress);
    }

    if (granteeID != "")
    {
        grantee.SetID(granteeID);
    }

    if (granteeDisplayName != "")
    {
        grantee.SetDisplayName(granteeDisplayName);
    }

    if (granteeURI != "")
    {
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

    if (outcome.IsSuccess())
    {
        return true;
    }
    else
    {
        auto error = outcome.GetError();
        std::cout << "Error: PutBucketAcl: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;

        return false;
    }
}

bool AwsDoc::S3::GetBucketAcl(const Aws::String& bucketName,
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetBucketAclRequest request;
    request.SetBucket(bucketName);

    Aws::S3::Model::GetBucketAclOutcome outcome = 
        s3_client.GetBucketAcl(request);

    if (outcome.IsSuccess())
    {
        Aws::S3::Model::Owner owner = outcome.GetResult().GetOwner();
        Aws::Vector<Aws::S3::Model::Grant> grants = 
            outcome.GetResult().GetGrants();

        std::cout << "Bucket ACL information for bucket '" << bucketName <<
            "':" << std::endl << std::endl;

        std::cout << "Owner:" << std::endl << std::endl;
        std::cout << "Display name:  " << owner.GetDisplayName() << std::endl;
        std::cout << "ID:            " << owner.GetID() << std::endl << 
                                             std::endl;
        
        std::cout << "Grantees:" << std::endl << std::endl;

        for (auto it = std::begin(grants); it != end(grants); ++it) {
            auto grantee = it->GetGrantee();
            std::cout << "Display name:  " << grantee.GetDisplayName() << 
                                                  std::endl;
            std::cout << "Email address: " << grantee.GetEmailAddress() << 
                                                  std::endl;
            std::cout << "ID:            " << grantee.GetID() << std::endl;
            std::cout << "Type:          " << GetGranteeType(
                                                  grantee.GetType()) << 
                                                  std::endl;
            std::cout << "URI:           " << grantee.GetURI() << std::endl << 
                                                  std::endl;
        }

        return true;
    }
    else
    {
        auto error = outcome.GetError();
        std::cout << "Error: GetBucketAcl: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;

        return false;
    }
}

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Change bucket_name to the name of a bucket in your account.
        //If the bucket is not in your account, you will get one of two errors: 
        //AccessDenied if the bucket exists in some other account, or NoSuchBucket 
        //if the bucket does not exist in any account.
        const Aws::String bucket_name = "DOC-EXAMPLE-BUCKET";
        //TODO: Set to the AWS Region in which the bucket was created.
        const Aws::String region = "us-east-1";

        //TODO: Set the ACL's owner information (if it is your bucket, then you want your canonical id). 
        //See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
        const Aws::String owner_id = 
            "b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE";

        // Set the ACL's grantee information.
        const Aws::String grantee_permission = "READ"; //Give the grantee Read permissions.
        
        //TODO: Select which form of grantee you want to specify, and update the corresponding data.
        // If the grantee is by canonical user, then either the user's ID or 
        // display name must be specified:
        const Aws::String grantee_type = "Canonical user";
        const Aws::String grantee_id = 
            "51ffd418eb142601651cc9d54984604a32b51a23153b4898fd2224772EXAMPLE";
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
        //TODO: If you elected to use a grantee type other than canonical user above, update this method to 
        //uncomment the additional parameters so that you are supplying the information necessary for the 
        //grantee type you selected (e.g. the name, email address, etc).
        if (!AwsDoc::S3::PutBucketAcl(bucket_name,
           region,
            owner_id,
            grantee_permission,
            grantee_type,
            grantee_id))
            // grantee_display_name, 
            // grantee_email_address, 
            // grantee_uri));
        {
            return 1;
        }
        
        // Get the bucket's ACL information that was just set.
        if (!AwsDoc::S3::GetBucketAcl(bucket_name, region))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[s3.cpp.get_put_bucket_acl.code]
