export interface ApiConfig {
  token: string | null;
}

const request: typeof fetch = async (input, init) => {
  try {
    const response = await fetch(`/api${input}`, init);
    if (response.status === 401) {
      throw new Error("Unauthorized");
    }
    return response;
  } catch (err) {
    console.error("Fetch request failed.", err);
    throw new Error("Fetch request failed.", { cause: err });
  }
};

const getHeaders = (config: ApiConfig): { Authorization: string } | {} =>
  config.token
    ? {
        Authorization: `Bearer ${config.token}`,
        "Content-Type": "application/json",
      }
    : {};

export const uploadFile = async (file: File, config: ApiConfig) => {
  const response = await request(`/upload/${file.name}`, {
    method: "PUT",
    body: file,
    headers: {
      ...getHeaders(config),
      "Content-Type": "image/jpeg",
      "Content-Length": `${file.size}`,
    },
  });

  if (!response.ok) {
    console.error("API Upload failed.", response);
    throw new Error("API Upload failed.", { cause: response });
  }
};
