<?php

namespace Tests\Feature;

use Aws\Laravel\AwsFacade;
use Aws\Rds\RdsClient;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;
use Aws\Laravel\AwsFacade as AWS;


class ExampleTest extends TestCase
{
    protected static RdsClient $client;

    public function test_the_application_returns_a_successful_response()
    {
        $response = $this->get('/');

        $response->assertStatus(200);
    }

    public function test_the_items_api_route()
    {
        $response = $this->get('/api/items');
        $response->assertStatus(200);

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
}
