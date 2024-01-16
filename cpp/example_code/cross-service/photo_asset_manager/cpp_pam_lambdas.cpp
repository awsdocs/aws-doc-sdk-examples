// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * For information on the structure of the code examples and how to build and run the examples, see
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started-code-examples.html.
 *
 **/

#include <memory>
#include <aws/lambda-runtime/runtime.h>
#include <aws/logging/logging.h>
#include <aws/core/Aws.h>
#include <aws/core/utils/UUID.h>
#include <aws/core/utils/StringUtils.h>
#include <json/json.h>
#include "cpp_lambda_functions.h"

char const UPLOAD_HANDLER[] = "upload";
char const DETECT_LABELS_HANDLER[] = "detectLabels";
char const GET_LABELS_HANDLER[] = "getLabels";
char const DOWNLOAD_HANDLER[] = "download";

char const WORKING_BUCKET_ENV_NAME[] = "WORKING_BUCKET_NAME";
char const STORAGE_BUCKET_ENV_NAME[] = "STORAGE_BUCKET_NAME";
char const DATABASE_ENV_NAME[] = "LABELS_TABLE_NAME";
char const SNS_TOPIC_ARN_ENC_NAME[] = "NOTIFICATION_TOPIC";

char const BODY_KEY[] = "body";
char const FILE_NAME_KEY[] = "file_name";
char const RECORDS_KEY[] = "Records";

char const S3_KEY[] = "s3";
char const BUCKET_KEY[] = "bucket";
char const BUCKET_NAME_KEY[] = "name";
char const OBJECT_KEY[] = "object";
char const OBJECT_NAME_KEY[] = "key";
char const LABELS_KEY[] = "labels";

char const TAG[] = "LAMBDA_LOG";

//! Routine which parses a json string for the bucket and object names.
/*!
  \param jsonString: A JSON string as input.
  \param bucket: A string to receive the bucket name.
  \param object: A string to receive the object name.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
static bool
getBucketAndObjectFromDetectLabelsJSONString(const std::string &jsonString,
                                             std::string &bucket, std::string &object,
                                             std::string &errorString);

//! Routine which parses a json string for a file name.
/*!
  \param jsonString: A JSON string as input.
  \param fileName: A string to receive the file name.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
static bool
getFileNameFromUploadJSONString(const std::string &jsonString, std::string &fileName,
                                std::string &errorString);

//! Routine which parses a json string for a list of labels.
/*!
  \param jsonString: A JSON string as input.
  \param fileName: A vector to receive the labels.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
static bool labelsFromDownloadJSONString(const std::string &jsonString,
                                         std::vector<std::string> &labels,
                                         std::string &errorString);

//! A handler for the AWS Lambda upload function.
/*!
  \param request: A lambda runtime invocation request.
  \return invocation_response: A lambda runtime invocation response.
 */
static aws::lambda_runtime::invocation_response
uploadHandler(aws::lambda_runtime::invocation_request const &request) {
    std::string payload = request.payload;

    std::string fileName;
    std::string errorString;
    if (!getFileNameFromUploadJSONString(request.payload, fileName, errorString)) {
        std::stringstream stringstream;
        stringstream << R"({
	"statusCode": 400,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"error\": \")" << errorString << R"(\"}"
})";

        return aws::lambda_runtime::invocation_response::success(stringstream.str(),
                                                                 "application/json");
    }
    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    std::string key =
            Aws::Utils::StringUtils::ToLower(uuid.c_str()).substr(0, 14) + fileName;

    const char *env_var = std::getenv(STORAGE_BUCKET_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::success(R"({
	"statusCode": 400,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"error\": \"Error Missing bucket name variable 'bucket'.\"}"
})", "application/json");
    }

    Aws::String bucketName(env_var);
    Aws::Client::ClientConfiguration clientConfiguration;
    std::string presignedURL = AwsDoc::PAM::getPreSignedS3UploadURL(bucketName, key,
                                                                    clientConfiguration);
    if (presignedURL.empty()) {
        return aws::lambda_runtime::invocation_response::success(R"({
	"statusCode": 400,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"error\": \"Error creating presigned URL\"}"
})", "application/json");
    }

    std::stringstream stringstream;

    stringstream << R"(
{
	"statusCode": 200,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"url\": \")" << presignedURL << R"(\"}"
}
)";

    aws::logging::log_debug(TAG, "Response: %s", stringstream.str().c_str());

    return aws::lambda_runtime::invocation_response::success(stringstream.str(),
                                                             "application/json");
}

