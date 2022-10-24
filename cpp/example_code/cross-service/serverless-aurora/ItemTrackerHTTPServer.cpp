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
            handleRequest(Poco::Net::HTTPServerRequest &req,
                          Poco::Net::HTTPServerResponse &resp) {
                std::string method = req.getMethod();
                std::string uri = req.getURI();
                std::cout << "<p>Host: " << req.getHost() << "\n"
                          << "<p>Method: " << req.getMethod() << "\n"
                          << "<p>URI: " << req.getURI() << "\n"
                          << "content length " << req.getContentLength() << std::endl;

                resp.set("Access-Control-Allow-Origin", "*");
                if (method == "OPTIONS") {
                    resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                    resp.add("Access-Control-Allow-Methods", "OPTIONS, PUT, POST, GET");
                    resp.add("Access-Control-Allow-Headers",
                             "X-PINGOTHER, Content-Type");
                    resp.add("Access-Control-Max-Age", "86400");
                    resp.send();
                }
                else if (method == "GET") {
                    if (uri == "/api/items") {
                        getItemsAndRespond(AwsDoc::CrossService::WorkItemStatus::BOTH,
                                           resp);
                    }
                    else if (uri == "/api/items?archived=true") {
                        getItemsAndRespond(
                                AwsDoc::CrossService::WorkItemStatus::ARCHIVED, resp);
                    }
                    else if (uri == "/api/items?archived=false") {
                        getItemsAndRespond(
                                AwsDoc::CrossService::WorkItemStatus::NOT_ARCHIVED,
                                resp);
                    }
                    else {
                        std::cerr << "Unhandled GET uri " << uri << std::endl;
                    }
                }
                else if ((method == "POST")) {
                    if (uri == "/api/items") {
                        std::istream &istream = req.stream();
                        if (req.getContentLength() > 0) {
                            std::vector<char> body(req.getContentLength() + 1);
                            istream.read(body.data(), body.size() - 1);
                            body[body.size() - 1] = 0;
                            std::cout << body.data() << std::endl;
                            mItemTrackerServer.addWorkItem(&body[0]);
                            resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                            resp.send();
                        }
                        else {
                            std::cerr << "No content in Post /api/items" << std::endl;
                        }
                    }
                    else if (uri == "/api/items:report") {
                        std::istream &istream = req.stream();
                        if (req.getContentLength() > 0) {
                            std::vector<char> body(req.getContentLength() + 1);
                            istream.read(body.data(), body.size() - 1);
                            body[body.size() - 1] = 0;
                            std::cout << body.data() << std::endl;
                            mItemTrackerServer.sendEmail(&body[0]);
                            resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                            resp.send();
                        }
                        else {
                            std::cerr << "No content in Post /api/items" << std::endl;
                        }
                    }
                    else {
                        std::cerr << "Unhandled POST uri " << uri << std::endl;
                    }

                }
                else if (method == "PUT") {

                }
                else {
                    std::cerr << "Unhandled method " << method << std::endl;
                }

                std::cout << "HTTP response" << std::endl;
                for (auto iter = resp.begin(); iter != resp.end(); ++iter) {
                    std::cout << iter->first << " : " << iter->second << std::endl;
                }

            }

        private:
            void getItemsAndRespond(AwsDoc::CrossService::WorkItemStatus status,
                                    Poco::Net::HTTPServerResponse &resp) {
                resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                resp.setContentType("application/json");
                std::ostream &ostream = resp.send();
                ostream << mItemTrackerServer.getWorkItemJSON(status);
            }

            void getItemAndRespond(const Aws::String &itemID,
                                   Poco::Net::HTTPServerResponse &resp) {
                resp.setStatus(Poco::Net::HTTPResponse::HTTP_OK);
                resp.setContentType("application/json");
                std::ostream &ostream = resp.send();
                ostream << mItemTrackerServer.getWorkItemWithIdJson(itemID);
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
            explicit MyServerApp(
                    AwsDoc::CrossService::ItemTrackerHTTPServer &itemTrackerHttpServer)
                    :
                    mItemTrackerHttpServer(itemTrackerHttpServer) {}

        protected:
            int main(const std::vector<std::string> &) {
                Poco::Net::HTTPServer pocoHTTPServer(
                        new MyRequestHandlerFactory(mItemTrackerHttpServer),
                        Poco::Net::ServerSocket(8080),
                        new Poco::Net::HTTPServerParams);

                pocoHTTPServer.start();
                std::cout << "\nPoco http server started" << std::endl;

                waitForTerminationRequest();  // wait for CTRL-C or kill

                std::cout << "\nPoco http shutting down..." << std::endl;
                pocoHTTPServer.stop();

                return Application::EXIT_OK;
            }

            AwsDoc::CrossService::ItemTrackerHTTPServer &mItemTrackerHttpServer;
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
        AwsDoc::CrossService::RDSDataReceiver &rdsDataReceiver,
        SESEmailReceiver& emailReceiver) :
        mRdsDataReceiver(rdsDataReceiver),
        mEmailReceiver(emailReceiver) {

}

