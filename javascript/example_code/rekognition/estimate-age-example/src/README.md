# Amazon Rekognition Detect Faces example
Amazon Rekognition makes it easy to add image and video analysis to your applications using  AWS SDK for JavaScript version 2 (v2).

This example demonstrates how to estimate the ages of faces in an photo.

# Running the example

1. Open the AWS CloudFormation Console, choose the stack, and choose the  **Resources** tab. 

2. Copy the **Physical ID** of the **CognitoDefaultUnauthenticatedRole**.

3. In the *estimate-age.html* file, replace **IDENTITY_POOL_ID** with the **Physical ID** of the **CognitoDefaultUnauthenticatedRole**.

4. Also in the *estimate-age.html* file, replace **REGION** with your AWS Region.

5. Open *estimate-age.html* in your browser, and follow the instructions onscreen.


## Resources
- [AWS SDK for JavaScript v2 API Reference Guide](https://docs.aws.amazon.com/AWSJavaScriptSDK/latest/AWS/Rekognition.html)
- [Amazon Rekognition Developer Guide - AWS SDK for JavaScript (v2) version of this example](https://docs.aws.amazon.com/rekognition/latest/dg/image-bytes-javascript.html)
