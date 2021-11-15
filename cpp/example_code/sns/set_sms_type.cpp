// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0

#include <aws/core/Aws.h>
#include <aws/sns/SNSClient.h>
#include <aws/sns/model/SetSMSAttributesRequest.h>
#include <aws/sns/model/SetSMSAttributesResult.h>
#include <iostream>

/**
 * Set the SMS type - demonstrates how to use Amazon SNS to set the DefaultSMSType attribute.
 *
 * For more information on SetSMSAttributes, see https://docs.aws.amazon.com/sns/latest/api/API_SetSMSAttributes.html.
 *
 * <sms_type> set to "Promotional" to indicate noncritical messages.
 */

int main(int argc, char ** argv)
{
  if (argc != 2)
  {
    std::cout << "Usage: set_sms_type <sms_type> " << std::endl;
    return 1;
  }
  // snippet-start:[sns.cpp.set_sms_type.code]
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::SNS::SNSClient sns;
    Aws::String sms_type =  argv[1];

    Aws::SNS::Model::SetSMSAttributesRequest ssmst_req;
    ssmst_req.AddAttributes("DefaultSMSType", sms_type);

    auto ssmst_out = sns.SetSMSAttributes(ssmst_req);

    if (ssmst_out.IsSuccess())
    {
      std::cout << "SMS Type set successfully " << std::endl;
    }
    else
    {
        std::cout << "Error while setting SMS Type: '" << ssmst_out.GetError().GetMessage()
            << "'" << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  // snippet-end:[sns.cpp.set_sms_type.code]
  return 0;
}
