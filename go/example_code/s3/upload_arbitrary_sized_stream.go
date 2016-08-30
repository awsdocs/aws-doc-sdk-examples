    package main

    import (
        "compress/gzip"
        "io"
        "log"
        "os"

        "github.com/aws/aws-sdk-go/aws"
        "github.com/aws/aws-sdk-go/aws/session"
        "github.com/aws/aws-sdk-go/service/s3/s3manager"
    )

    func main() {
        file, err := os.Open("upload_file")
        if err != nil {
            log.Fatal("Failed to open file", err)
        }

        // Not required, but you could zip the file before uploading it
        // using io.Pipe read/writer to stream gzip'd file contents.
        reader, writer := io.Pipe()
        go func() {
            gw := gzip.NewWriter(writer)
            io.Copy(gw, file)

            file.Close()
            gw.Close()
            writer.Close()
        }()
        uploader := s3manager.NewUploader(session.New(&aws.Config{Region: aws.String("us-west-2")}))
        result, err := uploader.Upload(&s3manager.UploadInput{
            Body:   reader,
            Bucket: aws.String("myBucket"),
            Key:    aws.String("myKey"),
        })
        if err != nil {
            log.Fatalln("Failed to upload", err)
        }

        log.Println("Successfully uploaded to", result.Location)
    }