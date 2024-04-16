const logger = require('./logger');

const handleError = (err) => {
  logger.error(err);
  // Additional error handling logic, e.g., retries, fallbacks
};

module.exports = {
  handleError,
};