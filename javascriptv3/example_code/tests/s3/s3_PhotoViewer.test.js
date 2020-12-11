const mockListObjects = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
  S3: function S3() {
    this.ListObjectsCommand = mockListObjects;
  },
}));
const { listAlbums } = require("../../s3/photoViewer/s3_PhotoViewer");

test("has to mock S3#listAlbums", async (done) => {
  await listAlbums();
  expect(mockListObjects).toHaveBeenCalled;
  done();
});

const mockViewAlbum = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
  S3: function S3() {
    this.ListObjectsCommand = mockViewAlbum;
  },
}));
const { viewAlbum } = require("../../s3/photoViewer/s3_PhotoViewer");

test("has to mock S3#viewAlbum", async (done) => {
  await viewAlbum();
  expect(mockViewAlbum).toHaveBeenCalled;
  done();
});
