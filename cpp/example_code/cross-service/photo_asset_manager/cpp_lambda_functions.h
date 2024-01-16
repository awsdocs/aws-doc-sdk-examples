// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#pragma once
#ifndef PAM_EXAMPLES_GTESTS_CPP_LAMBDA_FUNCTIONS_H
#define PAM_EXAMPLES_GTESTS_CPP_LAMBDA_FUNCTIONS_H

#include <string>
#include <map>
#include <vector>
#include <iostream>
#include <aws/dynamodb/model/AttributeValue.h>
#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace PAM {

        extern const char LABEL_KEY[];

        // Struct to hold label and count.
        struct LabelAndCounts {
            LabelAndCounts() : mCount(0) {}

            std::string mLabel;
            int mCount = 0;
        };

        typedef std::map<std::string, std::vector<std::shared_ptr<Aws::DynamoDB::Model::AttributeValue>>> AttributeValueMap;

        //! Routine that returns a presigned Amazon Simple Storage Service (Amazon S3) upload URL.
        /*!
          \param bucket: An S3 bucket name.
          \param key: An S3 object key.
          \param clientConfiguration: AWS client configuration.
          \return std::string: The URL as a string.
         */
        std::string
        getPreSignedS3UploadURL(const std::string &bucket, const std::string &key,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine that analyzes an image and returns labels using Amazon Rekognition.
        /*!
          \param bucket: An S3 bucket name which contains the image.
          \param key: An S3 object key for the image.
          \param imageLabels: A vector to receive the label results.
          \param errStream: An std::iostream for error messaging.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool analyzeAndGetLabels(const std::string &bucket, const std::string &key,
                                 std::vector<std::string> &imageLabels,
                                 std::ostream &errStream,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

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
        bool getKeysForLabelsFromDatabase(const std::string &databaseName,
                                          const std::vector<std::string> &labels,
                                          AttributeValueMap &mapOfImageKeys,
                                          std::ostream &errStream,
                                          const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine that updates a DynamoDB table with labels and the associated S3 object key of the image.
        /*!
          \param databaseName: A DynamoDB table name.
          \param labels: A vector of labels as DynamoDB table keys.
          \param bucketKey: The S3 object key for the image.
          \param errStream: An std::iostream for error messaging.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateLabelsInDatabase(const std::string &databaseName,
                                    const std::vector<std::string> &labels,
                                    const std::string &bucketKey,
                                    std::ostream &errStream,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine which returns the labels and their associated counts from a DynamoDB table.
        /*!
          \param databaseName: A DynamoDB table name.
          \param labelAndCounts: A vector to receive the labels and counts.
          \param errStream: An std::iostream for error messaging.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getLabelsAndCounts(const std::string &databaseName,
                                std::vector<LabelAndCounts> &labelAndCounts,
                                std::ostream &errStream,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

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
        bool zipAndUploadImages(const std::string &databaseName,
                                const std::string &sourceBucket,
                                const std::string &destinationBucket,
                                const std::string &destinationKey,
                                const std::vector<std::string> &labels,
                                std::string &preSignedURL,
                                std::ostream &errStream,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Routine which publishes a presigned URL to an Amazon Simple Notification Service (Amazon SNS)
        //! topic.
        /*!
          \param topicARN: An Amazon SNS topic name.
          \param preSignedURL: The presigned URL to publish.
          \param errStream: An std::iostream for error messaging.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool publishPreSignedURL(const std::string &topicARN,
                                 const std::string &preSignedURL,
                                 std::ostream &errStream,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace PAM
} // namespace AwsDoc
#endif //PAM_EXAMPLES_GTESTS_CPP_LAMBDA_FUNCTIONS_H
