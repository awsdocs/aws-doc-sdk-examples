/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

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
    "encoding/json"
    "errors"
    "io/ioutil"
    "testing"
    "time"

    "github.com/google/uuid"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/iam"
    "github.com/aws/aws-sdk-go/service/lambda"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"
)

type Config struct {
    Bucket   string `json:"Bucket"`
    Function string `json:"Function"`
    Handler  string `json:"Handler"`
    RoleARN  string `json:"RoleARN"`
    Runtime  string `json:"Runtime"`
    ZipFile  string `json:"ZipFile"`
    Cleanup  bool   `json:"Cleanup"`
}

var configFileName = "config.json"

var globalConfig Config

func PopulateConfiguration(t *testing.T) error {
    content, err := ioutil.ReadFile(configFileName)
    if err != nil {
        return err
    }

    text := string(content)

    err = json.Unmarshal([]byte(text), &globalConfig)
    if err != nil {
        return err
    }

    t.Log("Default config.json values:")
    t.Log("Bucket:   " + globalConfig.Bucket)
    t.Log("Function: " + globalConfig.Function)
    t.Log("Handler:  " + globalConfig.Handler)
    t.Log("RoleARN:  " + globalConfig.RoleARN)
    t.Log("Runtime:  " + globalConfig.Runtime)
    t.Log("ZipFile:  " + globalConfig.ZipFile)

    return nil
}

func createRole(sess *session.Session, roleName *string) (*iam.CreateRoleOutput, error) {
    svc := iam.New(sess)

    trustRelationship := []byte(`{
        "Version": "2012-10-17",
        "Statement": [
           {
            "Effect": "Allow",
            "Principal": {
               "Service": "lambda.amazonaws.com"
            },
            "Action": "sts:AssumeRole"
         }
        ]
     }`)

    trustPolicy := string(trustRelationship[:])

    result, err := svc.CreateRole(&iam.CreateRoleInput{
        AssumeRolePolicyDocument: aws.String(trustPolicy),
        RoleName:                 roleName,
    })

    if err != nil {
        return nil, err
    }

    _, err = svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
        PolicyArn: aws.String("arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"),
        RoleName:  roleName,
    })
    if err != nil {
        return nil, err
    }

    _, err = svc.AttachRolePolicy(&iam.AttachRolePolicyInput{
        PolicyArn: aws.String("arn:aws:iam::aws:policy/AWSXRayDaemonWriteAccess"),
        RoleName:  roleName,
    })
    if err != nil {
        return nil, err
    }

    return result, nil
}

func createBucket(sess *session.Session, bucket *string) error {
    // Create S3 service client
    svc := s3.New(sess)

    // Create the S3 Bucket
    _, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    // Wait until bucket is created before finishing
    err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    return nil
}

func deleteFunction(sess *session.Session, function *string) error {
    svc := lambda.New(sess)

    _, err := svc.DeleteFunction(&lambda.DeleteFunctionInput{
        FunctionName: function,
    })
    if err != nil {
        return err
    }

    return nil
}

func deleteRole(sess *session.Session, role *string) error {
    svc := iam.New(sess)

    _, err := svc.DeleteRole(&iam.DeleteRoleInput{
        RoleName: role,
    })
    if err != nil {
        return err
    }

    return nil
}

func deleteBucket(sess *session.Session, bucket *string) error {
    // Create S3 service client
    svc := s3.New(sess)

    // Delete all objects in the bucket
    iter := s3manager.NewDeleteListIterator(svc, &s3.ListObjectsInput{
        Bucket: bucket,
    })

    if err := s3manager.NewBatchDeleteWithClient(svc).Delete(aws.BackgroundContext(), iter); err != nil {
        return err
    }

    // Delete the S3 Bucket
    _, err := svc.DeleteBucket(&s3.DeleteBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    // Wait until bucket is gone before finishing
    err = svc.WaitUntilBucketNotExists(&s3.HeadBucketInput{
        Bucket: bucket,
    })
    if err != nil {
        return err
    }

    return nil
}

func TestLambdaUpload(t *testing.T) {
    thisTime := time.Now()
    nowString := thisTime.Format("2006-01-02 15:04:05 Monday")
    t.Log("Starting unit test at " + nowString)

    err := PopulateConfiguration(t)
    if err != nil {
        t.Fatal(err)
    }

    // Initialize a session that the SDK will use to load
    // credentials from the shared credentials file ~/.aws/credentials.
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    id := uuid.New()

    if globalConfig.Runtime == "" {
        globalConfig.Runtime = "go1.x"
    }

    roleCreated := false
    roleName := ""

    if globalConfig.RoleARN == "" {
        roleName = "my-lambda-role-" + id.String()
        result, err := createRole(sess, &roleName)
        if err != nil {
            t.Fatal(err)
        }

        globalConfig.RoleARN = *result.Role.Arn
        roleCreated = true
        t.Log("Created role ")
    }

    if globalConfig.Handler == "" {
        globalConfig.Handler = "main"
    }

    bucketCreated := false

    if globalConfig.Bucket == "" {
        globalConfig.Bucket = "my-lambda-bucket-" + id.String()
        err := createBucket(sess, &globalConfig.Bucket)
        if err != nil {
            t.Fatal(err)
        }

        t.Log("Created bucket " + globalConfig.Bucket)
        bucketCreated = true
    }

    functionCreated := false

    if globalConfig.Function == "" {
        globalConfig.Function = "my-lambda-function-" + id.String()
        functionCreated = true
    }

    if globalConfig.ZipFile == "" {
        globalConfig.ZipFile = "main"
    }

    if globalConfig.Bucket == "" || globalConfig.Function == "" || globalConfig.Handler == "" || globalConfig.RoleARN == "" || globalConfig.Runtime == "" || globalConfig.ZipFile == "" {
        t.Log("Bucket:   " + globalConfig.Bucket)
        t.Log("Function: " + globalConfig.Function)
        t.Log("Handler:  " + globalConfig.Handler)
        t.Log("RoleARN:  " + globalConfig.RoleARN)
        t.Log("Runtime:  " + globalConfig.Runtime)
        t.Log("ZipFile:  " + globalConfig.ZipFile)

        msg := "You must supply a zip file name, bucket name, function name, handler (package) name, role ARN, and runtime value."
        t.Fatal(errors.New(msg))
    }

    result, err := UploadFunction(sess, &globalConfig.ZipFile, &globalConfig.Bucket, &globalConfig.Function, &globalConfig.Handler, &globalConfig.RoleARN, &globalConfig.Runtime)
    if err != nil {
        t.Fatal(err)
    }

    t.Log("Lambda function ARN: " + *result.FunctionArn)

    if globalConfig.Cleanup {
        if functionCreated {
            err := deleteFunction(sess, &globalConfig.Function)
            if err != nil {
                t.Log("You'll have to delete function " + globalConfig.Function + " yourself")
                t.Fatal(err)
            }

            t.Log("Deleted function " + globalConfig.Function)
        }

        if roleCreated {
            err := deleteRole(sess, &roleName)
            if err != nil {
                t.Log("You'll have to delete role " + roleName + " yourself")
            }

            t.Log("Deleted role " + roleName)
        }

        if bucketCreated {
            err := deleteBucket(sess, &globalConfig.Bucket)
            if err != nil {
                t.Log("You'll have to delete the bucket " + globalConfig.Bucket + " yourself")
                t.Fatal(err)
            }

            t.Log("Deleted bucket " + globalConfig.Bucket)
        }
    }
}
