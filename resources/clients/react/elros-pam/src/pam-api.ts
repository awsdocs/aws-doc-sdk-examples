export interface PamApiConfig {
  token: string;
}

export interface TagsResponse {
  [key: string]: { count: number };
}

export const getTags = async (config: PamApiConfig): Promise<TagsResponse> => {
  // const response = await fetch(
  //   `${import.meta.env.VITE_API_GATEWAY_BASE_URL}/labels`,
  //   {
  //     headers: {
  //       Authorization: config.token,
  //     },
  //   }
  // );
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
