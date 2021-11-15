// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/GetSMSAttributesRequest.h>
#include <aws/sns/model/GetSMSAttributesResult.h>
#include <iostream>

/**
 * Get the SMS type - demonstrates how to retrieve the settings for sending Amazon SMS messages.
 *
 * For more information on SetSMSAttributes, see https://docs.aws.amazon.com/sns/latest/api/API_SetSMSAttributes.html.
 */

int main(int argc, char ** argv)
{
  if (argc != 1)
  {
    std::cout << "Usage: get_sms_type" << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.get_sms_type.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;

    Aws::SNS::Model::GetSMSAttributesRequest gsmst_req;
    //Set the request to only retrieve the DefaultSMSType setting.
    //Without the following line, GetSMSAttributes would retrieve all settings.
    gsmst_req.AddAttributes("DefaultSMSType");

    auto gsmst_out = sns.GetSMSAttributes(gsmst_req);

    if (gsmst_out.IsSuccess())
    {
        for (auto const& att : gsmst_out.GetResult().GetAttributes())
        {
            std::cout <<  att.first << ":  " <<  att.second << std::endl;
        }
    }
    else
    {
      std::cout << "Error while getting SMS Type: '" << gsmst_out.GetError().GetMessage()
        << "'" << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.get_sms_type.code]
  return 0;
}
