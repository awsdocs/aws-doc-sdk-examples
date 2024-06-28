// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

// Package demotools provides a set of tools that help you write code examples.
//
// **FileSystem**
//
// The filesystem is used to abstract the os module and allow for
// variations to be swapped out, such as mocks for testing.
package demotools

import (
	"io"
	"os"
)

// IFileSystem defines an interface that provides basic file i/o actions.
type IFileSystem interface {
	Getwd() (string, error)
	OpenFile(filename string) (io.ReadWriteCloser, error)
	CloseFile(file io.ReadWriteCloser)
}

// FileSystem is a struct to hold the injected object.
type FileSystem struct{}

// NewStandardFileSystem uses the plain os module implementations.
func NewStandardFileSystem() *FileSystem {
	return &FileSystem{}
}

// Getwd returns the current working directory using the os module.
func (filesystem FileSystem) Getwd() (string, error) {
	return os.Getwd()
}

// OpenFile returns an io.ReadWriterCloser object based on the provided filename.
func (filesystem FileSystem) OpenFile(filename string) (io.ReadWriteCloser, error) {
	file, err := os.Open(filename)
	return file, err
}

// CloseFile closes the provided file.
func (filesystem FileSystem) CloseFile(file io.ReadWriteCloser) {
	file.Close()
}

// MockFileSystem is a mock version of IFileSystem for testing.
type MockFileSystem struct {
	mockfile io.ReadWriteCloser
}

// NewMockFileSystem mocks the FileSystem and holds a singular io.ReadWriteCloser object to return.
func NewMockFileSystem(mockfile io.ReadWriteCloser) *MockFileSystem {
	mockfilesystem := MockFileSystem{}
	mockfilesystem.mockfile = mockfile
	return &mockfilesystem
}

// Getwd returns a basic mock directory string.
func (filesystem MockFileSystem) Getwd() (string, error) {
	return "mock/dir", nil
}

// OpenFile returns the io.ReadWriteCloser provided on object creation.
func (filesystem MockFileSystem) OpenFile(_ string) (io.ReadWriteCloser, error) {
	return filesystem.mockfile, nil
}

// CloseFile closes the io.ReadWriteCloser provided on object creation.
func (filesystem MockFileSystem) CloseFile(_ io.ReadWriteCloser) {
	filesystem.mockfile.Close()
}
