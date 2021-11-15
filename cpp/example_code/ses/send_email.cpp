// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
/*
Purpose:
send_email.cpp demonstrates how to compose an Amazon SES email and queue it for sending.]
*/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/SendEmailRequest.h>
#include <aws/email/model/SendEmailResult.h>
#include <aws/email/model/Destination.h>
#include <aws/email/model/Message.h>
#include <aws/email/model/Body.h>
#include <aws/email/model/Content.h>
#include <iostream>

int main(int argc, char **argv)
{
  if (argc != 6)
  {
    std::cout << "Usage: send_email <message_body_html_data> <message_body_text_data>"
      "<message_subject_data> <sender_email_address> <cc_address> <reply_to_address>"
      "<to_addresses>";
    return 1;
  }
  Aws::SDKOptions options;
  Aws::InitAPI(options);
  {
    Aws::String message_body_html_data(argv[1]);
    Aws::String message_body_text_data(argv[2]);
    Aws::String message_subject_data(argv[3]);
    Aws::String sender_email_address(argv[4]);
    Aws::String cc_address(argv[5]);
    Aws::String reply_to_address(argv[6]);
    Aws::SES::SESClient ses;

    Aws::SES::Model::SendEmailRequest se_req;
    Aws::SES::Model::Destination destination;
    Aws::SES::Model::Message message;
    Aws::SES::Model::Body message_body;
    Aws::SES::Model::Content message_body_text;
    Aws::SES::Model::Content message_body_html;
    Aws::SES::Model::Content message_subject;


    destination.AddCcAddresses(cc_address);
    for (int i = 6; i < argc; ++i)
    {
      destination.AddToAddresses(argv[i]);
    }

    message_body_html.SetData(message_body_html_data);
    message_body_html.SetCharset("UTF-8");
    message_body_text.SetData(message_body_text_data);
    message_body_text.SetCharset("UTF-8");
    message_subject.SetData(message_subject_data);
    message_subject.SetCharset("UTF-8");

    message_body.SetText(message_body_text);
    message_body.SetHtml(message_body_html);

    message.SetBody(message_body);
    message.SetSubject(message_subject);

    se_req.SetDestination(destination);
    se_req.SetMessage(message);
    se_req.SetSource(sender_email_address);
    se_req.AddReplyToAddresses(reply_to_address);

    auto se_out = ses.SendEmail(se_req);

    if (se_out.IsSuccess())
    {
      std::cout << "Successfully sent GetMessage " << se_out.GetResult().GetMessageId()
        << std::endl;
    }

    else
    {
      std::cout << "Error creating receipt filter " << se_out.GetError().GetMessage()
        << std::endl;
    }
  }

  Aws::ShutdownAPI(options);
  return 0;
}
