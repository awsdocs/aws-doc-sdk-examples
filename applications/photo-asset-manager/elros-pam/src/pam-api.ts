// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
export interface PamApiConfig {
  token: string | null;
}
export interface Label {
  name: string;
  count: number;
}

export interface LabelsResponse {
  labels: Record<string, { count: number }>;
}

const request: typeof fetch = async (input, init) => {
  const response = await fetch(input, init);
  if (response.status === 401) {
    throw new Error("Unauthorized");
  }
  return response;
};

const getHeaders = (config: PamApiConfig): { Authorization: string } | {} =>
  config.token
    ? {
        Authorization: `Bearer ${config.token}`,
        "Content-Type": "application/json",
      }
    : {};

export const getLabels = async (config: PamApiConfig): Promise<Label[]> => {
  const response = await request(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}labels`,
    {
      headers: getHeaders(config),
    }
  );

  const tagsResponse: LabelsResponse = await response.json();
  return Object.entries(tagsResponse.labels).map(([name, { count }]) => ({
    name,
    count,
  }));
};

export const s3Copy = async (bucketName: string, config: PamApiConfig) => {
  const response = await request(
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
    console.error("API Copy failed.", response);
    throw new Error("API Copy failed.", { cause: response });
  }
};

export const initializeDownload = async (
  labels: string[],
  config: PamApiConfig
) => {
  const response = await request(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}download`,
    {
      method: "POST",
      body: JSON.stringify({
        labels,
      }),
      headers: getHeaders(config),
    }
  );

  if (!response.ok) {
    console.error("API Download failed.", response);
    throw new Error("API Download failed.", { cause: response });
  }
};

export const uploadFile = async (file: File, config: PamApiConfig) => {
  const response = await request(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}upload`,
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
