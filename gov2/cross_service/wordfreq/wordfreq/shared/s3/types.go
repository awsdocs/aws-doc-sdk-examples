// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package s3

import (
	"net"
	"time"
)

type S3ObjectData struct {
	Key, ETag, VersionID, Sequencer string
	Size                            uint64
}

type S3BucketData struct {
	Name, ARN, Owner string
}

type S3RecordData struct {
	SchemaVersion   string
	ConfigurationId string
	Bucket          S3BucketData
	Object          S3ObjectData
}

type GlacierEventData struct {
	RestorationExpiryTime   time.Time
	RestorationStorageClass string
}

type Record struct {
	EventVersion  string
	EventSource   string
	AwsRegion     string
	EventTime     time.Time
	EventName     string
	Identity      string
	SourceAddress net.IP

	EventData   S3RecordData
	GlacierData GlacierEventData
}
