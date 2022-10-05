/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/
/**
 * Before running this C++ code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/getting-started.html
 *
 * Purpose
 *
 * Demonstrates using the AWS SDK for C++ to create an Amazon DynamoDB table and
 *  and perform a series of operations on the table.
 *
 * 1. Create a table with partition: year (N) and sort: title (S). (CreateTable)
 * 2. Add a new movie. (PutItem)
 * 3. Update the rating and plot of the movie by using an update expression.
 *    (UpdateItem with UpdateExpression + ExpressionAttributeValues args)
 * 4. Put movies in the table from moviedata.json, downloaded from the
 *    Amazon DynamoDB Developer Guide. (BatchWriteItem)
 * 5. Get a movie by Key. (partition + sort) (GetItem)
 * 6. Use Query with a key condition expression to return all movies released in a given
 *    year. (Query + KeyConditionExpression arg)
 * 7. Use Scan to return movies released within a range of years. Show how to
 *    paginate data using ExclusiveStartKey. (Scan + FilterExpression)
 * 8. Delete a movie. (DeleteItem)
 * 9. Delete the table. (DeleteTable)
 */

#include "dyanamodb_samples.h"
#include <iostream>
#include <iomanip>
#include <aws/core/Aws.h>
#include <aws/dynamodb/DynamoDBClient.h>
#include <aws/dynamodb/model/CreateTableRequest.h>
#include <aws/dynamodb/model/DeleteTableRequest.h>
#include <aws/dynamodb/model/DeleteItemRequest.h>
#include <aws/dynamodb/model/DescribeTableRequest.h>
#include <aws/dynamodb/model/GetItemRequest.h>
#include <aws/dynamodb/model/PutItemRequest.h>
#include <aws/dynamodb/model/QueryRequest.h>
#include <aws/dynamodb/model/ScanRequest.h>
#include <aws/dynamodb/model/UpdateItemRequest.h>
#include <dynamodb/model/BatchWriteItemRequest.h>
#include <aws/core/http/HttpClientFactory.h>
#include <aws/core/http/HttpClient.h>
#include <fstream>

#ifdef _HAS_ZLIB_
#include <zlib.h>
#endif // _HAS_ZLIB_

#include <array>

namespace AwsDoc {
    namespace DynamoDB {
        /**
         * Constants used for DynamoDB table creation.
         */
        static const Aws::String MOVIE_TABLE_NAME("doc-example-table-movies");
        static const Aws::String YEAR_KEY("year");
        static const Aws::String TITLE_KEY("title");
        static const Aws::String INFO_KEY("info");
        static const Aws::String RATING_KEY("rating");
        static const Aws::String PLOT_KEY("plot");
        static const int PROVISIONED_THROUGHPUT_UNITS = 10;
        static const Aws::String ALLOCATION_TAG("dynamodb_scenario");
        static const int ASTERIX_FILL_WIDTH = 88;

        //! Delete a DynamoDB table.
        /*!
          \sa deleteDynamoTable()
          \param tableName: The DynamoDB table's name.
          \param dynamoClient: A DynamoDB client.
          \return bool: Function succeeded.
        */
        static bool deleteDynamoTable(const Aws::String &tableName,
                                      const Aws::DynamoDB::DynamoDBClient &dynamoClient);

        //! Query a newly created DynamoDB table until it is active.
        /*!
          \sa waitTableActive()
          \param waitTableActive: The DynamoDB table's name.
          \param dynamoClient: A DynamoDB client.
          \return bool: Function succeeded.
        */
        static bool waitTableActive(const Aws::String &tableName,
                                    const Aws::DynamoDB::DynamoDBClient &dynamoClient);

        //! Convert an Aws JsonView object to a map of DynamoDB attribute values.
        /*!
          \sa movieJsonViewToAttributeMap()
          \param jsonView: An Aws JsonView.
          \return Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>.
        */
        Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue>
        movieJsonViewToAttributeMap(const Aws::Utils::Json::JsonView &jsonView);

