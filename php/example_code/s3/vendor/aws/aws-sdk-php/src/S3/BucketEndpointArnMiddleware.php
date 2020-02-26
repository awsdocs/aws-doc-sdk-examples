<?php
namespace Aws\S3;

use Aws\Api\Service;
use Aws\Arn\ArnInterface;
use Aws\Arn\ArnParser;
use Aws\Arn\Exception\InvalidArnException;
use Aws\Arn\S3\AccessPointArn;
use Aws\CommandInterface;
use Aws\Endpoint\PartitionEndpointProvider;
use Aws\Exception\InvalidRegionException;
use Aws\Exception\UnresolvedEndpointException;
use Aws\S3\Exception\S3Exception;
use Psr\Http\Message\RequestInterface;

/**
 * Checks for access point ARN in members targeting BucketName, modifying
 * endpoint as appropriate
 *
 * @internal
 */
class BucketEndpointArnMiddleware
{
    /** @var Service */
    private $service;

    /** @var callable */
    private $nextHandler;

    /** @var string */
    private $region;

    /** @var $config */
    private $config;

    /** @var PartitionEndpointProvider */
    private $partitionProvider;

    /** @var array */
    private $nonArnableCommands = ['CreateBucket'];

    /**
     * Create a middleware wrapper function.
     *
     * @param Service $service
     * @param $region
     * @param array $config
     * @return callable
     */
    public static function wrap(
        Service $service,
        $region,
        array $config

    ) {
        return function (callable $handler) use ($service, $region, $config) {
            return new self($handler, $service, $region, $config);
        };
    }

    public function __construct(
        callable $nextHandler,
        Service $service,
        $region,
        array $config = []
    ) {
        $this->partitionProvider = PartitionEndpointProvider::defaultProvider();
        $this->region = $region;
        $this->service = $service;
        $this->config = $config;
        $this->nextHandler = $nextHandler;
    }

    public function __invoke(CommandInterface $cmd, RequestInterface $req)
    {
        $nextHandler = $this->nextHandler;

        $op = $this->service->getOperation($cmd->getName())->toArray();
        if (!empty($op['input']['shape'])) {
            $service = $this->service->toArray();
            if (!empty($input = $service['shapes'][$op['input']['shape']])) {
                foreach ($input['members'] as $key => $member) {
                    if ($member['shape'] === 'BucketName') {
                        $arnableKey = $key;
                        break;
                    }
                }

                if (!empty($arnableKey) && ArnParser::isArn($cmd[$arnableKey])) {

                    try {
                        // Throw for commands that do not support ARN inputs
                        if (in_array($cmd->getName(), $this->nonArnableCommands)) {
                            throw new S3Exception(
                                'ARN values cannot be used in the bucket field for'
                                    . ' the ' . $cmd->getName() . ' operation.',
                                $cmd
                            );
                        }

                        $arn = ArnParser::parse($cmd[$arnableKey]);
                        $partition = $this->validateArn($arn);

                        $host = $this->generateAccessPointHost($arn, $req);

                        // Remove encoded bucket string from path
                        $path = $req->getUri()->getPath();
                        $encoded = rawurlencode($cmd[$arnableKey]);
                        $len = strlen($encoded) + 1;
                        if (substr($path, 0, $len) === "/{$encoded}") {
                            $path = substr($path, $len);
                        }
                        if (empty($path)) {
                            $path = '';
                        }

                        // Set modified request
                        $req = $req->withUri(
                            $req->getUri()->withHost($host)->withPath($path)
                        );

                        // Update signing region based on ARN data if configured to do so
                        if ($this->config['use_arn_region']->isUseArnRegion()) {
                            $region = $arn->getRegion();
                        } else {
                            $region = $this->region;
                        }
                        $endpointData = $partition([
                            'region' => $region,
                            'service' => $arn->getService()
                        ]);
                        $cmd['@context']['signing_region'] = $endpointData['signingRegion'];

                    } catch (InvalidArnException $e) {
                        // Add context to ARN exception
                        throw new S3Exception(
                            'Bucket parameter parsed as ARN and failed with: '
                                . $e->getMessage(),
                            $cmd,
                            [],
                            $e
                        );
                    }
                }
            }
        }

        return $nextHandler($cmd, $req);
    }

