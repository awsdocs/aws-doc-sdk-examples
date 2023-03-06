import {
  TopNavigation,
  TopNavigationProps,
} from "@cloudscape-design/components";

import { useUiStore } from "./store-ui";
import { useAuthStore } from "./store-auth";
import { User } from "./auth";
import { useTagsStore } from "./store-tags";

export interface LoginNavigationProps {
  title: string;
}

function LoginNavigation({ title }: LoginNavigationProps) {
  const { authStatus, authManager, currentUser, setAuthStatus } =
    useAuthStore();
  const { clearTags } = useTagsStore();
  const {
    login: { setLoginModalVisible },
  } = useUiStore();

  const signedInUtilities = (
    currentUser: User | null
  ): TopNavigationProps.Utility => ({
    type: "menu-dropdown",
    text: currentUser?.username ?? "Unknown",
    items: [{ id: "signout", text: "Sign out" }],
    onItemClick: async (clickEvent) => {
      if (clickEvent.detail.id === "signout") {
        const authStatus = await authManager.signOut();
        clearTags();
        setAuthStatus(authStatus.status);
      }
    },
  });

  const signedOutUtilities = (): TopNavigationProps.Utility => ({
    type: "button",
    text: "Log in",
    onClick: () => {
      setLoginModalVisible(true);
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
