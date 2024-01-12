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

#include "cpp_lambda_functions.h"
#include <aws/core/Aws.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/BatchGetItemRequest.h>
#include <aws/dynamodb/model/BatchWriteItemRequest.h>
#include <aws/dynamodb/model/KeysAndAttributes.h>
#include <aws/dynamodb/model/ScanRequest.h>
#include <aws/rekognition/RekognitionClient.h>
#include <aws/rekognition/model/DetectLabelsRequest.h>
#include <aws/rekognition/model/S3Object.h>
#include <aws/rekognition/model/Image.h>
#include <aws/s3/S3Client.h>
#include <aws/s3/model/GetObjectRequest.h>
#include <aws/s3/model/PutObjectRequest.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/PublishRequest.h>
#include <zip.h>
#include <set>
#include <fstream>

namespace AwsDoc {
    namespace PAM {
        const char LABEL_KEY[] = "Label";
        static const char COUNT_KEY[] = "count";
        static const char IMAGES_KEY[] = "images";

        struct image_zip_data {
            image_zip_data() : mIOStream(nullptr) {}

            Aws::IOStream *mIOStream;
        };

        //! A libzip callback routine for an Aws::IOStream as a data source.
        /*!
         \param userdata: The image_zip_data user data.
         \param data: A pointer for returning data.
         \param len: The length of data to return.
         \param cmd: A zip source command.
         \return zip_int64_t: A zip_source result.
        */
        zip_int64_t my_zip_source(void *userdata, void *data, zip_uint64_t len,
                                  zip_source_cmd_t cmd);
    } // PAM
} // AwsDoc

//! Routine that returns a presigned Amazon Simple Storage Service (Amazon S3) upload URL.
/*!
  \param bucket: An S3 bucket name.
  \param key: An S3 object key.
  \param clientConfiguration: AWS client configuration.
  \return std::string: The URL as a string.
 */
std::string
AwsDoc::PAM::getPreSignedS3UploadURL(const std::string &bucket, const std::string &key,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::S3::S3Client s3Client(clientConfiguration);
    return s3Client.GeneratePresignedUrl(bucket, key, Aws::Http::HttpMethod::HTTP_PUT,
                                         300 // expirationInSeconds
    );
}

