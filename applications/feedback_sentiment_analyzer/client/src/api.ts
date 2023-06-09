import { API_GATEWAY_BASE_URL } from "./env";

export interface ApiConfig {
  token: string | null;
}

const request: typeof fetch = async (input, init) => {
  try {
    const response = await fetch(input, init);
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
  const response = await request(
    `${API_GATEWAY_BASE_URL}upload`,
    {
      method: "PUT",
      body: JSON.stringify({
        file_name: file.name,
      }),
      headers: getHeaders(config),
    }
  );

  if (response.ok) {
    const { url } = await response.json();
    return request(url, {
      method: "PUT",
      headers: {
        "Content-Type": "image/jpeg",
        "Content-Length": `${file.size}`,
      },
      body: await file.arrayBuffer(),
    });
  } else {
    console.error("API Upload failed.", response);
    throw new Error("API Upload failed.", { cause: response });
  }
};
