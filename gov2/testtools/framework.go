// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package testtools

import (
	"errors"
	"reflect"
	"testing"

	"github.com/aws/smithy-go"
)

// ExitTest is a helper function that verifies that all calls specified in the stubber
// were called by your test.
func ExitTest(stubber *AwsmStubber, t *testing.T) {
	err := stubber.VerifyAllStubsCalled()
	if err != nil {
		t.Error(err)
	}
}

// containsErrorType checks, by type, whether an error is in a list of error types.
func containsErrorType(check error, errList []interface{}) bool {
	for _, err := range errList {
		if reflect.TypeOf(check) == reflect.TypeOf(err) {
			return true
		}
	}
	return false
}

// VerifyError is a helper function that verifies actual errors against expected errors.
// You can call it immediately after you call the code you're testing to verify actual
// errors against expected errors.
//
// If an error is raised but none are expected, the test fails.
//
// If no error is raised but one is expected, the test fails.
//
// If an error is expected to be raised by the stubber and handled by the code under
// test, but the test returns an error, the test fails.
//
// If an error is raised of a different type than the expected error, the test fails.
//
// If an error other than a smithy.OperationError is raised, the test fails.
//
// Otherwise, the test passes.
func VerifyError(actual error, raised *StubError, t *testing.T, handled ...interface{}) {
	if actual != nil {
		if raised == nil {
			t.Fatalf("Got error %v but expected no error.", actual)
		} else {
			var opErr *smithy.OperationError
			if errors.As(actual, &opErr) {
				if opErr.Err.Error() != raised.Error() {
					t.Fatalf("Failed with %v but expected %v.", opErr, raised)
				}
			} else {
				t.Fatalf("Got unexpected error type %v.", actual)
			}
		}
	} else if raised != nil && !containsErrorType(raised.Err, handled) {
		t.Fatalf("Raised unhandled error %v but got no actual error.", raised)
	}
}
