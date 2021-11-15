// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_receipt_rule.cpp demonstrates how to delete an Amazon SES receipt rule.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/DeleteReceiptRuleRequest.h>
#include <aws/email/model/DeleteReceiptRuleResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: delete_receipt_rule <rule_name> <rule_set_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String rule_name(argv[1]);
    Aws::String rule_set_name(argv[2]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteReceiptRuleRequest drr_req;

    drr_req.SetRuleName(rule_name);
    drr_req.SetRuleSetName(rule_set_name);

    auto drr_out = ses.DeleteReceiptRule(drr_req);

    if (drr_out.IsSuccess())
    {
      std::cout << "Successfully deleted receipt rule" << std::endl;
    }

    else
    {
      std::cout << "Error deleting receipt rule" << drr_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
