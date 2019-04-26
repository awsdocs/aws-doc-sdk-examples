//snippet-sourcedescription:[upload_archive_multipart.cpp demonstrates how to perform a multipart upload to Amazon S3 Glacier.]
//snippet-service:[glacier]
//snippet-keyword:[Amazon S3 Glacier]
//snippet-keyword:[C++]
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
#include <aws/glacier/model/AbortMultipartUploadRequest.h>
#include <aws/glacier/model/InitiateMultipartUploadRequest.h>
#include <aws/glacier/model/InitiateMultipartUploadResult.h>
#include <aws/glacier/model/UploadMultipartPartRequest.h>
#include <aws/glacier/model/UploadMultipartPartResult.h>
#include <aws/glacier/model/CompleteMultipartUploadRequest.h>
#include <aws/glacier/model/CompleteMultipartUploadResult.h>
#include <fstream>
#include <iostream>
#include <sys/stat.h>


/**
 * Determine file size
 *
 */
long get_file_size(const Aws::String& name)
{
    struct stat buffer;
    if (stat(name.c_str(), &buffer) != 0)
    {
        return 0;
    }
    return buffer.st_size;
}


/**
 * Initiate a multipart archive upload to Amazon S3 Glacier
 *
 * Returns Archive ID. If error, returns empty string.
 */
Aws::String initiate_multipart_upload(const Aws::Glacier::GlacierClient& glacier_client,
    const Aws::String& vault_name,
    const Aws::String& part_size,
    const Aws::String& account_id,
    const Aws::String& archive_description)
{
    // Set up the request
    Aws::Glacier::Model::InitiateMultipartUploadRequest init_request;
    init_request.SetVaultName(vault_name);
    init_request.SetPartSize(part_size);
    init_request.SetAccountId(account_id);
    init_request.SetArchiveDescription(archive_description);

    // Initiate the upload
    auto init_outcome = glacier_client.InitiateMultipartUpload(init_request);

    // Process the result
    if (init_outcome.IsSuccess())
    {
        // Return the upload_id
        return init_outcome.GetResult().GetUploadId();
    }
    else
    {
        std::cout << "ERROR: " << init_outcome.GetError().GetMessage() << std::endl;
        return "";
    }
}


/**
 * Upload the parts of a multipart upload
 *
 * Returns the checksum of the complete archive. If error, returns empty string.
 */
Aws::String upload_parts(const Aws::Glacier::GlacierClient& glacier_client,
    const Aws::String& vault_name,
    const Aws::String& part_size,
    const Aws::String& account_id,
    const Aws::String& upload_id,
    const Aws::String& file_name)
{

    // Convert Aws::String --> std::string --> int
    std::string part_size_std(part_size.c_str(), part_size.size());
    int part_size_int = std::stoi(part_size_std);

    // Open the file to upload
    std::ifstream file (file_name.c_str(), std::ios::in | std::ios::binary);
    if (!file.is_open())
    {
        std::cout << "ERROR: Could not open " << file_name.c_str() << std::endl;
        return "";
    }

    // Process file in chucks of size part_size_int
    Aws::List<Aws::Utils::ByteBuffer> part_checksums;
    char* buffer = new char[part_size_int];
    std::streamsize current_filepos = 0;
    while (!file.eof())
    {
        // Read the next part
        file.read(buffer, part_size_int);
        std::streamsize chars_read = file.gcount();

        // Copy buffer contents: char* --> std::string --> Aws::String
        std::string buffer_std(buffer, chars_read);
        Aws::String buffer_aws(buffer_std.c_str(), buffer_std.size());

        // Calculate SHA-256 tree hash of part, saving each byte_checksum
        Aws::Utils::ByteBuffer byte_checksum = Aws::Utils::HashingUtils::CalculateSHA256TreeHash(buffer_aws);
        Aws::String part_checksum = Aws::Utils::HashingUtils::HexEncode(byte_checksum);
        part_checksums.push_back(byte_checksum);

        // Construct range string ("bytes %s-%s/*") --> Aws::String
        std::string range = "bytes " + std::to_string(current_filepos) + "-" + 
            std::to_string(current_filepos + chars_read - 1) + "/*";
        Aws::String range_aws(range.c_str(), range.size());

        // Set stream input to the loaded part buffer
        // std::stringstream buffer_ptr(buffer_std);
        const std::shared_ptr<Aws::IOStream> buffer_ptr = 
            Aws::MakeShared<std::stringstream>("SampleAllocationTag", buffer_std);

        // Set up request
        Aws::Glacier::Model::UploadMultipartPartRequest upload_request;
        upload_request.SetUploadId(upload_id);
        upload_request.SetVaultName(vault_name);
        upload_request.SetRange(range_aws);
        upload_request.SetBody(buffer_ptr);
        upload_request.SetChecksum(part_checksum);
        upload_request.SetAccountId(account_id);

        // Upload the part
        auto upload_outcome = glacier_client.UploadMultipartPart(upload_request);

        // Process the result
        if (upload_outcome.IsSuccess())
        {
            // Update the current file position
            current_filepos += chars_read;
        }
        else {
            std::cout << "ERROR: " << upload_outcome.GetError().GetMessage() << std::endl;
            file.close();
            delete[] buffer;
            return "";
        }
    }

    // Clean up resources
    file.close();
    delete[] buffer;

    // Calculate checksum for entire file
#if 0
    // Ideally, the file checksum is calculated from the checksums of the parts
    // by calling the TreeHashFinalCompute() method. However, that method is
    // not currently exposed.
    Aws::Utils::ByteBuffer file_byte_checksum = 
        Aws::Utils::HashingUtils::TreeHashFinalCompute(part_checksums);
#else
    // Until TreeHashFinalComplete() becomes public, calculate the checksum
    // by re-reading the entire file.
    const std::shared_ptr<Aws::IOStream> file_contents =
        Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
            file_name.c_str(), std::ios::in | std::ios::binary);
    Aws::Utils::ByteBuffer file_byte_checksum = 
        Aws::Utils::HashingUtils::CalculateSHA256TreeHash(*file_contents);
