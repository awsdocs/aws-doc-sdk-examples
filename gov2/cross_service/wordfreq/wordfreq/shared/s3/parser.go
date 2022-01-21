// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package s3

import (
	"errors"
	"net"

	"github.com/tidwall/gjson"
)

func isTestMessage(json string) (bool, error) {

	if json == "" {
		return true, errors.New("empty string is not valid")
	}

	if !gjson.Valid(json) {
		return false, errors.New("invalid json")
	}

	event := gjson.Get(json, "Event")
	if event.Exists() && event.String() == "s3:TestEvent" {
		return true, nil
	}
	return false, nil
}

func ParseEvent(json string) ([]Record, error) {
	istest, err := isTestMessage(json)
	if err != nil {
		// probably invalid json...
		return nil, err
	} else if istest {
		return make([]Record, 0), errors.New("test message")
	}

	// we can pre-allocate the number of records we need just by asking GJSON
	num_records := gjson.Get(json, "Records.#").Int()
	records := make([]Record, num_records)

	for rec_idx, jsonrecord := range gjson.Get(json, "Records").Array() {

		version := jsonrecord.Get("eventVersion")

		if !version.Exists() {
			return nil, errors.New("no version information")
		}

		if version.Exists() && version.String() != "2.1" {
			return nil, errors.New("don't know that version of the schema")
		}

		// This parser is kinda bad: It does a bad job making sure the subfields are
		// accounted for, but for the vast majority of cases this will be fine.
		// This assumes that S3 doesn't change their event structure any time soon
		// and version 2.1 should be around for a while.

		records[rec_idx] = Record{
			EventVersion:  jsonrecord.Get("eventVersion").String(),
			EventSource:   jsonrecord.Get("eventSource").String(),
			EventName:     jsonrecord.Get("eventName").String(),
			AwsRegion:     jsonrecord.Get("awsRegion").String(),
			EventTime:     jsonrecord.Get("eventTime").Time(),
			Identity:      jsonrecord.Get("userIdentity.principalId").String(),
			SourceAddress: net.ParseIP(jsonrecord.Get("requestParameters.sourceIPAddress").String()),
			EventData: S3RecordData{
				SchemaVersion:   jsonrecord.Get("s3.s3SchemaVersion").String(),
				ConfigurationId: jsonrecord.Get("s3.configurationId").String(),
				Bucket: S3BucketData{
					Name:  jsonrecord.Get("s3.bucket.name").String(),
					Owner: jsonrecord.Get("s3.bucket.ownerIdentity.principalId").String(),
					ARN:   jsonrecord.Get("s3.bucket.arn").String(),
				},
				Object: S3ObjectData{
					Key:       jsonrecord.Get("s3.object.key").String(),
					Size:      uint64(jsonrecord.Get("s3.object.size").Int()),
					ETag:      jsonrecord.Get("s3.object.eTag").String(),
					VersionID: jsonrecord.Get("s3.object.versionId").String(),
					Sequencer: jsonrecord.Get("s3.object.sequencer").String(),
				},
			},
		}

		// check if it's an S3 event or a Glacier event.
		// as of version 2.1, this is the only reason that the glacier data will be around.
		if jsonrecord.Get("glacierEventData.restoreEventData").Exists() &&
			records[rec_idx].EventName == "s3:ObjectRestore:Completed" {
			// parse the Glacier data
			records[rec_idx].GlacierData = GlacierEventData{
				RestorationExpiryTime:   jsonrecord.Get("glacierEventData.restoreEventData.lifecycleRestorationExpiryTime").Time(),
				RestorationStorageClass: jsonrecord.Get("glacierEventData.restoreEventData.lifecycleRestoreStorageClass").String(),
			}

		}

	}

	return records, nil
}
