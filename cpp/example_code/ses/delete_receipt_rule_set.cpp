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
#include <aws/email/model/DeleteReceiptRuleSetRequest.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.DeleteReceiptRuleSet]
//! Delete an Amazon Simple Email Service (Amazon SES) receipt rule set.
/*!
  \param receiptRuleSetName: The name for the receipt rule set.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::deleteReceiptRuleSet(const Aws::String &receiptRuleSetName,
                                       const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::DeleteReceiptRuleSetRequest deleteReceiptRuleSetRequest;

    deleteReceiptRuleSetRequest.SetRuleSetName(receiptRuleSetName);

    Aws::SES::Model::DeleteReceiptRuleSetOutcome outcome = sesClient.DeleteReceiptRuleSet(
            deleteReceiptRuleSetRequest);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully deleted receipt rule set." << std::endl;
    }

    else {
        std::cerr << "Error deleting receipt rule set. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.DeleteReceiptRuleSet]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: Usage: run_delete_receipt_rule_set <rule_set_name>'
 *
 *  Prerequisites: An existing SES receipt rule set to delete.
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: run_delete_receipt_rule_set <rule_set_name>";
    return 1;
  }
  Aws::SDKOptions options;
    Aws::InitAPI(options);
  {
    Aws::String ruleSetName(argv[1]);

    Aws::Client::ClientConfiguration clientConfig;
      // Optional: Set to the AWS Region (overrides config file).
      // clientConfig.region = "us-east-1";

      AwsDoc::SES::deleteReceiptRuleSet(ruleSetName, clientConfig);
   }

  Aws::ShutdownAPI(options);
  return 0;
}

#endif // TESTING_BUILD