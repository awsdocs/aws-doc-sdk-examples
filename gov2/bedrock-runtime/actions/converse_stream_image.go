// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package actions

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"strings"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime"
	"github.com/aws/aws-sdk-go-v2/service/bedrockruntime/types"
)

const (
	promptImage = `Describe what you see in this image in detail.`
)

func main() {
	modelID := "us.anthropic.claude-3-7-sonnet-20250219-v1:0"
	// load AWS configure
	cfg, err := config.LoadDefaultConfig(context.TODO(),
		config.WithRegion("us-west-2"), // replace your bedrock region
	)
	if err != nil {
		log.Fatal("unable to load SDK configuration:", err)
	}

	// create bedrock runtime client
	client := bedrockruntime.NewFromConfig(cfg)

	// read image file
	imagePath := "C:\\Users\\pc\\Downloads\\img.png" // Update with your image path
	imageBytes, err := os.ReadFile(imagePath)
	if err != nil {
		log.Fatal("unable to read image:", err)
	}

	// create image block with format specified
	imageBlock := types.ContentBlockMemberImage{
		Value: types.ImageBlock{
			Format: types.ImageFormatPng, // Add the format based on your image type
			Source: &types.ImageSourceMemberBytes{
				Value: imageBytes,
			},
		},
	}

	// create message
	messages := types.Message{
		Role: "user",
		Content: []types.ContentBlock{
			&types.ContentBlockMemberText{
				Value: promptImage,
			},
			&imageBlock,
		},
	}

	ctx := context.Background()

	// Use ConverseStream instead of Converse
	resp, err := client.ConverseStream(ctx, &bedrockruntime.ConverseStreamInput{
		ModelId:  &modelID,
		Messages: []types.Message{messages},
	})
	if err != nil {
		panic(err)
	}

	// Get the event stream
	stream := resp.GetStream()
	defer stream.Close()

	fmt.Println("Streaming response:")
	var fullResponse strings.Builder

	// Process the streaming response
	for event := range stream.Events() {
		// Type switch to handle different event types
		switch v := event.(type) {
		case *types.ConverseStreamOutputMemberContentBlockDelta:
			// Handle content block delta
			if delta, ok := v.Value.Delta.(*types.ContentBlockDeltaMemberText); ok {
				text := delta.Value
				fmt.Print(text)
				fullResponse.WriteString(text)
			}
		case *types.ConverseStreamOutputMemberMessageStop:
			// Handle message stop event (contains usage info)
			fmt.Println("\n\nMessage complete")
		}
	}

	// Check for any errors that occurred during streaming
	if err := stream.Err(); err != nil {
		fmt.Printf("\nStream error: %v\n", err)
	}

	fmt.Println("\nDone streaming")
	fmt.Printf("\nFull response:\n%s\n", fullResponse.String())
}

func printJSON(data interface{}) {
	jsonBytes, err := json.MarshalIndent(data, "", "  ")
	if err != nil {
		log.Printf("Error marshaling JSON: %v", err)
		return
	}
	log.Printf("\nJSON:\n%s\n", string(jsonBytes))
}