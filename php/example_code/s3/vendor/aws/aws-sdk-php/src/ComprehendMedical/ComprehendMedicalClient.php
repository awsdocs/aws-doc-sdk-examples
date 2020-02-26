<?php
namespace Aws\ComprehendMedical;

use Aws\AwsClient;

/**
 * This client is used to interact with the **AWS Comprehend Medical** service.
 * @method \Aws\Result describeEntitiesDetectionV2Job(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeEntitiesDetectionV2JobAsync(array $args = [])
 * @method \Aws\Result describePHIDetectionJob(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describePHIDetectionJobAsync(array $args = [])
 * @method \Aws\Result detectEntities(array $args = [])
 * @method \GuzzleHttp\Promise\Promise detectEntitiesAsync(array $args = [])
 * @method \Aws\Result detectEntitiesV2(array $args = [])
 * @method \GuzzleHttp\Promise\Promise detectEntitiesV2Async(array $args = [])
 * @method \Aws\Result detectPHI(array $args = [])
 * @method \GuzzleHttp\Promise\Promise detectPHIAsync(array $args = [])
 * @method \Aws\Result inferICD10CM(array $args = [])
 * @method \GuzzleHttp\Promise\Promise inferICD10CMAsync(array $args = [])
 * @method \Aws\Result inferRxNorm(array $args = [])
 * @method \GuzzleHttp\Promise\Promise inferRxNormAsync(array $args = [])
 * @method \Aws\Result listEntitiesDetectionV2Jobs(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listEntitiesDetectionV2JobsAsync(array $args = [])
 * @method \Aws\Result listPHIDetectionJobs(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listPHIDetectionJobsAsync(array $args = [])
 * @method \Aws\Result startEntitiesDetectionV2Job(array $args = [])
 * @method \GuzzleHttp\Promise\Promise startEntitiesDetectionV2JobAsync(array $args = [])
 * @method \Aws\Result startPHIDetectionJob(array $args = [])
 * @method \GuzzleHttp\Promise\Promise startPHIDetectionJobAsync(array $args = [])
 * @method \Aws\Result stopEntitiesDetectionV2Job(array $args = [])
 * @method \GuzzleHttp\Promise\Promise stopEntitiesDetectionV2JobAsync(array $args = [])
 * @method \Aws\Result stopPHIDetectionJob(array $args = [])
 * @method \GuzzleHttp\Promise\Promise stopPHIDetectionJobAsync(array $args = [])
 */
class ComprehendMedicalClient extends AwsClient {}
