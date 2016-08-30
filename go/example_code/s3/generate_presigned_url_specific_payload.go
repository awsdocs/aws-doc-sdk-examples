    svc := s3.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    req, _ := svc.PutObjectRequest(&s3.PutObjectInput{
        Bucket: aws.String("myBucket"),
        Key:    aws.String("myKey"),
        Body:   strings.NewReader("EXPECTED CONTENTS"),
    })
    str, err := req.Presign(15 * time.Minute)

    log.Println("The URL is:", str, " err:", err)