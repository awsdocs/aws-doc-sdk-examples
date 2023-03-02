import { create } from "zustand";
import produce from "immer";



export interface UiStore {
  login: {
    enabled: boolean;
    loginModalVisible: boolean;
    setEnabled: (v: boolean) => void;
    setLoginModalVisible: (v: boolean) => void;
  };
}

export const useUiStore = create<UiStore>((set, get) => ({
  login: {
    enabled: true,
    loginModalVisible: false,
    setEnabled: (v) => {
      set(
        produce(get(), (draft) => {
          draft.login.enabled = true;
        })
      );
    },
    setLoginModalVisible: (v) =>
      set(
        produce(get(), (draft) => {
          draft.login.loginModalVisible = v;
        })
      ),
  },
}));
