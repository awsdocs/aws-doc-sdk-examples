// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package testtools

import (
	"github.com/awsdocs/aws-doc-sdk-examples/gov2/demotools"
	"strconv"
)

// MockQuestioner is a mock questioner that can be used to simulate user input during
// a unit test. Specify a slice of Answers that are returned in sequence during a
// test run.
type MockQuestioner struct {
	Answers     []string
	answerIndex int
}

// Next returns the next answer in the slice of answers.
func (mock *MockQuestioner) Next() string {
	if mock.answerIndex < len(mock.Answers) {
		answer := mock.Answers[mock.answerIndex]
		mock.answerIndex++
		return answer
	} else {
		panic("No more answers in the questioner mock!")
	}
}

func (mock *MockQuestioner) Ask(question string, validators []demotools.IAnswerValidator) string {
	return mock.Next()
}

func (mock *MockQuestioner) AskBool(question string, expected string) bool {
	return mock.Next() == expected
}

func (mock *MockQuestioner) AskInt(question string, validators []demotools.IAnswerValidator) int {
	answerInt, _ := strconv.Atoi(mock.Next())
	return answerInt
}

func (mock *MockQuestioner) AskFloat64(question string, validators []demotools.IAnswerValidator) float64 {
	answerFloat, _ := strconv.ParseFloat(mock.Next(), 64)
	return answerFloat
}
