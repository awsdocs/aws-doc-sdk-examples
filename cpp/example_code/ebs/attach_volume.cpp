 
//snippet-sourcedescription:[attach_volume.cpp demonstrates how to attach an Amazon Elastic Block Store volume to an Amazon EC2 instance.]
//snippet-keyword:[C++]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon Elastic Block Store]
//snippet-service:[ebs]
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
