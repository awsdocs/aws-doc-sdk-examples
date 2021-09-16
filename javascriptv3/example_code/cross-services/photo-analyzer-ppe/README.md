#  Creating an example AWS photo analyzer to detect images with Personal Protective Equipment

You can create an application that uses Amazon Rekognition to detect personal protective equipment (PPE)
in images located in an Amazon Simple Storage Service (Amazon S3) bucket. 

![AWS Tracking Application](images/readme_images/dynamodb.png)

In addition, the app stores the information in an Amazon DynamoDB table, and notifies a specified used by email 
using the Amazon Simple Email (Amazon SES) service.

This tutorial shows you how to use the AWS SDK for JavaScript V3 API to invoke these AWS services: 

- Amazon S3 service
- Amazon Rekognition service
- Amazon DynamoDB service
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
    - An Amazon DynamoDB table named **PPE** with a key named **id**. 
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

2. Open the AWS Command Console from the *./photo-analyzer* folder.

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
        },
        {
            "Action": "dynamodb:*",
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
cd javascriptv3/example_code/cross-services/detect-ppe
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
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <script src="https://code.jquery.com/jquery-1.12.4.min.js"></script>
    <script src="https://code.jquery.com/ui/1.11.4/jquery-ui.min.js"></script>
    <script src="./js/main.js"></script>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css"/>

    <title>AWS Personal Protective Equipment (PPE) photo analyzer</title>

    <script>
        function myFunction() {
            alert("The form was submitted");
        }
    </script>

</head>

<body>
<div class="container">

    <h2>AWS Photo Analyzer Application</h2>
</div>
<div class="container">
    <p>You can analyze photos in an Amazon S3 bucket for personal protective equipement (PPE). You can send the report to the following email address. </p>
    <label for="email">Email address:</label><br>
    <input type="text" id="email" name="email" value=""><br>

    <div>
        <br>
        <p>Select the following button analyze the photos, and update the Amazon DynamoDB table with the results.</p>
        <button onclick="processImages()">Analyze Photos</button>
    </div>
</div>
</body>
</html>
```
### Creating the JavaScript

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
import { rekognitionClient } from "../libs/rekognitionClient.js";
import { s3Client } from "../libs/s3Client.js";
import { dynamoDBClient, REGION } from "../libs/dynamodbClient.js";
import { sesClient } from "../libs/sesClient.js";
import { SendEmailCommand } from "@aws-sdk/client-ses";
import { ListObjectsCommand } from "@aws-sdk/client-s3";
import { DetectProtectiveEquipmentCommand } from "@aws-sdk/client-rekognition";
import { PutItemCommand } from "@aws-sdk/client-dynamodb";

const BUCKET = "S3_BUCKET_NAME";
const TABLE = "DDB_TABLE_NAME";
const FROM_EMAIL = "SENDER_EMAIL_ADDRESS";
```

Next, you define functions for working with the table.
```javascript
export const sendEmail = async () => {
  // Helper function to send an email to user.
  const TO_EMAIL = document.getElementById("email").value;
  try {
    // Set the parameters
    const params = {
      Destination: {
        /* required */
        CcAddresses: [
          /* more items */
        ],
        ToAddresses: [
          TO_EMAIL, //RECEIVER_ADDRESS
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
                "<h1>Hello!</h1>" +
                "<p> The Amazon DynamoDB table " +
                TABLE +
                " has been updated with PPE information <a href='https://" +
                REGION +
                ".console.aws.amazon.com/dynamodb/home?region=" +
                REGION +
                "#item-explorer?table=" +
                TABLE +
                "'>here.</a></p>"
          },
        },
        Subject: {
          Charset: "UTF-8",
          Data: "PPE image report ready.",
        },
      },
      Source: FROM_EMAIL,
      ReplyToAddresses: [
        /* more items */
      ],
    };
    const data = await sesClient.send(new SendEmailCommand(params));
    alert("Success. Email sent.");
  } catch (err) {
    console.log("Error sending email. ", err);
  }
};

export const processImages = async () => {
  try {
    const listPhotosParams = {
      Bucket: BUCKET,
    };
    // Retrieve list of objects in the Amazon S3 bucket.
    const data = await s3Client.send(new ListObjectsCommand(listPhotosParams));
    console.log("Success, list of objects in bucket retrieved.", data);

    // Helper function to convert floating numbers to integers.
    function float2int(value) {
      return value | 0;
    }

    // Loop through images to get the parameters for each.
    for (let i = 0; i < data.Contents.length; i++) {
      const key = data.Contents[i].Key;

      const imageParams = {
        Image: {
          S3Object: {
            Bucket: BUCKET,
            Name: key,
          },
        },
        SummarizationAttributes: {
          MinConfidence: float2int(50) /* required */,
          RequiredEquipmentTypes: ["FACE_COVER", "HAND_COVER", "HEAD_COVER"],
        },
      };
      const ppedata = await rekognitionClient.send(
          new DetectProtectiveEquipmentCommand(imageParams)
      );

      // Parse the results using conditional nested loops.
      const noOfPeople = ppedata.Persons.length;
      for (let i = 0; i < noOfPeople; i++) {
        if (ppedata.Persons[i].BodyParts[0].EquipmentDetections.length === 0) {
          const noOfBodyParts = ppedata.Persons[i].BodyParts.length;
          for (let j = 0; j < noOfBodyParts; j++) {
            const bodypart = ppedata.Persons[i].BodyParts[j].Name;
            const confidence = ppedata.Persons[i].BodyParts[j].Confidence;
            var equipment = "Not identified";
            const val = Math.floor(1000 + Math.random() * 9000);
            const id = val.toString() + "";
            const image = imageParams.Image.S3Object.Name;
            const ppeParams = {
              TableName: TABLE,
              Item: {
                id: { N: id + "" },
                bodyPart: { S: bodypart + "" },
                confidence: { S: confidence + "" },
                equipment: { S: equipment + "" },
                image: { S: image },
              },
            };
            const tableData = await dynamoDBClient.send(
                new PutItemCommand(ppeParams)
            );
          }
        } else {
          const noOfBodyParts = ppedata.Persons[i].BodyParts.length;
          for (let j = 0; j < noOfBodyParts; j++) {
            const bodypart = ppedata.Persons[i].BodyParts[j].Name;
            const confidence = ppedata.Persons[i].BodyParts[j].Confidence;
            var equipment =
                ppedata.Persons[i].BodyParts[j].EquipmentDetections[0].Type;
            const val = Math.floor(1000 + Math.random() * 9000);
            const id = val.toString() + "";
            const image = imageParams.Image.S3Object.Name;
            const ppeParams = {
              TableName: TABLE,
              Item: {
                id: { N: id + "" },
                bodyPart: { S: bodypart + "" },
                confidence: { S: confidence + "" },
                equipment: { S: equipment + "" },
                image: { S: image },
              },
            };
            const tableData = await dynamoDBClient.send(
                new PutItemCommand(ppeParams)
            );
          }
        }
      }
    }
    alert("Images analyzed and table updated.");
  } catch (err) {
    console.log("Error analyzing images. ", err);
  }
  try {
    sendEmail();
  } catch (err) {
    alert("Error sending email");
  }
};
// Expose the function to the browser.
window.processImages = processImages;
```
**Important**: You must bundle all the JavaScript and Node.js code required for the app into a single
 file (**main.js**) to run the app. For instructions, see [Bundling the scripts](#bundling-the-scripts).


### Bundling the scripts
This is a static site consisting only of HTML, CSS, and client-side JavaScript. 
However, a build step is required to enable the modules to work natively in the browser.

To bundle the JavaScript and Node.js for this example in a single file named main.js, 
enter the following commands in sequence in the AWS CLI command line:

```
cd javascriptv3/example_code/cross-services/photo-analyzer-ppe
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
