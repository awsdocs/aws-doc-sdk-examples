#  Creating an Amazon Rekogition to detect objects in videos

You can create an application that uses Amazon Rekognition to objects in videos located in an Amazon Simple Storage Service (Amazon S3) bucket. 

In addition, the app sens the user results by email using the Amazon Simple Email (Amazon SES) service.

This tutorial shows you how to use the AWS SDK for JavaScript V3 API to invoke these AWS services: 

- Amazon S3 service
- Amazon Rekognition service
- Amazon SES service

**Cost to complete**: The AWS services included in this document are included in the [AWS Free Tier](https://aws.amazon.com/free/?all-free-tier.sort-by=item.additionalFields.SortRank&all-free-tier.sort-order=asc).

**Note**: Be sure to terminate all of the resources after you have completed this tutorial to ensure that you are not charged.

## Prerequisites

To build this cross-service example, you need the following:

* An AWS account. For more information see [AWS SDKs and Tools Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/overview.html).
* A project environment to run this Node JavaScript example, and install the required AWS SDK for JavaScript and third-party modules.  For instructions, see [Create a Node.js project environment](#create-a-nodejs-project-environment) on this page.
* At least one email address verified on Amazon SES. For instructions, see [Verifying an email address on Amazon SES](#verifying-an-email-address-on-amazon-ses).
* The following AWS resources:
    - An unauthenticated AWS Identity and Access Management (IAM) user role with required permissions (described below).
**Note**: An unauthenticated role enables you to provide permissions to unauthenticated users to use the AWS Services. To create an authenticated role, see [Amazon Cognito Identity Pools (Federated Identities)](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-identity.html).   


## ⚠ Important
* We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*. 
* This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
* Running this code might result in charges to your AWS account. We recommend you destroy the resources when you are finished. For instructions, see [Destroying the resources](#destroying-the-resources).
* This tutorial is written to work with the specific versions defined in the *package.json*. If you change these versions, the tutorial may not work correctly.

## Create the resources
You can create the AWS resources required for this cross-service example using either of the following:
- [The Amazon CloudFormation](#create-the-resources-using-amazon-cloudformation)
- [The AWS Management Console](#create-the-resources-using-the-aws-management-console)

### Create the resources using Amazon CloudFormation
To run the stack using the AWS CLI:

1. Install and configure the AWS CLI following the instructions in the AWS CLI User Guide.

2. Open the AWS Command Console from the *./video-analyzer* folder.

3. Run the following command, replacing *STACK_NAME* with a unique name for the stack.
```
aws cloudformation create-stack --stack-name STACK_NAME --template-body file://setup.yaml --capabilities CAPABILITY_IAM
```
**Important**: The stack name must be unique within an AWS Region and AWS account. You can specify up to 128 characters, and numbers and hyphens are allowed.

4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the **Stacks** page.

![ ](images/readme_images/cloud_formation_stacks.png)

5. Choose the **Resources** tab. The **Physical ID** of the **IDENTITY_POOL_ID** you require for this cross-service example is displayed.

![ ](images/readme_images/cloud_formation_resources_tab.png)

For more information on the create-stack command parameters, see the [AWS CLI Command Reference guide](https://docs.aws.amazon.com/cli/latest/reference/cloudformation/create-stack.html), and the [AWS CloudFormation User Guide](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-cli-creating-stack.html).

### Create the resources using the AWS Management Console

#### Create an unauthenticated user role
1. Open [AWS Cognito in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.
2. Choose **Manage Identity Pools**.
3. Choose **Create new identity pool**.
4. In the **Identity pool name** field, give your identity pool a name.
5. Select the **Enable access to unauthenticated identities** checkbox.
6. Choose **Create Pool**.
7. Choose **Allow**.
8. Take note of the **Identity pool ID**, which is highlighted in red in the **Get AWS Credentials** section.

![ ](images/readme_images/identity_pool_ids.png)

9.Choose **Edit identity pool**.
10. Take note of the name of the role in the **Unauthenticated role** field.

#### Adding permissions to an unauthenticated user role
1. Open [IAM in the AWS Management Console](https://aws.amazon.com/iam/), and open the *Roles* page.
2. Search for the unauthenticated role you just created.
3. Open the role. 
4. Click the down arrow beside the policy name.
5. Choose **Edit Policy**.
6. Choose the **JSON** tab.
7. Delete the existing content, and paste the code below into it.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "lambda:InvokeFunction",
                "mobileanalytics:PutEvents",
                "cognito-sync:*"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "ses:*",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "iam:*",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "s3:*",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "rekognition:*",
            "Resource": "*",
            "Effect": "Allow"
        }
    ]
}
```
8. Choose **Review Policy**.
9. Choose **Save Changes**.   

### Verifying an email address on Amazon SES 
1. Open [AWS SES in the AWS Management Console](https://aws.amazon.com/SES/), and open the **Email Addresses** page.
2. Choose **Verify a New Email Address**.
3. Enter a working email address, and choose **Verify This Email Address**.
4. Open the email in your email application, and verify it.

## Create a Node.js project environment

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment. 
See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for 
instructions.

2. Run the following commands in sequence in the AWS CLI command line to install the AWS service client modules and third-party modules listed in the *package.json*:

```
npm install node -g
cd javascriptv3/example_code/cross-services/video-analyzer
npm install
```
## Building the code
This app runs from the browser, so we create the interface using HTML and CSS. 
The app uses JavaScript to provide basic interactive features, and Node.js to invoke the AWS Services.

### Creating the HTML and CSS
In **index.html**, the **head** section loads the **main.js**, which contains the following JavaScript and Node.js functions used in the app.

**Note**: **main.js** is a bundled file containing all the required JavaScript. You'll create this later in the tutorial.

The remaining code defines the interface features, including a table and buttons.

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head th:fragment="site-head">
    <meta charset="UTF-8" />
    <link rel="icon" href="../public/images/favicon.ico" />
    <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"/>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <link rel="stylesheet" href="../css/styles.css" th:href="@{/css/styles.css}" />
    <link rel="icon" href="../images/favicon.ico" th:href="@{/images/favicon.ico}" />
    <script src="./js/main.js" ></script>
    <html xmlns:th="http://www.thymeleaf.org" >
    <link href="//cdn.datatables.net/1.10.21/css/jquery.dataTables.min.css" rel="stylesheet" type="text/css">
    <script src="//cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
</head>
<body>
<div id="upload">
    <div class="container">
        <h2>AWS Video Analyzer application</h2>
        <p>Upload a video to an Amazon S3 bucket that will be analyzed!</p>
        <input id="videoupload" type="file" name="file" /><br/><br/>
        <button id="addvideo" onclick="uploadVideo()">Add video</button>
        </form>
        <div>
            <br>
            <p>Choose the following button to get information about the video to analyze.</p>
            <button onclick="getVideo()">Show Video</button>
            <table id="myTable" class="display" style="width:100%">
                <thead>
                <tr>
                    <th>Name</th>
                    <th>Owner</th>
                    <th>Date</th>
                    <th>Size</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td id="videoname">No Data</td>
                    <td id ="videoowner">No Data</td>
                    <td id ="videodate">No Data </td>
                    <td id ="videosize">No Data</td>
                </tr>
                </tbody>
                <div id="success3"></div>
            </table>
        </div>
    </div>
</div>
<div id="analyze"  >
    <div class="container">
        <p>You can generate a report that analyzes a video in an Amazon S3 bucket. You can send the report to the following email address. </p>
        <label for="email">Email address:</label><br>
        <input type="text" id="email" name="email" value=""><br>
        <div>
            <br>
            <p>Click the following button to analyze the video and obtain a report</p>
            <button id="button" onclick="ProcessImages()">Analyze Video</button>
        </div>
        <div id="spinner">
            <p>Report is being generated:</p>
        </div>
    </div>
</div>
</div>
<script src="./js/main.js" ></script>
</body>
</html>
```
### Creating the JavaScript

The **./src/libs/** folders contains a file for each of the AWS Service clients required. You must
replace "REGION" with your AWS Region, and replace "IDENTITY_POOL_ID" with the Amazon Cognito identity pool id
you created in [Create the resources](#create-the-resources) on this page. Here's an example of one of these client configuration files:
 
```javascript
import { CognitoIdentityClient } from "@aws-sdk/client-cognito-identity";
import { fromCognitoIdentityPool } from "@aws-sdk/credential-provider-cognito-identity";
import { RekognitionClient } from "@aws-sdk/client-rekognition";

const REGION = "REGION";
const IDENTITY_POOL_ID = "IDENTITY_POOL_ID"; // An Amazon Cognito Identity Pool ID.

// Create an AWS Rekognition service client object.
const rekognitionClient = new RekognitionClient({
    region: REGION,
    credentials: fromCognitoIdentityPool({
        client: new CognitoIdentityClient({ region: REGION }),
        identityPoolId: IDENTITY_POOL_ID,
    }),
});

export { rekognitionClient };
```
In **.src/js/index.js**, you first import all the required AWS Service and third party modules, and set global parameters.
```javascript
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { s3Client } from "../libs/s3Client.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import { ListObjectsCommand } from "@aws-sdk/client-s3";
import { StartFaceDetectionCommand, GetFaceDetectionCommand } from "@aws-sdk/client-rekognition";

const BUCKET = "BUCKET_NAME";
const SNS_TOPIC_ARN = "SNS_TOPIC_ARN";
const IAM_ROLE_ARN = "IAM_ROLE_ARN";
```

Next, you define functions for uploading the video.
```javascript
$(function () {
  $("#myTable").DataTable({
    scrollY: "500px",
    scrollX: true,
    scrollCollapse: true,
    paging: true,
    columnDefs: [{ width: 200, targets: 0 }],
    fixedColumns: true,
  });
});
// Upload the video.


const uploadVideo = async () => {
  try {
    // Retrieve a list of objects in the bucket.
    const listObjects = await s3Client.send(
      new ListObjectsCommand({ Bucket: BUCKET })
    );
    console.log("Object in bucket: ", listObjects);
    console.log("listObjects.Contents ", listObjects.Contents );

    const noOfObjects = listObjects.Contents;
    // If the Amazon S3 bucket is not empty, delete the existing content.
    if(noOfObjects != null) {
      for (let i = 0; i < noOfObjects.length; i++) {
        const data = await s3Client.send(
            new DeleteObjectCommand({
              Bucket: BUCKET,
              Key: listObjects.Contents[i].Key
            })
        );
      }
    }
    console.log("Success - bucket empty.");

    // Create the parameters for uploading the video.
    const videoName = document.getElementById("videoname").innerHTML + ".mp4";
    const files = document.getElementById("videoupload").files;
    const file = files[0];
    const uploadParams = {
      Bucket: BUCKET,
      Body: file,
    };
    uploadParams.Key = path.basename(file.name);
    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    console.log("Success - video uploaded");
  } catch (err) {
    console.log("Error", err);
  }
};
window.uploadVideo = uploadVideo;
```
Next, you define functions for retrieving the video.
```javascript
const getVideo = async () => {
  try {
    const listVideoParams = {
      Bucket: BUCKET
    };
    const data = await s3Client.send(new ListObjectsCommand(listVideoParams));
    console.log("Success - video deleted", data);
    const videoName = data.Contents[0].Key;
    document.getElementById("videoname").innerHTML = videoName;
    const videoDate = data.Contents[0].LastModified;
    document.getElementById("videodate").innerHTML = videoDate;
    const videoOwner = data.Contents[0].Owner;
    document.getElementById("videoowner").innerHTML = videoOwner;
    const videoSize = data.Contents[0].Size;
    document.getElementById("videosize").innerHTML = videoSize;
  } catch (err) {
    console.log("Error", err);
  }
};
window.getVideo = getVideo;
```
Define functions for analyzing the video, and sending the email.
```javascript
const ProcessImages = async () => {
  try {
    // Create the parameters required to start face detection.
    const videoName = document.getElementById("videoname").innerHTML;
    const startDetectParams = {
      Video: {
        S3Object: {
          Bucket: BUCKET,
          Name: videoName
        },
      },
      notificationChannel: {
        roleARN: IAM_ROLE_ARN,
        SNSTopicArn: SNSTOPIC
      },
    };
    // Start the Amazon Rekognition face detection process.
    const data = await rekognitionClient.send(
      new StartFaceDetectionCommand(startDetectParams)
    );
    console.log("Success, face detection started. ", data);
    const faceDetectParams = {
      JobId: data.JobId,
    };
    try {
      var finished = false;
      var facesArray = [];
      // Detect the faces.
      while (!finished) {
        var results = await rekognitionClient.send(
          new GetFaceDetectionCommand(faceDetectParams)
        );
        // Wait until the job succeeds.
        if (results.JobStatus == "SUCCEEDED") {
          finished = true;
        }
      }
      finished = false;
      // Parse results into CVS format.
      const noOfFaces = results.Faces.length;
      var i;
      for (i = 0; i < results.Faces.length; i++) {
        var boundingbox = JSON.stringify(results.Faces[i].Face.BoundingBox);
        var confidence = JSON.stringify(results.Faces[i].Face.Confidence);
        var pose = JSON.stringify(results.Faces[i].Face.Pose);
        var quality = JSON.stringify(results.Faces[i].Face.Quality);
        var arrayfirst = [];
        var arraysecond = [];
        var arraythird = [];
        var arrayforth = [];
        arrayfirst.push(boundingbox);
        arraysecond.push(confidence);
        arraythird.push(pose);
        arrayforth.push(quality);
        arrayfirst.push(arraysecond);
        arrayfirst.push(arraythird);
        arrayfirst.push(arrayforth);
        facesArray.push(arrayfirst);
      }
      // Create the CSV file.
      create_csv_file(facesArray);
    } catch (err) {
      console.log("Error", err);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
window.ProcessImages = ProcessImages;

// Helper function to create the CSV file.
function create_csv_file(facesArray) {
  // Define the heading for each row of the data.
  var csv = "Bounding Box, , , , Confidance, Pose, , ,  Quality, ,\n";

  // Merge the data with CSV.
  facesArray.forEach(function (row) {
    csv += row.join(",");
    csv += "\n";
  });
  // Upload the CSV file to Amazon S3.
  uploadFile(csv);
}

// Helper function to upload file to Amazon S3.
const uploadFile = async (csv) => {
  const uploadParams = {
    Bucket: BUCKET,
    Body: csv,
    Key: "Face.csv"
  };
  try {
    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      uploadParams.Bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      uploadParams.key;
    console.log("Success. Report uploaded to " + linkToCSV + ".");

    // Send an email to notify user when report is available.
    sendEmail(uploadParams.Bucket, uploadParams.Key);
  } catch (err) {
    console.log("Error", err);
  }
};
// Helper function to send an email to user.
const sendEmail = async (bucket, key) => {
  const toEmail = document.getElementById("email").value;
  const fromEmail = "SENDER_ADDRESS";// 
  try {
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      key;
    // Set the parameters
    const params = {
      Destination: {   /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          toEmail, //RECEIVER_ADDRESS
          /* more To-email addresses */
        ],
      },
      Message: {   /* required */
        Body: {   /* required */
          Html: {
            Charset: "UTF-8",
            Data:
              "<h1>Hello!</h1><p>Please see the the analyzed video report for " +
              key +
              " <a href=" +
              linkToCSV +
              "> here</a></p>",
          },
          Text: {
            Charset: "UTF-8",
            Data:
              "Hello,\\r\\n" +
              "Please see the attached file for the analyzed video report at" +
              linkToCSV +
              "\n\n",
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: key + " analyzed video report ready",
        },
      },
      Source: fromEmail, // SENDER_ADDRESS
      ReplyToAddresses: [
        /* more items */
      ],
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    console.log("Success. Email sent.", data);
  } catch (err) {
    console.log("Error", err);
  }
};
```
**Important**: You must bundle all the JavaScript and Node.js code required for the app into a single
 file (**main.js**) to run the app. For instructions, see [Bundling the scripts](#bundling-the-scripts).


### Bundling the scripts
This is a static site consisting only of HTML, CSS, and client-side JavaScript. 
However, a build step is required to enable the modules to work natively in the browser.

To bundle the JavaScript and Node.js for this example in a single file named main.js, 
enter the following commands in sequence in the AWS CLI command line:

```
cd javascriptv3/example_code/cross-services/video-analyzer
webpack ./js/index.js --mode development --target web --devtool false -o ./js/main.js
```
## Run the app
Open the index.html in your favorite browser, and follow the onscreen instructions.

## Destroying the resources
4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.

![ ](images/cloud_formation_stacks.png)

5. Select the stack you created in [Create the resources](#create-the-resources) on this page.

6. Choose **Delete**.

**Note**: If any of the resources have been altered, you must manually delete them via the AWS Console.

### Next steps
Congratulations! You have created and deployed the AWS Photo Analyzer application. 
For more AWS multiservice examples, see
[cross-services](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services).
