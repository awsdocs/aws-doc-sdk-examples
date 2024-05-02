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


/*
 * TODO: Handle calculating of HASH for TransferManager
 * TODO: Explain that you cannot use your own hash with the TransferManager
 * TODO: Check why MD5 is not working for TransferManager and MultiPart comparisons.
 */
namespace AwsDoc::S3 {
     constexpr char TEST_FILE_KEY[] = "/CMakeLists.txt";
    constexpr char TEST_FILE[] = SRC_DIR"/CMakeLists.txt";
    constexpr char MULTI_PART_TEST_FILE[] = LARGE_FILE_DIR"/s3-userguide.pdf";
    constexpr char TEST_BUCKET_PREFIX[] = "integrity-workflow-";
    constexpr size_t MAX_BUCKET_NAME_LENGTH = 63;
    const size_t BUFFER_SIZE_IN_MEGABYTES = 5;
    const size_t UPLOAD_BUFFER_SIZE = BUFFER_SIZE_IN_MEGABYTES * 1024 * 1024;

    static bool gUseCalculatedChecksum = false;

    typedef int HASH_METHOD;

    static const HASH_METHOD DEFAULT = 1;
    static const HASH_METHOD MD5 = DEFAULT + 1;
    static const HASH_METHOD CRC32 = MD5 + 1;
    static const HASH_METHOD CRC32C = CRC32 + 1;
    static const HASH_METHOD SHA1 = CRC32C + 1;
    static const HASH_METHOD SHA256 = SHA1 + 1;

    class Hasher {
        Aws::Utils::ByteBuffer m_Hash;

    public:
        Hasher() = default;

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
                           bool useDefaultHashMethod,
                           const Aws::S3::S3Client &client);

    bool retrieveObjectHash(const Aws::String &bucket, const Aws::String &key,
                            AwsDoc::S3::HASH_METHOD hashMethod,
                            Aws::String &hashData,
                            std::vector<Aws::String> *partHashes,
                            const Aws::S3::S3Client &client);

    bool doTransferManagerUpload(const Aws::String &bucket, const Aws::String &key,
                                 AwsDoc::S3::HASH_METHOD hashMethod,
                                 bool useDefaultHashMethod,
                                 const std::shared_ptr<Aws::S3::S3Client> &client);

    bool calculatePartHashesForFile(AwsDoc::S3::HASH_METHOD hashMethod,
                                    const Aws::String &fileName,
                                    size_t bufferSize,
                                    AwsDoc::S3::Hasher &hashDataResult,
                                    std::vector<Aws::String> &partHashes);

