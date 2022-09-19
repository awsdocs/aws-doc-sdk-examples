import axios from "axios";
import configData from "./config.json";

export const getMessages = async () => {
  return await axios.get(`${configData.BASE_URL}/chat/msgs`);
};

export const postMessage = async (item) => {
  let user = item.username;
  let message = item.message;
  await axios.post(
    `${configData.BASE_URL}/chat/add?user=` + user + `&message=` + message
  );
};

export const purgeMessages = async () => {
  await axios.get(`${configData.BASE_URL}/chat/purge`)
}