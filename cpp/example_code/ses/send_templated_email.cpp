/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <aws/core/Aws.h>
#include <aws/email/SESClient.h>
#include <aws/email/model/SendTemplatedEmailRequest.h>
#include <aws/email/model/Destination.h>
#include <sstream>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.SendTemplatedEmail]
//! Send a templated email to a list of recipients.
/*!
  \param recipients; Vector of recipient email addresses.
  \param templateName: The name of the template to use.
  \param templateData: Map of key-value pairs for replacing text in template.
  \param senderEmailAddress: Email address of sender. Ignored if empty string.
  \param ccAddresses: Vector of cc addresses. Ignored if empty.
  \param replyToAddress: Reply to email address. Ignored if empty string.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::sendTemplatedEmail(const Aws::Vector<Aws::String> &recipients,
                                     const Aws::String &templateName,
                                     const Aws::Map<Aws::String, Aws::String> &templateData,
                                     const Aws::String &senderEmailAddress,
                                     const Aws::Vector<Aws::String> &ccAddresses,
                                     const Aws::String &replyToAddress,
                                     const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::Destination destination;
    if (!ccAddresses.empty()) {
        destination.WithCcAddresses(ccAddresses);
    }
    if (!recipients.empty()) {
        destination.WithToAddresses(recipients);
    }

    Aws::SES::Model::SendTemplatedEmailRequest sendTemplatedEmailRequest;
    sendTemplatedEmailRequest.SetDestination(destination);
    sendTemplatedEmailRequest.SetTemplate(templateName);

    std::ostringstream templateDataStream;
    templateDataStream << "{";
    size_t dataCount = 0;
    for (auto &pair: templateData) {
        templateDataStream << "\"" << pair.first << "\":\"" << pair.second << "\"";
        dataCount++;
        if (dataCount < templateData.size()) {
            templateDataStream << ",";
        }
    }
    templateDataStream << "}";

    sendTemplatedEmailRequest.SetTemplateData(templateDataStream.str());

    if (!senderEmailAddress.empty()) {
        sendTemplatedEmailRequest.SetSource(senderEmailAddress);
    }
    if (!replyToAddress.empty()) {
        sendTemplatedEmailRequest.AddReplyToAddresses(replyToAddress);
    }

    auto outcome = sesClient.SendTemplatedEmail(sendTemplatedEmailRequest);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully sent templated message with ID "
                  << outcome.GetResult().GetMessageId()
                  << "." << std::endl;
    }
    else {
        std::cerr << "Error sending templated message. "
                  << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.SendTemplatedEmail]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_send_templated_email <template_name> <template_data_key> <template_data_value>
 *                                          <sender_email_address> <cc_address> <reply_to_address>
 *                                          <to_addresses>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc < 7) {
        std::cout << "Usage: run_send_templated_email <template_name> <template_data_key>"
                     " <template_data_value> <sender_email_address> <cc_address> <reply_to_address> <to_addresses>";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String template_name(argv[1]);

        Aws::Map<Aws::String, Aws::String> template_data;
        template_data.emplace(argv[2], argv[3]);
        Aws::String sender_email_address(argv[4]);
        Aws::String cc_address(argv[5]);
        Aws::String reply_to_address(argv[6]);
        Aws::Vector<Aws::String> to_addresses;
        for (int i = 6; i < argc; ++i) {
            to_addresses.emplace_back(argv[i]);
        }

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SES::sendTemplatedEmail(to_addresses, template_name, template_data, sender_email_address,
                                        {cc_address}, reply_to_address, clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD
