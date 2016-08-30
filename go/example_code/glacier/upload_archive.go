    vaultName := "YOUR_VAULT_NAME"

    svc := glacier.New(session.New(&aws.Config{Region: aws.String("us-west-2")}))
    result, err := svc.UploadArchive(&glacier.UploadArchiveInput{
        AccountId: aws.String("-"),
        VaultName: &vaultName,
        Body:      bytes.NewReader(make([]byte, 2*1024*1024)), // 2 MB buffer
    })
    if err != nil {
        log.Println("Error uploading archive.", err)
        return
    }

    log.Println("Uploaded to archive", *result.ArchiveId)
    