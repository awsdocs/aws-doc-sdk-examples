# S3 Express One Zone Basics Scenario Specification

## Overview
This SDK Basics scenario demonstrates how to interact with S3 Express One Zone (S3 Express) using the AWS SDK. It demonstrates various tasks such as creating a Directory bucket, using sessions for fast connect times, and downloading objects.  Finally this scenario demonstrates how to clean up resources.

## Resources and User Input
This scenario requires a {bunch of stuff}.
This program is intended for users not familiar with S3 Express to easily get up and running.

## Scenario Program Flow

The S3 Express Basics scenario follows these steps:

1. **Set up a VPC and VPC Endpoint**:
    - Explain to the user that running this code locally will show a lower performance increase than running it on an EC2 instance in the same AZ as the Directory bucket.
    - Prompt the user for whether they are running the code locally or on an EC2 instance. If they are running it locally, skip the rest of step 1.
    - Using the EC2 Client, create a VPC.
    - Get the Route Table for the VPC using `DescribeRouteTables`.
    - Using the EC2 client, create a VPC Endpoint with the created VPC and set the Route Table.
    - **Exception Handling**: There are no unique errors for these operations.

2. **Set up the S3 Express Policies, Roles, and User to work with S3 Express buckets**:
    - Create the policies, roles, and user.
    - Use STS to assume the role and create S3 Express credentials.
    - **Exception Handling**: Check for `InvalidArgumentException`.

3. **Create *two* S3 Clients**:
    - Create a regular S3 Client using normal methods.
    - Create an S3 Client with the created credentials.
    - **Exception Handling**: Check for `InvalidArgumentException`.

4. **Create two buckets**:
    - Create a regular S3 bucket with the normal S3 Client.
    - Create a directory bucket *also using the normal S3 Client*.
    - **Exception Handling**: Check for `BucketAlreadyExists`.

5. **Create an object and copy it over**:
    - Create a basic object with text in the normal S3 bucket with the normal client.
    - Use CopyObject to copy the object from the normal bucket to the directory bucket, using the S3 Express client.
    - **Exception Handling**: No relevant unique errors for this operation.

6. **Demonstrate performance difference**:
    - Use GetObject to download the object X (1000) times using the S3 Express client. Measure how long it takes. If possible in the language being used, run these in parallel.
    - Use GetObject to download the object X (1000) times using the normal S3 client. Measure how long it takes. If possible in the language being used, run these in parallel.
    - **Exception Handling**: Check for `NoSuchKey`.

7. **Populate the buckets to show the lexicographical difference**:
    - In both buckets, add:
        - `other/objectkey`, `alt/objectkey`, `other/alt/objectkey`
    - Use `ListObjects` for each bucket and show the difference in ordering.
    - **Exception Handling**: Check for `NoSuchBucket`.

8. **Prompt the user to see if they want to clean up the resources**:
    - **Description**: If the user chose to do so, clean up relevant resources. Let the user know if any resources could not be deleted.
    - **Exception Handling**: Any relevant errors related to resource deletion.

### Program execution
The following shows the output of the Amazon S3 Batch program in the console. 

