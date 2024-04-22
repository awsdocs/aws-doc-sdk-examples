// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0


#pragma once
#ifndef MEDICAL_IMAGING_EXAMPLES_MEDICAL_IMAGING_SAMPLES_H
#define MEDICAL_IMAGING_EXAMPLES_MEDICAL_IMAGING_SAMPLES_H

#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/SearchCriteria.h>

namespace AwsDoc {
    namespace Medical_Imaging {
        //! Routine which downloads a HealthImaging image frame.
        /*!
          \param dataStoreID The HealthImaging data store ID.
          \param imageSetID: The image set ID.
          \param frameID: The image frame ID.
          \param jphFile: Name of output file.
          \param clientConfig: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool getImageFrame(const Aws::String &dataStoreID,
                           const Aws::String &imageSetID,
                           const Aws::String &frameID,
                           const Aws::String &jphFile,
                           const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which gets a HealthImaging DICOM import job's properties.
        /*!
          \param dataStoreID: The HealthImaging data store ID.
          \param importJobID: The DICOM import job ID
          \param clientConfig: Aws client configuration.
          \return GetDICOMImportJobOutcome: The import job outcome.
        */
        Aws::MedicalImaging::Model::GetDICOMImportJobOutcome
        getDICOMImportJob(const Aws::String &dataStoreID,
                          const Aws::String &importJobID,
                          const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which gets a HealthImaging image set's metadata.
        /*!
          \param dataStoreID: The HealthImaging data store ID.
          \param imageSetID: The HealthImaging image set ID.
          \param versionID: The HealthImaging image set version ID, ignored if empty.
          \param outputFilePath: The path where the metadata will be stored as gzipped json.
          \param clientConfig: Aws client configuration.
          \return bool: Function succeeded.
        */
        bool getImageSetMetadata(const Aws::String &dataStoreID,
                                 const Aws::String &imageSetID,
                                 const Aws::String &versionID,
                                 const Aws::String &outputFilePath,
                                 const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which starts a HealthImaging import job.
        /*!
          \param dataStoreID: The HealthImaging data store ID.
          \param inputBucketName: The name of the Amazon S3 bucket containing the DICOM files.
          \param inputDirectory: The directory in the S3 bucket containing the DICOM files.
          \param outputBucketName: The name of the S3 bucket for the output.
          \param outputDirectory: The directory in the S3 bucket to store the output.
          \param roleArn: The ARN of the IAM role with permissions for the import.
          \param importJobId: A string to receive the import job ID.
          \param clientConfig: Aws client configuration.
          \return bool: Function succeeded.
          */
        bool startDICOMImportJob(
                const Aws::String &dataStoreID, const Aws::String &inputBucketName,
                const Aws::String &inputDirectory, const Aws::String &outputBucketName,
                const Aws::String &outputDirectory, const Aws::String &roleArn,
                Aws::String &importJobId,
                const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which deletes an AWS HealthImaging image set.
        /*!
          \param dataStoreID: The HealthImaging data store ID.
          \param imageSetID: The image set ID.
          \param clientConfig: Aws client configuration.
          \return bool: Function succeeded.
          */
        bool deleteImageSet(
                const Aws::String &dataStoreID, const Aws::String &imageSetID,
                const Aws::Client::ClientConfiguration &clientConfig);

        //! Routine which searches for image sets based on defined input attributes.
        /*!
          \param dataStoreID: The HealthImaging data store ID.
          \param searchCriteria: A search criteria instance.
          \param imageSetResults: Vector to receive the image set IDs.
          \param clientConfig: Aws client configuration.
          \return bool: Function succeeded.
          */
        bool searchImageSets(const Aws::String &dataStoreID,
                             const Aws::MedicalImaging::Model::SearchCriteria &searchCriteria,
                             Aws::Vector<Aws::String> &imageSetResults,
                             const Aws::Client::ClientConfiguration &clientConfig);


    } // namespace Medical_Imaging
} // namespace AwsDoc


#endif //MEDICAL_IMAGING_EXAMPLES_MEDICAL_IMAGING_SAMPLES_H
