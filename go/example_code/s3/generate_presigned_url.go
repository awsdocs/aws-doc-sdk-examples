    svc := s3.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    req, _ := svc.GetObjectRequest(&s3.GetObjectInput{
        Bucket: aws.String("myBucket"),
        Key:    aws.String("myKey"),
    })
    urlStr, err := req.Presign(15 * time.Minute)

    if err != nil {
        log.Println("Failed to sign request", err)
    }

    log.Println("The URL is", urlStr)