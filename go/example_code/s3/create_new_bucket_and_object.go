    bucket := "myBucket"
    key := "TestFile.txt"

    svc := s3.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    result, err := svc.CreateBucket(&s3.CreateBucketInput{
        Bucket: &bucket,
    })
    if err != nil {
        log.Println("Failed to create bucket", err)
        return
    }

    if err = svc.WaitUntilBucketExists(&s3.HeadBucketInput{Bucket: &bucket}); err != nil {
        log.Printf("Failed to wait for bucket to exist %s, %s\n", bucket, err)
        return
    }

    uploadResult, err = svc.PutObject(&s3.PutObjectInput{
        Body:   strings.NewReader("Hello World!"),
        Bucket: &bucket,
        Key:    &key,
    })
    if err != nil {
        log.Printf("Failed to upload data to %s/%s, %s\n", bucket, key, err)
        return
    }

    log.Printf("Successfully created bucket %s and uploaded data with key %s\n", bucket, key)