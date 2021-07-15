// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0 

//snippet-start:[s3.cpp.get_put_object_acl.inc]
#include <iostream>
#include <aws/core/Aws.h>
#include <aws/s3/model/Permission.h>
#include <aws/s3/model/Type.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/Owner.h>
#include <aws/s3/model/Grantee.h>
#include <aws/s3/model/Grant.h>
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/PutObjectAclRequest.h>
#include <awsdoc/s3/s3_examples.h>
//snippet-end:[s3.cpp.get_put_object_acl.inc]

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
 * Function: PutObjectAcl
 *
 * Purpose: Set the access control list (ACL) for an object in 
 * an Amazon S3 bucket.
 *
 * Prerequisites: An existing bucket that contains the object you want to set 
 * the ACL for.
 *
 * Inputs:
 * - bucketName: The name of the bucket to which the ACL is applied. For example,
 *   "DOC-EXAMPLE-BUCKET".
 * - objectKey: The name of the object in the bucket. For example, "my-file.txt".
 * - region: The AWS Region identifier for the bucket. For example, "us-east-1".
 * - ownerID: The canonical ID of the bucket owner. For example,
 *   "b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE".
 *   See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
 * - granteePermission: The access level to enable for the grantee. For example:
 *   - "FULL_CONTROL": Can read the object's data and its metadata, 
 *     and read/write the object's permissions.
 *   - "READ": Can read the object's data and its metadata.
 *   - "WRITE_ACP": Can write the object's permissions.
 *   - "READ_ACP": Can read the object's permissions.
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
 * Outputs: true if the ACL was set for the object; otherwise, false.
 * ////////////////////////////////////////////////////////////////////////////
 * Function: GetObjectAcl
 *
 * Purpose: Gets information about the access control list (ACL) for an
 * object in an Amazon S3 bucket.
 *
 * Prerequisites: An existing bucket that contains the object you want to get 
 * information about the ACL for.
 *
 * Inputs:
 * - bucketName: The name of the bucket. For example,
 *   "DOC-EXAMPLE-BUCKET".
 * - objectKey: The name of the object to get ACL information 
 *   about. For example, "my-file.txt".
 * - region: The AWS Region identifier for the bucket. For example, "us-east-1".
 *
 * Outputs: true if ACL information was retrieved for the bucket;
 * otherwise, false.
 * ///////////////////////////////////////////////////////////////////////// */

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
// snippet-start:[s3.cpp.put_object_acl.code]
bool AwsDoc::S3::PutObjectAcl(const Aws::String& bucketName,
    const Aws::String& objectKey, 
    const Aws::String& region, 
    const Aws::String& ownerID, 
    const Aws::String& granteePermission, 
    const Aws::String& granteeType, 
    const Aws::String& granteeID, 
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

    Aws::S3::Model::PutObjectAclRequest request;
    request.SetAccessControlPolicy(acp);
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::PutObjectAclOutcome outcome =
        s3_client.PutObjectAcl(request);

    if (outcome.IsSuccess())
    {
        return true;
    }
    else
    {
        auto error = outcome.GetError();
        std::cout << "Error: PutObjectAcl: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;

        return false;
    }
}
// snippet-end:[s3.cpp.put_object_acl.code]

// snippet-start:[s3.cpp.get_object_acl.code]
bool AwsDoc::S3::GetObjectAcl(const Aws::String& bucketName,
    const Aws::String& objectKey, 
    const Aws::String& region)
{
    Aws::Client::ClientConfiguration config;
    config.region = region;

    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetObjectAclRequest request;
    request.SetBucket(bucketName);
    request.SetKey(objectKey);

    Aws::S3::Model::GetObjectAclOutcome outcome =
        s3_client.GetObjectAcl(request);

    if (outcome.IsSuccess())
    {
        Aws::S3::Model::Owner owner = outcome.GetResult().GetOwner();
        Aws::Vector<Aws::S3::Model::Grant> grants =
            outcome.GetResult().GetGrants();

        std::cout << "Object ACL information for object '" << objectKey <<
            "' in bucket '" << bucketName << "':" << std::endl << std::endl;

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
        std::cout << "Error: GetObjectAcl: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;

        return false;
    }
}
// snippet-end:[s3.cpp.get_object_acl.code]

int main()
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        //TODO: Change bucket_name to the name of a bucket in your account.
        const Aws::String bucket_name = "DOC-EXAMPLE-BUCKET";
        //TODO: Create a file called "my-file.txt" in the local folder where your executables are built to.
        const Aws::String object_name = "my-file.txt";
        //TODO: Set to the AWS Region in which the bucket was created.
        const Aws::String region = "us-east-1";


        //TODO: Set owner_id to your canonical id.  It is your bucket so you are the ACL owner. 
        //See https://docs.aws.amazon.com/AmazonS3/latest/userguide/finding-canonical-user-id.html for more information.
        //You can also find it by running the executable run_get_acl.exe of this project. 

        const Aws::String owner_id = 
            "b380d412791d395dbcdc1fb1728b32a7cd07edae6467220ac4b7c0769EXAMPLE";

        // Set the ACL's grantee information.
        const Aws::String grantee_permission = "READ"; //Give the grantee Read permissions.

        //TODO: Select which form of grantee (grantee_type) you want, then you must update the data
        // corresponding to that selected type.
        // If the grantee is by canonical user, then you must specify either the user's ID or 
        // grantee_display_name:
        const Aws::String grantee_type = "Canonical user";
        const Aws::String grantee_id = 
            "51ffd418eb142601651cc9d54984604a32b51a23153b4898fd2224772EXAMPLE";
        // const Aws::String grantee_display_name = "janedoe";

        // If the grantee is by Amazon customer by email, then you must specify the email 
        // address:
        // const Aws::String grantee_type = "Amazon customer by email";
        // const Aws::String grantee_email_address = "janedoe@example.com";

        // If the grantee is by group, then you must specify the predefined group URI:
        //const Aws::String grantee_type = "Group";
        //// This example uses one of Amazon S3 predefined groups: Authenticated Users group (Access permission to this group allows any AWS account to access the resource. )
        //// See https://docs.aws.amazon.com/AmazonS3/latest/userguide/acl-overview.html#specifying-grantee for more information.
        // const Aws::String grantee_uri = 
        //     "http://acs.amazonaws.com/groups/global/AllUsers";

        // Set the object's ACL.
        //TODO: If you elected to use a grantee type other than canonical user above, update this method to 
        //uncomment the additional parameters so that you are supplying the information necessary for the 
        //grantee type you selected (e.g. the name, email address, etc).
        if (!AwsDoc::S3::PutObjectAcl(bucket_name,
            object_name, 
            region,
            owner_id, 
            grantee_permission, 
            grantee_type,
            grantee_id))
            // grantee_display_name, 
            // grantee_email_address, 
            // grantee_uri))
        {
            return 1;
        }

        // Get the object's ACL information that was just set.
        if (!AwsDoc::S3::GetObjectAcl(bucket_name, object_name, region))
        {
            return 1;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}
