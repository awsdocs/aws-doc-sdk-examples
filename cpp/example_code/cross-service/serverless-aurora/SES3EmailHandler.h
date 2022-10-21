/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SERVERLESSAURORA_SES3EMAILHANDLER_H
#define SERVERLESSAURORA_SES3EMAILHANDLER_H

#include "ItemTrackerHTTPServer.h"
#include <aws/core/client/ClientConfiguration.h>

namespace AwsDoc {
    namespace CrossService {
class SES3EmailHandler : public SESEmailReceiver {
public :
    SES3EmailHandler(const Aws::Client::ClientConfiguration& clientConfiguration);

    virtual bool sendEmail(const Aws::String emailAddress) override;

private:
    Aws::Client::ClientConfiguration mClientConfiguration;
};
    }  // namespace CrossService
} // namespace AwsDoc



#endif //SERVERLESSAURORA_SES3EMAILHANDLER_H
