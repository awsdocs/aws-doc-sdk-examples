/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is drrstributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
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
