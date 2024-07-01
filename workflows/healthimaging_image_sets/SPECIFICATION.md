# HealthImaging Image Sets And Frames Specification

This document contains the technical specifications for *HealthImaging image sets and frames*,
a workflow scenario that showcases AWS services and SDKs. It is primarily intended for the AWS code
examples team to use while developing this example in additional languages. 

This document explains the following:

- Deploying AWS resources with a CloudFormation stack.
- Flow of the demo, importing DICOM files, and downloading and decoding image frames. 
- Destroying the AWS resources at the end of the example.

For an introduction to *HealthImaging image sets and frames*, see the [README.md](README.md).

Note: HealthImaging is often referenced as MedicalImaging or medical-imaging in the APIs.

---

### Table of contents

- [Architecture](#architecture)
- [User actions](#user-actions)
- [Demo](#demo)
- [Other material](#other-material)

---

## Architecture

A CloudFormation template is used to create and destroy the following resources.

1. A HealthImaging data store.
2. Two S3 buckets.
3. An IAM role with permissions for a DICOM import job.

Before deleting the stack, the image sets in the datastore must be deleted by calling DeleteImageSet in the code.


The template is stored at [resources/cfn_template.yaml](resources/cfn_template.yaml).



#### Instructions for generating a new template file if necessary.

If changes are required to the template, the template can be re-generated from an updated CDK script, "resources/cdk/healthimaging_imagesets_and_frames/cdk.json.
After the template file has been generated, it must be edited manually to implement object deletion for the S3 bucket. A lambda function is created by the CDK script and output to "cdk.out/asset.???/index.js". This lambda function must be copied
to the CloudFormation template. 

Replace

```
CustomS3AutoDeleteObjectsCustomResourceProviderHandler9D90184F:
Type: AWS::Lambda::Function
Properties:
Code:
S3Bucket:
Fn::Sub: cdk-hnb659fds-assets-${AWS::AccountId}-${AWS::Region}
S3Key: b7f33614a69548d6bafe224d751a7ef238cde19097415e553fe8b63a4c8fd8a6.zip
```

With

```
CustomS3AutoDeleteObjectsCustomResourceProviderHandler9D90184F:
Type: AWS::Lambda::Function
Properties:
Code:
ZipFile: |
"use strict";var C=Object.create,i=Object.defineProperty,I=Object.getOwnPropertyDescriptor,w=Object.getOwnPropertyNames,P=Object.getPrototypeOf,A=Object.prototype.hasOwnProperty,L=(e,t)=>{for(var o in t)i(e,o,{get:t[o],enumerable:!0})},d=(e,t,o,r)=>{if(t&&typeof t=="object"||typeof t=="function")for(let s of w(t))!A.call(e,s)&&s!==o&&i(e,s,{get:()=>t[s],enumerable:!(r=I(t,s))||r.enumerable});return e},l=(e,t,o)=>(o=e!=null?C(P(e)):{},d(t||!e||!e.__esModule?i(o,"default",{value:e,enumerable:!0}):o,e)),k=e=>d(i({},"__esModule",{value:!0}),e),U={};L(U,{autoDeleteHandler:()=>S,handler:()=>_}),module.exports=k(U);var h=require("@aws-sdk/client-s3"),y=l(require("https")),m=l(require("url")),a={sendHttpRequest:T,log:b,includeStackTraces:!0,userHandlerIndex:"./index"},p="AWSCDK::CustomResourceProviderFramework::CREATE_FAILED",B="AWSCDK::CustomResourceProviderFramework::MISSING_PHYSICAL_ID";function R(e){return async(t,o)=>{let r={...t,ResponseURL:"..."};if(a.log(JSON.stringify(r,void 0,2)),t.RequestType==="Delete"&&t.PhysicalResourceId===p){a.log("ignoring DELETE event caused by a failed CREATE event"),await u("SUCCESS",t);return}try{let s=await e(r,o),n=D(t,s);await u("SUCCESS",n)}catch(s){let n={...t,Reason:a.includeStackTraces?s.stack:s.message};n.PhysicalResourceId||(t.RequestType==="Create"?(a.log("CREATE failed, responding with a marker physical resource id so that the subsequent DELETE will be ignored"),n.PhysicalResourceId=p):a.log(`ERROR: Malformed event. "PhysicalResourceId" is required: ${JSON.stringify(t)}`)),await u("FAILED",n)}}}function D(e,t={}){let o=t.PhysicalResourceId??e.PhysicalResourceId??e.RequestId;if(e.RequestType==="Delete"&&o!==e.PhysicalResourceId)throw new Error(`DELETE: cannot change the physical resource ID from "${e.PhysicalResourceId}" to "${t.PhysicalResourceId}" during deletion`);return{...e,...t,PhysicalResourceId:o}}async function u(e,t){let o={Status:e,Reason:t.Reason??e,StackId:t.StackId,RequestId:t.RequestId,PhysicalResourceId:t.PhysicalResourceId||B,LogicalResourceId:t.LogicalResourceId,NoEcho:t.NoEcho,Data:t.Data};a.log("submit response to cloudformation",o);let r=JSON.stringify(o),s=m.parse(t.ResponseURL),n={hostname:s.hostname,path:s.path,method:"PUT",headers:{"content-type":"","content-length":Buffer.byteLength(r,"utf8")}};await O({attempts:5,sleep:1e3},a.sendHttpRequest)(n,r)}async function T(e,t){return new Promise((o,r)=>{try{let s=y.request(e,n=>o());s.on("error",r),s.write(t),s.end()}catch(s){r(s)}})}function b(e,...t){console.log(e,...t)}function O(e,t){return async(...o)=>{let r=e.attempts,s=e.sleep;for(;;)try{return await t(...o)}catch(n){if(r--<=0)throw n;await x(Math.floor(Math.random()*s)),s*=2}}}async function x(e){return new Promise(t=>setTimeout(t,e))}var g="aws-cdk:auto-delete-objects",H=JSON.stringify({Version:"2012-10-17",Statement:[]}),c=new h.S3({}),_=R(S);async function S(e){switch(e.RequestType){case"Create":return;case"Update":return F(e);case"Delete":return f(e.ResourceProperties?.BucketName)}}async function F(e){let t=e,o=t.OldResourceProperties?.BucketName,r=t.ResourceProperties?.BucketName;if(r!=null&&o!=null&&r!==o)return f(o)}async function N(e){try{let t=(await c.getBucketPolicy({Bucket:e}))?.Policy??H,o=JSON.parse(t);o.Statement.push({Principal:"*",Effect:"Deny",Action:["s3:PutObject"],Resource:[`arn:aws:s3:::${e}/*`]}),await c.putBucketPolicy({Bucket:e,Policy:JSON.stringify(o)})}catch(t){if(t.name==="NoSuchBucket")throw t;console.log(`Could not set new object deny policy on bucket '${e}' prior to deletion.`)}}async function E(e){let t=await c.listObjectVersions({Bucket:e}),o=[...t.Versions??[],...t.DeleteMarkers??[]];if(o.length===0)return;let r=o.map(s=>({Key:s.Key,VersionId:s.VersionId}));await c.deleteObjects({Bucket:e,Delete:{Objects:r}}),t?.IsTruncated&&await E(e)}async function f(e){if(!e)throw new Error("No BucketName was provided.");try{if(!await W(e)){console.log(`Bucket does not have '${g}' tag, skipping cleaning.`);return}await N(e),await E(e)}catch(t){if(t.name==="NoSuchBucket"){console.log(`Bucket '${e}' does not exist.`);return}throw t}}async function W(e){return(await c.getBucketTagging({Bucket:e})).TagSet?.some(o=>o.Key===g&&o.Value==="true")}
```

---


## User actions

This example runs as a console application that prompts the user for the name of resources and
the particular DICOM files to import:

1. Enter a stack name.
2. Enter a data store name. 
3. Select the DICOM files to import.
4. Choose whether to delete the stack.

For more detail on how this is implemented, see [Demo](#demo).

---

## Demo

The reference implementation for this example is in C++. You can find it in
[cpp/example_code/medical-imaging/imaging_set_and_frames_workflow](../../cpp/example_code/medical-imaging/imaging_set_and_frames_workflow).

### Application Output

```
***************************************************************************************

Welcome to the AWS HealthImaging working with image sets and frames workflow.

***************************************************************************************

This workflow will import DICOM files into a HealthImaging data store.
DICOM® — Digital Imaging and Communications in Medicine — is the international
standard for medical images and related information.

The workflow will then download all the image frames created during the DICOM import and decode
the image frames from their HTJ2K format to a bitmap format.
The bitmaps will then be validated with a checksum to ensure they are correct.

This workflow requires a number of AWS resources to run.

It requires a HealthImaging data store, an Amazon Simple Storage Service (Amazon S3)
bucket for uploaded DICOM files, an Amazon S3 bucket for the output of a DICOM import, and
an AWS Identity and Access Management (IAM) role for importing the DICOM files into
the data store.

These resources can be created for you using an AWS CloudFormation stack.

Would you like to let this workflow create the resources for you? (y/n) y
Enter a name for the AWS CloudFormation stack to create. workflow-stack
Enter a name for the HealthImaging datastore to create. workflow-datastore
Stack creation initiated.
Waiting for the stack to be created.
Stack creation completed.
The following resources have been created.
A HealthImaging datastore with ID: 12345678901234567890123456789012.
An Amazon S3 input bucket named: workflow-stack-docexampledicominbucket1234567.
An Amazon S3 output bucket named: workflow-stack-docexampledicomoutbucket1234567.
An IAM role with the ARN: arn:aws:iam::12345678901:role/workflow-stack-docexampleimportrole1234567.
Enter return to continue.

***************************************************************************************

This workflow uses DICOM files from the National Cancer Institute Imaging Data
Commons (IDC) Collections.
Here is the link to their website.
https://registry.opendata.aws/nci-imaging-data-commons/
We will use DICOM files stored in an S3 bucket managed by the IDC.
First one of the DICOM folders in the IDC collection must be copied to your
input S3 bucket.
You have the choice of one of the following 4 folders to copy.
1 - CT of chest (2 images)
2 - CT of pelvis (57 images)
3 - MRI of head (192 images)
4 - MRI of breast (92 images)
Choose DICOM files to import: 3
The files in the directory '0002d261-8a5d-4e63-8e2e-0cbfac87b904' in the bucket 'idc-open-data' will be copied 
to the folder 'input/12345678901234567890123456789012' in the bucket 'workflow-stack-docexampledicominbucket1234567'.
Enter return to start the copy.
192 DICOM files were copied.
```
The application uses S3 CopyObject to copy the objects. Attempts to import directly from the IDC S3 bucket into the HealthImaging data store resulted in access violations.
So the files are first copied to the user's S3 bucket, then imported into the data store.

In the C++ application, these copies were done asynchronously, which was much faster.

```

***************************************************************************************

Now the DICOM images will be imported into the datastore with ID '12345678901234567890123456789012'
Enter return to start the DICOM import job.
DICOM import job started with job ID 12345678901234567890123456789012.
DICOM import job status: IN_PROGRESS
DICOM import job status: IN_PROGRESS
DICOM import job status: IN_PROGRESS
DICOM import job status: IN_PROGRESS
DICOM import job status: IN_PROGRESS
DICOM import job status: COMPLETED
DICOM import job completed.
The DICOM files were successfully imported. The import job ID is '12345678901234567890123456789012'.
Information about the import job, including the IDs of the created image sets,
is located in a file named 'job-output-manifest.json'. This file is located in a
folder specified by the import job's 'outputS3Uri'.
The 'outputS3Uri' is retrieved by calling the 'GetDICOMImportJob' action.

***************************************************************************************

The image set IDs will be retrieved by downloading 'job-output-manifest.json' file from the output S3 bucket.
Enter return to continue.
The image sets created by this import job are: 
Image set: 12345678901234567890123456789012
If you would like information about how HealthImaging organizes image sets,
go to the following link.
https://docs.aws.amazon.com/healthimaging/latest/devguide/understanding-image-sets.html
Enter return to continue.
Enter return to continue.

```

The application uses S3 GetObject to retrieve the 'job-output-manifest.json'. It uses a jmesPath expression to get the image set IDs.

"jobSummary.imageSetsSummary[].imageSetId"

https://jmespath.org/specification.html

```

***************************************************************************************

Next this workflow will download all the image frames created in this import job.
The IDs of all the image frames in an image set are stored in the image set metadata.
The image set metadata will be downloaded and parsed for the image frame IDs.
Enter return to continue.
192 image frames were created by this import job.

```

The application uses GetImageSetMetadata to retrieve the metadata as gzipped JSON data. JmesPath is then used to
retrieve the image frame IDs.
Other values which are or could be used for image display are also retrieved, as an aid to customers.

- An image set can have one or more image instances.
- An image instance can have one or more image frames.


- JMESPath query for array of image instances. `Study.Series.*.Instances[].*[]`
- JMESPath query on image instance for the rescale slope. `DICOM.RescaleSlope`
- JMESPath query on image instance for the rescale intercept. `DICOM.RescaleIntercept`
- JMESPath query on image instance for array of image frames. `ImageFrames[][]`
- JMESPath query on image frame for max pixel value. `MaxPixelValue`
- JMESPath query on image frame for min pixel value. `MinPixelValue`
- JMESPath query on image frame for checksum of max resolution image. `max_by(PixelDataChecksumFromBaseToFullResolution, &Width).Checksum`


The image frames are decoded to a bitmap format and then the checksum is calculated and compared with the checksum
retrieved from the metadata.

```
***************************************************************************************

The image frames are encoded in the HTJ2K format. This example will convert
the image frames to bitmaps. The decoded images will be verified using
a CRC32 checksum retrieved from the image set metadata,
The OpenJPEG open-source library will be used for the conversion.
The following link contains information about HTJ2K decoding libraries.
https://docs.aws.amazon.com/healthimaging/latest/devguide/reference-htj2k.html
Enter return to download and convert the images.
192 image files were downloaded.
The image files were successfully decoded and validated.
The HTJ2K image files are located in the directory
'output/import_job_12345678901234567890123456789012' in the working directory
of this application.

***************************************************************************************

This concludes this workflow.

***************************************************************************************

Would you like to delete the stack workflow-stack? (y/n)y
Deleting the image sets in the stack.
Successfully deleted image set 12345678901234567890123456789012 from data store 12345678901234567890123456789012

***************************************************************************************

Deleting the stack.
Stack deletion initiated.
Stack deletion completed.
```

## Hello Service


* medical-imaging.ListDatastores. List the data stores. 


Output:

```
4 HealthImaging data stores were retrieved.
  Datastore: cpp-test
  Datastore ID: 12345678901234567890123456789012
  Datastore: java_auto_test
  Datastore ID: 12345678901234567890123456789012
  Datastore: java_test_1414
  Datastore ID: 12345678901234567890123456789012
  Datastore: java_test_1896
  Datastore ID: 12345678901234567890123456789012

```

---

## Actions

**HealthImaging(MedicalImaging)**

* `StartDICOMImportJob`
* `GetDICOMImportJob`
* `GetImageSetMetadata`
* `GetImageFrame`
* `SearchImageSets`
* `DeleteImageSet`


**S3**

* `ListObjects`
* `CopyObject`
* `GetObject`
  
**CloudFormation**

* `CreateStack`
* `DeleteStack`
* `DescribeStack` or a waiter when available.


---

## Metadata

**medical-imaging_metadata.yaml**

* medical-imaging_Hello
* medical-imaging_StartDICOMImportJob
* medical-imaging_GetDICOMImportJob
* medical-imaging_GetImageSetMetadata
* medical-imaging_GetImageFrame:
* medical-imaging_SearchImageSets
* medical-imaging_DeleteImageSet
* medical-imaging_Scenario_image_sets_and_frames


---

# Other material

If technical details are not what you seek, try these instead:

* [README](README.md)