```
--------------------------------------
Welcome to the Amazon S3 Express Basics demo using PHP!
--------------------------------------
Let's get started! First, please note that S3 Express One Zone works best when working within the AWS infrastructure,
specifically when working in the same Availability Zone. To see the best results in this example, and when you implement
Directory buckets into your infrastructure, it is best to put your Compute resources in the same AZ as your Directory
bucket.

1. First, we'll set up a new VPC and VPC Endpoint if this program is running in an EC2 instance in the same AZ as your Directory buckets will be.
Skipping the VPC setup. Don't forget to use this in production!

2. Policies, users, and roles with CDK.
Now, we'll set up some policies, roles, and a user. This user will only have permissions to do S3 Express One Zone actions.

3. Create an additional client using the credentials with S3 Express permissions.
This client is created with the credentials associated with the user account with the S3 Express policy attached, so it can perform S3 Express operations.
All the roles and policies were created an attached to the user. Then, a new S3 Client and Service were created using that user's credentials.
We can now use this client to make calls to S3 Express operations. Keeping permissions in mind (and adhering to least-privilege) is crucial to S3 Express.

3. Create two buckets.
Now we will create a Directory bucket, which is the linchpin of the S3 Express One Zone service.
Directory buckets behave in different ways from regular S3 buckets, which we will explore here.
We'll also create a normal bucket, put an object into the normal bucket, and copy it over to the Directory bucket.
Now, let's create the actual Directory bucket, as well as a regular bucket.
Great! Both buckets were created.

5. Create an object and copy it over.
We'll create a basic object consisting of some text and upload it to the normal bucket.
Next, we'll copy the object into the Directory bucket using the regular client.
This works fine, because Copy operations are not restricted for Directory buckets.
It worked! It's important to remember the user permissions when interacting with Directory buckets.
Instead of validating permissions on every call as normal buckets do, Directory buckets utilize the user credentials and session token to validate.
This allows for much faster connection speeds on every call. For single calls, this is low, but for many concurrent calls, this adds up to a lot of time saved.

6. Demonstrate performance difference.
Now, let's do a performance test. We'll download the same object from each bucket 1000 times and compare the total time needed. Note: the performance difference will be much more pronounced if this example is run in an EC2 instance in the same AZ as the bucket.
The directory bucket took 2464126625 nanoseconds, while the normal bucket took 1989507125.
That's a difference of -474619500 nanoseconds, or -0.4746195 seconds.

7. Populate the buckets to show the lexicographical difference.
Now let's explore how Directory buckets store objects in a different manner to regular buckets.
The key is in the name "Directory!"
Where regular buckets store their key/value pairs in a flat manner, Directory buckets use actual directories/folders.
This allows for more rapid indexing, traversing, and therefore retrieval times!
The more segmented your bucket is, with lots of directories, sub-directories, and objects, the more efficient it becomes.
This structural difference also causes ListObjects to behave differently, which can cause unexpected results.
Let's add a few more objects with layered directories as see how the output of ListObjects changes.
Directory bucket content
other/basic-text-object
other/alt/basic-text-object
basic-text-object
alt/basic-text-object

Normal bucket content
alt/basic-text-object
basic-text-object
other/alt/basic-text-object
other/basic-text-object
Notice how the normal bucket lists objects in lexicographical order, while the directory bucket does not. This is because the normal bucket considers the whole "key" to be the object identifies, while the directory bucket actually creates directories and uses the object "key" as a path to the object.

That's it for our tour of the basic operations for S3 Express One Zone.

```

## SOS Tags

The following table describes the metadata used in this SDK Getting Started Scenario.


| action                         | metadata file                  | metadata key                             |
|--------------------------------|--------------------------------|------------------------------------------|
| `CreateVpc`                    | ec2_metadata.yml               | ec2_CreateVpc                            |
| `DescribeRouteTables`          | ec2_metadata.yml               | ec2_DescribeRouteTables                  |
| `CreateVpcEndpoint`            | ec2_metadata.yml               | ec2_CreateVpcEndpoint                    |
| `CreateBucket`                 | s3_metadata.yml                | s3_CreateBucket                          |
| `CopyObject`                   | s3_metadata.yml                | s3_CopyObject                            |
| `GetObject`                    | s3_metadata.yml                | s3_GetObject                             |
| `PutObject`                    | s3_metadata.yml                | s3_PutObject                             |
| `ListObjects`                  | s3_metadata.yml                | s3_ListObjectsV2                         |
| `DeleteObject`                 | s3_metadata.yml                | s3_DeleteObject                          |
| `DeleteBucket`                 | s3_metadata.yml                | s3_DeleteBucket                          |
| `DeleteVpcEndpoint`            | ec2_metadata.yml               | ec2_DeleteVpcEndpoint                    |
| `DeleteVpc`                    | ec2_metadata.yml               | ec2_DeleteVpc                            |
| ------------------------------ | ------------------------------ | ---------------------------------------- |