    private function generateAccessPointHost(
        AccessPointArn $arn,
        RequestInterface $req
    ) {
        $host = $arn->getResourceId() . '-' . $arn->getAccountId()
            . '.s3-accesspoint';
        if (!empty($this->config['dual_stack'])) {
            $host .= '.dualstack';
        }
        if (!empty($this->config['use_arn_region']->isUseArnRegion())) {
            $region = $arn->getRegion();
        } else {
            $region = $this->region;
        }
        $host .= '.' . $region . '.' . $this->getPartitionSuffix($arn);

        return $host;
    }

    private function getPartitionSuffix(ArnInterface $arn)
    {
        $partition = $this->partitionProvider->getPartition(
            $arn->getRegion(),
            $arn->getService()
        );
        return $partition->getDnsSuffix();
    }

    private function getSigningRegion($region)
    {
        $partition = PartitionEndpointProvider::defaultProvider()->getPartition($region, 's3');
        $data = $partition->toArray();
        if (isset($data['services']['s3']['endpoints'][$region]['credentialScope']['region'])) {
            return $data['services']['s3']['endpoints'][$region]['credentialScope']['region'];
        }
        return $region;
    }

    private function isMatchingSigningRegion($arnRegion, $clientRegion)
    {
        $arnRegion = strtolower($arnRegion);
        $clientRegion = $this->stripPseudoRegions(strtolower($clientRegion));
        if ($arnRegion === $clientRegion) {
            return true;
        }
        if ($this->getSigningRegion($clientRegion) === $arnRegion) {
            return true;
        }
        return false;
    }

    private function stripPseudoRegions($region)
    {
        return str_replace(['fips-', '-fips'], ['', ''], $region);
    }

    /**
     * Validates an ARN, returning a partition object corresponding to the ARN
     * if successful
     *
     * @param $arn
     * @return \Aws\Endpoint\Partition
     */
    private function validateArn($arn)
    {
        if ($arn instanceof AccessPointArn) {
            // Accelerate is not supported with access points
            if (!empty($this->config['accelerate'])) {
                throw new UnresolvedEndpointException(
                    'Accelerate is currently not supported with access points.'
                    . ' Please disable accelerate or do not supply an access'
                    . ' point ARN.');
            }

            // Path-style is not supported with access points
            if (!empty($this->config['path_style'])) {
                throw new UnresolvedEndpointException(
                    'Path-style addressing is currently not supported with'
                    . ' access points. Please disable path-style or do not'
                    . ' supply an access point ARN.');
            }

            // Custom endpoint is not supported with access points
            if (!is_null($this->config['endpoint'])) {
                throw new UnresolvedEndpointException(
                    'A custom endpoint has been supplied along with an access'
                    . ' point ARN, and these are not compatible with each other.'
                    . ' Please only use one or the other.');
            }

            // Get partitions for ARN and client region
            $arnPart = $this->partitionProvider->getPartition(
                $arn->getRegion(),
                's3'
            );
            $clientPart = $this->partitionProvider->getPartition(
                $this->region,
                's3'
            );

            // If client partition not found, try removing pseudo-region qualifiers
            if (!($clientPart->isRegionMatch($this->region, 's3'))) {
                $clientPart = $this->partitionProvider->getPartition(
                    $this->stripPseudoRegions($this->region),
                    's3'
                );
            }

            // Verify that the partition matches for supplied partition and region
            if ($arn->getPartition() !== $clientPart->getName()) {
                throw new InvalidRegionException('The supplied ARN partition'
                    . " does not match the client's partition.");
            }
            if ($clientPart->getName() !== $arnPart->getName()) {
                throw new InvalidRegionException('The corresponding partition'
                    . ' for the supplied ARN region does not match the'
                    . " client's partition.");
            }

            // Ensure ARN region matches client region unless
            // configured for using ARN region over client region
            if (!($this->isMatchingSigningRegion($arn->getRegion(), $this->region))) {
                if (empty($this->config['use_arn_region'])
                    || !($this->config['use_arn_region']->isUseArnRegion())
                ) {
                    throw new InvalidRegionException('The region'
                        . " specified in the ARN (" . $arn->getRegion()
                        . ") does not match the client region ("
                        . "{$this->region}).");
                }
            }

            return $arnPart;
        }

        throw new InvalidArnException('Provided ARN was not'
            . ' a valid S3 access point ARN');
    }
}
