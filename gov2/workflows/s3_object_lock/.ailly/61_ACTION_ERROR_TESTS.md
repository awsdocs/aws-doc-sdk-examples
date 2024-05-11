---
combined: true
debug:
  engine: bedrock
  finish: max_tokens
  id: null
  model: anthropic.claude-3-sonnet-20240229-v1:0
  usage: null
isolated: false
prompt: |
  Write unit tests to cover all possible error paths for each action in this code:
  <example>
    package actions
    
    import (
        "bytes"
        "context"
        "errors"
        "fmt"
        "log"
        "time"
    
        "github.com/aws/aws-sdk-go-v2/aws"
        "github.com/aws/aws-sdk-go-v2/feature/s3/manager"
        "github.com/aws/aws-sdk-go-v2/service/s3"
        "github.com/aws/aws-sdk-go-v2/service/s3/types"
        "github.com/aws/smithy-go"
    )
    
    // S3Actions wraps S3 service actions.
    type S3Actions struct {
        S3Client  *s3.Client
        S3Manager *manager.Uploader
    }
    
    // CreateBucketWithLock creates a new S3 bucket with optional object locking enabled.
    func (actor S3Actions) CreateBucketWithLock(ctx context.Context, bucket string, region string, enableObjectLock bool) (string, error) {
        input := &s3.CreateBucketInput{
            Bucket: aws.String(bucket),
            CreateBucketConfiguration: &types.CreateBucketConfiguration{
                LocationConstraint: types.BucketLocationConstraint(region),
            },
        }
    
        if enableObjectLock {
            input.ObjectLockEnabledForBucket = aws.Bool(true)
        }
    
        _, err := actor.S3Client.CreateBucket(ctx, input)
        if err != nil {
            var owned *types.BucketAlreadyOwnedByYou
            var exists *types.BucketAlreadyExists
            if errors.As(err, &owned) {
                log.Printf("You already own bucket %s.\n", bucket)
                err = owned
            } else if errors.As(err, &exists) {
                log.Printf("Bucket %s already exists.\n", bucket)
                err = exists
            }
        } else {
            err = s3.NewBucketExistsWaiter(actor.S3Client).Wait(
                ctx, &s3.HeadBucketInput{Bucket: aws.String(bucket)}, time.Minute)
            if err != nil {
                log.Printf("Failed attempt to wait for bucket %s to exist.\n", bucket)
            }
        }
    
        return bucket, err
    }
        
    // GetObjectLegalHold retrieves the legal hold status for an S3 object.
    func (actor S3Actions) GetObjectLegalHold(ctx context.Context, bucket string, key string, versionId string) (*types.ObjectLockLegalHoldStatus, error) {
        var status *types.ObjectLockLegalHoldStatus
        input := &s3.GetObjectLegalHoldInput{
            Bucket:    aws.String(bucket),
            Key:       aws.String(key),
            VersionId: aws.String(versionId),
        }
    
        output, err := actor.S3Client.GetObjectLegalHold(ctx, input)
        if err != nil {
            var noSuchKeyErr *types.NoSuchKey
            var apiErr *smithy.GenericAPIError
            if errors.As(err, &noSuchKeyErr) {
                log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
                err = noSuchKeyErr
            } else if errors.As(err, &apiErr) {
                switch apiErr.ErrorCode() {
                case "NoSuchObjectLockConfiguration":
                    log.Printf("Object %s does not have an object lock configuration.\n", key)
                    err = nil
                case "InvalidRequest":
                    log.Printf("Bucket %s does not have an object lock configuration.\n", bucket)
                    err = nil
                }
            }
        } else {
            status = &output.LegalHold.Status
        }
    
        return status, err
    }
    
    // GetObjectLockConfiguration retrieves the object lock configuration for an S3 bucket.
    func (actor S3Actions) GetObjectLockConfiguration(ctx context.Context, bucket string) (*types.ObjectLockConfiguration, error) {
        var lockConfig *types.ObjectLockConfiguration
        input := &s3.GetObjectLockConfigurationInput{
            Bucket: aws.String(bucket),
        }
    
        output, err := actor.S3Client.GetObjectLockConfiguration(ctx, input)
        if err != nil {
            var noBucket *types.NoSuchBucket
            var apiErr *smithy.GenericAPIError
            if errors.As(err, &noBucket) {
                log.Printf("Bucket %s does not exist.\n", bucket)
                err = noBucket
            } else if errors.As(err, &apiErr) && apiErr.ErrorCode() == "ObjectLockConfigurationNotFoundError" {
                log.Printf("Bucket %s does not have an object lock configuration.\n", bucket)
                err = nil
            }
        } else {
            lockConfig = output.ObjectLockConfiguration
        }
    
        return lockConfig, err
    }
    
    // GetObjectRetention retrieves the object retention configuration for an S3 object.
    func (actor S3Actions) GetObjectRetention(ctx context.Context, bucket string, key string) (*types.ObjectLockRetention, error) {
        var retention *types.ObjectLockRetention
        input := &s3.GetObjectRetentionInput{
            Bucket: aws.String(bucket),
            Key:    aws.String(key),
        }
    
        output, err := actor.S3Client.GetObjectRetention(ctx, input)
        if err != nil {
            var noKey *types.NoSuchKey
            var apiErr *smithy.GenericAPIError
            if errors.As(err, &noKey) {
                log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
                err = noKey
            } else if errors.As(err, &apiErr) {
                switch apiErr.ErrorCode() {
                case "NoSuchObjectLockConfiguration":
                    err = nil
                case "InvalidRequest":
                    log.Printf("Bucket %s does not have locking enabled.", bucket)
                    err = nil
                }
            }
        } else {
            retention = output.Retention
        }
    
        return retention, err
    }
    
    // PutObjectLegalHold sets the legal hold configuration for an S3 object.
    func (actor S3Actions) PutObjectLegalHold(ctx context.Context, bucket string, key string, versionId string, legalHoldStatus types.ObjectLockLegalHoldStatus) error {
        input := &s3.PutObjectLegalHoldInput{
            Bucket: aws.String(bucket),
            Key:    aws.String(key),
            LegalHold: &types.ObjectLockLegalHold{
                Status: legalHoldStatus,
            },
        }
        if versionId != "" {
            input.VersionId = aws.String(versionId)
        }
    
        _, err := actor.S3Client.PutObjectLegalHold(ctx, input)
        if err != nil {
            var noKey *types.NoSuchKey
            if errors.As(err, &noKey) {
                log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
                err = noKey
            }
        }
    
        return err
    }
    
    // ModifyDefaultBucketRetention modifies the default retention period of an existing bucket.
    func (actor S3Actions) ModifyDefaultBucketRetention(
        ctx context.Context, bucket string, lockMode types.ObjectLockEnabled, retentionPeriod int32, retentionMode types.ObjectLockRetentionMode) error {
    
        input := &s3.PutObjectLockConfigurationInput{
            Bucket: aws.String(bucket),
            ObjectLockConfiguration: &types.ObjectLockConfiguration{
                ObjectLockEnabled: lockMode,
                Rule: &types.ObjectLockRule{
                    DefaultRetention: &types.DefaultRetention{
                        Days: aws.Int32(retentionPeriod),
                        Mode: retentionMode,
                    },
                },
            },
        }
        _, err := actor.S3Client.PutObjectLockConfiguration(ctx, input)
        if err != nil {
            var noBucket *types.NoSuchBucket
            if errors.As(err, &noBucket) {
                log.Printf("Bucket %s does not exist.\n", bucket)
                err = noBucket
            }
        }
    
        return err
    }
    
    // EnableObjectLockOnBucket enables object locking on an existing bucket.
    func (actor S3Actions) EnableObjectLockOnBucket(ctx context.Context, bucket string) error {
        // Versioning must be enabled on the bucket before object locking is enabled.
        verInput := &s3.PutBucketVersioningInput{
            Bucket: aws.String(bucket),
            VersioningConfiguration: &types.VersioningConfiguration{
                MFADelete: types.MFADeleteDisabled,
                Status:    types.BucketVersioningStatusEnabled,
            },
        }
        _, err := actor.S3Client.PutBucketVersioning(ctx, verInput)
        if err != nil {
            var noBucket *types.NoSuchBucket
            if errors.As(err, &noBucket) {
                log.Printf("Bucket %s does not exist.\n", bucket)
                err = noBucket
            }
            return err
        }
    
        input := &s3.PutObjectLockConfigurationInput{
            Bucket: aws.String(bucket),
            ObjectLockConfiguration: &types.ObjectLockConfiguration{
                ObjectLockEnabled: types.ObjectLockEnabledEnabled,
            },
        }
        _, err = actor.S3Client.PutObjectLockConfiguration(ctx, input)
        if err != nil {
            var noBucket *types.NoSuchBucket
            if errors.As(err, &noBucket) {
                log.Printf("Bucket %s does not exist.\n", bucket)
                err = noBucket
            }
        }
    
        return err
    }
    
    // PutObjectRetention sets the object retention configuration for an S3 object.
    func (actor S3Actions) PutObjectRetention(ctx context.Context, bucket string, key string, retentionMode types.ObjectLockRetentionMode, retentionPeriodDays int32) error {
        input := &s3.PutObjectRetentionInput{
            Bucket: aws.String(bucket),
            Key:    aws.String(key),
            Retention: &types.ObjectLockRetention{
                Mode:            retentionMode,
                RetainUntilDate: aws.Time(time.Now().AddDate(0, 0, int(retentionPeriodDays))),
            },
            BypassGovernanceRetention: aws.Bool(true),
        }
    
        _, err := actor.S3Client.PutObjectRetention(ctx, input)
        if err != nil {
            var noKey *types.NoSuchKey
            if errors.As(err, &noKey) {
                log.Printf("Object %s does not exist in bucket %s.\n", key, bucket)
                err = noKey
            }
        }
    
        return err
    }
    
    // UploadObject uses the S3 upload manager to upload an object to a bucket.
    func (actor S3Actions) UploadObject(ctx context.Context, bucket string, key string, contents string) (string, error) {
        var outKey string
        input := &s3.PutObjectInput{
            Bucket:            aws.String(bucket),
            Key:               aws.String(key),
            Body:              bytes.NewReader([]byte(contents)),
            ChecksumAlgorithm: types.ChecksumAlgorithmSha256,
        }
        output, err := actor.S3Manager.Upload(ctx, input)
        if err != nil {
            var noBucket *types.NoSuchBucket
            if errors.As(err, &noBucket) {
                log.Printf("Bucket %s does not exist.\n", bucket)
                err = noBucket
            }
        } else {
            err := s3.NewObjectExistsWaiter(actor.S3Client).Wait(ctx, &s3.HeadObjectInput{
                Bucket: aws.String(bucket),
                Key:    aws.String(key),
            }, time.Minute)
            if err != nil {
                log.Printf("Failed attempt to wait for object %s to exist in %s.\n", key, bucket)
            } else {
                outKey = *output.Key
            }
        }
        return outKey, err
    }
    
    func (actor S3Actions) ListObjectVersions(ctx context.Context, bucket string) ([]types.ObjectVersion, error) {
        var err error
        var output *s3.ListObjectVersionsOutput
        var versions []types.ObjectVersion
        input := &s3.ListObjectVersionsInput{Bucket: aws.String(bucket)}
        versionPaginator := s3.NewListObjectVersionsPaginator(actor.S3Client, input)
        for versionPaginator.HasMorePages() {
            output, err = versionPaginator.NextPage(ctx)
            if err != nil {
                var noBucket *types.NoSuchBucket
                if errors.As(err, &noBucket) {
                    log.Printf("Bucket %s does not exist.\n", bucket)
                    err = noBucket
                }
                break
            } else {
                versions = append(versions, output.Versions...)
            }
        }
        return versions, err
    }
    
    func (actor S3Actions) DeleteObject(ctx context.Context, bucket string, key string, versionId string, bypassGovernance bool) (bool, error) {
        deleted := false
        input := &s3.DeleteObjectInput{
            Bucket: aws.String(bucket),
            Key:    aws.String(key),
        }
        if versionId != "" {
            input.VersionId = aws.String(versionId)
        }
        if bypassGovernance {
            input.BypassGovernanceRetention = aws.Bool(true)
        }
        _, err := actor.S3Client.DeleteObject(ctx, input)
        if err != nil {
            var noKey *types.NoSuchKey
            var apiErr *smithy.GenericAPIError
            if errors.As(err, &noKey) {
                log.Printf("Object %s does not exist in %s.\n", key, bucket)
                err = noKey
            } else if errors.As(err, &apiErr) {
                switch apiErr.ErrorCode() {
                case "AccessDenied":
                    log.Printf("Access denied: cannot delete object %s from %s.\n", key, bucket)
                    err = nil
                case "InvalidArgument":
                    if bypassGovernance {
                        log.Printf("You cannot specify bypass governance on a bucket without lock enabled.")
                        err = nil
                    }
                }
            }
        } else {
            deleted = true
        }
        return deleted, err
    }
    
    func (actor S3Actions) DeleteObjects(ctx context.Context, bucket string, objects []types.ObjectIdentifier, bypassGovernance bool) error {
        if len(objects) == 0 {
            return nil
        }
    
        input := s3.DeleteObjectsInput{
            Bucket: aws.String(bucket),
            Delete: &types.Delete{
                Objects: objects,
                Quiet:   aws.Bool(true),
            },
        }
        if bypassGovernance {
            input.BypassGovernanceRetention = aws.Bool(true)
        }
        delOut, err := actor.S3Client.DeleteObjects(ctx, &input)
        if err != nil || len(delOut.Errors) > 0 {
            log.Printf("Error deleting objects from bucket %s.\n", bucket)
            if err != nil {
                var noBucket *types.NoSuchBucket
                if errors.As(err, &noBucket) {
                    log.Printf("Bucket %s does not exist.\n", bucket)
                    err = noBucket
                }
            } else if len(delOut.Errors) > 0 {
                for _, outErr := range delOut.Errors {
                    log.Printf("%s: %s\n", *outErr.Key, *outErr.Message)
                }
                err = fmt.Errorf("%s", *delOut.Errors[0].Message)
            }
        }
        return err
    }
  </example>
  Use this this example as a template to create the code:
  <example>
  func TestCreateBucketWithLock(t *testing.T) {
      ctx, stubber, actor := enterTest()

      for _, err := range []error{&types.BucketAlreadyOwnedByYou{}, &types.BucketAlreadyExists{}} {
          expectedErr, stubErr := wrapErr(err)
          stubber.Add(stubs.StubCreateBucket("test-bucket", "test-region", true, stubErr))
          _, actualErr := actor.CreateBucketWithLock(ctx, "test-bucket", "test-region", true)
          verifyErr(expectedErr, &actualErr, t)
      }

      testtools.ExitTest(stubber, t)
  }

  func TestGetObjectLegalHold(t *testing.T) {
      ctx, stubber, actor := enterTest()

      for _, err := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "NoSuchObjectLockConfiguration"}, &smithy.GenericAPIError{Code: "InvalidRequest"}} {
          expectedErr, stubErr := wrapErr(err)
          stubber.Add(stubs.StubGetObjectLegalHold("test-bucket", "test-region", "test-version", types.ObjectLockLegalHoldStatusOn, stubErr))
          _, actualErr := actor.GetObjectLegalHold(ctx, "test-bucket", "test-region", "test-version")
          verifyErr(expectedErr, &actualErr, t)
      }

      testtools.ExitTest(stubber, t)
  }
  </example>
