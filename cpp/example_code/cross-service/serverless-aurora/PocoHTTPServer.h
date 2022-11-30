/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 *  PocoHTTPHandler.h/.cpp
 *
 *  The code in these two file implements a Poco HTTP server.  This code is designed
 *  to be reused unmodified across multiple examples.
 *
 *  To run the example, refer to the instructions in the ReadMe.
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
          *  Implementation of Poco HTTP server application.
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
             \param httpReceiver: Handler for HTTP requests.
             */
            explicit PocoHTTPServer(AwsDoc::CrossService::HTTPReceiver &httpReceiver);

        protected:
            int main(const std::vector<std::string> &) override;

            AwsDoc::CrossService::HTTPReceiver &mHttpReceiver;
        };
    }  // namespace PocoImpl
} // namespace AwsDoc


#endif //SERVERLESSAURORA_POCOHTTPSERVER_H
