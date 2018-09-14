/*
   Copyright 2010-2018 Amazon.com, Inc. or its affiliates. All Rights Reserved.

   This file is licensed under the Apache License, Version 2.0 (the "License").
   You may not use this file except in compliance with the License. A copy of
   the License is located at

    http://aws.amazon.com/apache2.0/

   This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
   CONDITIONS OF ANY KIND, either express or implied. See the License for the
   specific language governing permissions and limitations under the License.
*/

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
    
