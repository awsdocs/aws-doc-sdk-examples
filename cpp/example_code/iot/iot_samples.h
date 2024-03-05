// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0


#pragma once
#ifndef EXAMPLES_IOT_SAMPLES_H
#define EXAMPLES_IOT_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>
#include <aws/iot/model/UpdateIndexingConfigurationRequest.h>

namespace AwsDoc {
    namespace IoT {
        //! Workflow which demonstrates multiple operations on IoT things and shadows.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        IoTBasicsWorkflow(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Attach a principal to an AWS IoT thing.
        /*!
          \param principal: A principal to attach.
          \param thingName: The name for the thing.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool attachThingPrincipal(const Aws::String &principal,
                                  const Aws::String &thingName,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create keys and certificate for an Aws IoT device.
        //! This routine will save certificates and keys to an output folder, if provided.
        /*!
          \param outputFolder: Location for storing output in files, ignored when string is empty.
          \param certificateARNResult: A string to receive the Amazon Resource Name (ARN) of the created certificate.
          \param certificateID: A string to receive the ID of the created certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createKeysAndCertificate(const Aws::String &outputFolder,
                                      Aws::String &certificateARNResult,
                                      Aws::String &certificateID,
                                      const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an AWS IoT rule with an SNS topic as the target..
        /*!
          \param ruleName: The name for the rule.
          \param snsTopic: The SNS topic ARN for the action.
          \param sql: The SQL statement used to query the topic.
          \param roleARN: The IAM role ARN for the action.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createTopicRule(const Aws::String &ruleName,
                             const Aws::String &snsTopicARN, const Aws::String &sql,
                             const Aws::String &roleARN,
                             const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Create an AWS IoT thing.
        /*!
          \param thingName: The name for the thing.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createThing(const Aws::String &thingName,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete a certificate.
        /*!
          \param certificateID: The ID of a certificate.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteCertificate(const Aws::String &certificateID,
                               const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an AWS IoT thing.
        /*!
          \param thingName: The name for the thing.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteThing(const Aws::String &thingName,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Delete an AWS IoT rule.
        /*!
          \param ruleName: The name for the rule.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool deleteTopicRule(const Aws::String &ruleName,
                             const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe the endpoint specific to the AWS account making the call.
        /*!
          \param endpointResult: String to receive the endpoint result.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool describeEndpoint(Aws::String &endpointResult,
                              const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Describe an AWS IoT thing.
        /*!
          \param thingName: The name for the thing.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool describeThing(const Aws::String &thingName,
                           const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Detach a principal from an AWS IoT thing.
        /*!
          \param principal: A principal to detach.
          \param thingName: The name for the thing.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool detachThingPrincipal(const Aws::String &principal,
                                  const Aws::String &thingName,
                                  const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Get the shadow of an AWS IoT thing.
        /*!
          \param thingName: The name for the thing.
          \param documentResult: String to receive the state information, in JSON format.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getThingShadow(const Aws::String &thingName,
                            Aws::String &documentResult,
                            const Aws::Client::ClientConfiguration &clientConfiguration);

        //! List certificates registered in the AWS account making the call.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        listCertificates(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Lists the AWS IoT topic rules.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        listTopicRules(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Query the AWS IoT fleet index.
        //! For query information, see https://docs.aws.amazon.com/iot/latest/developerguide/query-syntax.html
        /*!
          \param: query: The query string.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool searchIndex(const Aws::String &query,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Update an AWS IoT thing with attributes.
        /*!
          \param thingName: The name for the thing.
          \param attributeMap: A map of key/value attributes.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateThing(const Aws::String &thingName,
                         const std::map<Aws::String, Aws::String> &attributeMap,
                         const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Update the indexing configuration.
        /*!
          \param thingIndexingConfiguration: A ThingIndexingConfiguration object which is ignored if not set.
          \param thingGroupIndexingConfiguration: A ThingGroupIndexingConfiguration object which is ignored if not set.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateIndexingConfiguration(
                const Aws::IoT::Model::ThingIndexingConfiguration &thingIndexingConfiguration,
                const Aws::IoT::Model::ThingGroupIndexingConfiguration &thingGroupIndexingConfiguration,
                const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Update the shadow of an AWS IoT thing.
        /*!
          \param thingName: The name for the thing.
          \param document: The state information, in JSON format.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool updateThingShadow(const Aws::String &thingName,
                               const Aws::String &document,
                               const Aws::Client::ClientConfiguration &clientConfiguration);
    }
}

#endif //EXAMPLES_IOT_SAMPLES_H
