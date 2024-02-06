<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

/*
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

$url = "https://auth.amazonworkdocs.com/oauth?app_id=" . $appId . "&auth_type=ImplicitGrant&redirect_uri="
    . $redirectUri . "&scopes=workdocs.content.read&state=xyz";

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
echo "<a href='https://docs.aws.amazon.com/workdocs/latest/developerguide/wd-auth-user.html'>";
echo "Amazon WorkDocs Developer Guide</a></p>";

$guzzle = new httpClient([
    'base_uri' => $url
]);
$response = $guzzle->request('GET');

// snippet-end:[workdocs.php.get_authorization_token.main]
// snippet-end:[workdocs.php.get_authorization_token.complete]
// snippet-sourceauthor:[jschwarzwalder (AWS)]