//! Routine that analyzes an image and returns labels using Amazon Rekognition.
/*!
  \param bucket: An S3 bucket name which contains the image.
  \param key: An S3 object key for the image.
  \param imageLabels: A vector to receive the label results.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool
AwsDoc::PAM::analyzeAndGetLabels(const std::string &bucket, const std::string &key,
                                 std::vector<std::string> &imageLabels,
                                 std::ostream &errStream,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::Rekognition::RekognitionClient rekognitionClient(clientConfiguration);
    Aws::Rekognition::Model::S3Object s3Object;
    s3Object.SetBucket(bucket);
    s3Object.SetName(key);

    Aws::Rekognition::Model::Image image;
    image.SetS3Object(s3Object);
    Aws::Rekognition::Model::DetectLabelsRequest request;
    request.SetImage(image);
    request.SetMaxLabels(10);

    const Aws::Rekognition::Model::DetectLabelsOutcome outcome = rekognitionClient.DetectLabels(
            request);

    if (outcome.IsSuccess()) {
        const Aws::Vector<Aws::Rekognition::Model::Label> &labels = outcome.GetResult().GetLabels();

        for (const Aws::Rekognition::Model::Label &label: labels) {
            imageLabels.push_back(label.GetName());
        }
    }
    else {
        errStream << "Failed to detect labels, " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

//! Routine that updates a DynamoDB table with labels and the associated S3 object key of the image.
/*!
  \param databaseName: A DynamoDB table name.
  \param labels: A vector of labels as DynamoDB table keys.
  \param bucketKey: The S3 object key for the image.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::PAM::updateLabelsInDatabase(const std::string &databaseName,
                                         const std::vector<std::string> &labels,
                                         const std::string &bucketKey,
                                         std::ostream &errStream,
                                         const Aws::Client::ClientConfiguration &clientConfiguration) {
    // Retrieve the existing entries
    Aws::DynamoDB::DynamoDBClient dbClient(clientConfiguration);

    AttributeValueMap mapOfImageKeys;
    if (!getKeysForLabelsFromDatabase(databaseName, labels, mapOfImageKeys,
                                      errStream, clientConfiguration)) {
        return false;
    }

    int labelsWritten = 0;
    while (labelsWritten < labels.size()) {
        int labelsToWrite = std::min(static_cast<int>(labels.size()) - labelsWritten,
                                     25); // Batch write has a limit of 25.
        std::vector<std::string> labelsThisWrite(labels.cbegin() + labelsWritten,
                                                 labels.cbegin() + labelsWritten +
                                                 labelsToWrite);
        labelsWritten += labelsToWrite;

        Aws::Vector<Aws::DynamoDB::Model::WriteRequest> writeRequests;
        for (const auto &label: labelsThisWrite) {
            Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> attributes;
            attributes[LABEL_KEY] = Aws::DynamoDB::Model::AttributeValue().SetS(label);
            Aws::Vector<std::shared_ptr<Aws::DynamoDB::Model::AttributeValue>> imageKeys;
            auto iter = mapOfImageKeys.find(label);
            if (iter != mapOfImageKeys.cend()) {
                imageKeys = iter->second;
            }
            std::shared_ptr<Aws::DynamoDB::Model::AttributeValue> value = std::make_shared<Aws::DynamoDB::Model::AttributeValue>();
            value->SetS(bucketKey);
            imageKeys.push_back(value);
            attributes[IMAGES_KEY] = Aws::DynamoDB::Model::AttributeValue().SetL(
                    imageKeys);
            attributes[COUNT_KEY] = Aws::DynamoDB::Model::AttributeValue().SetN(
                    static_cast<int>(imageKeys.size()));
            Aws::DynamoDB::Model::PutRequest putRequest;
            putRequest.SetItem(attributes);
            writeRequests.push_back(
                    Aws::DynamoDB::Model::WriteRequest().WithPutRequest(putRequest));
        }

        Aws::DynamoDB::Model::BatchWriteItemRequest batchWriteItemRequest;
        batchWriteItemRequest.AddRequestItems(databaseName, writeRequests);

        Aws::DynamoDB::Model::BatchWriteItemOutcome outcome = dbClient.BatchWriteItem(
                batchWriteItemRequest);

        if (!outcome.IsSuccess()) {
            errStream << "Error with DynamoDB::BatchWriteItem. "
                      << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    }

    return true;
}


//! Routine which returns the labels and their associated counts from a DynamoDB table.
/*!
  \param databaseName: A DynamoDB table name.
  \param labelAndCounts: A vector to receive the labels and counts.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::PAM::getLabelsAndCounts(const std::string &databaseName,
                                     std::vector<LabelAndCounts> &labelAndCounts,
                                     std::ostream &errStream,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {

    Aws::DynamoDB::DynamoDBClient dbClient(clientConfiguration);

    Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> lastEvaluatedKey;  // This is used for pagination of results.
    do {
        Aws::DynamoDB::Model::ScanRequest request;
        request.SetTableName(databaseName);

        if (!lastEvaluatedKey.empty())
        {
            request.SetExclusiveStartKey(lastEvaluatedKey);
        }

        request.SetProjectionExpression("#l, #c");

        Aws::Http::HeaderValueCollection headerValueCollection;
        headerValueCollection.emplace("#l", LABEL_KEY);
        headerValueCollection.emplace("#c", COUNT_KEY);
        request.SetExpressionAttributeNames(headerValueCollection);

        // Perform scan on table.
        const Aws::DynamoDB::Model::ScanOutcome &outcome = dbClient.Scan(request);
        if (outcome.IsSuccess()) {

            lastEvaluatedKey = outcome.GetResult().GetLastEvaluatedKey();

            const Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>> &items = outcome.GetResult().GetItems();
            for (const auto &item: items) {
                LabelAndCounts labelAndCount;
                for (const auto &entry: item) {
                    if (entry.first == LABEL_KEY) {
                        labelAndCount.mLabel = entry.second.GetS();
                    }
                    else if (entry.first == COUNT_KEY) {
                        try {
                            labelAndCount.mCount = std::stoi(entry.second.GetN());
                        }
                        catch (std::invalid_argument const &ex) {
                            errStream << "std::invalid_argument::what(): " << ex.what()
                                      << std::endl;
                            return false;
                        }
                        catch (std::out_of_range const &ex) {
                            errStream << "std::out_of_range::what(): " << ex.what()
                                      << std::endl;
                            return false;
                        }
                    }
                }

                if (!labelAndCount.mLabel.empty() && labelAndCount.mCount > 0) {
                    labelAndCounts.push_back(labelAndCount);
                }
                else {
                    errStream << "Table entry incorrect label '" << labelAndCount.mLabel
                              << "' count " <<
                              labelAndCount.mCount << std::endl;
                    return false;
                }
            }
        }
        else {
            errStream << "Failed to Scan items: " << outcome.GetError().GetMessage()
                      << std::endl;
            return false;
        }
    } while (!lastEvaluatedKey.empty());

    return true;
}

//! Routine that retrieves the S3 bucket keys for images associated with labels by querying
//! an Amazon DynamoDB table.
/*!
  \param databaseName: A DynamoDB table name.
  \param labels: A vector of labels as DynamoDB table keys.
  \param mapOfImageKeys: A map to receive the S3 object keys for images associated with labels.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::PAM::getKeysForLabelsFromDatabase(const std::string &databaseName,
                                               const std::vector<std::string> &labels,
                                               AttributeValueMap &mapOfImageKeys,
                                               std::ostream &errStream,
                                               const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::DynamoDB::DynamoDBClient dbClient(clientConfiguration);

    Aws::DynamoDB::Model::KeysAndAttributes tableKeysAndAttributes;
    tableKeysAndAttributes.SetProjectionExpression("#l, #c,  #i");

    Aws::Http::HeaderValueCollection headerValueCollection;
    headerValueCollection.emplace("#l", LABEL_KEY);
    headerValueCollection.emplace("#c", COUNT_KEY);
    headerValueCollection.emplace("#i", IMAGES_KEY);
    tableKeysAndAttributes.SetExpressionAttributeNames(headerValueCollection);

    for (const std::string &imageLabel: labels) {
        Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> keys;
        Aws::DynamoDB::Model::AttributeValue key;
        key.SetS(imageLabel);
        keys.emplace("Label", key);
        tableKeysAndAttributes.AddKeys(keys);
    }

    Aws::Map<Aws::String, Aws::DynamoDB::Model::KeysAndAttributes> requestItems;
    requestItems.emplace(databaseName, tableKeysAndAttributes);

    Aws::DynamoDB::Model::BatchGetItemRequest request;
    request.SetRequestItems(requestItems);

    const Aws::DynamoDB::Model::BatchGetItemOutcome batchGetOutcome = dbClient.BatchGetItem(
            request);
    if (batchGetOutcome.IsSuccess()) {
        const Aws::Map<Aws::String, Aws::Vector<Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>>> &responses =
                batchGetOutcome.GetResult().GetResponses();

        auto iter = responses.cbegin();
        if (iter != responses.cend()) {
            for (const auto &list: iter->second) {
                std::string label;
                Aws::Vector<std::shared_ptr<Aws::DynamoDB::Model::AttributeValue>> imageKeys;
                for (const auto &entry: list) {
                    if (entry.first == LABEL_KEY) {
                        label = entry.second.GetS();
                    }
                    else if (entry.first == IMAGES_KEY) {
                        imageKeys = entry.second.GetL();
                    }
                }

                if (!label.empty() && !imageKeys.empty()) {
                    mapOfImageKeys[label] = imageKeys;
                }
                else {
                    errStream << "Table entry incorrect label '" << label
                              << "' images count "
                              << imageKeys.size() << ".";
                }
            }
        }
    }
    else {
        errStream
                << "AwsDoc::PAM::getKeysForLabelsFromDatabase - error with BatchGetItem. "
                << batchGetOutcome.GetError().GetMessage() << std::endl;

    }

    return batchGetOutcome.IsSuccess();
}

//! A libzip callback routine for an Aws::IOStream as a data source.
/*!
 \param userdata: The image_zip_data user data.
 \param data: A pointer for returning data.
 \param len: The length of data to return.
 \param cmd: A zip source command.
 \return zip_int64_t: A zip_source result.
*/
zip_int64_t AwsDoc::PAM::my_zip_source(void *userdata, void *data, zip_uint64_t len,
                                       zip_source_cmd_t cmd) {
    auto imageZipData = static_cast<image_zip_data *>(userdata);
    zip_int64_t result;
    switch (cmd) {
        case ZIP_SOURCE_READ: {
            if (imageZipData->mIOStream == nullptr) {
                std::cerr << "AwsDoc::PAM::my_zip_source - IO stream is null. cmd  "
                          << cmd << std::endl;
                return ZIP_ER_INVAL;
            }
            imageZipData->mIOStream->read(static_cast<char *>(data),
                                          static_cast<std::streamsize>(len));
            auto bytesRead = imageZipData->mIOStream->gcount();
            result = bytesRead;
        }
            break;
        case ZIP_SOURCE_STAT : {
            if (imageZipData->mIOStream == nullptr) {
                std::cerr << "AwsDoc::PAM::my_zip_source - IO stream is null. cmd  "
                          << cmd << std::endl;
                return ZIP_ER_INVAL;
            }
            auto stat = static_cast<struct zip_stat *>(data);
            stat->mtime = time(nullptr);
            auto currentPos = imageZipData->mIOStream->tellg();
            imageZipData->mIOStream->seekg(0, std::ios::end);
            stat->size = imageZipData->mIOStream->tellg();
            imageZipData->mIOStream->seekg(currentPos);
            result = 0;
        }
            break;

        case ZIP_SOURCE_SUPPORTS :
            result = zip_source_make_command_bitmap(ZIP_SOURCE_OPEN, ZIP_SOURCE_READ,
                                                    ZIP_SOURCE_CLOSE, ZIP_SOURCE_STAT,
                                                    ZIP_SOURCE_ERROR);
            break;

        default :
            result = 0;
            break;
    }

    return result;
}

