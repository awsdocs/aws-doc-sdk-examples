/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ItemTrackerHTTPServer.h"
#include <Poco/Net/ServerSocket.h>
#include <Poco/Net/HTTPServer.h>
#include <Poco/Net/HTTPRequestHandler.h>
#include <Poco/Net/HTTPRequestHandlerFactory.h>
#include <Poco/Net/HTTPResponse.h>
#include <Poco/Net/HTTPServerRequest.h>
#include <Poco/Net/HTTPServerResponse.h>
#include <Poco/Util/ServerApplication.h>
#include <aws/core/utils/Document.h>
#include <iostream>
#include <string>
#include <vector>


namespace AwsDoc {
    namespace PocoImpl {
    class MyRequestHandler : public Poco::Net::HTTPRequestHandler {
        public:
            explicit MyRequestHandler(
                    AwsDoc::CrossService::ItemTrackerHTTPServer &itemTrackerServer) :
                    mItemTrackerServer(itemTrackerServer) {}

            virtual void
            handleRequest(Poco::Net::HTTPServerRequest &req, Poco::Net::HTTPServerResponse &resp) {
                std::string method = req.getMethod();
                std::string uri = req.getURI();
                std::cout << "<h1>Hello world!</h1>"
                          << "<p>Host: " << req.getHost() << "</p>"
                          << "<p>Method: " << req.getMethod() << "</p>"
                          << "<p>URI: " << req.getURI() << "</p>" << std::endl;

                resp.set("Access-Control-Allow-Origin", "*");
                if (method == "GET")
                {
                    if (uri == "//items/active")
                    {
                        setItemsResponse(AwsDoc::CrossService::WorkItemStatus::ACTIVE, resp);
                    }
                    else{
                        std::cerr << "Unhandled GET uri " << uri << std::endl;
                    }
                }
                else if ((method == "POST") || (method == "OPTIONS"))
                {
                    if (uri == "//items")
                    {
                        std::istream& istream = req.stream();
                        std::vector<char> body(req.getContentLength() + 1);
                        istream.read(body.data(), body.size() - 1);
                        body[body.size() - 1] = 0;
                        std::cout << body.data() << std::endl;
                    }
                    else{
                        std::cerr << "Unhandled POST uri " << uri << std::endl;
                    }

                }
                else if (method == "PUT")
                {

                }
                else
                {
                    std::cerr << "Unhandled method " << method << std::endl;
                }

             }

        private:
            void setItemsResponse(AwsDoc::CrossService::WorkItemStatus status,
                                         Poco::Net::HTTPServerResponse &resp)
            {
                resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                resp.setContentType("application/json");
                std::ostream& ostream = resp.send();
                ostream << mItemTrackerServer.getWorkItemJSON(status);
            }

            AwsDoc::CrossService::ItemTrackerHTTPServer &mItemTrackerServer;
        };

        class MyRequestHandlerFactory : public Poco::Net::HTTPRequestHandlerFactory {
        public:
            explicit MyRequestHandlerFactory(
                    AwsDoc::CrossService::ItemTrackerHTTPServer &itemTrackerServer) :
                    mItemTrackerServer(itemTrackerServer) {}

            virtual Poco::Net::HTTPRequestHandler *
            createRequestHandler(const Poco::Net::HTTPServerRequest &) {
                return new MyRequestHandler(mItemTrackerServer);
            }

        private:
            AwsDoc::CrossService::ItemTrackerHTTPServer &mItemTrackerServer;
        };

    class MyServerApp : public Poco::Util::ServerApplication {
        public:
            explicit MyServerApp(MyRequestHandlerFactory &myRequestHandlerFactory) :
                    mMyRequestHandlerFactory(myRequestHandlerFactory) {}

        protected:
            int main(const std::vector <std::string> &) {
                Poco::Net::HTTPServer s(&mMyRequestHandlerFactory, Poco::Net::ServerSocket(8000),
                             new Poco::Net::HTTPServerParams);

                s.start();
                std::cout << "\nServer started" << std::endl;

                waitForTerminationRequest();  // wait for CTRL-C or kill

                std::cout << "\nShutting down..." << std::endl;
                s.stop();

                return Application::EXIT_OK;
            }

            MyRequestHandlerFactory &mMyRequestHandlerFactory;
        };

    }  // namespace PocoImpl
} // namespace AwsDoc

namespace AwsDoc {
    namespace CrossService {
        static const Aws::String ID_KEY("id");
        static const Aws::String NAME_KEY("name");
        static const Aws::String GUIDE_KEY("guide");
        static const Aws::String DESCRIPTION_KEY("description");
        static const Aws::String STATUS_KEY("status");
    }  // namespace CrossService
} // namespace AwsDoc

AwsDoc::CrossService::ItemTrackerHTTPServer::ItemTrackerHTTPServer(
        AwsDoc::CrossService::RDSDataReceiver &rdsDataReceiver) :
        mRdsDataReceiver(rdsDataReceiver) {

}

void AwsDoc::CrossService::ItemTrackerHTTPServer::run(int argc, char **argv) {
    AwsDoc::PocoImpl::MyRequestHandlerFactory myRequestHandlerFactory(*this);
    AwsDoc::PocoImpl::MyServerApp myServerApp(myRequestHandlerFactory);
    myServerApp.run(argc, argv);
}

std::string AwsDoc::CrossService::ItemTrackerHTTPServer::getWorkItemJSON(
        AwsDoc::CrossService::WorkItemStatus status) {

    std::vector<WorkItem> workItems = mRdsDataReceiver.getWorkItems(status);

    std::stringstream jsonString;
    jsonString << "[";
     for (size_t i = 0; i < workItems.size(); ++i)
    {
        WorkItem workItem = workItems[i];
        Aws::Utils::Document jsonWorkItem;
        jsonWorkItem.WithString(ID_KEY, workItem.mID);
        jsonWorkItem.WithString(NAME_KEY, workItem.mName);
        jsonWorkItem.WithString(GUIDE_KEY, workItem.mGuide);
        jsonWorkItem.WithString(DESCRIPTION_KEY, workItem.mDescription);
        jsonWorkItem.WithString(STATUS_KEY, workItem.mStatus);
        jsonString << jsonWorkItem.View().WriteReadable();
        if (i < workItems.size() - 1)
        {
            jsonString << ",";
        }
    }
    jsonString << "]";

     std::cout << "work items\n" << jsonString.str() << std::endl;
    return jsonString.str();
}

void AwsDoc::CrossService::ItemTrackerHTTPServer::addWorkItem(
        const std::string &workItemJson) {

    WorkItem workItem = jsonToWorkItem(workItemJson);
    mRdsDataReceiver.addWorkItem(workItem);
}

AwsDoc::CrossService::WorkItem
AwsDoc::CrossService::ItemTrackerHTTPServer::jsonToWorkItem(
        const std::string &jsonString) {
    WorkItem result;
    Aws::Utils::Document document(jsonString);
    Aws::Utils::DocumentView  view(document);
    result.mName = view.GetString(NAME_KEY);
    result.mGuide = view.GetString(GUIDE_KEY);
    result.mDescription = view.GetString(DESCRIPTION_KEY);
    result.mStatus = view.GetString(STATUS_KEY);

    return result;
}
