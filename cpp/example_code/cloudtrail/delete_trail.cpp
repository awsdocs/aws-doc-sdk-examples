// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

/*
Purpose:

delete_trail.cpp demonstrates how to delete an AWS CloudTrail resource using command-line input.
*/
//snippet-start:[ct.cpp.delete_trial.inc]
#include <aws/core/Aws.h>
#include <aws/core/utils/Outcome.h>
#include <aws/cloudtrail/CloudTrailClient.h>
#include <aws/cloudtrail/model/DeleteTrailRequest.h>
#include <aws/cloudtrail/model/DeleteTrailResult.h>
#include <iostream>
//snippet-end:[ct.cpp.delete_trial.inc]

int main(int argc, char **argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: delete_trail <trail_name>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String trail_name(argv[1]);
    //snippet-start:[ct.cpp.delete_trial]
    Aws::CloudTrail::CloudTrailClient ct;

    Aws::CloudTrail::Model::DeleteTrailRequest dt_req;
    dt_req.SetName(trail_name);

    auto dt_out = ct.DeleteTrail(dt_req);

    if (dt_out.IsSuccess())
    {
      std::cout << "Successfully deleted cloud trail" << std::endl;
    }

    else
    {
      std::cout << "Error deleting cloud trail " << dt_out.GetError().GetMessage()
        << std::endl;
    }
  }
    //snippet-start:[ct.cpp.delete_trial]
  Aws::ShutdownAPI(options);
  return 0;
}
//snippet-end:[ct.cpp.delete_trial]
