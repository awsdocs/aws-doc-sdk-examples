require('dotenv').config();

const config = {
  deliveryStreamName: process.env.DELIVERY_STREAM_NAME,
  logGroupName: process.env.LOG_GROUP_NAME,
};

module.exports = config;