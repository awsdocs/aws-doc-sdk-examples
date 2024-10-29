// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for action not covered by scenarios.

package actions

import (
	"context"
	"errors"
	"reflect"
	"testing"

	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/aws/aws-sdk-go-v2/service/s3/types"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func enterTest() (context.Context, *testtools.AwsmStubber, *BucketBasics) {
	stubber := testtools.NewStubber()
	basics := &BucketBasics{S3Client: s3.NewFromConfig(*stubber.SdkConfig)}
	return context.Background(), stubber, basics
}

func wrapErr(expectedErr error) (error, *testtools.StubError) {
	return expectedErr, &testtools.StubError{Err: expectedErr}
}

func verifyErr(expectedErr error, actualErr error, t *testing.T) {
	if reflect.TypeOf(expectedErr) != reflect.TypeOf(actualErr) {
		t.Errorf("Expected error %T, got %T", expectedErr, actualErr)
	}
}

func TestBucketBasics_CopyToBucket(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { CopyToBucket(nil, t) })
	t.Run("TestError", func(t *testing.T) { CopyToBucket(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func CopyToBucket(raiseErr *testtools.StubError, t *testing.T) {
	ctx, stubber, basics := enterTest()
	defer testtools.ExitTest(stubber, t)

	expectedErr, stubErr := wrapErr(&types.ObjectNotInActiveTierError{})
	stubber.Add(stubs.StubCopyObject("amzn-s3-demo-bucket-source", "object-key", "amzn-s3-demo-bucket-dest", "object-key", stubErr))
	stubber.Add(stubs.StubHeadObject("amzn-s3-demo-bucket-source", "object-key", raiseErr))

	actualErr := basics.CopyToBucket(ctx, "amzn-s3-demo-bucket-source", "amzn-s3-demo-bucket-dest", "object-key")
	verifyErr(expectedErr, actualErr, t)
}
