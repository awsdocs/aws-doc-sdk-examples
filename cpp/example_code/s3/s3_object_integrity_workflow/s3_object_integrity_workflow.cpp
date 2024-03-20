// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
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

#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/CreateMultipartUploadRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/GetObjectAttributesRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/UploadPartRequest.h>
#include <aws/s3/model/CompleteMultipartUploadRequest.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/HashingUtils.h>
#include <aws/core/utils/stream/PreallocatedStreamBuf.h>
#include <md5.h>
#include <iomanip>
#include <fstream>
#include <vector>
#include "awsdoc/s3/s3_examples.h"


namespace AwsDoc::S3 {
  //  constexpr char TEST_FILE[] = SRC_DIR"/../../../../resources/sample_files/movies.json";
  constexpr char TEST_FILE[] = SRC_DIR"/s3_object_integrity_workflow.cpp";
    constexpr char TEST_BUCKET_PREFIX[] = "integrity-workflow-";
    constexpr char TEST_KEY_MD5[] = "test_key_md5";
    constexpr size_t MAX_BUCKET_NAME_LENGTH = 63;

    enum class HASH_METHOD {
        MD5,
        CRC32,
        CRC32C,
        SHA1,
        SHA256
    };

    //! Routine which runs the S3 object integrity workflow.
    /*!
       \param clientConfig: Aws client configuration.
       \return bool: Function succeeded.
    */
    bool s3ObjectIntegrityWorkflow(
            const Aws::Client::ClientConfiguration &clientConfiguration);

    bool calculateObjectHash(Aws::IOStream &data,
                             AwsDoc::S3::HASH_METHOD hashMethod,
                             Aws::String &hash);

    bool putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                           const Aws::String &hashData, AwsDoc::S3::HASH_METHOD hashMethod,
                           const std::shared_ptr<Aws::IOStream> &body,
                           const Aws::S3::S3Client &client);

    bool retrieveObjectHash(const Aws::String &bucket,
                            const Aws::String &key,
                            Aws::String &hashData,
                            AwsDoc::S3::HASH_METHOD hashMethod,
                            const Aws::S3::S3Client &client);

    bool downloadObjectAndCheckHash(const Aws::String &bucket,
                                    const Aws::String &key,
                                    const Aws::String &hashData,
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    const Aws::S3::S3Client &client);

    bool doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                       const Aws::String &key,
                                       AwsDoc::S3::HASH_METHOD hashMethod,
                                       const std::shared_ptr<Aws::IOStream> &fileStream,
                                       Aws::String &hashDataResult,
                                       const Aws::S3::S3Client &client);

    Aws::String stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

    bool cleanUp(const Aws::String &bucket,
                 const Aws::Client::ClientConfiguration &clientConfiguration);

    //! Test routine passed as argument to askQuestion routine.
    /*!
     \param string: A string to test.
     \return bool: True if empty.
     */
    static bool testForEmptyString(const Aws::String &string);

    //! Command line prompt/response utility function.
    /*!
     \param string: A question prompt.
     \param test: Test function for response.
     \return Aws::String: User's response.
     */
    static Aws::String askQuestion(const Aws::String &string,
                                   const std::function<bool(
                                           Aws::String)> &test = testForEmptyString);

    //! Command line prompt/response for yes/no question.
    /*!
     \param string: A question prompt expecting a 'y' or 'n' response.
     \return bool: True if yes.
     */
    static bool askYesNoQuestion(const Aws::String &string);

    //! Command line prompt/response utility function for an int result confined to
    //! a range.
    /*!
     \param string: A question prompt.
     \param low: Low inclusive.
     \param high: High inclusive.
     \return int: User's response.
     */
    static int askQuestionForIntRange(const Aws::String &string, int low,
                                      int high);

    //! Utility routine to print a line of asterisks to standard out.
    /*!
    \return void:
     */
    static void printAsterisksLine() {
        std::cout << "\n" << std::setfill('*') << std::setw(88) << "\n"
                  << std::endl;
    }

    //! Test routine passed as argument to askQuestion routine.
    /*!
     \return bool: Always true.
     */
    static bool alwaysTrueTest(const Aws::String &) { return true; }

    static bool DEBUGGING = false;
} // namespace AwsDoc::S3

