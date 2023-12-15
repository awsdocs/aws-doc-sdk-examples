/**
 * @typedef {Object} Interval
 * @property {number} years
 * @property {number} months
 * @property {number} days
 * @property {number} hours
 * @property {number} minutes
 * @property {number} seconds
 */

/**
 * @typedef {Object} DateRangeGeneratorConfig
 * @property {Interval} interval
 * @property {Date} start
 * @property {Date} end
 */

/**
 * @type {Interval}
 */
const defaultInterval = {
  years: 0,
  months: 0,
  days: 0,
  hours: 0,
  minutes: 0,
  seconds: 0,
};

/**
 *  @param {DateRangeGeneratorConfig} config
 */
export function* dateRangeGenerator({ interval, start, end }) {
  const { years, months, days, hours, minutes, seconds } = {
    ...defaultInterval,
    ...interval,
  };

  console.log(years, months, days, hours, minutes, seconds);

  let chunkEnd = new Date(end);

  while (true) {
    const chunkStart = new Date(
      chunkEnd.getFullYear() - years,
      chunkEnd.getMonth() - months,
      chunkEnd.getDate() - days,
      chunkEnd.getHours() - hours,
      chunkEnd.getMinutes() - minutes,
      chunkEnd.getSeconds() - seconds,
    );

    if (chunkStart <= start) {
      yield { startDate: start, endDate: chunkEnd };
      break;
    }

    yield { startDate: chunkStart, endDate: chunkEnd };
    chunkEnd = new Date(chunkStart);
  }
}