#endif
    Aws::String file_checksum = Aws::Utils::HashingUtils::HexEncode(file_byte_checksum);
    return file_checksum;
}


/**
 *  Finish the multipart upload operation
 */
bool complete_upload(const Aws::Glacier::GlacierClient& glacier_client,
    const Aws::String& vault_name,
    const Aws::String& account_id,
    const Aws::String& upload_id,
    const Aws::String& checksum,
    const Aws::String& filesize,
    Aws::String& archive_id,
    Aws::String& location)
{
    // Set up the request
    Aws::Glacier::Model::CompleteMultipartUploadRequest complete_request;
    complete_request.SetUploadId(upload_id);
    complete_request.SetVaultName(vault_name);
    complete_request.SetChecksum(checksum);
    complete_request.SetArchiveSize(filesize);
    complete_request.SetAccountId(account_id);

    // Complete the upload operation
    auto complete_outcome = glacier_client.CompleteMultipartUpload(complete_request);

    // Process the result
    if (complete_outcome.IsSuccess())
    {
        auto result = complete_outcome.GetResult();
        archive_id = result.GetArchiveId();
        location = result.GetLocation();
        return true;
    }
    else {
        std::cout << "ERROR: " << complete_outcome.GetError().GetMessage() << std::endl;
        archive_id = "";
        location = "";
        return false;
    }
}


/**
 * Abort an initiated multipart upload
 */
void abort_multipart_upload(const Aws::Glacier::GlacierClient& glacier_client,
    const Aws::String& vault_name,
    const Aws::String& account_id,
    const Aws::String& upload_id)
{
    // Set up the request
    Aws::Glacier::Model::AbortMultipartUploadRequest abort_request;
    abort_request.SetUploadId(upload_id);
    abort_request.SetVaultName(vault_name);
    abort_request.SetAccountId(account_id);

    // Abort the multipart upload
    auto abort_outcome = glacier_client.AbortMultipartUpload(abort_request);

    // Process the result
    if (!abort_outcome.IsSuccess())
    {
        std::cout << "ERROR: " << abort_outcome.GetError().GetMessage() << std::endl;
    }
}


/**
 * Upload a file in multiple parts to an archive in an Amazon S3 Glacier vault
 */
int main(int argc, char** argv)
{
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        // Assign these values before running the program
        Aws::String vault_name("VAULT_NAME");
        Aws::String file_name("FILE_NAME");

        // Optional values to modify
        // Aws::String part_size = "1048576";  // 1MB
        Aws::String part_size = "4194304";  // 4MB
        Aws::String account_id("-");        // Hyphen = Use current user's credentials
        Aws::String archive_description("TestArchiveUpload");

        // Initiate multipart upload
        Aws::Glacier::GlacierClient glacier_client;
        Aws::String upload_id = initiate_multipart_upload(glacier_client, 
            vault_name, part_size, account_id, archive_description);
        if (upload_id.empty()) {
            exit(1);
        }

        // Upload all parts
        Aws::String checksum = upload_parts(glacier_client,
            vault_name, part_size, account_id, upload_id, file_name);
        if (checksum.empty()) {
            abort_multipart_upload(glacier_client, vault_name, account_id, upload_id);
            exit(2);
        }

        // Complete the operation
        std::string filesize_std = std::to_string(get_file_size(file_name));
        Aws::String filesize(filesize_std.c_str(), filesize_std.size());
        Aws::String archive_id;
        Aws::String location;
        bool result = complete_upload(glacier_client, 
            vault_name, account_id, upload_id, checksum, filesize, 
            archive_id, location);

        // Process the result
        if (result)
        {
            std::cout << "Archive ID: " << archive_id << std::endl;
            std::cout << "Location: " << location << std::endl;
        }
        else {
            abort_multipart_upload(glacier_client, vault_name, account_id, upload_id);
        }
    }

    Aws::ShutdownAPI(options);
    return 0;
}
