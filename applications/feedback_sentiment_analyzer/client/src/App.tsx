// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import AppLayout from "@cloudscape-design/components/app-layout";
import { useEffect } from "react";

import "./App.css";
import LoginNavigation from "./LoginNavigation";
import { useStore } from "./store";
import FeedbackLayout from "./FeedbackLayout";

function App() {
  const { checkAuth, token } = useStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth, token]);

  return (
    <>
      <LoginNavigation title="Feedback sentiment analyzer" />
      <AppLayout
        toolsHide={true}
        navigationHide={true}
        contentType="cards"
        content={<FeedbackLayout />}
      />
    </>
  );
}

export { App };
