// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

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
