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
#include <aws/email/model/UpdateTemplateRequest.h>
#include <aws/email/model/Template.h>
#include <iostream>
#include "ses_samples.h"

// snippet-start:[cpp.example_code.ses.UpdateTemplate]
//! Update an Amazon Simple Email Service (Amazon SES) template.
/*!
  \param templateName: The name of the template.
  \param htmlPart: The HTML body of the email.
  \param subjectPart: The subject line of the email.
  \param textPart: The plain text version of the email.
  \param clientConfiguration: AWS client configuration.
  \return bool: Function succeeded.
 */
bool AwsDoc::SES::updateTemplate(const Aws::String &templateName,
                                 const Aws::String &htmlPart,
                                 const Aws::String &subjectPart,
                                 const Aws::String &textPart,
                                 const Aws::Client::ClientConfiguration &clientConfiguration) {
    Aws::SES::SESClient sesClient(clientConfiguration);

    Aws::SES::Model::Template templateValues;

    templateValues.SetTemplateName(templateName);
    templateValues.SetSubjectPart(subjectPart);
    templateValues.SetHtmlPart(htmlPart);
    templateValues.SetTextPart(textPart);

    Aws::SES::Model::UpdateTemplateRequest updateTemplateRequest;
    updateTemplateRequest.SetTemplate(templateValues);

    Aws::SES::Model::UpdateTemplateOutcome outcome = sesClient.UpdateTemplate(updateTemplateRequest);

    if (outcome.IsSuccess()) {
        std::cout << "Successfully updated template." << std::endl;
    } else {
        std::cerr << "Error updating template. " << outcome.GetError().GetMessage()
                  << std::endl;
    }

    return outcome.IsSuccess();
}
// snippet-end:[cpp.example_code.ses.UpdateTemplate]

/*
 *
 *  main function
 *
 *  Usage: 'Usage: run_update_template <template_name> <html_content>
 *                 <subject_line> <text_content>'*<subject_line> <text_content>'
 *
 */

#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    if (argc != 5) {
        std::cout << "Usage: run_update_template <template_name> <html_content>"
                     "<subject_line> <text_content>";
        return 1;
    }
    Aws::SDKOptions options;
    Aws::InitAPI(options);
    {
        Aws::String templateName(argv[1]);
        Aws::String htmlContent(argv[2]);
        Aws::String subjectLine(argv[3]);
        Aws::String textContent(argv[4]);

        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        AwsDoc::SES::updateTemplate(templateName, htmlContent, subjectLine, textContent, clientConfig);
    }

    Aws::ShutdownAPI(options);
    return 0;
}

#endif // TESTING_BUILD