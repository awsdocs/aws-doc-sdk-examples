//snippet-sourcedescription:[create_receipt_rule.cpp demonstrates how to create an Amazon SES receipt rule.]
//snippet-service:[ses]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]

/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/CreateReceiptRuleRequest.h>
#include <aws/email/model/CreateReceiptRuleResult.h>
#include <aws/email/model/ReceiptRule.h>
#include <aws/email/model/ReceiptAction.h>
#include <aws/email/model/TlsPolicy.h>
#include <aws/email/model/S3Action.h>
#include <iostream>

/**
 * Creates an ses receipt filter based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 7)
  {
    std::cout << "Usage: create_receipt_rule <s3_bucket_name> <s3_object_key_prefix>"
      "<rule_name> <rule_set_name> <tls_policy_val> <recipients_value>";
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
      std::cout << "Error creating receipt rule" << crr_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
