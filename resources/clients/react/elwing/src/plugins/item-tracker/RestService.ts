export class RestService<T extends { id: string }> {
  constructor(
    readonly collection: string,
    readonly baseUrl = "https://localhost:3000/api"
  ) {}

  url(params: Partial<Record<keyof T, string>> = {}) {
    const url = new URL(`${this.baseUrl}/${this.collection}/`);
    for (const [k, v] of Object.entries(params)) {
      url.searchParams.append(k, v);
    }
    return url;
  }

  idUrl(id: string) {
    return new URL(`${this.baseUrl}/${this.collection}/${id}`);
  }

  /**
   * Sends a POST request to add a new work item.
   */
  async create(item: Omit<T, "id">) {
    return await (
      await fetch(this.url(), {
        method: "POST",
        body: JSON.stringify(item),
      })
    ).json();
  }

  /**
   * Sends a GET request to retrieve work items that are in the specified state.
   *
   * @param status: The state of work items to retrieve. Can be either 'active' or 'archive'.
   */
  async list(params: Partial<Record<keyof T, string>> = {}): Promise<T[]> {
    return await (await fetch(this.url(params))).json();
  }

  async retrieve(id: string): Promise<T> {
    return await (await fetch(this.idUrl(id))).json();
  }

  async update(id: string, body: Partial<T>): Promise<T> {
    return await (
      await fetch(this.idUrl(id), {
        method: "PUT",
        body: JSON.stringify(body),
      })
    ).json();
  }

  async delete(id: string) {
    return await fetch(this.idUrl(id), { method: "DELETE" });
  }
}
