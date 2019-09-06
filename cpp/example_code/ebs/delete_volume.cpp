 
//snippet-sourcedescription:[delete_volume.cpp demonstrates how to delete an Amazon Elastic Block Store volume from an Amazon EC2 instance.]
//snippet-keyword:[C++]
//snippet-sourcesyntax:[cpp]
//snippet-sourcesyntax:[cpp]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Block Store]
//snippet-service:[ebs]
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
