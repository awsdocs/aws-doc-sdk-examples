/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "RDSDataHandler.h"

bool AwsDoc::CrossService::RDSDataHandler::setWorkItem(
        const AwsDoc::CrossService::WorkItem &workItem) {
    std::cout << "setWorkItem " <<
              " item.mName " << workItem.mName <<
              " workItem.mGuide " << workItem.mGuide <<
              " workItem.mDescription " << workItem.mDescription <<
              " workItem.mStatus " << workItem.mStatus << std::endl;
    return true;
}

std::vector<AwsDoc::CrossService::WorkItem>
AwsDoc::CrossService::RDSDataHandler::getWorkItems(
        AwsDoc::CrossService::WorkItemStatus status) {
    std::vector<WorkItem> result;
    for (int i = 0; i < 3; ++i) {
        WorkItem item;
        item.mID = std::to_string(i);
        item.mName = std::to_string(i) + " name";
        item.mGuide = std::to_string(i) + " guide";
        item.mDescription = std::to_string(i) + " description";
        item.mStatus = (status == AwsDoc::CrossService::WorkItemStatus::ACTIVE) ?
                       "active" : "archived";
        item.mDate = "2022-10-17T07:20:45Z";
        result.push_back(item);
    }
    return result;
}
