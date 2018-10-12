 
//snippet-sourcedescription:[<<FILENAME>> demonstrates how to ...]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-service:[Amazon Elastic Block Store]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[]
//snippet-sourceauthor:[tapasweni-pathak]


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
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DescribeVolumesRequest.h>
#include <aws/ec2/model/DescribeVolumesResponse.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: describe_volumes" << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::DescribeVolumesRequest dv_req;

    auto dv_out = ec2.DescribeVolumes(dv_req);

    if (dv_out.IsSuccess())
    {
      std::cout << "Successfully describing volumes as:";
      for (auto val: dv_out.GetResult().GetVolumes())
      {
        std::cout << " " << val.GetVolumeId();
      }
      std::cout << std::endl;
    }
    else
    {
      std::cout << "Error describing volumes" << dv_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
