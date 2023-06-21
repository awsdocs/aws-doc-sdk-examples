import {
  TopNavigation,
  TopNavigationProps,
} from "@cloudscape-design/components";

import { COGNITO_SIGN_IN_URL } from "./env";
import { useStore } from "./store";

export interface LoginNavigationProps {
  title: string;
}

function LoginNavigation({ title }: LoginNavigationProps) {
  const { authStatus, currentUser, signOut } = useStore();

  const signedInUtilities: TopNavigationProps.Utility = {
    type: "menu-dropdown",
    text: currentUser?.username ?? "Unknown",
    items: [{ id: "signout", text: "Sign out" }],
    onItemClick: async (clickEvent) => {
      if (clickEvent.detail.id === "signout") {
        signOut();
      }
    },
  };

  const signedOutUtilities: TopNavigationProps.Utility = {
    type: "button",
    text: "Sign in",
    href: `${COGNITO_SIGN_IN_URL}&redirect_uri=${location}`,
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
