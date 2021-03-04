const mockDeleteAlbum = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.deleteObjects = mockDeleteAlbum;
    },
}));
const { deleteAlbum } = require("../../s3/photoExample/s3_PhotoExample");

test("has to mock S3#deleteAlbum", async (done) => {
    await deleteAlbum();
    expect(mockDeleteAlbum).toHaveBeenCalled;
    done();
});

const mockListObjects = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
    S3: function S3() {
        this.ListObjectsCommand = mockListObjects;
    },
}));
const { listAlbums } = require("../../s3/photoExample/s3_PhotoExample");
test("has to mock S3#listAlbums", async (done) => {
    await listAlbums();
    expect(mockListObjects).toHaveBeenCalled;
    done();
});

const mockAddPhoto = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
    S3: function S3() {
        this.ListObjectsCommand = mockAddPhoto;
    },
}));
const { addPhoto } = require("../../s3/photoExample/s3_PhotoExample");

test("has to mock S3#addPhoto", async (done) => {
    await addPhoto();
    expect(mockAddPhoto).toHaveBeenCalled;
    done();
});

const mockViewAlbum = jest.fn();
jest.mock("@aws-sdk/client-s3/commands/ListObjectsCommand", () => ({
    S3: function S3() {
        this.ListObjectsCommand = mockViewAlbum;
    },
}));
const { viewAlbum } = require("../../s3/photoExample/s3_PhotoExample");
test("has to mock S3#viewAlbum", async (done) => {
    await viewAlbum();
    expect(mockViewAlbum).toHaveBeenCalled;
    done();
});

const mockCreateAlbum = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.putObject = mockCreateAlbum;
    },
}));
const { createAlbum } = require("../../s3/photoExample/s3_PhotoExample");
test("has to mock S3#createAlbum", async (done) => {
    await createAlbum();
    expect(mockCreateAlbum).toHaveBeenCalled;
    done();
});


const mockDeletePhoto = jest.fn();
jest.mock("@aws-sdk/client-s3", () => ({
    S3: function S3() {
        this.deleteObject = mockDeletePhoto;
    },
}));
const { deletePhoto } = require("../../s3/photoExample/s3_PhotoExample");

test("has to mock S3#deletePhoto", async (done) => {
    await deletePhoto();
    expect(mockDeletePhoto).toHaveBeenCalled;
    done();
});


