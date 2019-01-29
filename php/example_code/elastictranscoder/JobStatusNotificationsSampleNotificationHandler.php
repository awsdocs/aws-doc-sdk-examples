<?php
/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 *  ABOUT THIS PHP SAMPLE: This sample is part of the Elastic Transcoder Developer Guide topic at
 *  https://alpha-docs-aws.amazon.com/elastictranscoder/latest/developerguide/sample-code.html
 *
 */
$tmp_path = '/tmp';

# Get raw notification data from the POST.
$data = file_get_contents('php://input');
$notification = json_decode($data, true);

if ($notification['Type'] == 'SubscriptionConfirmation') {
  $subscription_file = "$tmp_path/subscribe_requests.txt";

  # Dump subscription request into temp file.
  file_put_contents($subscription_file, "$data\n", FILE_APPEND | LOCK_EX);
  try {
    # Automatically handle subscription confirmation requests.
    echo 'url: ', $notification['SubscribeURL'];
    $response = file_get_contents($notification['SubscribeURL']);
    file_put_contents($subscription_file, "$response\n", FILE_APPEND | LOCK_EX);
  } catch (Exception $e) {
    file_put_contents($subscription_file, "Error confirming subscription: {$e->getMessage()}\n", FILE_APPEND | LOCK_EX);
  }
} else if ($notification['Type'] == 'Notification') {
  # Handle Elastic Transcoder notifications.  In this example, we write them to
  # $tmp_path/<job-id>.
  $job_status = json_decode($notification['Message'], true);
  file_put_contents("$tmp_path/{$job_status['jobId']}", json_encode($job_status) . "\n", FILE_APPEND |  LOCK_EX);
} else {
  # Write unknown notifications out to disk.
  file_put_contents("$tmp_path/unknown_notification.txt", "$data\n", FILE_APPEND | LOCK_EX);
}

// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[JobStatusNotificationsSampleNotificationHandler.php demonstrates how to create a notification handler for an Elastic Transcoder job.]
// snippet-keyword:[PHP]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon Elastic Transcoder]
// snippet-service:[elastictranscoder]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[]

?>
