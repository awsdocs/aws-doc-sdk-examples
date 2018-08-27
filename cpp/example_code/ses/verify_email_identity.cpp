/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
#include <aws/email/model/VerifyEmailIdentityRequest.h>
#include <aws/email/model/VerifyEmailIdentityResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: verify_email_address <email_address>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String email_address(argv[1]);
    Aws::SES::SESClient ses;

    Aws::SES::Model::VerifyEmailIdentityRequest vea_req;

    vea_req.SetEmailAddress(email_address);

    auto vea_out = ses.VerifyEmailIdentity(vea_req);

    if (vea_out.IsSuccess())
    {
      std::cout << "Email verification initiated" << std::endl;
    }

    else
    {
      std::cout << "Error initiating email verification" << vea_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
