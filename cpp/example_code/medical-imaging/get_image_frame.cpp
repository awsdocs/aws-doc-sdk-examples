// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <iostream>
#include <aws/core/Aws.h>
#include <fstream>
#include <aws/medical-imaging/MedicalImagingClient.h>
#include <aws/medical-imaging/model/GetImageFrameRequest.h>
#include "medical-imaging_samples.h"

/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to download an AWS HealthImaging image frame.
 *
 */

// snippet-start:[cpp.example_code.medical-imaging.GetImageFrame]
//! Routine which downloads an AWS HealthImaging image frame.
/*!
  \param dataStoreID: The HealthImaging data store ID.
  \param imageSetID: The image set ID.
  \param frameID: The image frame ID.
  \param jphFile: File to store the downloaded frame.
  \param clientConfig: Aws client configuration.
  \return bool: Function succeeded.
*/
bool AwsDoc::Medical_Imaging::getImageFrame(const Aws::String &dataStoreID,
                                            const Aws::String &imageSetID,
                                            const Aws::String &frameID,
                                            const Aws::String &jphFile,
                                            const Aws::Client::ClientConfiguration &clientConfig) {
    Aws::MedicalImaging::MedicalImagingClient client(clientConfig);

    Aws::MedicalImaging::Model::GetImageFrameRequest request;
    request.SetDatastoreId(dataStoreID);
    request.SetImageSetId(imageSetID);

    Aws::MedicalImaging::Model::ImageFrameInformation imageFrameInformation;
    imageFrameInformation.SetImageFrameId(frameID);
    request.SetImageFrameInformation(imageFrameInformation);

    Aws::MedicalImaging::Model::GetImageFrameOutcome outcome = client.GetImageFrame(
            request);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully retrieved image frame." << std::endl;
        auto &buffer = outcome.GetResult().GetImageFrameBlob();

        std::ofstream outfile(jphFile, std::ios::binary);
        outfile << buffer.rdbuf();
    }
    else {
        std::cout << "Error retrieving image frame." << outcome.GetError().GetMessage()
                  << std::endl;

    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.medical-imaging.GetImageFrame]

/*
 *
 * main function
 *
 * Prerequisites: An image set in a HealthImaging data store.
 *
 *  Usage: 'run_get_image_frame <datastore_id> <image_set_id> <frame_id>'
 *
*/

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 5) {
        std::cout
                << "Usage: run_get_image_frame <datastore_id> <image_set_id> <frame_id> <image_file>"
                << std::endl;
        return 1;
    }
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        Aws::String dataStoreID = argv[1];
        Aws::String imageSetID = argv[2];
        Aws::String frameID = argv[3];
        Aws::String jphFile = argv[4];

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region in which the bucket was created (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::Medical_Imaging::getImageFrame(dataStoreID, imageSetID, frameID,
                                               jphFile,
                                               clientConfig);
    }
    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD

