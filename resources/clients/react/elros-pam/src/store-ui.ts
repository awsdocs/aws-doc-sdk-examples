import { create } from "zustand";
import produce from "immer";

export interface UiStore {
  login: {
    loginModalVisible: boolean;
    setLoginModalVisible: (v: boolean) => void;
  };
}

export const useUiStore = create<UiStore>((set, get) => ({
  login: {
    loginModalVisible: false,
    setLoginModalVisible: (v) =>
      set(
        produce(get(), (draft) => {
          draft.login.loginModalVisible = v;
        })
      ),
  },
}));
