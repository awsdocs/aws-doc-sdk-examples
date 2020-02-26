<?php
namespace Aws\CodeGuruReviewer;

use Aws\AwsClient;

/**
 * This client is used to interact with the **Amazon CodeGuru Reviewer** service.
 * @method \Aws\Result associateRepository(array $args = [])
 * @method \GuzzleHttp\Promise\Promise associateRepositoryAsync(array $args = [])
 * @method \Aws\Result describeRepositoryAssociation(array $args = [])
 * @method \GuzzleHttp\Promise\Promise describeRepositoryAssociationAsync(array $args = [])
 * @method \Aws\Result disassociateRepository(array $args = [])
 * @method \GuzzleHttp\Promise\Promise disassociateRepositoryAsync(array $args = [])
 * @method \Aws\Result listRepositoryAssociations(array $args = [])
 * @method \GuzzleHttp\Promise\Promise listRepositoryAssociationsAsync(array $args = [])
 */
class CodeGuruReviewerClient extends AwsClient {}
