let APP_NAME: string, APP_EMAIL: string, APP_LANG: string;

if (!process.env.FSA_NAME) {
  console.error("FSA_NAME is not defined");
  process.exit(1);
} else {
  APP_NAME = process.env.FSA_NAME.toLowerCase();
}

if (!process.env.FSA_EMAIL) {
  console.error("FSA_EMAIL is not defined");
  process.exit(1);
} else {
  APP_EMAIL = process.env.FSA_EMAIL.toLowerCase();
}

if (!process.env.FSA_LANG) {
  console.error("FSA_LANG is not defined");
  process.exit(1);
} else {
  APP_LANG = process.env.FSA_LANG.toLowerCase();
}

const PREFIX = `${APP_NAME}-${APP_LANG}`.toLowerCase();

export { APP_NAME, APP_EMAIL, APP_LANG, PREFIX };
