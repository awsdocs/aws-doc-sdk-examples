/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "PocoHTTPServer.h"
#include <Poco/Net/HTTPServer.h>
#include <Poco/Net/ServerSocket.h>
#include <Poco/Net/HTTPRequestHandler.h>
#include <Poco/Net/HTTPRequestHandlerFactory.h>
#include <Poco/Net/HTTPResponse.h>
#include <Poco/Net/HTTPServerRequest.h>
#include <Poco/Net/HTTPServerResponse.h>

#include <iostream>
#include <sstream>

namespace AwsDoc {
    namespace PocoImpl {
        class MyRequestHandler : public Poco::Net::HTTPRequestHandler {
        public:
            explicit MyRequestHandler(
                    AwsDoc::CrossService::HTTPReceiver &httpReceiver) :
                    mHttpReceiver(httpReceiver) {}

            virtual void
            handleRequest(Poco::Net::HTTPServerRequest &req,
                          Poco::Net::HTTPServerResponse &resp) {
                std::string method = req.getMethod();
                std::string uri = req.getURI();

                resp.set("Access-Control-Allow-Origin", "*");
                if (method == "OPTIONS") {
                    resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                    resp.add("Access-Control-Allow-Methods", "OPTIONS, PUT, POST, GET");
                    resp.add("Access-Control-Allow-Headers",
                             "X-PINGOTHER, Content-Type");
                    resp.add("Access-Control-Max-Age", "86400");
                    resp.send();
                }
                else {
                    std::string contentType;
                    std::stringstream responseStream;
                    std::string requestContent;
                    if (req.getContentLength() > 0) {
                        std::vector<char> body(req.getContentLength() + 1);
                        req.stream().read(body.data(), body.size() - 1);
                        body[body.size() - 1] = 0;
                        requestContent = body.data();
                    }

                    bool result = mHttpReceiver.handleHTTP(method, uri, requestContent,
                                                           contentType,
                                                           responseStream);
                    resp.setStatus(result ? Poco::Net::HTTPResponse::HTTP_OK :
                                   Poco::Net::HTTPResponse::HTTP_NOT_ACCEPTABLE);
                    if (!contentType.empty()) {
                        resp.setContentType(contentType);
                    }
                    std::ostream &ostream = resp.send();
                    ostream << responseStream.str() << std::endl;
                }
            }

        private :
            AwsDoc::CrossService::HTTPReceiver &mHttpReceiver;
       };

        class MyRequestHandlerFactory : public Poco::Net::HTTPRequestHandlerFactory {
        public:
            explicit MyRequestHandlerFactory(
                    AwsDoc::CrossService::HTTPReceiver &httpReceiver) :
                    mHttpReceiver(httpReceiver) {}

            virtual Poco::Net::HTTPRequestHandler *
            createRequestHandler(const Poco::Net::HTTPServerRequest &) {
                return new MyRequestHandler(mHttpReceiver);
            }

        private:
            AwsDoc::CrossService::HTTPReceiver &mHttpReceiver;
        };
    }  // namespace PocoImpl
} // namespace AwsDoc


AwsDoc::PocoImpl::PocoHTTPServer::PocoHTTPServer(
        AwsDoc::CrossService::HTTPReceiver &httpReceiver) :
        mHttpReceiver(httpReceiver) {
}

int AwsDoc::PocoImpl::PocoHTTPServer::main(const std::vector<std::string> &) {
    Poco::Net::HTTPServer pocoHTTPServer(
            new MyRequestHandlerFactory(mHttpReceiver),
            Poco::Net::ServerSocket(8080),
            new Poco::Net::HTTPServerParams);

    pocoHTTPServer.start();
    std::cout << "\nPoco http server started" << std::endl;

    waitForTerminationRequest();  // wait for CTRL-C or kill

    std::cout << "\nPoco http shutting down..." << std::endl;
    pocoHTTPServer.stop();

    return Application::EXIT_OK;
}