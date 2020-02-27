# Amazon S3 Java Readme

A README that discusses how to run and test the Java Amazon Simple Storage Service (Amazon S3) code examples.

## Running the Amazon S3 Java files

**IMPORTANT**

The Java examples perform AWS operations for the account and region for which you've specified credentials, and you may incur AWS service charges by running them. Please visit the `AWS Pricing 

<https://aws.amazon.com/pricing/>`_ page for details about the charges you can expect for a given service and operation.

Some of these examples perform *destructive* operations on AWS resources, such as deleting an Amazon S3 bucket. **Be very careful** when running an operation that 
may delete or modify AWS resources in your account. It's best to create separate test-only resources when experimenting with these examples.

To run these examples, you'll need the AWS SDK for Java libraries in your ``CLASSPATH``::

export CLASSPATH=target/sdk-s3-examples-1.0.jar:/path/to/aws-java-sdk/<jar-file-name>.jar

Where ``/path/to/aws-java-sdk/<jar-file-name>.jar`` is the path to where you extracted or built the AWS Java SDK jar.

Once you set the ``CLASSPATH``, you can run a particular example like this::

	java com.example.s3.S3BucketOps

For systems with bash support

-----------------------------

As an alternative to setting the ``CLASSPATH`` and specifying the full namespace of the class to run, we've included a ``bash`` script, ``run_example.sh``, that you can use on Linux, Unix or OS X (or on Windows by using `Cygwin <https://www.cygwin.com/>`_, `MingW <http://www.mingw.org/>`_, or

`Bash on Ubuntu on Windows <https://msdn.microsoft.com/en-us/commandline/wsl/about>`_).

You can execute ``run_example.sh`` as shown::

	./run_example.sh S3BucketOps

This will run the `S3BucketOps <example_code/s3/src/main/java/com/example/s3/S3BucketOps.java>`_

example (assuming that you've built the examples first!).

If the example requires arguments, pass the argument list in quotes::

	./run_example.sh S3BucketOps "<arg1> <arg2> <arg3>"

.. _maven: https://maven.apache.org/

.. _javasdk: https://aws.amazon.com/sdk-for-java/

.. _javasdk-docs: http://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/

 ## Testing the Amazon S3 Java files

You can test the Amazon S3 Java code examples by running a test file named **AmazonS3ServiceIntegrationTest**. This file uses JUnit 5 to run the JUnit tests and is located in the **src/test/java** folder. For more information, see [https://junit.org/junit5/](https://junit.org/junit5/) .

Before running the Amazon S3 tests, you must define values in the **config.properties** file located in the resources folder. This file contains values that are required to execute the JUnit tests. For example, you define an object key required for various tests. If you do not define all values, the JUnit tests fail.

After you define the values, you can execute the JUnit tests from a Java IDE, such as IntelliJ, or from the command line by using Maven. As each test is executed, you can view messages that inform you if the various tests succeed or fail. For example, the following message informs you that test 3 passed and successfully placed an object into an Amazon S3 bucket:

**Running Amazon S3 Test 3**

**The file book.pdf has been successfully placed into buckettestfeb7**

**WARNING** : _Running these JUnit tests manipulate real Amazon S3 resources and may incur charges on your account._

 ### Properties file

Define these values that are required to run the JUnit tests:

- **bucketName** - the name of the bucket to use. For example, **buckettestfeb7**.

- **objectKey** – the name of the object to use. For example, **book.pdf**.

- **objectPath** – the path where the object is located. For example, **/AWS/book2.pdf**.

- **toBucket** - the name of another bucket in your account. For example, **febbucket101**.

- **policyText** – the location where a text file is located that defines a policy. For example, **/AWS/bucketpolicy.txt**

- **id**  - the ID of the user whom owns the bucket. You can get this values from the AWS Console. This value appears as a GUID value.

- **access** - the access level to test an ACL with. You can specify one of these values: **FULL_CONTROL** , **READ** , **WRITE** , **READ_ACP** , **WRITE_ACP**.

###  Sample policy text

For the purpose of the JUnit tests, you can use the following example content for the policy text. Be sure to specify the correct ARN bucket name in the **Resource** section; otherwise, your test is not successful.

	{
	"Version": "2012-10-17",
	"Id": "S3PolicyId1",
	"Statement": [
	{
	"Sid": "IPAllow",
	"Effect": "Deny",
	"Principal": "*",
	"Action": "s3:*",
	"Resource": "arn:aws:s3:::examplebucket/*",
	"Condition": {
	"NotIpAddress": {"aws:SourceIp": "54.240.143.0/24"}
	}
	}
	]
	}

### Command line

To execute the JUnit tests from the command line, you can use the following command:

		mvn test
You will see output from the JUNIT tests, as shown here:

	[INFO] -------------------------------------------------------
	[INFO]  T E S T S
	[INFO] -------------------------------------------------------
	[INFO] Running AWSS3ServiceIntegrationTest
	Running Amazon S3 Test 1
	Running Amazon S3 Test 2
	...Done!
	[INFO] Results:
	[INFO]
	[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
	[INFO]
	INFO] --------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO]--------------------------------------------
	[INFO] Total time:  12.003 s
	[INFO] Finished at: 2020-02-10T14:25:08-05:00
	[INFO] --------------------------------------------

### Unsuccessful tests

If you do not define the correct values in the properties file, your JUnit tests are not successful. You will see an error message such as below. You need to double check the values that you set in the properties file and run the tests again. Also, ensure that you specify the correct resource name in the **Sample policy** text file as well as the correct owner ID value (you can retrieve this value from the AWS Console).

	[INFO]
	[INFO] --------------------------------------
	[INFO] BUILD FAILURE
	[INFO] --------------------------------------
	[INFO] Total time:  19.038 s
	[INFO] Finished at: 2020-02-10T14:41:51-05:00
	[INFO] ---------------------------------------
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.22.1:test (default-test) on project S3J2Project:  There are test failures.
	[ERROR];
