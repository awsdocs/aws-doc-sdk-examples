#!/bin/bash
echo "$AWS_CONFIG" > .aws/config
echo "$AWS_CREDENTIALS" > .aws/credentials
export AWS_REGION='us-east-2'
export AWS_ACCESS_KEY_ID=$(grep "aws_access_key_id" .aws/credentials | cut -d "=" -f 2)
export AWS_SECRET_ACCESS_KEY=$(grep "aws_secret_access_key" .aws/credentials | cut -d "=" -f 2)
export AWS_SESSION_TOKEN=$(grep "aws_session_token" .aws/credentials | cut -d "=" -f 2)

# Usage example:
#     docker build -f ruby/ruby.Dockerfile -t <TAGNAME> .
#     docker run -e AWS_CREDENTIALS="$(cat ~/.aws/credentials)" -e AWS_CONFIG="$(cat ~/.aws/config)" -it <TAGNAME>
