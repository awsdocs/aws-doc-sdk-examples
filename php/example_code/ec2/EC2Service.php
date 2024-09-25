<?php
// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

namespace Ec2;

use Aws\Ec2\Ec2Client;
use Aws\Ec2\Exception\Ec2Exception;

class EC2Service
{
    protected Ec2Client $ec2Client;
    protected string $region;

    public function getRegion(): string
    {
        return $this->region;
    }

    public function __construct($client = null, $region = 'us-west-2', $version = 'latest', $profile = 'default')
    {
        if (gettype($client) == Ec2Client::class) {
            $this->ec2Client = $client;
            return;
        }
        $this->ec2Client = new Ec2Client([
            'region' => $region,
            'version' => $version,
            'profile' => $profile,
        ]);
        $this->region = $region;
        /* Inline declaration example
        // snippet-start:[php.example_code.ec2.basics.createClient]
        $ec2Client = new Ec2Client(['region' => 'us-west-2', 'version' => 'latest', 'profile' => 'default']);
        // snippet-end:[php.example_code.ec2.basics.createClient]
        */
    }

    // snippet-start:[php.example_code.ec2.service.createVpcEndpoint]

    /**
     * @param string $serviceName
     * @param string $vpcId
     * @param array $routeTableIds
     * @return array
     */
    public function createVpcEndpoint(string $serviceName, string $vpcId, array $routeTableIds): array
    {
        $result = $this->ec2Client->createVpcEndpoint([
            'ServiceName' => $serviceName,
            'VpcId' => $vpcId,
            'RouteTableIds' => $routeTableIds,
        ]);

        return $result["VpcEndpoint"];
    }

    // snippet-end:[php.example_code.ec2.service.createVpcEndpoint]

    // snippet-start:[php.example_code.ec2.service.createVpc]

    public function createVpc($cidr)
    {
        $result = $this->ec2Client->createVpc([
            "CidrBlock" => $cidr,
        ]);
        return $result['Vpc'];
    }

    // snippet-end:[php.example_code.ec2.service.createVpc]

    // snippet-start:[php.example_code.ec2.service.deleteVpc]

    public function deleteVpc(mixed $vpcId)
    {
        $result = $this->ec2Client->deleteVpc([
            "VpcId" => $vpcId,
        ]);
    }

    // snippet-end:[php.example_code.ec2.service.deleteVpc]

    // snippet-start:[php.example_code.ec2.service.deleteVpcEndpoint]

    public function deleteVpcEndpoint(mixed $vpcEndpointId)
    {
        $result = $this->ec2Client->deleteVpcEndpoints([
            "VpcEndpointIds" => [$vpcEndpointId],
        ]);
    }

    // snippet-end:[php.example_code.ec2.service.deleteVpcEndpoint]

    // snippet-start:[php.example_code.ec2.service.describeRouteTables]

    public function describeRouteTables(array $routeTableIds = [], array $filters = [])
    {
        $parameters = [];
        if($routeTableIds){
            $parameters['RouteTableIds'] = $routeTableIds;
        }
        if($filters){
            $parameters['Filters'] = $filters;
        }
        try {
            $paginator = $this->ec2Client->getPaginator("DescribeRouteTables", $parameters);
            $contents = [];
            foreach ($paginator as $result) {
                foreach ($result['RouteTables'] as $object) {
                    $contents[] = $object['RouteTableId'];
                }
            }
        }catch (Ec2Exception $caught){
            echo "There was a problem paginating the results of DescribeRouteTables: {$caught->getAwsErrorMessage()}\n";
            throw $caught;
        }
        return $contents;
    }

    // snippet-end:[php.example_code.ec2.service.describeRouteTables]

    // snippet-start:[php.example_code.ec2.service.waitForVpcAvailable]

    public function waitForVpcAvailable(string $VpcId)
    {

        $waiter = $this->ec2Client->getWaiter("VpcAvailable", [
            'VpcIds' => [$VpcId],
        ]);

        $promise = $waiter->promise();
        $promise->wait();

    }

    // snippet-end:[php.example_code.ec2.service.waitForVpcAvailable]
}