    bool doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                       const Aws::String &key,
                                       AwsDoc::S3::HASH_METHOD hashMethod,
                                       const std::shared_ptr<Aws::IOStream> &fileStream,
                                       bool useDefaultHashMethod,
                                       AwsDoc::S3::Hasher &hashDataResult,
                                       std::vector<Aws::String> &partHashes,
                                       const Aws::S3::S3Client &client);

    Aws::String stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

    Aws::S3::Model::ChecksumAlgorithm
    getChecksumAlgorithmForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

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

    std::cout
            << "Welcome to the Amazon Simple Storage Service (Amazon S3) object integrity\n";
    std::cout << "workflow." << std::endl;
    std::cout
            << "This workflow demonstrates how Amazon S3 uses checksum values to verify the integrity of data\n";
    std::cout << "uploaded to Amazon S3 buckets" << std::endl;
    std::cout
            << "The AWS SDK for C++ automatically handles checksums. By default the SDK computes an MD5 checksum which\n";
    std::cout
            << "is stored as an etag. (Amazon S3 Express One Zone buckets use a different checksum algorithm)"
            << std::endl;
    std::cout
            << "You can specify one of 4 other checksums to use for object integrity verification,\n";
    std::cout << "CRC32, CRC32C, SHA-1 or SHA-256." << std::endl;
    std::cout
            << "For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html."
            << std::endl;

    std::cout
            << "This workflow will locally compute checksums for files uploaded to an Amazon S3 bucket,\n";
    std::cout << "even when the SDK also computes the checksum." << std::endl;
    std::cout
            << "This is done to provide demonstration code for how the checksums are calculated."
            << std::endl;

    std::cout << "Now to begin..." << std::endl;

    Aws::S3::S3ClientConfiguration s3ClientConfiguration(clientConfiguration);
    std::shared_ptr<Aws::S3::S3Client> client = Aws::MakeShared<Aws::S3::S3Client>("S3Client", s3ClientConfiguration);
    do {
        std::cout << "Choose from one of the following checksum algorithms."
                  << std::endl;

        for (HASH_METHOD hashMethod = DEFAULT; hashMethod <= SHA256; ++hashMethod) {
            std::cout << "  " << hashMethod << " - " << stringForHashMethod(hashMethod)
                      << std::endl;
        }

        HASH_METHOD chosenHashMethod = askQuestionForIntRange("Enter an index: ", DEFAULT,
                                                              SHA256);


        gUseCalculatedChecksum = !askYesNoQuestion(
                "Let the SDK calculate the checksum for you? (y/n) ");

        printAsterisksLine();

        std::cout << "The workflow will now upload a file using PutObject."
                  << std::endl;
        std::cout << "Object integrity will be verified using the "
                  << stringForHashMethod(chosenHashMethod) << " algorithm."
                  << std::endl;
        if (gUseCalculatedChecksum) {
            std::cout
                    << "A checksum computed by this workflow will be used for object integrity verification."
                    << std::endl;
        }
        else {
            std::cout
                    << "A checksum computed by the SDK will be used for object integrity verification."
                    << std::endl;
        }

        askQuestion("Press any key to continue...", alwaysTrueTest);

        std::shared_ptr<Aws::IOStream> inputData =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              TEST_FILE,
                                              std::ios_base::in |
                                              std::ios_base::binary);

        if (!*inputData) {
            std::cerr << "Error unable to read file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Hasher hasher;
        HASH_METHOD putObjectHashMethod = chosenHashMethod;
        if (putObjectHashMethod == DEFAULT) {
            putObjectHashMethod = MD5;

            std::cout << "The default checksum algorithm for PutObject is "
                      << stringForHashMethod(putObjectHashMethod)
                      << std::endl;
        }

        if (!hasher.calculateObjectHash(*inputData, putObjectHashMethod)) {
            std::cerr << "Error calculating hash for file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }
        Aws::String key = stringForHashMethod(putObjectHashMethod);
        key += "_";
        key += TEST_FILE_KEY;
        Aws::String localHash = hasher.getBase64HashString();
        if (!putObjectWithHash(bucketName, key, localHash, putObjectHashMethod,
                               inputData, chosenHashMethod == DEFAULT,
                               *client)) {
            std::cerr << "Error putting file " << TEST_FILE << " to bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        Aws::String retrievedHash;
        if (!retrieveObjectHash(bucketName, key,
                                putObjectHashMethod, retrievedHash,
                                nullptr, *client)) {
            std::cerr << "Error getting file " << TEST_FILE << " from bucket "
                      << bucketName << " with key " << key << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        bool checksumsMatch = true;
        if (putObjectHashMethod == MD5) {
            Aws::String hashAsHex = hasher.getHexHashString();
            if (hashAsHex != retrievedHash) {
                std::cerr << "Error retrieved etag " << retrievedHash
                          << " does not match " << hashAsHex << std::endl;
                checksumsMatch = false;
            }
        }
        else {
            if (retrievedHash != localHash) {
                std::cerr << "Error retrieved hash " << retrievedHash
                          << " does not match " << localHash << std::endl;
                checksumsMatch = false;
            }
        }

        std::cout << "The upload was successful.\n ";
        std::cout << "If the checksums had not matched, the upload would have failed."
                  << std::endl;
        std::cout
                << "The checksums calculated by the server have been retrieved using the GetObjectAttributes."
                << std::endl;
        std::cout
                << "The locally calculated checksums have been verified against the retrieved checksums."
                << std::endl;
        if (checksumsMatch) {
            std::cout << "Everything matches!" << std::endl;
        }
        else {
            std::cout
                    << "The checksums did not match. This should not happen. Something is wrong with the code."
                    << std::endl;
        }
        askQuestion("Press any key to continue...", alwaysTrueTest);

        printAsterisksLine();

        std::cout
                << "Now the workflow will demonstrate object integrity for multi-part uploads."
                << std::endl;
        std::cout
                << "The AWS C++ SDK has a TransferManager which simplifies multipart uploads."
                << std::endl;
        std::cout
                << "The following code lets the TransferManager handle much of the checksum configuration."
                << std::endl;
        key = "tr_";
        key += stringForHashMethod(chosenHashMethod) + "-s3-userguide.pdf";

        std::cout << "An object with the key '" << key
                  << ", will be uploaded by the TransferManager using a "
                  << BUFFER_SIZE_IN_MEGABYTES << " MB buffer." << std::endl;


        askQuestion("Press any key to continue...", alwaysTrueTest);
        HASH_METHOD transferManagerHashMethod = chosenHashMethod;
        if (transferManagerHashMethod == DEFAULT) {
            transferManagerHashMethod = CRC32;

            std::cout << "The default checksum algorithm for TransferManager is "
                      << stringForHashMethod(transferManagerHashMethod)
                      << std::endl;
        }

        if (!doTransferManagerUpload(bucketName, key, transferManagerHashMethod, chosenHashMethod == DEFAULT,
                                     client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }


        std::vector<Aws::String> retrievedTransferManagerPartHashes;
        Aws::String retrievedTransferManagerFinalHash;

        if (!retrieveObjectHash(bucketName, key,
                                transferManagerHashMethod,
                                retrievedTransferManagerFinalHash,
                                &retrievedTransferManagerPartHashes, *client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        AwsDoc::S3::Hasher locallyCalculatedFinalHash;
        std::vector<Aws::String> locallyCalculatedPartHashes;

        if (!calculatePartHashesForFile(transferManagerHashMethod, MULTI_PART_TEST_FILE,
                                        UPLOAD_BUFFER_SIZE,
                                        locallyCalculatedFinalHash,
                                        locallyCalculatedPartHashes)){
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }
        Aws::String hashString;
        if (transferManagerHashMethod == MD5) {
            hashString = locallyCalculatedFinalHash.getHexHashString();
            hashString += "-" + std::to_string(locallyCalculatedPartHashes.size());
        }
        else {
            hashString = locallyCalculatedFinalHash.getBase64HashString();
        }
        bool allMatch = true;
        if (hashString != retrievedTransferManagerFinalHash) {
            std::cerr << "The main hashes do not match" << std::endl;
            std::cerr << "Local hash- '" << hashString << "'" << std::endl;
            std::cerr << "Remote hash - '" << retrievedTransferManagerFinalHash << "'" << std::endl;
            allMatch = false;
        }

        if (transferManagerHashMethod != MD5) {
            if (locallyCalculatedPartHashes.size() != retrievedTransferManagerPartHashes.size()) {
                std::cerr << "The number of part hashes do not match" << std::endl;
                std::cerr << "Local number of hashes- '" << locallyCalculatedPartHashes.size() << "'"
                          << std::endl;
                std::cerr << "Remote number of hashes - '"
                          << retrievedTransferManagerPartHashes.size()
                          << "'" << std::endl;
                continue;
            }

            for (int i = 0; i < locallyCalculatedPartHashes.size(); ++i) {
                if (locallyCalculatedPartHashes[i] != retrievedTransferManagerPartHashes[i]) {
                    std::cerr << "The part hashes do not match for part " << i + 1
                              << "." << std::endl;
                    std::cerr << "Local hash- '" << locallyCalculatedPartHashes[i] << "'"
                              << std::endl;
                    std::cerr << "Remote hash - '" << retrievedTransferManagerPartHashes[i] << "'"
                              << std::endl;
                    allMatch = false;
                }
            }
        }

        if (allMatch) {
            std::cout << "Locally and remotely calculated hashes all match!" << std::endl;
        }

        std::cout
                << "Now we will provide an in-depth demonstration of multi-part uploading by calling the multi-part upload APIs directly."
                << std::endl;
        std::cout << "These are the same APIs used by the TransferManager."
                  << std::endl;
        std::cout
                << "In the following code, the checksums are also calculated locally and then compared."
                << std::endl;
        std::cout
                << "For multi-part uploads, a checksum is uploaded with each part. The final checksum is a concatenation of"
                << std::endl;
        std::cout << "the checksums for each part." << std::endl;
        std::cout
                << "This is explained in the user guide in the section \"Using part-level checksums for multipart uploads\"."
                << std::endl;

        std::shared_ptr<Aws::IOStream> largeFileInputData =
                Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                              MULTI_PART_TEST_FILE,
                                              std::ios_base::in |
                                              std::ios_base::binary);

        if (!largeFileInputData->good()) {
            std::cerr << "Error unable to read file " << TEST_FILE << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        key = "mp_";
        key += stringForHashMethod(chosenHashMethod);
        key += "-s3-userguide.pdf";
        std::cout << "Starting multipart upload of with hash method " <<
                  stringForHashMethod(chosenHashMethod) << std::endl;
        HASH_METHOD multipartUploadHashMethod = chosenHashMethod;
        if (multipartUploadHashMethod == DEFAULT) {
            multipartUploadHashMethod = MD5;

            std::cout << "The default checksum algorithm for PutObject is "
                      << stringForHashMethod(putObjectHashMethod)
                      << std::endl;
        }
        AwsDoc::S3::Hasher hashData;
        std::vector<Aws::String> partHashes;

        if (!doMultipartUploadAndCheckHash(bucketName, key,
                                           multipartUploadHashMethod,
                                           largeFileInputData, chosenHashMethod == DEFAULT,
                                           hashData,
                                           partHashes,
                                           *client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        std::cout << "Finished multipart upload of with hash method " <<
                  stringForHashMethod(multipartUploadHashMethod) << std::endl;

        std::cout << "Now we will retrieve the checksums from the server." << std::endl;

        retrievedHash.clear();
        std::vector<Aws::String> retrievedPartHashes;
        if (!retrieveObjectHash(bucketName, key,
                                multipartUploadHashMethod,
                                retrievedHash, &retrievedPartHashes, *client)) {
            std::cerr << "Exiting because of an error" << std::endl;
            cleanUp(bucketName, clientConfiguration);
            return false;
        }

        std::cout << "Retrieved hash " << retrievedHash << std::endl;
        std::cout << retrievedPartHashes.size() << " part hash(es) were also retrieved."
                  << std::endl;
        for (auto &retrievedPartHash: retrievedPartHashes) {
            std::cout << "  Part hash " << retrievedPartHash << std::endl;
        }

        if (multipartUploadHashMethod == MD5) {
            hashString = hashData.getHexHashString();
            hashString += "-" + std::to_string(partHashes.size());
        }
        else {
            hashString = hashData.getBase64HashString();
        }

        allMatch = true;
        if (hashString != retrievedHash) {
            std::cerr << "The main hashes do not match" << std::endl;
            std::cerr << "Local hash- '" << hashString << "'" << std::endl;
            std::cerr << "Remote hash - '" << retrievedHash << "'" << std::endl;
            allMatch = false;
        }

        if (multipartUploadHashMethod != MD5) {
            if (partHashes.size() != retrievedPartHashes.size()) {
                std::cerr << "The number of part hashes do not match" << std::endl;
                std::cerr << "Local number of hashes- '" << partHashes.size() << "'"
                          << std::endl;
                std::cerr << "Remote number of hashes - '"
                          << retrievedPartHashes.size()
                          << "'" << std::endl;
                continue;
            }

            for (int i = 0; i < partHashes.size(); ++i) {
                if (partHashes[i] != retrievedPartHashes[i]) {
                    std::cerr << "The part hashes do not match for part " << i + 1
                              << "." << std::endl;
                    std::cerr << "Local hash- '" << partHashes[i] << "'"
                              << std::endl;
                    std::cerr << "Remote hash - '" << retrievedPartHashes[i] << "'"
                              << std::endl;
                    allMatch = false;
                }
            }
        }

        if (allMatch) {
            std::cout << "Locally and remotely calculated hashes all match!" << std::endl;
        }

        askQuestion("Press any key to continue...", alwaysTrueTest);

        if (allMatch) {
            std::cout << "All the local hashes match the remote hashes."
                      << std::endl;
        }
    } while (askYesNoQuestion(
            "Would you like to repeat with a different checksum algorithm? "));

    return cleanUp(bucketName, clientConfiguration);
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
                                   bool useDefaultHashMethod,
                                   const Aws::S3::S3Client &client) {
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    if (!useDefaultHashMethod)
    {
        if (hashMethod != MD5) {
            request.SetChecksumAlgorithm(getChecksumAlgorithmForHashMethod(hashMethod));
        }
    }

    if (gUseCalculatedChecksum) {
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
                case AwsDoc::S3::DEFAULT:
                    break;  // Default is not supported.
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
                        case AwsDoc::S3::DEFAULT: // Default is not supported.
                            break;
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
        case AwsDoc::S3::DEFAULT:
            return "Default";
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

bool AwsDoc::S3::doMultipartUploadAndCheckHash(const Aws::String &bucket,
                                               const Aws::String &key,
                                               AwsDoc::S3::HASH_METHOD hashMethod,
                                               const std::shared_ptr<Aws::IOStream> &fileStream,
                                               bool useDefaultHashMethod,
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
    if (!useDefaultHashMethod) {
        if (hashMethod != MD5) {
            createMultipartUploadRequest.SetChecksumAlgorithm(
                    getChecksumAlgorithmForHashMethod(hashMethod));
        }
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
    std::streamsize uploadedBytes = 0;
    int partNumber = 1;
    Aws::S3::Model::CompletedMultipartUpload completedMultipartUpload;
    while (uploadedBytes < objectSize) {
        std::cout << "Uploading part " << partNumber << "." << std::endl;

        Aws::S3::Model::UploadPartRequest uploadPartRequest;
        uploadPartRequest.SetBucket(bucket);
        uploadPartRequest.SetKey(key);
        uploadPartRequest.SetUploadId(uploadID);
        uploadPartRequest.SetPartNumber(partNumber++);
        if (!useDefaultHashMethod) {
            if (hashMethod != MD5) {
                uploadPartRequest.SetChecksumAlgorithm(
                        getChecksumAlgorithmForHashMethod(hashMethod));
            }
        }

        std::vector<unsigned char> buffer(UPLOAD_BUFFER_SIZE);
        std::streamsize bytesToRead = static_cast<std::streamsize>(std::min(buffer.size(), objectSize - uploadedBytes));
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
        if (gUseCalculatedChecksum) {
            switch (hashMethod) {
                case AwsDoc::S3::MD5:
                    uploadPartRequest.SetContentMD5(base64HashString);
                    break;
                case AwsDoc::S3::SHA1:
                    uploadPartRequest.SetChecksumSHA1(base64HashString);
                    break;
                case AwsDoc::S3::SHA256:
                    uploadPartRequest.SetChecksumSHA256(base64HashString);
                     break;
                case AwsDoc::S3::CRC32:
                    uploadPartRequest.SetChecksumCRC32(base64HashString);
                    break;
                case AwsDoc::S3::CRC32C:
                    uploadPartRequest.SetChecksumCRC32C(base64HashString);
                    break;
                default:
                    std::cerr << "Unhandled hash method for uploadPartRequest." << std::endl;
                    uploadSucceeded = false;
                    break;
            }
            if (!uploadSucceeded) {
                break;
            }
        }
        Aws::Utils::ByteBuffer hashBuffer = hasher.getByteBufferHash();

        totalHashBuffer.insert(totalHashBuffer.end(), hashBuffer.GetUnderlyingData(),
                               hashBuffer.GetUnderlyingData() + hashBuffer.GetLength());

        uploadPartRequest.SetBody(body);

        auto uploadPartOutcome = client.UploadPart(uploadPartRequest);
        if (uploadPartOutcome.IsSuccess()) {
            const Aws::S3::Model::UploadPartResult &uploadPartResult = uploadPartOutcome.GetResult();
            completedPart.SetETag(uploadPartResult.GetETag());
            completedPart.SetPartNumber(uploadPartRequest.GetPartNumber());
            switch (hashMethod) {
                case AwsDoc::S3::MD5:
                     break; // Do nothing.
                case AwsDoc::S3::SHA1:
                    completedPart.SetChecksumSHA1(uploadPartResult.GetChecksumSHA1());
                    break;
                case AwsDoc::S3::SHA256:
                    completedPart.SetChecksumSHA256(uploadPartResult.GetChecksumSHA256());
                    break;
                case AwsDoc::S3::CRC32:
                    completedPart.SetChecksumCRC32(uploadPartResult.GetChecksumCRC32());
                    break;
                case AwsDoc::S3::CRC32C:
                    completedPart.SetChecksumCRC32C(uploadPartResult.GetChecksumCRC32C());
                    break;
                default:
                    std::cerr << "Unhandled hash method for completedPart." << std::endl;
                    break;
            }

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
            if (!hashDataResult.calculateObjectHash(totalHashBuffer, hashMethod))
            {
                std::cerr << "Error calculating hash." << std::endl;
                return false;
            }
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
    switch (hashMethod) {
        case AwsDoc::S3::DEFAULT:
            std::cerr << "getChecksumAlgorithmForHashMethod- DEFAULT is not valid." << std::endl;
            break;  // Default is not supported.
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
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    bool useDefaultHashMethod,
                                    const std::shared_ptr<Aws::S3::S3Client> &client) {
    auto executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>(
            "executor", 25);
    Aws::Transfer::TransferManagerConfiguration transfer_config(executor.get());
    transfer_config.s3Client = client;
    transfer_config.bufferSize = UPLOAD_BUFFER_SIZE;
    if (!useDefaultHashMethod) {
        if (hashMethod == MD5) {
            transfer_config.computeContentMD5 = true;
        }
        else  {
            transfer_config.checksumAlgorithm = getChecksumAlgorithmForHashMethod(
                    hashMethod);
        }
    }

    auto transfer_manager = Aws::Transfer::TransferManager::Create(transfer_config);

    auto uploadHandle = transfer_manager->UploadFile(MULTI_PART_TEST_FILE, bucket, key,
                                                     "text/plain",
                                                     Aws::Map<Aws::String, Aws::String>());
    uploadHandle->WaitUntilFinished();
    bool success =
            uploadHandle->GetStatus() == Aws::Transfer::TransferStatus::COMPLETED;
    if (!success) {
        auto err = uploadHandle->GetLastError();
        std::cout << "File upload failed:  " << err.GetMessage() << std::endl;
    }

    return success;
}

bool AwsDoc::S3::calculatePartHashesForFile(AwsDoc::S3::HASH_METHOD hashMethod,
                                            const Aws::String &fileName,
                                            size_t bufferSize,
                                            AwsDoc::S3::Hasher &hashDataResult,
                                            std::vector<Aws::String> &partHashes) {
    std::ifstream fileStream(fileName.c_str(), std::ifstream::binary);
    fileStream.seekg(0, std::ifstream::end);
    size_t objectSize = fileStream.tellg();
    fileStream.seekg(0, std::ifstream::beg);
    std::vector<unsigned char> totalHashBuffer;
    size_t uploadedBytes = 0;


    while (uploadedBytes < objectSize) {
        std::vector<unsigned char> buffer(bufferSize);
        std::streamsize bytesToRead = static_cast<std::streamsize>(std::min(buffer.size(), objectSize - uploadedBytes));
        fileStream.read((char *) buffer.data(), bytesToRead);
        Aws::Utils::Stream::PreallocatedStreamBuf preallocatedStreamBuf(buffer.data(),
                                                                        bytesToRead);
        std::shared_ptr<Aws::IOStream> body =
                Aws::MakeShared<Aws::IOStream>("SampleAllocationTag",
                                               &preallocatedStreamBuf);
        Hasher hasher;
        if (!hasher.calculateObjectHash(*body, hashMethod)) {
            std::cerr << "Error calculating hash." << std::endl;
            return false;
        }
        Aws::String base64HashString = hasher.getBase64HashString();
        partHashes.push_back(base64HashString);

        Aws::Utils::ByteBuffer hashBuffer = hasher.getByteBufferHash();

        totalHashBuffer.insert(totalHashBuffer.end(), hashBuffer.GetUnderlyingData(),
                               hashBuffer.GetUnderlyingData() + hashBuffer.GetLength());

        uploadedBytes += bytesToRead;
    }

    return hashDataResult.calculateObjectHash(totalHashBuffer, hashMethod);
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
        case AwsDoc::S3::DEFAULT:
            std::cerr << "Default hash method in calculateObjectHash." << std::endl;
            break;
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
    data.seekg(0, std::ifstream::beg);
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


