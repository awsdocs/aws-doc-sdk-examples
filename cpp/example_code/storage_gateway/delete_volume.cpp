// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/DeleteVolumeRequest.h>
#include <aws/storagegateway/model/DeleteVolumeResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_volume <volume_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String volume_arn(argv[1]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::DeleteVolumeRequest dv_req;

    dv_req.SetVolumeARN(volume_arn);

    auto dv_out = storagegateway.DeleteVolume(dv_req);

    if (dv_out.IsSuccess())
    {
      std::cout << "Successfully deleted volume arn." << std::endl;
    }
    else
    {
      std::cout << "Error deleting volume arn." << dv_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
