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

#include <aws/core/Aws.h>
#include <aws/rekognition/RekognitionClient.h>
#include <aws/rekognition/model/DetectLabelsRequest.h>
#include <aws/rekognition/model/Image.h>
#include <iostream>
#include "rekognition_samples.h"

// snippet-start:[cpp.example_code.rekognition.DetectLabels]
//! Detect instances of real-world entities within an image by using Amazon Rekognition
/*!
  \param imageBucket: The Amazon Simple Storage Service (Amazon S3) bucket containing an image.
  \param imageKey: The Amazon S3 key of an image object.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::Rekognition::detectLabels(const Aws::String &imageBucket,
                                       const Aws::String &imageKey,
                                       const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::Rekognition::RekognitionClient rekognitionClient(clientConfiguration);

    Aws::Rekognition::Model::DetectLabelsRequest request;
    Aws::Rekognition::Model::S3Object s3Object;
    s3Object.SetBucket(imageBucket);
    s3Object.SetName(imageKey);

    Aws::Rekognition::Model::Image image;
    image.SetS3Object(s3Object);

    request.SetImage(image);

    const Aws::Rekognition::Model::DetectLabelsOutcome outcome = rekognitionClient.DetectLabels(request);

    if (outcome.IsSuccess()) {
        const Aws::Vector<Aws::Rekognition::Model::Label> &labels = outcome.GetResult().GetLabels();
        if (labels.empty()) {
            std::cout << "No labels detected" << std::endl;
        } else {
            for (const Aws::Rekognition::Model::Label &label: labels) {
                std::cout << label.GetName() << ": " << label.GetConfidence() << std::endl;
            }
        }
    } else {
        std::cerr << "Error while detecting labels: '"
                  << outcome.GetError().GetMessage()
                  << "'" << std::endl;
    }

    return outcome.IsSuccess();
}

// snippet-end:[cpp.example_code.rekognition.DetectLabels]

/*
 *  main function
 *
 *  Usage: 'run_detect_labels <bucket> <image_key>'
 *
 *  Prerequisites: An S3 bucket with an image.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cout << "Usage: run_detect_labels <bucket> <image_key>" << std::endl;
        return 1;
    }
    Aws::String bucket = argv[1];
    Aws::String imageKey = argv[2];
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::Rekognition::detectLabels(bucket, imageKey, clientConfig);
    }

    Aws::ShutdownAPI(options);

    return 0;
}

#endif // TESTING_BUILD