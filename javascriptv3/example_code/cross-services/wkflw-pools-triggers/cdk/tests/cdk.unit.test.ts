// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import { describe, test, expect } from "vitest";
import { PreSignUpHandler } from "../lib/stack.autoConfirmHandler";
import type { UserRepository } from "../lib/user-repository";
import type { PreSignUpTriggerEvent } from "aws-lambda";

class FakeUserRepository implements UserRepository {
  private userInfo: Record<string, unknown> | undefined;

  setUserInfo(userInfo: Record<string, unknown> | undefined) {
    this.userInfo = userInfo;
  }

  async getUserInfoByEmail(
    userEmail: string,
  ): Promise<Record<string, unknown> | undefined> {
    return this.userInfo;
  }
}

describe("PreSignUpHandler", () => {
  test("should auto-confirm user if email and username match", async () => {
    const fakeUserRepo = new FakeUserRepository();
    fakeUserRepo.setUserInfo({
      UserName: "testuser",
      UserEmail: "test@example.com",
    });

    const preSignUpHandler = new PreSignUpHandler(fakeUserRepo);
    const event = {
      triggerSource: "PreSignUp_SignUp",
      userName: "testuser",
      request: {
        userAttributes: {
          email: "test@example.com",
        },
      },
      response: {
        autoConfirmUser: false,
        autoVerifyEmail: false,
      },
    } as unknown as PreSignUpTriggerEvent;

    const result = await preSignUpHandler.handlePreSignUpTriggerEvent(event);
    expect(result.response.autoConfirmUser).toBe(true);
    expect(result.response.autoVerifyEmail).toBe(true);
  });

  test("should not auto-confirm user if email and username do not match", async () => {
    const fakeUserRepo = new FakeUserRepository();
    fakeUserRepo.setUserInfo({
      UserName: "differentuser",
      UserEmail: "test@example.com",
    });

    const preSignUpHandler = new PreSignUpHandler(fakeUserRepo);
    const event = {
      triggerSource: "PreSignUp_SignUp",
      userName: "testuser",
      request: {
        userAttributes: {
          email: "test@example.com",
        },
      },
      response: {
        autoConfirmUser: false,
        autoVerifyEmail: false,
      },
    } as unknown as PreSignUpTriggerEvent;

    const result = await preSignUpHandler.handlePreSignUpTriggerEvent(event);
    expect(result.response.autoConfirmUser).toBe(false);
    expect(result.response.autoVerifyEmail).toBe(false);
  });

  test("should not auto-confirm user if email is not found", async () => {
    const fakeUserRepo = new FakeUserRepository();
    fakeUserRepo.setUserInfo(undefined);

    const preSignUpHandler = new PreSignUpHandler(fakeUserRepo);
    const event = {
      triggerSource: "PreSignUp_SignUp",
      userName: "testuser",
      request: {
        userAttributes: {
          email: "test@example.com",
        },
      },
      response: {
        autoConfirmUser: false,
        autoVerifyEmail: false,
      },
    } as unknown as PreSignUpTriggerEvent;

    const result = await preSignUpHandler.handlePreSignUpTriggerEvent(event);
    expect(result.response.autoConfirmUser).toBe(false);
    expect(result.response.autoVerifyEmail).toBe(false);
  });
});
