 
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
#include <aws/storagegateway/StorageGatewayClient.h>
#include <aws/storagegateway/model/StartGatewayRequest.h>
#include <aws/storagegateway/model/StartGatewayResult.h>
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

    Aws::StorageGateway::StorageGatewayClient sg;

    Aws::StorageGateway::Model::StartGatewayRequest sg_req;

    sg.SetGatewayARN(gateway_arn);

    auto sg_out = storagegatway.StartGateway(sg_req);

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
