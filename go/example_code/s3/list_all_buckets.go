    svc := s3.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    result, err := svc.ListBuckets(&s3.ListBucketsInput{})
    if err != nil {
        log.Println("Failed to list buckets", err)
        return
    }

    log.Println("Buckets:")
    for _, bucket := range result.Buckets {
        log.Printf("%s : %s\n", aws.StringValue(bucket.Name), bucket.CreationDate)
    }
