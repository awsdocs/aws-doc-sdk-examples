export interface PamApiConfig {
  token: string;
}

export const getTags = async (config: PamApiConfig) => {
  console.log(config.token);
  const response = await fetch(
    `${import.meta.env.VITE_API_GATEWAY_BASE_URL}/labels`,
    {
      headers: {
        Authorization: config.token,
      },
    }
  );
  console.log(response);
};
