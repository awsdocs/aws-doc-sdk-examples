 
//snippet-sourcedescription:[set_acl.cpp demonstrates how to set the access control list permissions of an Amazon S3 bucket or bucket object.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon S3]
//snippet-service:[s3]
//snippet-sourcetype:[full-example]
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
#include <aws/s3/model/AccessControlPolicy.h>
#include <aws/s3/model/GetBucketAclRequest.h>
#include <aws/s3/model/PutBucketAclRequest.h>
#include <aws/s3/model/GetObjectAclRequest.h>
#include <aws/s3/model/PutObjectAclRequest.h>
#include <aws/s3/model/Grantee.h>
#include <aws/s3/model/Permission.h>

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

void SetAclForBucket(Aws::String bucket_name, Aws::String email,
    Aws::String access, Aws::String user_region)
{
    std::cout << "Setting ACL for bucket: " << bucket_name << std::endl;

    Aws::Client::ClientConfiguration config;
    config.region = user_region;
    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetBucketAclRequest get_request;
    get_request.SetBucket(bucket_name);

    auto get_outcome = s3_client.GetBucketAcl(get_request);

    if (get_outcome.IsSuccess())
    {
        Aws::S3::Model::Grantee grantee;
        grantee.SetEmailAddress(email);
        Aws::S3::Model::PutBucketAclRequest put_request;
        put_request.SetBucket(bucket_name);
        s3_client.PutBucketAcl(put_request);
    }
    else
    {
        std::cout << "GetBucketAcl error: "
            << get_outcome.GetError().GetExceptionName() << " - "
            << get_outcome.GetError().GetMessage() << std::endl;
    }
}

void SetAclForObject(Aws::String bucket_name, Aws::String object_key,
    Aws::String email, Aws::String access, Aws::String user_region)
{
    std::cout << "Setting ACL for object: " << object_key << std::endl
        << "             in bucket: " << bucket_name << std::endl;

    Aws::Client::ClientConfiguration config;
    config.region = user_region;
    Aws::S3::S3Client s3_client(config);

    Aws::S3::Model::GetObjectAclRequest get_request;
    get_request.SetBucket(bucket_name);
    get_request.SetKey(object_key);

    auto get_outcome = s3_client.GetObjectAcl(get_request);

    if (get_outcome.IsSuccess())
    {
        Aws::S3::Model::Grantee grantee;
        grantee.SetEmailAddress(email);
        Aws::S3::Model::PutObjectAclRequest put_request;
        put_request.SetBucket(bucket_name);
        put_request.SetKey(object_key);
        s3_client.PutObjectAcl(put_request);
    }
    else
    {
        std::cout << "GetObjectAcl error: "
            << get_outcome.GetError().GetExceptionName() << " - "
            << get_outcome.GetError().GetMessage() << std::endl;
    }
}

/**
 * Get an Amazon S3 bucket website configuration.
 */
int main(int argc, char** argv)
{
    if (argc < 2)
    {
        std::cout << "set_acl - get the access control list (ACL) for" << std::endl
            << "          an S3 bucket (or object)" << std::endl
            << "\nUsage:" << std::endl
            << "  set_acl <bucket>/[object] <email> <access> [region]" << std::endl
            << "\nWhere:" << std::endl
            << "  bucket - the bucket name" << std::endl
            << "  object - the object name" << std::endl
            << "           (optional, if specified, the ACL will be retrieved" << std::endl
            << "            for the named object instead of the bucket)" << std::endl
            << "  email  - email of user to set permission for" << std::endl
            << "  access - type of access to set" << std::endl
            << "  region - AWS region for the bucket" << std::endl
            << "           (optional, default: us-east-1)" << std::endl
            << "\nExample:" << std::endl
            << "  set_acl testbucket" << std::endl << std::endl;
        exit(1);
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        int cur_arg = 1;
        const Aws::String s3_path = argv[cur_arg++];
        const Aws::String email = argv[cur_arg++];
        const Aws::String access = argv[cur_arg++];
        const Aws::String user_region = (argc > cur_arg) ? argv[cur_arg] : "us-east-1";

        size_t slash_pos = s3_path.find_first_of('/');

        Aws::String bucket_name = "";
        Aws::String object_key = "";

        if (slash_pos == std::string::npos)
        {
            bucket_name = s3_path;
        }
        else
        {
            bucket_name = s3_path.substr(0, slash_pos);
            object_key = s3_path.substr(slash_pos, std::string::npos);
        }

        if (object_key == "")
        {
            SetAclForBucket(bucket_name, email, access, user_region);
        }
        else
        {
            SetAclForObject(bucket_name, object_key, email, access, user_region);
        }
    }
    Aws::ShutdownAPI(options);
}