//! A handler for the AWS Lambda detect labels function.
/*!
  \param request: A lambda runtime invocation request.
  \return invocation_response: A lambda runtime invocation response.
 */
static aws::lambda_runtime::invocation_response
detectLabelsHandler(aws::lambda_runtime::invocation_request const &request) {
    std::string storageBucketName;

    const char *env_var = std::getenv(DATABASE_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::failure(
                "Missing DATABASE_ENV_NAME", "420");
    }
    std::string databaseName(env_var);

    std::string jsonString = request.payload;
    std::string bucket;
    std::string object;
    std::string error;

    if (!getBucketAndObjectFromDetectLabelsJSONString(jsonString, bucket, object,
                                                      error)) {
        return aws::lambda_runtime::invocation_response::failure(error, "420");

    }

    std::vector<std::string> imageLabels;
    std::stringstream errStream;
    Aws::Client::ClientConfiguration clientConfiguration;
    if (!AwsDoc::PAM::analyzeAndGetLabels(bucket, object, imageLabels, errStream,
                                          clientConfiguration)) {
        return aws::lambda_runtime::invocation_response::failure(
                "Error detecting image labels" + errStream.str(), "420");
    }

    if (!AwsDoc::PAM::updateLabelsInDatabase(databaseName, imageLabels, object,
                                             errStream, clientConfiguration)) {
        return aws::lambda_runtime::invocation_response::failure(
                "Error updating database" + errStream.str(), "420");
    }

    return aws::lambda_runtime::invocation_response::success("OK", "text/plain");
}

//! A handler for the AWS Lambda get labels function.
/*!
  \param request: A lambda runtime invocation request.
  \return invocation_response: A lambda runtime invocation response.
 */
static aws::lambda_runtime::invocation_response
getLabelsHandler(aws::lambda_runtime::invocation_request const &request) {
    const char *env_var = std::getenv(DATABASE_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::success(R"({
	"statusCode": 400,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"error\": \"Error Missing DATABASE_ENV_NAME name variable.\"}"
})", "application/json");
    }
    std::string databaseName(env_var);
    std::vector<AwsDoc::PAM::LabelAndCounts> labelAndCounts;
    std::stringstream errStream;
    Aws::Client::ClientConfiguration clientConfiguration;
    if (!AwsDoc::PAM::getLabelsAndCounts(databaseName, labelAndCounts, errStream,
                                         clientConfiguration)) {
        aws::logging::log_error(TAG, "getLabelsAndCounts error %s",
                                errStream.str().c_str());
        return aws::lambda_runtime::invocation_response::success(R"({
	"statusCode": 400,
	"headers": {
		"Access-Control-Allow-Origin": "*"
	},
	"body": "{\"error\": \"Error retrieving labels.\"}"
})", "application/json");
    }

    std::stringstream bodyStream;
    bodyStream << R"({ "statusCode": 200, "headers": { "Access-Control-Allow-Origin": "*" },
"body": "{\"labels\":{)";

    for (size_t i = 0; i < labelAndCounts.size(); ++i) {

        bodyStream << R"(\")" << labelAndCounts[i].mLabel << R"(\":{\"count\":)"
                   << labelAndCounts[i].mCount << "}";

        if (i < labelAndCounts.size() - 1) {
            bodyStream << ",";
        }
    }

    bodyStream << R"(}}", "isBase64Encoded": false })";
    return aws::lambda_runtime::invocation_response::success(bodyStream.str(),
                                                             "application/json");
}

//! A handler for the AWS Lambda download function.
/*!
  \param request: A lambda runtime invocation request.
  \return invocation_response: A lambda runtime invocation response.
 */
