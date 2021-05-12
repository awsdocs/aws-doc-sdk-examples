# TypeScript environment for AWS Simple Storage Service (S3) examples
Amazon S3 is an object storage service that offers industry-leading scalability, data availability, security, and performance.

## Code examples
This is a workspace where you can find the following AWS SDK for JavaScript version 3 (v3) Amazon S3 examples: 

- [Create and upload objects](src/s3_create_and_upload_objects.js)
- [Create a bucket](src/s3_createbucket.js)
- [Delete all objects from a bucket](src/s3_delete_all_objects.js)
- [Delete multiple objects from a bucket](src/s3_delete_multiple_objects.js)
- [Delete an object from a bucket](src/delete_objects.js)
- [Delete a bucket policy](src/s3_deletebucketpolicy.js)
- [Delete a bucket website policy](src/s3_deletebucketwebsite.js)
- [Create a presigned URL to get objects](src/s3_get_presignedURL.js)
- [Get a bucket Access Control List (ACL)](src/s3_getbucketacl.js)
- [Get a bucket policy](src/s3_getbucketpolicy.js)
- [Get a bucket website policy](src/s3_getbucketwebsite.js)
- [Get a bucket CORS policy](src/s3_getcors.js)
- [Get objects from a bucket](src/s3_getobjects.js)
- [Get more than 1000 objects from a bucket](src/s3_list1000plusobjects.js)
- [List buckets](src/s3_listbuckets.js)
- [List objects](src/s3_listobjects.js)
- [Multipart object upload](src/s3_multipartupload.js)
- [Create a presigned URL to get objects](src/s3_put_presignedURL.js)
- [Set a bucket ACL](src/s3_putbucketacl.js)
- [Set a bucket policy](src/s3_putbucketpolicy.js)
- [Set a bucket website policy](src/s3_setbucketwebsite.js)
- [Set a bucket CORS policy](src/s3_setcors.js)
- [Upload objects to a bucket](src/s3_upload_object.js)

**Note**: All code examples are written in ECMAscript 6 (ES6). For guidelines on converting to CommonJS, see 
[JavaScript ES6/CommonJS syntax](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/sdk-examples-javascript-syntax.html).

## Getting started

1. Clone the [AWS SDK Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for instructions.

2. Install the dependencies listed in the package.json.

```
npm install ts-node -g # If using JavaScript, enter 'npm install node -g' instead
cd javascriptv3/example_code/s3
npm install
```
4. In your text editor, update user variables specified in the ```Inputs``` section of the sample file.

5. Run sample code:
```
cd src
node [example name].js
```

## Unit tests
For more information see, the [README](../README.rst).

## Resources
- [AWS SDK for JavaScript v3 repo](https://github.com/aws/aws-sdk-js-v3)
- [AWS SDK for JavaScript v3 Developer Guide](https://docs.aws.amazon.com/sdk-for-javascript/v3/developer-guide/s3-examples.html)
- [AWS SDK for JavaScript v3 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/v3/latest/clients/client-s3/index.html) 
