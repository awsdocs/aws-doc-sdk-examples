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

class LogSystemOverride : public Aws::Utils::Logging::DefaultLogSystem {
public:
    explicit LogSystemOverride(Aws::Utils::Logging::LogLevel logLevel,
                               const Aws::String &logPrefix)
            : DefaultLogSystem(logLevel, logPrefix), m_OverrideLog(false) {}

    const Aws::Utils::Stream::SimpleStreamBuf &GetStreamBuf() const {
        return m_StreamBuf;
    }

    void setOverrideLog(bool overrideLog) {
        m_OverrideLog = overrideLog;
    }

protected:

    virtual void ProcessFormattedStatement(Aws::String &&statement) override {
        if (m_OverrideLog) {
            std::lock_guard<std::mutex> lock(m_StreamMutex);
            m_StreamBuf.sputn(statement.c_str(), statement.length());
        }
        else {
            DefaultLogSystem::ProcessFormattedStatement(std::move(statement));
        }
    }

private:
    Aws::Utils::Stream::SimpleStreamBuf m_StreamBuf;
    // Use a mutex when writing to the buffer because
    // ProcessFormattedStatement can be called from multiple threads.
    std::mutex m_StreamMutex;
    std::atomic<bool> m_OverrideLog;
};

/*
 *
 *  main function
 *
 *  Usage: 'run_override_default_logger'
 *
 */

int main(int argc, char **argv) {

    Aws::SDKOptions options;
    options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;
    auto logSystemOverride = Aws::MakeShared<LogSystemOverride>("AllocationTag",
                                                                options.loggingOptions.logLevel,
                                                                options.loggingOptions.defaultLogPrefix);
    options.loggingOptions.logger_create_fn = [logSystemOverride]() {
            return logSystemOverride;
    };

    Aws::InitAPI(options);
    {
        Aws::Client::ClientConfiguration clientConfig;
        // Optional: Set to the AWS Region (overrides config file).
        // clientConfig.region = "us-east-1";

        Aws::S3::S3Client s3Client(clientConfig);

        logSystemOverride->setOverrideLog(true);
        auto outcome = s3Client.ListBuckets();
        if (outcome.IsSuccess()) {
            std::cout << "ListBuckets succeeded:" << std::endl;
        }
        logSystemOverride->setOverrideLog(false);

        std::cout << "Log for ListBuckets" << std::endl;
        std::cout << logSystemOverride->GetStreamBuf().str() << std::endl;
    }

    Aws::ShutdownAPI(options);

    return 0;
}
