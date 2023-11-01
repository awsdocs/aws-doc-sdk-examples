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
#include <aws/email/model/CreateReceiptRuleRequest.h>
#include <aws/email/model/ReceiptRule.h>
#include <aws/email/model/ReceiptAction.h>
#include <aws/email/model/TlsPolicy.h>
#include <aws/email/model/S3Action.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.CreateReceiptRule]
//! Create an Amazon Simple Email Service (Amazon SES) receipt rule.
/*!
  \param receiptRuleName: The name for the receipt rule.
  \param s3BucketName: The name of the S3 bucket for incoming mail.
  \param s3ObjectKeyPrefix: The prefix for the objects in the S3 bucket.
  \param ruleSetName: The name of the rule set where the receipt rule is added.
  \param recipients: Aws::Vector of recipients.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::createReceiptRule(const Aws::String &receiptRuleName,
                                    const Aws::String &s3BucketName,
                                    const Aws::String &s3ObjectKeyPrefix,
                                    const Aws::String &ruleSetName,
                                    const Aws::Vector<Aws::String> &recipients,
                                    const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::CreateReceiptRuleRequest createReceiptRuleRequest;

    Aws::SES::Model::S3Action s3Action;
    s3Action.SetBucketName(s3BucketName);
    s3Action.SetObjectKeyPrefix(s3ObjectKeyPrefix);

    Aws::SES::Model::ReceiptAction receiptAction;
    receiptAction.SetS3Action(s3Action);

    Aws::SES::Model::ReceiptRule receiptRule;
    receiptRule.SetName(receiptRuleName);
    receiptRule.WithRecipients(recipients);

    Aws::Vector<Aws::SES::Model::ReceiptAction> receiptActionList;
    receiptActionList.emplace_back(receiptAction);
    receiptRule.SetActions(receiptActionList);

    createReceiptRuleRequest.SetRuleSetName(ruleSetName);
    createReceiptRuleRequest.SetRule(receiptRule);

    auto outcome = sesClient.CreateReceiptRule(createReceiptRuleRequest);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully created receipt rule." << std::endl;
    }
    else {
        std::cerr << "Error creating receipt rule. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}

// snippet-end:[cpp.example_code.ses.CreateReceiptRule]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_create_receipt_filter <receipt_filter_name> <cidr_value> <receipt_filter_policy_val>'
 *
 *  Prerequisites: An S3 bucket for incoming mail.
 *
 */

#ifndef TESTING_BUILD


int main(int argc, char **argv) {
    if (argc < 5) {
        std::cout << "Usage: run_create_receipt_rule <s3_bucket_name> <s3_object_key_prefix>"
                     "<rule_name> <rule_set_name> <recipients_value>";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String s3BucketName(argv[1]);
        Aws::String s3ObjectKeyPrefix(argv[2]);

        Aws::String ruleName(argv[3]);
        Aws::String ruleSetName(argv[4]);

        Aws::Vector<Aws::String> recipients;
        for (int i = 5; i < argc; ++i) {
            recipients.emplace_back(argv[i]);
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SES::createReceiptRule(ruleName, s3BucketName, s3ObjectKeyPrefix, ruleSetName, recipients,
                                       clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD