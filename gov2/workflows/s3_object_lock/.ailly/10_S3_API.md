  * Constants
  * func GetHostIDMetadata(metadata middleware.Metadata) (string, bool)
  * func NewDefaultEndpointResolver() *internalendpoints.Resolver
  * func WithAPIOptions(optFns ...func(*middleware.Stack) error) func(*Options)
  * func WithEndpointResolver(v EndpointResolver) func(*Options)deprecated
  * func WithEndpointResolverV2(v EndpointResolverV2) func(*Options)
  * func WithPresignClientFromClientOptions(optFns ...func(*Options)) func(*PresignOptions)
  * func WithPresignExpires(dur time.Duration) func(*PresignOptions)
  * func WithSigV4ASigningRegions(regions []string) func(*Options)
  * func WithSigV4SigningName(name string) func(*Options)
  * func WithSigV4SigningRegion(region string) func(*Options)
  * type AbortMultipartUploadInput
  * type AbortMultipartUploadOutput
  * type AuthResolverParameters
  * type AuthSchemeResolver
  * type BucketExistsWaiter
  *     * func NewBucketExistsWaiter(client HeadBucketAPIClient, optFns ...func(*BucketExistsWaiterOptions)) *BucketExistsWaiter
  *     * func (w *BucketExistsWaiter) Wait(ctx context.Context, params *HeadBucketInput, maxWaitDur time.Duration, ...) error
    * func (w *BucketExistsWaiter) WaitForOutput(ctx context.Context, params *HeadBucketInput, maxWaitDur time.Duration, ...) (*HeadBucketOutput, error)
  * type BucketExistsWaiterOptions
  * type BucketNotExistsWaiter
  *     * func NewBucketNotExistsWaiter(client HeadBucketAPIClient, optFns ...func(*BucketNotExistsWaiterOptions)) *BucketNotExistsWaiter
  *     * func (w *BucketNotExistsWaiter) Wait(ctx context.Context, params *HeadBucketInput, maxWaitDur time.Duration, ...) error
    * func (w *BucketNotExistsWaiter) WaitForOutput(ctx context.Context, params *HeadBucketInput, maxWaitDur time.Duration, ...) (*HeadBucketOutput, error)
  * type BucketNotExistsWaiterOptions
  * type ChecksumValidationMetadata
  *     * func GetChecksumValidationMetadata(m middleware.Metadata) (ChecksumValidationMetadata, bool)
  * type Client
  *     * func New(options Options, optFns ...func(*Options)) *Client
    * func NewFromConfig(cfg aws.Config, optFns ...func(*Options)) *Client
  *     * func (c *Client) AbortMultipartUpload(ctx context.Context, params *AbortMultipartUploadInput, ...) (*AbortMultipartUploadOutput, error)
    * func (c *Client) CompleteMultipartUpload(ctx context.Context, params *CompleteMultipartUploadInput, ...) (*CompleteMultipartUploadOutput, error)
    * func (c *Client) CopyObject(ctx context.Context, params *CopyObjectInput, optFns ...func(*Options)) (*CopyObjectOutput, error)
    * func (c *Client) CreateBucket(ctx context.Context, params *CreateBucketInput, optFns ...func(*Options)) (*CreateBucketOutput, error)
    * func (c *Client) CreateMultipartUpload(ctx context.Context, params *CreateMultipartUploadInput, ...) (*CreateMultipartUploadOutput, error)
    * func (c *Client) CreateSession(ctx context.Context, params *CreateSessionInput, optFns ...func(*Options)) (*CreateSessionOutput, error)
    * func (c *Client) DeleteBucket(ctx context.Context, params *DeleteBucketInput, optFns ...func(*Options)) (*DeleteBucketOutput, error)
    * func (c *Client) DeleteBucketAnalyticsConfiguration(ctx context.Context, params *DeleteBucketAnalyticsConfigurationInput, ...) (*DeleteBucketAnalyticsConfigurationOutput, error)
    * func (c *Client) DeleteBucketCors(ctx context.Context, params *DeleteBucketCorsInput, optFns ...func(*Options)) (*DeleteBucketCorsOutput, error)
    * func (c *Client) DeleteBucketEncryption(ctx context.Context, params *DeleteBucketEncryptionInput, ...) (*DeleteBucketEncryptionOutput, error)
    * func (c *Client) DeleteBucketIntelligentTieringConfiguration(ctx context.Context, params *DeleteBucketIntelligentTieringConfigurationInput, ...) (*DeleteBucketIntelligentTieringConfigurationOutput, error)
    * func (c *Client) DeleteBucketInventoryConfiguration(ctx context.Context, params *DeleteBucketInventoryConfigurationInput, ...) (*DeleteBucketInventoryConfigurationOutput, error)
    * func (c *Client) DeleteBucketLifecycle(ctx context.Context, params *DeleteBucketLifecycleInput, ...) (*DeleteBucketLifecycleOutput, error)
    * func (c *Client) DeleteBucketMetricsConfiguration(ctx context.Context, params *DeleteBucketMetricsConfigurationInput, ...) (*DeleteBucketMetricsConfigurationOutput, error)
    * func (c *Client) DeleteBucketOwnershipControls(ctx context.Context, params *DeleteBucketOwnershipControlsInput, ...) (*DeleteBucketOwnershipControlsOutput, error)
    * func (c *Client) DeleteBucketPolicy(ctx context.Context, params *DeleteBucketPolicyInput, optFns ...func(*Options)) (*DeleteBucketPolicyOutput, error)
    * func (c *Client) DeleteBucketReplication(ctx context.Context, params *DeleteBucketReplicationInput, ...) (*DeleteBucketReplicationOutput, error)
    * func (c *Client) DeleteBucketTagging(ctx context.Context, params *DeleteBucketTaggingInput, ...) (*DeleteBucketTaggingOutput, error)
    * func (c *Client) DeleteBucketWebsite(ctx context.Context, params *DeleteBucketWebsiteInput, ...) (*DeleteBucketWebsiteOutput, error)
    * func (c *Client) DeleteObject(ctx context.Context, params *DeleteObjectInput, optFns ...func(*Options)) (*DeleteObjectOutput, error)
    * func (c *Client) DeleteObjectTagging(ctx context.Context, params *DeleteObjectTaggingInput, ...) (*DeleteObjectTaggingOutput, error)
    * func (c *Client) DeleteObjects(ctx context.Context, params *DeleteObjectsInput, optFns ...func(*Options)) (*DeleteObjectsOutput, error)
    * func (c *Client) DeletePublicAccessBlock(ctx context.Context, params *DeletePublicAccessBlockInput, ...) (*DeletePublicAccessBlockOutput, error)
    * func (c *Client) GetBucketAccelerateConfiguration(ctx context.Context, params *GetBucketAccelerateConfigurationInput, ...) (*GetBucketAccelerateConfigurationOutput, error)
    * func (c *Client) GetBucketAcl(ctx context.Context, params *GetBucketAclInput, optFns ...func(*Options)) (*GetBucketAclOutput, error)
    * func (c *Client) GetBucketAnalyticsConfiguration(ctx context.Context, params *GetBucketAnalyticsConfigurationInput, ...) (*GetBucketAnalyticsConfigurationOutput, error)
    * func (c *Client) GetBucketCors(ctx context.Context, params *GetBucketCorsInput, optFns ...func(*Options)) (*GetBucketCorsOutput, error)
    * func (c *Client) GetBucketEncryption(ctx context.Context, params *GetBucketEncryptionInput, ...) (*GetBucketEncryptionOutput, error)
    * func (c *Client) GetBucketIntelligentTieringConfiguration(ctx context.Context, params *GetBucketIntelligentTieringConfigurationInput, ...) (*GetBucketIntelligentTieringConfigurationOutput, error)
    * func (c *Client) GetBucketInventoryConfiguration(ctx context.Context, params *GetBucketInventoryConfigurationInput, ...) (*GetBucketInventoryConfigurationOutput, error)
    * func (c *Client) GetBucketLifecycleConfiguration(ctx context.Context, params *GetBucketLifecycleConfigurationInput, ...) (*GetBucketLifecycleConfigurationOutput, error)
    * func (c *Client) GetBucketLocation(ctx context.Context, params *GetBucketLocationInput, optFns ...func(*Options)) (*GetBucketLocationOutput, error)
    * func (c *Client) GetBucketLogging(ctx context.Context, params *GetBucketLoggingInput, optFns ...func(*Options)) (*GetBucketLoggingOutput, error)
    * func (c *Client) GetBucketMetricsConfiguration(ctx context.Context, params *GetBucketMetricsConfigurationInput, ...) (*GetBucketMetricsConfigurationOutput, error)
    * func (c *Client) GetBucketNotificationConfiguration(ctx context.Context, params *GetBucketNotificationConfigurationInput, ...) (*GetBucketNotificationConfigurationOutput, error)
    * func (c *Client) GetBucketOwnershipControls(ctx context.Context, params *GetBucketOwnershipControlsInput, ...) (*GetBucketOwnershipControlsOutput, error)
    * func (c *Client) GetBucketPolicy(ctx context.Context, params *GetBucketPolicyInput, optFns ...func(*Options)) (*GetBucketPolicyOutput, error)
    * func (c *Client) GetBucketPolicyStatus(ctx context.Context, params *GetBucketPolicyStatusInput, ...) (*GetBucketPolicyStatusOutput, error)
    * func (c *Client) GetBucketReplication(ctx context.Context, params *GetBucketReplicationInput, ...) (*GetBucketReplicationOutput, error)
    * func (c *Client) GetBucketRequestPayment(ctx context.Context, params *GetBucketRequestPaymentInput, ...) (*GetBucketRequestPaymentOutput, error)
    * func (c *Client) GetBucketTagging(ctx context.Context, params *GetBucketTaggingInput, optFns ...func(*Options)) (*GetBucketTaggingOutput, error)
    * func (c *Client) GetBucketVersioning(ctx context.Context, params *GetBucketVersioningInput, ...) (*GetBucketVersioningOutput, error)
    * func (c *Client) GetBucketWebsite(ctx context.Context, params *GetBucketWebsiteInput, optFns ...func(*Options)) (*GetBucketWebsiteOutput, error)
    * func (c *Client) GetObject(ctx context.Context, params *GetObjectInput, optFns ...func(*Options)) (*GetObjectOutput, error)
    * func (c *Client) GetObjectAcl(ctx context.Context, params *GetObjectAclInput, optFns ...func(*Options)) (*GetObjectAclOutput, error)
    * func (c *Client) GetObjectAttributes(ctx context.Context, params *GetObjectAttributesInput, ...) (*GetObjectAttributesOutput, error)
    * func (c *Client) GetObjectLegalHold(ctx context.Context, params *GetObjectLegalHoldInput, optFns ...func(*Options)) (*GetObjectLegalHoldOutput, error)
    * func (c *Client) GetObjectLockConfiguration(ctx context.Context, params *GetObjectLockConfigurationInput, ...) (*GetObjectLockConfigurationOutput, error)
    * func (c *Client) GetObjectRetention(ctx context.Context, params *GetObjectRetentionInput, optFns ...func(*Options)) (*GetObjectRetentionOutput, error)
    * func (c *Client) GetObjectTagging(ctx context.Context, params *GetObjectTaggingInput, optFns ...func(*Options)) (*GetObjectTaggingOutput, error)
    * func (c *Client) GetObjectTorrent(ctx context.Context, params *GetObjectTorrentInput, optFns ...func(*Options)) (*GetObjectTorrentOutput, error)
    * func (c *Client) GetPublicAccessBlock(ctx context.Context, params *GetPublicAccessBlockInput, ...) (*GetPublicAccessBlockOutput, error)
    * func (c *Client) HeadBucket(ctx context.Context, params *HeadBucketInput, optFns ...func(*Options)) (*HeadBucketOutput, error)
    * func (c *Client) HeadObject(ctx context.Context, params *HeadObjectInput, optFns ...func(*Options)) (*HeadObjectOutput, error)
    * func (c *Client) ListBucketAnalyticsConfigurations(ctx context.Context, params *ListBucketAnalyticsConfigurationsInput, ...) (*ListBucketAnalyticsConfigurationsOutput, error)
    * func (c *Client) ListBucketIntelligentTieringConfigurations(ctx context.Context, params *ListBucketIntelligentTieringConfigurationsInput, ...) (*ListBucketIntelligentTieringConfigurationsOutput, error)
    * func (c *Client) ListBucketInventoryConfigurations(ctx context.Context, params *ListBucketInventoryConfigurationsInput, ...) (*ListBucketInventoryConfigurationsOutput, error)
    * func (c *Client) ListBucketMetricsConfigurations(ctx context.Context, params *ListBucketMetricsConfigurationsInput, ...) (*ListBucketMetricsConfigurationsOutput, error)
    * func (c *Client) ListBuckets(ctx context.Context, params *ListBucketsInput, optFns ...func(*Options)) (*ListBucketsOutput, error)
    * func (c *Client) ListDirectoryBuckets(ctx context.Context, params *ListDirectoryBucketsInput, ...) (*ListDirectoryBucketsOutput, error)
    * func (c *Client) ListMultipartUploads(ctx context.Context, params *ListMultipartUploadsInput, ...) (*ListMultipartUploadsOutput, error)
    * func (c *Client) ListObjectVersions(ctx context.Context, params *ListObjectVersionsInput, optFns ...func(*Options)) (*ListObjectVersionsOutput, error)
    * func (c *Client) ListObjects(ctx context.Context, params *ListObjectsInput, optFns ...func(*Options)) (*ListObjectsOutput, error)
    * func (c *Client) ListObjectsV2(ctx context.Context, params *ListObjectsV2Input, optFns ...func(*Options)) (*ListObjectsV2Output, error)
    * func (c *Client) ListParts(ctx context.Context, params *ListPartsInput, optFns ...func(*Options)) (*ListPartsOutput, error)
    * func (c *Client) Options() Options
    * func (c *Client) PutBucketAccelerateConfiguration(ctx context.Context, params *PutBucketAccelerateConfigurationInput, ...) (*PutBucketAccelerateConfigurationOutput, error)
    * func (c *Client) PutBucketAcl(ctx context.Context, params *PutBucketAclInput, optFns ...func(*Options)) (*PutBucketAclOutput, error)
    * func (c *Client) PutBucketAnalyticsConfiguration(ctx context.Context, params *PutBucketAnalyticsConfigurationInput, ...) (*PutBucketAnalyticsConfigurationOutput, error)
    * func (c *Client) PutBucketCors(ctx context.Context, params *PutBucketCorsInput, optFns ...func(*Options)) (*PutBucketCorsOutput, error)
    * func (c *Client) PutBucketEncryption(ctx context.Context, params *PutBucketEncryptionInput, ...) (*PutBucketEncryptionOutput, error)
    * func (c *Client) PutBucketIntelligentTieringConfiguration(ctx context.Context, params *PutBucketIntelligentTieringConfigurationInput, ...) (*PutBucketIntelligentTieringConfigurationOutput, error)
    * func (c *Client) PutBucketInventoryConfiguration(ctx context.Context, params *PutBucketInventoryConfigurationInput, ...) (*PutBucketInventoryConfigurationOutput, error)
    * func (c *Client) PutBucketLifecycleConfiguration(ctx context.Context, params *PutBucketLifecycleConfigurationInput, ...) (*PutBucketLifecycleConfigurationOutput, error)
    * func (c *Client) PutBucketLogging(ctx context.Context, params *PutBucketLoggingInput, optFns ...func(*Options)) (*PutBucketLoggingOutput, error)
    * func (c *Client) PutBucketMetricsConfiguration(ctx context.Context, params *PutBucketMetricsConfigurationInput, ...) (*PutBucketMetricsConfigurationOutput, error)
    * func (c *Client) PutBucketNotificationConfiguration(ctx context.Context, params *PutBucketNotificationConfigurationInput, ...) (*PutBucketNotificationConfigurationOutput, error)
    * func (c *Client) PutBucketOwnershipControls(ctx context.Context, params *PutBucketOwnershipControlsInput, ...) (*PutBucketOwnershipControlsOutput, error)
    * func (c *Client) PutBucketPolicy(ctx context.Context, params *PutBucketPolicyInput, optFns ...func(*Options)) (*PutBucketPolicyOutput, error)
    * func (c *Client) PutBucketReplication(ctx context.Context, params *PutBucketReplicationInput, ...) (*PutBucketReplicationOutput, error)
    * func (c *Client) PutBucketRequestPayment(ctx context.Context, params *PutBucketRequestPaymentInput, ...) (*PutBucketRequestPaymentOutput, error)
    * func (c *Client) PutBucketTagging(ctx context.Context, params *PutBucketTaggingInput, optFns ...func(*Options)) (*PutBucketTaggingOutput, error)
    * func (c *Client) PutBucketVersioning(ctx context.Context, params *PutBucketVersioningInput, ...) (*PutBucketVersioningOutput, error)
    * func (c *Client) PutBucketWebsite(ctx context.Context, params *PutBucketWebsiteInput, optFns ...func(*Options)) (*PutBucketWebsiteOutput, error)
    * func (c *Client) PutObject(ctx context.Context, params *PutObjectInput, optFns ...func(*Options)) (*PutObjectOutput, error)
    * func (c *Client) PutObjectAcl(ctx context.Context, params *PutObjectAclInput, optFns ...func(*Options)) (*PutObjectAclOutput, error)
    * func (c *Client) PutObjectLegalHold(ctx context.Context, params *PutObjectLegalHoldInput, optFns ...func(*Options)) (*PutObjectLegalHoldOutput, error)
    * func (c *Client) PutObjectLockConfiguration(ctx context.Context, params *PutObjectLockConfigurationInput, ...) (*PutObjectLockConfigurationOutput, error)
    * func (c *Client) PutObjectRetention(ctx context.Context, params *PutObjectRetentionInput, optFns ...func(*Options)) (*PutObjectRetentionOutput, error)
    * func (c *Client) PutObjectTagging(ctx context.Context, params *PutObjectTaggingInput, optFns ...func(*Options)) (*PutObjectTaggingOutput, error)
    * func (c *Client) PutPublicAccessBlock(ctx context.Context, params *PutPublicAccessBlockInput, ...) (*PutPublicAccessBlockOutput, error)
    * func (c *Client) RestoreObject(ctx context.Context, params *RestoreObjectInput, optFns ...func(*Options)) (*RestoreObjectOutput, error)
    * func (c *Client) SelectObjectContent(ctx context.Context, params *SelectObjectContentInput, ...) (*SelectObjectContentOutput, error)
    * func (c *Client) UploadPart(ctx context.Context, params *UploadPartInput, optFns ...func(*Options)) (*UploadPartOutput, error)
    * func (c *Client) UploadPartCopy(ctx context.Context, params *UploadPartCopyInput, optFns ...func(*Options)) (*UploadPartCopyOutput, error)
    * func (c *Client) WriteGetObjectResponse(ctx context.Context, params *WriteGetObjectResponseInput, ...) (*WriteGetObjectResponseOutput, error)
  * type CompleteMultipartUploadInput
  * type CompleteMultipartUploadOutput
  * type ComputedInputChecksumsMetadata
  *     * func GetComputedInputChecksumsMetadata(m middleware.Metadata) (ComputedInputChecksumsMetadata, bool)
  * type CopyObjectInput
  * type CopyObjectOutput
  * type CreateBucketInput
  * type CreateBucketOutput
  * type CreateMultipartUploadInput
  * type CreateMultipartUploadOutput
  * type CreateSessionInput
  * type CreateSessionOutput
  * type DeleteBucketAnalyticsConfigurationInput
  * type DeleteBucketAnalyticsConfigurationOutput
  * type DeleteBucketCorsInput
  * type DeleteBucketCorsOutput
  * type DeleteBucketEncryptionInput
  * type DeleteBucketEncryptionOutput
  * type DeleteBucketInput
  * type DeleteBucketIntelligentTieringConfigurationInput
  * type DeleteBucketIntelligentTieringConfigurationOutput
  * type DeleteBucketInventoryConfigurationInput
  * type DeleteBucketInventoryConfigurationOutput
  * type DeleteBucketLifecycleInput
  * type DeleteBucketLifecycleOutput
  * type DeleteBucketMetricsConfigurationInput
  * type DeleteBucketMetricsConfigurationOutput
  * type DeleteBucketOutput
  * type DeleteBucketOwnershipControlsInput
  * type DeleteBucketOwnershipControlsOutput
  * type DeleteBucketPolicyInput
  * type DeleteBucketPolicyOutput
  * type DeleteBucketReplicationInput
  * type DeleteBucketReplicationOutput
  * type DeleteBucketTaggingInput
  * type DeleteBucketTaggingOutput
  * type DeleteBucketWebsiteInput
  * type DeleteBucketWebsiteOutput
  * type DeleteObjectInput
  * type DeleteObjectOutput
  * type DeleteObjectTaggingInput
  * type DeleteObjectTaggingOutput
  * type DeleteObjectsInput
  * type DeleteObjectsOutput
  * type DeletePublicAccessBlockInput
  * type DeletePublicAccessBlockOutput
  * type EndpointParameters
  *     * func (p EndpointParameters) ValidateRequired() error
    * func (p EndpointParameters) WithDefaults() EndpointParameters
  * type EndpointResolver
  *     * func EndpointResolverFromURL(url string, optFns ...func(*aws.Endpoint)) EndpointResolver
  * type EndpointResolverFunc
  *     * func (fn EndpointResolverFunc) ResolveEndpoint(region string, options EndpointResolverOptions) (endpoint aws.Endpoint, err error)
  * type EndpointResolverOptions
  * type EndpointResolverV2
  *     * func NewDefaultEndpointResolverV2() EndpointResolverV2
  * type ExpressCredentialsProvider
  * type GetBucketAccelerateConfigurationInput
  * type GetBucketAccelerateConfigurationOutput
  * type GetBucketAclInput
  * type GetBucketAclOutput
  * type GetBucketAnalyticsConfigurationInput
  * type GetBucketAnalyticsConfigurationOutput
  * type GetBucketCorsInput
  * type GetBucketCorsOutput
  * type GetBucketEncryptionInput
  * type GetBucketEncryptionOutput
  * type GetBucketIntelligentTieringConfigurationInput
  * type GetBucketIntelligentTieringConfigurationOutput
  * type GetBucketInventoryConfigurationInput
  * type GetBucketInventoryConfigurationOutput
  * type GetBucketLifecycleConfigurationInput
  * type GetBucketLifecycleConfigurationOutput
  * type GetBucketLocationInput
  * type GetBucketLocationOutput
  * type GetBucketLoggingInput
  * type GetBucketLoggingOutput
  * type GetBucketMetricsConfigurationInput
  * type GetBucketMetricsConfigurationOutput
  * type GetBucketNotificationConfigurationInput
  * type GetBucketNotificationConfigurationOutput
  * type GetBucketOwnershipControlsInput
  * type GetBucketOwnershipControlsOutput
  * type GetBucketPolicyInput
  * type GetBucketPolicyOutput
  * type GetBucketPolicyStatusInput
  * type GetBucketPolicyStatusOutput
  * type GetBucketReplicationInput
  * type GetBucketReplicationOutput
  * type GetBucketRequestPaymentInput
  * type GetBucketRequestPaymentOutput
  * type GetBucketTaggingInput
  * type GetBucketTaggingOutput
  * type GetBucketVersioningInput
  * type GetBucketVersioningOutput
  * type GetBucketWebsiteInput
  * type GetBucketWebsiteOutput
  * type GetObjectAclInput
  * type GetObjectAclOutput
  * type GetObjectAttributesInput
  * type GetObjectAttributesOutput
  * type GetObjectInput
  * type GetObjectLegalHoldInput
  * type GetObjectLegalHoldOutput
  * type GetObjectLockConfigurationInput
  * type GetObjectLockConfigurationOutput
  * type GetObjectOutput
  * type GetObjectRetentionInput
  * type GetObjectRetentionOutput
  * type GetObjectTaggingInput
  * type GetObjectTaggingOutput
  * type GetObjectTorrentInput
  * type GetObjectTorrentOutput
  * type GetPublicAccessBlockInput
  * type GetPublicAccessBlockOutput
  * type HTTPClient
  * type HTTPPresignerV4
  * type HTTPSignerV4
  * type HeadBucketAPIClient
  * type HeadBucketInput
  * type HeadBucketOutput
  * type HeadObjectAPIClient
  * type HeadObjectInput
  * type HeadObjectOutput
  * type ListBucketAnalyticsConfigurationsInput
  * type ListBucketAnalyticsConfigurationsOutput
  * type ListBucketIntelligentTieringConfigurationsInput
  * type ListBucketIntelligentTieringConfigurationsOutput
  * type ListBucketInventoryConfigurationsInput
  * type ListBucketInventoryConfigurationsOutput
  * type ListBucketMetricsConfigurationsInput
  * type ListBucketMetricsConfigurationsOutput
  * type ListBucketsInput
  * type ListBucketsOutput
  * type ListDirectoryBucketsAPIClient
  * type ListDirectoryBucketsInput
  * type ListDirectoryBucketsOutput
  * type ListDirectoryBucketsPaginator
  *     * func NewListDirectoryBucketsPaginator(client ListDirectoryBucketsAPIClient, params *ListDirectoryBucketsInput, ...) *ListDirectoryBucketsPaginator
  *     * func (p *ListDirectoryBucketsPaginator) HasMorePages() bool
    * func (p *ListDirectoryBucketsPaginator) NextPage(ctx context.Context, optFns ...func(*Options)) (*ListDirectoryBucketsOutput, error)
  * type ListDirectoryBucketsPaginatorOptions
  * type ListMultipartUploadsAPIClient
  * type ListMultipartUploadsInput
  * type ListMultipartUploadsOutput
  * type ListMultipartUploadsPaginator
  *     * func NewListMultipartUploadsPaginator(client ListMultipartUploadsAPIClient, params *ListMultipartUploadsInput, ...) *ListMultipartUploadsPaginator
  *     * func (p *ListMultipartUploadsPaginator) HasMorePages() bool
    * func (p *ListMultipartUploadsPaginator) NextPage(ctx context.Context, optFns ...func(*Options)) (*ListMultipartUploadsOutput, error)
  * type ListMultipartUploadsPaginatorOptions
  * type ListObjectVersionsAPIClient
  * type ListObjectVersionsInput
  * type ListObjectVersionsOutput
  * type ListObjectVersionsPaginator
  *     * func NewListObjectVersionsPaginator(client ListObjectVersionsAPIClient, params *ListObjectVersionsInput, ...) *ListObjectVersionsPaginator
  *     * func (p *ListObjectVersionsPaginator) HasMorePages() bool
    * func (p *ListObjectVersionsPaginator) NextPage(ctx context.Context, optFns ...func(*Options)) (*ListObjectVersionsOutput, error)
  * type ListObjectVersionsPaginatorOptions
  * type ListObjectsInput
  * type ListObjectsOutput
  * type ListObjectsV2APIClient
  * type ListObjectsV2Input
  * type ListObjectsV2Output
  * type ListObjectsV2Paginator
  *     * func NewListObjectsV2Paginator(client ListObjectsV2APIClient, params *ListObjectsV2Input, ...) *ListObjectsV2Paginator
  *     * func (p *ListObjectsV2Paginator) HasMorePages() bool
    * func (p *ListObjectsV2Paginator) NextPage(ctx context.Context, optFns ...func(*Options)) (*ListObjectsV2Output, error)
  * type ListObjectsV2PaginatorOptions
  * type ListPartsAPIClient
  * type ListPartsInput
  * type ListPartsOutput
  * type ListPartsPaginator
  *     * func NewListPartsPaginator(client ListPartsAPIClient, params *ListPartsInput, ...) *ListPartsPaginator
  *     * func (p *ListPartsPaginator) HasMorePages() bool
    * func (p *ListPartsPaginator) NextPage(ctx context.Context, optFns ...func(*Options)) (*ListPartsOutput, error)
  * type ListPartsPaginatorOptions
  * type ObjectExistsWaiter
  *     * func NewObjectExistsWaiter(client HeadObjectAPIClient, optFns ...func(*ObjectExistsWaiterOptions)) *ObjectExistsWaiter
  *     * func (w *ObjectExistsWaiter) Wait(ctx context.Context, params *HeadObjectInput, maxWaitDur time.Duration, ...) error
    * func (w *ObjectExistsWaiter) WaitForOutput(ctx context.Context, params *HeadObjectInput, maxWaitDur time.Duration, ...) (*HeadObjectOutput, error)
  * type ObjectExistsWaiterOptions
  * type ObjectNotExistsWaiter
  *     * func NewObjectNotExistsWaiter(client HeadObjectAPIClient, optFns ...func(*ObjectNotExistsWaiterOptions)) *ObjectNotExistsWaiter
  *     * func (w *ObjectNotExistsWaiter) Wait(ctx context.Context, params *HeadObjectInput, maxWaitDur time.Duration, ...) error
    * func (w *ObjectNotExistsWaiter) WaitForOutput(ctx context.Context, params *HeadObjectInput, maxWaitDur time.Duration, ...) (*HeadObjectOutput, error)
  * type ObjectNotExistsWaiterOptions
  * type Options
  *     * func (o Options) Copy() Options
    * func (o Options) GetIdentityResolver(schemeID string) smithyauth.IdentityResolver
  * type PresignClient
  *     * func NewPresignClient(c *Client, optFns ...func(*PresignOptions)) *PresignClient
  *     * func (c *PresignClient) PresignDeleteBucket(ctx context.Context, params *DeleteBucketInput, ...) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignDeleteObject(ctx context.Context, params *DeleteObjectInput, ...) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignGetObject(ctx context.Context, params *GetObjectInput, optFns ...func(*PresignOptions)) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignHeadBucket(ctx context.Context, params *HeadBucketInput, optFns ...func(*PresignOptions)) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignHeadObject(ctx context.Context, params *HeadObjectInput, optFns ...func(*PresignOptions)) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignPutObject(ctx context.Context, params *PutObjectInput, optFns ...func(*PresignOptions)) (*v4.PresignedHTTPRequest, error)
    * func (c *PresignClient) PresignUploadPart(ctx context.Context, params *UploadPartInput, optFns ...func(*PresignOptions)) (*v4.PresignedHTTPRequest, error)
  * type PresignOptions
  * type PutBucketAccelerateConfigurationInput
  * type PutBucketAccelerateConfigurationOutput
  * type PutBucketAclInput
  * type PutBucketAclOutput
  * type PutBucketAnalyticsConfigurationInput
  * type PutBucketAnalyticsConfigurationOutput
  * type PutBucketCorsInput
  * type PutBucketCorsOutput
  * type PutBucketEncryptionInput
  * type PutBucketEncryptionOutput
  * type PutBucketIntelligentTieringConfigurationInput
  * type PutBucketIntelligentTieringConfigurationOutput
  * type PutBucketInventoryConfigurationInput
  * type PutBucketInventoryConfigurationOutput
  * type PutBucketLifecycleConfigurationInput
  * type PutBucketLifecycleConfigurationOutput
  * type PutBucketLoggingInput
  * type PutBucketLoggingOutput
  * type PutBucketMetricsConfigurationInput
  * type PutBucketMetricsConfigurationOutput
  * type PutBucketNotificationConfigurationInput
  * type PutBucketNotificationConfigurationOutput
  * type PutBucketOwnershipControlsInput
  * type PutBucketOwnershipControlsOutput
  * type PutBucketPolicyInput
  * type PutBucketPolicyOutput
  * type PutBucketReplicationInput
  * type PutBucketReplicationOutput
  * type PutBucketRequestPaymentInput
  * type PutBucketRequestPaymentOutput
  * type PutBucketTaggingInput
  * type PutBucketTaggingOutput
  * type PutBucketVersioningInput
  * type PutBucketVersioningOutput
  * type PutBucketWebsiteInput
  * type PutBucketWebsiteOutput
  * type PutObjectAclInput
  * type PutObjectAclOutput
  * type PutObjectInput
  * type PutObjectLegalHoldInput
  * type PutObjectLegalHoldOutput
  * type PutObjectLockConfigurationInput
  * type PutObjectLockConfigurationOutput
  * type PutObjectOutput
  * type PutObjectRetentionInput
  * type PutObjectRetentionOutput
  * type PutObjectTaggingInput
  * type PutObjectTaggingOutput
  * type PutPublicAccessBlockInput
  * type PutPublicAccessBlockOutput
  * type ResolveEndpoint
  *     * func (m *ResolveEndpoint) HandleSerialize(ctx context.Context, in middleware.SerializeInput, ...) (out middleware.SerializeOutput, metadata middleware.Metadata, err error)
    * func (*ResolveEndpoint) ID() string
  * type ResponseError
  * type RestoreObjectInput
  * type RestoreObjectOutput
  * type SelectObjectContentEventStream
  *     * func NewSelectObjectContentEventStream(optFns ...func(*SelectObjectContentEventStream)) *SelectObjectContentEventStream
  *     * func (es *SelectObjectContentEventStream) Close() error
    * func (es *SelectObjectContentEventStream) Err() error
    * func (es *SelectObjectContentEventStream) Events() <-chan types.SelectObjectContentEventStream
  * type SelectObjectContentEventStreamReader
  * type SelectObjectContentInput
  * type SelectObjectContentOutput
  *     * func (o *SelectObjectContentOutput) GetStream() *SelectObjectContentEventStream
  * type UnknownEventMessageError
  *     * func (e *UnknownEventMessageError) Error() string
  * type UploadPartCopyInput
  * type UploadPartCopyOutput
  * type UploadPartInput
  * type UploadPartOutput
  * type WriteGetObjectResponseInput
  * type WriteGetObjectResponseOutput