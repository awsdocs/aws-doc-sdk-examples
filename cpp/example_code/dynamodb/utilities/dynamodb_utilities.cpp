/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include "dynamodb_utilities.h"
#include <aws/core/http/HttpClientFactory.h>
#include <aws/core/client/ClientConfiguration.h>
#include <aws/core/http/HttpClient.h>
#include <fstream>
#include <array>

#ifdef _HAS_ZLIB_
#include <zlib.h>
#endif // _HAS_ZLIB_

Aws::String AwsDoc::DynamoDB::askQuestion(const Aws::String &string,
                                          const std::function<bool(
                                                  Aws::String)> &test) {
    return "";
}

int AwsDoc::DynamoDB::askQuestionForInt(const Aws::String &string) {
   return 0;
}

float AwsDoc::DynamoDB::askQuestionForFloatRange(const Aws::String &string, float low,
                                                 float high) {
    return 0.0;
}

int AwsDoc::DynamoDB::askQuestionForIntRange(const Aws::String &string, int low,
                                             int high) {
     return 0;
}

Aws::String AwsDoc::DynamoDB::getMovieJSON() {
    return "";
}

