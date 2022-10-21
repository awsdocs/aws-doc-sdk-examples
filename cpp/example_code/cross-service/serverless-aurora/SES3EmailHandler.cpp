/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "SES3EmailHandler.h"
#include <aws/sesv2/SESV2Client.h>
#include <aws/sesv2/model/SendEmailRequest.h>
#include <Poco/Net/MailMessage.h>
#include <Poco/Net/StringPartSource.h>
#include <Poco/Base64Encoder.h>
#include <aws/core/utils/base64/Base64.h>


AwsDoc::CrossService::SES3EmailHandler::SES3EmailHandler(
        const Aws::Client::ClientConfiguration &clientConfiguration) :
        mClientConfiguration(clientConfiguration){

}

bool AwsDoc::CrossService::SES3EmailHandler::sendEmail(const Aws::String emailAddress)
{
    Aws::SESV2::SESV2Client client(mClientConfiguration);

    std::string textBody("\nThis is the body of my text\n");
    std::string csv("john,Jack,Jill");
    Aws::Utils::Base64::Base64 base64;
    Aws::Utils::ByteBuffer  encodeBuffer(reinterpret_cast<const unsigned char*>(csv.c_str()), csv.length());
    csv = base64.Encode(encodeBuffer);
    std::stringstream rawStream;
    rawStream << R"(From: meyertst@amazon.com
To: meyertst@amazon.com
Return-Path: meyertst@amazon.com
Subject: A subject
Content-Type: multipart/alternative;
	boundary="--MIME_boundary_2A837DD77556CFB3"

----MIME_boundary_2A837DD77556CFB3
Content-Type: text/plain; charset=UTF-8
Content-Transfer-Encoding: 7bit
)";
    rawStream << textBody;
    rawStream << R"(----MIME_boundary_2A837DD77556CFB3
Content-Type: text/html; charset=UTF-8
Content-Transfer-Encoding: 7bit

<!doctype html>
<html>
  <body>
)";
    rawStream << textBody;
    rawStream << R"(</body>
</html>
----MIME_boundary_2A837DD77556CFB3
Content-Type: text/csv; name=Report.csv
Content-Transfer-Encoding: base64
Content-Disposition: attachment

am9obixKYWNrLEppbGw=
----MIME_boundary_2A837DD77556CFB3--)";

#if 0

    ----MIME_boundary_2A837DD77556CFB3
Content-Type: text/csv; name=Report.csv
Content-Transfer-Encoding: base64
Content-Disposition: attachment

am9obixKYWNrLEppbGw=
#endif

    std::cout << rawStream.str() << std::endl;
#if 1
    Aws::SESV2::Model::EmailContent  emailContent;


    std::string rawMessageString = rawStream.str();
    Aws::SESV2::Model::RawMessage rawMessage;
    Aws::Utils::ByteBuffer  buffer(reinterpret_cast<const unsigned char*>(rawMessageString.c_str()), rawMessageString.length());
    rawMessage.SetData(buffer);
    emailContent.SetRaw(rawMessage);
    Aws::SESV2::Model::SendEmailRequest request;
    request.SetContent(emailContent);

    Aws::SESV2::Model::Destination destination;
    destination.AddToAddresses(emailAddress);
    request.SetDestination(destination);
    request.SetFromEmailAddress("meyertst@amazon.com");


    Aws::SESV2::Model::SendEmailOutcome outcome = client.SendEmail(request);

    if (outcome.IsSuccess())
    {
        std::cout << "Successfully sent email to '" << emailAddress << "'" << std::endl;
    }
    else
    {
        std::cerr << "Error sending email to '" << emailAddress << "'\n"
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
#else
    Aws::SESV2::Model::Message message;

    Aws::SESV2::Model::Content message_body_text;
    message_body_text.SetData(R"(First line.
Second line.)");
    message_body_text.SetCharset("UTF-8");

    Aws::SESV2::Model::Content message_body_html;
    message_body_html.SetData(R"(<!doctype html>
<html>
  <body>
    <p>This is an example paragraph. Anything in the <strong>body</strong> tag will appear on the page, just like this <strong>p</strong> tag and its contents.</p>
  </body>
</html>)");
    message_body_html.SetCharset("UTF-8");

    Aws::SESV2::Model::Body message_body;
    message_body.SetText(message_body_text);
    message_body.SetHtml(message_body_html);

    message.SetBody(message_body);

    Aws::SESV2::Model::Content message_subject;
    message_subject.SetData("Email subject");
    message_subject.SetCharset("UTF-8");
    message.SetSubject(message_subject);

    Aws::SESV2::Model::EmailContent  content;
    content.SetSimple(message);

    Aws::SESV2::Model::SendEmailRequest request;
    request.SetContent(emailContent);

    Aws::SESV2::Model::Destination destination;
    destination.AddToAddresses(emailAddress);
    request.SetDestination(destination);
    request.SetFromEmailAddress("meyertst@amazon.com");

    Aws::SESV2::Model::SendEmailOutcome outcome = client.SendEmail(request);

    if (outcome.IsSuccess())
    {
        std::cout << "Successfully sent email to '" << emailAddress << "'" << std::endl;
    }
    else
    {
        std::cerr << "Error sending email to '" << emailAddress << "'\n"
        << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
#endif

}

