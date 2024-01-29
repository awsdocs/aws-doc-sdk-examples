// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Unit tests for action not covered by scenarios.

package actions

import (
	"errors"
	"testing"

	"github.com/aws/aws-sdk-go-v2/service/s3"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/s3/stubs"
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
)

func enterTest() (*testtools.AwsmStubber, *BucketBasics) {
	stubber := testtools.NewStubber()
	basics := &BucketBasics{S3Client: s3.NewFromConfig(*stubber.SdkConfig)}
	return stubber, basics
}

func TestBucketBasics_CopyToBucket(t *testing.T) {
	t.Run("NoErrors", func(t *testing.T) { CopyToBucket(nil, t) })
	t.Run("TestError", func(t *testing.T) { CopyToBucket(&testtools.StubError{Err: errors.New("TestError")}, t) })
}

func CopyToBucket(raiseErr *testtools.StubError, t *testing.T) {
	stubber, basics := enterTest()
	stubber.Add(stubs.StubCopyObject("source-bucket", "object-key", "dest-bucket", "object-key", raiseErr))

	err := basics.CopyToBucket("source-bucket", "dest-bucket", "object-key")

	testtools.VerifyError(err, raiseErr, t)
	testtools.ExitTest(stubber, t)
}
