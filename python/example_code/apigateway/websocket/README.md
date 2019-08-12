# AWS API Gateway WebSocket Chat Program

## Repository files

* `websocket.py` : Main program source file
* `lambda_util.py` : Utility functions to manage AWS Lambda functions
* `websocket_connect.py` : AWS Lambda function to implement the WebSocket `$connect` route
* `websocket_disconnect.py` : AWS Lambda function to implement the WebSocket `$disconnect` route
* `websocket_send_msg.py` : AWS Lambda function to implement the WebSocket `sendmsg` custom route (Python)
* `websocket_send_msg.js` : AWS Lambda function to implement the WebSocket `sendmsg` custom route (JavaScript)

## AWS infrastructure resources

* API Gateway WebSocket API
* AWS Lambda functions for the WebSocket `$connect` and `$disconnect` routes and a WebSocket `sendmsg` custom route
* AWS Identity and Access Management (IAM) role and policy for the AWS Lambda functions
* Amazon DynamoDB table to store connection IDs and user names

## Prerequisites

* Install Python 3.x.
* Install the AWS SDK for Python `boto3`. Instructions are at https://github.com/boto/boto3.
* Install the AWS CLI (Command Line Interface). Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html.
* Configure the AWS CLI. Instructions are at 
  https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html.

## Instructions

To create the WebSocket infrastructure:

    python websocket.py

To delete the WebSocket infrastructure:

    python websocket.py -d
    OR
    python websocket.py --delete

To use the WebSocket Chat Program:

1. Download and install `Node.js` from https://nodejs.org. `Node.js` includes the `npm` package manager.
2. Use `npm` to globally install `wscat`.

        npm install -g wscat

3. When the WebSocket infrastructure is created, the WebSocket WSS address is output.
   Copy the output WebSocket WSS address and enter it on the `wscat` command line to open
   a WebSocket connection. Open multiple connections by running `wscat` in separate
   terminal windows.
   
        wscat -c WSS_ADDRESS

   Example command line:
   
        wscat -c wss://123abc456def.execute-api.us-west-2.amazonaws.com/dev

   Optional: Specify a user name as a query parameter in the WSS address (Used
   by the Python version of `websocket_send_msg.py`)
   
        wscat -c wss://123abc456def.execute-api.us-west-2.amazonaws.com/dev?name=Steven

4. Send a chat message to all open connections:

        {"action": "sendmsg", "msg": "Enter message text here"}

   The `"action": "sendmsg"` pair invokes the WebSocket `sendmsg` custom route.
   
   The `"msg": "Enter message text here"` pair specifies the message text to send.

5. To close the WebSocket connection:

        <Ctrl-C>
