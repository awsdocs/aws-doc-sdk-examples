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
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/SetSMSAttributesRequest.h>
#include <aws/sns/model/SetSMSAttributesResult.h>
#include <iostream>

/**
 * Set the sms type based on command line input
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: set_sms_type <sms_type> " << std::endl;
    return 1;
  }

  Aws::SDKOptions options;
  AWS::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String sms_type = argv[1];

    Aws::SNS::Model::SetSMSAttributesRequest ssmst_req;
    ssmst_req.AddAttributes("DefaultSMSStype", sms_type);

    auto ssmst_out = sns.SetSMSAttributes(ssmst_req);

    if (ssmst_out.IsSuccess())
    {
      std::cout << "SMS Type set successfully " << ssmst_out.GetResult() << std::endl;
    }
    else
    {
      std::cout << "Error while setting SMS Type " << gsmst_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
