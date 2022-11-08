import { RequestHandler } from "express";

declare type Handler = {
  withClient: ({
    rdsDataClient,
    sesClient,
  }: {
    rdsDataClient?: Sendable;
    sesClient?: Sendable;
  }) => (...params: Parameters<RequestHandler>) => Promise<void>;
};
