//snippet-sourcedescription:[delete_receipt_rule_set.cpp demonstrates how to delete an Amazon SES rule set.]
//snippet-service:[ses]
//snippet-keyword:[Amazon Simple Email Service]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
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
