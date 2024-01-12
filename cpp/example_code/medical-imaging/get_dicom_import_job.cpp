// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0


#include <iostream>
#include <aws/core/Aws.h>
#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/GetDICOMImportJobRequest.h>
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

// snippet-start:[cpp.example_code.medical_imaging.GetDICOMImportJob]
//! Routine which gets a HealthImaging DICOM import job's properties.
/*!
  \param dataStoreID: The HealthImaging data store ID.
  \param importJobID: The DICOM import job ID
  \param clientConfig: Aws client configuration.
  \return GetDICOMImportJobOutcome: The import job outcome.
*/
Aws::MedicalImaging::Model::GetDICOMImportJobOutcome
AwsDoc::Medical_Imaging::getDICOMImportJob(const Aws::String &dataStoreID,
                                           const Aws::String &importJobID,
                                           const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::MedicalImaging::MedicalImagingClient client(clientConfig);
    Aws::MedicalImaging::Model::GetDICOMImportJobRequest request;
    request.SetDatastoreId(dataStoreID);
    request.SetJobId(importJobID);
    Aws::MedicalImaging::Model::GetDICOMImportJobOutcome outcome = client.GetDICOMImportJob(
            request);
    if (!outcome.IsSuccess()) {
        std::cerr << "GetDICOMImportJob error: "
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome;
}
// snippet-end:[cpp.example_code.medical_imaging.GetDICOMImportJob]

/*
 *
 * main function
 *
 * Prerequisites: An image set in a HealthImaging data store.
 *
 *  Usage: 'run_get_dicom_import_job <data_store_id> <import_job_id>'
 *
 *  Prerequisites: The job ID of a DICOM import job in a HealthImaging data store.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout
                << "Usage: 'run_get_dicom_import_job <data_store_id> <import_job_id>'"
                << std::endl;
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String dataStoreID = argv[1];
        Aws::String importJobID = argv[2];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        Aws::MedicalImaging::Model::GetDICOMImportJobOutcome outcome = AwsDoc::Medical_Imaging::getDICOMImportJob(dataStoreID, importJobID, clientConfig);
        if (outcome.IsSuccess()) {
            std::cout << "GetDICOMImportJob: " << std::endl;
            std::cout << "  Job ID: " << outcome.GetResult().GetJobProperties().GetJobId() << std::endl;
        }
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

