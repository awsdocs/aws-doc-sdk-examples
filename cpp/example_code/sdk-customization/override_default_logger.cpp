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

#include <iostream>
#include <fstream>
#include <aws/core/Aws.h>
#include <aws/s3/S3Client.h>
#include <aws/core/utils/logging/DefaultLogSystem.h>
#include <aws/core/utils/stream/SimpleStreamBuf.h>


/*
 * This class overrides the default log system and allows temporary logging to a
 * 'SimpleStreamBuf'.
 */
// snippet-start:[cpp.example_code.sdk_customization.override_logger.LogSystem]
class LogSystemOverride : public Aws::Utils::Logging::DefaultLogSystem {
public:
    explicit LogSystemOverride(Aws::Utils::Logging::LogLevel logLevel,
                               const Aws::String &logPrefix)
            : DefaultLogSystem(logLevel, logPrefix), mLogToStreamBuf(false) {}

    const Aws::Utils::Stream::SimpleStreamBuf &GetStreamBuf() const {
        return mStreamBuf;
    }

    void setLogToStreamBuf(bool logToStreamBuf) {
        mLogToStreamBuf = logToStreamBuf;
    }

protected:

    void ProcessFormattedStatement(Aws::String &&statement) override {
        if (mLogToStreamBuf) {
            std::lock_guard<std::mutex> lock(mStreamMutex);
            mStreamBuf.sputn(statement.c_str(), statement.length());
        }

        DefaultLogSystem::ProcessFormattedStatement(std::move(statement));
    }

private:
    Aws::Utils::Stream::SimpleStreamBuf mStreamBuf;
    // Use a mutex when writing to the buffer because
    // ProcessFormattedStatement can be called from multiple threads.
    std::mutex mStreamMutex;
    std::atomic<bool> mLogToStreamBuf;
};
// snippet-end:[cpp.example_code.sdk_customization.override_logger.LogSystem]

/*
 *
 *  main function
 *
 *  Usage: 'run_override_default_logger'
 *
 */
// snippet-start:[cpp.example_code..sdk_customization.override_logger.Use]
int main(int argc, char **argv) {
    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;
    auto logSystemOverride = Aws::MakeShared<LogSystemOverride>("AllocationTag",
                                                                options.loggingOptions.logLevel,
                                                                options.loggingOptions.defaultLogPrefix);
    options.loggingOptions.logger_create_fn = [logSystemOverride]() {
        return logSystemOverride;
    };

    Aws::InitAPI(options);  // Call Aws::InitAPI only once in an application.
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        Aws::S3::S3Client s3Client(clientConfig);

        logSystemOverride->setLogToStreamBuf(true);
        auto outcome = s3Client.ListBuckets();
        if (!outcome.IsSuccess()) {
            std::cerr << "ListBuckets error: " <<
                      outcome.GetError().GetExceptionName() << " " <<
                      outcome.GetError().GetMessage() << std::endl;
        }

        logSystemOverride->setLogToStreamBuf(false);

        std::cout << "Log for ListBuckets" << std::endl;
        std::cout << logSystemOverride->GetStreamBuf().str() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}
// snippet-end:[cpp.example_code..sdk_customization.override_logger.Use]
