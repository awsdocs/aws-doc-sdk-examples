import {
  StartQueryCommand,
  GetQueryResultsCommand,
} from "@aws-sdk/client-cloudwatch-logs";
import { splitDateRange } from "@aws-sdk-examples/libs/utils/util-date.js";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";

class DateOutOfBoundsError extends Error {}

export class CloudWatchQuery {
  /**
   * Run a query against CloudWatch Logs.
   * CloudWatch logs return a max of 10,000 results. CloudWatchQuery will
   * perform a binary search across all of the logs in the provided date range
   * if a query returns the maximum number of results.
   *
   * Note: The "@timestamp" field must be included in the results in order
   * for this to work.
   * @param {import('@aws-sdk/client-cloudwatch-logs').CloudWatchLogsClient} client
   * @param {{ logGroupNames: string[], dateRange: [Date, Date] }} config
   */
  constructor(client, { logGroupNames, dateRange }) {
    this.client = client;
    this.logGroupNames = logGroupNames;
    this.dateRange = dateRange;
    /**
     * @type {{ queries: { [key: string]: { resultCount: number, dateRange: [Date, Date] } } }}
     */
    this.resultsMeta = {
      logGroupNames: this.logGroupNames,
      initialDateRange: this.dateRange,
      queries: {},
      logs: 0,
    };
  }

  run() {
    this.resultsMeta.queries = {};
    this.resultsMeta.logs = 0;
    return this._bigQuery(this.dateRange);
  }

  /**
   * Recursively query for logs.
   * @param {[Date, Date]} dateRange
   * @returns {Promise<import("@aws-sdk/client-cloudwatch-logs").ResultField[][]>}
   */
  async _bigQuery(dateRange) {
    const maxLogs = 100;
    const logs = await this._query(dateRange, maxLogs);
    this.resultsMeta.logs += logs.length;

    if (logs.length < maxLogs) {
      return logs;
    }

    const lastLogDate = this._getLastLogDate(logs);
    const offsetLastLogDate = new Date(lastLogDate);
    offsetLastLogDate.setMilliseconds(lastLogDate.getMilliseconds() + 1);
    const subDateRange = [offsetLastLogDate, dateRange[1]];
    const [r1, r2] = splitDateRange(subDateRange);
    const results = await Promise.all([this._bigQuery(r1), this._bigQuery(r2)]);
    return [logs, ...results].flat();
  }

  /**
   *
   * @param {import("@aws-sdk/client-cloudwatch-logs").ResultField[][]} logs
   */
  _getLastLogDate(logs) {
    const timestamps = logs
      .map(
        (log) =>
          log.find((fieldMeta) => fieldMeta.field === "@timestamp")?.value,
      )
      .filter((t) => !!t)
      .map((t) => `${t}Z`)
      .sort();

    // Throw if no timestamp.
    if (!timestamps.length) {
      throw new Error("No timestamp found in logs.");
    }

    return new Date(timestamps[timestamps.length - 1]);
  }

  _getQueryResults(queryId) {
    return this.client.send(new GetQueryResultsCommand({ queryId }));
  }

  /**
   * @param {[Date, Date]} dateRange
   * @param {number} maxLogs
   */
  async _query(dateRange, maxLogs) {
    try {
      const { queryId } = await this._startQuery(dateRange, maxLogs);
      const { results } = await this._waitUntilQueryDone(queryId);
      this.resultsMeta.queries[queryId] = {
        resultCount: results.length,
        dateRange,
      };
      return results ?? [];
    } catch (err) {
      if (err instanceof DateOutOfBoundsError) {
        return [];
      } else {
        throw err;
      }
    }
  }

  /**
   * @param {[Date, Date]} dateRange
   * @param {number} maxLogs
   * @returns {Promise<{ queryId: string }>}
   */
  async _startQuery([startDate, endDate], maxLogs = 10000) {
    try {
      return await this.client.send(
        new StartQueryCommand({
          logGroupNames: this.logGroupNames,
          queryString: "fields @timestamp, @message | sort @timestamp asc",
          startTime: startDate.valueOf(),
          endTime: endDate.valueOf(),
          limit: maxLogs,
        }),
      );
    } catch (err) {
      /** @type {string} */
      const message = err.message;
      if (message.startsWith("Query's end date and time")) {
        throw new DateOutOfBoundsError(message);
      }

      throw err;
    }
  }

  _waitUntilQueryDone(queryId) {
    const getResults = async () => {
      const results = await this._getQueryResults(queryId);
      const queryDone = [
        "Complete",
        "Failed",
        "Cancelled",
        "Timeout",
        "Unknown",
      ].includes(results.status);

      return { queryDone, results };
    };

    return retry({ intervalInMs: 1000, maxRetries: 60 }, async () => {
      const { queryDone, results } = await getResults();
      if (!queryDone) {
        throw new Error("Query not done.");
      }

      return results;
    });
  }
}
