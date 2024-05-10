// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package demotools

import (
	"bufio"
	"strconv"
	"testing"
)

type mockReader struct {
	data []byte
}

func (mock mockReader) Read(p []byte) (int, error) {
	copy(p, mock.data)
	return len(p), nil
}

func TestQuestioner_AskEmpty(t *testing.T) {
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte("\n")})}
	questioner.Ask("Answer?")
}

func TestQuestioner_AskNonEmpty(t *testing.T) {
	expected := "test"
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte(expected + "\n")})}
	actual := questioner.Ask("Answer?", NotEmpty{})
	if actual != expected {
		t.Errorf("Expected %v, got %v\n", expected, actual)
	}
}

func TestQuestioner_AskTrue(t *testing.T) {
	expected := "y"
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte(expected + "\n")})}
	actual := questioner.AskBool("Bool?", "y")
	if !actual {
		t.Errorf("Expected true, got %v\n", actual)
	}
}

func TestQuestioner_AskFalse(t *testing.T) {
	expected := "y"
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte(expected + "\n")})}
	actual := questioner.AskBool("Bool?", "n")
	if actual {
		t.Errorf("Expected false, got %v\n", actual)
	}
}

func AskBoolWithDefault_ByArguments(t *testing.T, expected string, defaultValue string, entry string, success bool) {
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte(entry + "\n")})}
	actual := questioner.AskBoolWithDefault("Bool?", expected, defaultValue)
	if actual != success {
		t.Errorf("Expected %v, got %v\n", success, actual)
	}
}

func TestQuestioner_AskBoolWithDefault_yY_Blank(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "Y", "", true)
}

func TestQuestioner_AskBoolWithDefault_yN_Blank(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "N", "", false)
}
func TestQuestioner_AskBoolWithDefault_nY_Blank(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "Y", "", false)
}
func TestQuestioner_AskBoolWithDefault_nN_Blank(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "N", "", true)
}

func TestQuestioner_AskBoolWithDefault_yY_y(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "Y", "y", true)
}
func TestQuestioner_AskBoolWithDefault_yN_y(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "N", "y", true)
}
func TestQuestioner_AskBoolWithDefault_nY_y(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "Y", "y", false)
}
func TestQuestioner_AskBoolWithDefault_nN_y(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "N", "y", false)
}

func TestQuestioner_AskBoolWithDefault_yY_n(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "Y", "n", false)
}
func TestQuestioner_AskBoolWithDefault_yN_n(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "y", "N", "n", false)
}
func TestQuestioner_AskBoolWithDefault_nY_n(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "Y", "n", true)
}
func TestQuestioner_AskBoolWithDefault_nN_n(t *testing.T) {
	AskBoolWithDefault_ByArguments(t, "n", "N", "n", true)
}

func TestQuestioner_AskBoolWithDefaultWithoutMatch(t *testing.T) {
	expected := "y"
	defaultValue := "N"
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte("\n")})}
	actual := questioner.AskBoolWithDefault("Bool?", expected, defaultValue)
	if actual {
		t.Errorf("Expected false, got %v\n", actual)
	}
}

func TestQuestioner_AskChoice(t *testing.T) {
	expected := 2
	questioner := Questioner{reader: bufio.NewReader(mockReader{data: []byte(strconv.Itoa(expected) + "\n")})}
	actual := questioner.AskChoice("Choice?\n", []string{"test1", "test2", "test3"})
	if actual != expected-1 {
		t.Errorf("Expected %v, got %v\n", expected, actual)
	}
}

func TestQuestioner_AskInt(t *testing.T) {
	s := "test"
	out := 33
	in := 14
	questioner := Questioner{reader: bufio.NewReader(mockReader{
		data: []byte(s + "\n" + strconv.Itoa(out) + "\n" + strconv.Itoa(in) + "\n")})}
	actual := questioner.AskInt("Int?", InIntRange{Lower: 5, Upper: 20})
	if actual != in {
		t.Errorf("Expected %v, got %v\n", in, actual)
	}
}

func TestQuestioner_AskFloat64(t *testing.T) {
	s := "test"
	out := "33.33"
	in := "14.25"
	inFloat := 14.25
	questioner := Questioner{reader: bufio.NewReader(mockReader{
		data: []byte(s + "\n" + out + "\n" + in + "\n")})}
	actual := questioner.AskFloat64("Int?", InFloatRange{Lower: 5.6, Upper: 16.78})
	if actual != inFloat {
		t.Errorf("Expected %v, got %v\n", inFloat, actual)
	}
}
