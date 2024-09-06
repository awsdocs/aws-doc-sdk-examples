// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package demotools

import (
	"os"
	"testing"
)

func TestNewStandardFileSystemGwd(t *testing.T) {
	filesystem := NewStandardFileSystem()
	testGwd, tErr := filesystem.Getwd()
	realGwd, rErr := os.Getwd()
	if testGwd != realGwd || tErr != nil || rErr != nil {
		t.Errorf("NewStandardFileSystemGwd(): got %v, want %v. Errors(%v, %v)", testGwd, realGwd, tErr, rErr)
	}
}

func TestNewStandardFileSystemFileIO(t *testing.T) {
	filesystem := NewStandardFileSystem()
	filename := "test.txt"
	file, _ := os.Create(filename)
	fsFile, err := filesystem.OpenFile(filename)
	if err != nil {
		t.Errorf("NewStandardFileSystemFileInteraction(): error opening file: %v", err)
	}
	filesystem.CloseFile(fsFile)
	file.Close()
	err = os.Remove(filename)
	if err != nil {
		t.Errorf("NewStandardFileSystemFileInteraction(): error removing file: %v", err)
	}
}

func TestNewMockFileSystem(t *testing.T) {
	filename := "test.txt"
	file, err := os.Create(filename)
	if err != nil {
		t.Errorf("NewMockFileSystem(): error creating file: %v", err)
	}
	filesystem := NewMockFileSystem(file)
	mockGwd, gwdErr := filesystem.Getwd()
	if mockGwd != "mock/dir" || gwdErr != nil {
		t.Errorf("NewMockFileSystem(): %v is not correct or the error was: %v", mockGwd, gwdErr)
	}
	mockFile, fErr := filesystem.OpenFile("any string will do")
	if fErr != nil {
		t.Errorf("NewMockFileSystem(): error opening file: %v", fErr)
	}
	filesystem.CloseFile(mockFile)
	file.Close()
	err = os.Remove(filename)
	if err != nil {
		t.Errorf("NewMockFileSystem(): error removing file: %v", err)
	}
}
