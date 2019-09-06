//snippet-sourcedescription:[create_receipt_rule_set.cpp demonstrates how to create an empty Amazon SES rule set.]
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
#include <aws/email/model/CreateReceiptRuleSetRequest.h>
#include <aws/email/model/CreateReceiptRuleSetResult.h>
#include <iostream>

/**
 * Creates an ses receipt filter based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_receipt_rule_set <rule_set_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String rule_set_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::CreateReceiptRuleSetRequest crrs_req;

    crrs_req.SetRuleSetName(rule_set_name);

    auto crrs_out = ses.CreateReceiptRuleSet(crrs_req);

    if (crrs_out.IsSuccess())
    {
      std::cout << "Successfully created receipt rule set" << std::endl;
    }

    else
    {
      std::cout << "Error creating receipt rule set" << crrs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
