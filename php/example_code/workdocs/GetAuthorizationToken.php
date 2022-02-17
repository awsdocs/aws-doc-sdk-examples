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
 * For more information about creating a WorkDocs application see the WorkDocs Developer Guide at
 * https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html
 *
 */
// snippet-start:[workdocs.php.get_authorization_token.complete]
// snippet-start:[workdocs.php.get_authorization_token.import]
require 'vendor/autoload.php';

use GuzzleHttp\Client as httpClient;

// snippet-end:[workdocs.php.get_authorization_token.import]
// snippet-start:[workdocs.php.get_authorization_token.main]
$appId = 'appid';
$redirectUri = 'https://';

$url = "https://auth.amazonworkdocs.com/oauth?app_id=" . $appId . "&auth_type=ImplicitGrant&redirect_uri=" . $redirectUri . "&scopes=workdocs.content.read&state=xyz";

echo "<p>Url = <a href = '" . $url . "' target='_blank'>Request Authentication token</a></p>";
echo "<ol>";
echo "<li>Click on Link above</li>";
echo "<li>Enter the Amazon WorkDocs site name. Note that it is case sensitive.</li>";
echo "<li>Login to your Amazon WorkDocs site </li>";
echo "<li>To Grant or deny your application access to Amazon WorkDocs, select <b>Accept</b></li>";
echo "<li>Copy the URL you are taken</li>";
echo "<li>Save the string after <b>access_token=</b> and <b>region=</b></li>";
echo "</ol>";
echo "<p> For more information about Authentication and Access Control for User Applications see ";
echo "<a href='https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html'>Amazon WorkDocs Developer Guide</a></p>";

$guzzle = new httpClient([
    'base_uri' => $url
]);
$response = $guzzle->request('GET', '');

// snippet-end:[workdocs.php.get_authorization_token.main]
// snippet-end:[workdocs.php.get_authorization_token.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[GetAuthorizationToken.php demonstrates how to retrieve authorization token for your Amazon WorkDocs Application.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon WorkDocs]
// snippet-service:[workdocs]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2019-02-09]
// snippet-sourceauthor:[jschwarzwalder (AWS)] 