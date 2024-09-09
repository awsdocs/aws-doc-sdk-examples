package main

import (
	"log"

	"github.com/gordonklaus/portaudio"
)

type MicProvider struct {
	intBuf []int16
	stream *portaudio.Stream
}

func NewMicProvider(log *log.Logger) *MicProvider {
	portaudio.Initialize()

	defaultBytesToRead := 2048

	intBuf := make([]int16, defaultBytesToRead)

	inputChannels := 1
	samplingRate := 16000

	stream, err := portaudio.OpenDefaultStream(inputChannels, 0, float64(samplingRate), len(intBuf), intBuf)
	if err != nil {
		log.Fatal("failed to initialize port audio:", err)
	}

	stream.Start()

	return &MicProvider{
		intBuf: intBuf,
		stream: stream,
	}
}

func (mp *MicProvider) Read() []int16 {
	err := mp.stream.Read()
	if err != nil {
		return []int16{}
	}

	return mp.intBuf
}
