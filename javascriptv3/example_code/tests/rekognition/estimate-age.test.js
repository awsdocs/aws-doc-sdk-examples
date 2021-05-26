import { DetectFaces } from "../../rekognition/estimate-age-example/src/estimate-age";
import { rekognitionClient } from "../../rekognition/estimate-age-example/src/libs/rekognitionClient.js";

jest.mock("../../rekognition/estimate-age-example/src/libs/rekognitionClient.js");

describe("@aws-sdk/client-rekognition mock", () => {
    it("should successfully mock Rekognition client", async () => {
        rekognitionClient.send.mockResolvedValue({ isMock: true });
        const response = await DetectFaces([0,1]);
        expect(response.isMock).toEqual(true);
    });
});
