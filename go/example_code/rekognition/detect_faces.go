// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"flag"
	"fmt"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rekognition"
)

var svc *rekognition.Rekognition
var bucket = flag.String("bucket", "", "The name of the bucket")
var photo = flag.String("photo", "", "The path to the photo file (JPEG, JPG, PNG)")
var checks = true

func init() {

	//https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/sessions.html

	flag.StringVar(bucket, "b", "", "The name of the bucket")
	flag.StringVar(photo, "p", "", "The path to the photo file (JPEG, JPG, PNG)")
	flag.Parse()

	if *bucket == "" || *photo == "" {
		checks = false
		flag.PrintDefaults()
		fmt.Println("You must supply a bucket name (-b BUCKET) and photo file (-p PHOTO)")
		return
	}

	fileExtension := filepath.Ext(*photo)
	validExtension := map[string]bool{
		".png":  true,
		".jpg":  true,
		".jpeg": true,
	}

	if !validExtension[fileExtension] {
		checks = false
		fmt.Println("Rekognition only supports jpeg, jpg or png")
		return
	}

	//Access keys are read from ~/.aws/credentials
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("ap-south-1"),
	})

	if err != nil {
		checks = false
		fmt.Println("Error while creating session,", err)
		return
	}

	svc = rekognition.New(sess)
	_ = svc
}

func main() {

	if checks == false {
		return
	}

	params := &rekognition.DetectFacesInput{
		Image: &rekognition.Image{

			S3Object: &rekognition.S3Object{
				Bucket: bucket,
				Name:   photo,
			},
		},
		Attributes: []*string{
			aws.String("ALL"),
		},
	}

	resp, err := svc.DetectFaces(params)
	if err != nil {
		fmt.Println(err.Error())
		return
	}

	if len(resp.FaceDetails) == 0 {
		fmt.Println("No faces detected in the image !")
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
			fmt.Printf("Emotion : %v\n", *fdetails.Emotions[0].Type)
		}

		if fdetails.Gender != nil {
			fmt.Printf("Gender : %v\n\n", *fdetails.Gender.Value)
		}
	}

}
