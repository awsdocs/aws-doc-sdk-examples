// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
#include <aws/core/Aws.h>
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/DescribeGatewayInformationRequest.h>
#include <aws/storagegateway/model/DescribeGatewayInformationResult.h>
#include <aws/core/utils/Outcome.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: describe_gateway <gateway_arn>" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String gateway_arn(argv[1]);

    Aws::StorageGateway::StorageGatewayClient storagegateway;

    Aws::StorageGateway::Model::DescribeGatewayInformationRequest dgi_req;

    dgi_req.SetGatewayARN(gateway_arn);

    auto dgi_out = storagegateway.DescribeGatewayInformation(dgi_req);

    if (dgi_out.IsSuccess())
    {
      std::cout << "Successfully describing gateway as:";
      std::cout << " " << dgi_out.GetResult().GetGatewayId();
      std::cout << " " << dgi_out.GetResult().GetGatewayName();
      std::cout << std::endl;
    }
    else
    {
      std::cout << "Error describing gateway" << dgi_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
