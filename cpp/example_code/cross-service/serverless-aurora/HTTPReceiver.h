/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#ifndef AWSDOC_CROSSSERVICE_HTTPRECEIVER_H
#define AWSDOC_CROSSSERVICE_HTTPRECEIVER_H

#include <string>
#include <ostream>

namespace AwsDoc {
    namespace CrossService {
        /**
         *  HTTPReceiver
         *
         *  Abstract class defining interface of cross-service HTTP server
         *  receiver.
         *
         *  This code is designed to be used unmodified on multiple cross-service
         *  examples.
         *
         */
        class HTTPReceiver {
        public:
            //! Routine which handles HTTP server requests.
            /*!
             \sa handleHTTP()
             \param method: Method of HTTP request.
             \param uri: Uri of HTTP request.
             \param requestContent Content of HTTP request.
             \param responseContentType Content type of response, if any.
             \param responseStream Content of response, if any.
             \return bool: Successful completion.
            */
            virtual bool handleHTTP(const std::string &method, const std::string &uri,
                                    const std::string &requestContent,
                                    std::string &responseContentType,
                                    std::ostream &responseStream) = 0;
        };
    }  // namespace CrossService
} // namespace AwsDoc

#endif //AWSDOC_CROSSSERVICE_HTTPRECEIVER_H