---
Sure, here are unit tests that cover all possible error paths for each action in the provided code:

```go
package actions

import (
    "context"
    "errors"
    "testing"

    "github.com/aws/aws-sdk-go-v2/aws"
    "github.com/aws/aws-sdk-go-v2/service/s3"
    "github.com/aws/aws-sdk-go-v2/service/s3/types"
    "github.com/aws/smithy-go"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/testtools"
    "github.com/awsdocs/aws-doc-sdk-examples/gov2/workflows/stubs"
)

func TestCreateBucketWithLock(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    for _, err := range []error{&types.BucketAlreadyOwnedByYou{}, &types.BucketAlreadyExists{}} {
        expectedErr, stubErr := wrapErr(err)
        stubber.Add(stubs.StubCreateBucket("test-bucket", "test-region", true, stubErr))
        _, actualErr := actor.CreateBucketWithLock(ctx, "test-bucket", "test-region", true)
        verifyErr(expectedErr, &actualErr, t)
    }

    stubber.Add(stubs.StubCreateBucket("test-bucket", "test-region", true, nil))
    stubber.Add(stubs.StubBucketExistsWaiter("test-bucket", &testtools.StubError{Err: errors.New("test error")}))
    _, err := actor.CreateBucketWithLock(ctx, "test-bucket", "test-region", true)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }
}

func TestGetObjectLegalHold(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    for _, err := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "NoSuchObjectLockConfiguration"}, &smithy.GenericAPIError{Code: "InvalidRequest"}} {
        expectedErr, stubErr := wrapErr(err)
        stubber.Add(stubs.StubGetObjectLegalHold("test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn, stubErr))
        _, actualErr := actor.GetObjectLegalHold(ctx, "test-bucket", "test-key", "test-version")
        verifyErr(expectedErr, &actualErr, t)
    }

    stubber.Add(stubs.StubGetObjectLegalHold("test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn, nil))
    _, err := actor.GetObjectLegalHold(ctx, "test-bucket", "test-key", "test-version")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestGetObjectLockConfiguration(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    for _, err := range []error{&types.NoSuchBucket{}, &smithy.GenericAPIError{Code: "ObjectLockConfigurationNotFoundError"}} {
        expectedErr, stubErr := wrapErr(err)
        stubber.Add(stubs.StubGetObjectLockConfiguration("test-bucket", &types.ObjectLockConfiguration{}, stubErr))
        _, actualErr := actor.GetObjectLockConfiguration(ctx, "test-bucket")
        verifyErr(expectedErr, &actualErr, t)
    }

    stubber.Add(stubs.StubGetObjectLockConfiguration("test-bucket", &types.ObjectLockConfiguration{}, nil))
    _, err := actor.GetObjectLockConfiguration(ctx, "test-bucket")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestGetObjectRetention(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    for _, err := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "NoSuchObjectLockConfiguration"}, &smithy.GenericAPIError{Code: "InvalidRequest"}} {
        expectedErr, stubErr := wrapErr(err)
        stubber.Add(stubs.StubGetObjectRetention("test-bucket", "test-key", &types.ObjectLockRetention{}, stubErr))
        _, actualErr := actor.GetObjectRetention(ctx, "test-bucket", "test-key")
        verifyErr(expectedErr, &actualErr, t)
    }

    stubber.Add(stubs.StubGetObjectRetention("test-bucket", "test-key", &types.ObjectLockRetention{}, nil))
    _, err := actor.GetObjectRetention(ctx, "test-bucket", "test-key")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestPutObjectLegalHold(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubPutObjectLegalHold("test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn, &types.NoSuchKey{}))
    err := actor.PutObjectLegalHold(ctx, "test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubPutObjectLegalHold("test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn, nil))
    err = actor.PutObjectLegalHold(ctx, "test-bucket", "test-key", "test-version", types.ObjectLockLegalHoldStatusOn)
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestModifyDefaultBucketRetention(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubPutObjectLockConfiguration("test-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance, &types.NoSuchBucket{}))
    err := actor.ModifyDefaultBucketRetention(ctx, "test-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubPutObjectLockConfiguration("test-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance, nil))
    err = actor.ModifyDefaultBucketRetention(ctx, "test-bucket", types.ObjectLockEnabledEnabled, 30, types.ObjectLockRetentionModeGovernance)
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestEnableObjectLockOnBucket(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubPutBucketVersioning("test-bucket", types.BucketVersioningStatusEnabled, &types.NoSuchBucket{}))
    err := actor.EnableObjectLockOnBucket(ctx, "test-bucket")
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubPutBucketVersioning("test-bucket", types.BucketVersioningStatusEnabled, nil))
    stubber.Add(stubs.StubPutObjectLockConfiguration("test-bucket", types.ObjectLockEnabledEnabled, &types.NoSuchBucket{}))
    err = actor.EnableObjectLockOnBucket(ctx, "test-bucket")
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubPutBucketVersioning("test-bucket", types.BucketVersioningStatusEnabled, nil))
    stubber.Add(stubs.StubPutObjectLockConfiguration("test-bucket", types.ObjectLockEnabledEnabled, nil))
    err = actor.EnableObjectLockOnBucket(ctx, "test-bucket")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestPutObjectRetention(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubPutObjectRetention("test-bucket", "test-key", types.ObjectLockRetentionModeGovernance, 30, &types.NoSuchKey{}))
    err := actor.PutObjectRetention(ctx, "test-bucket", "test-key", types.ObjectLockRetentionModeGovernance, 30)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubPutObjectRetention("test-bucket", "test-key", types.ObjectLockRetentionModeGovernance, 30, nil))
    err = actor.PutObjectRetention(ctx, "test-bucket", "test-key", types.ObjectLockRetentionModeGovernance, 30)
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestUploadObject(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubUploadObject("test-bucket", "test-key", "test-contents", &types.NoSuchBucket{}))
    _, err := actor.UploadObject(ctx, "test-bucket", "test-key", "test-contents")
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubUploadObject("test-bucket", "test-key", "test-contents", nil))
    stubber.Add(stubs.StubObjectExistsWaiter("test-bucket", "test-key", &testtools.StubError{Err: errors.New("test error")}))
    _, err = actor.UploadObject(ctx, "test-bucket", "test-key", "test-contents")
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubUploadObject("test-bucket", "test-key", "test-contents", nil))
    stubber.Add(stubs.StubObjectExistsWaiter("test-bucket", "test-key", nil))
    _, err = actor.UploadObject(ctx, "test-bucket", "test-key", "test-contents")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestListObjectVersions(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubListObjectVersions("test-bucket", []types.ObjectVersion{}, &types.NoSuchBucket{}))
    _, err := actor.ListObjectVersions(ctx, "test-bucket")
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubListObjectVersions("test-bucket", []types.ObjectVersion{}, nil))
    _, err = actor.ListObjectVersions(ctx, "test-bucket")
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func TestDeleteObject(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    for _, err := range []error{&types.NoSuchKey{}, &smithy.GenericAPIError{Code: "AccessDenied"}, &smithy.GenericAPIError{Code: "InvalidArgument"}} {
        expectedErr, stubErr := wrapErr(err)
        stubber.Add(stubs.StubDeleteObject("test-bucket", "test-key", "test-version", true, stubErr))
        _, actualErr := actor.DeleteObject(ctx, "test-bucket", "test-key", "test-version", true)
        verifyErr(expectedErr, &actualErr, t)
    }

    stubber.Add(stubs.StubDeleteObject("test-bucket", "test-key", "test-version", true, nil))
    deleted, err := actor.DeleteObject(ctx, "test-bucket", "test-key", "test-version", true)
    if !deleted || err != nil {
        t.Errorf("Expected deleted=true, err=nil, got deleted=%v, err=%v", deleted, err)
    }
}

func TestDeleteObjects(t *testing.T) {
    ctx, stubber, actor := enterTest()
    defer testtools.ExitTest(stubber, t)

    stubber.Add(stubs.StubDeleteObjects("test-bucket", []types.ObjectIdentifier{}, true, &types.NoSuchBucket{}))
    err := actor.DeleteObjects(ctx, "test-bucket", []types.ObjectIdentifier{}, true)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubDeleteObjects("test-bucket", []types.ObjectIdentifier{}, true, &testtools.StubError{
        Err: fmt.Errorf("test error"),
        Output: &s3.DeleteObjectsOutput{
            Errors: []types.DeleteError{
                {Key: aws.String("test-key"), Message: aws.String("test error")},
            },
        },
    }))
    err = actor.DeleteObjects(ctx, "test-bucket", []types.ObjectIdentifier{}, true)
    if err == nil {
        t.Errorf("Expected error, got nil")
    }

    stubber.Add(stubs.StubDeleteObjects("test-bucket", []types.ObjectIdentifier{}, true, nil))
    err = actor.DeleteObjects(ctx, "test-bucket", []types.ObjectIdentifier{}, true)
    if err != nil {
        t.Errorf("Unexpected error: %v", err)
    }
}

func enterTest() (context.Context, *testtools.AwsmStubber, *S3Actions) {
    ctx := context.Background()
    stubber := testtools.NewAwsmStubber(ctx, &aws.Config{})
    s3Client := s3.NewFromConfig(*stubber.SdkConfig)
    s3Manager := testtools.NewMockS3Manager()
    actor := S3Actions{S3Client: s3Client, S3Manager: s3Manager}
    return ctx, stubber, &actor
}

func wrapErr(err error) (error, *testtools.StubError) {
    if err == nil {
        return nil, nil
    }
    return err, &testtools.StubError{Err: err}
}

func verifyErr(expectedErr, actualErr *error, t *testing.T) {
    if (expectedErr == nil && actualErr != nil) || (expectedErr != nil && actualErr ==