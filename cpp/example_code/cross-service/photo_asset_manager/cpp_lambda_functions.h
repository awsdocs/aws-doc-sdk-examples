/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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

        //! Scenario to modify and query an Amazon DynamoDB table using single PartiQL statements.
        /*!
          \sa getPreSignedS3UploadURL()
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        std::string
        getPreSignedS3UploadURL(const std::string &bucket, const std::string &key,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        bool analyzeAndGetLabels(const std::string &bucket, const std::string &key,
                                 std::vector<std::string> &imageLabels,
                                 std::ostream &errStream,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);

        bool getKeysForLabelsFromDatabase(const std::string &databaseName,
                                          const std::vector<std::string> &labels,
                                          AttributeValueMap &mapOfImageKeys,
                                          std::ostream &errStream,
                                          const Aws::Client::ClientConfiguration &clientConfiguration);

        bool updateLabelsInDatabase(const std::string &databaseName,
                                    const std::vector<std::string> &labels,
                                    const std::string &bucketKey,
                                    std::ostream &errStream,
                                    const Aws::Client::ClientConfiguration &clientConfiguration);

        bool getLabelsAndCounts(const std::string &databaseName,
                                std::vector<LabelAndCounts> &labelAndCounts,
                                std::ostream &errStream,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        bool zipAndUploadImages(const std::string &databaseName,
                                const std::string &sourceBucket,
                                const std::string &destinationBucket,
                                const std::string &destinationKey,
                                const std::vector<std::string> &labels,
                                std::string &preSignedURL,
                                std::ostream &errStream,
                                const Aws::Client::ClientConfiguration &clientConfiguration);

        bool publishPreSignedURL(const std::string &topicARN,
                                 const std::string &preSignedURL,
                                 std::ostream &errStream,
                                 const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace PAM
} // namespace AwsDoc
#endif //PAM_EXAMPLES_GTESTS_CPP_LAMBDA_FUNCTIONS_H
