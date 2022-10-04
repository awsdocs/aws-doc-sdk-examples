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
