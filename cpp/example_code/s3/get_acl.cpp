 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


/*
   Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
#include <aws/s3/model/GetBucketAclRequest.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/Permission.h>
#include <aws/s3/model/Grant.h>

Aws::String GetPermissionString(const Aws::S3::Model::Permission p)
{
    switch (p)
    {
    case Aws::S3::Model::Permission::NOT_SET:
        return "NOT_SET";
    case Aws::S3::Model::Permission::FULL_CONTROL:
        return "FULL_CONTROL";
    case Aws::S3::Model::Permission::WRITE:
        return "WRITE";
    case Aws::S3::Model::Permission::READ:
        return "READ";
    case Aws::S3::Model::Permission::WRITE_ACP:
        return "WRITE_ACP";
    case Aws::S3::Model::Permission::READ_ACP:
        return "READ_ACP";
    default:
        return "*unknown!*";
    }
}

void GetAclForBucket(Aws::String bucket_name, Aws::String user_region)
{
    std::cout << "Retrieving ACL for bucket: " << bucket_name << std::endl;

    Aws::Client::ClientConfiguration config;
    config.region = user_region;
    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetBucketAclRequest request;
    request.SetBucket(bucket_name);

    auto outcome = s3_client.GetBucketAcl(request);

    if (outcome.IsSuccess())
    {
        Aws::Vector<Aws::S3::Model::Grant> grants =
            outcome.GetResult().GetGrants();
        for (auto it = grants.begin(); it != grants.end(); it++)
        {
            Aws::S3::Model::Grant grant = *it;
            std::cout << grant.GetGrantee().GetDisplayName() << ": "
                << GetPermissionString(grant.GetPermission())
                << std::endl;
        }
    }
    else
    {
        std::cout << "GetBucketAcl error: "
            << outcome.GetError().GetExceptionName() << " - "
            << outcome.GetError().GetMessage() << std::endl;
    }
}

void GetAclForObject(Aws::String bucket_name, Aws::String object_key,
    Aws::String user_region)
{
    std::cout << "Retrieving ACL for object: " << object_key << std::endl
        << "                in bucket: " << bucket_name << std::endl;

    Aws::Client::ClientConfiguration config;
    config.region = user_region;
    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetObjectAclRequest request;
    request.SetBucket(bucket_name);
    request.SetKey(object_key);

    auto outcome = s3_client.GetObjectAcl(request);

    if (outcome.IsSuccess())
    {
        Aws::Vector<Aws::S3::Model::Grant> grants =
            outcome.GetResult().GetGrants();
        for (auto it = grants.begin(); it != grants.end(); it++)
        {
            Aws::S3::Model::Grant grant = *it;
            std::cout << grant.GetGrantee().GetDisplayName() << ": "
                << GetPermissionString(grant.GetPermission())
                << std::endl;
        }
    }
    else
    {
        std::cout << "GetObjectAcl error: "
            << outcome.GetError().GetExceptionName() << " - "
            << outcome.GetError().GetMessage() << std::endl;
    }
}

/**
 * Get an Amazon S3 bucket website configuration.
 */
int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << "get_acl - get the access control list (ACL) for" << std::endl
            << "          an S3 bucket (or object)" << std::endl
            << "\nUsage:" << std::endl
            << "  get_acl <bucket> [object] [region]" << std::endl
            << "\nWhere:" << std::endl
            << "  bucket - the bucket name" << std::endl
            << "  object - the object name" << std::endl
            << "           (optional, if specified, the ACL will be retrieved" << std::endl
            << "            for the named object instead of the bucket)" << std::endl
            << "  region - AWS region for the bucket" << std::endl
            << "           (optional, default: us-east-1)" << std::endl
            << "\nExample:" << std::endl
            << "  get_acl testbucket" << std::endl << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        int cur_arg = 1;
        const Aws::String bucket_name = argv[cur_arg++];
        const Aws::String object_key = (argc >= 4) ? argv[cur_arg++] : "";
        const Aws::String user_region = (argc > cur_arg) ? argv[cur_arg] : "us-east-1";

        if (object_key == "")
        {
            GetAclForBucket(bucket_name, user_region);
        }
        else
        {
            GetAclForObject(bucket_name, object_key, user_region);
        }
    }
    Aws::ShutdownAPI(options);
}

