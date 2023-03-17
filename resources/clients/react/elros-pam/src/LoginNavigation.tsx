import {
  TopNavigation,
  TopNavigationProps,
} from "@cloudscape-design/components";
import { useCallback } from "react";

import { useAuthStore, User } from "./store-auth";

export interface LoginNavigationProps {
  title: string;
}

function LoginNavigation({ title }: LoginNavigationProps) {
  const { authStatus, currentUser } = useAuthStore();

  const signedInUtilities: TopNavigationProps.Utility = {
    type: "menu-dropdown",
    text: currentUser?.username ?? "Unknown",
    items: [{ id: "signout", text: "Sign out" }],
    onItemClick: async (clickEvent) => {
      if (clickEvent.detail.id === "signout") {
        location.assign(import.meta.env.VITE_COGNITO_SIGN_OUT_URL)
      }
    },
  };

  const signedOutUtilities: TopNavigationProps.Utility = {
    type: "button",
    text: "Sign in",
    href: import.meta.env.VITE_COGNITO_SIGN_IN_URL,
  };

  return (
    <TopNavigation
      identity={{ title, href: "#" }}
      i18nStrings={{
        overflowMenuTitleText: "All",
        overflowMenuTriggerText: "More",
      }}
      utilities={[
        authStatus === "signed_in" ? signedInUtilities : signedOutUtilities,
      ]}
    />
  );
}

export default LoginNavigation;
