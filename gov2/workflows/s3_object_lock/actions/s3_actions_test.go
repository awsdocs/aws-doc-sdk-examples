// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for action errors not covered by scenarios.

package actions

import (
	"context"
	"reflect"
	"s3_object_lock/stubs"
	"testing"
	"time"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/feature/s3/manager"
	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/aws/smithy-go"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func enterTest() (context.Context, *testtools.AwsmStubber, *S3Actions) {
	stubber := testtools.NewStubber()
	actor := &S3Actions{S3Client: s3.NewFromConfig(*stubber.SdkConfig)}
	return context.Background(), stubber, actor
}

func wrapErr(expectedErr error) (error, *testtools.StubError) {
	return expectedErr, &testtools.StubError{Err: expectedErr}
}

func verifyErr(expectedErr error, actualErr error, t *testing.T) {
	if reflect.TypeOf(expectedErr) != reflect.TypeOf(actualErr) {
		t.Errorf("Expected error %T, got %T", expectedErr, actualErr)
	}
}

func TestCreateBucketWithLock(t *testing.T) {
	for _, expectedErr := range []error{&types.BucketAlreadyOwnedByYou{}, &types.BucketAlreadyExists{}} {
		ctx, stubber, actor := enterTest()
		_, stubErr := wrapErr(expectedErr)
		stubber.Add(stubs.StubCreateBucket("amzn-s3-demo-bucket", "test-region", true, stubErr))
		_, actualErr := actor.CreateBucketWithLock(ctx, "amzn-s3-demo-bucket", "test-region", true)
		verifyErr(expectedErr, actualErr, t)
		testtools.ExitTest(stubber, t)
	}
}

func TestGetObjectLegalHold(t *testing.T) {
	for _, raisedErr := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "NoSuchObjectLockConfiguration"}, &smithy.GenericAPIError{Code: "InvalidRequest"}} {
		ctx, stubber, actor := enterTest()
		_, stubErr := wrapErr(raisedErr)
		stubber.Add(stubs.StubGetObjectLegalHold("amzn-s3-demo-bucket", "test-region", "test-version", types.ObjectLockLegalHoldStatusOn, stubErr))
		_, actualErr := actor.GetObjectLegalHold(ctx, "amzn-s3-demo-bucket", "test-region", "test-version")
		expectedErr := raisedErr
		if _, ok := raisedErr.(*smithy.GenericAPIError); ok {
			expectedErr = nil
		}
		verifyErr(expectedErr, actualErr, t)
		testtools.ExitTest(stubber, t)
	}
}

func TestGetObjectLockConfiguration(t *testing.T) {
	for _, raisedErr := range []error{&types.NoSuchBucket{}, &smithy.GenericAPIError{Code: "ObjectLockConfigurationNotFoundError"}} {
		ctx, stubber, actor := enterTest()
		_, stubErr := wrapErr(raisedErr)
		stubber.Add(stubs.StubGetObjectLockConfiguration("amzn-s3-demo-bucket", types.ObjectLockEnabledEnabled, stubErr))
		_, actualErr := actor.GetObjectLockConfiguration(ctx, "amzn-s3-demo-bucket")
		expectedErr := raisedErr
		if _, ok := raisedErr.(*smithy.GenericAPIError); ok {
			expectedErr = nil
		}
		verifyErr(expectedErr, actualErr, t)
		testtools.ExitTest(stubber, t)
	}
}

func TestGetObjectRetention(t *testing.T) {
	for _, raisedErr := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "NoSuchObjectLockConfiguration"}, &smithy.GenericAPIError{Code: "InvalidRequest"}} {
		ctx, stubber, actor := enterTest()
		_, stubErr := wrapErr(raisedErr)
		stubber.Add(stubs.StubGetObjectRetention("amzn-s3-demo-bucket", "test-key", types.ObjectLockRetentionModeGovernance, time.Now(), stubErr))
		_, actualErr := actor.GetObjectRetention(ctx, "amzn-s3-demo-bucket", "test-key")
		expectedErr := raisedErr
		if _, ok := raisedErr.(*smithy.GenericAPIError); ok {
			expectedErr = nil
		}
		verifyErr(expectedErr, actualErr, t)
		testtools.ExitTest(stubber, t)
	}
}

