    svc := glacier.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    _, err := svc.CreateVault(&glacier.CreateVaultInput{
        VaultName: aws.String("YOUR_VAULT_NAME"),
    })
    if err != nil {
        log.Println(err)
        return
    }
    log.Println("Created vault!")