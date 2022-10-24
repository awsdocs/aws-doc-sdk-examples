/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SERVERLESSAURORA_POCOHTTPSERVER_H
#define SERVERLESSAURORA_POCOHTTPSERVER_H

#include <Poco/Util/ServerApplication.h>

namespace AwsDoc {
    namespace PocoImpl {
        class PocoHTTPReceiver {
        public:
            virtual bool handleHTTP(const std::string &method, const std::string &uri,
                                    const std::string &requestContent,
                                    std::string &responseContentType,
                                    std::ostream &responseStream) = 0;
        };

        class PocoHTTPServer : public Poco::Util::ServerApplication {
        public:
            explicit PocoHTTPServer(PocoHTTPReceiver &httpReceiver);

        protected:
            int main(const std::vector<std::string> &) override;

            PocoHTTPReceiver &mHttpReceiver;
        };

    }  // namespace PocoImpl
} // namespace AwsDoc


#endif //SERVERLESSAURORA_POCOHTTPSERVER_H