func TestPutObjectLegalHold(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchKey{})
	stubber.Add(stubs.StubPutObjectLegalHold("amzn-s3-demo-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn, stubErr))
	actualErr := actor.PutObjectLegalHold(ctx, "amzn-s3-demo-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn)
	verifyErr(expectedErr, actualErr, t)
}

func TestModifyDefaultBucketRetention(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchBucket{})
	stubber.Add(stubs.StubPutObjectLockConfiguration("amzn-s3-demo-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance, stubErr))
	actualErr := actor.ModifyDefaultBucketRetention(ctx, "amzn-s3-demo-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance)
	verifyErr(expectedErr, actualErr, t)
}

func TestEnableObjectLockOnBucket(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchBucket{})
	stubber.Add(stubs.StubPutBucketVersioning("amzn-s3-demo-bucket", stubErr))
	actualErr := actor.EnableObjectLockOnBucket(ctx, "amzn-s3-demo-bucket")
	verifyErr(expectedErr, actualErr, t)

	expectedErr, stubErr = wrapErr(&types.NoSuchBucket{})
	stubber.Add(stubs.StubPutBucketVersioning("amzn-s3-demo-bucket", nil))
	stubber.Add(stubs.StubPutObjectLockConfiguration("amzn-s3-demo-bucket", types.ObjectLockEnabledEnabled, 0, types.ObjectLockRetentionModeGovernance, stubErr))
	actualErr = actor.EnableObjectLockOnBucket(ctx, "amzn-s3-demo-bucket")
	verifyErr(expectedErr, actualErr, t)
}

func TestPutObjectRetention(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchKey{})
	stubber.Add(stubs.StubPutObjectRetention("amzn-s3-demo-bucket", "test-key", stubErr))
	actualErr := actor.PutObjectRetention(ctx, "amzn-s3-demo-bucket", "test-key", types.ObjectLockRetentionModeGovernance, 30)
	verifyErr(expectedErr, actualErr, t)
}

func TestUploadObject(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	actor.S3Manager = manager.NewUploader(actor.S3Client)
	expectedErr, stubErr := wrapErr(&types.NoSuchBucket{})
	checksum := types.ChecksumAlgorithmSha256
	stubber.Add(stubs.StubPutObject("amzn-s3-demo-bucket", "test-key", &checksum, stubErr))
	_, actualErr := actor.UploadObject(ctx, "amzn-s3-demo-bucket", "test-key", "test-contents")
	verifyErr(expectedErr, actualErr, t)
}

func TestListObjectVersions(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchBucket{})
	stubber.Add(stubs.StubListObjectVersions("amzn-s3-demo-bucket", []types.ObjectVersion{}, stubErr))
	_, actualErr := actor.ListObjectVersions(ctx, "amzn-s3-demo-bucket")
	verifyErr(expectedErr, actualErr, t)
}

func TestDeleteObject(t *testing.T) {
	for _, raisedErr := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "AccessDenied"}, &smithy.GenericAPIError{Code: "InvalidArgument"}} {
		ctx, stubber, actor := enterTest()
		_, stubErr := wrapErr(raisedErr)
		stubber.Add(stubs.StubDeleteObject("amzn-s3-demo-bucket", "test-key", "test-version", true, stubErr))
		stubber.Add(stubs.StubHeadObject("amzn-s3-demo-bucket", "test-key", &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
		_, actualErr := actor.DeleteObject(ctx, "amzn-s3-demo-bucket", "test-key", "test-version", true)
		expectedErr := raisedErr
		if _, ok := raisedErr.(*smithy.GenericAPIError); ok {
			expectedErr = nil
		}
		verifyErr(expectedErr, actualErr, t)
		testtools.ExitTest(stubber, t)
	}
}

func TestDeleteObjects(t *testing.T) {
	ctx, stubber, actor := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.NoSuchBucket{})
	stubber.Add(stubs.StubDeleteObjects("amzn-s3-demo-bucket", []types.ObjectVersion{{Key: aws.String("test-key"), VersionId: aws.String("test-version")}}, true, stubErr))
	stubber.Add(stubs.StubHeadObject("amzn-s3-demo-bucket", "test-key", &testtools.StubError{Err: &types.NotFound{}, ContinueAfter: true}))
	actualErr := actor.DeleteObjects(ctx, "amzn-s3-demo-bucket", []types.ObjectIdentifier{{Key: aws.String("test-key"), VersionId: aws.String("test-version")}}, true)
	verifyErr(expectedErr, actualErr, t)
}
