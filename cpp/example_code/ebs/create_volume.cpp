// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/CreateVolumeRequest.h>
#include <aws/ec2/model/CreateVolumeResponse.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: create_volume <az>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String az(argv[1]);

    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::CreateVolumeRequest av_req;

    av_req.SetAvailabilityZone(az);

    auto av_out = ec2.CreateVolume(av_req);

    if (av_out.IsSuccess())
    {
      std::cout << "Successfully create volume at: " << av_out.GetResult().GetCreateTime().ToGmtString(Aws::Utils::DateFormat::RFC822)
                << " with volume id: " << av_out.GetResult().GetVolumeId() << std::endl;
    }
    else
    {
      std::cout << "Error creating volume." << av_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
