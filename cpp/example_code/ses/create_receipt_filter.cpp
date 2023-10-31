/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
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
#include <aws/email/SESClient.h>
#include <aws/email/model/CreateReceiptFilterRequest.h>
#include <aws/email/model/ReceiptFilterPolicy.h>
#include <aws/email/model/ReceiptFilter.h>
#include <aws/email/model/ReceiptIpFilter.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.CreateReceiptFilter]
//! Create an Amazon Simple Email Service (Amazon SES) receipt filter..
/*!
  \param receiptFilterName: The name for the receipt filter.
  \param cidr: IP address or IP address range in Classless Inter-Domain Routing (CIDR) notation.
  \param policy: Block or allow enum of type ReceiptFilterPolicy.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::createReceiptFilter(const Aws::String &receiptFilterName,
                                      const Aws::String &cidr,
                                      Aws::SES::Model::ReceiptFilterPolicy policy,
                                      const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);
    Aws::SES::Model::CreateReceiptFilterRequest createReceiptFilterRequest;
    Aws::SES::Model::ReceiptFilter receiptFilter;
    Aws::SES::Model::ReceiptIpFilter receiptIpFilter;
    receiptIpFilter.SetCidr(cidr);
    receiptIpFilter.SetPolicy(policy);
    receiptFilter.SetName(receiptFilterName);
    receiptFilter.SetIpFilter(receiptIpFilter);
    createReceiptFilterRequest.SetFilter(receiptFilter);
    Aws::SES::Model::CreateReceiptFilterOutcome createReceiptFilterOutcome = sesClient.CreateReceiptFilter(
            createReceiptFilterRequest);
    if (createReceiptFilterOutcome.IsSuccess()) {
        std::cout << "Successfully created receipt filter." << std::endl;
    }
    else {
        std::cerr << "Error creating receipt filter: " <<
                  createReceiptFilterOutcome.GetError().GetMessage() << std::endl;
    }

    return createReceiptFilterOutcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.CreateReceiptFilter]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_create_receipt_filter <receipt_filter_name> <cidr_value> <receipt_filter_policy_val>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 4) {
        std::cout << "Usage: run_create_receipt_filter <receipt_filter_name> <cidr_value> <receipt_filter_policy_val>";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String receiptFilterName = argv[1];
        Aws::String cidrValue = argv[2];
        Aws::String receiptFilterPolicyVal = argv[3];
        if (receiptFilterPolicyVal != "ALLOW" && receiptFilterPolicyVal != "BLOCK") {
            std::cerr << "Invalid receipt filter policy value,  please use ALLOW or BLOCK" << std::endl;
            return 1;
        }

        Aws::SES::Model::ReceiptFilterPolicy policy =
                receiptFilterPolicyVal == "ALLOW" ? Aws::SES::Model::ReceiptFilterPolicy::Allow
                                                  : Aws::SES::Model::ReceiptFilterPolicy::Block;

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SES::createReceiptFilter(receiptFilterName, cidrValue, policy, clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD