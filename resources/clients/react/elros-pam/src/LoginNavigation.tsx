import {
  TopNavigation,
  TopNavigationProps,
} from "@cloudscape-design/components";

import { useAuthStore, User } from "./store-auth";
import { useTagsStore } from "./store-tags";

export interface LoginNavigationProps {
  title: string;
}

function LoginNavigation({ title }: LoginNavigationProps) {
  const { authStatus, currentUser, signOut } = useAuthStore();
  const { clearTags } = useTagsStore();

  const signedInUtilities = (
    currentUser: User | null
  ): TopNavigationProps.Utility => ({
    type: "menu-dropdown",
    text: currentUser?.username ?? "Unknown",
    items: [{ id: "signout", text: "Sign out" }],
    onItemClick: async (clickEvent) => {
      if (clickEvent.detail.id === "signout") {
        signOut();
        clearTags();
      }
    },
  });

  const signedOutUtilities = (): TopNavigationProps.Utility => ({
    type: "button",
    text: "Sign in",
    onClick: () => {
      location.replace(import.meta.env.VITE_COGNITO_SIGN_IN_URL);
    },
  });

  return (
    <TopNavigation
      identity={{ title, href: "#" }}
      i18nStrings={{
        overflowMenuTitleText: "All",
        overflowMenuTriggerText: "More",
      }}
      utilities={[
        authStatus === "signed_in"
          ? signedInUtilities(currentUser)
          : signedOutUtilities(),
      ]}
    />
  );
}

export default LoginNavigation;
