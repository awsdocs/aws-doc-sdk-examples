// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0


#include <iostream>
#include <aws/core/Aws.h>
#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/StartDICOMImportJobRequest.h>
#include "medical-imaging_samples.h"

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

// snippet-start:[cpp.example_code.medical_imaging.StartDICOMImportJob]
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
bool AwsDoc::Medical_Imaging::startDICOMImportJob(
        const Aws::String &dataStoreID, const Aws::String &inputBucketName,
        const Aws::String &inputDirectory, const Aws::String &outputBucketName,
        const Aws::String &outputDirectory, const Aws::String &roleArn,
        Aws::String &importJobId,
        const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::MedicalImaging::MedicalImagingClient medicalImagingClient(clientConfig);
    Aws::String inputURI = "s3://" + inputBucketName + "/" + inputDirectory + "/";
    Aws::String outputURI = "s3://" + outputBucketName + "/" + outputDirectory + "/";
    Aws::MedicalImaging::Model::StartDICOMImportJobRequest startDICOMImportJobRequest;
    startDICOMImportJobRequest.SetDatastoreId(dataStoreID);
    startDICOMImportJobRequest.SetDataAccessRoleArn(roleArn);
    startDICOMImportJobRequest.SetInputS3Uri(inputURI);
    startDICOMImportJobRequest.SetOutputS3Uri(outputURI);

    Aws::MedicalImaging::Model::StartDICOMImportJobOutcome startDICOMImportJobOutcome = medicalImagingClient.StartDICOMImportJob(
            startDICOMImportJobRequest);

    if (startDICOMImportJobOutcome.IsSuccess()) {
        importJobId = startDICOMImportJobOutcome.GetResult().GetJobId();
    }
    else {
        std::cerr << "Failed to start DICOM import job because "
                  << startDICOMImportJobOutcome.GetError().GetMessage() << std::endl;
    }

    return startDICOMImportJobOutcome.IsSuccess();
}

// snippet-end:[cpp.example_code.medical_imaging.StartDICOMImportJob]

/*
 *
 * main function
 *
 *  Usage: 'run_start_dicom_import job <data_store_id> <inputBucketName>
 *       <inputDirectory> <outputBucketName> <outputDirectory> <roleArn>'
 *
 *  Prerequisites:
 *     1. An HealthImaging data store.
 *     2. An S3 bucket with DICOM files.
 *     3. An S3 bucket for the output. This can be the same as the input S3 bucket.
 *     4. An IAM role with permissions for the import.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 7) {
        std::cout
                << "Usage: 'run_start_dicom_import job <data_store_id> <inputBucketName>\n"
                << "        <inputDirectory> <outputBucketName> <outputDirectory> <roleArn>'"
                << std::endl;
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String dataStoreID = argv[1];
        Aws::String inputBucketName = argv[2];
        Aws::String inputDirectory = argv[3];
        Aws::String outputBucketName = argv[4];
        Aws::String outputDirectory = argv[5];
        Aws::String roleArn = argv[6];
        Aws::String importJobIdOutput;

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        if (AwsDoc::Medical_Imaging::startDICOMImportJob(dataStoreID, inputBucketName,
                                                     inputDirectory, outputBucketName,
                                                     outputDirectory, roleArn,
                                                     importJobIdOutput, clientConfig))
        {
            std::cout << "Import job ID: " << importJobIdOutput << std::endl;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
