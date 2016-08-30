    svc := dynamodb.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    result, err := svc.ListTables(&dynamodb.ListTablesInput{})
    if err != nil {
        log.Println(err)
        return
    }

    log.Println("Tables:")
    for _, table := range result.TableNames {
        log.Println(*table)
    }