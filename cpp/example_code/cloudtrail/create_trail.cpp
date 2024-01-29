// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/CreateTrailRequest.h>
#include <aws/cloudtrail/model/CreateTrailResult.h>
#include <iostream>

/**
 * Creates a cloud trail on command line input
 */

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
  }

  Aws::ShutdownAPI(options);
  return 0;
}
