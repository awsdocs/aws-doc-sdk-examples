//snippet-sourcedescription:[upload_archive.cpp demonstrates how to upload a file to Amazon S3 Glacier.]
//snippet-service:[glacier]
//snippet-keyword:[Amazon S3 Glacier]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[snippet]
//snippet-sourcedate:[2019-04-26]
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

#include <aws/core/Aws.h>
#include <aws/core/utils/HashingUtils.h>
#include <aws/core/utils/Outcome.h>
#include <aws/glacier/GlacierClient.h>
#include <aws/glacier/model/UploadArchiveRequest.h>
#include <aws/glacier/model/UploadArchiveResult.h>
#include <fstream>
#include <iostream>

/**
 * Upload a file to an archive in an Amazon S3 Glacier vault
 */
int main(int argc, char **argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before running the program
        Aws::String vault_name("VAULT_NAME");
        Aws::String file_name("FILE_NAME");

        // Optional values to modify
        Aws::String account_id("-");    // Hyphen = Use current user's credentials
        Aws::String archive_description("TestArchiveUpload");

        // Calculate SHA-256 tree hash of file contents
        const std::shared_ptr<Aws::IOStream> file_contents = 
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag", 
                                          file_name.c_str(), 
                                          std::ios::in | std::ios::binary);
        Aws::Utils::ByteBuffer byte_checksum = 
            Aws::Utils::HashingUtils::CalculateSHA256TreeHash(*file_contents);
        Aws::String checksum = Aws::Utils::HashingUtils::HexEncode(byte_checksum);

        // Set up the request
        Aws::Glacier::Model::UploadArchiveRequest upload_request;
        upload_request.SetVaultName(vault_name);
        upload_request.SetBody(file_contents);
        upload_request.SetChecksum(checksum);
        upload_request.SetAccountId(account_id);
        upload_request.SetArchiveDescription(archive_description);

        // Upload the file
        Aws::Glacier::GlacierClient glacier_client;
        auto upload_outcome = glacier_client.UploadArchive(upload_request);

        // Process the result
        if (upload_outcome.IsSuccess())
        {
            std::cout << "Success: Archive ID: " 
                << upload_outcome.GetResult().GetArchiveId() << std::endl;
        }
        else
        {
            std::cout << "ERROR: " << upload_outcome.GetError().GetMessage() 
                << std::endl;
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
