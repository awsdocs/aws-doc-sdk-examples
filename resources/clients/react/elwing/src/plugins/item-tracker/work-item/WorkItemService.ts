// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { RestService } from "../RestService";
import config from "../config.json";

/**
 * Sends REST requests to get work items, add new work items, modify work items,
 * and send an email report.
 *
 * The base URL of the REST service is stored in config.json. If necessary, update this
 * value to your endpoint.
 */

export type WorkItemStatus = "ARCH" | "ACT" | "";

export interface WorkItem {
  id: string;
  name: string;
  guide: string;
  description: string;
  status: string;
  archive: WorkItemStatus;
}

export class WorkItemService extends RestService<WorkItem> {
  constructor(baseUrl: string = config.BASE_URL) {
    super("items", baseUrl);
  }

  /**
   * Sends a PUT request to archive an active item.
   */
  async archiveItem(itemId: string) {
    return this.fetch(this.url({ id: itemId, adverb: "archive" }));
  }

  /**
   * Sends a POST request to email a report of work items.
   */
  async mailItem(email: string) {
    return await this.fetch(this.url({ adverb: "report" }), {
      method: "POST",
      body: JSON.stringify({ email }),
    });
  }
}

export const workItemService = new WorkItemService();
