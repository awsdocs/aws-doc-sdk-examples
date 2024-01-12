// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/*
 * Test types are indicated by the test label ending.
 *
 * _1_ Requires credentials, permissions, and AWS resources.
 * _2_ Requires credentials and permissions.
 * _3_ Does not require credentials.
 *
 */

#include <gtest/gtest.h>
#include "cpp_lambda_functions.h"
#include "pam_gtests.h"
#include <aws/core/http/HttpClientFactory.h>
#include <aws/s3/model/ServerSideEncryption.h>
#include <aws/s3/S3Client.h>
#include <aws/core/http/HttpClient.h>
#include <fstream>

const char STORAGE_BUCKET_NAME[] = "PAM_STORAGE_BUCKET_NAME";
const char WORKING_BUCKET_NAME[] = "PAM_WORKING_BUCKET_NAME";
const char IMAGE_FILE_NAME[] = SOURCE_DIR  "/test_image.jpg";
const char TABLE_NAME[] = "PAM_TABLE_NAME";
const char TOPIC_ARN[] = "PAM_TOPIC_ARN";


namespace AwsDocTest {
    // NOLINTNEXTLINE(readability-named-parameter)
    TEST_F(PAM_GTests, pam_integration_test_1_) {
        const char *env_var = std::getenv(STORAGE_BUCKET_NAME);
        ASSERT_NE(env_var, nullptr) << preconditionError() << STORAGE_BUCKET_NAME;
        Aws::String storageBucketName(env_var);


        // Test the code for the upload lambda.
        Aws::String key = uuidName("file_name");
        auto preSignedUrlPut = AwsDoc::PAM::getPreSignedS3UploadURL(storageBucketName,
                                                                    key,
                                                                    *s_clientConfig);
        ASSERT_TRUE(!preSignedUrlPut.empty());


        std::shared_ptr<Aws::Http::HttpRequest> putRequest =
                CreateHttpRequest(preSignedUrlPut, Aws::Http::HttpMethod::HTTP_PUT,
                                  Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);


        const std::shared_ptr<Aws::IOStream> input_data =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              IMAGE_FILE_NAME,
                                              std::ios_base::in |
                                              std::ios_base::binary |
                                              std::ios_base::ate);

        Aws::StringStream intConverter;
        intConverter << input_data->tellp();
        putRequest->SetContentLength(intConverter.str());
        input_data->seekg(std::ios::beg);
        putRequest->SetContentType("image/jpeg");
        putRequest->AddContentBody(input_data);

        const std::shared_ptr<Aws::Http::HttpClient> httpClient = Aws::Http::CreateHttpClient(
                Aws::Client::ClientConfiguration());
        std::shared_ptr<Aws::Http::HttpResponse> putResponse = httpClient->MakeRequest(
                putRequest);

        ASSERT_FALSE(putResponse->HasClientError())
                                    << putResponse->GetClientErrorMessage()
                                    << std::endl;
        ASSERT_EQ(Aws::Http::HttpResponseCode::OK, putResponse->GetResponseCode())
                                    << putResponse->GetResponseBody().rdbuf()
                                    << std::endl;

        addObjectToDelete(storageBucketName, key);

        // Test the code for the detect labels lambda.
        std::vector<std::string> labels;
        bool successBool = AwsDoc::PAM::analyzeAndGetLabels(storageBucketName, key,
                                                            labels, std::cerr,
                                                            *s_clientConfig);
        ASSERT_TRUE(successBool);

        env_var = std::getenv(TABLE_NAME);
        ASSERT_NE(env_var, nullptr) << preconditionError() << TABLE_NAME;
        const std::string tableName(env_var);

        setDatabaseName(tableName);

        successBool = AwsDoc::PAM::updateLabelsInDatabase(tableName, labels,
                                                          key, std::cerr,
                                                          *s_clientConfig);
        ASSERT_TRUE(successBool);

        addLabelsToDelete(labels);

        //  Test the code for the get labels lambda.
        std::vector<AwsDoc::PAM::LabelAndCounts> labelsAndCounts;
        successBool = AwsDoc::PAM::getLabelsAndCounts(tableName, labelsAndCounts,
                                                      std::cerr, *s_clientConfig);
        ASSERT_TRUE(successBool);

        env_var = std::getenv(WORKING_BUCKET_NAME);
        ASSERT_NE(env_var, nullptr) << preconditionError() << WORKING_BUCKET_NAME;
        const std::string workingBucketName(env_var);

        // Test the code for the download lambda.
        std::string destinationKey = "PAM_Test.zip";
        std::string preSignedURLGet;
        successBool = AwsDoc::PAM::zipAndUploadImages(tableName, storageBucketName,
                                                      workingBucketName, destinationKey,
                                                      labels, preSignedURLGet,
                                                      std::cerr, *s_clientConfig);
        ASSERT_TRUE(successBool);
        addObjectToDelete(workingBucketName, destinationKey);

        std::shared_ptr<Aws::Http::HttpRequest> getRequest =
                CreateHttpRequest(preSignedURLGet, Aws::Http::HttpMethod::HTTP_GET,
                                  Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);

