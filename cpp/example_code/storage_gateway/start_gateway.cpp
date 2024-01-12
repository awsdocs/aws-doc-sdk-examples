// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/StartGatewayRequest.h>
#include <aws/storagegateway/model/StartGatewayResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: start_gateway <gateway_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String gateway_arn(argv[1]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::StartGatewayRequest sg_req;

    sg_req.SetGatewayARN(gateway_arn);

    auto sg_out = storagegateway.StartGateway(sg_req);

    if (sg_out.IsSuccess())
    {
      std::cout << "Successfully started gateway." << std::endl;
    }
    else
    {
      std::cout << "Error starting gateway." << sg_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
