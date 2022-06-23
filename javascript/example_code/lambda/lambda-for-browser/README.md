#  Creating a browser-based application that passes user selections to an AWS Lambda function

You can create a browser-based application that passes user selections to an AWS Lambda function, and triggers the Lambda function.


This tutorial shows you how to use the AWS SDK for JavaScript V2 API to invoke these AWS services:

- AWS Lambda
- Amazon DynamoDB

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
You can create the AWS resources required for this cross-service example the [AWS Management Console](#create-the-resources-using-the-aws-management-console)

#### Create an unauthenticated user role
1. Open [AWS Cognito in the AWS Management Console](https://aws.amazon.com/cloudformation/), and open the *Stacks* page.
2. Choose **Manage Identity Pools**.
3. Choose **Create new identity pool**.
4. In the **Identity pool name** field, give your identity pool a name.
5. Select the **Enable access to unauthenticated identities** checkbox.
6. Choose **Create Pool**.
7. Choose **Allow**.
8. Take note of the **Identity pool ID**, which is highlighted in red in the **Get AWS Credentials** section. 
9. Choose **Edit identity pool**.
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
      "Action": "dynamodb:PutItem",
      "Resource": "*",
      "Effect": "Allow"
    },
    {
      "Action": "lambda:InvokeFunction",
      "Resource": "*",
      "Effect": "Allow"
    },
    {
      "Effect": "Allow",
      "Action": "iam:PassRole",
      "Resource": "*",
      "Condition": {
        "StringEquals": {
          "iam:PassedToService": "lambda.amazonaws.com"
        }
      }
    }
  ]
}
```
8. Choose **Review Policy**.
9. Choose **Save Changes**.

#### Update trust policy
You should update your IAM role's trust policy.

1. Open the IAM Management Console.
2. Choose **Roles**.
3. Choose the IAM role you created above.
4. Choose the **Trust relationships** tab.
5. Choose **Edit test relationships**.
6. Modify the trust relationships as below. Replace **IDENTITY_POOL_ID** with the identity pool id you created above,
   and **USER_ARN** with the Amazon Resource Name (ARN) of your Amazon user.
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "cognito-identity.amazonaws.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "cognito-identity.amazonaws.com:aud": "IDENTITY_POOL_ID"
        },
        "ForAnyValue:StringLike": {
          "cognito-identity.amazonaws.com:amr": "unauthenticated"
        }
      }
    },
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    },
    {
      "Effect": "Allow",
      "Principal": {
        "AWS": "USER_ARN"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
```
#### Create an Amazon DynamoDB table
Create an Amazon DynamoDB table called 'DesignRequests', with the following attributes:
- Id (Number)
- Color (String)
- Pattern (String)

For more information, see [Creating a table](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.NodeJs.01.html)
and [Load sample data](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.NodeJs.02.html).


## Create a Node.js project environment

1. Clone the [AWS Code Samples repo](https://github.com/awsdocs/aws-doc-sdk-examples) to your local environment.
   See [the Github documentation](https://docs.github.com/en/github/creating-cloning-and-archiving-repositories/cloning-a-repository) for
   instructions.

2. Run the following commands in sequence in the AWS CLI command line to install the AWS service client modules and third-party modules listed in the *package.json*:

```
npm install node -g
cd javascript/example_code/lambda/lambda-for-browser
npm install
```
## Building the code
This app runs from the browser, so we create the interface using HTML and CSS.
The app uses JavaScript to provide basic interactive features, and Node.js to invoke the AWS Services.

### Creating the HTML
In **index.html**, the **head** section loads the **main.js**, which contains the following JavaScript and Node.js functions used in the app.

**Note**: **main.js** is a bundled file containing all the required JavaScript. You'll create this later in the tutorial.

The remaining code defines the interface features, including a table and buttons.

```html
<!DOCTYPE html>
<head>
    <script src="https://sdk.amazonaws.com/js/aws-sdk-2.1044.0.min.js"></script>
    <script type="text/javascript" src="main.js"></script>
</head>
<body>
<br>    <!--<form action="/myform" method="GET"> -->
<form action="#">
    <div align="Center">
        <br> <label for="skill"><b>Choose a colour and pattern</b></label> <br>
        <select name="colours" id="c1">
            <option value="red">Red</option>
            <option value="orange">Orange</option>
            <option value="yellow">Yellow</option>
            <option value="green">Green</option>
            <option value="blue">Blue</option>
            <option value="indigo">Indigo</option>
            <option value="violet">Violet</option>
        </select>
        <select name="pattern" id="p1">
            <option value="floral">Floral</option>
            <option value="floral">Floral</option>
            <option value="modern">Modern</option>
            <option value="paisley">Paisley</option>
            <option value="plain">Plain</option>
            <option value="plush">Plush</option>
            <option value="squares">Squares</option>
            <option value="stripes">Stripes</option>
        </select>
        <br>
</form>
<button type="button" onclick="myFunction();">Submit</button>
</body>
</html>
```
### Creating the JavaScript

There are two JavaScript scripts
- **index.js**
- **main.js**

#### index.js
**index.js** is the AWS Lambda function. It uses the Amazon DynamoDB Document client to pass the user's browser selections to a
DynamoDB table.
```javascript
'use strict'

console.log('Loading function');

var AWS = require('aws-sdk');

// Initialize the Amazon Cognito credentials provider.
AWS.config.region = "REGION";
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: "IDENTITY_POOL_ID",
});


// Create client.
const docClient = new AWS.DynamoDB.DocumentClient();


exports.handler = async(event, context, callback) => {
    const params = {
        Item: {
            Id: event.Item.Id,
            Color: event.Item.Color,
            Pattern: event.Item.Pattern
        },
        TableName: event.TableName
    };
    await docClient.put(params, async function (err, data) {
        if (err) {
            console.error(
                "Unable to add item. Error JSON:",
                JSON.stringify(err, null, 2)
            );
        } else {
            console.log("Adding data to dynamodb...");
            console.log("Added item:", JSON.stringify(data, null, 2));
        }
    });
    callback(null, event);
};
```
You compress **index.js** into a ZIP file. Then create a Lambda function using this ZIP file as follows:
1. Open the AWS Lambda Management Console.
2. Choose **Create function**.
3. Enter a function name.
4. Choose **Create function**.
5. In the **Code Source** panel, select **Upload from** - **.zip file**.
6. Navigate to and select the ZIP file you compressed.

Your Lambda function is displayed.

#### main.js
**main.js** contains the JavaScript function that invokes the Lambda function. It is triggered when the user clicks the button on
the UI of the app.
```javascript
AWS.config.region = "REGION";
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: "IDENTITY_POOL_ID",
});

// Create client.
const lambda = new AWS.Lambda();

const myFunction = async () => {
    const color = document.getElementById("c1").value
    const pattern = document.getElementById("p1").value
    const id = Math.floor(Math.random() * (10000 - 1 + 1)) + 1;
    const params = {
        FunctionName: 'forPathryusah', /* required */
        Payload: JSON.stringify( { Item: {
                Id: id,
                Color: color,
                Pattern: pattern
            },
            TableName: "DesignRequests",
        })
    };
    lambda.invoke(params,  function (err, data){
        if (err) console.log(err, err.stack); // an error occurred
        else console.log('Success, payload', data);           // successful response
    })
};
```

## Run the app
Open the index.html in your favorite browser, and follow the onscreen instructions.

### Next steps
Congratulations! You have created and deployed the AWS Lambda Browser application.
For more AWS multiservice examples, see
[cross-services](https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javascriptv3/example_code/cross-services).
