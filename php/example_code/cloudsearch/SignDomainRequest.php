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
 * ABOUT THIS PHP SAMPLE: This sample is part of the SDK for PHP Developer Guide topic at
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/service_cloudsearch-custom-requests.html
 *
 */
// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.complete]
// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.import]
use Aws\Credentials\CredentialProvider;
use Aws\Signature\SignatureV4;
use GuzzleHttp\Client;
use GuzzleHttp\Psr7\Request;

// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.import]
/**
 * Sign Custom Amazon CloudSearch Domain Request.
 *
 * This code expects that you have AWS credentials set up per:
 * https://docs.aws.amazon.com/sdk-for-php/v3/developer-guide/guide_credentials.html
 */
// Create the client
// snippet-start:[cloudsearch.php.sign_cloudsearch_domain_request.main] 

// Prepare a CloudSearch domain request
$request = new Request(
    'GET',
    'https://<your-domain>.<region-of-domain>.cloudsearch.amazonaws.com/2013-01-01/search?q=star+wars&return=title'
);

// Get your credentials from the environment
$credentials = call_user_func(CredentialProvider::defaultProvider())->wait();

// Construct a request signer
$signer = new SignatureV4('cloudsearch', '<region-of-domain>');

// Sign the request
$request = $signer->signRequest($request, $credentials);

// Send the request
$response = (new Client)->send($request);
$results = json_decode($response->getBody());
if ($results->hits->found > 0) {
    echo $results->hits->hit[0]->fields->title . "\n";
}
// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.main]
// snippet-end:[cloudsearch.php.sign_cloudsearch_domain_request.complete]
// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[SignDomainRequest.php demonstrates how to sign a custom Amazon CloudSearch Domain Request.]
// snippet-keyword:[PHP]
// snippet-sourcesyntax:[php]
// snippet-keyword:[AWS SDK for PHP v3]
// snippet-keyword:[Code Sample]
// snippet-keyword:[Amazon CloudSearch]
// snippet-keyword:[signRequest]
// snippet-service:[cloudsearch]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[]
// snippet-sourceauthor:[jschwarzwalder (AWS)]