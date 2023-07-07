export interface ApiConfig {
  token: string | null;
}

export interface Feedback {
  id: string;
  text: string;
  audioUrl: string;
}

export type FeedbackResponse = {
  feedback: Array<Feedback>;
};

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
    console.error("API Upload failed.", response);
    throw new Error("API Upload failed.", { cause: response });
  }
};

export const downloadFile = async (fileName: string, config: ApiConfig) => {
  const response = await request(`/media/${fileName}`, {
    method: "GET",
    headers: { ...getAuthHeaders(config) },
  });

  if (!response.ok) {
    console.error("API Download failed.", response);
    throw new Error("API Download failed.", { cause: response });
  }

  const blob = await response.blob();
  const reader = new FileReader();

  return new Promise<string>((resolve, reject) => {
    reader.onloadend = () => {
      resolve(reader.result as string);
    };
    reader.onerror = reject;
    reader.readAsDataURL(blob);
  });
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
    console.error("API Feedback failed.", response);
    throw new Error("API Feedback failed.", { cause: response });
  } else {
    return response.json();
  }
};
