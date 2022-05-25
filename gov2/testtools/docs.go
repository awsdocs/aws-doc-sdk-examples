// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package testtools provides a set of tools to help test code that calls AWS
// services.
//
// **AWS Middleware Stubber**
//
// The AWS Middleware Stubber is a unit testing tool that hooks into the AWS SDK for Go
// middleware (https://aws.github.io/aws-sdk-go-v2/docs/middleware/) to short-circuit
// calls to AWS services, verify inputs, and return predefined outputs. This
// improves unit testing because you don't have to define mocks or change the
// way your code calls AWS. Tests run without calling AWS, which means tests
// run faster and don't incur charges or risk impacting your resources.
//
// To use AwsmStubber, first create an instance of AwsmStubber.
//
//   stubber := testtools.NewStubber()
//
// The stubber is configured to handle all calls to AWS before the Serialize middleware
// step. Use the stubber config to create a service client.
//
//   client := dynamodb.NewFromConfig(*stubber.SdkConfig)
//
// Define and add all service actions that are called by your test.
//
//   stubber.Add(testtools.Stub{
//		OperationName: "GetItem",
//		Input: &dynamodb.GetItemInput{ TableName: aws.String(tableName), Key: key },
//		Output: &dynamodb.GetItemOutput{Item: map[string]types.AttributeValue{
//			"title": &types.AttributeValueMemberS{Value: title},
//			"year": &types.AttributeValueMemberN{Value: year},
//			"info": &types.AttributeValueMemberM{Value: map[string]types.AttributeValue{
//				"rating": &types.AttributeValueMemberN{Value: rating},
//				"plot": &types.AttributeValueMemberS{Value: plot},
//			}},
//		}},
//		Error: raiseErr,
//	}
//
// During your test run, the stubber verifies that each call is made in the order that
// stubs are added to the stubber. The stubber also checks actual input against expected
// input. If the call is verified, either the specified output is returned or, if an
// error is requested, the error is returned.
//
// Run your test and verify the results. Use testtools helper functions to verify
// errors and run exit code.
//
//   gotMovie, err := basics.GetMovie(movie.Title, movie.Year)
//
//   testtools.VerifyError(err, raiseErr, t)
//   if err == nil {
//     if gotMovie.Title != movie.Title || gotMovie.Year != movie.Year {
//       t.Errorf("got %s but expected %s", gotMovie, movie)
//     }
//   }
//
//   testtools.ExitTest(stubber, t)
//
// By using sub tests, you can use the same test code to test both error and non-error
// paths.
//
//   func TestTableBasics_GetMovie(t *testing.T) {
//     t.Run("NoErrors", func (t *testing.T) { GetMovie(nil, t) })
//     t.Run("TestError", func (t *testing.T) { GetMovie(&testtools.StubError{Err: errors.New("TestError")}, t)})
//   }
//
// The testtools.ExitTest helper verifies that all expected stubs were called during
// the test, so if your test exits early and leaves uncalled stubs, the test fails.
//
// **Framework**
//
// The framework section of the package provides a set of helper functions that you
// can use in your tests to perform common tasks, such as verifying that errors
// returned from the code under test match up with the expected errors, and running
// exit checks to verify all stubs were called.
//
// **Scenarios**
//
// The scenarios section of the package provides a set of helper functions that you
// can use to run scenario tests. Scenarios typically string together several
// actions in a narrative format. The scenario test functions let you define
// the expected actions of your scenario as a list of stubs. Then, your test function
// is called first with no errors, and subsequently with each stub set to return an error.
//
// **Mocks**
//
// The mocks section of the package provides mocks of components that are used in
// the code examples, such as a mock of the IQuestioner interface that lets you
// specify a list of expected answers. The mock questioner returns these answers
// in sequence during a test to mock user input.
package testtools
