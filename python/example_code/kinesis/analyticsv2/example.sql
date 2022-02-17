-- Application code uploaded to a Kinesis Data Analytics application to demonstrate
-- how to process data from an input stream to a destination stream.
-- Creates a temporary stream.
CREATE OR REPLACE STREAM "TEMP_STREAM" (
	        "heartRate"        INTEGER,
	        "rateType"         varchar(20),
	        "ANOMALY_SCORE"    DOUBLE);

-- Creates another stream for application output.
CREATE OR REPLACE STREAM "DESTINATION_SQL_STREAM" (
	        "heartRate"        INTEGER,
	        "rateType"         varchar(20),
	        "ANOMALY_SCORE"    DOUBLE);

-- Computes an anomaly score for each record in the input stream
-- using Random Cut Forest
CREATE OR REPLACE PUMP "STREAM_PUMP" AS
   INSERT INTO "TEMP_STREAM"
      SELECT STREAM "heartRate", "rateType", ANOMALY_SCORE
      FROM TABLE(RANDOM_CUT_FOREST(
              CURSOR(SELECT STREAM * FROM "SOURCE_SQL_STREAM_001")));

-- Sorts records by descending anomaly score and inserts them into the output stream
CREATE OR REPLACE PUMP "OUTPUT_PUMP" AS
   INSERT INTO "DESTINATION_SQL_STREAM"
      SELECT STREAM * FROM "TEMP_STREAM"
      ORDER BY FLOOR("TEMP_STREAM".ROWTIME TO SECOND), ANOMALY_SCORE DESC;
