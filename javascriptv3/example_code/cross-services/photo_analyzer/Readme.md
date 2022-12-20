#  Creating an example AWS photo analyzer application using the AWS SDK for JavaScript (v3)

## Purpose
The cross-service example demonstrates how to build an app that analyzes nature images located in an Amazon Simple Storage Service (Amazon S3) bucket
by using the AWS Rekognition service. For example, the following image shows a lake.

![AWS Photo Analyzer](images/Lake1.png)

After the application analyzes this image, it creates this data:
*	Panoramic - 99.99971
*	Outdoors - 99.99971
*	Nature - 99.99971
*	Landscape - 99.99971
*	Scenery	 - 99.99971
*	Wilderness - 96.90007
*	Water - 93.501465
*	Lake - 87.28128

The application can analyze many images and generate reports for each image in a
separate Amazon S3 bucket, breaking the image down into a series of labels. In addition, this application uses Amazon Simple Email Service (Amazon SES)
to send emails with a link to each reports to the recipient. The app uses the following AWS services:

- [AWS Rekognition](https://aws.amazon.com/rekognition/)
- [Amazon Simple Storage Services (S3)](https://aws.amazon.com/s3/)
- [Amazon Simple Email Services (SES)](https://aws.amazon.com/ses/)

## Prerequisites

To build this cross-service example, you need the following:

* An AWS account. For more information see [AWS SDKs and Tools Reference Guide](https://docs.aws.amazon.com/sdkref/latest/guide/overview.html).
* A project environment to run this Node JavaScript example, and install the required AWS SDK for JavaScript and third-party modules.  For instructions, see [Create a Node.js project environment](#create-a-nodejs-project-environment) on this page.
* At least one email address verified on Amazon SES. For instructions, see [Verifying an email address on Amazon SES](#verifying-an-email-address-on-amazon-ses).
* The following AWS resources:
    - An unauthenticated AWS Identity and Access Management (IAM) user role with the following permissions:
        - sns:*

**Note**: An unauthenticated role enables you to provide permissions to unauthenticated users to use the AWS Services. To create an authenticated role, see [Amazon Cognito Identity Pools (Federated Identities)](https://docs.aws.amazon.com/cognito/latest/developerguide/cognito-identity.html).

 For instructions on creating the minimum resources required for this tutorial, see [Create the resources](#create-the-resources) on this page.

## ⚠ Important
* We recommend that you grant this code least privilege, or at most the minimum permissions required to perform the task. For more information, see [Grant Least Privilege](https://docs.aws.amazon.com/IAM/latest/UserGuide/best-practices.html#grant-least-privilege) in the *AWS Identity and Access Management User Guide*.
* This code has not been tested in all AWS Regions. Some AWS services are available only in specific [Regions](https://aws.amazon.com/about-aws/global-infrastructure/regional-product-services).
* Running this code might result in charges to your AWS account. We recommend you destroy the resources when you are finished. For instructions, see [Destroying the resources](#destroying-the-resources).
* Running the unit tests might result in charges to your AWS account.
* This tutorial is written to work with the specific versions defined in the *package.json*. If you change these versions, the tutorial may not work correctly.


## Create the resources
You can create the AWS resources required for this cross-service example using either of the following:
- [The Amazon CloudFormation](#create-the-resources-using-amazon-cloudformation)
- [The AWS Management Console](#create-the-resources-using-the-aws-management-console)

### Create the resources using Amazon CloudFormation
To run the stack using the AWS CLI:

1. Install and configure the AWS CLI following the instructions in the AWS CLI User Guide.

2. Open the AWS Command Console from the *./photo-analyzer* folder.

3. Run the following command, replacing *STACK_NAME* with a unique name for the stack.
```
aws cloudformation create-stack --stack-name STACK_NAME --template-body file://setup.yaml --capabilities CAPABILITY_IAM
```
**Important**: The stack name must be unique within an AWS Region and AWS account. You can specify up to 128 characters, and numbers and hyphens are allowed.

4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the **Stacks** page.

![ ](images/cloud_formation_stacks.png)

5. Choose the **Resources** tab. The **Physical ID** of the **IDENTITY_POOL_ID** you require for this cross-service example is displayed.

![ ](images/cloud_formation_resources_tab.png)

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

![ ](images/identity_pool_ids.png)

9. Choose **Edit identity pool**.
10. Take note of the name of the role in the **Unauthenticated role** field.

#### Adding permissions to an unauthenticated user role
11. Open [IAM in the AWS Management Console](https://aws.amazon.com/iam/), and open the *Roles* page.
12. Search for the unauthenticated role you just created.
13. Open the role.
14. Click the down arrow beside the policy name.
15. Choose **Edit Policy**.
16. Choose the **JSON** tab.
17. Delete the existing content, and paste the code below into it.
```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Action": [
                "mobileanalytics:PutEvents",
                "cognito-sync:*"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "rekognition:DetectLabels",
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": [
                "s3:PutObject",
                "s3:DeleteObject",
                "s3:ListBucket",
                "s3:GetObject"
            ],
            "Resource": "*",
            "Effect": "Allow"
        },
        {
            "Action": "ses:SendEmail",
            "Resource": "*",
            "Effect": "Allow"
        }
    ]
}
```
18. Choose **Review Policy**.
19. Choose **Save Changes**.

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
cd javascriptv3/example_code/cross-services/photo-analyzer
npm install
```
## Building the code
This app runs from the browser, so we create the interface using HTML and CSS.
The app uses JavaScript to provide basic interactive features, and Node.js to invoke the AWS Services.

### Creating the HTML and CSS
In **index.html**, the **head** section loads [JQuery](https://jquery.com/), [DataTables CDN](https://cdn.datatables.net/), and [BootStrap](https://getbootstrap.com/) libraries.
It also loads **stlyes.css**, which applies styles to the HTML,
and the **main.js**, which contains the following JavaScript and Node.js functions used in the app.

**Note**: **main.js** is a bundled file containing all the required JavaScript. You'll create this later in the tutorial.

The remaining code defines the interface features, including a table and buttons.

```html
<!DOCTYPE html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <script src="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.js"></script>
    <script src="./js/main.js"></script>

    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="https://cdn.datatables.net/v/dt/dt-1.10.20/datatables.min.css"/>
    <link rel="stylesheet" href="./css/styles.css"/>


    <title>AWS Photo Analyzer</title>

    <script>
        function myFunction() {
            alert("The form was submitted");
        }
    </script>

</head>

<body>
<div class="container">

<h2>AWS Photo Analyzer Application</h2>
<p>Upload images to an S3 Bucket. Each image will be analysed!</p>

    <input id="imageupload" type="file" name="file" /><br/><br/>
    <button id="addimage" onclick="addToBucket()">Add image</button>
</div>
</div>
<div class ="container" >
    <br>

    <p>Select the following button to determine the number of images in the bucket.</p>

    <button onclick="getImages()">Get Images</button>
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
        </tbody>
        <tfoot>
        <tr>
            <th>Name</th>
            <th>Owner</th>
            <th>Date</th>
            <th>Size</th>
        </tr>
        </tfoot>
        <div id="success3"></div>
    </table>

</div>
<div class="container">
<p>You can generate a report that analyzes the images in the S3 bucket. You can send the report to the following email address. </p>
<label for="email">Email address:</label><br>
<input type="text" id="email" name="email" value=""><br>

<div>
    <br>

    <p>Click the followi ng button to obtain a report</p>
    <button onclick="ProcessImages()">Analyze Photos</button>
</div>
</div>
</body>
</html>
```
### Creating the JavaScript and Node.js
The **./js/libs/** folders contains a file for each of the AWS Service clients required. You must
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
In **./js/index.js**, you first import all the required AWS Service and third party modules, and set global parameters.
```javascript
import { DetectLabelsCommand } from "@aws-sdk/client-rekognition";
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { ListObjectsCommand, PutObjectCommand } from "@aws-sdk/client-s3";
import { REGION, s3Client } from "../libs/s3Client.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";

// Set global parameters.
const BUCKET_IMAGES = "BUCKET_IMAGES";
const BUCKET_REPORTS ="BUCKET_REPORTS";
const EMAIL_SENDER_ADDRESS = "EMAIL_SENDER_ADDRESS"; // A verified Amazon SES email.
```

Next, you define functions for working with the table.
```javascript
// Load table parameters.
$(function() {
  $('#myTable').DataTable( {
    scrollY:        "500px",
    scrollX:        true,
    scrollCollapse: true,
    paging:         true,
    columnDefs: [
      { width: 200, targets: 0 }
    ],
    fixedColumns: true
  } );
} );

// Load images from Amazon S3 bucket to the table.
const loadTable = async () => {
  window.alert = function() {};
  try {
    const listVideoParams = {
      Bucket: BUCKET_IMAGES
    };
    const data = await s3Client.send(new ListObjectsCommand(listVideoParams));
    console.log("Success", data);
    for (let i = 0; i < data.Contents.length; i++) {
      console.log('checking')
      var t = $('#myTable').DataTable();
      t.row.add([
        data.Contents[i].Key,
        data.Contents[i].Owner,
        data.Contents[i].LastModified,
        data.Contents[i].Size
      ]).draw(false);
    };
  } catch (err) {
    console.log("Error", err);
  }
};
loadTable();

// Refresh page to populate table with latest images.
const getImages = async () => {
  window.location.reload();
};
window.getImages = getImages;
```
Next you define the functions for adding an image to the Amazon S3 bucket (*addToBucket*), analyzing the image (*ProcessImages*), creating the CSV report (*create_csv_file*),
uploading the report to the Amazon S3 bucket (*uploadFile*), and sending the specified recepient email notification about each image (*sendEmail*).
```javascript
const addToBucket = async () => {
  try{
    // Create the parameters for uploading the video.
    const files = document.getElementById("imageupload").files;
    const file = files[0];
    const key = document.getElementById("imageupload").files[0].name
    const uploadParams = {
      Bucket: BUCKET_IMAGES,
      Body: file,
      Key: key
    };

    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    console.log("Success - image uploaded");
  } catch (err) {
    console.log("Error", err);
  }
};
// Expose function to browser.
window.addToBucket = addToBucket;


const ProcessImages = async () => {
  try {
    const listPhotosParams = {
      Bucket: BUCKET_IMAGES,
    };
    // Retrieve list of objects in the Amazon S3 bucket.
    const data = await s3Client.send(new ListObjectsCommand(listPhotosParams));
    console.log("Success, list of objects in bucket retrieved.", data);

    // Loop through images. For each image, retreive the image name,
    // then analyze image by detecting it's labels, then parse results
    // into CSV format.
    for (let i = 0; i < data.Contents.length; i++) {
      const key = data.Contents[i].Key;
      const imageParams = {
        Image: {
          S3Object: {
            Bucket: BUCKET_IMAGES,
            Name: key,
          },
        },
      };

      const lastdata = await rekognitionClient.send(
        new DetectLabelsCommand(imageParams)
      );
      console.log("Success, labels detected.", lastdata);
      var objectsArray = [];
      // Parse results into CVS format.
      const noOfLabels = lastdata.Labels.length;
      var j;
      for (j = 0; j < data.Contents.length; j++) {
        var name = JSON.stringify(lastdata.Labels[j].Name);
        var confidence = JSON.stringify(lastdata.Labels[j].Confidence);
        var arrayfirst = [];
        var arraysecond = [];
        arrayfirst.push(name);
        arraysecond.push(confidence);
        arrayfirst.push(arraysecond);
        objectsArray.push(arrayfirst);
      }
      // Create a CSV file report for each images.
      create_csv_file(objectsArray, key);
    }
  } catch (err) {
    console.log("Error", err);
  }
};
// Expose function to browser.
window.ProcessImages = ProcessImages;

// Helper function to create the CSV report.
function create_csv_file(objectsArray, key) {
  // Define the heading for each row of the data.
  var csv = "Object, Confidance \n";

  // Merge the data with CSV.
  objectsArray.forEach(function (row) {
    csv += row.join(",");
    csv += "\n";
  });
  // Upload the CSV file to Amazon S3 bucket for reports.
  uploadFile(csv, key);
}

// Helper function to upload reports to Amazon S3 bucket for reports.
const uploadFile = async (csv, key) => {
  const uploadParams = {
    Bucket: BUCKET_REPORTS,
    Body: csv,
    Key: key + ".csv",
  };
  try {
    const data = await s3Client.send(new PutObjectCommand(uploadParams));
    const linkToCSV =
      "https://s3.console.aws.amazon.com/s3/object/" +
      uploadParams.Bucket +
      "?region=" +
      REGION +
      "&prefix=" +
      uploadParams.Key;
    console.log("Success. Report uploaded to " + linkToCSV + ".");

    // Send an email to notify user when report is available.
    sendEmail(uploadParams.Bucket, uploadParams.Key, linkToCSV);
  } catch (err) {
    console.log("Error", err);
  }
};
// Helper function to send an email to user.
const sendEmail = async (bucket, key, linkToCSV) => {
  const toEmail = document.getElementById("email").value;
  const fromEmail = EMAIL_SENDER_ADDRESS; //
  try {
    // Set the parameters
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          toEmail, //RECEIVER_ADDRESS
          /* more To-email addresses */
        ],
      },
      Message: {
        /* required */
        Body: {
          /* required */
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
cd javascriptv3/example_code/cross-services/photo-analyzer/src
webpack index.js --mode development --target web --devtool false -o main.js
```
## Run the app
Open the index.html in your favorite browser, and follow the onscreen instructions.

## Destroying the resources
4. Open [AWS CloudFormation in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.

![ ](images/cloud_formation_stacks.png)

5. Select the stack you created in [Create the resources](#create-the-resources) on this page.

6. Choose **Delete**.

### Next steps
Congratulations! You have created and deployed the AWS Photo Analyzer application.
For more AWS multiservice examples, see
[usecases](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/usecases).
