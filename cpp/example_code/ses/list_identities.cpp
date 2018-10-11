//snippet-sourceauthor: [tapasweni-pathak]

//snippet-sourcedescription:[Description]

//snippet-service:[AWSService]

//snippet-sourcetype:[full example]

//snippet-sourcedate:[N/A]

/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is dtstributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/ListIdentitiesRequest.h>
#include <aws/email/model/ListIdentitiesResult.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_identities <identity_type>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SES::SESClient ses;

    Aws::SES::Model::ListIdentitiesRequest li_req;

    const Aws::String identityType = argv[1];

    if (identityType == "EmailAddress")
    {
      li_req.SetIdentityType(Aws::SES::Model::IdentityType::EmailAddress);
    }
    else if (identityType == "Domain")
    {
      li_req.SetIdentityType(Aws::SES::Model::IdentityType::Domain);
    }
    else
    {
      li_req.SetIdentityType(Aws::SES::Model::IdentityType::NOT_SET);
    }

    auto li_out = ses.ListIdentities(li_req);

    if (li_out.IsSuccess())
    {
      std::cout << "List of identities:";
      for (auto identities: li_out.GetResult().GetIdentities())
      {
        std::cout << " " << identities << std::endl;
      }
    }

    else
    {
      std::cout << "Error listing identities" << li_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
