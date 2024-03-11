// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved. 
// SPDX-License-Identifier: Apache-2.0


#include <iostream>
#include <aws/core/Aws.h>
#include <fstream>
#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/GetImageSetMetadataRequest.h>
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
// snippet-start:[cpp.example_code.medical_imaging.GetImageSetMetadata]
//! Routine which gets a HealthImaging image set's metadata.
/*!
  \param dataStoreID: The HealthImaging data store ID.
  \param imageSetID: The HealthImaging image set ID.
  \param versionID: The HealthImaging image set version ID, ignored if empty.
  \param outputFilePath: The path where the metadata will be stored as gzipped json.
  \param clientConfig: Aws client configuration.
  \\return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::getImageSetMetadata(const Aws::String &dataStoreID,
                                                  const Aws::String &imageSetID,
                                                  const Aws::String &versionID,
                                                  const Aws::String &outputFilePath,
                                                  const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::MedicalImaging::Model::GetImageSetMetadataRequest request;
    request.SetDatastoreId(dataStoreID);
    request.SetImageSetId(imageSetID);
    if (!versionID.empty()) {
        request.SetVersionId(versionID);
    }
    Aws::MedicalImaging::MedicalImagingClient client(clientConfig);
    Aws::MedicalImaging::Model::GetImageSetMetadataOutcome outcome = client.GetImageSetMetadata(
            request);
    if (outcome.IsSuccess()) {
        std::ofstream file(outputFilePath, std::ios::binary);
        auto &metadata = outcome.GetResult().GetImageSetMetadataBlob();
        file << metadata.rdbuf();
    }
    else {
        std::cerr << "Failed to get image set metadata: "
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.medical_imaging.GetImageSetMetadata]

/*
 *
 * main function
 *
 *  Usage: 'run_get_image_set_metadata <data_store_id> <image_set_id> <output_file_path>
 *         <version_id>'
 *
 *  Prerequisites: An existing HealthImaging image set.
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 5) {
        std::cout
                << "Usage: 'run_get_image_set_metadata <data_store_id> <image_set_id> <output_file_path>'\n"
                << "   <version_id>"
                << std::endl;
        return 1;
    }
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::String dataStoreID = argv[1];
        Aws::String imageSetID = argv[2];
        Aws::String outputFilePath = argv[3];
        Aws::String versionID = argv[4];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        // Get the metadata without specifying the version ID.
// snippet-start:[cpp.example_code.medical_imaging.GetImageSetMetadata.without_version]
        if (AwsDoc::Medical_Imaging::getImageSetMetadata(dataStoreID, imageSetID, "", outputFilePath, clientConfig))
        {
            std::cout << "Successfully retrieved image set metadata." << std::endl;
            std::cout << "Metadata stored in: " << outputFilePath << std::endl;
        }
// snippet-end:[cpp.example_code.medical_imaging.GetImageSetMetadata.without_version]

        // Get the metadata specifying the version ID.
// snippet-start:[cpp.example_code.medical_imaging.GetImageSetMetadata.with_version]
        if (AwsDoc::Medical_Imaging::getImageSetMetadata(dataStoreID, imageSetID, versionID, outputFilePath, clientConfig))
        {
            std::cout << "Successfully retrieved image set metadata." << std::endl;
            std::cout << "Metadata stored in: " << outputFilePath << std::endl;
        }
// snippet-end:[cpp.example_code.medical_imaging.GetImageSetMetadata.with_version]
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD
