// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package demotools

import (
	"fmt"
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
func (mock *MockQuestioner) Next(question string) string {
	if mock.answerIndex < len(mock.Answers) {
		answer := mock.Answers[mock.answerIndex]
		mock.answerIndex++
		return answer
	} else {
		panic(fmt.Sprintf("No mock answer to question: %v", question))
	}
}

func (mock *MockQuestioner) Ask(question string, validators ...IAnswerValidator) string {
	return mock.Next(question)
}

func (mock *MockQuestioner) AskBool(question string, expected string) bool {
	return mock.Next(question) == expected
}

func (mock *MockQuestioner) AskInt(question string, validators ...IAnswerValidator) int {
	answerInt, _ := strconv.Atoi(mock.Next(question))
	return answerInt
}

func (mock *MockQuestioner) AskFloat64(question string, validators ...IAnswerValidator) float64 {
	answerFloat, _ := strconv.ParseFloat(mock.Next(question), 64)
	return answerFloat
}
