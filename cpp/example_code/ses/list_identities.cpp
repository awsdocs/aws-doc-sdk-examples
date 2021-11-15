// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
list_identities.cpp demonstrates how to list all the identities for an AWS account.
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
