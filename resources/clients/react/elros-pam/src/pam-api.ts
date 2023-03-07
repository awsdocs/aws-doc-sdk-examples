export interface PamApiConfig {
  token: string | null;
}

export interface TagsResponse {
  [key: string]: { count: number };
}

const getHeaders = (config: PamApiConfig): { Authorization: string } | {} =>
  config.token ? { Authorization: config.token } : {};

export const getTags = async (config: PamApiConfig): Promise<TagsResponse> => {
  // const response = await fetch(
  //   `${import.meta.env.VITE_API_GATEWAY_BASE_URL}/labels`,
  //   {
  //     headers: getHeaders(config),
  //   }
  // );
  // return response.json();
  return {
    landscape: { count: 5 },
    nature: { count: 5 },
    portrait: { count: 3 },
    street: { count: 1 },
    travel: { count: 1 },
    water: { count: 7 },
    art: { count: 1 },
    clouds: { count: 2 },
    boating: { count: 11 },
    sports: { count: 4 },
    architecture: { count: 11 },
    food: { count: 12 },
    "ice-cream": { count: 1 },
  };
};

export const s3Copy = async (bucketName: string, config: PamApiConfig) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}/s3_copy`,
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

export const uploadFile = async (file: File, config: PamApiConfig) => {
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}/upload`,
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
      headers: {
        "Content-Type": file.type,
        "Content-Length": `${file.size}`,
        ...getHeaders(config),
      },
      body: await file.arrayBuffer(),
    });
  } else {
    console.error(response);
    throw new Error("Upload failed.");
  }
};