//! Routine which uploads a zip file of images associated with a list of labels to an S3 bucket.
/*!
  \param databaseName: A DynamoDB table name.
  \param sourceBucket: The S3 bucket storing the images.
  \param destinationBucket: The S3 bucket for the uploaded zip file.
  \param destinationKey: The S3 object key for the uploaded zip file.
  \param labels: The labels associated with the images.
  \param preSignedURL: A string to receive a presigned URL for the zip file.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::PAM::zipAndUploadImages(const std::string &databaseName,
                                     const std::string &sourceBucket,
                                     const std::string &destinationBucket,
                                     const std::string &destinationKey,
                                     const std::vector<std::string> &labels,
                                     std::string &preSignedURL,
                                     std::ostream &errStream,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {

    std::set<std::string> imageKeys;
    AttributeValueMap mapOfImageKeys;

    if (!getKeysForLabelsFromDatabase(databaseName, labels, mapOfImageKeys,
                                      errStream, clientConfiguration)) {
        return false;
    }

    for (const auto &entry: mapOfImageKeys) {
        const auto &keyAttributes = entry.second;
        for (const auto &keyAttribute: keyAttributes) {
            imageKeys.insert(keyAttribute->GetS());
        }
    }


    Aws::S3::S3Client s3Client(clientConfiguration);
    std::string tempZipFileName("/tmp/temp.zip");
    bool firstTime = true;
    for (const auto &imageKey: imageKeys) {
        Aws::S3::Model::GetObjectRequest request;
        request.SetBucket(sourceBucket);
        request.SetKey(imageKey);

        Aws::S3::Model::GetObjectOutcome outcome = s3Client.GetObject(request);

        if (!outcome.IsSuccess()) {
            errStream << "AwsDoc::PAM::zipAndUploadImages - error with GetObject. "
                      << outcome.GetError().GetMessage() << std::endl;
            return false;
        }
        else {
            int zipError = 0;

            zip_t *zipFile = zip_open(tempZipFileName.c_str(),
                                      firstTime ? (ZIP_CREATE | ZIP_TRUNCATE) : 0,
                                      &zipError);
            firstTime = false;
            if (zipFile == nullptr) {
                errStream << "AwsDoc::PAM::zipAndUploadImages - error with zip_open. "
                          << zipError << std::endl;
                return false;
            }

            image_zip_data imageZipData;
            imageZipData.mIOStream = &outcome.GetResult().GetBody();
            zip_error_t zipErrorType;
            zip_error_init(&zipErrorType);
            zip_source_t *zipSource = zip_source_function_create(my_zip_source,
                                                                 &imageZipData,
                                                                 &zipErrorType);
            if (zipSource == nullptr) {
                errStream
                        << "AwsDoc::PAM::zipAndUploadImages - error with zip_source_function_create. "
                        << zipErrorType.str << std::endl;
                zip_close(zipFile);
                return false;
            }
            auto err = zip_file_add(zipFile, imageKey.c_str(), zipSource,
                                    ZIP_FL_ENC_GUESS);
            if (err == -1) {
                errStream
                        << "AwsDoc::PAM::zipAndUploadImages - error with zip_file_add. "
                        << err << std::endl;
                zip_source_free(zipSource);
                zip_close(zipFile);
                return false;
            }
            zip_close(
                    zipFile);  // "zip_close" must be called on every iteration to add the data.
        }
    }

    Aws::S3::Model::PutObjectRequest putObjectRequest;
    putObjectRequest.SetKey(destinationKey);
    putObjectRequest.SetBucket(destinationBucket);
    std::shared_ptr<Aws::IOStream> inputData =
            Aws::MakeShared<Aws::FStream>("SampleAllocationTag",
                                          tempZipFileName.c_str(),
                                          std::ios_base::in | std::ios_base::binary);

    putObjectRequest.SetBody(inputData);

    auto putObjectOutcome = s3Client.PutObject(putObjectRequest);
    if (putObjectOutcome.IsSuccess()) {
        std::cout << "Zip file upload was successful." << std::endl;
    }
    else {
        errStream << "Error uploading zip file. "
                  << putObjectOutcome.GetError().GetMessage() << std::endl;
        return false;
    }

    preSignedURL = s3Client.GeneratePresignedUrl(destinationBucket, destinationKey,
                                                 Aws::Http::HttpMethod::HTTP_GET,
                                                 600 // expirationInSeconds
    );
    return true;
}

//! Routine which publishes a presigned URL to an Amazon Simple Notification Service (Amazon SNS)
//! topic.
/*!
  \param topicARN: An Amazon SNS topic name.
  \param preSignedURL: The presigned URL to publish.
  \param errStream: An std::iostream for error messaging.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::PAM::publishPreSignedURL(const std::string &topicARN,
                                      const std::string &preSignedURL,
                                      std::ostream &errStream,
                                      const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SNS::SNSClient snsClient(clientConfiguration);

    Aws::SNS::Model::PublishRequest publishRequest;
    publishRequest.SetMessage(
            ("Your archived images can downloaded with this link\n" + preSignedURL));
    publishRequest.SetTopicArn(topicARN);

    auto publishOutcome = snsClient.Publish(publishRequest);

    if (!publishOutcome.IsSuccess()) {
        errStream << "AwsDoc::PAM::publishPreSignedURL error. "
                  << publishOutcome.GetError().GetMessage() << std::endl;
    }

    return publishOutcome.IsSuccess();
}