static aws::lambda_runtime::invocation_response
downloadHandler(aws::lambda_runtime::invocation_request const &request) {
    const char *env_var = std::getenv(WORKING_BUCKET_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::failure(
                "Missing WORKING_BUCKET_ENV_NAME", "420");
    }
    std::string workingBucket(env_var);

    env_var = std::getenv(STORAGE_BUCKET_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::failure(
                "Missing STORAGE_BUCKET_ENV_NAME", "420");
    }
    std::string storageBucket(env_var);

    env_var = std::getenv(DATABASE_ENV_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::failure(
                "Missing DATABASE_ENV_NAME", "420");
    }
    std::string database(env_var);

    env_var = std::getenv(SNS_TOPIC_ARN_ENC_NAME);
    if (nullptr == env_var) {
        return aws::lambda_runtime::invocation_response::failure(
                "Missing SNS_TOPIC_ARN_ENC_NAME", "420");
    }
    std::string snsTopicARRN(env_var);

    std::string jsonString = request.payload;
    std::vector<std::string> labels;
    std::string error;
    if (!labelsFromDownloadJSONString(jsonString, labels, error)) {
        return aws::lambda_runtime::invocation_response::failure(error, "420");
    }

    if (labels.empty()) {
        return aws::lambda_runtime::invocation_response::failure("Empty labels input",
                                                                 "420");
    }

    Aws::String uuid = Aws::Utils::UUID::RandomUUID();
    std::string destinationKey =
            Aws::Utils::StringUtils::ToLower(uuid.c_str()).substr(0, 14) +
            "_download.zip";
    std::string preSignedURL;

    std::stringstream errStream;
    Aws::Client::ClientConfiguration clientConfiguration;
    if (!AwsDoc::PAM::zipAndUploadImages(database, storageBucket, workingBucket,
                                         destinationKey, labels, preSignedURL,
                                         errStream, clientConfiguration)) {
        return aws::lambda_runtime::invocation_response::failure(
                "zipAndUploadImages failure" + errStream.str(), "420");
    }

    if (!AwsDoc::PAM::publishPreSignedURL(snsTopicARRN, preSignedURL, errStream,
                                          clientConfiguration)) {
        return aws::lambda_runtime::invocation_response::failure(
                "publishPreSignedURL failure" + errStream.str(), "420");
    }

    return aws::lambda_runtime::invocation_response::success("OK", "text/plain");
}

//! The main function.
/*!
  \param argc: The number of arguments.
  \param argv: The argument strings.
  \return int: A result code.
 */
int main(int argc, char **argv) {
    if (argc < 2) {
        return 1;
    }

    Aws::SDKOptions options;
    Aws::InitAPI(options);

    std::string handler_name(argv[1]);
    int result = 0;

    if (handler_name == UPLOAD_HANDLER) {
        run_handler(uploadHandler);
    }
    else if (handler_name == DETECT_LABELS_HANDLER) {
        run_handler(detectLabelsHandler);
    }
    else if (handler_name == GET_LABELS_HANDLER) {
        run_handler(getLabelsHandler);
    }
    else if (handler_name == DOWNLOAD_HANDLER) {
        run_handler(downloadHandler);
    }
    else {
        aws::logging::log_error(TAG, "Unknown handler %s", handler_name.c_str());
        result = 1;
    }

    ShutdownAPI(options);

    return result;
}

