<?php
namespace Aws\S3Control;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS S3 Control** service.
 * @method \Aws\Result createAccessPoint(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createAccessPointAsync(array $args = [])
 * @method \Aws\Result createJob(array $args = [])
 * @method \GuzzleHttp\Promise\Promise createJobAsync(array $args = [])
 * @method \Aws\Result deleteAccessPoint(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteAccessPointAsync(array $args = [])
 * @method \Aws\Result deleteAccessPointPolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deleteAccessPointPolicyAsync(array $args = [])
 * @method \Aws\Result deletePublicAccessBlock(array $args = [])
 * @method \GuzzleHttp\Promise\Promise deletePublicAccessBlockAsync(array $args = [])
 * @method \Aws\Result describeJob(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeJobAsync(array $args = [])
 * @method \Aws\Result getAccessPoint(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getAccessPointAsync(array $args = [])
 * @method \Aws\Result getAccessPointPolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getAccessPointPolicyAsync(array $args = [])
 * @method \Aws\Result getAccessPointPolicyStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getAccessPointPolicyStatusAsync(array $args = [])
 * @method \Aws\Result getPublicAccessBlock(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getPublicAccessBlockAsync(array $args = [])
 * @method \Aws\Result listAccessPoints(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listAccessPointsAsync(array $args = [])
 * @method \Aws\Result listJobs(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listJobsAsync(array $args = [])
 * @method \Aws\Result putAccessPointPolicy(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putAccessPointPolicyAsync(array $args = [])
 * @method \Aws\Result putPublicAccessBlock(array $args = [])
 * @method \GuzzleHttp\Promise\Promise putPublicAccessBlockAsync(array $args = [])
 * @method \Aws\Result updateJobPriority(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateJobPriorityAsync(array $args = [])
 * @method \Aws\Result updateJobStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateJobStatusAsync(array $args = [])
 */
class S3ControlClient extends AwsClient 
{
    public static function getArguments()
    {
        $args = parent::getArguments();
        return $args + [
            'use_dual_stack_endpoint' => [
                'type' => 'config',
                'valid' => ['bool'],
                'doc' => 'Set to true to send requests to an S3 Control Dual Stack'
                    . ' endpoint by default, which enables IPv6 Protocol.'
                    . ' Can be enabled or disabled on individual operations by setting'
                    . ' \'@use_dual_stack_endpoint\' to true or false.',
                'default' => false,
            ],
        ];
    }

    /**
     * {@inheritdoc}
     *
     * In addition to the options available to
     * {@see Aws\AwsClient::__construct}, S3ControlClient accepts the following
     * option:
     *
     * - use_dual_stack_endpoint: (bool) Set to true to send requests to an S3
     *   Control Dual Stack endpoint by default, which enables IPv6 Protocol.
     *   Can be enabled or disabled on individual operations by setting
     *   '@use_dual_stack_endpoint\' to true or false. Note:
     *   you cannot use it together with an accelerate endpoint.
     *
     * @param array $args
     */
    public function __construct(array $args)
    {
        parent::__construct($args);
        $stack = $this->getHandlerList();
        $stack->appendBuild(
            S3ControlEndpointMiddleware::wrap(
                $this->getRegion(),
                [
                    'dual_stack' => $this->getConfig('use_dual_stack_endpoint'),
                ]
            ),
            's3control.endpoint_middleware'
        );
    }
}
