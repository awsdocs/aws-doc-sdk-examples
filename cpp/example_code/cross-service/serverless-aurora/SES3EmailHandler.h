/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SERVERLESSAURORA_SES3EMAILHANDLER_H
#define SERVERLESSAURORA_SES3EMAILHANDLER_H

#include "ItemTrackerHTTPHandler.h"
#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace CrossService {
class SES3EmailHandler : public SESEmailReceiver {
public :
    explicit SES3EmailHandler(const Aws::String& fromEmailAddress,
            const Aws::Client::ClientConfiguration& clientConfiguration);

    virtual bool sendEmail(const Aws::String emailAddress,
                           const std::vector<WorkItem> &workItems) override;

private:
    void writeMultipartHeader(const Aws::String &toEmail,
            const Aws::String &subject, const Aws::String &returnPath,
            std::ostream &ostream);

    void writePlainTextPart(const Aws::String &plainText, std::ostream& ostream);

    void writeHtmlTextPart(const Aws::String &htmlText, std::ostream& ostream);

    void writeAttachmentPart(const Aws::String& contentType, const Aws::String& name,
                             const std::vector<unsigned char>& attachmentBuffer,
                             std::ostream& ostream);

    Aws::Client::ClientConfiguration mClientConfiguration;
    Aws::String mFromEmailAddress;
};
    }  // namespace CrossService
} // namespace AwsDoc



#endif //SERVERLESSAURORA_SES3EMAILHANDLER_H
