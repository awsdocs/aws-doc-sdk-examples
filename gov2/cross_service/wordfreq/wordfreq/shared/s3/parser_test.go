// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX - License - Identifier: Apache - 2.0
package s3

import (
	_ "embed"
	"testing"
)

// test message

//go:embed test_data/test_message.json
var test_str_testmessage string

func TestIsTestMessage(t *testing.T) {
	is_test, err := isTestMessage(test_str_testmessage)

	if err != nil {
		t.Fatal("Valid JSON threw an error")
	}

	if !is_test {
		t.Fatal("Test messages should be recognizable")
	}
}

func TestIsTestEmptyString(t *testing.T) {
	_, err := isTestMessage("")
	if err == nil {
		t.Log("empty string accepted as valid input!")
	}
}

//go:embed test_data/example1.json
var test_str_exampleRecords string

func TestParseFullMessage(t *testing.T) {

	records, err := ParseEvent(test_str_exampleRecords)

	if err != nil {
		t.Fatal("Failed to handle simple test case; failure!")
	}
	if len(records) != 1 {
		t.Fatal("Found more than one record in one-record example")
	}
	// Now, check the values in it.
	record := records[0]
	if record.AwsRegion == "" {
		t.Fatal("region was not parsed")
	}
	if record.EventName == "" {
		t.Fatal("event name was not parsed")
	}
	if record.EventTime.Unix() < 0 {
		t.Fatal("time was not parsed")
	}
	if record.EventSource == "" {
		t.Fatal("event source was not parsed")
	}
	if record.EventVersion == "" {
		t.Fatal("event version was not parsed")
	}

	// The identity is hidden in a submember, so we should make sure that path gets handled too..
	if record.Identity == "" {
		t.Fatal("Event identiy principal was not parsed")
	}

	// make sure the source address is appropriately parsed out.
	if record.SourceAddress == nil {
		t.Fatal("source address was not parsed")
	}

	if record.EventData.Bucket.Name == "" {
		t.Fatal("bucket name was not parsed")
	}

}
