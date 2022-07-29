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
#include <cstdio>
#include <chrono>
#include <thread>

using namespace Aws;
using namespace Aws::TranscribeStreamingService;
using namespace Aws::TranscribeStreamingService::Model;

//TODO: Update path to location of local .wav test file.
static const char FILE_NAME[] = "C:\\TODO\\transcribe-test-file.wav";

int main()
{
	Aws::SDKOptions options;
	options.loggingOptions.logLevel = Aws::Utils::Logging::LogLevel::Trace;
	Aws::InitAPI(options);
	{
		//TODO: Set to the region of your AWS account.
		const Aws::String region = Aws::Region::US_WEST_2;

		Aws::Utils::Threading::Semaphore received(0 /*initialCount*/, 1 /*maxCount*/);

		//Load a profile that has been granted AmazonTranscribeFullAccess AWS managed permission policy.
		Aws::Client::ClientConfiguration config("TODO");
		//TODO: Update to location of your .crt file.
		config.caFile = "C:\\TODO\\curl-ca-bundle.crt";
		config.region = region;

		TranscribeStreamingServiceClient client(config);
		StartStreamTranscriptionHandler handler;
		handler.SetOnErrorCallback([](const Aws::Client::AWSError<TranscribeStreamingServiceErrors>& error) {
			printf("ERROR: %s", error.GetMessage().c_str());
			});
		//SetTranscriptEventCallback called for every 'chunk' of file transcripted. Partial results are returned in real time.
		handler.SetTranscriptEventCallback([&received](const TranscriptEvent& ev) {
			for (auto&& r : ev.GetTranscript().GetResults()) {
				if (r.GetIsPartial()) {
					printf("[partial] ");
				}
				else {
					printf("[Final] ");
				}
				for (auto&& alt : r.GetAlternatives()) {
					printf("%s\n", alt.GetTranscript().c_str());
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
				std::cout << "Failed to open " << FILE_NAME << '\n';
			}
			char buf[1024];
			int i = 0;
			while (file)
			{
				file.read(buf, sizeof(buf));

				if (!file)
					std::cout << "File: only " << file.gcount() << " could be read";

				Aws::Vector<unsigned char> bits{ buf, buf + file.gcount() };
				AudioEvent event(std::move(bits));
				if (!stream)
				{
					printf("Failed to create a stream\n");
					break;
				}
				//The std::basic_istream::gcount() is used to count the characters in the given string. It returns
				//the number of characters extracted by the last read() operation.
				if (file.gcount() > 0) {
					if (!stream.WriteAudioEvent(event))
					{
						printf("Failed to write an audio event\n");
						break;
					}
				}
				else {
					break;
				}
				std::this_thread::sleep_for(std::chrono::milliseconds(25)); // Slow down since we are streaming from a file.
			}
			if (!stream.WriteAudioEvent(AudioEvent())) { // Per the spec, we have to send an empty event (i.e. without a payload) at the end.
				printf("Failed to send an empty frame\n");
			}
			else {
				printf("Successfully sent the empty frame\n");
			}
			stream.flush();
			std::this_thread::sleep_for(std::chrono::milliseconds(10000)); // Workaround to prevent an error at the end of the stream for this contrived example.
			stream.Close();
		};

		Aws::Utils::Threading::Semaphore signaling(0 /*initialCount*/, 1 /*maxCount*/);
		auto OnResponseCallback = [&signaling](const TranscribeStreamingServiceClient*,
			const Model::StartStreamTranscriptionRequest&,
			const Model::StartStreamTranscriptionOutcome&,
			const std::shared_ptr<const Aws::Client::AsyncCallerContext>&) {
				signaling.Release();
		};

		printf("Starting...\n");
		client.StartStreamTranscriptionAsync(request, OnStreamReady, OnResponseCallback, nullptr /*context*/);
		signaling.WaitOne(); // Prevent the application from exiting until we're done.
		printf("Done\n");
	}

	Aws::ShutdownAPI(options);

	return 0;

}