    file, err := os.Create("download_file")
    if err != nil {
        log.Fatal("Failed to create file", err)
    }
    defer file.Close()

    downloader := s3manager.NewDownloader(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    numBytes, err := downloader.Download(file,
        &s3.GetObjectInput{
            Bucket: aws.String("myBucket"),
            Key:    aws.String("myKey"),
        })
    if err != nil {
        fmt.Println("Failed to download file", err)
        return
    }

    fmt.Println("Downloaded file", file.Name(), numBytes, "bytes")