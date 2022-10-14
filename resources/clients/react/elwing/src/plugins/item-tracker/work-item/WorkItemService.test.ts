import { workItemService } from "./WorkItemService";

global.fetch = jest.fn();

const fetch = jest.fn().mockResolvedValue({});
// @ts-ignore
workItemService.fetch = fetch;

describe("WorkItemService", () => {
  it("builds a URL for the archive route", async () => {
    await workItemService.archiveItem("0");
    expect(fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/items/0:archive"
    );
  });

  it("builds a URL for the reports route", async () => {
    await workItemService.mailItem("test@example.com");
    expect(fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/items:report",
      { method: "POST", body: JSON.stringify({ email: "test@example.com" }) }
    );
  });

  it("includes archived state", async () => {
    await workItemService.list({ archive: "active" });
    expect(fetch).toHaveBeenCalledWith(
      "http://localhost:8080/api/items?archive=active"
    );
  });
});
