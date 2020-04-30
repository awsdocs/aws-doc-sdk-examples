// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
// snippet-start:[rds.go.copy_snapshot_to_s3]
package main

// snippet-start:[rds.go.copy_snapshot_to_s3.imports]
import (
    "flag"
    "fmt"
    "strings"
    "time"

    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/rds"
    "github.com/aws/aws-sdk-go/service/rds/rdsiface"
)
// snippet-end:[rds.go.copy_snapshot_to_s3.imports]

// StoreInstance save an Amazon RDS instance snapshot to an Amazon S3 bucket.
// Inputs:
//     sess is the current session, which provides configuration for the SDK's service clients
//     rdsSnapshotName is the name of the snapshot
//     exportRDSSnapshotName is the name given to the stored instance snapshot in the bucket
//     roleArn is the ARN of a role that has permission to write to the bucket
//     kmsKey is the key used to encrypt the snapshot
//     bucketName is the name of the bucket
// Output:
//     If success, the SOMETHING of the RESOURCE and nil
//     Otherwise, an empty string and an error from the call to FUNCTION
func StoreInstance(svc rdsiface.RDSAPI, rdsSnapshotName, exportRDSSnapshotName, roleArn, kmsKey, bucketName *string) (*rds.StartExportTaskOutput, error) {
    // snippet-start:[rds.go.copy_snapshot_to_s3.get_snapshots]
    result, err := svc.DescribeDBSnapshots(nil)
    // snippet-end:[rds.go.copy_snapshot_to_s3.get_snapshots]
    if err != nil {
        return nil, err
    }

    // snippet-start:[rds.go.copy_snapshot_to_s3.call]
    currentTime := time.Now()
    snapshotTime := currentTime.Format("2006-01-02")
    rdsSnapshot := *rdsSnapshotName + snapshotTime
    IndentifierSnapshotTime := currentTime.Format("20060102")
    exportIdentifier := *exportRDSSnapshotName + IndentifierSnapshotTime

    // Getting latest snapsnot from rds snapshot list
    for _, s := range result.DBSnapshots {
        if strings.Contains(*s.DBSnapshotArn, rdsSnapshot) {
            result, err := svc.StartExportTask(&rds.StartExportTaskInput{
                ExportTaskIdentifier: aws.String(exportIdentifier),
                IamRoleArn:           roleArn,
                KmsKeyId:             kmsKey,
                S3BucketName:         bucketName,
                SourceArn:            aws.String(*s.DBSnapshotArn),
            })
            // snippet-end:[rds.go.copy_snapshot_to_s3.call]
            return result, err
        }
    }

    return nil, nil
}

func main() {
    // snippet-start:[rds.go.copy_snapshot_to_s3.args]
    roleArn := flag.String("a", "", "The ARN of the role")
    kmsKey := flag.String("k", "", "The KMS key")
    bucketName := flag.String("b", "", "The name of the bucket to store the snapshot")
    rdsSnapshotName := flag.String("s", "", "The name of the RDS snapshot")
    exportRDSSnapshotName := flag.String("e", "", "The name to export to")

    if *roleArn == "" || *kmsKey == "" || *bucketName == "" || *rdsSnapshotName == "" || *exportRDSSnapshotName == "" {
        fmt.Println("You must supply a role ARN, KMS key, bucket name, RDS snapshot name, and export name")
        fmt.Println("-a ROLE-ARN -k KMS-KEY -b BUCKET-NAME -s SNAPSHOT-NAME -e EXPORT-NAME")
        return
    }
    // snippet-end:[rds.go.copy_snapshot_to_s3.args]

    // snippet-start:[rds.go.copy_snapshot_to_s3.session]
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    svc := rds.New(sess)
    // snippet-end:[rds.go.copy_snapshot_to_s3.session]

    result, err := StoreInstance(svc, rdsSnapshotName, exportRDSSnapshotName, roleArn, kmsKey, bucketName)
    if err != nil {
        fmt.Println("Got an error storing the instance:")
        fmt.Println(err)
        return
    }

    fmt.Println(result)
}
// snippet-end:[rds.go.copy_snapshot_to_s3]
