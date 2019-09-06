//snippet-sourcedescription:[delete_template.cpp demonstrates how to delete an Amazon SES email template.]
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
#include <aws/email/model/DeleteTemplateRequest.h>
#include <aws/email/model/DeleteTemplateResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_template_request <template_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteTemplateRequest dt_req;

    dt_req.SetTemplateName(template_name);

    auto dt_out = ses.DeleteTemplate(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully deleted template" << std::endl;
    }

    else
    {
      std::cout << "Error deleting template" << dt_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
