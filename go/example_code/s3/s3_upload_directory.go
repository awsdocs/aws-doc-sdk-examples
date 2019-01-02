package main

import (
	"fmt"
	"os"
	"path/filepath"

	"github.com/aws/aws-sdk-go/aws"
	"github.com/aws/aws-sdk-go/aws/session"
	"github.com/aws/aws-sdk-go/service/s3/s3manager"
)

const exitError = 1

func main() {
	if len(os.Args) != 4 {
		exitErrorf("region, bucket and directory path required\nUsage: %s region bucket_name path",
			os.Args[0])
	}

	region := os.Args[1]
	bucket := os.Args[2]
	path := os.Args[3]
	di := NewDirectoryIterator(bucket, path)

	sess, err := session.NewSession(&aws.Config{
		Region: aws.String(region)},
	)
	if err != nil {
		exitErrorf("failed to create a session %q, %v", err)
	}

	uploader := s3manager.NewUploader(sess)

	if err := uploader.UploadWithIterator(aws.BackgroundContext(), di); err != nil {
		exitErrorf("failed to upload %q, %v", err)
	}
	fmt.Printf("successfully uploaded %q to %q", path, bucket)
}

// DirectoryIterator represents an iterator of a specified directory
type DirectoryIterator struct {
	filePaths []string
	bucket    string
	next      struct {
		path string
		f    *os.File
	}
	err error
}

// NewDirectoryIterator builds a new DirectoryIterator
func NewDirectoryIterator(bucket, dir string) s3manager.BatchUploadIterator {
	var paths []string
	filepath.Walk(dir, func(path string, info os.FileInfo, err error) error {
		if !info.IsDir() {
			paths = append(paths, path)
		}
		return nil
	})

	return &DirectoryIterator{
		filePaths: paths,
		bucket:    bucket,
	}
}

// Next returns whether next file exists or not
func (di *DirectoryIterator) Next() bool {
	if len(di.filePaths) == 0 {
		di.next.f = nil
		return false
	}

	f, err := os.Open(di.filePaths[0])
	di.err = err
	di.next.f = f
	di.next.path = di.filePaths[0]
	di.filePaths = di.filePaths[1:]

	return true && di.Err() == nil
}

// Err returns error of DirectoryIterator
func (di *DirectoryIterator) Err() error {
	return di.err
}

// UploadObject uploads a file
func (di *DirectoryIterator) UploadObject() s3manager.BatchUploadObject {
	f := di.next.f
	return s3manager.BatchUploadObject{
		Object: &s3manager.UploadInput{
			Bucket: &di.bucket,
			Key:    &di.next.path,
			Body:   f,
		},
		After: func() error {
			return f.Close()
		},
	}
}

func exitErrorf(msg string, args ...interface{}) {
	fmt.Fprintf(os.Stderr, msg+"\n", args...)
	os.Exit(exitError)
}
