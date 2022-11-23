// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package testtools

import (
	"bytes"
	"errors"
	"fmt"
	"log"
	"os"
	"strings"
	"testing"
)

// IScenarioTest defines callback functions that an individual scenario test must
// implement.
//
// SetupDataAndStubs sets up test data specific to the test and returns a list of stubs
// that specify the expected calls that are made during a scenario run.
//
// RunSubTest runs the actual code under test.
type IScenarioTest interface {
	SetupDataAndStubs() []Stub
	RunSubTest(stubber *AwsmStubber)
	Cleanup()
}

// RunScenarioTests runs the scenario multiple times. The first time, it
// runs with no errors. In subsequent runs, it specifies that each stub in the sequence
// should raise an error, and it verifies the results.
func RunScenarioTests(scenarioTest IScenarioTest, t *testing.T) {
	done := false
	stubIndex := -1
	for !done {
		stubs := scenarioTest.SetupDataAndStubs()
		if stubIndex == -1 {
			t.Run("NoErrors", func(t *testing.T) { SubTestRunScenario(scenarioTest, stubs, nil, t) })
		} else {
			stub := &stubs[stubIndex]
			if stub.Error == nil && !stub.SkipErrorTest {
				errName := fmt.Sprintf("%vError", stub.OperationName)
				stub.Error = &StubError{Err: errors.New(errName)}
				t.Run(errName, func(t *testing.T) { SubTestRunScenario(scenarioTest, stubs, stub.Error, t) })
			}
		}
		stubIndex++
		done = stubIndex == len(stubs)
	}
}

// SubTestRunScenario performs a single test run with a set of stubs that are set up to
// run with or without errors.
func SubTestRunScenario(scenarioTest IScenarioTest, stubs []Stub, stubErr *StubError, t *testing.T) {
	stubber := NewStubber()
	for _, stub := range stubs {
		stubber.Add(stub)
	}

	log.SetFlags(0)
	var buf bytes.Buffer
	log.SetOutput(&buf)

	scenarioTest.RunSubTest(stubber)

	log.SetOutput(os.Stderr)
	if stubErr == nil {
		if !strings.Contains(buf.String(), "Thanks for watching") {
			t.Errorf("didn't run to successful completion")
			t.Logf("Here's the log: \n%v", buf.String())
		} else if strings.Contains(buf.String(), "operation error") {
			t.Errorf("got an unexpected error")
			t.Logf("Here's the log: \n%v", buf.String())
		}
	} else {
		if !strings.Contains(buf.String(), stubErr.Error()) {
			t.Errorf("did not get expected error %v", stubErr)
			t.Logf("Here's the log: \n%v", buf.String())
		}
	}

	scenarioTest.Cleanup()
}