//! Routine which parses a json string for the bucket and object names.
/*!
  \param jsonString: A JSON string as input.
  \param bucket: A string to receive the bucket name.
  \param object: A string to receive the object name.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
bool
getBucketAndObjectFromDetectLabelsJSONString(const std::string &jsonString,
                                             std::string &bucket, std::string &object,
                                             std::string &errorString) {
    Json::Value root;
    Json::CharReaderBuilder builder;
    const std::unique_ptr<Json::CharReader> reader(builder.newCharReader());
    JSONCPP_STRING err;
    errorString.clear();

    if (!reader->parse(jsonString.c_str(), jsonString.c_str() + jsonString.length(),
                       &root,
                       &err)) {
        errorString = "Payload error " + err;
        return false;
    }

    for (auto &member: root.getMemberNames()) {
        std::cout << "root member " << member << std::endl;
    }

    if (!root.isMember(RECORDS_KEY) || !root[RECORDS_KEY].isArray() ||
        root[RECORDS_KEY].empty()) {
        errorString = "Records key invalid.";
        return false;
    }

    const Json::Value &recordsMap = root[RECORDS_KEY][0];

    if (!recordsMap.isObject()) {
        errorString = "Records map is not object.";
        return false;
    }


    for (auto &member: recordsMap.getMemberNames()) {
        std::cout << "member " << member << std::endl;
    }

    if (!recordsMap.isMember(S3_KEY) || !recordsMap[S3_KEY].isObject()) {
        errorString = "s3 key invalid.";
        return false;
    }

    Json::Value s3Map = recordsMap[S3_KEY];

    if (!s3Map.isMember(BUCKET_KEY)) {
        errorString = "Bucket key invalid.";
        return false;
    }

    if (!s3Map[BUCKET_KEY].isObject()) {
        errorString = "Bucket key invalid.";
        return false;
    }

    Json::Value bucketMap = s3Map[BUCKET_KEY];

    if (!bucketMap.isMember(BUCKET_NAME_KEY) ||
        !bucketMap[BUCKET_NAME_KEY].isString()) {
        errorString = "Bucket name key invalid.";
        return false;
    }

    bucket = bucketMap[BUCKET_NAME_KEY].asString();

    if (!s3Map.isMember(OBJECT_KEY) || !s3Map[OBJECT_KEY].isObject()) {
        errorString = "Object key invalid.";
        return false;
    }

    Json::Value objectMap = s3Map[OBJECT_KEY];

    if (!objectMap.isMember(OBJECT_NAME_KEY) ||
        !objectMap[OBJECT_NAME_KEY].isString()) {
        errorString = "Object name key invalid.";
        return false;
    }

    object = objectMap[OBJECT_NAME_KEY].asString();

    return true;
}

//! Routine which parses a json string for a file name.
/*!
  \param jsonString: A JSON string as input.
  \param fileName: A string to receive the file name.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
bool
getFileNameFromUploadJSONString(const std::string &jsonString, std::string &fileName,
                                std::string &errorString) {
    Json::Value root;
    Json::CharReaderBuilder builder;
    const std::unique_ptr<Json::CharReader> reader(builder.newCharReader());
    JSONCPP_STRING err;

    if (!reader->parse(jsonString.c_str(), jsonString.c_str() + jsonString.length(),
                       &root,
                       &err)) {
        errorString = "Error parsing main JSON. " + err;
        return false;

    }

    if (!root.isMember(BODY_KEY) || !root[BODY_KEY].isString()) {
        errorString = "Error finding body key.";
        return false;
    }

    std::string bodyJSON = root[BODY_KEY].asString();

    Json::Value body;
    if (!reader->parse(bodyJSON.c_str(), bodyJSON.c_str() + bodyJSON.length(), &body,
                       &err)) {
        errorString = "Error parsing body JSON. " + err;
        return false;
    }
    if (!body.isMember(FILE_NAME_KEY) || !body[FILE_NAME_KEY].isString()) {
        errorString = "Error finding fileName key.";
        return false;
    }

    fileName = body[FILE_NAME_KEY].asString();
    return true;
}

//! Routine which parses a json string for a list of labels.
/*!
  \param jsonString: A JSON string as input.
  \param fileName: A vector to receive the labels.
  \param errorString: A string to receive an error message.
  \return bool: Function succeeded.
 */
bool labelsFromDownloadJSONString(const std::string &jsonString,
                                  std::vector<std::string> &labels,
                                  std::string &errorString) {
    Json::Value root;
    Json::CharReaderBuilder builder;
    const std::unique_ptr<Json::CharReader> reader(builder.newCharReader());
    JSONCPP_STRING err;

    if (!reader->parse(jsonString.c_str(), jsonString.c_str() + jsonString.length(),
                       &root,
                       &err)) {
        errorString = "Error parsing main JSON. " + err;
        return false;

    }

    if (!root.isMember(LABELS_KEY) || !root[LABELS_KEY].isArray()) {
        errorString = "Error finding LABELS_KEY key.";
        return false;
    }

    Json::Value labelsJson = root[LABELS_KEY];
    for (const auto &label: labelsJson) {
        labels.push_back(label.asString());
    }

    return true;
}
