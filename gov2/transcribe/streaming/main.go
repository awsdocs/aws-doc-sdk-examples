package main

import (
	"bufio"
	"bytes"
	"context"
	"encoding/binary"
	"log"
	"os"

	"github.com/aws/aws-sdk-go-v2/aws"
	transcribe "github.com/aws/aws-sdk-go-v2/service/transcribestreaming"
	"github.com/aws/aws-sdk-go-v2/service/transcribestreaming/types"
)

func main() {
	logger := log.New(os.Stdout, "", log.Lmsgprefix)
	ctx := context.Background()

	mic, err := NewMicProvider(logger)
	if err != nil {
		log.Fatal("failed to initialize port audio:", err)
	}

	cp := aws.CredentialsProviderFunc(func(ctx context.Context) (aws.Credentials, error) {
		return aws.Credentials{
			AccessKeyID:     "xxx Access Key Id xxx",
			SecretAccessKey: "xxx Access Key Secret xxx",
		}, nil
	})

	options := transcribe.Options{
		Credentials: cp,
		Region:      "eu-west-1",
	}

	tc := transcribe.New(options)

	sti := &transcribe.StartStreamTranscriptionInput{
		LanguageCode:         "en-US",
		MediaSampleRateHertz: aws.Int32(16000),
		MediaEncoding:        types.MediaEncodingPcm,
	}
	sto, err := tc.StartStreamTranscription(ctx, sti)
	if err != nil {
		logger.Fatal(err)
		return
	}
	stream := sto.GetStream()

	startMicrophone(ctx, mic, stream)
	readTranscription(stream)

	// wait for user input to exit
	input := bufio.NewScanner(os.Stdin)
	input.Scan()
}

func startMicrophone(ctx context.Context, mic *MicProvider, stream *transcribe.StartStreamTranscriptionEventStream) {
	go func() {
		for {
			audio := mic.Read()
			data := int16ToLittleEndianByte(audio)

			audioStream := &types.AudioStreamMemberAudioEvent{
				Value: types.AudioEvent{
					AudioChunk: data,
				},
			}

			err := stream.Send(ctx, audioStream)
			if err != nil {
				log.Println("failed to send audio chunk: ", err)
			}
		}
	}()
}

func readTranscription(stream *transcribe.StartStreamTranscriptionEventStream) {
	go func() {
		for {
			resultStream := <-stream.Events()
			event, ok := resultStream.(*types.TranscriptResultStreamMemberTranscriptEvent)
			if !ok {
				log.Println("error casting result stream to event")
				continue
			}

			results := event.Value.Transcript.Results
			for _, result := range results {
				if result.IsPartial {
					// if desired, process partial results here
					continue
				}

				log.Println("=== Result -> IsPartial: ", result.IsPartial)

				for _, alternative := range result.Alternatives {
					log.Println("\t--- Transcript: ", *alternative.Transcript) // the final transcription
					log.Printf("\t--- Items: %+v \n", alternative.Items)       // this contains all the words, phrases, and punctuations contained in the transcription
				}
			}
		}
	}()
}

func int16ToLittleEndianByte(f []int16) []byte {
	var buf bytes.Buffer
	err := binary.Write(&buf, binary.LittleEndian, f)
	if err != nil {
		log.Println("binary.Write failed. Err:", err)
	}

	return buf.Bytes()
}
