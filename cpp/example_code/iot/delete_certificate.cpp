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
#include <aws/iot/IoTClient.h>
#include <aws/iot/model/DeleteCertificateRequest.h>
#include <iostream>
#include "iot_samples.h"

// snippet-start:[cpp.example_code.iot.DeleteCertificate]
//! Delete a certificate.
/*!
  \param certificateID: The ID of a certificate.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::IoT::deleteCertificate(const Aws::String &certificateID,
                                    const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::IoT::IoTClient iotClient(clientConfiguration);

    Aws::IoT::Model::DeleteCertificateRequest request;
    request.SetCertificateId(certificateID);

    Aws::IoT::Model::DeleteCertificateOutcome outcome = iotClient.DeleteCertificate(
            request);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully deleted certificate " << certificateID << std::endl;
    }
    else {
        std::cerr << "Error deleting certificate " << certificateID << ": " <<
                  outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.iot.DeleteCertificate]

/*
 *
 *  main function
 *
 *  Usage: 'delete_certificate <certificate_id>'
 *
 */

#ifndef EXCLUDE_ACTION_MAIN

int main(int argc, char **argv) {
    if (argc != 2) {
        std::cout << "Usage: delete_certificate <certificate_id>" << std::endl;
        return 1;
    }
    Aws::SDKOptions options;

    Aws::InitAPI(options);
    {
        const Aws::String certificateID(argv[1]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::IoT::deleteCertificate(certificateID, clientConfig);
    }
    Aws::ShutdownAPI(options);
    return 0;
}

#endif // EXCLUDE_ACTION_MAIN

