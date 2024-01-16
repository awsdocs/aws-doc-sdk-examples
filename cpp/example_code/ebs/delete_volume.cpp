// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/ec2/EC2Client.h>
#include <aws/ec2/model/DeleteVolumeRequest.h>
#include <iostream>

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_volume <volume_id>"
              << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String volume_id(argv[1]);

    Aws::EC2::EC2Client ec2;

    Aws::EC2::Model::DeleteVolumeRequest dv_req;

    dv_req.SetVolumeId(volume_id);

    auto dv_out = ec2.DeleteVolume(dv_req);

    if (dv_out.IsSuccess())
    {
      std::cout << "Successfully deleted volume. " << std::endl;
    }
    else
    {
      std::cout << "Error deleting volume." << dv_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
