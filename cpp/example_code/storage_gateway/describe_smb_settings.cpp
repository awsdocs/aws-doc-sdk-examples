//snippet-sourcedescription:[describe_smb_settings.cpp demonstrates how to retrieve information pertaining to the Server Message Block (SMB) settings for an AWS Storage Gateway resource.]
//snippet-service:[storagegateway]
//snippet-keyword:[AWS Storage Gateway]
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
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/DescribeSMBSettingsRequest.h>
#include <aws/storagegateway/model/DescribeSMBSettingsResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: describe_smb_settings <gateway_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String gateway_arn(argv[1]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::DescribeSMBSettingsRequest dsmbs_req;

    dsmbs_req.SetGatewayARN(gateway_arn);

    auto dsmbs_out = storagegateway.DescribeSMBSettings(dsmbs_req);

    if (dsmbs_out.IsSuccess())
    {
      std::cout << "Successfully describing SMB settings as:";
      for (auto val : dsmbs_out.GetResult().GetDomainName())
      {
        std::cout << " " << val;
      }
    }
    else
    {
      std::cout << "Error describing SMB settings." << dsmbs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
