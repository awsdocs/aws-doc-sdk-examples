import { viewAlbum, listAlbums } from "../src/s3_PhotoViewer";

describe("Photoviewer", () => {

  let mockListObjects;

  beforeEach(() => {
    mockListObjects = jest.fn();
    jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
      S3: function S3() {
        this.ListObjectsCommand = mockListObjects;
      },
    }));
  });

  //test function
  test("has to mock S3#listAlbums", async (done) => {
    await listAlbums();
    expect(mockListObjects).toHaveBeenCalled;
    done();
  });

  //test function
  test("has to mock S3#viewAlbum", async (done) => {
    await viewAlbum("My Name");
    expect(mockListObjects).toHaveBeenCalled;
    done();
  });
});
