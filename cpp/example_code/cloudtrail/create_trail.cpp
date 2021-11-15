// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/*
Purpose:
create_trail.cpp demonstrates how to create an AWS CloudTrail resource using command-line input.
*/
//snippet-start:[ct.cpp.create_trail.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/CreateTrailRequest.h>
#include <aws/cloudtrail/model/CreateTrailResult.h>
#include <iostream>
//snippet-end:[ct.cpp.create_trail.inc]
int main(int argc, char **argv)
{
  if (argc != 3)
  {
    std::cout << "Usage: create_trail <trail_name> <bucket_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String trail_name(argv[1]);
    Aws::String bucket_name(argv[2]);
    //snippet-start:[ct.cpp.create_trail]
    Aws::CloudTrail::CloudTrailClient ct;

    Aws::CloudTrail::Model::CreateTrailRequest ct_req;
    ct_req.SetName(trail_name);
    ct_req.SetS3BucketName(bucket_name);

    auto ct_out = ct.CreateTrail(ct_req);

    if (ct_out.IsSuccess())
    {
      std::cout << "Successfully created cloud trail" << std::endl;
    }

    else
    {
      std::cout << "Error creating cloud trail " << ct_out.GetError().GetMessage()
        << std::endl;
    }
     //snippet-start:[ct.cpp.create_trail]
  }
  Aws::ShutdownAPI(options);
  return 0;
}
