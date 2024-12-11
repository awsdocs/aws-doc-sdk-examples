// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
//
/// An example that demonstrates how to watch an transcribe event stream.

// snippet-start:[swift.s3.binary-streaming.imports]
import ArgumentParser
import AWSClientRuntime
import AWSTranscribeStreaming
import Foundation
import Smithy
//import SmithyHTTPAPI
import SmithyStreams

// snippet-end:[swift.s3.binary-streaming.imports]

// -MARK: - Async command line tool

struct ExampleCommand: ParsableCommand {
    enum MediaFormat: String, ExpressibleByArgument {
        case ogg = "ogg"
        case pcm = "pcm"
        case flac = "flac"
    }

    // -MARK: Command arguments
    @Option(help: "Language code to transcribe into")
    var lang: String = "en-US"
    @Option(help: "Format of the source audio file")
    var format: MediaFormat
    @Option(help: "Sample rate of the source audio file in Hertz")
    var sampleRate: Int = 16000
    @Option(help: "Path of the source audio file")
    var path: String
    @Option(help: "Name of the Amazon S3 Region to use (default: us-east-1)")
    var region = "us-east-1"

    static var configuration = CommandConfiguration(
        commandName: "tsevents",
        abstract: """
        This example shows how to use event streaming with Amazon Transcribe.
        """,
        discussion: """
        """
    )

    func transcribe() async throws {
        let config = try await TranscribeStreamingClient.TranscribeStreamingClientConfiguration(
            region: region
        )
        let client = TranscribeStreamingClient(config: config)

        let mediaEncoding: TranscribeStreamingClientTypes.MediaEncoding
        switch format {
        case .flac:
            mediaEncoding = .flac
        case .ogg:
            mediaEncoding = .oggOpus
        case .pcm:
            mediaEncoding = .pcm
        }

        // Create the source audio stream.

        let fileURL: URL = URL(fileURLWithPath: path)
        let audioData = try Data(contentsOf: fileURL)

        print("Processing file...")

        // Properties defining the size of audio chunks and the total size of
        // the audio file in bytes.

        let chunkSize = 16384
        let audioDataSize = audioData.count

        // Create an audio stream from the source data. The stream's job is
        // to send the audio in chunks to Amazon Transcribe as
        // `AudioStream.audioevent` events.

        let audioStream = AsyncThrowingStream<TranscribeStreamingClientTypes.AudioStream,
                                Error> { continuation in
            Task {
                var currentStart = 0
                var currentEnd = min(chunkSize, audioDataSize - currentStart)

                while currentStart < audioDataSize {
                    let dataChunk = audioData[currentStart ..< currentEnd]
                    
                    let audioEvent = TranscribeStreamingClientTypes.AudioStream.audioevent(
                        .init(audioChunk: dataChunk)
                    )
                    continuation.yield(audioEvent)

                    currentStart = currentEnd
                    currentEnd = min(currentStart + chunkSize, audioDataSize)
                }

                continuation.finish()
            }
        }

        // Start the transcription running on the audio stream.

        let output = try await client.startStreamTranscription(
            input: StartStreamTranscriptionInput(
                audioStream: audioStream,
                languageCode: TranscribeStreamingClientTypes.LanguageCode(rawValue: lang),
                mediaEncoding: mediaEncoding,
                mediaSampleRateHertz: sampleRate
            )
        )

        // Iterate over the events in the transcript result stream. Each
        // `transcriptevent` contains a list of result fragments which need
        // to be concatenated together to build the final transcript.
        for try await event in output.transcriptResultStream! {
            switch event {
            case .transcriptevent(let event):
            for result in event.transcript?.results ?? [] {
                guard let transcript = result.alternatives?.first?.transcript else {
                    continue
                }

                // When the complete fragment of transcribed text is ready,
                // print it. This could just as easily be used to draw the
                // text as a subtitle over a playing video, though timing
                // would need to be managed.

                if !result.isPartial {
                    print(transcript)
                }
            }
            default:
                print("Error: Unexpected message from Amazon Transcribe:")
            }
        }
    }
}

// -MARK: - Entry point

/// The program's asynchronous entry point.
@main
struct Main {
    static func main() async {
        let args = Array(CommandLine.arguments.dropFirst())

        do {
            let command = try ExampleCommand.parse(args)
            try await command.transcribe()
        } catch let error as TranscribeError {
            print("ERROR: \(error.errorDescription ?? "Unknown error")")
        } catch {
            ExampleCommand.exit(withError: error)
        }
    }    
}
