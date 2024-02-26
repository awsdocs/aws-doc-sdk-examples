# Amazon S3 Object Lock Workflow - Technical specification

This document contains the technical specifications for _Amazon S3 Object Lock Workflow_,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example workflow.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [User input](#user-input)
- [Output](#output)
- [Metadata](#metadata)

## Resources and User Input

- Amazon Simple Storage Service (Amazon S3) Buckets and objects (created in the scenario)
  - One bucket with no locking features enabled.
  - One bucket with object lock enabled on creation.
  - One bucket with object lock enabled after creation.
  - One bucket with a default retention period.

Example:
```S3 buckets can be created either with or without object lock enabled.
        Creating bucket dotnet-s3-lock-example101-no-lock with object lock False.
        Creating bucket dotnet-s3-lock-example101-lock-enabled with object lock True.
        Creating bucket dotnet-s3-lock-example101-retention-after-creation with object lock False.
Press Enter to continue.


A bucket can also have object locking with a default retention period.
        Creating bucket dotnet-s3-lock-example101-retention-on-creation with object lock True.
Press Enter to continue.


Object lock policies can also be added to existing buckets.
        Added an object lock policy to bucket dotnet-s3-lock-example101-lock-enabled.
        Added a default retention to bucket dotnet-s3-lock-example101-retention-after-creation.
Press Enter to continue.

```
- Amazon S3 objects created in the scenario:
  - Two files in each bucket, so that different settings can be applied.
  - For Object Lock and Retention Period settings, the user should be given the option to add these settings.
    - For Retention Period, only Governance mode should be used, since Compliance mode cannot be removed even by the root user.
    
Example:

```
Now let's add some test files:
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-no-lock.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-no-lock.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-lock-enabled.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-lock-enabled.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-retention-on-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-retention-on-creation.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-retention-after-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-retention-after-creation.
Press Enter to continue.


Now we can set some object lock policies on individual files:

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled?
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled?
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled until 2/27/2024.
```

- Locking Options
  - In order to cover all the example topics in the S3 guide section, the workflow covers attempting to delete, overwrite, and view object lock and retention settings for a file. This section should provide a menu of options to the user so they can observe the results on the locked and unlocked files.

Example
```
Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
        
6
--------------------------------------------------------------------------------

Current buckets and objects:

1: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
3: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
4: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
5: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Gk_06bqOl73hrTWgs_5o1sMjk6oX0q7_
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
7: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX

Enter the number of the object to view:
7
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation:
        Status: ON
--------------------------------------------------------------------------------
```
- Cleanup
  - The workflow should get the full list of objects, and remove all locks before deleting the objects and buckets.
    - Object locks should be removed.
    - Governance Retention periods should be bypassed using the BypassGovernanceRetention header.
    - All versions should be deleted.
    - The user should be notified if the delete operation cannot occur.

Example:

```
// snippet-start:[S3LockWorkflow.dotnetv3.DeleteObjectFromBucket]
/// <summary>
/// Delete an object from a specific bucket.
/// </summary>
/// <param name="bucketName">The Amazon S3 bucket to use.</param>
/// <param name="objectKey">The key of the object to delete.</param>
/// <param name="hasRetention">True if the object has retention settings.</param>
/// <param name="versionId">Optional versionId.</param>
/// <returns>True if successful.</returns>
public async Task<bool> DeleteObjectFromBucket(string bucketName, string objectKey, bool hasRetention, string? versionId = null)
{
    try
    {
        var request = new DeleteObjectRequest()
        {
            BucketName = bucketName,
            Key = objectKey,
            VersionId = versionId,
        };
        if (hasRetention)
        {
            // Set the BypassGovernanceRetention header
            // if the file has retention settings.
            request.BypassGovernanceRetention = true;
        }

        await _amazonS3.DeleteObjectAsync(request);
        Console.WriteLine(
            $"Deleted {objectKey} in {bucketName}.");
        return true;
    }
    catch (AmazonS3Exception ex)
    {
        Console.WriteLine($"\tUnable to delete object {objectKey} in bucket {bucketName}: " + ex.Message);
        return false;
    }
}
```

---


## Output

The user should be guided through the 3 main parts of the example:
1. Setting up buckets and objects.
2. Explore the behavior of locked objects.
3. Cleaning up the objects and buckets.

Complete Output:

```
--------------------------------------------------------------------------------
Welcome to the Amazon Simple Storage Service (S3) Object Locking Workflow Scenario.
--------------------------------------------------------------------------------

For this workflow, we will use the AWS SDK for .NET to create several S3
buckets and files to demonstrate working with S3 locking features.

--------------------------------------------------------------------------------
Press Enter when you are ready to start.


S3 buckets can be created either with or without object lock enabled.
        Creating bucket dotnet-s3-lock-example101-no-lock with object lock False.
        Creating bucket dotnet-s3-lock-example101-lock-enabled with object lock True.
        Creating bucket dotnet-s3-lock-example101-retention-after-creation with object lock False.
Press Enter to continue.


A bucket can also have object locking with a default retention period.
        Creating bucket dotnet-s3-lock-example101-retention-on-creation with object lock True.
Press Enter to continue.


Object lock policies can also be added to existing buckets.
        Added an object lock policy to bucket dotnet-s3-lock-example101-lock-enabled.
        Added a default retention to bucket dotnet-s3-lock-example101-retention-after-creation.
Press Enter to continue.


Now let's add some test files:
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-no-lock.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-no-lock.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-lock-enabled.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-lock-enabled.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-retention-on-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-retention-on-creation.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example101-retention-after-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-retention-after-creation.
Press Enter to continue.


Now we can set some object lock policies on individual files:

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled?
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled?
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled until 2/27/2024.

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-on-creation?
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-on-creation.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-on-creation?
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-on-creation until 2/27/2024.

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation?
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-after-creation?
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-after-creation until 2/27/2024.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
1
--------------------------------------------------------------------------------

Current buckets and objects:

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
7: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX
8: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: VDO.21OOpn7Op0_GPx6njRX51NdoFYgN
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
2
--------------------------------------------------------------------------------

Enter the number of the object to delete:

Current buckets and objects:

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
7: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX
8: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: VDO.21OOpn7Op0_GPx6njRX51NdoFYgN
1
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example101-no-lock.
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
3
--------------------------------------------------------------------------------

Enter the number of the object to delete:

Current buckets and objects:

1: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
3: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
4: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
5: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
6: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX
7: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: VDO.21OOpn7Op0_GPx6njRX51NdoFYgN
7
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-after-creation.
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
4
--------------------------------------------------------------------------------

Current buckets and objects:

1: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
3: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
4: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
5: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
6: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX

Enter the number of the object to overwrite:
5
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example101-retention-on-creation.
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
5
--------------------------------------------------------------------------------

Current buckets and objects:

1: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
3: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
4: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
5: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Gk_06bqOl73hrTWgs_5o1sMjk6oX0q7_
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
7: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX

Enter the number of the object and bucket to view:
3
        Object retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled:
        GOVERNANCE until 2/27/2024.
        Bucket object lock config for dotnet-s3-lock-example101-lock-enabled in dotnet-s3-lock-example101-lock-enabled:
        Enabled: Enabled
        Rule:
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
6
--------------------------------------------------------------------------------

Current buckets and objects:

1: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-no-lock
        Version: null
2: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: AnTrWHG77pY_s4p02vj__uQUhEqTHH3h
3: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-lock-enabled
        Version: 3PbveCh7fUvGelDJusIV43v.dxkA6hnJ
4: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: R6_x7px5f.ED6hyS0JtnMKwtiF3xpr9k
5: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Gk_06bqOl73hrTWgs_5o1sMjk6oX0q7_
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example101-retention-on-creation
        Version: Av0mu_zS4PFnGiC2MKPUMSIll2jdi99c
7: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example101-retention-after-creation
        Version: uqVtfPlwdC0DgSo48uLQepHkWbiD3ibX

Enter the number of the object to view:
7
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation:
        Status: ON
--------------------------------------------------------------------------------

Explore the S3 locking features by selecting one of the following choices:
        1. List all files in buckets.
        2. Attempt to delete a file.
        3. Attempt to delete a file with retention period bypass.
        4. Attempt to overwrite a file.
        5. View the object and bucket retention settings for a file.
        6. View the legal hold settings for a file.
        7. Finish the workflow.
7
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Cleaning up resources.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Do you want to clean up all files and buckets? (y/n)
y
        Unable to fetch legal hold: 'Bucket is missing Object Lock Configuration'
        Unable to fetch object lock retention: 'Bucket is missing Object Lock Configuration'
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example101-no-lock.
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled:
        Status: ON
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled.
        Unable to fetch object lock retention: 'The specified object does not have a ObjectLock configuration'
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example101-lock-enabled.
        Unable to fetch legal hold: 'The specified object does not have a ObjectLock configuration'
        Object retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled:
        GOVERNANCE until 2/27/2024.
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example101-lock-enabled.
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-on-creation:
        Status: ON
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-on-creation.
        Unable to fetch object lock retention: 'The specified object does not have a ObjectLock configuration'
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-on-creation.
        Unable to fetch legal hold: 'The specified object does not have a ObjectLock configuration'
        Unable to fetch object lock retention: 'The specified object does not have a ObjectLock configuration'
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-on-creation.
        Unable to fetch legal hold: 'The specified object does not have a ObjectLock configuration'
        Object retention for dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-on-creation:
        GOVERNANCE until 2/27/2024.
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example101-retention-on-creation.
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation:
        Status: ON
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation.
        Object retention for dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation:
        GOVERNANCE until 2/27/2024.
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example101-retention-after-creation.
        Delete for dotnet-s3-lock-example101-no-lock complete.
        Delete for dotnet-s3-lock-example101-lock-enabled complete.
        Delete for dotnet-s3-lock-example101-retention-on-creation complete.
        Delete for dotnet-s3-lock-example101-retention-after-creation complete.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Amazon S3 Object Locking Workflow is complete.
--------------------------------------------------------------------------------

C:\Work\Repos\Forks\aws-doc-sdk-examples\dotnetv3\S3\scenarios\S3ObjectLockScenario\ObjectLockWorkflow\bin\Debug\net6.0\S3ObjectLockScenario.exe (process 30956) exited with code 0.
To automatically close the console when debugging stops, enable Tools->Options->Debugging->Automatically close the console when debugging stops.
Press any key to close this window . . .
```

---

## Metadata

| action / scenario            | metadata file    | metadata key                      |
|------------------------------|------------------| --------------------------------- |
| `GetObjectLegalHold`         | s3_metadata.yaml | s3_GetObjectLegalHoldConfiguration   |
| `GetObjectLockConfiguration` | s3_metadata.yaml | s3_GetObjectLockConfiguration   |
| `GetObjectRetention`         | s3_metadata.yaml | s3_GetObjectRetention   |
| `PutObjectLegalHold`         | s3_metadata.yaml | s3_PutObjectLegalHold   |
| `PutObjectLockConfiguration` | s3_metadata.yaml | s3_PutObjectLockConfiguration   |
| `PutObjectRetention`         | s3_metadata.yaml | s3_PutObjectRetention   |
| `S3 Object Lock Scenario`    | s3_metadata.yaml | s3_Scenario_ObjectLock   |
