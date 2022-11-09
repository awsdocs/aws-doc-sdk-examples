/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

/**
 *  ItemTrackerHTTPHandler.h/.cpp
 *
 *  The code in these two files implements the HTTP server portion of the Amazon Aurora
 *  Serverless cross-service example.
 *
 *  To run the example, refer to the instructions in the ReadMe.
*/
#pragma once
#ifndef SERVERLESSAURORA_ITEMTRACKERSERVER_H
#define SERVERLESSAURORA_ITEMTRACKERSERVER_H

#include <aws/core/Aws.h>
#include "HTTPReceiver.h"

namespace AwsDoc {
    namespace CrossService {
        enum WorkItemStatus {
            ARCHIVED,
            NOT_ARCHIVED,
            BOTH
        };

        /**
         *
         *  Struct to store work item data.
         *
         */
        struct WorkItem {
            WorkItem() {}

            WorkItem(const Aws::String &id,
                     const Aws::String &name,
                     const Aws::String &guide,
                     const Aws::String &description,
                     const Aws::String &status,
                     bool archived) :
                    mID(id), mName(name), mGuide(guide), mDescription(description),
                    mStatus(status), mArchived(archived) {}

            Aws::String mID;
            Aws::String mName;
            Aws::String mGuide;
            Aws::String mDescription;
            Aws::String mStatus;
            bool mArchived;

        };

        /**
         *
         *  Constants for HTTP request keys.
         *
         */
        extern const Aws::String HTTP_ID_KEY;
        extern const Aws::String HTTP_NAME_KEY;
        extern const Aws::String HTTP_GUIDE_KEY;
        extern const Aws::String HTTP_DESCRIPTION_KEY;
        extern const Aws::String HTTP_STATUS_KEY;
        extern const Aws::String HTTP_ARCHIVED_KEY;
        extern const Aws::String HTTP_EMAIL_KEY;

        /**
         * RDSDataReceiver
         *
         *  Abstract class defining handler for Amazon Relational Database Service (Amazon RDS).
         *
         */
        class RDSDataReceiver {
        public:
            //! Routine which adds one work item.
            /*!
             \sa RDSDataReceiver::addWorkItem()
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            virtual bool addWorkItem(const WorkItem &workItem) = 0;

            //! Routine which retrieves a list of work items.
            /*!
             \sa RDSDataReceiver::getWorkItems()
             \param status: Filter for work item status.
             \param workItems: Vector of work items.
             \return bool: Successful completion.
             */
            virtual bool
            getWorkItems(WorkItemStatus status, std::vector<WorkItem> &workItems) = 0;

            //! Routine which retrieves one work item.
            /*!
             \sa RDSDataReceiver::getWorkItemWithId()
             \param id: ID of work item.
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            virtual bool
            getWorkItemWithId(const Aws::String &id, WorkItem &workItem) = 0;

            //! Routine which updates a work item, setting it as archived.
            /*!
             \sa RDSDataReceiver::setWorkItemToArchive()
             \param id: ID of work item.
             \return bool: Successful completion.
             */
            virtual bool setWorkItemToArchive(const Aws::String &id) = 0;

            //! Routine which updates a work item's columns.
            /*!
             \sa RDSDataReceiver::updateWorkItem()
             \param workItem: Work item struct.
             \return bool: Successful completion.
             */
            virtual bool updateWorkItem(const WorkItem &workItem) = 0;
        };

        /**
         * SESEmailReceiver
         *
         *  Abstract class defining handler for Amazon Simple Email Service (Amazon SES).
         *
         */
        class SESEmailReceiver {
        public:
            //! Routine which sends an email containing work items.
            /*!
             \sa SESEmailReceiver::sendEmail()
             \param emailAddress: Receiver's email address.
             \param workItems: Vector of work items.
             \return bool: Successful completion.
             */
            virtual bool sendEmail(const Aws::String emailAddress,
                                   const std::vector<WorkItem> &workItems) = 0;
        };

        /**
         * ItemTrackerHTTPHandler
         *
         *  Implementation of HTTP server handler.
         *
         */
        class ItemTrackerHTTPHandler : public HTTPReceiver {
        public:
            //! ItemTrackerHTTPHandler constructor.
            /*!
             \sa ItemTrackerHTTPHandler::ItemTrackerHTTPHandler()
             \param rdsDataReceiver: Handler for Amazon Relational Database Service (Amazon RDS).
             \param emailReceiver: Handler for Amazon Simple Email Service (Amazon SES).
            */
            explicit ItemTrackerHTTPHandler(RDSDataReceiver &rdsDataReceiver,
                                            SESEmailReceiver &emailReceiver);

            //! Override of HTTPReceiver::handleHTTP routine which handles HTTP server requests.
            /*!
             \sa ItemTrackerHTTPHandler::handleHTTP()
             \param method: Method of HTTP request.
             \param uri: Uri of HTTP request.
             \param requestContent Content of HTTP request.
             \param responseContentType Content type of response, if any.
             \param responseStream Content of response, if any.
             \return bool: Successful completion.
            */
            bool handleHTTP(const std::string &method, const std::string &uri,
                            const std::string &requestContent,
                            std::string &responseContentType,
                            std::ostream &responseStream) override;

            //! Routine which adds a work item to Amazon RDS.
            /*!
             \sa ItemTrackerHTTPHandler::addWorkItem()
             \param workItemJson: Content of HTTP request as JSON string.
             \return bool: Successful completion.
            */
            bool addWorkItem(const std::string &workItemJson);

            //! Routine which sends an email using Amazon SES.
            /*!
             \sa ItemTrackerHTTPHandler::sendEmail()
             \param emailJson: HTTP request JSON string containing an email.
             \return bool: Successful completion.
            */
            bool sendEmail(const std::string &emailJson);

        private:

            //! Routine which retrieves a work item from Amazon RDS with the specified ID.
            /*!
             \sa ItemTrackerHTTPHandler::getWorkItemWithIdJson()
             \param id: Work item id.
             \param jsonString: String with JSON response.
             \return bool: Successful completion.
            */
            bool getWorkItemWithIdJson(
                    const Aws::String &id, std::string &jsonString);

            //! Routine which retrieves a list of work items from Amazon RDS as an HTTP response
            //! JSON string.
            /*!
             \sa ItemTrackerHTTPHandler::getWorkItemJSON()
             \param status: Status filter for work items.
             \param jsonString: HTTP response JSON string.
             \return bool: Successful completion.
            */
            bool getWorkItemJSON(
                    AwsDoc::CrossService::WorkItemStatus status,
                    std::string &jsonString);

            //! Routine which converts a JSON string to a WorkItem struct.
            /*!
             \sa ItemTrackerHTTPHandler::jsonToWorkItem()
             \param workItemJson: Content of HTTP request as JSON string.
             \return WorkItem: WorkItem struct.
            */
            static WorkItem jsonToWorkItem(const std::string &jsonString);

            RDSDataReceiver &mRdsDataReceiver;
            SESEmailReceiver &mEmailReceiver;
            std::mutex mHTTPMutex;  // HTTP is received asynchronously.
            // The lock ensures the get items following
            // a post item contains the posted record.
        };
    }  // namespace CrossService
} // namespace AwsDoc


#endif //SERVERLESSAURORA_ITEMTRACKERSERVER_H
