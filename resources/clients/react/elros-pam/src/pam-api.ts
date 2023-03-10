export interface PamApiConfig {
  token: string | null;
}
export interface Tag {
  name: string;
  count: number;
}

export interface TagsResponse {
  labels: { [key: string]: { count: number } };
}

const getHeaders = (config: PamApiConfig): { Authorization: string } | {} =>
  config.token ? { Authorization: config.token } : {};

export const getTags = async (config: PamApiConfig): Promise<Tag[]> => {
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}labels`,
    {
      headers: getHeaders(config),
    }
  );

  const tagsResponse: TagsResponse = await response.json();
  return Object.entries(tagsResponse.labels).map(([name, { count }]) => ({
    name,
    count,
  }));
};

export const s3Copy = async (bucketName: string, config: PamApiConfig) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}s3_copy`,
    {
      method: "PUT",
      body: JSON.stringify({
        source: bucketName,
      }),
      headers: getHeaders(config),
    }
  );

  if (response.ok) {
    return response.json();
  } else {
    console.error(response);
    throw new Error("Copy failed.");
  }
};

export const initializeDownload = async (labels: string[]) => {
  await fetch(`${import.meta.env.VITE_API_GATEWAY_BASE_URL}restore`, {
    method: "PUT",
    body: JSON.stringify({
      labels,
    }),
  });
};

export const uploadFile = async (file: File, config: PamApiConfig) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}upload`,
    {
      method: "PUT",
      body: JSON.stringify({
        file_name: file.name,
      }),
    }
  );

  if (response.ok) {
    const { url } = await response.json();
    return fetch(url, {
      method: "PUT",
      headers: {
        "Content-Type": "image/jpeg",
        "Content-Length": `${file.size}`,
      },
      body: await file.arrayBuffer(),
    });
  } else {
    console.error(response);
    throw new Error("Upload failed.");
  }
};
