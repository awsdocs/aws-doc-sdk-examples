import {
  StartQueryCommand,
  GetQueryResultsCommand,
  DescribeQueriesCommand,
} from "@aws-sdk/client-cloudwatch-logs";
import { retry } from "@aws-sdk-examples/libs/utils/util-timers.js";
import { dateRangeGenerator } from "@aws-sdk-examples/libs/utils/util-date.js";

export class CloudWatchQuery {
  /**
   * @param {import('@aws-sdk/client-cloudwatch-logs').CloudWatchLogsClient} client
   * @param {{ logGroupNames: string[], queryString: string, startDate: Date, endDate: Date }} config
   */
  constructor(client, { logGroupNames, queryString, startDate, endDate }) {
    this.client = client;
    this.logGroupNames = logGroupNames;
    this.queryString = queryString;
    this.startDate = startDate;
    this.endDate = endDate;
    this.stopNewQueries = false;
    /**
     * @type {{ queryId: string, response: import("@aws-sdk/client-cloudwatch-logs").GetQueryResultsResponse, startDate: Date, endDate: Date, error: Error | undefined }[]}
     */
    this.subQueries = [];
  }

  /** @param {(subQueries: CloudWatchQuery['subQueries']) => void} cb */
  run(cb) {
    this.stopNewQueries = false;
    this.subQueries = [];

    this._pollQueryResults(cb);
    this._partitionQueries();
    // TODO: do something with errors in sub queries
  }

  /**
   * Get the count of ALL running queries, including those not started
   * with this instance of CloudWatchQuery.
   */
  async _getActiveQueryCount() {
    /**
     * @param {string} nextToken
     * @param {import("@aws-sdk/client-cloudwatch-logs").QueryInfo[]} queries
     */
    const describeQueries = async (nextToken, queries = []) => {
      const response = await retry({ intervalInMs: 500, maxRetries: 60 }, () =>
        this.client.send(new DescribeQueriesCommand({ nextToken })),
      );
      queries.push(...response.queries);

      if (response.nextToken) {
        await describeQueries(response.nextToken, queries);
      }

      return queries;
    };

    const queries = await describeQueries();
    const activeQueries = queries.filter(
      (query) => query.status === "Running" || query.status === "Scheduled",
    );
    return activeQueries.length;
  }

  _getQueryResults(queryId) {
    return this.client.send(new GetQueryResultsCommand({ queryId }));
  }

  _partitionQueries() {
    const maxQueries = 30; // CloudWatch Logs has a max of 30 active queries.

    const dateRanges = dateRangeGenerator({
      interval: { days: 1 },
      start: this.startDate,
      end: this.endDate,
    });
    const intervalInMs = 200;

    // TODO: refactor to use timersPromises api
    return new Promise((resolve, reject) => {
      const tick = setInterval(async () => {
        if (this.stopNewQueries) {
          clearInterval(tick);
          resolve();
          return;
        }

        const activeQueries = await this._getActiveQueryCount();
        if (activeQueries < maxQueries) {
          try {
            const { startDate, endDate } = dateRanges.next().value;
            const { queryId } = await this._startQuery(startDate, endDate);
            if (queryId) {
              this.subQueries.push({ queryId, startDate, endDate });
            }
          } catch (err) {
            reject(err);
          }
        }
      }, intervalInMs);
    });
  }

  /** @param {(subQueries: CloudWatchQuery['subQueries']) => void} cb */
  _pollQueryResults(cb) {
    /**
     * @param {import("@aws-sdk/client-cloudwatch-logs").GetQueryResultsResponse} queryResponse
     */
    const queryDone = (queryResponse) =>
      ["Complete", "Failed", "Cancelled", "Timeout", "Unknown"].includes(
        queryResponse?.status,
      );

    /**
     * @param {import("@aws-sdk/client-cloudwatch-logs").GetQueryResultsResponse} queryResponse
     */
    const queryErred = (queryResponse) =>
      ["Failed", "Cancelled", "Timeout", "Unknown"].includes(
        queryResponse?.status,
      );

    const intervalInMs = 500;
    const tick = setInterval(async () => {
      const allQueriesDone = this.subQueries.every((subQuery) =>
        queryDone(subQuery.response),
      );
      if (this.stopNewQueries && allQueriesDone) {
        clearInterval(tick);
        cb(this.subQueries);
        return;
      }

      for (const subQuery of this.subQueries.values()) {
        if (queryDone(subQuery.response)) {
          continue;
        }

        // Handle throttling exceptions.
        const response = await retry(
          { intervalInMs: 500, maxRetries: 60 },
          () => this._getQueryResults(subQuery.queryId),
        );

        subQuery.response = response;

        if (queryErred(response)) {
          subQuery.error = new Error(`Sub query error: ${response.status}`);
        }
      }
    }, intervalInMs);
  }

  /**
   * @param {string} queryString
   * @param {Date} startDate
   * @param {Date} endDate
   */
  async _startQuery(startDate, endDate) {
    try {
      console.debug("---");
      console.debug(`log groups: ${this.logGroupNames}`);
      console.debug(`query: ${this.queryString}`);
      console.debug(`start: ${startDate.toISOString()}`);
      console.debug(`end: ${endDate.toISOString()}`);
      console.debug("---");
      return await this.client.send(
        new StartQueryCommand({
          logGroupNames: this.logGroupNames,
          queryString: this.queryString,
          startTime: startDate.valueOf(),
          endTime: endDate.valueOf(),
        }),
      );
    } catch (err) {
      /** @type {string} */
      const message = err.message;
      if (message.startsWith("Query's end date and time")) {
        this.stopNewQueries = true;
        return {};
      }

      throw err;
    }
  }
}
