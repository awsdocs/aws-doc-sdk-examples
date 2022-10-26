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
        class HTTPReceiver {
        public:
            virtual bool handleHTTP(const std::string &method, const std::string &uri,
                                    const std::string &requestContent,
                                    std::string &responseContentType,
                                    std::ostream &responseStream) = 0;
        };
    }  // namespace CrossService
} // namespace AwsDoc

#endif //AWSDOC_CROSSSERVICE_HTTPRECEIVER_H
