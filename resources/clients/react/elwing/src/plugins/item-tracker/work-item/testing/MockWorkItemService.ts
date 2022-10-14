import { WorkItem, WorkItemService } from "../WorkItemService";

export class MockWorkItemService extends WorkItemService {
  id = 0;
  nextId() {
    this.id += 1;
    return String(this.id);
  }
  items: Record<string, WorkItem> = {};

  override archiveItem: (itemId: string) => Promise<unknown> = jest
    .fn()
    .mockResolvedValue({});

  override mailItem: (email: string) => Promise<Response> = jest
    .fn()
    .mockResolvedValue({});

  override create: (item: Omit<WorkItem, "id">) => Promise<WorkItem> = jest.fn(
    (item: Omit<WorkItem, "id">) => {
      let workItem: WorkItem = {
        ...item,
        id: this.nextId(),
      };
      this.items[workItem.id] = workItem;
      return Promise.resolve(workItem);
    }
  );

  override list: (
    params?: Partial<Record<keyof WorkItem, string>>
  ) => Promise<WorkItem[]> = jest.fn(
    (params?: Partial<Record<keyof WorkItem, string>>) =>
      Promise.resolve([...Object.values(this.items)])
  );

  override retrieve: (
    id: string,
    params?: Partial<Record<keyof WorkItem, string>>
  ) => Promise<WorkItem> = jest.fn(
    (id: string, params?: Partial<Record<keyof WorkItem, string>>) =>
      Promise.resolve(this.items[id])
  );

  override update: (id: string, body: Partial<WorkItem>) => Promise<WorkItem> =
    jest.fn((id: string, body: Partial<WorkItem>) => {
      this.items[id] = body as WorkItem;
      return Promise.resolve(this.items[id]);
    });

  override delete: (id: string) => Promise<void> = jest.fn((id: string) => {
    delete this.items[id];
    return Promise.resolve();
  });
}
