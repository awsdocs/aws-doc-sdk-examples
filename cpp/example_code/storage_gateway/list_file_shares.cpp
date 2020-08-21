 
//snippet-sourcedescription:[list_file_shares.cpp demonstrates how to retrieve a list of the file shares for an AWS Storage Gateway resource.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[AWS Storage Gateway]
//snippet-service:[storagegateway]
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
