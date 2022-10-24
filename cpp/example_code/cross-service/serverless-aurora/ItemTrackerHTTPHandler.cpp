/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "ItemTrackerHTTPHandler.h"
#include <aws/core/utils/Document.h>
#include <iostream>
#include <string>
#include <vector>

namespace AwsDoc {
    namespace CrossService {
        static const Aws::String ID_KEY("id");
        static const Aws::String NAME_KEY("name");
        static const Aws::String GUIDE_KEY("guide");
        static const Aws::String DESCRIPTION_KEY("description");
        static const Aws::String STATUS_KEY("status");
    }  // namespace CrossService
} // namespace AwsDoc

AwsDoc::CrossService::ItemTrackerHTTPHandler::ItemTrackerHTTPHandler(
        AwsDoc::CrossService::RDSDataReceiver &rdsDataReceiver,
        SESEmailReceiver& emailReceiver) :
        mRdsDataReceiver(rdsDataReceiver),
        mEmailReceiver(emailReceiver) {
}

void AwsDoc::CrossService::ItemTrackerHTTPHandler::run(int argc, char **argv) {
    AwsDoc::PocoImpl::PocoHTTPServer myServerApp(*this);
    myServerApp.run(argc, argv);
}

std::string AwsDoc::CrossService::ItemTrackerHTTPHandler::getWorkItemJSON(
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

void AwsDoc::CrossService::ItemTrackerHTTPHandler::addWorkItem(
        const std::string &workItemJson) {

    WorkItem workItem = jsonToWorkItem(workItemJson);
    {
        std::lock_guard<std::mutex> lock(mHTTPMutex);
        mRdsDataReceiver.addWorkItem(workItem);
    }
}

AwsDoc::CrossService::WorkItem
AwsDoc::CrossService::ItemTrackerHTTPHandler::jsonToWorkItem(
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
AwsDoc::CrossService::ItemTrackerHTTPHandler::sendEmail(const std::string &emailJson) {
    Aws::Utils::Document document(emailJson);
    Aws::Utils::DocumentView view(document);
    Aws::String email = view.GetString("email");

    if (!email.empty()) {
        std::vector<WorkItem> workItems = mRdsDataReceiver.getWorkItems(WorkItemStatus::BOTH);
        mEmailReceiver.sendEmail(email, workItems);
    }
}

std::string AwsDoc::CrossService::ItemTrackerHTTPHandler::getWorkItemWithIdJson(
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

bool AwsDoc::CrossService::ItemTrackerHTTPHandler::handleHTTP(const std::string &method,
                                                              const std::string &uri,
                                                              const std::string &requestContent,
                                                              std::string &responseContentType,
                                                              std::ostream &responseStream) {
    bool result = false;
    if (method == "GET") {
        if (uri == "/api/items") {
            result = getItemsAndRespond(AwsDoc::CrossService::WorkItemStatus::BOTH,
                               responseContentType, responseStream);
        }
        else if (uri == "/api/items?archived=true") {
            result = getItemsAndRespond(
                    AwsDoc::CrossService::WorkItemStatus::ARCHIVED, responseContentType,
                    responseStream);
        }
        else if (uri == "/api/items?archived=false") {
            result = getItemsAndRespond(
                    AwsDoc::CrossService::WorkItemStatus::NOT_ARCHIVED,
                    responseContentType, responseStream);
        }
        else if (uri.find("/api/items/") == 0)
        {
            size_t startPos = strlen("/api/items/");
            Aws::String itemID = uri.substr(startPos, uri.length() - startPos);
            result = getItemAndRespond(itemID, responseContentType, responseStream);
        }
        else {
            std::cerr << "Unhandled GET uri " << uri << std::endl;
        }
    }
    else if ((method == "POST")) {
        if (uri == "/api/items") {
            if (!requestContent.empty()) {
                addWorkItem(requestContent);
                result = true;
            }
            else {
                std::cerr << "No content in Post /api/items" << std::endl;
            }
        }
        else if (uri == "/api/items:report") {
             if (!requestContent.empty()) {
                 sendEmail(requestContent);
                 result = true;
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
        if (uri.find("/api/items/") == 0)
        {
            size_t archivePos = uri.find_last_of(":archive");
            
        }
        else {
            std::cerr << "Unhandled PUT uri " << uri << std::endl;
        }
    }
    else {
        std::cerr << "Unhandled method " << method << std::endl;
    }
    return result;
}

bool AwsDoc::CrossService::ItemTrackerHTTPHandler::getItemsAndRespond(
        AwsDoc::CrossService::WorkItemStatus status, std::string &contentType,
        std::ostream &ostream) {

    contentType = "application/json";
    ostream << getWorkItemJSON(status);
    return true;
}

bool AwsDoc::CrossService::ItemTrackerHTTPHandler::getItemAndRespond(
        const Aws::String &itemID, std::string &contentType, std::ostream &ostream) {
    contentType = "application/json";
    ostream << getWorkItemWithIdJson(itemID);
    return true;
}

