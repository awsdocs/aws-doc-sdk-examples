// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/ListFileSharesRequest.h>
#include <aws/storagegateway/model/ListFileSharesResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: list_file_shares <gateway_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String gateway_arn(argv[1]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::ListFileSharesRequest lfs_req;

    lfs_req.SetGatewayARN(gateway_arn);

    auto lfs_out = storagegateway.ListFileShares(lfs_req);

    if (lfs_out.IsSuccess())
    {
      std::cout << "Successfully listing file shares";
      for (auto fileShareInfo: lfs_out.GetResult().GetFileShareInfoList())
      {
        std::cout << " " << fileShareInfo.GetFileShareId();
      }
    }
    else
    {
      std::cout << "Error listing File share" << lfs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
