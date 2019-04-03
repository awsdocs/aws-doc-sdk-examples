//snippet-sourcedescription:[get_put_object_acl.cpp demonstrates how to retrieve and modify the access control list of an Amazon S3 object.]
//snippet-service:[s3]
//snippet-keyword:[Amazon S3]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[snippet]
//snippet-sourceauthor:[AWS]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

//snippet-start:[s3.cpp.get_put_object_acl.inc]
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/PutObjectAclRequest.h>
#include <aws/s3/model/Grant.h>
#include <aws/s3/model/Grantee.h>
#include <aws/s3/model/Permission.h>
//snippet-end:[s3.cpp.get_put_object_acl.inc]

Aws::S3::Model::Permission GetPermission(Aws::String access)
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

void SetAclForObject(Aws::String bucket_name,
    Aws::String object_name,
    Aws::String grantee_id,
    Aws::String permission)
{
    // snippet-start:[s3.cpp.get_object_acl.code]
    // Set up the get request
    Aws::S3::S3Client s3_client;
    Aws::S3::Model::GetObjectAclRequest get_request;
    get_request.SetBucket(bucket_name);
    get_request.SetKey(object_name);

    // Get the current access control policy
    auto get_outcome = s3_client.GetObjectAcl(get_request);
    if (!get_outcome.IsSuccess())
    {
        auto error = get_outcome.GetError();
        std::cout << "Original GetObjectAcl error: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;
        return;
    }
    // snippet-end:[s3.cpp.get_object_acl.code]

    // snippet-start:[s3.cpp.put_object_acl.code]
    // Reference the retrieved access control policy
    auto result = get_outcome.GetResult();

    // Copy the result to an access control policy object (cannot type cast)
    Aws::S3::Model::AccessControlPolicy acp;
    acp.SetOwner(result.GetOwner());
    acp.SetGrants(result.GetGrants());

    // Define and add new grant
    Aws::S3::Model::Grant new_grant;
    Aws::S3::Model::Grantee new_grantee;
    new_grantee.SetID(grantee_id);
    new_grantee.SetType(Aws::S3::Model::Type::CanonicalUser);
    new_grant.SetGrantee(new_grantee);
    new_grant.SetPermission(GetPermission(permission));
    acp.AddGrants(new_grant);

    // Set up the put request
    Aws::S3::Model::PutObjectAclRequest put_request;
    put_request.SetAccessControlPolicy(acp);
    put_request.SetBucket(bucket_name);
    put_request.SetKey(object_name);

    // Set the new access control policy
    auto set_outcome = s3_client.PutObjectAcl(put_request);
    // snippet-end:[s3.cpp.put_object_acl.code]
    if (!set_outcome.IsSuccess())
    {
        auto error = set_outcome.GetError();
        std::cout << "PutObjectAcl error: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;
        return;
    }

    // Verify the operation by retrieving the updated ACP
    get_outcome = s3_client.GetObjectAcl(get_request);
    if (!get_outcome.IsSuccess())
    {
        auto error = get_outcome.GetError();
        std::cout << "Updated GetObjectAcl error: " << error.GetExceptionName()
            << " - " << error.GetMessage() << std::endl;
        return;
    }
    result = get_outcome.GetResult();

    // Output some settings of the updated ACP
    std::cout << "Updated Object ACL:\n";
    auto grants = result.GetGrants();
    for (auto & grant : grants) {
        auto grantee = grant.GetGrantee();
        std::cout << "  Grantee Display Name: "
            << grantee.GetDisplayName() << std::endl;

        std::cout << "  Permission: ";
        auto perm = grant.GetPermission();
        switch (perm)
        {
        case Aws::S3::Model::Permission::NOT_SET:
            std::cout << "NOT_SET\n";
            break;
        case Aws::S3::Model::Permission::FULL_CONTROL:
            std::cout << "FULL_CONTROL\n";
            break;
        case Aws::S3::Model::Permission::WRITE:
            std::cout << "WRITE\n";
            break;
        case Aws::S3::Model::Permission::WRITE_ACP:
            std::cout << "WRITE_ACP\n";
            break;
        case Aws::S3::Model::Permission::READ:
            std::cout << "READ\n";
            break;
        case Aws::S3::Model::Permission::READ_ACP:
            std::cout << "READ_ACP\n";
            break;
        default:
            std::cout << "UNKNOWN VALUE\n";
            break;
        }
    }
}

/**
 * Exercise SetAclForObject()
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before compiling the program
        const Aws::String bucket_name = "BUCKET_NAME";
        const Aws::String object_name = "OBJECT_NAME";
        const Aws::String grantee_id = "AWS_USER_CANONICAL_ID";
        const Aws::String permission = "READ";

        // Set the access control list for an object
        SetAclForObject(bucket_name, object_name, grantee_id, permission);
    }
    Aws::ShutdownAPI(options);
}
