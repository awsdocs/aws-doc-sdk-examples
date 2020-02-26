<?php
namespace Aws\ComputeOptimizer;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Compute Optimizer** service.
 * @method \Aws\Result getAutoScalingGroupRecommendations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getAutoScalingGroupRecommendationsAsync(array $args = [])
 * @method \Aws\Result getEC2InstanceRecommendations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getEC2InstanceRecommendationsAsync(array $args = [])
 * @method \Aws\Result getEC2RecommendationProjectedMetrics(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getEC2RecommendationProjectedMetricsAsync(array $args = [])
 * @method \Aws\Result getEnrollmentStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getEnrollmentStatusAsync(array $args = [])
 * @method \Aws\Result getRecommendationSummaries(array $args = [])
 * @method \GuzzleHttp\Promise\Promise getRecommendationSummariesAsync(array $args = [])
 * @method \Aws\Result updateEnrollmentStatus(array $args = [])
 * @method \GuzzleHttp\Promise\Promise updateEnrollmentStatusAsync(array $args = [])
 */
class ComputeOptimizerClient extends AwsClient {}
