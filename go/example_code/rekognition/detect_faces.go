// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourceauthor:[Doug-AWS]
// snippet-sourcedescription:[Deletes the website configuration on an S3 bucket.]
// snippet-keyword:[Amazon Simple Storage Service]
// snippet-keyword:[Amazon S3]
// snippet-keyword:[DeleteBucketWebsite function]
// snippet-keyword:[Go]
// snippet-sourcesyntax:[go]
// snippet-service:[s3]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2018-03-16]
/*
   Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at
    http://aws.amazon.com/apache2.0/
   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

package main

import (
	"flag"
	"fmt"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/rekognition"
)

var svc *rekognition.Rekognition
var bucket = flag.String("bucket", "", "The name of the bucket")
var photo = flag.String("photo", "", "The path to the photo file (JPEG, GIF, or ???)")

func init() {

	//https://docs.aws.amazon.com/sdk-for-go/v1/developer-guide/sessions.html

	flag.StringVar(bucket, "b", "", "The name of the bucket")
	flag.StringVar(photo, "p", "", "The path to the photo file (JPEG, GIF, or ???)")
	flag.Parse()

	if *bucket == "" || *photo == "" {
		fmt.Println("You must supply a bucket name (-b BUCKET) and photo file (-p PHOTO)")
		return
	}

	//Access keys are read from ~/.aws/credentials
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("ap-south-1"),
	})

	if err != nil {
		fmt.Println("Error while creating session,", err)
		return
	}

	svc = rekognition.New(sess)
	_ = svc
}

func main() {

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
