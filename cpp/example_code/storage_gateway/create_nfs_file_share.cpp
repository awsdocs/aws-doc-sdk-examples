// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/CreateNFSFileShareRequest.h>
#include <aws/storagegateway/model/CreateNFSFileShareResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 5)
  {
    std::cout << "Usage: create_nfs_file_share <gateway_arn> <client_token> <location_arn> <role>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String gateway_arn(argv[1]);
    Aws::String client_token(argv[2]);
    Aws::String location_arn(argv[3]);
    Aws::String role(argv[4]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::CreateNFSFileShareRequest cnfsfs_req;

    cnfsfs_req.SetGatewayARN(gateway_arn);
    cnfsfs_req.SetRole(role);
    cnfsfs_req.SetLocationARN(location_arn);
    cnfsfs_req.AddClientList(client_token);

    auto cnfsfs_out = storagegateway.CreateNFSFileShare(cnfsfs_req);

    if (cnfsfs_out.IsSuccess())
    {
      std::cout << "Successfully created NFS File share" << std::endl;
    }
    else
    {
      std::cout << "Error creating NFS File share" << cnfsfs_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