void AwsDoc::CrossService::ItemTrackerHTTPServer::run(int argc, char **argv) {
    AwsDoc::PocoImpl::MyServerApp myServerApp(*this);
    myServerApp.run(argc, argv);
}

std::string AwsDoc::CrossService::ItemTrackerHTTPServer::getWorkItemJSON(
        AwsDoc::CrossService::WorkItemStatus status) {
    std::vector<WorkItem> workItems;
    {
        std::lock_guard<std::mutex> lock(mHTTPMutex);
        workItems = mRdsDataReceiver.getWorkItems(status);
    }

    std::stringstream jsonString;
    jsonString << "[";
    for (size_t i = 0; i < workItems.size(); ++i) {
        WorkItem workItem = workItems[i];
        Aws::Utils::Document jsonWorkItem;
        jsonWorkItem.WithString(ID_KEY, workItem.mID);
        jsonWorkItem.WithString(NAME_KEY, workItem.mName);
        jsonWorkItem.WithString(GUIDE_KEY, workItem.mGuide);
        jsonWorkItem.WithString(DESCRIPTION_KEY, workItem.mDescription);
        jsonWorkItem.WithString(STATUS_KEY, workItem.mStatus);
        jsonString << jsonWorkItem.View().WriteReadable();
        if (i < workItems.size() - 1) {
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
    {
        std::lock_guard<std::mutex> lock(mHTTPMutex);
        mRdsDataReceiver.addWorkItem(workItem);
    }
}

AwsDoc::CrossService::WorkItem
AwsDoc::CrossService::ItemTrackerHTTPServer::jsonToWorkItem(
        const std::string &jsonString) {
    WorkItem result;
    Aws::Utils::Document document(jsonString);
    Aws::Utils::DocumentView view(document);
    result.mName = view.GetString(NAME_KEY);
    result.mGuide = view.GetString(GUIDE_KEY);
    result.mDescription = view.GetString(DESCRIPTION_KEY);
    result.mStatus = view.GetString(STATUS_KEY);

    return result;
}

void
AwsDoc::CrossService::ItemTrackerHTTPServer::sendEmail(const std::string &emailJson) {
    Aws::Utils::Document document(emailJson);
    Aws::Utils::DocumentView view(document);
    Aws::String email = view.GetString("email");

    if (!email.empty()) {
        std::vector<WorkItem> workItems = mRdsDataReceiver.getWorkItems(WorkItemStatus::BOTH);
        mEmailReceiver.sendEmail(email, workItems);
    }
}

std::string AwsDoc::CrossService::ItemTrackerHTTPServer::getWorkItemWithIdJson(
        const Aws::String &id) {
    WorkItem workItem;
    {
        std::lock_guard<std::mutex> lock(mHTTPMutex);
        workItem = mRdsDataReceiver.getWorkItemWithId(id);
    }

    std::stringstream jsonString;
    jsonString << "[";
    Aws::Utils::Document jsonWorkItem;
    jsonWorkItem.WithString(ID_KEY, workItem.mID);
    jsonWorkItem.WithString(NAME_KEY, workItem.mName);
    jsonWorkItem.WithString(GUIDE_KEY, workItem.mGuide);
    jsonWorkItem.WithString(DESCRIPTION_KEY, workItem.mDescription);
    jsonWorkItem.WithString(STATUS_KEY, workItem.mStatus);
    jsonString << jsonWorkItem.View().WriteReadable();

    jsonString << "]";

    std::cout << "work items\n" << jsonString.str() << std::endl;
    return jsonString.str();
}
