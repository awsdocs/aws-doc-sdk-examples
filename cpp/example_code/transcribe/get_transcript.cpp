/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

#include <aws/core/Aws.h>
#include <aws/core/utils/threading/Semaphore.h>
#include <aws/core/auth/AWSCredentialsProviderChain.h>
#include <aws/transcribestreaming/TranscribeStreamingServiceClient.h>
#include <aws/transcribestreaming/model/StartStreamTranscriptionHandler.h>
#include <aws/transcribestreaming/model/StartStreamTranscriptionRequest.h>
#include <aws/core/platform/FileSystem.h>
#include <fstream>
#include <chrono>
#include <thread>
#include <array>

using namespace Aws;
using namespace Aws::TranscribeStreamingService;
using namespace Aws::TranscribeStreamingService::Model;

//TODO(User): Update path to location of local .wav test file, if necessary.
static const Aws::String FILE_NAME{ MEDIA_DIR "/transcribe-test-file.wav"};
static const int BUFFER_SIZE = 1024;
static const int END_OF_STREAM_SLEEP_SECONDS = 10;

//snippet-start:[transcribe.cpp.stream_transcription_async.code]
int main()
{
	Aws::SDKOptions options;
	options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;
	Aws::InitAPI(options);
	{
		//TODO(User): Set to the region of your AWS account.
		const Aws::String region = Aws::Region::US_WEST_2;

		Aws::Utils::Threading::Semaphore received(0 /*initialCount*/, 1 /*maxCount*/);

		//Load a profile that has been granted AmazonTranscribeFullAccess AWS managed permission policy.
		Aws::Client::ClientConfiguration config;
#ifdef _WIN32
		// ATTENTION: On Windows with AWS SDK version 1.9, this example only runs if the SDK is built
        // with the curl library.  (9/15/2022)
		// See the accompanying ReadMe.
		// See "Building the SDK for Windows with curl".
		// https://docs.aws.amazon.com/sdk-for-cpp/v1/developer-guide/setup-windows.html
		//TODO(User): Update to location of your .crt file.
		config.caFile = "C:/curl/bin/curl-ca-bundle.crt";
#endif
		config.region = region;

		TranscribeStreamingServiceClient client(config);
		StartStreamTranscriptionHandler handler;
		handler.SetOnErrorCallback([](const Aws::Client::AWSError<TranscribeStreamingServiceErrors>& error) {
            std::cerr << "ERROR: " + error.GetMessage() << std::endl;
			});
		//SetTranscriptEventCallback called for every 'chunk' of file transcripted. Partial results are returned in real time.
		handler.SetTranscriptEventCallback([&received](const TranscriptEvent& ev) {
			for (auto&& r : ev.GetTranscript().GetResults()) {
				if (r.GetIsPartial()) {
                    std::cout << "[partial] ";
				}
				else {
                    std::cout << "[Final] ";
				}
				for (auto&& alt : r.GetAlternatives()) {
                    std::cout << alt.GetTranscript() << std::endl;
				}
			}
			received.Release();
			});

		StartStreamTranscriptionRequest request;
		request.SetMediaSampleRateHertz(8000);
		request.SetLanguageCode(LanguageCode::en_US);
		request.SetMediaEncoding(MediaEncoding::pcm); //wav and aiff files are PCM formats
		request.SetEventStreamHandler(handler);

		auto OnStreamReady = [](AudioStream& stream)
		{
			Aws::FStream file(FILE_NAME, std::ios_base::in | std::ios_base::binary);
			if (!file.is_open()) {
				std::cerr << "Failed to open " << FILE_NAME << '\n';
			}
			std::array<char,BUFFER_SIZE> buf;
			int i = 0;
			while (file)
			{
				file.read(&buf[0], buf.size());

				if (!file)
					std::cout << "File: only " << file.gcount() << " could be read" << std::endl;

				Aws::Vector<unsigned char> bits{ buf.begin(), buf.end()};
				AudioEvent event(std::move(bits));
				if (!stream)
				{
                    std::cerr << "Failed to create a stream" << std::endl;
					break;
				}
				//The std::basic_istream::gcount() is used to count the characters in the given string. It returns
				//the number of characters extracted by the last read() operation.
				if (file.gcount() > 0) {
					if (!stream.WriteAudioEvent(event))
					{
                        std::cerr << "Failed to write an audio event" << std::endl;
 						break;
					}
				}
				else {
					break;
				}
				std::this_thread::sleep_for(std::chrono::milliseconds(25)); // Slow down since we are streaming from a file.
			}
			if (!stream.WriteAudioEvent(AudioEvent())) { // Per the spec, we have to send an empty event (i.e. without a payload) at the end.
				std::cerr << "Failed to send an empty frame" << std::endl;
			}
			else {
                std::cout << "Successfully sent the empty frame" << std::endl;
			}
			stream.flush();
			std::this_thread::sleep_for(std::chrono::seconds (END_OF_STREAM_SLEEP_SECONDS)); // Workaround to prevent an error at the end of the stream for this contrived example.
			stream.Close();
		};

		Aws::Utils::Threading::Semaphore signaling(0 /*initialCount*/, 1 /*maxCount*/);
		auto OnResponseCallback = [&signaling](const TranscribeStreamingServiceClient* /*unused*/,
			const Model::StartStreamTranscriptionRequest& /*unused*/,
			const Model::StartStreamTranscriptionOutcome& /*unused*/,
			const std::shared_ptr<const Aws::Client::AsyncCallerContext>& /*unused*/) {
				signaling.Release();
		};

        std::cout << "Starting..." << std::endl;
		client.StartStreamTranscriptionAsync(request, OnStreamReady, OnResponseCallback, nullptr /*context*/);
		signaling.WaitOne(); // Prevent the application from exiting until we're done.
        std::cout << "Done" << std::endl;
	}

	Aws::ShutdownAPI(options);

	return 0;
}
//snippet-end:[transcribe.cpp.stream_transcription_async.code]