        //! Command line prompt/response utility function.
        /*!
         \\sa askQuestion()
         \param string: A question prompt.
         \param test: Test function for response.
         \return Aws::String: User's response.
         */
        static Aws::String askQuestion(const Aws::String &string,
                                       const std::function<bool(
                                               Aws::String)> &test = [](
                                               const Aws::String &) -> bool { return true; });

        //! Command line prompt/response utility function for an integer result.
        /*!
         \sa askQuestionForInt()
         \param string: A question prompt.
         \return int: User's response.
         */
        int askQuestionForInt(const std::string &string);

        //! Command line prompt/response utility function for a float result confined to
        //! a range.
        /*!
         \sa askQuestionForFloatRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return float: User's response.
         */
        float
        askQuestionForFloatRange(const Aws::String &string, float low, float high);

        //! Command line prompt/response utility function for an int result confined to
        //! a range.
        /*!
         \sa askQuestionForIntRange()
         \param string: A question prompt.
         \param low: Low inclusive.
         \param high: High inclusive.
         \return int: User's response.
         */
        int askQuestionForIntRange(const Aws::String &string, int low,
                                   int high);

        //! Utility function to log movie attributes to std::cout.
        /*!
         \sa printMovieInfo()
         \param movieMap: Map of DynamoDB attribute values.
         \return void
         */
        void printMovieInfo(
                const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &movieMap);

        //! Download a JSON movie database file from the web and unzip the file.
        /*!
         \sa getMovieJSON()
         \return Aws::String: Movie database as JSON string.
         */
        static Aws::String getMovieJSON();

    } //  namespace DynamoDB
} // namespace AwsDoc

//! Scenario to create, modify, query, and delete a DynamoDB table.
/*!
  \sa dynamodbGettingStartedScenario()
  \param clientConfig: Aws client configuration.
  \return bool: Function succeeded.
 */

// snippet-start:[cpp.example_code.dynamodb.Scenario_GettingStarted]
// This is debugging code, not a full implementation
bool AwsDoc::DynamoDB::dynamodbGettingStartedScenario(
        const Aws::Client::ClientConfiguration &clientConfiguration) {
    std::cout << std::setfill('*') << std::setw(ASTERIX_FILL_WIDTH) << " " << std::endl;
    std::cout << "Welcome to the Amazon DynamoDB getting started demo." << std::endl;
    std::cout << std::setfill('*') << std::setw(ASTERIX_FILL_WIDTH) << " " << std::endl;

    Aws::DynamoDB::DynamoDBClient dynamoClient(clientConfiguration);

    bool movieTableAlreadyExisted = false;

    {
        Aws::DynamoDB::Model::CreateTableRequest request;

        Aws::DynamoDB::Model::AttributeDefinition yearAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(YEAR_KEY);
        yearAttributeDefinition.SetAttributeType(
                Aws::DynamoDB::Model::ScalarAttributeType::N);
        request.AddAttributeDefinitions(yearAttributeDefinition);

        Aws::DynamoDB::Model::AttributeDefinition titleAttributeDefinition;
        yearAttributeDefinition.SetAttributeName(TITLE_KEY);
        yearAttributeDefinition.SetAttributeType(
                Aws::DynamoDB::Model::ScalarAttributeType::S);
        request.AddAttributeDefinitions(yearAttributeDefinition);

     }

    return false;
}

bool AwsDoc::DynamoDB::deleteDynamoTable(const Aws::String &tableName,
                                         const Aws::DynamoDB::DynamoDBClient &dynamoClient) {

    return false;
}

bool AwsDoc::DynamoDB::waitTableActive(const Aws::String &tableName,
                                       const Aws::DynamoDB::DynamoDBClient &dynamoClient) {
    return false;
}
// snippet-end:[cpp.example_code.dynamodb.Scenario_GettingStarted]


#ifndef TESTING_BUILD

int main(int argc, char **argv) {
    (void) argc; // suppress unused warning
    (void) argv; // suppress unused warning
    Aws::SDKOptions options;
    InitAPI(options);

    {
        Aws::Client::ClientConfiguration clientConfig;
        AwsDoc::DynamoDB::dynamodbGettingStartedScenario(clientConfig);
    }

    return 0;
}

