// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
delete_identity.cpp demonstrates how to delete an Amazon SES identity.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/DeleteIdentityRequest.h>
#include <iostream>

/**
 * Creates an ses receipt filter based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_identity <identity>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String identity(argv[1]);

    Aws::SES::SESClient ses;

    Aws::SES::Model::DeleteIdentityRequest di_req;

    di_req.SetIdentity(identity);

    auto di_out = ses.DeleteIdentity(di_req);

    if (di_out.IsSuccess())
    {
      std::cout << "Successfully deleted identity" << std::endl;
    }

    else
    {
      std::cout << "Error deleting identity" << di_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
