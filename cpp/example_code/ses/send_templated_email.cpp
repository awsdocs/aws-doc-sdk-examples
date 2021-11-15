// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
send_templated_email.cpp demonstrates how to compose an Amazon SES templated email and queue it for sending.
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/SendTemplatedEmailRequest.h>
#include <aws/email/model/SendTemplatedEmailResult.h>
#include <aws/email/model/Destination.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 6)
  {
    std::cout << "Usage: send_templated_email <template_name> <template_data>"
      "<sender_email_address> <cc_address> <reply_to_address> <to_addresses>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String template_name(argv[1]);
    Aws::String template_data(argv[2]);
    Aws::String sender_email_address(argv[3]);
    Aws::String cc_address(argv[4]);
    Aws::String reply_to_address(argv[5]);
    Aws::SES::SESClient ses;

    Aws::SES::Model::SendTemplatedEmailRequest ste_req;
    Aws::SES::Model::Destination destination;

    destination.AddCcAddresses(cc_address);

    for (int i = 6; i < argc; ++i)
    {
      destination.AddToAddresses(argv[i]);
    }

    ste_req.SetDestination(destination);
    ste_req.SetTemplate(template_name);
    ste_req.SetTemplateData(template_data);
    ste_req.SetSource(sender_email_address);
    ste_req.AddReplyToAddresses(reply_to_address);

    auto ste_out = ses.SendTemplatedEmail(ste_req);

    if (ste_out.IsSuccess())
    {
      std::cout << "Successfully sent templated message " << ste_out.GetResult().GetMessageId()
        << std::endl;
    }

    else
    {
      std::cout << "Error sending templated message " << ste_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
