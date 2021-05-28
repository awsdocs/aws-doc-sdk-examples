// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"path/filepath"

	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/aws-sdk-go-v2/service/rekognition"
	"github.com/aws/aws-sdk-go-v2/service/rekognition/types"
)

// RekognitionDetectFacesAPI defines the interface for the DetectFaces function.
// We use this interface to test the function using a mocked service.
type RekognitionDetectFacesAPI interface {
	DetectFaces(ctx context.Context,
		params *rekognition.DetectFacesInput,
		optFns ...func(*rekognition.Options)) (*rekognition.DetectFacesOutput, error)
}

// GetFaces retrieves the faces in a jpeg, jpg, or png image.
// Inputs:
//     c is the context of the method call, which includes the AWS Region.
//     api is the interface that defines the method call.
//     input defines the input arguments to the service call.
// Output:
//     If successful, a DetectFacesOutput object containing the result of the service call and nil.
//     Otherwise, nil and an error from the call to DetectFaces.
func GetFaces(c context.Context, api RekognitionDetectFacesAPI, input *rekognition.DetectFacesInput) (*rekognition.DetectFacesOutput, error) {
	resp, err := api.DetectFaces(c, input)

	return resp, err
}

func main() {
	bucketName := flag.String("b", "", "The name of the bucket to get the object")
	image := flag.String("i", "", "The path to the image file (JPEG, JPG, PNG)")
	flag.Parse()

	if len(*bucketName) == 0 || len(*image) == 0 {
		log.Fatalf("You must supply a bucket name (-b BUCKET) and photo file (-i IMAGE)")
		return
	}

	fileExtension := filepath.Ext(*image)
	validExtension := map[string]bool{
		".png":  true,
		".jpg":  true,
		".jpeg": true,
	}

	if !validExtension[fileExtension] {
		fmt.Println("Rekognition only supports jpeg, jpg or png")
		return
	}

	// Load the SDK's configuration from environment and shared config, and
	// create the client with this.
	cfg, err := config.LoadDefaultConfig(context.TODO())
	if err != nil {
		log.Fatalf("failed to load SDK configuration, %v", err)
	}

	client := rekognition.NewFromConfig(cfg)

	input := &rekognition.DetectFacesInput{
		Image: &types.Image{
			S3Object: &types.S3Object{
				Bucket: bucketName,
				Name:   image,
			},
		},
		Attributes: []types.Attribute{
			"ALL",
		},
	}

	// Get faces from image
	resp, err := GetFaces(context.TODO(), client, input)
	if err != nil {
		fmt.Println("Got an error calling GetFaces")
		return
	}

	if len(resp.FaceDetails) == 0 {
		fmt.Println("No faces detected in the image")
		return
	}

	for idx, fdetails := range resp.FaceDetails {
		fmt.Printf("Person #%d : \n", idx+1)
		fmt.Printf("Position : %v %v \n", *fdetails.BoundingBox.Left, *fdetails.BoundingBox.Top)

		if fdetails.AgeRange != nil {
			fmt.Printf("Age (Low) : %d \n", *fdetails.AgeRange.Low)
			fmt.Printf("Age (High) : %d \n", *fdetails.AgeRange.High)
		}

		if fdetails.Emotions != nil {
			fmt.Printf("Emotion : %v\n", fdetails.Emotions[0].Type)
		}

		if fdetails.Gender != nil {
			fmt.Printf("Gender : %v\n\n", fdetails.Gender.Value)
		}
	}
}
