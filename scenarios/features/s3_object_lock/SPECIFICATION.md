# Amazon S3 Object Lock Scenario - Technical specification

This document contains the technical specifications for _Amazon S3 Object Lock Scenario_,
a feature scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages.

This document explains the following:

- Architecture and features of the example scenario.
- Metadata information for the scenario.
- Sample reference output.

For an introduction, see the [README.md](README.md).

---

### Table of contents

- [Resources and User Input](#resources-and-user-input)
- [Metadata](#metadata)

## Resources and User Input

- Amazon Simple Storage Service (Amazon S3) Buckets and objects (created in the scenario)
  - One bucket with no locking features enabled.
  - One bucket with object lock enabled on creation.
  - One bucket with object lock enabled after creation.
  - One bucket with a default retention period.

Example:
```
S3 buckets can be created either with or without object lock enabled.
        Creating bucket dotnet-s3-lock-example0-no-lock with object lock False.
        Creating bucket dotnet-s3-lock-example0-lock-enabled with object lock True.
        Creating bucket dotnet-s3-lock-example0-retention-after-creation with object lock False.
Press Enter to continue.


A bucket can be configured to use object locking with a default retention period.
        Added a default retention to bucket dotnet-s3-lock-example0-retention-after-creation.
Press Enter to continue.


Object lock policies can also be added to existing buckets.
        Added an object lock policy to bucket dotnet-s3-lock-example0-lock-enabled.
Press Enter to continue.

```
- Amazon S3 objects created in the scenario:
  - Two files in each bucket, so that different settings can be applied.
  - For Object Lock and Retention Period settings, the user should be given the option to add these settings.
    - For Retention Period, only Governance mode should be used, since Compliance mode cannot be removed even by the root user.
    
Example:

```
Now let's add some test files:
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-no-lock.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-no-lock.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-lock-enabled.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-lock-enabled.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-retention-after-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-retention-after-creation.
Press Enter to continue.


Now we can set some object lock policies on individual files:

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled? (y/n)
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example0-lock-enabled? (y/n)
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
```

- Locking Options
  - In order to cover all the example topics in the S3 guide section, the scenario covers attempting to delete, overwrite, and view object lock and retention settings for a file. This section should provide a menu of options to the user so they can observe the results on the locked and unlocked files.

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

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: 5aHizC1XBao6nnTyLnbiaOEmKAVSIy6R

Enter the number of the object to view:
3
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled:
        Status: ON
--------------------------------------------------------------------------------
```
- Cleanup
  - The scenario should get the full list of objects, and remove all locks before deleting the objects and buckets.
    - Object locks should be removed.
    - Governance Retention periods should be bypassed using the BypassGovernanceRetention header.
    - All versions should be deleted.
    - The user should be notified if the delete operation cannot occur.

Example:

```
--------------------------------------------------------------------------------
Welcome to the Amazon Simple Storage Service (S3) Object Locking Feature Scenario.
--------------------------------------------------------------------------------

For this workflow, we will use the AWS SDK for .NET to create several S3
buckets and files to demonstrate working with S3 locking features.

--------------------------------------------------------------------------------
Press Enter when you are ready to start.


S3 buckets can be created either with or without object lock enabled.
        Creating bucket dotnet-s3-lock-example0-no-lock with object lock False.
        Creating bucket dotnet-s3-lock-example0-lock-enabled with object lock True.
        Creating bucket dotnet-s3-lock-example0-retention-after-creation with object lock False.
Press Enter to continue.


A bucket can be configured to use object locking with a default retention period.
        Added a default retention to bucket dotnet-s3-lock-example0-retention-after-creation.
Press Enter to continue.


Object lock policies can also be added to existing buckets.
        Added an object lock policy to bucket dotnet-s3-lock-example0-lock-enabled.
Press Enter to continue.


Now let's add some test files:
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-no-lock.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-no-lock.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-lock-enabled.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-lock-enabled.
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-retention-after-creation.
        Successfully uploaded dotnet-example-file1.txt to dotnet-s3-lock-example0-retention-after-creation.
Press Enter to continue.


Now we can set some object lock policies on individual files:

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled? (y/n)
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example0-lock-enabled? (y/n)
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example0-lock-enabled until 2/28/2024.

Would you like to add a legal hold to dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation? (y/n)
y
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation.

Would you like to add a 1 day Governance retention period to dotnet-example-file1.txt in dotnet-s3-lock-example0-retention-after-creation? (y/n)
Reminder: Only a user with the s3:BypassGovernanceRetention permission will be able to delete this file or its bucket until the retention period has expired.
y
        Set retention for dotnet-example-file1.txt in dotnet-s3-lock-example0-retention-after-creation until 2/28/2024.
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
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: 5aHizC1XBao6nnTyLnbiaOEmKAVSIy6R
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
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: 5aHizC1XBao6nnTyLnbiaOEmKAVSIy6R
6
        Unable to delete object dotnet-example-file1.txt in bucket dotnet-s3-lock-example0-retention-after-creation: Access Denied
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

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6
6: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: 5aHizC1XBao6nnTyLnbiaOEmKAVSIy6R
6
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example0-retention-after-creation.
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

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6

Enter the number of the object to overwrite:
5
        Successfully uploaded dotnet-example-file0.txt to dotnet-s3-lock-example0-retention-after-creation.
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

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: xTlwkuQ_l9uKZoksfHKCHRjNNXLuEqCi
6: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6

Enter the number of the object and bucket to view:
5
        Object retention for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation:
        GOVERNANCE until 2/28/2024.
        Bucket object lock config for dotnet-s3-lock-example0-retention-after-creation in dotnet-s3-lock-example0-retention-after-creation:
        Enabled: Enabled
        Rule: Amazon.S3.Model.DefaultRetention
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

1: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
2: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-no-lock
        Version: null
3: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: GY4S9LbwYsglEhq3FY5I0tvc85peaplB
4: dotnet-example-file1.txt
        Bucket: dotnet-s3-lock-example0-lock-enabled
        Version: h4BEwfBUtanzdrDFbnqLfqxJflYQ2_9g
5: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: xTlwkuQ_l9uKZoksfHKCHRjNNXLuEqCi
6: dotnet-example-file0.txt
        Bucket: dotnet-s3-lock-example0-retention-after-creation
        Version: X22KoUqO4DRfrHgNnS_4ZDf7bpR4eKc6

Enter the number of the object to view:
3
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled:
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
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example0-no-lock.
        Unable to fetch legal hold: 'Bucket is missing Object Lock Configuration'
        Unable to fetch object lock retention: 'Bucket is missing Object Lock Configuration'
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example0-no-lock.
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled:
        Status: ON
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled.
        Unable to fetch object lock retention: 'The specified object does not have a ObjectLock configuration'
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example0-lock-enabled.
        Unable to fetch legal hold: 'The specified object does not have a ObjectLock configuration'
        Object retention for dotnet-example-file1.txt in dotnet-s3-lock-example0-lock-enabled:
        GOVERNANCE until 2/28/2024.
Deleted dotnet-example-file1.txt in dotnet-s3-lock-example0-lock-enabled.
        Unable to fetch legal hold: 'The specified object does not have a ObjectLock configuration'
        Object retention for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation:
        GOVERNANCE until 2/28/2024.
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation.
        Object legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation:
        Status: ON
        Modified legal hold for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation.
        Object retention for dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation:
        GOVERNANCE until 2/28/2024.
Deleted dotnet-example-file0.txt in dotnet-s3-lock-example0-retention-after-creation.
        Delete for dotnet-s3-lock-example0-no-lock complete.
        Delete for dotnet-s3-lock-example0-lock-enabled complete.
        Delete for dotnet-s3-lock-example0-retention-after-creation complete.
--------------------------------------------------------------------------------
--------------------------------------------------------------------------------
Amazon S3 Object Locking Workflow is complete.
--------------------------------------------------------------------------------
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
| `PutObjectLockConfiguration` | s3_metadata.yaml | s3_PutDefaultObjectLockConfiguration  |
| `S3 Object Lock Scenario`    | s3_metadata.yaml | s3_Scenario_ObjectLock   |

