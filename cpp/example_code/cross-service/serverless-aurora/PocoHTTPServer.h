/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#pragma once
#ifndef SERVERLESSAURORA_POCOHTTPSERVER_H
#define SERVERLESSAURORA_POCOHTTPSERVER_H

#include <Poco/Util/ServerApplication.h>
#include "HTTPReceiver.h"

namespace AwsDoc {
    namespace PocoImpl {
        /**
          *  PocoHTTPServer
          *
          *  Implementation of Poco http server application.
          *
          *  This code is designed to be used unmodified on multiple cross-service
          *  examples.
          *
          */
        class PocoHTTPServer : public Poco::Util::ServerApplication {
        public:
            //! PocoHTTPServer constructor.
            /*!
             \sa PocoHTTPServer::PocoHTTPServer()
             \param httpReceiver: Handler for http requests.
             */
            explicit PocoHTTPServer(AwsDoc::CrossService::HTTPReceiver &httpReceiver);

        protected:
            int main(const std::vector<std::string> &) override;

            AwsDoc::CrossService::HTTPReceiver &mHttpReceiver;
        };
    }  // namespace PocoImpl
} // namespace AwsDoc


#endif //SERVERLESSAURORA_POCOHTTPSERVER_H
