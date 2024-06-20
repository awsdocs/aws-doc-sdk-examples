// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/core/http/HttpClient.h>
#include <aws/core/client/AWSUrlPresigner.h>
#include <aws/core/client/RetryStrategy.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/PutObjectRequest.h>
#include "s3_examples.h"

// The libcurl must be installed to test the pre-signed URL returned in this example.
// See, https://curl.se/libcurl/c/libcurl.html.
#if HAS_CURL

#include <curl/curl.h>

#endif

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

// snippet-start:[cpp.example_code.s3.presigned.PutObject]
//! Routine which demonstrates creating a pre-signed URL to upload an object to an
//! Amazon Simple Storage Service (Amazon S3) bucket.
/*!
  \param bucketName: Name of the bucket.
  \param key: Name of an object key.
  \param clientConfig: Aws client configuration.
  \return Aws::String: A pre-signed URL.
*/
Aws::String AwsDoc::S3::generatePreSignedPutObjectUrl(const Aws::String &bucketName,
                                                      const Aws::String &key,
                                                      uint64_t expirationSeconds,
                                                      const Aws::S3::S3ClientConfiguration &clientConfig) {
    Aws::S3::S3Client client(clientConfig);
    return client.GeneratePresignedUrl(bucketName, key, Aws::Http::HttpMethod::HTTP_PUT,
                                       expirationSeconds);
}
// snippet-end:[cpp.example_code.s3.presigned.PutObject]

#if HAS_CURL

// snippet-start:[cpp.example_code.s3.presigned.PutObjectUpload]
static size_t myCurlReadBack(char *buffer, size_t size, size_t nitems, void *userdata) {
    Aws::StringStream *str = (Aws::StringStream *) userdata;

    str->read(buffer, size * nitems);

    return str->gcount();
}

static size_t myCurlWriteBack(char *buffer, size_t size, size_t nitems, void *userdata) {
    Aws::StringStream *str = (Aws::StringStream *) userdata;

    if (nitems > 0) {
        str->write(buffer, size * nitems);
    }
    return size * nitems;
}

//! Utility routine to test putObject with a pre-signed URL.
/*!
  \param presignedURL: A pre-signed URL to put an object in a bucket.
  \param data: Body of the putObject request.
  \return bool: Function succeeded.
*/
bool AwsDoc::S3::PutStringWithPresignedObjectURL(const Aws::String &presignedURL,
                                                 const Aws::String &data) {
    CURL *curl = curl_easy_init();
    CURLcode result;

    Aws::StringStream readStringStream;
    readStringStream << data;
    result = curl_easy_setopt(curl, CURLOPT_READFUNCTION, myCurlReadBack);

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_READFUNCTION" << std::endl;
        return false;
    }

    result = curl_easy_setopt(curl, CURLOPT_READDATA, &readStringStream);
    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_READDATA" << std::endl;
        return false;
    }

    result = curl_easy_setopt(curl, CURLOPT_INFILESIZE_LARGE,
                              (curl_off_t) data.size());

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_INFILESIZE_LARGE" << std::endl;
        return false;
    }

    result = curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, myCurlWriteBack);

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_WRITEFUNCTION" << std::endl;
        return false;
    }

    std::stringstream outWriteString;

    result = curl_easy_setopt(curl, CURLOPT_WRITEDATA, &outWriteString);

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_WRITEDATA " << std::endl;
        return false;
    }

    result = curl_easy_setopt(curl, CURLOPT_URL, presignedURL.c_str());

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_URL" << std::endl;
        return false;
    }

    result = curl_easy_setopt(curl, CURLOPT_UPLOAD, 1L);

    if (result != CURLE_OK) {
        std::cerr << "Failed to set CURLOPT_PUT" << std::endl;
        return false;
    }

    result = curl_easy_perform(curl);

    if (result != CURLE_OK) {
        std::cerr << "Failed to perform CURL request" << std::endl;
        return false;
    }

    std::string outString = outWriteString.str();
    if (outString.empty()) {
        std::cout << "Successfully put object." << std::endl;
        return true;
    } else {
        std::cout << "A server error was encountered, output:\n" << outString
                  << std::endl;
        return false;
    }
}
// snippet-end:[cpp.example_code.s3.presigned.PutObjectUpload]

#endif
/*
 *
 * main function
 *
 * Prerequisites: An existing bucket.
 *
 * Usage: 'run_presigned_put_object <bucket_name> <object_key>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {

    if (argc != 3) {
        std::cout << R"(
Usage:
   run_presigned_put_object <bucket_name> <object_key>
Where:
   bucket_name - Name of S3 bucket.
   <object_key> - An object key.
)" << std::endl;

        return 1;
    }
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
    Aws::InitAPI(options);
    {
        Aws::String bucketName(argv[1]);
        Aws::String objectKey(argv[2]);
        uint64_t presignedSecondsTimeout = 10 * 60;

        Aws::S3::S3ClientConfiguration clientConfig;
        // clientConfig.region = "us-east-1";
        Aws::String presignedUrl = AwsDoc::S3::generatePreSignedPutObjectUrl(bucketName,
                                                                             objectKey,
                                                                             presignedSecondsTimeout,
                                                                             clientConfig);

        std::cout << "Presigned URL:\n" << presignedUrl << std::endl;
#if HAS_CURL
        AwsDoc::S3::PutStringWithPresignedObjectURL(presignedUrl, "test string");
#endif
    }

    ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD