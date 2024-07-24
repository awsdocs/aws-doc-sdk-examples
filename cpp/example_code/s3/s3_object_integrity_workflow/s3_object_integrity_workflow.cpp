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
#include <filesystem>
#include "s3_examples.h"


#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
namespace AwsDoc::S3 {
    constexpr char TEST_FILE_KEY[] = "test_file.cpp";
    constexpr char TEST_FILE[] = __FILE__;  // Use this source file as the test file.
    constexpr char MULTI_PART_TEST_FILE[] = "large_test_file.cpp"; // Large file created for multipart upload tests.
    constexpr char TEST_BUCKET_PREFIX[] = "integrity-workflow-";
    constexpr size_t MAX_BUCKET_NAME_LENGTH = 63;
    const size_t BUFFER_SIZE_IN_MEGABYTES = 5;
    const size_t UPLOAD_BUFFER_SIZE = BUFFER_SIZE_IN_MEGABYTES * 1024 * 1024;

    const size_t LARGE_FILE_SIZE = 3 * UPLOAD_BUFFER_SIZE;

    static bool gUseCalculatedChecksum = false;

    typedef int HASH_METHOD;

    static const HASH_METHOD DEFAULT = 1;
    static const HASH_METHOD MD5 = DEFAULT + 1;
    static const HASH_METHOD CRC32 = MD5 + 1;
    static const HASH_METHOD CRC32C = CRC32 + 1;
    static const HASH_METHOD SHA1 = CRC32C + 1;
    static const HASH_METHOD SHA256 = SHA1 + 1;

    //! A class that provides functionality for calculating hash values of data.
    /*!
       The `Hasher` class is responsible for calculating hash values of data using various hashing algorithms, such as MD5, CRC32, CRC32C, SHA1, and SHA256.
       It provides methods to calculate the hash value of data stored in a `std::vector<unsigned char>` or an `Aws::IOStream`, and to retrieve the
       hash value in different formats (base64, hex, and byte buffer).

       The class maintains an internal `Aws::Utils::ByteBuffer` to store the calculated hash value. The `calculateObjectHash` methods update
       this internal buffer with the calculated hash value.

       The supported hashing algorithms are defined as static constants within the class:
       - `MD5`: The default hashing algorithm.
       - `CRC32`: The CRC32 hashing algorithm.
       - `CRC32C`: The CRC32C hashing algorithm.
       - `SHA1`: The SHA1 hashing algorithm.
       - `SHA256`: The SHA256 hashing algorithm.
    */
    class Hasher {
        Aws::Utils::ByteBuffer m_Hash;

    public:
        Hasher() = default;

        //! Calculate the object hash for vector input.
        /*!
           \param data: A vector of unsigned bytes.
           \param hashMethod: The hash method to use.
           \return bool: Function succeeded.
        */
        bool calculateObjectHash(std::vector<unsigned char> &data,
                                 HASH_METHOD hashMethod);

        //! Calculate the object hash for stream input.
        /*!
           \param data: An IOStream for input.
           \param hashMethod: The hash method to use.
           \return bool: Function succeeded.
        */
        bool calculateObjectHash(Aws::IOStream &data,
                                 HASH_METHOD hashMethod);

        //! Retrieve the stored hash as a Base64 string.
        /*!
           \return String: Hash as Base64 string.
        */
        [[nodiscard]] Aws::String getBase64HashString() const;

        //! Retrieve the stored hash as a hexadecimal string.
        /*!
           \return String: Hash as hexadecimal string.
        */
        [[nodiscard]] Aws::String getHexHashString() const;

        //! Retrieve the stored hash as a ByteBuffer.
        /*!
           \return String: Hash as ByteBuffer.
        */
        [[nodiscard]] Aws::Utils::ByteBuffer getByteBufferHash() const;
    };

    //! Routine which uploads an object to an S3 bucket with different object integrity hashing methods.
    /*!
       \param bucket: The name of the S3 bucket where the object will be uploaded.
       \param key: The unique identifier (key) for the object within the S3 bucket.
       \param hashData: The hash value that will be associated with the uploaded object.
       \param hashMethod: The hashing algorithm to use when calculating the hash value.
       \param body: The data content of the object being uploaded.
       \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
       \param client: The S3 client instance used to perform the upload operation.
       \return bool: Function succeeded.
    */
    bool putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                           const Aws::String &hashData,
                           AwsDoc::S3::HASH_METHOD hashMethod,
                           const std::shared_ptr<Aws::IOStream> &body,
                           bool useDefaultHashMethod,
                           const Aws::S3::S3Client &client);

    //! Routine which retrieves the hash value of an object stored in an S3 bucket.
    /*!
       \param bucket: The name of the S3 bucket where the object is stored.
       \param key: The unique identifier (key) of the object within the S3 bucket.
       \param hashMethod: The hashing algorithm used to calculate the hash value of the object.
       \param client: The S3 client instance used to retrieve the object.
       \param[out] hashValue: The retrieved hash value of the object.
       \return bool: Function succeeded.
    */
    bool retrieveObjectHash(const Aws::String &bucket, const Aws::String &key,
                            AwsDoc::S3::HASH_METHOD hashMethod,
                            Aws::String &hashData,
                            std::vector<Aws::String> *partHashes,
                            const Aws::S3::S3Client &client);

    //! Verifies the hashing results between the retrieved and local hashes.
    /*!
     \param retrievedHash The hash value retrieved from the remote source.
     \param localHash The hash value calculated locally.
     \param uploadtype The type of upload (e.g., "multipart", "single-part").
     \param hashMethod The hashing method used (e.g., MD5, SHA-256).
     \param retrievedPartHashes (Optional) The list of hashes for the individual parts retrieved from the remote source.
     \param localPartHashes (Optional) The list of hashes for the individual parts calculated locally.
     */
    void verifyHashingResults(const Aws::String &retrievedHash, const Hasher &localHash,
                              const Aws::String &uploadtype, HASH_METHOD hashMethod,
                              const std::vector<Aws::String> &retrievedPartHashes = std::vector<Aws::String>(),
                              const std::vector<Aws::String> &localPartHashes = std::vector<Aws::String>());

    //! Routine which uploads an object to an S3 bucket using the AWS C++ SDK's Transfer Manager.
    /*!
       \param bucket: The name of the S3 bucket where the object will be uploaded.
       \param key: The unique identifier (key) for the object within the S3 bucket.
       \param hashMethod: The hashing algorithm to use when calculating the hash value.
       \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
       \param client: The S3 client instance used to perform the upload operation.
       \return bool: Function succeeded.
    */
    bool doTransferManagerUpload(const Aws::String &bucket, const Aws::String &key,
                                 AwsDoc::S3::HASH_METHOD hashMethod,
                                 bool useDefaultHashMethod,
                                 const std::shared_ptr<Aws::S3::S3Client> &client);

    //! Routine which calculates the hash values for each part of a file being uploaded to an S3 bucket.
    /*!
       \param hashMethod: The hashing algorithm to use when calculating the hash values.
       \param fileName: The path to the file for which the part hashes will be calculated.
       \param bufferSize: The size of the buffer to use when reading the file.
       \param[out] hashDataResult: The Hasher object that will store the concatenated hash value.
       \param[out] partHashes: The vector that will store the calculated hash values for each part of the file.
       \return bool: Function succeeded.
    */
    bool calculatePartHashesForFile(AwsDoc::S3::HASH_METHOD hashMethod,
                                    const Aws::String &fileName,
                                    size_t bufferSize,
                                    AwsDoc::S3::Hasher &hashDataResult,
                                    std::vector<Aws::String> &partHashes);

    //! Create a multipart upload.
    /*!
        \param bucket: The name of the S3 bucket where the object will be uploaded.
        \param key: The unique identifier (key) for the object within the S3 bucket.
         \param client: The S3 client instance used to perform the upload operation.
        \return Aws::String: Upload ID or empty string if failed.
    */
    Aws::String
    createMultipartUpload(const Aws::String &bucket, const Aws::String &key,
                          Aws::S3::Model::ChecksumAlgorithm checksumAlgorithm,
                          const Aws::S3::S3Client &client);

    //! Upload a part to an S3 bucket.
    /*!
        \param bucket: The name of the S3 bucket where the object will be uploaded.
        \param key: The unique identifier (key) for the object within the S3 bucket.
        \param uploadID: An upload ID string.
        \param partNumber:
        \param checksumAlgorithm: Checksum algorithm, ignored when NOT_SET.
        \param calculatedHash: A data integrity hash to set, depending on the checksum algorithm,
                                ignored when it is an empty string.
        \param body: An shared_ptr IOStream of the data to be uploaded.
        \param client: The S3 client instance used to perform the upload operation.
        \return UploadPartOutcome: The outcome.
    */
    Aws::S3::Model::UploadPartOutcome uploadPart(const Aws::String &bucket,
                                                 const Aws::String &key,
                                                 const Aws::String &uploadID,
                                                 int partNumber,
                                                 Aws::S3::Model::ChecksumAlgorithm checksumAlgorithm,
                                                 const Aws::String &calculatedHash,
                                                 const std::shared_ptr<Aws::IOStream> &body,
                                                 const Aws::S3::S3Client &client);

    //! Abort a multipart upload to an S3 bucket.
    /*!
        \param bucket: The name of the S3 bucket where the object will be uploaded.
        \param key: The unique identifier (key) for the object within the S3 bucket.
        \param uploadID: An upload ID string.
        \param client: The S3 client instance used to perform the upload operation.
        \return bool: Function succeeded.
    */

    bool abortMultipartUpload(const Aws::String &bucket,
                              const Aws::String &key,
                              const Aws::String &uploadID,
                              const Aws::S3::S3Client &client);

    //! Complete a multipart upload to an S3 bucket.
    /*!
        \param bucket: The name of the S3 bucket where the object will be uploaded.
        \param key: The unique identifier (key) for the object within the S3 bucket.
        \param uploadID: An upload ID string.
        \param parts: A vector of CompleteParts.
        \param client: The S3 client instance used to perform the upload operation.
        \return CompleteMultipartUploadOutcome: The request outcome.
    */
    Aws::S3::Model::CompleteMultipartUploadOutcome completeMultipartUpload(const Aws::String &bucket,
                                                                           const Aws::String &key,
                                                                           const Aws::String &uploadID,
                                                                           const Aws::Vector<Aws::S3::Model::CompletedPart> &parts,
                                                                           const Aws::S3::S3Client &client);

    //! Routine which performs a multi-part upload.
    /*!
        \param bucket: The name of the S3 bucket where the object will be uploaded.
        \param key: The unique identifier (key) for the object within the S3 bucket.
        \param hashMethod: The hashing algorithm to use when calculating the hash value.
        \param ioStream: An IOStream for the data to be uploaded.
        \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
        \param[out] hashDataResult: The Hasher object that will store the concatenated hash value.
        \param[out] partHashes: The vector that will store the calculated hash values for each part of the file.
        \param client: The S3 client instance used to perform the upload operation.
        \return bool: Function succeeded.
    */
    bool doMultipartUpload(const Aws::String &bucket,
                           const Aws::String &key,
                           AwsDoc::S3::HASH_METHOD hashMethod,
                           const std::shared_ptr<Aws::IOStream> &ioStream,
                           bool useDefaultHashMethod,
                           AwsDoc::S3::Hasher &hashDataResult,
                           std::vector<Aws::String> &partHashes,
                           const Aws::S3::S3Client &client);

    //! Routine which retrieves the string for a HASH_METHOD constant.
    /*!
        \param: hashMethod: A HASH_METHOD constant.
        \return: String: A string description of the hash method.
    */
    Aws::String stringForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

    //! Routine that returns the ChecksumAlgorithm for a HASH_METHOD constant.
    /*!
        \param: hashMethod: A HASH_METHOD constant.
        \return: ChecksumAlgorithm: The ChecksumAlgorithm enum.
    */
    Aws::S3::Model::ChecksumAlgorithm
    getChecksumAlgorithmForHashMethod(AwsDoc::S3::HASH_METHOD hashMethod);

    //! Routine which cleans up after the example is complete.
    /*!
        \param bucket: The name of the S3 bucket where the object was uploaded.
        \param clientConfiguration: The client configuration for the S3 client.
        \return bool: Function succeeded.
    */
    bool cleanUp(const Aws::String &bucket,
                 const Aws::S3::S3ClientConfiguration &clientConfiguration);

    //! Console interaction introducing the workflow.
    /*!
      \param bucketName: The name of the S3 bucket to use.
    */
    void introductoryExplanations(const Aws::String &bucketName);

    //! Console interaction which explains the PutObject results.
    /*!
    */
    void explainPutObjectResults();

    //! Console interaction explaining transfer manager uploads.
    /*!
      \param objectKey: The key for the object being uploaded.
    */
    void introductoryTransferManagerUploadExplanations(const Aws::String &objectKey);

    //! Console interaction explaining multi-part uploads.
    /*!
      \param objectKey: The key for the object being uploaded.
      \param chosenHashMethod: The hash method selected by the user.
    */
    void multiPartUploadExplanations(const Aws::String &objectKey,
                                     HASH_METHOD chosenHashMethod);

    //! Create a large file for doing multi-part uploads.
    /*!
    */
    bool createLargeFileIfNotExists();

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

    void pressEnterToContinue() {
        askQuestion("Press Enter to continue...", alwaysTrueTest);
    }  // namespace S3
} // namespace AwsDoc

// snippet-start:[cpp.example_code.s3.Scenario_ObjectIntegrity]
//! Routine which runs the S3 object integrity workflow.
/*!
   \param clientConfig: Aws client configuration.
   \return bool: Function succeeded.
*/
bool AwsDoc::S3::s3ObjectIntegrityWorkflow(
        const Aws::S3::S3ClientConfiguration &clientConfiguration) {

    /*
     * Create a large file to be used for multipart uploads.
     */
    if (!createLargeFileIfNotExists()) {
        std::cerr << "Workflow exiting because large file creation failed." << std::endl;
        return false;
    }

    Aws::String bucketName = TEST_BUCKET_PREFIX;
    bucketName += Aws::Utils::UUID::RandomUUID();
    bucketName = Aws::Utils::StringUtils::ToLower(bucketName.c_str());

    bucketName.resize(std::min(bucketName.size(), MAX_BUCKET_NAME_LENGTH));

    introductoryExplanations(bucketName);

    if (!AwsDoc::S3::createBucket(bucketName, clientConfiguration)) {
        std::cerr << "Workflow exiting because bucket creation failed." << std::endl;
        return false;
    }

    Aws::S3::S3ClientConfiguration s3ClientConfiguration(clientConfiguration);
    std::shared_ptr<Aws::S3::S3Client> client = Aws::MakeShared<Aws::S3::S3Client>("S3Client", s3ClientConfiguration);

    printAsterisksLine();
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
                << "A checksum computed by this workflow will be used for object integrity verification,"
                << std::endl;
        std::cout << "except for the TransferManager upload." << std::endl;
    } else {
        std::cout
                << "A checksum computed by the SDK will be used for object integrity verification."
                << std::endl;
    }

    pressEnterToContinue();
    printAsterisksLine();

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
        putObjectHashMethod = MD5; // MD5 is the default hash method for PutObject.

        std::cout << "The default checksum algorithm for PutObject is "
                  << stringForHashMethod(putObjectHashMethod)
                  << std::endl;
    }

    // Demonstrate in code how the hash is computed.
    if (!hasher.calculateObjectHash(*inputData, putObjectHashMethod)) {
        std::cerr << "Error calculating hash for file " << TEST_FILE << std::endl;
        cleanUp(bucketName, clientConfiguration);
        return false;
    }
    Aws::String key = stringForHashMethod(putObjectHashMethod);
    key += "_";
    key += TEST_FILE_KEY;
    Aws::String localHash = hasher.getBase64HashString();

    // Upload the object with PutObject
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

    explainPutObjectResults();
    verifyHashingResults(retrievedHash, hasher,
                         "PutObject upload", putObjectHashMethod);


    printAsterisksLine();
    pressEnterToContinue();

    key = "tr_";
    key += stringForHashMethod(chosenHashMethod) + "_" + MULTI_PART_TEST_FILE;

    introductoryTransferManagerUploadExplanations(key);

    HASH_METHOD transferManagerHashMethod = chosenHashMethod;
    if (transferManagerHashMethod == DEFAULT) {
        transferManagerHashMethod = CRC32;  // The default hash method for the TransferManager is CRC32.

        std::cout << "The default checksum algorithm for TransferManager is "
                  << stringForHashMethod(transferManagerHashMethod)
                  << std::endl;
    }

    // Upload the large file using the transfer manager.
    if (!doTransferManagerUpload(bucketName, key, transferManagerHashMethod, chosenHashMethod == DEFAULT,
                                 client)) {
        std::cerr << "Exiting because of an error in doTransferManagerUpload." << std::endl;
        cleanUp(bucketName, clientConfiguration);
        return false;
    }

    std::vector<Aws::String> retrievedTransferManagerPartHashes;
    Aws::String retrievedTransferManagerFinalHash;

    // Retrieve all the hashes for the TransferManager upload.
    if (!retrieveObjectHash(bucketName, key,
                            transferManagerHashMethod,
                            retrievedTransferManagerFinalHash,
                            &retrievedTransferManagerPartHashes, *client)) {
        std::cerr << "Exiting because of an error in retrieveObjectHash for TransferManager." << std::endl;
        cleanUp(bucketName, clientConfiguration);
        return false;
    }

    AwsDoc::S3::Hasher locallyCalculatedFinalHash;
    std::vector<Aws::String> locallyCalculatedPartHashes;

    // Calculate the hashes locally to demonstrate how TransferManager hashes are computed.
    if (!calculatePartHashesForFile(transferManagerHashMethod, MULTI_PART_TEST_FILE,
                                    UPLOAD_BUFFER_SIZE,
                                    locallyCalculatedFinalHash,
                                    locallyCalculatedPartHashes)) {
        std::cerr << "Exiting because of an error in calculatePartHashesForFile." << std::endl;
        cleanUp(bucketName, clientConfiguration);
        return false;
    }

    verifyHashingResults(retrievedTransferManagerFinalHash,
                         locallyCalculatedFinalHash, "TransferManager upload",
                         transferManagerHashMethod,
                         retrievedTransferManagerPartHashes,
                         locallyCalculatedPartHashes);

    printAsterisksLine();

    key = "mp_";
    key += stringForHashMethod(chosenHashMethod) + "_" + MULTI_PART_TEST_FILE;

    multiPartUploadExplanations(key, chosenHashMethod);

    pressEnterToContinue();

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

    HASH_METHOD multipartUploadHashMethod = chosenHashMethod;
    if (multipartUploadHashMethod == DEFAULT) {
        multipartUploadHashMethod = MD5;  // The default hash method for multipart uploads is MD5.

        std::cout << "The default checksum algorithm for multipart upload is "
                  << stringForHashMethod(putObjectHashMethod)
                  << std::endl;
    }

    AwsDoc::S3::Hasher hashData;
    std::vector<Aws::String> partHashes;

    if (!doMultipartUpload(bucketName, key,
                           multipartUploadHashMethod,
                           largeFileInputData, chosenHashMethod == DEFAULT,
                           hashData,
                           partHashes,
                           *client)) {
        std::cerr << "Exiting because of an error in doMultipartUpload." << std::endl;
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
        std::cerr << "Exiting because of an error in retrieveObjectHash for multipart." << std::endl;
        cleanUp(bucketName, clientConfiguration);
        return false;
    }

    verifyHashingResults(retrievedHash, hashData, "MultiPart upload",
                         multipartUploadHashMethod,
                         retrievedPartHashes, partHashes);

    printAsterisksLine();

    if (askYesNoQuestion("Would you like to delete the resources created in this workflow? (y/n)")) {
        return cleanUp(bucketName, clientConfiguration);
    } else {
        std::cout << "The bucket " << bucketName << " was not deleted." << std::endl;
        return true;
    }
}

