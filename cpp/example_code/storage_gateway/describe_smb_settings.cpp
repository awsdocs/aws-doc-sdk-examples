// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
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
