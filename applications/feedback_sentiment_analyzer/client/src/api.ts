// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
export interface ApiConfig {
  token: string | null;
}

export interface Feedback {
  sentiment: "POSITIVE" | "NEGATIVE" | "NEUTRAL";
  text: string;
  audioUrl: string;
  imageUrl: string;
}

export type FeedbackResponse = {
  feedback: Array<Feedback>;
};

const request: typeof fetch = async (input, init) => {
  const response = await fetch(`/api${input}`, init);
  if (response.status === 401) {
    throw new Error("Unauthorized");
  }
  return response;
};

export const getAuthHeaders = (
  config: ApiConfig
): { Authorization: string } | {} =>
  config.token
    ? {
        Authorization: `Bearer ${config.token}`,
      }
    : {};

export const uploadFile = async (file: File, config: ApiConfig) => {
  const response = await request(`/media/${file.name}`, {
    method: "PUT",
    body: file,
    headers: {
      ...getAuthHeaders(config),
      "Content-Type": "image/jpeg",
      "Content-Length": `${file.size}`,
    },
  });

  if (!response.ok) {
    throw new Error("API Upload failed.", { cause: response });
  }
};

export const downloadFile = async (fileName: string, config: ApiConfig) => {
  const response = await request(`/media/${fileName}`, {
    method: "GET",
    headers: { ...getAuthHeaders(config) },
  });

  if (!response.ok) {
    throw new Error("API Download failed.", { cause: response });
  }

  const blob = await response.blob();
  return URL.createObjectURL(blob);
};

export const getFeedback = async (
  config: ApiConfig
): Promise<FeedbackResponse> => {
  const response = await request(`/feedback`, {
    method: "GET",
    headers: {
      ...getAuthHeaders(config),
      Accept: "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("API Feedback failed.", { cause: response });
  } else {
    return response.json();
  }
};