//! Routine which uploads an object to an S3 bucket with different object integrity hashing methods.
/*!
   \param bucket: The name of the S3 bucket where the object will be uploaded.
   \param key: The unique identifier (key) for the object within the S3 bucket.
   \param hashData: The hash value that will be associated with the uploaded object.
   \param hashMethod: The hashing algorithm to use when calculating the hash value.
   \param body: The data content of the object being uploaded.
   \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
   \param client: The S3 client instance used to perform the upload operation.
   \return bool: Function succeeded.
*/
bool AwsDoc::S3::putObjectWithHash(const Aws::String &bucket, const Aws::String &key,
                                   const Aws::String &hashData,
                                   AwsDoc::S3::HASH_METHOD hashMethod,
                                   const std::shared_ptr<Aws::IOStream> &body,
                                   bool useDefaultHashMethod,
                                   const Aws::S3::S3Client &client) {
    Aws::S3::Model::PutObjectRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    if (!useDefaultHashMethod) {
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
    } else {
        std::cerr << "Error uploading object." <<
                  outcome.GetError().GetMessage() << std::endl;
    }
    return outcome.IsSuccess();
}


// snippet-start:[cpp.example_code.s3.GetObjectAttributes]
// ! Routine which retrieves the hash value of an object stored in an S3 bucket.
/*!
   \param bucket: The name of the S3 bucket where the object is stored.
   \param key: The unique identifier (key) of the object within the S3 bucket.
   \param hashMethod: The hashing algorithm used to calculate the hash value of the object.
   \param[out] hashData: The retrieved hash.
   \param[out] partHashes: The part hashes if available.
   \param client: The S3 client instance used to retrieve the object.
   \return bool: Function succeeded.
*/
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
        } else {
            std::cerr << "Error retrieving object etag attributes." <<
                      outcome.GetError().GetMessage() << std::endl;
            return false;
        }
    } else { // hashMethod != MD5
        Aws::Vector<Aws::S3::Model::ObjectAttributes> attributes;
        attributes.push_back(Aws::S3::Model::ObjectAttributes::Checksum);
        request.SetObjectAttributes(attributes);

        Aws::S3::Model::GetObjectAttributesOutcome outcome = client.GetObjectAttributes(
                request);
        if (outcome.IsSuccess()) {
            const Aws::S3::Model::GetObjectAttributesResult &result = outcome.GetResult();
            switch (hashMethod) {
                case AwsDoc::S3::DEFAULT: // NOLINT(*-branch-clone)
                    break;  // Default is not supported.
#pragma clang diagnostic push
#pragma ide diagnostic ignored "UnreachableCode"
                case AwsDoc::S3::MD5:
                    break;  // MD5 is not supported.
#pragma clang diagnostic pop
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
        } else {
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
                        case AwsDoc::S3::DEFAULT: // Default is not supported. NOLINT(*-branch-clone)
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
            } else {
                std::cerr << "Error retrieving object attributes for object parts." <<
                          outcome.GetError().GetMessage() << std::endl;
                return false;
            }
        }
    }

    return true;
}
// snippet-end:[cpp.example_code.s3.GetObjectAttributes]

//! Verifies the hashing results between the retrieved and local hashes.
/*!
 \param retrievedHash The hash value retrieved from the remote source.
 \param localHash The hash value calculated locally.
 \param uploadtype The type of upload (e.g., "multipart", "single-part").
 \param hashMethod The hashing method used (e.g., MD5, SHA-256).
 \param retrievedPartHashes (Optional) The list of hashes for the individual parts retrieved from the remote source.
 \param localPartHashes (Optional) The list of hashes for the individual parts calculated locally.
 */
void AwsDoc::S3::verifyHashingResults(const Aws::String &retrievedHash,
                                      const Hasher &localHash,
                                      const Aws::String &uploadtype,
                                      HASH_METHOD hashMethod,
                                      const std::vector<Aws::String> &retrievedPartHashes,
                                      const std::vector<Aws::String> &localPartHashes) {
    std::cout << "For " << uploadtype << " retrieved hash is " << retrievedHash << std::endl;
    if (!retrievedPartHashes.empty()) {
        std::cout << retrievedPartHashes.size() << " part hash(es) were also retrieved."
                  << std::endl;
        for (auto &retrievedPartHash: retrievedPartHashes) {
            std::cout << "  Part hash " << retrievedPartHash << std::endl;
        }
    }
    Aws::String hashString;
    if (hashMethod == MD5) {
        hashString = localHash.getHexHashString();
        if (!localPartHashes.empty()) {
            hashString += "-" + std::to_string(localPartHashes.size());
        }
    } else {
        hashString = localHash.getBase64HashString();
    }

    bool allMatch = true;
    if (hashString != retrievedHash) {
        std::cerr << "For " << uploadtype << ", the main hashes do not match" << std::endl;
        std::cerr << "Local hash- '" << hashString << "'" << std::endl;
        std::cerr << "Remote hash - '" << retrievedHash << "'" << std::endl;
        allMatch = false;
    }

    if (hashMethod != MD5) {
        if (localPartHashes.size() != retrievedPartHashes.size()) {
            std::cerr << "For " << uploadtype << ", the number of part hashes do not match" << std::endl;
            std::cerr << "Local number of hashes- '" << localPartHashes.size() << "'"
                      << std::endl;
            std::cerr << "Remote number of hashes - '"
                      << retrievedPartHashes.size()
                      << "'" << std::endl;
        }

        for (int i = 0; i < localPartHashes.size(); ++i) {
            if (localPartHashes[i] != retrievedPartHashes[i]) {
                std::cerr << "For " << uploadtype << ", the part hashes do not match for part " << i + 1
                          << "." << std::endl;
                std::cerr << "Local hash- '" << localPartHashes[i] << "'"
                          << std::endl;
                std::cerr << "Remote hash - '" << retrievedPartHashes[i] << "'"
                          << std::endl;
                allMatch = false;
            }
        }
    }

    if (allMatch) {
        std::cout << "For " << uploadtype << ", locally and remotely calculated hashes all match!" << std::endl;
    }

}

static void transferManagerErrorCallback(const Aws::Transfer::TransferManager *,
                                         const std::shared_ptr<const Aws::Transfer::TransferHandle> &,
                                         const Aws::Client::AWSError<Aws::S3::S3Errors> &err) {
    std::cerr << "Error during transfer: '" << err.GetMessage() << "'" << std::endl;
}

static void transferManagerStatusCallback(const Aws::Transfer::TransferManager *,
                                          const std::shared_ptr<const Aws::Transfer::TransferHandle> &handle) {
    if (handle->GetStatus() == Aws::Transfer::TransferStatus::IN_PROGRESS) {
        std::cout << "Bytes transferred: " << handle->GetBytesTransferred() << std::endl;
    }
}

//! Routine which uploads an object to an S3 bucket using the AWS C++ SDK's Transfer Manager.
/*!
   \param bucket: The name of the S3 bucket where the object will be uploaded.
   \param key: The unique identifier (key) for the object within the S3 bucket.
   \param hashMethod: The hashing algorithm to use when calculating the hash value.
   \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
   \param client: The S3 client instance used to perform the upload operation.
   \return bool: Function succeeded.
*/
bool
AwsDoc::S3::doTransferManagerUpload(const Aws::String &bucket, const Aws::String &key,
                                    AwsDoc::S3::HASH_METHOD hashMethod,
                                    bool useDefaultHashMethod,
                                    const std::shared_ptr<Aws::S3::S3Client> &client) {
    std::shared_ptr<Aws::Utils::Threading::PooledThreadExecutor> executor = Aws::MakeShared<Aws::Utils::Threading::PooledThreadExecutor>(
            "executor", 25);
    Aws::Transfer::TransferManagerConfiguration transfer_config(executor.get());
    transfer_config.s3Client = client;
    transfer_config.bufferSize = UPLOAD_BUFFER_SIZE;
    if (!useDefaultHashMethod) {
        if (hashMethod == MD5) {
            transfer_config.computeContentMD5 = true;
        } else {
            transfer_config.checksumAlgorithm = getChecksumAlgorithmForHashMethod(
                    hashMethod);
        }
    }
    transfer_config.errorCallback = transferManagerErrorCallback;
    transfer_config.transferStatusUpdatedCallback = transferManagerStatusCallback;

    std::shared_ptr<Aws::Transfer::TransferManager> transfer_manager = Aws::Transfer::TransferManager::Create(
            transfer_config);

    std::cout << "Uploading the file..." << std::endl;
    std::shared_ptr<Aws::Transfer::TransferHandle> uploadHandle = transfer_manager->UploadFile(MULTI_PART_TEST_FILE,
                                                                                               bucket, key,
                                                                                               "text/plain",
                                                                                               Aws::Map<Aws::String, Aws::String>());
    uploadHandle->WaitUntilFinished();
    bool success =
            uploadHandle->GetStatus() == Aws::Transfer::TransferStatus::COMPLETED;
    if (!success) {
        Aws::Client::AWSError<Aws::S3::S3Errors> err = uploadHandle->GetLastError();
        std::cerr << "File upload failed:  " << err.GetMessage() << std::endl;
    }

    return success;
}

//! Routine which calculates the hash values for each part of a file being uploaded to an S3 bucket.
/*!
   \param hashMethod: The hashing algorithm to use when calculating the hash values.
   \param fileName: The path to the file for which the part hashes will be calculated.
   \param bufferSize: The size of the buffer to use when reading the file.
   \param[out] hashDataResult: The Hasher object that will store the concatenated hash value.
   \param[out] partHashes: The vector that will store the calculated hash values for each part of the file.
   \return bool: Function succeeded.
*/
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

// snippet-start:[cpp.example_code.s3.CreateMultipartUpload]
//! Create a multipart upload.
/*!
    \param bucket: The name of the S3 bucket where the object will be uploaded.
    \param key: The unique identifier (key) for the object within the S3 bucket.
    \param client: The S3 client instance used to perform the upload operation.
    \return Aws::String: Upload ID or empty string if failed.
*/
Aws::String
AwsDoc::S3::createMultipartUpload(const Aws::String &bucket, const Aws::String &key,
                                  Aws::S3::Model::ChecksumAlgorithm checksumAlgorithm,
                                  const Aws::S3::S3Client &client) {
    Aws::S3::Model::CreateMultipartUploadRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);

    if (checksumAlgorithm != Aws::S3::Model::ChecksumAlgorithm::NOT_SET) {
        request.SetChecksumAlgorithm(checksumAlgorithm);
    }

    Aws::S3::Model::CreateMultipartUploadOutcome outcome =
            client.CreateMultipartUpload(request);

    Aws::String uploadID;
    if (outcome.IsSuccess()) {
        uploadID = outcome.GetResult().GetUploadId();
    } else {
        std::cerr << "Error creating multipart upload: " << outcome.GetError().GetMessage() << std::endl;
    }

    return uploadID;
}
// snippet-end:[cpp.example_code.s3.CreateMultipartUpload]

