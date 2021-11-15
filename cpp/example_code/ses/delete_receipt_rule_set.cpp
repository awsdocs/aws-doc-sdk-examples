// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_receipt_rule_set.cpp demonstrates how to delete an Amazon SES rule set.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/DeleteReceiptRuleSetRequest.h>
#include <aws/email/model/DeleteReceiptRuleSetResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_receipt_rule_set <rule_set_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String rule_set_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteReceiptRuleSetRequest drrs_req;

    drrs_req.SetRuleSetName(rule_set_name);

    auto drrs_out = ses.DeleteReceiptRuleSet(drrs_req);

    if (drrs_out.IsSuccess())
    {
      std::cout << "Successfully deleted receipt rule set" << std::endl;
    }

    else
    {
      std::cout << "Error deleting receipt rule set" << drrs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
