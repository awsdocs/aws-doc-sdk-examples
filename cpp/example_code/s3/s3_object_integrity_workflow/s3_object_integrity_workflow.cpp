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
#include <aws/s3/model/AbortMultipartUploadRequest.h>
#include <aws/s3/model/CreateMultipartUploadRequest.h>
#include <aws/s3/model/DeleteObjectRequest.h>
#include <aws/s3/model/GetObjectAttributesRequest.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/s3/model/UploadPartRequest.h>
#include <aws/s3/model/CompleteMultipartUploadRequest.h>
#include <aws/transfer/TransferManager.h>
#include <aws/transfer/TransferHandle.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/HashingUtils.h>
#include <aws/core/utils/stream/PreallocatedStreamBuf.h>
#include <iomanip>
#include <fstream>
#include <vector>
#include "awsdoc/s3/s3_examples.h"


namespace AwsDoc::S3 {
    //  constexpr char TEST_FILE[] = SRC_DIR"/../../../../resources/sample_files/movies.json";
    constexpr char TEST_FILE[] = SRC_DIR"/CMakeLists.txt";
    constexpr char MULTI_PART_TEST_FILE[] = SRC_DIR"/s3-userguide.pdf";
    constexpr char TEST_BUCKET_PREFIX[] = "integrity-workflow-";
    constexpr char TEST_KEY_MD5[] = "test_key_md5";
    constexpr size_t MAX_BUCKET_NAME_LENGTH = 63;
    const size_t UPLOAD_BUFFER_SIZE = 5 * 1024 * 1024;

    static bool useCalculatedChecksum = false;

    typedef int HASH_METHOD;

    static const HASH_METHOD MD5 = 0;
    static const HASH_METHOD CRC32 = MD5 + 1;
    static const HASH_METHOD CRC32C = CRC32 + 1;
    static const HASH_METHOD SHA1 = CRC32C + 1;
    static const HASH_METHOD SHA256 = SHA1 + 1;

    class Hasher {
        Aws::Utils::ByteBuffer m_Hash;

    public:
        Hasher() {}

        bool calculateObjectHash(std::vector<unsigned char> &data,
                                 HASH_METHOD hashMethod);

        bool calculateObjectHash(Aws::IOStream &data,
                                 HASH_METHOD hashMethod);

        Aws::String getBase64HashString();

        Aws::String getHexHashString();

        Aws::Utils::ByteBuffer getByteBufferHash();
    };

    //! Routine which runs the S3 object integrity workflow.
    /*!
       \param clientConfig: Aws client configuration.
       \return bool: Function succeeded.
    */
    bool s3ObjectIntegrityWorkflow(
            const Aws::Client::ClientConfiguration &clientConfiguration);


    bool putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                           const Aws::String &hashData,
                           AwsDoc::S3::HASH_METHOD hashMethod,
                           const std::shared_ptr<Aws::IOStream> &body,
                           const Aws::S3::S3Client &client);

    bool retrieveObjectHash(const Aws::String &bucket, const Aws::String &key,
                            AwsDoc::S3::HASH_METHOD hashMethod,
                            Aws::String &hashData,
                            std::vector<Aws::String> *partHashes,
                            const Aws::S3::S3Client &client);

    bool downloadObjectAndCheckHash(const Aws::String &bucket,
                                    const Aws::String &key,
                                    const Aws::String &hashData,
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    const Aws::S3::S3Client &client);

    bool doTransferManagerUpload(const Aws::String &bucket, const Aws::String &key,
                                 AwsDoc::S3::HASH_METHOD hashMethod);

    bool doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                       const Aws::String &key,
                                       AwsDoc::S3::HASH_METHOD hashMethod,
                                       std::shared_ptr<Aws::IOStream> fileStream,
                                       AwsDoc::S3::Hasher &hashDataResult,
                                       std::vector<Aws::String> &partHashes,
                                       const Aws::S3::S3Client &client);

    Aws::String stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

    Aws::S3::Model::ChecksumAlgorithm getChecksumAlgorithmForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

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
#if 0
    for (HASH_METHOD hashMethod = MD5; hashMethod <= SHA256; ++hashMethod) {
        std::shared_ptr<Aws::IOStream> inputData =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              TEST_FILE,
                                              std::ios_base::in | std::ios_base::binary);

        if (!*inputData) {
            std::cerr << "Error unable to read file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Hasher hasher;
        std::cout << "Testing the hash " << stringForHashMethod(hashMethod) << std::endl;
        askQuestion("Press enter to continue...", alwaysTrueTest);
        if (!hasher.calculateObjectHash(*inputData, hashMethod)) {
            std::cerr << "Error calculating hash for file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }
        Aws::String key = stringForHashMethod(hashMethod);
        Aws::String localHash = hasher.getBase64HashString();
        if (!putObjectWithHash(bucketName, key, localHash, hashMethod, inputData, client)) {
            std::cerr << "Error putting file " << TEST_FILE << " to bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Aws::String retrievedHash;
        if (!retrieveObjectHash(bucketName, key,
                                hashMethod, retrievedHash,
                                nullptr, client)) {
            std::cerr << "Error getting file " << TEST_FILE << " from bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        if (hashMethod == MD5) {
            Aws::String hashAsHex = hasher.getHexHashString();
            if (hashAsHex != retrievedHash) {
                std::cerr << "Error retrieved etag " << retrievedHash
                          << " does not match " << hashAsHex << std::endl;
            }
        }
        else {
            if (retrievedHash != localHash) {
                std::cerr << "Error retrieved hash " << retrievedHash
                          << " does not match " << localHash << std::endl;
            }
        }
    }
#endif

    for (HASH_METHOD hashMethod = MD5;
         hashMethod <= SHA256; ++hashMethod) {
        Aws::String key("tr_");
        key += stringForHashMethod(hashMethod) + "-s3-userguide.pdf";
        std::cout << "Starting transfer manager upload of with hash method " <<
                  stringForHashMethod(hashMethod) << std::endl;
        if (!doTransferManagerUpload(bucketName, key, hashMethod))
        {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Aws::String retrievedHash;
        std::vector<Aws::String> retrievedPartHashes;
        if (!retrieveObjectHash(bucketName, key,
                                hashMethod,
                                retrievedHash, &retrievedPartHashes, client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        std::cout << "Retrieved hash " << retrievedHash << std::endl;
        for (auto retrievedPartHash : retrievedPartHashes){
            std::cout << "part hash " << retrievedPartHash << std::endl;
        }
    }
#if 1
    for (HASH_METHOD hashMethod = MD5;
         hashMethod <= SHA256; ++hashMethod) {
        std::shared_ptr<Aws::IOStream> inputData =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              MULTI_PART_TEST_FILE,
                                              std::ios_base::in |
                                              std::ios_base::binary);

        if (!inputData->good()) {
            std::cerr << "Error unable to read file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }


        Aws::String key = stringForHashMethod(hashMethod);
        key += "-s3-userguide.pdf";
        std::cout << "Starting multipart upload of with hash method " <<
                  stringForHashMethod(hashMethod) << std::endl;

        AwsDoc::S3::Hasher hashData;
        std::vector<Aws::String> partHashes;

        if (!doMultipartUploadAndCheckHash(bucketName, key,
                                                  hashMethod, inputData,
                                                  hashData,
                                                  partHashes,
                                                  client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        std::cout << "Finished multipart upload of with hash method " <<
                  stringForHashMethod(hashMethod) << std::endl;

        Aws::String retrievedHash;
        std::vector<Aws::String> retrievedPartHashes;
        if (!retrieveObjectHash(bucketName, key,
                                hashMethod,
                                retrievedHash, &retrievedPartHashes, client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        std::cout << "Retrieved hash " << retrievedHash << std::endl;
        for (auto retrievedPartHash : retrievedPartHashes){
            std::cout << "part hash " << retrievedPartHash << std::endl;
        }

        Aws::String hashString;
        if (hashMethod == MD5) {
            hashString = hashData.getHexHashString();
            hashString += "-" + std::to_string(partHashes.size());
        }
        else {
            hashString = hashData.getBase64HashString();
        }

        bool allMatch = true;
        if (hashString != retrievedHash) {
            std::cerr << "The main hashes do not match" << std::endl;
            std::cerr << "Local hash- '" << hashString << "'" << std::endl;
            std::cerr << "Remote hash - '" << retrievedHash << "'" << std::endl;
            allMatch = false;
        }

        if (hashMethod != MD5) {
            if (partHashes.size() != retrievedPartHashes.size()) {
                std::cerr << "The number of part hashes do not match" << std::endl;
                std::cerr << "Local number of hashes- '" << partHashes.size() << "'"
                          << std::endl;
                std::cerr << "Remote number of hashes - '" << retrievedPartHashes.size()
                          << "'" << std::endl;
                continue;
            }

            for (int i = 0; i < partHashes.size(); ++i) {
                if (partHashes[i] != retrievedPartHashes[i]) {
                    std::cerr << "The part hashes do not match for part " << i + 1
                              << "." << std::endl;
                    std::cerr << "Local hash- '" << partHashes[i] << "'" << std::endl;
                    std::cerr << "Remote hash - '" << retrievedPartHashes[i] << "'"
                              << std::endl;
                    allMatch = false;
                }
            }
        }

        if (allMatch) {
            std::cout << "All the local hashes match the remote hashes." << std::endl;
        }
    }
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
            result = AwsDoc::S3::DeleteObjects(keysResult, bucketName,
                                               clientConfiguration);
        }
    }
    else {
        result = false;
    }

    return result && AwsDoc::S3::DeleteBucket(bucketName, clientConfiguration);
}

bool AwsDoc::S3::putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                                   const Aws::String &hashData,
                                   AwsDoc::S3::HASH_METHOD hashMethod,
                                   const std::shared_ptr<Aws::IOStream> &body,
                                   const Aws::S3::S3Client &client) {
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    if (hashMethod !=  MD5)
    {
        request.SetChecksumAlgorithm(getChecksumAlgorithmForHashMethod(hashMethod));
    }
    if (useCalculatedChecksum) {
        switch (hashMethod) {
            case AwsDoc::S3::MD5:
                request.SetContentMD5(hashData);
                break;
            case AwsDoc::S3::SHA1:
                request.SetChecksumSHA1(hashData);
                break;
            case AwsDoc::S3::SHA256:
                request.SetChecksumSHA256(hashData);
                break;
            case AwsDoc::S3::CRC32:
                request.SetChecksumCRC32(hashData);
                break;
            case AwsDoc::S3::CRC32C:
                request.SetChecksumCRC32C(hashData);
                break;
            default:
                std::cerr << "Unknown hash method." << std::endl;
                return false;
        }
    }
    request.SetBody(body);
    Aws::S3::Model::PutObjectOutcome outcome = client.PutObject(request);
    body->seekg(0, body->beg);
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
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    Aws::String &hashData,
                                    std::vector<Aws::String> *partHashes,
                                    const Aws::S3::S3Client &client) {
    Aws::S3::Model::GetObjectAttributesRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);

    if (hashMethod == MD5) {
        Aws::Vector<Aws::S3::Model::ObjectAttributes> attributes;
        attributes.push_back(Aws::S3::Model::ObjectAttributes::ETag);
        request.SetObjectAttributes(attributes);

        Aws::S3::Model::GetObjectAttributesOutcome outcome = client.GetObjectAttributes(
                request);
        if (outcome.IsSuccess()) {
            const Aws::S3::Model::GetObjectAttributesResult &result = outcome.GetResult();
            hashData = result.GetETag();
        }
        else {
            std::cerr << "Error retrieving object etag attributes." <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
        }
    }
    else { // hashMethod != MD5
        Aws::Vector<Aws::S3::Model::ObjectAttributes> attributes;
        attributes.push_back(Aws::S3::Model::ObjectAttributes::Checksum);
        request.SetObjectAttributes(attributes);

        Aws::S3::Model::GetObjectAttributesOutcome outcome = client.GetObjectAttributes(
                request);
        if (outcome.IsSuccess()) {
            const Aws::S3::Model::GetObjectAttributesResult &result = outcome.GetResult();
            switch (hashMethod) {
                case AwsDoc::S3::MD5:
                    break;  // MD5 is not supported.
                case AwsDoc::S3::SHA1:
                    hashData = result.GetChecksum().GetChecksumSHA1();
                    break;
                case AwsDoc::S3::SHA256:
                    hashData = result.GetChecksum().GetChecksumSHA256();
                    break;
                case AwsDoc::S3::CRC32:
                    hashData = result.GetChecksum().GetChecksumCRC32();
                    break;
                case AwsDoc::S3::CRC32C:
                    hashData = result.GetChecksum().GetChecksumCRC32C();
                    break;
                default:
                    std::cerr << "Unknown hash method." << std::endl;
                    return false;
            }
        }
        else {
            std::cerr << "Error retrieving object checksum attributes." <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
        }
        if (nullptr != partHashes) {
            attributes.clear();
            attributes.push_back(Aws::S3::Model::ObjectAttributes::ObjectParts);
            request.SetObjectAttributes(attributes);
            outcome = client.GetObjectAttributes(request);
            if (outcome.IsSuccess()) {
                const Aws::S3::Model::GetObjectAttributesResult &result = outcome.GetResult();
                const Aws::Vector<Aws::S3::Model::ObjectPart> parts = result.GetObjectParts().GetParts();
                for (const Aws::S3::Model::ObjectPart &part: parts) {
                    switch (hashMethod) {
                        case AwsDoc::S3::MD5: // MD5 is not supported.
                            break;
                        case AwsDoc::S3::SHA1:
                            partHashes->push_back(part.GetChecksumSHA1());
                            break;
                        case AwsDoc::S3::SHA256:
                            partHashes->push_back(part.GetChecksumSHA256());
                            break;
                        case AwsDoc::S3::CRC32:
                            partHashes->push_back(part.GetChecksumCRC32());
                            break;
                        case AwsDoc::S3::CRC32C:
                            partHashes->push_back(part.GetChecksumCRC32C());
                            break;
                        default:
                            std::cerr << "Unknown hash method." << std::endl;
                            return false;
                    }
                }
            }
            else {
                std::cerr << "Error retrieving object attributes for object parts." <<
                          outcome.GetError().GetMessage() << std::endl;
                return false;
            }
        }
    }

    return true;
}

Aws::String AwsDoc::S3::stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod) {
    switch (hashMethod) {
        case AwsDoc::S3::MD5:
            return "MD5";
        case AwsDoc::S3::SHA1:
            return "SHA1";
        case AwsDoc::S3::SHA256:
            return "SHA256";
        case AwsDoc::S3::CRC32:
            return "CRC32";
        case AwsDoc::S3::CRC32C:
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
        AwsDoc::S3::Hasher hash;
        if (hash.calculateObjectHash(outcome.GetResult().GetBody(), hashMethod)) {
            if (hashData == hash.getBase64HashString()) {
                std::cout << "Hashes match." << std::endl;
            }
            else {
                std::cerr << "Hashes do not match." << std::endl;
                result = false;
            }
        }
        Aws::String storedHash;
        switch (hashMethod) {

            case AwsDoc::S3::MD5:
                std::cerr << "MD5 not supported." << std::endl;
                result &= false;
                break;
            case AwsDoc::S3::SHA1:
                storedHash = getObjectResult.GetChecksumSHA1();
                break;
            case AwsDoc::S3::SHA256:
                storedHash = getObjectResult.GetChecksumSHA256();
                break;
            case AwsDoc::S3::CRC32:
                storedHash = getObjectResult.GetChecksumCRC32();
                break;
            case AwsDoc::S3::CRC32C:
                storedHash = getObjectResult.GetChecksumCRC32C();
                break;
            default:
                std::cerr << "Unknown hash method." << std::endl;
                break;
        }

        if (storedHash != hashData) {
            std::cerr << "Hashes do not match." << std::endl;
            result &= false;
        }
    }
    return result;
}

bool AwsDoc::S3::doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                               const Aws::String &key,
                                               AwsDoc::S3::HASH_METHOD hashMethod,
                                               std::shared_ptr<Aws::IOStream> fileStream,
                                               AwsDoc::S3::Hasher &hashDataResult,
                                               std::vector<Aws::String> &partHashes,
                                               const Aws::S3::S3Client &client) {
    // Get object size.
    fileStream->seekg(0, fileStream->end);
    size_t objectSize = fileStream->tellg();
    fileStream->seekg(0, fileStream->beg);

    Aws::S3::Model::CreateMultipartUploadRequest createMultipartUploadRequest;
    createMultipartUploadRequest.SetBucket(bucket);
    createMultipartUploadRequest.SetKey(key);
    switch (hashMethod) { ;
        case AwsDoc::S3::MD5:
            break; // Ignore MD5.
        case AwsDoc::S3::SHA1:
            createMultipartUploadRequest.SetChecksumAlgorithm(
                    Aws::S3::Model::ChecksumAlgorithm::SHA1);
            break;
        case AwsDoc::S3::SHA256:
            createMultipartUploadRequest.SetChecksumAlgorithm(
                    Aws::S3::Model::ChecksumAlgorithm::SHA256);
            break;
        case AwsDoc::S3::CRC32:
            createMultipartUploadRequest.SetChecksumAlgorithm(
                    Aws::S3::Model::ChecksumAlgorithm::CRC32);
            break;
        case AwsDoc::S3::CRC32C:
            createMultipartUploadRequest.SetChecksumAlgorithm(
                    Aws::S3::Model::ChecksumAlgorithm::CRC32C);
            break;
    }

    auto createMultipartUploadOutcome = client.CreateMultipartUpload(
            createMultipartUploadRequest);

    Aws::String uploadID;
    if (createMultipartUploadOutcome.IsSuccess()) {
        uploadID = createMultipartUploadOutcome.GetResult().GetUploadId();
    }
    else {
        std::cerr << "Error creating multipart upload." <<
                  createMultipartUploadOutcome.GetError().GetMessage() << std::endl;
        return false;
    }

    std::vector<unsigned char> totalHashBuffer;
    bool uploadSucceeded = true;
    size_t uploadedBytes = 0;
    int partNumber = 1;
    Aws::S3::Model::CompletedMultipartUpload completedMultipartUpload;
    while (uploadedBytes < objectSize) {
        std::cout << "Uploading part " << partNumber << "." << std::endl;

        Aws::S3::Model::UploadPartRequest uploadPartRequest;
        uploadPartRequest.SetBucket(bucket);
        uploadPartRequest.SetKey(key);
        uploadPartRequest.SetUploadId(uploadID);
        uploadPartRequest.SetPartNumber(partNumber++);

        std::vector<unsigned char> buffer(UPLOAD_BUFFER_SIZE);
        size_t bytesToRead = std::min(buffer.size(), objectSize - uploadedBytes);
        fileStream->read((char *) buffer.data(), bytesToRead);
        Aws::Utils::Stream::PreallocatedStreamBuf preallocatedStreamBuf(buffer.data(),
                                                                        bytesToRead);
        std::shared_ptr<Aws::IOStream> body =
                Aws::MakeShared<Aws::IOStream>("SampleAllocationTag",
                                               &preallocatedStreamBuf);
        Hasher hasher;
        if (!hasher.calculateObjectHash(*body, hashMethod)) {
            std::cerr << "Error calculating hash." << std::endl;
            uploadSucceeded = false;
            break;
        }


        Aws::String base64HashString = hasher.getBase64HashString();
        partHashes.push_back(base64HashString);

        Aws::S3::Model::CompletedPart completedPart;
        switch (hashMethod) {
            case AwsDoc::S3::MD5:
                uploadPartRequest.SetContentMD5(base64HashString);
                break;
            case AwsDoc::S3::SHA1:
                uploadPartRequest.SetChecksumSHA1(base64HashString);
                completedPart.SetChecksumSHA1(base64HashString);
                break;
            case AwsDoc::S3::SHA256:
                uploadPartRequest.SetChecksumSHA256(base64HashString);
                completedPart.SetChecksumSHA256(base64HashString);
                break;
            case AwsDoc::S3::CRC32:
                uploadPartRequest.SetChecksumCRC32(base64HashString);
                completedPart.SetChecksumCRC32(base64HashString);
                break;
            case AwsDoc::S3::CRC32C:
                uploadPartRequest.SetChecksumCRC32C(base64HashString);
                completedPart.SetChecksumCRC32C(base64HashString);
                break;
            default:
                std::cerr << "Unknown hash method." << std::endl;
                uploadSucceeded = false;
                break;
        }
        if (!uploadSucceeded) {
            break;
        }
        Aws::Utils::ByteBuffer hashBuffer = hasher.getByteBufferHash();

        totalHashBuffer.insert(totalHashBuffer.end(), hashBuffer.GetUnderlyingData(),
                               hashBuffer.GetUnderlyingData() + hashBuffer.GetLength());

        uploadPartRequest.SetBody(body);

        auto uploadPartOutcome = client.UploadPart(uploadPartRequest);
        if (uploadPartOutcome.IsSuccess()) {
            const Aws::S3::Model::UploadPartResult &uploadPartResult = uploadPartOutcome.GetResult();
            completedPart.SetETag(uploadPartOutcome.GetResult().GetETag());
            completedPart.SetPartNumber(uploadPartRequest.GetPartNumber());

            completedMultipartUpload.AddParts(completedPart);
        }
        else {
            std::cerr << "Error uploading part. " <<
                      uploadPartOutcome.GetError().GetMessage() << std::endl;
            uploadSucceeded = false;
            break;
        }

        uploadedBytes += bytesToRead;
    }

    if (!uploadSucceeded) {
        Aws::S3::Model::AbortMultipartUploadRequest abortMultipartUploadRequest;
        abortMultipartUploadRequest.SetBucket(bucket);
        abortMultipartUploadRequest.SetKey(key);
        abortMultipartUploadRequest.SetUploadId(uploadID);
        auto abortMultipartUploadOutcome = client.AbortMultipartUpload(
                abortMultipartUploadRequest);
        if (!abortMultipartUploadOutcome.IsSuccess()) {
            std::cerr << "Error aborting multipart upload." <<
                      abortMultipartUploadOutcome.GetError().GetMessage() << std::endl;
        }
        return false;
    }
    else {
        Aws::S3::Model::CompleteMultipartUploadRequest completeMultipartUploadRequest;
        completeMultipartUploadRequest.SetBucket(bucket);
        completeMultipartUploadRequest.SetKey(key);
        completeMultipartUploadRequest.SetUploadId(uploadID);
        completeMultipartUploadRequest.SetMultipartUpload(completedMultipartUpload);
        auto completeMultipartUploadOutcome = client.CompleteMultipartUpload(
                completeMultipartUploadRequest);

        if (completeMultipartUploadOutcome.IsSuccess()) {
            std::cout << "Multipart upload completed." << std::endl;
            hashDataResult.calculateObjectHash(totalHashBuffer, hashMethod);
        }
        else {
            std::cerr << "Error completing multipart upload." <<
                      completeMultipartUploadOutcome.GetError().GetMessage()
                      << std::endl;
        }

        return completeMultipartUploadOutcome.IsSuccess();
    }
}

Aws::S3::Model::ChecksumAlgorithm
AwsDoc::S3::getChecksumAlgorithmForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod) {
    Aws::S3::Model::ChecksumAlgorithm result = Aws::S3::Model::ChecksumAlgorithm::NOT_SET;
    switch (hashMethod){
        case AwsDoc::S3::MD5:
             break; // Ignore MD5.
        case AwsDoc::S3::SHA1:
            result = Aws::S3::Model::ChecksumAlgorithm::SHA1;
            break;
        case AwsDoc::S3::SHA256:
            result = Aws::S3::Model::ChecksumAlgorithm::SHA256;
            break;
        case AwsDoc::S3::CRC32:
            result = Aws::S3::Model::ChecksumAlgorithm::CRC32;
            break;
        case AwsDoc::S3::CRC32C:
            result = Aws::S3::Model::ChecksumAlgorithm::CRC32C;
            break;
        default:
            std::cerr << "Unknown hash method." << std::endl;
            break;

    }

    return result;
}

bool
AwsDoc::S3::doTransferManagerUpload(const Aws::String &bucket, const Aws::String &key,
                                    AwsDoc::S3::HASH_METHOD hashMethod) {
    auto s3_client = Aws::MakeShared<Aws::S3::S3Client>("S3Client");
    auto executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>("executor", 25);
    Aws::Transfer::TransferManagerConfiguration transfer_config(executor.get());
    transfer_config.s3Client = s3_client;
    transfer_config.bufferSize = UPLOAD_BUFFER_SIZE;
    if (hashMethod == MD5)
    {
        transfer_config.computeContentMD5 = true;
    }
    else {
        transfer_config.checksumAlgorithm = getChecksumAlgorithmForHashMethod(hashMethod);
    }

    auto transfer_manager = Aws::Transfer::TransferManager::Create(transfer_config);

    auto uploadHandle = transfer_manager->UploadFile(MULTI_PART_TEST_FILE, bucket, key, "text/plain", Aws::Map<Aws::String, Aws::String>());
    uploadHandle->WaitUntilFinished();
    bool success = uploadHandle->GetStatus() == Aws::Transfer::TransferStatus::COMPLETED;
    if (!success) {
        auto err = uploadHandle->GetLastError();
        std::cout << "File upload failed:  "<< err.GetMessage() << std::endl;
    }

    return success;
}

bool AwsDoc::S3::Hasher::calculateObjectHash(std::vector<unsigned char> &data,
                                             AwsDoc::S3::HASH_METHOD hashMethod) {
    Aws::Utils::Stream::PreallocatedStreamBuf preallocatedStreamBuf(data.data(),
                                                                    data.size());
    std::shared_ptr<Aws::IOStream> body =
            Aws::MakeShared<Aws::IOStream>("SampleAllocationTag",
                                           &preallocatedStreamBuf);
    return calculateObjectHash(*body, hashMethod);
}

bool AwsDoc::S3::Hasher::calculateObjectHash(Aws::IOStream &data,
                                             AwsDoc::S3::HASH_METHOD hashMethod) {
    switch (hashMethod) {
        case AwsDoc::S3::MD5:
            m_Hash = Aws::Utils::HashingUtils::CalculateMD5(data);
            break;
        case AwsDoc::S3::SHA1:
            m_Hash = Aws::Utils::HashingUtils::CalculateSHA1(data);
            break;
        case AwsDoc::S3::SHA256:
            m_Hash = Aws::Utils::HashingUtils::CalculateSHA256(data);
            break;
        case AwsDoc::S3::CRC32:
            m_Hash = Aws::Utils::HashingUtils::CalculateCRC32(data);
            break;
        case AwsDoc::S3::CRC32C:
            m_Hash = Aws::Utils::HashingUtils::CalculateCRC32C(data);
            break;
        default:
            std::cerr << "Unknown hash method." << std::endl;
            return false;
    }
    data.seekg(0, data.beg);
    return true;
}

Aws::String AwsDoc::S3::Hasher::getBase64HashString() {
    return Aws::Utils::HashingUtils::Base64Encode(m_Hash);
}

Aws::String AwsDoc::S3::Hasher::getHexHashString() {
    std::stringstream stringstream;
    stringstream << std::hex << std::setfill('0');
    for (int i = 0; i < m_Hash.GetLength(); ++i) {
        stringstream << std::setw(2) << (int) m_Hash[i];
    }

    return stringstream.str();
}

Aws::Utils::ByteBuffer AwsDoc::S3::Hasher::getByteBufferHash() {
    return m_Hash;
}


