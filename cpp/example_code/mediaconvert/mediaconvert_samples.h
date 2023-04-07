/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef MEDIACONVERT_EXAMPLES_MEDIACONVERT_SAMPLES_H
#define MEDIACONVERT_EXAMPLES_MEDIACONVERT_SAMPLES_H

#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace MediaConvert {
        extern const char CACHED_ENDPOINT_FILE[];

        //! Create a an AWS Elemental MediaConvert job.
        /*!
          \param mediaConvertRole: An Amazon Resource Name (ARN) for the AWS Identity
                                   and Access Management (IAM) role for the job.
          \param fileInput: A URI to an input file that is stored in Amazon Simple
                            Storage Service (Amazon S3) or on an HTTP(S) server.
          \param fileOutput: A URI for an Amazon S3 output location and the output file name base.
          \param jobSettingsFile: An optional JSON settings file.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool createJob(const Aws::String &mediaConvertRole,
                       const Aws::String &fileInput,
                       const Aws::String &fileOutput,
                       const Aws::String &jobSettingsFile,
                       const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve the account API endpoint.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool
        describeEndpoints(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve a list of created jobs.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool listJobs(const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Retrieve the information for a specific completed transcoding job.
        /*!
          \param jobID: A job ID.
          \param clientConfiguration: AWS client configuration.
          \return bool: Function succeeded.
         */
        bool getJob(const Aws::String &jobID,
                    const Aws::Client::ClientConfiguration &clientConfiguration);

        //! Utility routine to handle caching of a retrieved endpoint.
        /*!
          \param clientConfiguration: AWS client configuration.
          \return Aws::String: The endpoint URI.
         */
        Aws::String getEndpointUriHelper(
                const Aws::Client::ClientConfiguration &clientConfiguration);
    } // namespace MediaConvert
} // namespace AwsDoc

#endif //MEDIACONVERT_EXAMPLES_MEDIACONVERT_SAMPLES_H
