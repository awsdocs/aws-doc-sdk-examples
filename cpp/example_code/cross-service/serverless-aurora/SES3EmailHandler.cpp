/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "SES3EmailHandler.h"
#include <aws/sesv2/SESV2Client.h>
#include <aws/sesv2/model/SendEmailRequest.h>
#include <aws/core/utils/base64/Base64.h>
#include <chrono>
#include <ctime>

namespace AwsDoc {
    namespace CrossService {
        static const Aws::String MIME_BOUNDARY("--MIME_boundary_2A837DD77556CFB3");
    }  // namespace CrossService
} // namespace AwsDoc

AwsDoc::CrossService::SES3EmailHandler::SES3EmailHandler(
        const Aws::String &fromEmailAddress,
        const Aws::Client::ClientConfiguration &clientConfiguration) :
        mClientConfiguration(clientConfiguration),
        mFromEmailAddress(fromEmailAddress) {

}

bool AwsDoc::CrossService::SES3EmailHandler::sendEmail(const Aws::String emailAddress,
                                                       const std::vector<WorkItem> &workItems) {
    Aws::SESV2::SESV2Client client(mClientConfiguration);

    auto currentTime = std::chrono::system_clock::to_time_t(std::chrono::system_clock::now());
    Aws::String dateString = ctime(&currentTime) ;
    Aws::String emailSubject("Greetings from AWS Example Code!");
    std::stringstream plainTextStream;
    plainTextStream << "This is a report for you.\n"
                    << "It contains " << workItems.size() << " items.\n"
                    << "Generated on " << dateString << ".\n";

    std::stringstream rawMessageStream;
    writeMultipartHeader(emailAddress, emailSubject, "" /* returnPath */,
                         rawMessageStream);
    writePlainTextPart(plainTextStream.str(), rawMessageStream);

    std::stringstream httpTextStream;
    httpTextStream << "<html>\n"
                   << "  <body>\n"
                   << "<h1>This is a report for you.</h1>\n"
                   << "<b>It contains " << workItems.size() << " items.</b>\n"
                   << "Generated on " << dateString << ".\n"
                   << "  </body>\n"
                   << "</html>\n";
    writeHtmlTextPart(httpTextStream.str(), rawMessageStream);

    std::stringstream csvStream;
    csvStream << "ID,Name,Guide,Description,Status,Archived\n";
    for (const WorkItem &item: workItems) {
        csvStream << item.mID << "," << item.mName << "," << item.mGuide << ","
                  << item.mDescription << "," << item.mStatus << ","
                  << (item.mArchived ? "yes" : "no") << "\n";
    }

    std::string csvString = csvStream.str();
    std::vector<unsigned char> csvVector(
            reinterpret_cast<const unsigned char *>(csvString.c_str()),
            reinterpret_cast<const unsigned char *>(csvString.c_str()) +
            csvString.length());

    writeAttachmentPart("text/csv", "report.csv", csvVector, rawMessageStream);
    std::string rawMessageString = rawMessageStream.str();

    //   std::cout << "\"" << rawMessageString << "\"" << std::endl;

    Aws::Utils::ByteBuffer buffer(
            reinterpret_cast<const unsigned char *>(rawMessageString.c_str()),
            rawMessageString.length());

    Aws::SESV2::Model::RawMessage rawMessage;
    rawMessage.SetData(buffer);

    Aws::SESV2::Model::EmailContent emailContent;
    emailContent.SetRaw(rawMessage);
    Aws::SESV2::Model::SendEmailRequest request;
    request.SetContent(emailContent);

    Aws::SESV2::Model::Destination destination;
    destination.AddToAddresses(emailAddress);
    request.SetDestination(destination);
    request.SetFromEmailAddress(mFromEmailAddress);

    Aws::SESV2::Model::SendEmailOutcome outcome = client.SendEmail(request);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully sent email to '" << emailAddress << "'" << std::endl;
    }
    else {
        std::cerr << "Error sending email to '" << emailAddress << "'\n"
                  << outcome.GetError().GetMessage() << std::endl;
    }

    return outcome.IsSuccess();
}

void AwsDoc::CrossService::SES3EmailHandler::writeMultipartHeader(
        const Aws::String &toEmail, const Aws::String &subject,
        const Aws::String &returnPath, std::ostream &ostream) {
    ostream << "From: " << mFromEmailAddress << "\n"
            << "To: " << toEmail << "\n"
            << "Subject: " << subject << "\n";
    if (!returnPath.empty()) {
        ostream << "Return-Path: " << returnPath << "\n";
    }
    ostream << "Content-Type: multipart/alternative;\n"
            << "\tboundary=\"" << MIME_BOUNDARY << "\"\n"
            << "\n"
            << "--" << MIME_BOUNDARY << "\n";
}

void
AwsDoc::CrossService::SES3EmailHandler::writePlainTextPart(const Aws::String &plainText,
                                                           std::ostream &ostream) {
    ostream << "Content-Type: text/plain; charset=UTF-8\n"
            << "Content-Transfer-Encoding: 7bit\n"
            << "\n"
            << "\n"
            << plainText << "\n"
            << "--" << MIME_BOUNDARY << "\n";
}

void
AwsDoc::CrossService::SES3EmailHandler::writeHtmlTextPart(const Aws::String &htmlText,
                                                          std::ostream &ostream) {
    ostream << "Content-Type: text/html; charset=UTF-8\n"
            << "Content-Transfer-Encoding: 7bit\n"
            << "\n"
            << "\n"
            << htmlText << "\n"
            << "--" << MIME_BOUNDARY << "\n";
}

void AwsDoc::CrossService::SES3EmailHandler::writeAttachmentPart(
        const Aws::String &contentType, const Aws::String &name,
        const std::vector<unsigned char> &attachmentBuffer, std::ostream &ostream) {
    Aws::Utils::ByteBuffer encodeBuffer(attachmentBuffer.data(),
                                        attachmentBuffer.size());
    Aws::Utils::Base64::Base64 base64;
    Aws::String encodedAttachment = base64.Encode(encodeBuffer);

    ostream << "Content-Type: " << contentType << "; name=" << name << "\n"
            << "Content-Transfer-Encoding: base64\n"
            << "Content-Disposition: attachment\n"
            << "\n"
            << encodedAttachment << "\n"
            << "--" << MIME_BOUNDARY << "\n";
}

