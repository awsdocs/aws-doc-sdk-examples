// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/AttachVolumeRequest.h>
#include <aws/ec2/model/AttachVolumeResponse.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 4)
  {
    std::cout << "Usage: attach_volume <device_name> <instance_id> <volume_id>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String device_name(argv[1]);
    Aws::String instance_id(argv[2]);
    Aws::String volume_id(argv[3]);

    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::AttachVolumeRequest av_req;

    av_req.SetDevice(device_name);
    av_req.SetInstanceId(instance_id);
    av_req.SetVolumeId(volume_id);

    auto av_out = ec2.AttachVolume(av_req);

    if (av_out.IsSuccess())
    {
      std::cout << "Successfully attached volume at: " << av_out.GetResult().GetAttachTime().ToGmtString(Aws::Utils::DateFormat::RFC822)
                << std::endl;
    }
    else
    {
      std::cout << "Error describing volumes" << av_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
