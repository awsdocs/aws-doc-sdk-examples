const logger = require('./logger');
const errorHandler = require('./errorHandler');

const putRecord = async (firehoseClient, deliveryStreamName, record) => {
  try {
    const params = {
      DeliveryStreamName: deliveryStreamName,
      Record: record,
    };

    const response = await firehoseClient.putRecord(params).promise();
    logger.info(`Successfully ingested record: ${record.toString()}`);
    return response;
  } catch (err) {
    errorHandler.handleError(err);
  }
};

const putRecordBatch = async (firehoseClient, deliveryStreamName, records) => {
  try {
    const params = {
      DeliveryStreamName: deliveryStreamName,
      Records: records,
    };

    const response = await firehoseClient.putRecordBatch(params).promise();
    logger.info(`Successfully ingested ${records.length} records`);
    return response;
  } catch (err) {
    errorHandler.handleError(err);
  }
};

module.exports = {
  putRecord,
  putRecordBatch,
};