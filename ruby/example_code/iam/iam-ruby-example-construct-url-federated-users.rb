# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX - License - Identifier: Apache - 2.0

require 'aws-sdk-core'
require 'open-uri'

# Gets a temporary URL that allows a federated user to sign in to a specified
# location within the AWS Management Console for a limited time with the
# specified permissions in the AWS account.
#
# @param sts_client [Aws::STS::Client] An initialized
#   AWS Security Token Service (AWS STS) client.
# @param user_name [String] A name for the federated user.
# @param duration_seconds [Integer] The number of seconds for which the
#   temporary URL is valid.
# @param policy [String] The AWS Identity and Access Management (IAM) policy
#   to apply to the federated user.
# @param issuer_url [String] The URL where the user is directed (such as
#   to your organization's internal sign-in page) when their session expires.
# @param console_url [String] The URL to the desired destination within the
#   AWS Management Console.
# @param signin_url [String] The URL of the AWS STS federation endpoint.
# @return [String] The temporary URL for the federated user; otherwise the
#   string 'Error'.
# @example
#   puts get_federated_user_console_sign_in_url(
#     Aws::STS::Client.new,
#     'my-user',
#     1_800, # 30 minutes.
#     "{\"Version\":\"2012-10-17\",\"Statement\":{\"Effect\":\"Allow\",\"Action\":\"sns:*\",\"Resource\":\"*\"}}",
#     'https://mysignin.internal.mycompany.com/',
#     'https://console.aws.amazon.com/sns',
#     'https://signin.aws.amazon.com/federation'
#   )
def get_federated_user_console_sign_in_url(
  sts_client,
  user_name,
  duration_seconds,
  policy,
  issuer_url,
  console_url,
  signin_url
)
  # Call AWS STS to create the temporary security credentials and
  # a session token.
  session = sts_client.get_federation_token(
    name: user_name,
    duration_seconds: duration_seconds,
    policy: policy
  )

  # Create a JSON object that contains the temporary credentials and
  # the session token that are returned by AWS STS.
  session_json = {
    sessionId: session.credentials[:access_key_id],
    sessionKey: session.credentials[:secret_access_key],
    sessionToken: session.credentials[:session_token]
  }.to_json

  # Get the sign-in token by calling the AWS STS federation endpoint
  # with the sign-in URL, the temporary credentials, and the
  # session token.
  get_signin_token_url = signin_url +
    '?Action=getSigninToken' +
    '&SessionType=json&Session=' +
    CGI.escape(session_json)
  returned_content = URI.parse(get_signin_token_url).read

  # Extract the sign-in token from the information returned
  # by the AWS STS federation endpoint.
  signin_token = JSON.parse(returned_content)['SigninToken']
  signin_token_param = '&SigninToken=' + CGI.escape(signin_token)

  # Create the URL to give to the user.
  issuer_param = '&Issuer=' + CGI.escape(issuer_url)
  destination_param = '&Destination=' + CGI.escape(console_url)
  signin_url = signin_url + '?Action=login' + signin_token_param +
    issuer_param + destination_param

  return signin_url
rescue StandardError => e
  puts "Error getting federated user console sign-in URL: #{e.message}"
  return 'Error'
end

# Full example call:
def run_me
  user_name = 'this-should-not-work' # 'my-user'
  duration_seconds = 1_800 # 30 minutes.

  # Allow the user to take all actions within
  # AWS Simple Notification Service (AWS SNS) across the AWS account.
  policy = "{\"Version\":\"2012-10-17\",\"Statement\":{\"Effect\":\"Allow\",\"Action\":\"sns:*\",\"Resource\":\"*\"}}"

  issuer_url = 'https://mysignin.internal.mycompany.com/'
  console_url = 'https://console.aws.amazon.com/sns'
  signin_url = 'https://signin.aws.amazon.com/federation'
  sts_client = Aws::STS::Client.new

  puts "Attempting to get the sign-in URL for the user '#{user_name}' to " \
    'the AWS Management Console, starting with the root URL of ' \
    "'#{signin_url}'..."

  signin_url = get_federated_user_console_sign_in_url(
    sts_client,
    user_name,
    duration_seconds,
    policy,
    issuer_url,
    console_url,
    signin_url
  )

  if signin_url == 'Error'
    puts 'Could not get the signin URL for the user.'
  else
    puts "The sign-in URL for the user is:\n\n"
    puts signin_url
    puts "\n\nThis sign-in URL is valid for the next " \
      "#{duration_seconds} seconds."
  end
end

run_me if $PROGRAM_NAME == __FILE__
