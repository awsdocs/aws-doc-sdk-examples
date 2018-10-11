 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[<<ADD SERVICE>>]
//snippet-sourcetype:[<<snippet or full-example>>]
//snippet-sourcedate:[]
//snippet-sourceauthor:[AWS]


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
#include <aws/core/utils/Outcome.h>
#include <aws/glacier/GlacierClient.h>
#include <aws/glacier/model/UploadArchiveRequest.h>
#include <aws/glacier/model/UploadArchiveResult.h>
#include <iostream>

/**
 * Uploads archives based on command line input
 */

int main(int argc, char **argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: upload_archive <vault_name> <account_id> <archive_description>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String vault_name(argv[1]);
    Aws::String account_id(argv[2]);
    Aws::String archive_description(argv[3]);

    Aws::Glacier::GlacierClient glacier;

    Aws::Glacier::Model::UploadArchiveRequest ua_req;
    ua_req.SetVaultName(vault_name);
    ua_req.SetAccountId(account_id);
    ua_req.SetArchiveDescription(archive_description);


    auto ua_out = glacier.UploadArchive(ua_req);

    if (ua_out.IsSuccess())
    {
      std::cout << "Successfully upload archive with archive id: " << ua_out.GetResult().GetArchiveId()
        << std::endl;
    }

    else
    {
      std::cout << "Error upload archive" << ua_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