// snippet-start:[cpp.example_code.s3.UploadPart]
//! Upload a part to an S3 bucket.
/*!
    \param bucket: The name of the S3 bucket where the object will be uploaded.
    \param key: The unique identifier (key) for the object within the S3 bucket.
    \param uploadID: An upload ID string.
    \param partNumber:
    \param checksumAlgorithm: Checksum algorithm, ignored when NOT_SET.
    \param calculatedHash: A data integrity hash to set, depending on the checksum algorithm,
                            ignored when it is an empty string.
    \param body: An shared_ptr IOStream of the data to be uploaded.
    \param client: The S3 client instance used to perform the upload operation.
    \return UploadPartOutcome: The outcome.
*/

Aws::S3::Model::UploadPartOutcome AwsDoc::S3::uploadPart(const Aws::String &bucket,
                                                         const Aws::String &key,
                                                         const Aws::String &uploadID,
                                                         int partNumber,
                                                         Aws::S3::Model::ChecksumAlgorithm checksumAlgorithm,
                                                         const Aws::String &calculatedHash,
                                                         const std::shared_ptr<Aws::IOStream> &body,
                                                         const Aws::S3::S3Client &client) {
    Aws::S3::Model::UploadPartRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    request.SetUploadId(uploadID);
    request.SetPartNumber(partNumber);
    if (checksumAlgorithm != Aws::S3::Model::ChecksumAlgorithm::NOT_SET) {
        request.SetChecksumAlgorithm(checksumAlgorithm);
    }
    request.SetBody(body);

    if (!calculatedHash.empty()) {
        switch (checksumAlgorithm) {
            case Aws::S3::Model::ChecksumAlgorithm::NOT_SET:
                request.SetContentMD5(calculatedHash);
                break;
            case Aws::S3::Model::ChecksumAlgorithm::CRC32:
                request.SetChecksumCRC32(calculatedHash);
                break;
            case Aws::S3::Model::ChecksumAlgorithm::CRC32C:
                request.SetChecksumCRC32C(calculatedHash);
                break;
            case Aws::S3::Model::ChecksumAlgorithm::SHA1:
                request.SetChecksumSHA1(calculatedHash);
                break;
            case Aws::S3::Model::ChecksumAlgorithm::SHA256:
                request.SetChecksumSHA256(calculatedHash);
                break;
        }
    }

    return client.UploadPart(request);
}
// snippet-end:[cpp.example_code.s3.UploadPart]

// snippet-start:[cpp.example_code.s3.AbortMultipartUpload]
//! Abort a multipart upload to an S3 bucket.
/*!
    \param bucket: The name of the S3 bucket where the object will be uploaded.
    \param key: The unique identifier (key) for the object within the S3 bucket.
    \param uploadID: An upload ID string.
    \param client: The S3 client instance used to perform the upload operation.
    \return bool: Function succeeded.
*/