        std::shared_ptr<Aws::Http::HttpResponse> getResponse = httpClient->MakeRequest(
                getRequest);

        ASSERT_FALSE(getResponse->HasClientError())
                                    << getResponse->GetClientErrorMessage()
                                    << std::endl;
        ASSERT_EQ(Aws::Http::HttpResponseCode::OK, getResponse->GetResponseCode())
                                    << getResponse->GetResponseBody().rdbuf()
                                    << std::endl;

        env_var = std::getenv(TOPIC_ARN);
        ASSERT_NE(env_var, nullptr) << preconditionError() << TOPIC_ARN;
        const std::string topicARN(env_var);

        successBool = AwsDoc::PAM::publishPreSignedURL(topicARN, preSignedURLGet,
                                                       std::cerr, *s_clientConfig);
        ASSERT_TRUE(successBool);
    }

    TEST_F(PAM_GTests, pam_integration_test_2_) {
        Aws::String storageBucketName = getCachedBucketName();
        ASSERT_FALSE(storageBucketName.empty()) << preconditionError();


        // Test the code for the upload lambda.
        Aws::String key = uuidName("file_name");
        auto preSignedUrlPut = AwsDoc::PAM::getPreSignedS3UploadURL(storageBucketName,
                                                                    key,
                                                                    *s_clientConfig);
        ASSERT_TRUE(!preSignedUrlPut.empty());


        std::shared_ptr<Aws::Http::HttpRequest> putRequest =
                CreateHttpRequest(preSignedUrlPut, Aws::Http::HttpMethod::HTTP_PUT,
                                  Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);


        const std::shared_ptr<Aws::IOStream> input_data =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              IMAGE_FILE_NAME,
                                              std::ios_base::in |
                                              std::ios_base::binary |
                                              std::ios_base::ate);

        Aws::StringStream intConverter;
        intConverter << input_data->tellp();
        putRequest->SetContentLength(intConverter.str());
        input_data->seekg(std::ios::beg);
        putRequest->SetContentType("image/jpeg");
        putRequest->AddContentBody(input_data);

        const std::shared_ptr<Aws::Http::HttpClient> httpClient = Aws::Http::CreateHttpClient(
                Aws::Client::ClientConfiguration());
        std::shared_ptr<Aws::Http::HttpResponse> putResponse = httpClient->MakeRequest(
                putRequest);

        ASSERT_FALSE(putResponse->HasClientError())
                                    << putResponse->GetClientErrorMessage()
                                    << std::endl;
        ASSERT_EQ(Aws::Http::HttpResponseCode::OK, putResponse->GetResponseCode())
                                    << putResponse->GetResponseBody().rdbuf()
                                    << std::endl;

        addObjectToDelete(storageBucketName, key);

        // Test the code for the detect labels lambda.
        std::vector<std::string> labels;
        bool successBool = AwsDoc::PAM::analyzeAndGetLabels(storageBucketName, key,
                                                            labels, std::cerr,
                                                            *s_clientConfig);
        ASSERT_TRUE(successBool);

        const std::string tableName = getCachedTableName();
        ASSERT_FALSE(tableName.empty()) << preconditionError();

        setDatabaseName(tableName);

        successBool = AwsDoc::PAM::updateLabelsInDatabase(tableName, labels,
                                                          key, std::cerr,
                                                          *s_clientConfig);
        ASSERT_TRUE(successBool);

        addLabelsToDelete(labels);

        //  Test the code for the get labels lambda.
        std::vector<AwsDoc::PAM::LabelAndCounts> labelsAndCounts;
        successBool = AwsDoc::PAM::getLabelsAndCounts(tableName, labelsAndCounts,
                                                      std::cerr, *s_clientConfig);
        ASSERT_TRUE(successBool);

        const std::string workingBucketName = getCachedBucketName();

        // Test the code for the download lambda.
        std::string destinationKey = "PAM_Test.zip";
        std::string preSignedURLGet;
        successBool = AwsDoc::PAM::zipAndUploadImages(tableName, storageBucketName,
                                                      workingBucketName, destinationKey,
                                                      labels, preSignedURLGet,
                                                      std::cerr, *s_clientConfig);
        ASSERT_TRUE(successBool);
        addObjectToDelete(workingBucketName, destinationKey);

        std::shared_ptr<Aws::Http::HttpRequest> getRequest =
                CreateHttpRequest(preSignedURLGet, Aws::Http::HttpMethod::HTTP_GET,
                                  Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);

        std::shared_ptr<Aws::Http::HttpResponse> getResponse = httpClient->MakeRequest(
                getRequest);

        ASSERT_FALSE(getResponse->HasClientError())
                                    << getResponse->GetClientErrorMessage()
                                    << std::endl;
        ASSERT_EQ(Aws::Http::HttpResponseCode::OK, getResponse->GetResponseCode())
                                    << getResponse->GetResponseBody().rdbuf()
                                    << std::endl;
    }

} // namespace AwsDocTest
