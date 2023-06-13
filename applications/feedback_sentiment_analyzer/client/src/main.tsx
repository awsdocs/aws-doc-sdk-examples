import "./polyfills";
import React from "react";
import ReactDOM from "react-dom/client";
import "@cloudscape-design/global-styles/index.css";

(async () => {
  if (import.meta.env.PROD) {
    const envResponse = await fetch("/api/env");
    const env = await envResponse.json();
    (window as any).APP_ENV = env;
  } else {
    (window as any).APP_ENV = {
      COGNITO_SIGN_IN_URL: "",
      COGNITO_SIGN_OUT_URL: "",
    };
  }

  const { App } = await import("./App");

  ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
})();
