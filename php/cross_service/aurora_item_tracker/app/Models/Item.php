<?php

namespace App\Models;

use Aws\RDSDataService\RDSDataServiceClient;
use Aws\Laravel\AwsFacade as AWS;

class Item
{
    protected RDSDataServiceClient $client;
    protected array $connection = [];

    public function __construct()
    {
        $this->client = AWS::createClient('rdsdataservice');
        $this->connection['database'] = env('DATABASE');
        $this->connection['resourceArn'] = env('RESOURCE_ARN');
        $this->connection['secretArn'] = env('SECRET_ARN');
    }

    public function getItemsByState($state = null)
    {
        $archive = 0;
        if ($state == 'archive') {
            $archive = "1";
        }
        if ($state == 'all') {
            $archive = "0, 1";
        }
        $this->connection['formatRecordsAs'] = 'JSON';
        $this->connection['sql'] =
            "SELECT *, work_item_id as id, username as name FROM work_items WHERE archive in ($archive);";
        return $this->client->executeStatement($this->connection)->get('formattedRecords');
    }

    public function storeItem(mixed $input)
    {
        $this->connection['sql'] =
            "INSERT INTO work_items (username, guide, description, status) VALUES ('{$input['name']}',
                              '{$input['guide']}', '{$input['description']}', '{$input['status']}');";
        return $this->client->executeStatement($this->connection);
    }

    public function archiveItem($itemId)
    {
        if (!is_numeric($itemId)) {
            return false;
        }
        $this->connection['sql'] = "UPDATE work_items SET archive = true WHERE work_item_id = $itemId;";
        return $this->client->executeStatement($this->connection)->get('records');
    }
}
