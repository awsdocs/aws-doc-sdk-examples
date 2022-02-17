// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[glacier.go.upload_archive]
package main

// snippet-start:[glacier.go.upload_archive.imports]
import (
    "bytes"
    "flag"
    "fmt"
    "os"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/glacier"
    "github.com/aws/aws-sdk-go/service/glacier/glacieriface"
)
// snippet-end:[glacier.go.upload_archive.imports]

// ArchiveFile archives a file in an Amazon Simple Storage Service Glacier (Amazon S3 Glacier) vault.
// Inputs:
//     svc is an Amazon S3 Glacier service client
//     vaultName is the name of the vault
//     fileName is the name of the file to upload
// Output:
//     If success, information about the archive and nil
//     Otherwise, nil and an error from the call to Open or UploadArchive
func ArchiveFile(svc glacieriface.GlacierAPI, vaultName, fileName *string) (*glacier.ArchiveCreationOutput, error) {
    // snippet-start:[glacier.go.upload_archive.open]
    file, err := os.Open(*fileName)
    // snippet-end:[glacier.go.upload_archive.open]
    if err != nil {
        return nil, err
    }

    // snippet-start:[glacier.go.upload_archive.read]
    defer file.Close()

    fileInfo, _ := file.Stat()
    var size int64 = fileInfo.Size()

    buffer := make([]byte, size)

    // read file content to buffer
    _, err = file.Read(buffer)
    // snippet-end:[glacier.go.upload_archive.read]
    if err != nil {
        return nil, err
    }

    // snippet-start:[glacier.go.upload_archive.call]
    fileBytes := bytes.NewReader(buffer)

    result, err := svc.UploadArchive(&glacier.UploadArchiveInput{
        AccountId: aws.String("-"), // use current account
        VaultName: vaultName,
        Body:      fileBytes,
    })
    // snippet-end:[glacier.go.upload_archive.call]
    if err != nil {
        return nil, err
    }

    return result, nil
}

func main() {
    // snippet-start:[glacier.go.upload_archive.args]
    vaultName := flag.String("v", "", "The name of the vault")
    fileName := flag.String("f", "", "The name of the file to archive")
    flag.Parse()

    if *vaultName == "" || *fileName == "" {
        fmt.Println("You must supply a vault name and file name")
        fmt.Println("-v VAULT-NAME -f FILE-NAME")
        return
    }
    // snippet-end:[glacier.go.upload_archive.args]

    // snippet-start:[glacier.go.upload_archive.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := glacier.New(sess)
    // snippet-end:[glacier.go.upload_archive.session]

    result, err := ArchiveFile(svc, vaultName, fileName)
    if err != nil {
        fmt.Println("Got an error archiving " + *fileName)
        return
    }

    // snippet-start:[glacier.go.upload_archive.display]
    fmt.Println("Uploaded to archive with ID " + *result.ArchiveId)
    // snippet-end:[glacier.go.upload_archive.display]
}
// snippet-end:[glacier.go.upload_archive]
