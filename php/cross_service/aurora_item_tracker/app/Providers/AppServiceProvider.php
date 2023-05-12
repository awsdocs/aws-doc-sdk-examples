<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace App\Providers;

use Aws\Laravel\AwsFacade as AWS;
use Aws\RDSDataService\RDSDataServiceClient;
use Aws\SesV2\SesV2Client;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    public function register()
    {
        $this->app->bind(SesV2Client::class, function () {
            return AWS::createClient('sesv2');
        });
        $this->app->bind(RDSDataServiceClient::class, function () {
            return AWS::createClient('rdsdataservice');
        });
    }
}