#endif // TESTING_BUILD

Aws::String AwsDoc::DynamoDB::askQuestion(const Aws::String &string,
                                          const std::function<bool(
                                                  Aws::String)> &test) {
    Aws::String result;
    do {
        std::cout << string;
        std::getline(std::cin, result);
        if (result.empty()) {
            std::cout << "Please enter some text." << std::endl;
        }
        if (!test(result)) {
            continue;
        }
    } while (result.empty());

    return result;
}

int AwsDoc::DynamoDB::askQuestionForInt(const Aws::String &string) {
    Aws::String resultString = askQuestion(string,
                                           [](const Aws::String &string1) -> bool {
                                                   try {
                                                       (void)std::stoi(string1);
                                                       return true;
                                                   }
                                                   catch (const std::invalid_argument &) {
                                                       return false;
                                                   }
                                           });

    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForInt string not an int "
                  << resultString << std::endl;
    }
    return result;
}

float AwsDoc::DynamoDB::askQuestionForFloatRange(const Aws::String &string, float low,
                                                 float high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                float number = std::stof(string1);
                return number >= low && number <= high;
            }
            catch (const std::invalid_argument &) {
                return false;
            }
    });
    float result = 0;
    try {
        result = std::stof(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

int AwsDoc::DynamoDB::askQuestionForIntRange(const Aws::String &string, int low,
                                             int high) {
    Aws::String resultString = askQuestion(string, [low, high](
            const Aws::String &string1) -> bool {
            try {
                int number = std::stoi(string1);
                return number >= low && number <= high;
            }
            catch (const std::invalid_argument &) {
                return false;
            }
    });
    int result = 0;
    try {
        result = std::stoi(resultString);
    }
    catch (const std::invalid_argument &) {
        std::cerr << "DynamoDB::askQuestionForFloatRange string not an int "
                  << resultString << std::endl;
    }

    return result;
}

void AwsDoc::DynamoDB::printMovieInfo(
        const Aws::Map<Aws::String, Aws::DynamoDB::Model::AttributeValue> &movieMap) {
    {
        auto const &iter = movieMap.find(TITLE_KEY);
        if (iter != movieMap.end()) {
            std::cout << "Movie title: '" + iter->second.GetS() << "'." << std::endl;
        }
    }

    {
        auto const &iter = movieMap.find(YEAR_KEY);
        if (iter != movieMap.end()) {
            std::cout << "    Year: " + iter->second.GetN() << "." << std::endl;
        }
    }

    {
        auto const &iter = movieMap.find(INFO_KEY);
        if (iter != movieMap.end()) {
            Aws::Map<Aws::String, const std::shared_ptr<Aws::DynamoDB::Model::AttributeValue>> infoMap =
                    iter->second.GetM();

            auto const &ratingIter = infoMap.find(RATING_KEY);
            if (ratingIter != infoMap.end()) {
                std::cout << "    Rating: " + ratingIter->second->GetN() << "."
                          << std::endl;
            }

            auto const &plotIter = infoMap.find(PLOT_KEY);
            if (plotIter != infoMap.end()) {
                std::cout << "    Synopsis: " + plotIter->second->GetS() << "."
                          << std::endl;
            }
        }
    }
}

static int deflateZip(FILE *source, FILE *dest);

Aws::String AwsDoc::DynamoDB::getMovieJSON() {
    const int BUFFER_SIZE = 1024;
    const Aws::String JSON_FILE_NAME("moviedata.json");
    Aws::String result;

#ifdef _HAS_ZLIB_
    const Aws::String ZIP_FILE_NAME("movie.zip");

    Aws::Client::ClientConfiguration config;

    auto httpClient = Aws::Http::CreateHttpClient(config);
    auto request = Aws::Http::CreateHttpRequest(Aws::String(
                                                        "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip"),
                                                Aws::Http::HttpMethod::HTTP_GET,
                                                Aws::Utils::Stream::DefaultResponseStreamFactoryMethod);
    request->SetUserAgent("curl/7.79.1");
    std::cout << "Downloading the json file." << std::endl;
    auto response = httpClient->MakeRequest(request);

    if (Aws::Http::HttpResponseCode::OK == response->GetResponseCode()) {
        {
            std::ofstream outStream(ZIP_FILE_NAME);
            outStream << response->GetResponseBody().rdbuf();
        }
        FILE *src = fopen(ZIP_FILE_NAME.c_str(), "r");
        FILE *dst = fopen(JSON_FILE_NAME.c_str(), "w");

        std::cout << "Unzipping the json file." << std::endl;
        int zipResult = deflateZip(src, dst);
        if (zipResult != Z_OK) {
            std::cerr << "Could not deflate zip file" << std::endl;
        }
        fclose(src);
        fclose(dst);
    }
    else {
        std::cerr << "Could not download json File "
                  << response->GetClientErrorMessage() << std::endl;
    }
#endif //_HAS_ZLIB_
    std::ifstream movieData(JSON_FILE_NAME);
    if (movieData) { // NOLINT (readability-implicit-bool-conversion)
        std::array<char, BUFFER_SIZE> buffer{};
        while (movieData) { // NOLINT (readability-implicit-bool-conversion)
            movieData.read(&buffer[0], buffer.size() - 2);
            buffer[movieData.gcount()] = 0;
            result += &buffer[0];
        }
    }
    else {
        std::cerr << "Could not open '" << JSON_FILE_NAME << "'." << std::endl;
#ifndef _HAS_ZLIB_
        std::cerr << "This app was built without zlib." << std::endl;
        std::cerr << "To run the complete scenario, install zlib or" << std::endl;
        std::cerr << "download and unzip the following file to your run directory."
                  << std::endl;
        std::cerr
                << "https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/samples/moviedata.zip"
                << std::endl;
#endif //_HAS_ZLIB_
    }

    return result;
}

#ifdef _HAS_ZLIB_
int deflateZip(FILE *source, FILE *dest) {
    const int IN_CHUNK = 32767;
    const int OUT_CHUNK = 65535;
    int ret;
    unsigned have;
    z_stream strm = {};
    unsigned char in[IN_CHUNK];
    unsigned char out[OUT_CHUNK];

    // Read to the end of the local file header.
    struct __attribute__((__packed__)) ZipHeader {
        uint16_t ignored[13];
        uint16_t fileNameLength;
        uint16_t extraFieldLength;
    };
    ZipHeader header{};

    fread(&header, 1, sizeof(header), source);
    fread(in, 1, header.fileNameLength + header.extraFieldLength, source);
    // Local file header read.

    strm.data_type = Z_BINARY;
    strm.zalloc = Z_NULL;
    strm.zfree = Z_NULL;
    strm.opaque = Z_NULL;
    strm.avail_in = 0;
    strm.next_in = Z_NULL;
    int windowBits = -MAX_WBITS;
    ret = inflateInit2(&strm, windowBits);
    if (ret != Z_OK)
        return ret;

     do {
        strm.avail_in = fread(in, 1, IN_CHUNK, source);
        if (ferror(source)) {
            (void) inflateEnd(&strm);
            return Z_ERRNO;
        }
        if (strm.avail_in == 0)
            break;
        strm.next_in = in;

         do {
            strm.avail_out = OUT_CHUNK;
            strm.next_out = out;
            ret = inflate(&strm, Z_SYNC_FLUSH);
            assert(ret != Z_STREAM_ERROR);  // State not clobbered.
            switch (ret) {
                case Z_NEED_DICT:
                    ret = Z_DATA_ERROR;     // And fall through.
                case Z_DATA_ERROR:
                case Z_MEM_ERROR:
                    (void) inflateEnd(&strm);
                    return ret;
                default:
                    break;
            }
            have = OUT_CHUNK - strm.avail_out;
            if (fwrite(out, 1, have, dest) != have || ferror(dest)) {
                (void) inflateEnd(&strm);
                return Z_ERRNO;
            }
        } while (strm.avail_out == 0);

     } while (ret != Z_STREAM_END);

    (void) inflateEnd(&strm);
    return ret == Z_STREAM_END ? Z_OK : Z_DATA_ERROR;
}
#endif
