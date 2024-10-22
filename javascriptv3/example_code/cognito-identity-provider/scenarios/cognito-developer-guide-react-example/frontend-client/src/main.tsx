// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./index.css";

const el = document.getElementById("root");

if (el) {
  ReactDOM.createRoot(el).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>,
  );
} else {
  throw new Error("Could not find root element.");
}
