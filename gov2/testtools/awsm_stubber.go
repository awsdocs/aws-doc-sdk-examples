// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package testtools

import (
	"context"
	"encoding/json"
	"fmt"
	"log"
	"reflect"

	"github.com/aws/aws-sdk-go-v2/aws"
	sdkmiddleware "github.com/aws/aws-sdk-go-v2/aws/middleware"
	"github.com/aws/aws-sdk-go-v2/config"
	"github.com/aws/smithy-go/middleware"
)

// StubError contains an error that is raised by a stub. ContinueAfter specifies
// whether to continue the test after the error.
type StubError struct {
	Err error
	ContinueAfter bool
}

// Error returns the contained error string.
func (stubErr StubError) Error() string {
	return stubErr.Err.Error()
}

// Stub defines a service operation, expected inputs, and outputs for a stubbed service
// action. When an error is specified, it is returned from the stubbed action.
// SkipErrorTest is used to skip automated error testing when a stub should not
// raise an error.
type Stub struct {
	OperationName string
	Input interface{}
	Output interface{}
	SkipErrorTest bool
	IgnoreFields []string
	Error *StubError
}

func isIgnorable(name string, ignorables [] string) bool {
	for _, ig := range ignorables {
		if name == ig {
			return true
		}
	}
	return false
}

func getComparableFields(x interface{}, ignorables []string) []string {
	t := reflect.TypeOf(x)
	if t.Kind() == reflect.Ptr {
		t = t.Elem()
	}
	var fields []string
	for i := 0; i < t.NumField(); i++ {
		name := t.Field(i).Name
		if t.Field(i).IsExported() && !isIgnorable(name, ignorables) {
			fields = append(fields, name)
		}
	}
	return fields
}

func getComparableValues(x interface{}, fields []string) []interface{} {
	var valDeref reflect.Value
	val := reflect.ValueOf(x)
	if val.Kind() == reflect.Ptr {
		valDeref = val.Elem()
	} else {
		valDeref = val
	}
	var vals []interface{}
	for _, field := range fields {
		vals = append(vals, valDeref.FieldByName(field).Interface())
	}
	return vals
}

// Compare compares the actual input to a service action against the expected input
// specified in the stub. If they are not equal according to reflect.DeepEqual, an
// error is returned.
func (stub Stub) Compare(actual interface{}) error {
	comparableFields := getComparableFields(stub.Input, stub.IgnoreFields)
	actualValues := getComparableValues(actual, comparableFields)
	stubValues := getComparableValues(stub.Input, comparableFields)

	var err error
	for i := 0; i < len(actualValues); i++ {
		actualValue := actualValues[i]
		stubValue := stubValues[i]
		if !reflect.DeepEqual(actualValue, stubValue) {
			parentType := reflect.TypeOf(actual)
			actType := reflect.TypeOf(actualValue)
			act, _ := json.MarshalIndent(actualValue, "", "  ")
			expType := reflect.TypeOf(stubValue)
			exp, _ := json.MarshalIndent(stubValue, "", "  ")
			err = fmt.Errorf("\n**** Mismatched inputs for%v.%v****\nGot:\n%s\n%s\nExpected:\n%s\n%s",
				parentType, comparableFields[i], actType, act, expType, exp)
			break
		}
	}
	return err
}

// AwsmStubber creates a config that has a function inserted into the AWS SDK for Go
// middleware chain. The stubber expects to be called once for each stub in the
// contained slice of stubs.
type AwsmStubber struct {
	SdkConfig *aws.Config
	stubs []Stub
	callIndex int
}

// NewStubber returns a new stubber that inserts a function before the Serialize step
// in the middleware chain. The function intercepts service actions and returns
// stubbed data instead of calling the actual AWS service.
func NewStubber() *AwsmStubber {
	cfg, err := config.LoadDefaultConfig(context.TODO())
	stubber := AwsmStubber{SdkConfig: &cfg}
	if err != nil {
		log.Fatalf("unable to load SDK config, %v", err)
	}
	stubFunc := middleware.SerializeMiddlewareFunc("AwsmStubber", stubber.MiddlewareStub)
	cfg.APIOptions = append(cfg.APIOptions, func(stack *middleware.Stack) error {
		return stack.Serialize.Add(stubFunc, middleware.Before)
	})
	return &stubber
}

// Add adds a stub to the end of the slice of stubs.
func (stubber *AwsmStubber) Add(stub Stub) {
	stubber.stubs = append(stubber.stubs, stub)
}

// Next returns the next stub in the slice of stubs.
func (stubber *AwsmStubber) Next() *Stub {
	if stubber.callIndex < len(stubber.stubs) {
		stub := stubber.stubs[stubber.callIndex]
		stubber.callIndex += 1
		return &stub
	} else {
		return nil
	}
}

// Clear removes all stubs from the stubber.
func (stubber *AwsmStubber) Clear() {
	stubber.stubs = nil
}

// VerifyAllStubsCalled returns an error if there are stubs in the slice that were not
// called. In this way, you can verify that your test made all the calls that you expected.
func (stubber *AwsmStubber) VerifyAllStubsCalled() error {
	var err error
	next := stubber.Next()
	if next != nil {
		err = fmt.Errorf("Remaining stub %v was never called.", next.OperationName)
	}
	return err
}

// MiddlewareStub is a middleware function that is inserted before the Serialize step
// in the middleware chain.
//
// It gets the next stub in the slice and compares its
// expected operation with the actual service operation. If they don't match, or if
// there are no more stubs in the slice, it returns an error.
//
// If the operations match, the actual input is compared against the expected input.
// If they don't match, it returns an error.
//
// If an error is specified on the stub, the error is returned. If ContinueAfter is
// false, the rest of the stubs are cleared from the stubber. By using this setting,
// you can write tests that stop on an error or that handle errors and continue
// processing.
//
// If the actual operation and input matches the expected operation and input, the
// function serializes the stubbed output and returns it. This creates a short circuit
// in the middleware chain so that no requests are made to AWS.
func (stubber *AwsmStubber) MiddlewareStub(ctx context.Context, in middleware.SerializeInput, next middleware.SerializeHandler) (
	out middleware.SerializeOutput, metadata middleware.Metadata, err error) {
	serOut := middleware.SerializeOutput{}
	gotName := sdkmiddleware.GetOperationName(ctx)

	stub := stubber.Next()
	if stub == nil {
		err = fmt.Errorf("got '%v', but had no more stubs in the queue", gotName)
	} else {
		if stub.Error != nil && stub.Error.Err != nil {
			err = stub.Error.Err
			if !stub.Error.ContinueAfter {
				stubber.Clear()
			}
		} else {
			if stub.OperationName != gotName {
				err = fmt.Errorf("expected operation '%v', got '%v'", stub.OperationName, gotName)
			} else {
				err = stub.Compare(in.Parameters)
			}
			serOut.Result = stub.Output
		}
	}
	return serOut, metadata, err
}
