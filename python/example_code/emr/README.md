# Amazon EMR cluster and command examples

## Purpose

Shows how to use the AWS SDK for Python (Boto3) with Amazon EMR to create
and manage clusters and job steps. Learn to accomplish the following tasks:

* Create a short-lived cluster that estimates the value of pi using Apache Spark to 
  parallelize a large number of calculations, writes output to Amazon S3, and
  terminates itself after completing the job.
* Create a long-lived cluster that uses Apache Spark to query historical Amazon 
  review data to discover the top products in various categories with certain 
  keywords in their product titles.
* Create security roles and groups to let Amazon EMR manage cluster instances and
  to let the instances access additional AWS resources.
* Run commands on cluster instances, such as EMRFS configuration and shell scripts
  to install additional libraries. 
* Query clusters for status and terminate them using the API.

*Amazon EMR is a web service that makes it easy to process vast amounts of data 
efficiently using Apache Hadoop and services offered by Amazon Web Services.*

## Code examples

**Common scenarios**

* [Create a long-lived cluster and run several steps](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_usage_demo.py)
(`demo_long_lived_cluster`)
* [Create a short-lived cluster and run a step](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_usage_demo.py)
(`demo_short_lived_cluster`)
* [Run an EMRFS command on a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emrfs_step.py)
* [Run a shell script to install libraries on instances](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/install_libraries.py)

**API examples**

* [Add steps to a job flow](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`AddJobFlowSteps`)
* [Describe a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`DescribeCluster`)
* [Describe a step](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`DescribeStep`)
* [List steps for a cluster](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`ListSteps`)
* [Run a job flow](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`RunJobFlow`)
* [Terminate job flows](https://github.com/awsdocs/aws-doc-sdk-examples/blob/master/python/example_code/emr/emr_basics.py)
(`TerminateJobFlows`)

## ⚠ Important

- As an AWS best practice, grant this code least privilege, or only the 
  permissions required to perform a task. For more information, see 
  [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) 
  in the *AWS Identity and Access Management 
  User Guide*.
- This code has not been tested in all AWS Regions. Some AWS services are 
  available only in specific Regions. For more information, see the 
  [AWS Region Table](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services/)
  on the AWS website.
- Running this code might result in charges to your AWS account.

## Running the code

### Prerequisites

- You must have an AWS account, and have your default credentials and AWS Region
  configured as described in the [AWS Tools and SDKs Shared Configuration and
  Credentials Reference Guide](https://docs.aws.amazon.com/credref/latest/refdocs/creds-config-files.html).
- Python 3.7 or later
- Boto3 1.11.10 or later
- PyTest 5.3.5 or later (to run unit tests)
- PySpark 3.0.0 or later (optional, to run Spark scripts locally)

### Command

These examples show how to use Amazon EMR clusters and how to run commands
on running cluster instances.

**Short-lived cluster**

To create a cluster that runs a job step that calculates pi and automatically
terminates itself upon job completion, run the following command at a command prompt:  

```
python emr_usage_demo.py short-lived
```  

**Long-lived cluster**

To create a cluster that runs several job steps to query Amazon review data for the
top products in specific categories, run the following command at a command prompt:

```
python emr_usage_demo.py long-lived
```

**EMRFS configuration**

To run an EMR File System (EMRFS) command to synchronize sample metadata to a cluster 
instance, run the following command at a command prompt:

```
python emrfs_step.py 
``` 

This example requires that you have a running cluster with EMRFS consistent view 
enabled and that you have previously created metadata with the default name 
of *EmrFSMetadata*.

**Install libraries**

To install additional libraries on running cluster instances, run the following
command at a command prompt:

```
python install_libraries.py CLUSTER_ID SHELL_SCRIPT_PATH
``` 

This example is intended to be run as part of the tutorial in 
[Installing Additional Kernels and Libraries](https://docs.aws.amazon.com/emr/latest/ReleaseGuide/emr-jupyterhub-install-kernels-libs.html) 
and requires that the cluster specified by *CLUSTER_ID* is set up to work with
AWS Systems Manager and that you have previously uploaded a shell script
to the Amazon S3 location specified by *SHELL_SCRIPT_PATH*. 

### Example structure

The examples are divided into the following files:

**emr_basics.py**

Shows how to create and manage clusters and job steps.

**emr_usage_demo.py**

Shows how to create and run job steps on both short-lived and long-lived clusters.

* Creates an Amazon S3 bucket and uploads a job script.
* Creates AWS Identity and Access Management (IAM) roles used by the demo. 
* Creates Amazon Elastic Compute Cloud (Amazon EC2) security groups used by the demo.
* Creates short-lived and long-lived clusters and runs job steps on them.
* Terminates clusters and cleans up all resources.  

**pyspark_estimate_pi.py**

Shows how to write a job step that uses Apache Spark to estimate the value of pi by 
performing a large number of parallelized calculations on cluster instances. Results
are written both to Amazon EMR logs and to an Amazon S3 bucket. 

**pyspark_top_product_keyword.py**

Shows how to write a job step that uses Apache Spark to read data from the
[Amazon Customer Reviews Dataset](https://s3.amazonaws.com/amazon-reviews-pds/readme.html)
and query the data for top-rated products in specific categories that contain 
keywords in their product titles. Results are written to an Amazon S3 bucket. 

**emrfs_step.py**

Shows how to run EMRFS commands on a running cluster instance to configure EMRFS.

**install_libraries.py**

Shows how to run a custom shell command on a running cluster instance to install
additional libraries.

## Running the tests

The unit tests in this module use the botocore Stubber. This captures requests before 
they are sent to AWS, and returns a mocked response. To run all of the tests, 
run the following in your [GitHub root]/python/example_code/emr 
folder.

```    
python -m pytest
```

## Additional information

- [Boto3 Amazon EMR service reference](https://boto3.amazonaws.com/v1/documentation/api/latest/reference/services/emr.html)
- [Amazon EMR documentation](https://docs.aws.amazon.com/emr/?id=docs_gateway)
- [Amazon Customer Reviews Dataset](https://s3.amazonaws.com/amazon-reviews-pds/readme.html)

---
Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.

SPDX-License-Identifier: Apache-2.0
