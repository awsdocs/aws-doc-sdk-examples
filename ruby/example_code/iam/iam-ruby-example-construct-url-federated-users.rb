# Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
# This file is licensed under the Apache License, Version 2.0 (the "License").
# You may not use this file except in compliance with the License. A copy of
# the License is located at
# 
# http://aws.amazon.com/apache2.0/
# 
# This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
# CONDITIONS OF ANY KIND, either express or implied. See the License for the
# specific language governing permissions and limitations under the License.

# snippet-sourcedescription:[construct-url-federated-users.py demonstrates how to programmatically construct a URL that gives federated users direct access to the AWS Management Console.]
# snippet-service:[iam]
# snippet-keyword:[Ruby]
# snippet-keyword:[AWS Identity and Access Management (IAM)]
# snippet-keyword:[Code Sample]
# snippet-keyword:[AssumeRole]
# snippet-sourcetype:[full-example]
# snippet-sourcedate:[2019-01-22]
# snippet-sourceauthor:[stephswo (AWS)]
# snippet-start:[iam.ruby.construct-url-federated-users.complete]
require 'rubygems'
require 'json'
require 'open-uri'
require 'cgi'
require 'aws-sdk'

# Create a new STS instance
# 
# Note: Calls to AWS STS API operations must be signed using an access key ID 
# and secret access key. The credentials can be in EC2 instance metadata 
# or in environment variables and will be automatically discovered by
# the default credentials provider in the AWS Ruby SDK. 
sts = Aws::STS::Client.new

# The following call creates a temporary session that returns 
# temporary security credentials and a session token.
# The policy grants permissions to work
# in the AWS SNS console.

session = sts.get_federation_token({
duration_seconds: 1800,
name: "UserName",
policy: "{\"Version\":\"2012-10-17\",\"Statement\":{\"Effect\":\"Allow\",\"Action\":\"sns:*\",\"Resource\":\"*\"}}",
})

# The issuer value is the URL where users are directed (such as
# to your internal sign-in page) when their session expires.
#
# The console value specifies the URL to the destination console.
# This example goes to the Amazon SNS console.
#
# The sign-in value is the URL of the AWS STS federation endpoint.
issuer_url = "https://mysignin.internal.mycompany.com/"
console_url = "https://console.aws.amazon.com/sns"
signin_url = "https://signin.aws.amazon.com/federation"

# Create a block of JSON that contains the temporary credentials
# (including the access key ID, secret access key, and session token).
session_json = {
:sessionId => session.credentials[:access_key_id],
:sessionKey => session.credentials[:secret_access_key],
:sessionToken => session.credentials[:session_token]
}.to_json

# Call the federation endpoint, passing the parameters
# created earlier and the session information as a JSON block. 
# The request returns a sign-in token that's valid for 15 minutes.
# Signing in to the console with the token creates a session 
# that is valid for 12 hours.
get_signin_token_url = signin_url + 
"?Action=getSigninToken" + 
"&SessionType=json&Session=" + 
CGI.escape(session_json)

returned_content = URI.parse(get_signin_token_url).read

# Extract the sign-in token from the information returned
# by the federation endpoint.
signin_token = JSON.parse(returned_content)['SigninToken']
signin_token_param = "&SigninToken=" + CGI.escape(signin_token)

# Create the URL to give to the user, which includes the
# sign-in token and the URL of the console to open.
# The "issuer" parameter is optional but recommended.
issuer_param = "&Issuer=" + CGI.escape(issuer_url)
destination_param = "&Destination=" + CGI.escape(console_url)
login_url = signin_url + "?Action=login" + signin_token_param + 
issuer_param + destination_param
# snippet-end:[iam.ruby.construct-url-federated-users.complete]

