<?php

# Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
# SPDX-License-Identifier: Apache-2.0

namespace Tests;

use App\Models\Item;
use Aws\Rds\RdsClient;
use Aws\SesV2\SesV2Client;
use Mockery;

class AuroraItemTrackerTest extends TestCase
{
    protected static RdsClient $client;

    /**
     * @group unit
     */
    public function test_the_items_api_route()
    {
        $mockClient = Mockery::mock('Aws\RDSDataService\RDSDataServiceClient');
        $mockResult = Mockery::mock('Result');
        $mockResult->shouldReceive('get')->andReturn("{}");
        $mockClient->shouldReceive('executeStatement')->andReturn($mockResult);
        $item = new Item($mockClient);
        $this->app->instance(Item::class, $item);

        $mockSesClient = Mockery::mock('Aws\SesV2\SesV2Client');
        $mockSesClient->shouldReceive('sendEmail');
        $this->app->instance(SesV2Client::class, $mockSesClient);

        $response = $this->get('/api/items');
        $response->assertStatus(200);
        $response->assertOk();

        $response = $this->get('/api/items/archive');
        $response->assertStatus(200);

        $response = $this->get('/api/items/all');
        $response->assertStatus(200);

        $response = $this->post('/api/items', [
            "name" => "test name",
            "guide" => "guide name",
            "description" => "description",
            "status" => "status",
        ]);
        $response->assertStatus(200);

        $response = $this->put('/api/items/2');
        $response->assertStatus(200);

        $response = $this->post('/api/report', [
            'email' => 'success@simulator.amazonses.com',
            'status' => 'all',
        ]);
        $response->assertStatus(200);
    }

    /**
     * @group unit
     */
    public function test_it_gets_items_by_state()
    {
        $mockClient = Mockery::mock('Aws\RDSDataService\RDSDataServiceClient');
        $mockResult = Mockery::mock('Result');
        $mockResult->shouldReceive('get')->andReturn("{}");
        $mockClient->shouldReceive('executeStatement')->andReturn($mockResult);
        $item = new Item($mockClient);
        $this->app->instance(Item::class, $item);
        $this->assertJson($item->getItemsByState());
        $this->assertJson($item->getItemsByState('active'));
        $this->assertJson($item->getItemsByState('archive'));
        $this->assertJson($item->getItemsByState('all'));
    }
}