bool AwsDoc::S3::abortMultipartUpload(const Aws::String &bucket,
                                      const Aws::String &key,
                                      const Aws::String &uploadID,
                                      const Aws::S3::S3Client &client) {
    Aws::S3::Model::AbortMultipartUploadRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    request.SetUploadId(uploadID);

    Aws::S3::Model::AbortMultipartUploadOutcome outcome =
            client.AbortMultipartUpload(request);

    if (outcome.IsSuccess()) {
        std::cout << "Multipart upload aborted." << std::endl;
    } else {
        std::cerr << "Error aborting multipart upload: " << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.s3.AbortMultipartUpload]

// snippet-start:[cpp.example_code.s3.CompleteMultipartUpload]
//! Complete a multipart upload to an S3 bucket.
/*!
    \param bucket: The name of the S3 bucket where the object will be uploaded.
    \param key: The unique identifier (key) for the object within the S3 bucket.
    \param uploadID: An upload ID string.
    \param parts: A vector of CompleteParts.
    \param client: The S3 client instance used to perform the upload operation.
    \return CompleteMultipartUploadOutcome: The request outcome.
*/
Aws::S3::Model::CompleteMultipartUploadOutcome AwsDoc::S3::completeMultipartUpload(const Aws::String &bucket,
                                                                                   const Aws::String &key,
                                                                                   const Aws::String &uploadID,
                                                                                   const Aws::Vector<Aws::S3::Model::CompletedPart> &parts,
                                                                                   const Aws::S3::S3Client &client) {
    Aws::S3::Model::CompletedMultipartUpload completedMultipartUpload;
    completedMultipartUpload.SetParts(parts);

    Aws::S3::Model::CompleteMultipartUploadRequest request;
    request.SetBucket(bucket);
    request.SetKey(key);
    request.SetUploadId(uploadID);
    request.SetMultipartUpload(completedMultipartUpload);

    Aws::S3::Model::CompleteMultipartUploadOutcome outcome =
            client.CompleteMultipartUpload(request);

    if (!outcome.IsSuccess()) {
        std::cerr << "Error completing multipart upload: " << outcome.GetError().GetMessage() << std::endl;
    }
    return outcome;
}
// snippet-end:[cpp.example_code.s3.CompleteMultipartUpload]

//! Routine which performs a multi-part upload.
/*!
    \param bucket: The name of the S3 bucket where the object will be uploaded.
    \param key: The unique identifier (key) for the object within the S3 bucket.
    \param hashMethod: The hashing algorithm to use when calculating the hash value.
    \param ioStream: An IOStream for the data to be uploaded.
    \param useDefaultHashMethod: A flag indicating whether to use the default hash method or the one specified in the hashMethod parameter.
    \param[out] hashDataResult: The Hasher object that will store the concatenated hash value.
    \param[out] partHashes: The vector that will store the calculated hash values for each part of the file.
    \param client: The S3 client instance used to perform the upload operation.
    \return bool: Function succeeded.
*/
bool AwsDoc::S3::doMultipartUpload(const Aws::String &bucket,
                                   const Aws::String &key,
                                   AwsDoc::S3::HASH_METHOD hashMethod,
                                   const std::shared_ptr<Aws::IOStream> &ioStream,
                                   bool useDefaultHashMethod,
                                   AwsDoc::S3::Hasher &hashDataResult,
                                   std::vector<Aws::String> &partHashes,
                                   const Aws::S3::S3Client &client) {
    // Get object size.
    ioStream->seekg(0, ioStream->end);
    size_t objectSize = ioStream->tellg();
    ioStream->seekg(0, ioStream->beg);

    Aws::S3::Model::ChecksumAlgorithm checksumAlgorithm = Aws::S3::Model::ChecksumAlgorithm::NOT_SET;
    if (!useDefaultHashMethod) {
        if (hashMethod != MD5) {
            checksumAlgorithm = getChecksumAlgorithmForHashMethod(hashMethod);
        }
    }
    Aws::String uploadID = createMultipartUpload(bucket, key, checksumAlgorithm, client);
    if (uploadID.empty()) {
        return false;
    }

    std::vector<unsigned char> totalHashBuffer;
    bool uploadSucceeded = true;
    std::streamsize uploadedBytes = 0;
    int partNumber = 1;
    Aws::Vector<Aws::S3::Model::CompletedPart> parts;
    while (uploadedBytes < objectSize) {
        std::cout << "Uploading part " << partNumber << "." << std::endl;

        std::vector<unsigned char> buffer(UPLOAD_BUFFER_SIZE);
        std::streamsize bytesToRead = static_cast<std::streamsize>(std::min(buffer.size(),
                                                                            objectSize - uploadedBytes));
        ioStream->read((char *) buffer.data(), bytesToRead);
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

        Aws::Utils::ByteBuffer hashBuffer = hasher.getByteBufferHash();

        totalHashBuffer.insert(totalHashBuffer.end(), hashBuffer.GetUnderlyingData(),
                               hashBuffer.GetUnderlyingData() + hashBuffer.GetLength());

        Aws::String calculatedHash;
        if (gUseCalculatedChecksum) {
            calculatedHash = base64HashString;
        }
        Aws::S3::Model::UploadPartOutcome uploadPartOutcome = uploadPart(bucket, key, uploadID, partNumber,
                                                                         checksumAlgorithm, base64HashString, body,
                                                                         client);
        if (uploadPartOutcome.IsSuccess()) {
            const Aws::S3::Model::UploadPartResult &uploadPartResult = uploadPartOutcome.GetResult();
            Aws::S3::Model::CompletedPart completedPart;
            completedPart.SetETag(uploadPartResult.GetETag());
            completedPart.SetPartNumber(partNumber);
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

            parts.push_back(completedPart);
        } else {
            std::cerr << "Error uploading part. " <<
                      uploadPartOutcome.GetError().GetMessage() << std::endl;
            uploadSucceeded = false;
            break;
        }

        uploadedBytes += bytesToRead;
        partNumber++;
    }

    if (!uploadSucceeded) {
        abortMultipartUpload(bucket, key, uploadID, client);
        return false;
    } else {

        Aws::S3::Model::CompleteMultipartUploadOutcome completeMultipartUploadOutcome = completeMultipartUpload(bucket,
                                                                                                                key,
                                                                                                                uploadID,
                                                                                                                parts,
                                                                                                                client);

        if (completeMultipartUploadOutcome.IsSuccess()) {
            std::cout << "Multipart upload completed." << std::endl;
            if (!hashDataResult.calculateObjectHash(totalHashBuffer, hashMethod)) {
                std::cerr << "Error calculating hash." << std::endl;
                return false;
            }
        } else {
            std::cerr << "Error completing multipart upload." <<
                      completeMultipartUploadOutcome.GetError().GetMessage()
                      << std::endl;
        }

        return completeMultipartUploadOutcome.IsSuccess();
    }
}

//! Routine which retrieves the string for a HASH_METHOD constant.
/*!
    \param: hashMethod: A HASH_METHOD constant.
    \return: String: A string description of the hash method.
*/
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

//! Routine that returns the ChecksumAlgorithm for a HASH_METHOD constant.
/*!
    \param: hashMethod: A HASH_METHOD constant.
    \return: ChecksumAlgorithm: The ChecksumAlgorithm enum.
*/
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

//! Routine which cleans up after the example is complete.
/*!
    \param bucket: The name of the S3 bucket where the object was uploaded.
    \param clientConfiguration: The client configuration for the S3 client.
    \return bool: Function succeeded.
*/
bool AwsDoc::S3::cleanUp(const Aws::String &bucketName,
                         const Aws::S3::S3ClientConfiguration &clientConfiguration) {

    Aws::Vector<Aws::String> keysResult;
    bool result = true;
    if (AwsDoc::S3::listObjects(bucketName, keysResult, clientConfiguration)) {
        if (!keysResult.empty()) {
            result = AwsDoc::S3::deleteObjects(keysResult, bucketName,
                                               clientConfiguration);
        }
    } else {
        result = false;
    }

    return result && AwsDoc::S3::deleteBucket(bucketName, clientConfiguration);
}

//! Console interaction introducing the workflow.
/*!
  \param bucketName: The name of the S3 bucket to use.
*/
void AwsDoc::S3::introductoryExplanations(const Aws::String &bucketName) {

    std::cout
            << "Welcome to the Amazon Simple Storage Service (Amazon S3) object integrity workflow."
            << std::endl;
    printAsterisksLine();
    std::cout
            << "This workflow demonstrates how Amazon S3 uses checksum values to verify the integrity of data\n";
    std::cout << "uploaded to Amazon S3 buckets" << std::endl;
    std::cout
            << "The AWS SDK for C++ automatically handles checksums.\n";
    std::cout
            << "By default it calculates a checksum that is uploaded with an object.\n"
            << "The default checksum algorithm for PutObject and MultiPart upload is an MD5 hash.\n"
            << "The default checksum algorithm for TransferManager uploads is a CRC32 checksum."
            << std::endl;
    std::cout
            << "You can override the default behavior, requiring one of the following checksums,\n";
    std::cout << "MD5, CRC32, CRC32C, SHA-1 or SHA-256." << std::endl;
    std::cout << "You can also set the checksum hash value, instead of letting the SDK calculate the value."
              << std::endl;
    std::cout
            << "For more information, see https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html."
            << std::endl;

    std::cout
            << "This workflow will locally compute checksums for files uploaded to an Amazon S3 bucket,\n";
    std::cout << "even when the SDK also computes the checksum." << std::endl;
    std::cout
            << "This is done to provide demonstration code for how the checksums are calculated."
            << std::endl;
    std::cout << "A bucket named '" << bucketName << "' will be created for the object uploads."
              << std::endl;
}

//! Console interaction which explains the PutObject results.
/*!
*/
void AwsDoc::S3::explainPutObjectResults() {

    std::cout << "The upload was successful.\n";
    std::cout << "If the checksums had not matched, the upload would have failed."
              << std::endl;
    std::cout
            << "The checksums calculated by the server have been retrieved using the GetObjectAttributes."
            << std::endl;
    std::cout
            << "The locally calculated checksums have been verified against the retrieved checksums."
            << std::endl;
}

//! Console interaction explaining transfer manager uploads.
/*!
  \param objectKey: The key for the object being uploaded.
*/
void AwsDoc::S3::introductoryTransferManagerUploadExplanations(
        const Aws::String &objectKey) {
    std::cout
            << "Now the workflow will demonstrate object integrity for TransferManager multi-part uploads."
            << std::endl;
    std::cout
            << "The AWS C++ SDK has a TransferManager class which simplifies multipart uploads."
            << std::endl;
    std::cout
            << "The following code lets the TransferManager handle much of the checksum configuration."
            << std::endl;

    std::cout << "An object with the key '" << objectKey
              << " will be uploaded by the TransferManager using a "
              << BUFFER_SIZE_IN_MEGABYTES << " MB buffer." << std::endl;
    if (gUseCalculatedChecksum) {
        std::cout << "For TransferManager uploads, this demo always lets the SDK calculate the hash value."
                  << std::endl;
    }

    pressEnterToContinue();
    printAsterisksLine();
}

//! Console interaction explaining multi-part uploads.
/*!
  \param objectKey: The key for the object being uploaded.
  \param chosenHashMethod: The hash method selected by the user.
*/
void AwsDoc::S3::multiPartUploadExplanations(const Aws::String &objectKey,
                                             HASH_METHOD chosenHashMethod) {
    std::cout
            << "Now we will provide an in-depth demonstration of multi-part uploading by calling the multi-part upload APIs directly."
            << std::endl;
    std::cout << "These are the same APIs used by the TransferManager when uploading large files."
              << std::endl;
    std::cout
            << "In the following code, the checksums are also calculated locally and then compared."
            << std::endl;
    std::cout
            << "For multi-part uploads, a checksum is uploaded with each part. The final checksum is a concatenation of"
            << std::endl;
    std::cout << "the checksums for each part." << std::endl;
    std::cout
            << "This is explained in the user guide, https://docs.aws.amazon.com/AmazonS3/latest/userguide/checking-object-integrity.html,\""
            << " in the section \"Using part-level checksums for multipart uploads\"." << std::endl;

    std::cout << "Starting multipart upload of with hash method " <<
              stringForHashMethod(chosenHashMethod) << " uploading to with object key\n"
              << "'" << objectKey << "'," << std::endl;

}

//! Create a large file for doing multi-part uploads.
/*!
*/
bool AwsDoc::S3::createLargeFileIfNotExists() {
    // Generate a large file by writing this source file multiple times to a new file.
    if (std::filesystem::exists(MULTI_PART_TEST_FILE)) {
        return true;
    }

    std::ofstream newFile(MULTI_PART_TEST_FILE, std::ios::out

                                                | std::ios::binary);

    if (!newFile) {
        std::cerr << "createLargeFileIfNotExists- Error creating file " << MULTI_PART_TEST_FILE <<
                  std::endl;
        return false;
    }

    std::ifstream input(TEST_FILE, std::ios::in

                                   | std::ios::binary);
    if (!input) {
        std::cerr << "Error opening file " << TEST_FILE <<
                  std::endl;
        return false;
    }
    std::stringstream buffer;
    buffer << input.rdbuf();

    input.close();

    while (newFile.tellp() < LARGE_FILE_SIZE && !newFile.bad()) {
        buffer.seekg(std::stringstream::beg);
        newFile << buffer.rdbuf();
    }

    newFile.close();

    return true;
}
// snippet-end:[cpp.example_code.s3.Scenario_ObjectIntegrity]

//! Calculate the object hash for vector input.
/*!
   \param data: A vector of unsigned bytes.
   \param hashMethod: The hash method to use.
   \return bool: Function succeeded.
*/
bool AwsDoc::S3::Hasher::calculateObjectHash(std::vector<unsigned char> &data,
                                             AwsDoc::S3::HASH_METHOD hashMethod) {
    Aws::Utils::Stream::PreallocatedStreamBuf preallocatedStreamBuf(data.data(),
                                                                    data.size());
    std::shared_ptr<Aws::IOStream> body =
            Aws::MakeShared<Aws::IOStream>("SampleAllocationTag",
                                           &preallocatedStreamBuf);
    return calculateObjectHash(*body, hashMethod);
}

//! Calculate the object hash for stream input.
/*!
   \param data: An IOStream for input.
   \param hashMethod: The hash method to use.
   \return bool: Function succeeded.
*/
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
    data.clear();
    data.seekg(0, std::ifstream::beg);
    return true;
}

//! Retrieve the stored hash as a Base64 string.
/*!
   \return String: Hash as Base64 string.
*/
Aws::String AwsDoc::S3::Hasher::getBase64HashString() const {
    return Aws::Utils::HashingUtils::Base64Encode(m_Hash);
}

//! Retrieve the stored hash as a hexadecimal string.
/*!
   \return String: Hash as hexadecimal string.
*/
Aws::String AwsDoc::S3::Hasher::getHexHashString() const {
    std::stringstream stringstream;
    stringstream << std::hex << std::setfill('0');
    for (int i = 0; i < m_Hash.GetLength(); ++i) {
        stringstream << std::setw(2) << (int) m_Hash[i];
    }

    return stringstream.str();
}

//! Retrieve the stored hash as a ByteBuffer.
/*!
   \return String: Hash as ByteBuffer.
*/
Aws::Utils::ByteBuffer AwsDoc::S3::Hasher::getByteBufferHash() const {
    return m_Hash;
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


#pragma clang diagnostic pop