 
//snippet-sourcedescription:[create_volume.cpp demonstrates how to create an Amazon Elastic Block Store volume for an Amazon EC2 instance.]
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