//! Routine which runs the HealthImaging workflow.
/*!
   \param clientConfig: Aws client configuration.
   \return bool: Function succeeded.
*/
bool AwsDoc::S3::s3ObjectIntegrityWorkflow(
        const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::String bucketName = TEST_BUCKET_PREFIX;
    bucketName += Aws::Utils::UUID::RandomUUID();
    bucketName = Aws::Utils::StringUtils::ToLower(bucketName.c_str());

    bucketName.resize(std::min(bucketName.size(), MAX_BUCKET_NAME_LENGTH));

    if (!AwsDoc::S3::CreateBucket(bucketName, clientConfiguration)) {
        std::cerr << "Workflow exiting because bucket creation failed." << std::endl;
        return false;
    }


    Aws::S3::S3Client client(clientConfiguration);

    for (int hashMethod = (int)HASH_METHOD::MD5; hashMethod <= (int)HASH_METHOD::SHA256; ++hashMethod) {
        std::shared_ptr<Aws::IOStream> inputData =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              TEST_FILE,
                                              std::ios_base::in | std::ios_base::binary);

        if (!*inputData) {
            std::cerr << "Error unable to read file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Aws::String hashData;
        std::cout << "Testing the hash " << stringForHashMethod((HASH_METHOD) hashMethod) << std::endl;
        askQuestion("Press enter to continue...", alwaysTrueTest);
        if (!calculateObjectHash(*inputData, (HASH_METHOD) hashMethod, hashData)) {
            std::cerr << "Error calculating hash for file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }
        Aws::String key = stringForHashMethod((HASH_METHOD) hashMethod);

        if (!putObjectWithHash(bucketName, key, hashData, (HASH_METHOD) hashMethod, inputData, client)) {
            std::cerr << "Error putting file " << TEST_FILE << " to bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        if (!retrieveObjectHash(bucketName, key, hashData, (HASH_METHOD) hashMethod, client)) {
            std::cerr << "Error getting file " << TEST_FILE << " from bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }
    }
#if 0
    Aws::S3::Model::GetObjectAttributesRequest getObjectAttributesRequest;
    getObjectAttributesRequest.SetBucket(TEST_BUCKET);
    getObjectAttributesRequest.SetKey(TEST_KEY_MD5);
    auto getObjectAttributesOutcome = client.GetObjectAttributes(getObjectAttributesRequest);
    if (!getObjectAttributesOutcome.IsSuccess()) {
        std::cerr << "Error getting file " << TEST_FILE << " from bucket "
                  << TEST_BUCKET << " with key " << TEST_KEY_MD5 << std::endl;
        return false;
    }
    const Aws::S3::Model::GetObjectAttributesResult &result = getObjectAttributesOutcome.GetResult();
#endif


    return cleanUp(bucketName, clientConfiguration);
}

static bool debugDeleteBucketsWithPrefix(const Aws::String &prefix,
                                       const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::S3::S3Client client(clientConfiguration);

    auto outcome = client.ListBuckets();
    Aws::Vector<Aws::String> buckets;
    if (outcome.IsSuccess()) {
        for (const auto &bucket: outcome.GetResult().GetBuckets()) {
            buckets.push_back(bucket.GetName());
        }
    }
    bool result = true;
    for (const auto &bucket: buckets) {
        if (bucket.find(prefix) == 0) {
            Aws::Vector<Aws::String> keysResult;
            if (AwsDoc::S3::ListObjects(bucket, keysResult, clientConfiguration)) {
                if (!keysResult.empty()) {
                    result = AwsDoc::S3::DeleteObjects(keysResult, bucket,
                                                       clientConfiguration);
                }
            }
            else {
                result = false;
            }

            AwsDoc::S3::DeleteBucket(bucket, clientConfiguration);
        }
    }
    return result;
}

/*
 *
 * main function
*
 *  Usage: 'run_medical_image_sets_and_frames_workflow'
 *
*/
int main(int argc, char **argv) {
    (void) argc;
    (void) argv;
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Debug;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::S3::s3ObjectIntegrityWorkflow(
                clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

//! Test routine passed as argument to askQuestion routine.
/*!
\param string: A string to test.
\return bool: True if empty.
*/
bool AwsDoc::S3::testForEmptyString(const Aws::String &string) {
    if (string.empty()) {
        std::cout << "Enter some text." << std::endl;
        return false;
    }

    return true;
}

//! Command line prompt/response utility function.
/*!
 \param string: A question prompt.
 \param test: Test function for response.
 \return Aws::String: User's response.
 */
Aws::String AwsDoc::S3::askQuestion(const Aws::String &string,
                                                 const std::function<bool(
                                                         Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
    } while (!test(result));

    return result;
}

//! Command line prompt/response for yes/no question.
/*!
 \param string: A question prompt expecting a 'y' or 'n' response.
 \return bool: True if yes.
 */
bool AwsDoc::S3::askYesNoQuestion(const Aws::String &string) {
    Aws::String resultString = askQuestion(string, [](
            const Aws::String &string1) -> bool {
            bool result = false;
            if (string1.length() == 1) {
                int answer = std::tolower(string1[0]);
                result = (answer == 'y') || (answer == 'n');
            }

            if (!result) {
                std::cout << "Answer 'y' or 'n'." << std::endl;
            }

            return result;
    });

    return std::tolower(resultString[0]) == 'y';
}

//! Command line prompt/response utility function for an int result confined to
//! a range.
/*!
 \param string: A question prompt.
 \param low: Low inclusive.
 \param high: High inclusive.
 \return int: User's response.
 */
int
AwsDoc::S3::askQuestionForIntRange(const Aws::String &string, int low,
                                                int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                bool result = number >= low && number <= high;
                if (!result) {
                    std::cerr << "\nThe number is out of range." << std::endl;
                }
                return result;
            }
            catch (const std::invalid_argument &) {
                std::cerr << "\nNot a valid number." << std::endl;
                return false;
            }
    });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

bool AwsDoc::S3::cleanUp(const Aws::String &bucketName,
                         const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::Vector<Aws::String> keysResult;
    bool result = true;
    if (AwsDoc::S3::ListObjects(bucketName, keysResult, clientConfiguration)) {
        if (!keysResult.empty()) {
            result = AwsDoc::S3::DeleteObjects(keysResult, bucketName, clientConfiguration);
        }
    }
    else {
        result = false;
    }

    return result && AwsDoc::S3::DeleteBucket(bucketName, clientConfiguration);
}

bool AwsDoc::S3::calculateObjectHash(Aws::IOStream &data,
                                     AwsDoc::S3::HASH_METHOD hashMethod,
                                     Aws::String &hash) {
    Aws::Utils::ByteBuffer dataBuffer;
    switch (hashMethod) {
        case AwsDoc::S3::HASH_METHOD::MD5:
            dataBuffer = Aws::Utils::HashingUtils::CalculateMD5(data);
            break;
        case AwsDoc::S3::HASH_METHOD::SHA1:
            dataBuffer = Aws::Utils::HashingUtils::CalculateSHA1(data);
            break;
        case AwsDoc::S3::HASH_METHOD::SHA256:
            dataBuffer = Aws::Utils::HashingUtils::CalculateSHA256(data);
            break;
         case HASH_METHOD::CRC32:
             dataBuffer = Aws::Utils::HashingUtils::CalculateCRC32(data);
            break;
        case HASH_METHOD::CRC32C:
            dataBuffer = Aws::Utils::HashingUtils::CalculateCRC32C(data);
            break;
        default:
            std::cerr << "Unknown hash method." << std::endl;
            return false;
    }
    data.seekg(std::ios_base::beg);
    hash = Aws::Utils::HashingUtils::Base64Encode(dataBuffer);

#if 1 //TODO remove
    std::stringstream debugStream;
    debugStream << std::hex << std::setfill('0') << std::setw(2);
    for (int i = 0; i < dataBuffer.GetLength(); ++i) {
        debugStream << (int)dataBuffer[i];
    }
    std::cout << "Unencoded has " << debugStream.str() << ", encoded hash " << hash << std::endl;
#endif

    return true;
}

bool AwsDoc::S3::putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                                   const Aws::String &hashData,
                                   AwsDoc::S3::HASH_METHOD hashMethod,
                                   const std::shared_ptr<Aws::IOStream> &body,
                                   const Aws::S3::S3Client &client) {
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    switch (hashMethod) {
        case AwsDoc::S3::HASH_METHOD::MD5:
            request.SetContentMD5(hashData);
            break;
        case AwsDoc::S3::HASH_METHOD::SHA1:
            request.SetChecksumSHA1(hashData);
            break;
        case AwsDoc::S3::HASH_METHOD::SHA256:
            request.SetChecksumSHA256(hashData);
            break;
        case HASH_METHOD::CRC32:
            request.SetChecksumCRC32(hashData);
            break;
        case HASH_METHOD::CRC32C:
            request.SetChecksumCRC32C(hashData);
            break;
        default:
            std::cerr << "Unknown hash method." << std::endl;
            return false;
    }
    request.SetBody(body);
    Aws::S3::Model::PutObjectOutcome outcome = client.PutObject(request);
    body->seekg(std::ios_base::beg);
    if (outcome.IsSuccess()) {
        std::cout << "Object successfully uploaded." << std::endl;
    }
    else {
        std::cerr << "Error uploading object." <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    return outcome.IsSuccess();
}

bool AwsDoc::S3::retrieveObjectHash(const Aws::String &bucket, const Aws::String &key,
                                    Aws::String &hashData,
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    const Aws::S3::S3Client &client) {
    Aws::S3::Model::GetObjectAttributesRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    auto outcome = client.GetObjectAttributes(request);
    if (outcome.IsSuccess()) {
        const Aws::S3::Model::GetObjectAttributesResult &result = outcome.GetResult();
        switch (hashMethod) {
            case AwsDoc::S3::HASH_METHOD::MD5:
                std::cerr << "MD5 not supported." << std::endl;
                break;
            case AwsDoc::S3::HASH_METHOD::SHA1:
                hashData = result.GetChecksum().GetChecksumSHA1();
                break;
            case AwsDoc::S3::HASH_METHOD::SHA256:
                hashData = result.GetChecksum().GetChecksumSHA256();
                break;
            case HASH_METHOD::CRC32:
                hashData = result.GetChecksum().GetChecksumCRC32();
                break;
            case HASH_METHOD::CRC32C:
                hashData = result.GetChecksum().GetChecksumCRC32C();
                break;
            default:
                std::cerr << "Unknown hash method." << std::endl;
                return false;
        }
    }

    return true;
 }

Aws::String AwsDoc::S3::stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod) {
    switch (hashMethod) {
        case AwsDoc::S3::HASH_METHOD::MD5:
            return "MD5";
        case AwsDoc::S3::HASH_METHOD::SHA1:
            return "SHA1";
        case AwsDoc::S3::HASH_METHOD::SHA256:
            return "SHA256";
        case HASH_METHOD::CRC32:
            return "CRC32";
        case HASH_METHOD::CRC32C:
            return "CRC32C";
        default:
            return "Unknown";
    }
}

bool AwsDoc::S3::downloadObjectAndCheckHash(const Aws::String &bucket,
                                            const Aws::String &key,
                                            const Aws::String &hashData,
                                            AwsDoc::S3::HASH_METHOD hashMethod,
                                            const Aws::S3::S3Client &client) {
    Aws::S3::Model::GetObjectRequest request;

    request.SetBucket(bucket);
    request.SetKey(key);
    bool result = true;
    auto outcome = client.GetObject(request);
    if (outcome.IsSuccess()) {
        auto &getObjectResult = outcome.GetResult();
        Aws::String hash;
        if (calculateObjectHash(outcome.GetResult().GetBody(), hashMethod, hash)) {
            if (hashData == hash) {
                std::cout << "Hashes match." << std::endl;
            }
            else {
                std::cerr << "Hashes do not match." << std::endl;
                result = false;
            }
        }
        Aws::String storedHash;
        switch (hashMethod) {

            case AwsDoc::S3::HASH_METHOD::MD5:
                std::cerr << "MD5 not supported." << std::endl;
                result &= false;
                break;
            case AwsDoc::S3::HASH_METHOD::SHA1:
                storedHash = getObjectResult.GetChecksumSHA1();
                break;
            case AwsDoc::S3::HASH_METHOD::SHA256:
                storedHash = getObjectResult.GetChecksumSHA256();
                break;
            case HASH_METHOD::CRC32:
                storedHash = getObjectResult.GetChecksumCRC32();
                break;
            case HASH_METHOD::CRC32C:
                storedHash = getObjectResult.GetChecksumCRC32C();
                break;
            default:
                std::cerr << "Unknown hash method." << std::endl;
                break;
        }

        if (storedHash != hash) {
            std::cerr << "Hashes do not match." << std::endl;
            result &= false;
        }
    }
    return result;
}

bool AwsDoc::S3::doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                               const Aws::String &key,
                                               AwsDoc::S3::HASH_METHOD hashMethod,
                                               const std::shared_ptr<Aws::IOStream> &fileStream,
                                               Aws::String &hashDataResult,
                                               const Aws::S3::S3Client &client) {
    // Create a buffer that will upload the object in 3 parts.

    // Get object size.
    fileStream->seekg(std::ios_base::end);
    size_t objectSize = fileStream->tellg();
    fileStream->seekg(std::ios_base::beg);

    size_t bufferSize = objectSize / 3;
    // Round up to a multiple of 32.
    bufferSize = ((bufferSize + 31) / 32) * 32;

    Aws::S3::Model::CreateMultipartUploadRequest createMultipartUploadRequest;
    createMultipartUploadRequest.SetBucket(bucket);
    createMultipartUploadRequest.SetKey(key);
    auto createMultipartUploadOutcome = client.CreateMultipartUpload(createMultipartUploadRequest);

    Aws::String uploadID;
    if (createMultipartUploadOutcome.IsSuccess()) {
        uploadID = createMultipartUploadOutcome.GetResult().GetUploadId();
    }
    else {
        std::cerr << "Error creating multipart upload." <<
                  createMultipartUploadOutcome.GetError().GetMessage() << std::endl;
        return false;
    }

    size_t uploadedBytes;
    int partNumber = 1;
    Aws::S3::Model::CompletedMultipartUpload completedMultipartUpload;
    while (uploadedBytes < objectSize){

        // Create a request to upload a part.
        Aws::S3::Model::UploadPartRequest uploadPartRequest;
        uploadPartRequest.SetBucket(bucket);
        uploadPartRequest.SetKey(key);
        uploadPartRequest.SetUploadId(uploadID);
        uploadPartRequest.SetPartNumber(partNumber++);

        std::vector<unsigned char> buffer(bufferSize);
        size_t bytesToRead = std::min(bufferSize, objectSize - uploadedBytes);
        fileStream->read((char *)buffer.data(), bytesToRead);
        Aws::Utils::Stream::PreallocatedStreamBuf preallocatedStreamBuf(buffer.data(), bytesToRead);
        std::shared_ptr<Aws::IOStream> body =
                Aws::MakeShared<Aws::IOStream>("SampleAllocationTag",
                                               &preallocatedStreamBuf);
        uploadPartRequest.SetBody(body);

        auto uploadPartOutcome = client.UploadPart(uploadPartRequest);
        if (uploadPartOutcome.IsSuccess()) {
            const Aws::S3::Model::UploadPartResult &uploadPartResult = uploadPartOutcome.GetResult();
            Aws::S3::Model::CompletedPart completedPart;
            completedPart.SetETag(uploadPartOutcome.GetResult().GetETag());
            completedPart.SetPartNumber(uploadPartRequest.GetPartNumber());

            completedMultipartUpload.AddParts(completedPart);
        }
        else {
            std::cerr << "Error uploading part." <<
                      uploadPartOutcome.GetError().GetMessage() << std::endl;
            return false;
        }

        uploadedBytes += bufferSize;
    }

    Aws::S3::Model::CompleteMultipartUploadRequest completeMultipartUploadRequest;
    completeMultipartUploadRequest.SetBucket(bucket);
    completeMultipartUploadRequest.SetKey(key);
    completeMultipartUploadRequest.SetUploadId(uploadID);
    completeMultipartUploadRequest.SetMultipartUpload(completedMultipartUpload);
    auto completeMultipartUploadOutcome = client.CompleteMultipartUpload(completeMultipartUploadRequest);

    if (completeMultipartUploadOutcome.IsSuccess()) {
        std::cout << "Multipart upload completed." << std::endl;
    }

    return false;
}

