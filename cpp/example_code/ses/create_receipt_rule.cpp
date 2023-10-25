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

//! Create an Amazon SES receipt rule.
/*!
  \param receiptRuleName: The nane for the receipt rule.
  \param s3BucketName: The name of the S3 bucket for incoming mail.
  \param s3ObjectKeyPrefix: The prefix for the objects in the S3 bucket.
  \param ruleSetName: The name of the rule set where the receipt rule is added.
  \param recipients: Aws::Vector of recipients.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::createReceiptRule(const Aws::String &receiptRuleName, Aws::String &s3BucketName,
                       const Aws::String &s3ObjectKeyPrefix, const Aws::String &ruleSetName,
                       const Aws::Vector<Aws::String> &recipients,
                       const Aws::Client::ClientConfiguration &clientConfiguration)
{

}

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


int main(int argc, char **argv)
{
  if (argc < 6)
  {
    std::cout << "Usage: create_receipt_rule <s3_bucket_name> <s3_object_key_prefix>"
      "<rule_name> <rule_set_name> <recipients_value>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String s3_bucket_name(argv[1]);
    Aws::String s3_object_key_prefix(argv[2]);

    for (int i = 6; i < argc; ++i)
    {
      const Aws::String arg(argv[i]);
    }
    Aws::String rule_name(argv[3]);
    Aws::String rule_set_name(argv[4]);
    Aws::String tls_policy_val(argv[5]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::CreateReceiptRuleRequest crr_req;
    Aws::SES::Model::ReceiptRule receipt_rule;
    Aws::SES::Model::ReceiptAction receipt_actions;
    Aws::SES::Model::S3Action s3_action;

    if (tls_policy_val == "Require")
    {
      receipt_rule.SetTlsPolicy(Aws::SES::Model::TlsPolicy::Require);
    }
    else if (tls_policy_val == "Optional")
    {
      receipt_rule.SetTlsPolicy(Aws::SES::Model::TlsPolicy::Optional);
    }
    else
    {
      receipt_rule.SetTlsPolicy(Aws::SES::Model::TlsPolicy::NOT_SET);
    }

    s3_action.SetBucketName(s3_bucket_name);
    s3_action.SetObjectKeyPrefix(s3_object_key_prefix);

    receipt_actions.SetS3Action(s3_action);

    receipt_rule.SetName(rule_name);

    for (int i = 6; i < argc; ++i)
    {
      receipt_rule.AddRecipients(argv[i]);
    }
    Aws::Vector<Aws::SES::Model::ReceiptAction> receiptActionList;
    receiptActionList.emplace_back(receipt_actions);
    receipt_rule.SetActions(receiptActionList);

    crr_req.SetRuleSetName(rule_set_name);
    crr_req.SetRule(receipt_rule);

    auto crr_out = ses.CreateReceiptRule(crr_req);

    if (crr_out.IsSuccess())
    {
      std::cout << "Successfully created receipt rule" << std::endl;
    }

    else
    {
      std::cerr << "Error creating receipt rule" << crr_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}

#endif // TESTING_BUILD