import { 
    deleteAlbum, 
    listAlbums, 
    viewAlbum,
    createAlbum,
    addPhoto, 
    deletePhoto } from "../src/s3_PhotoExample";


describe("S# Photo Example", () => {

    const ALBUM_NAME = "MY_ALBUM"
    const PHOTO_NAME = "MY_PHOTO"

    let mockCreateAlbum;
    let mockDeletePhoto; 
    let mockDeleteAlbum;
    let mockListObjects;
    let mockAddPhoto;

    beforeEach(() => {
        mockCreateAlbum = jest.fn();
        mockDeletePhoto = jest.fn();
        mockDeleteAlbum = jest.fn();
        mockListObjects = jest.fn();
        mockAddPhoto = jest.fn();

        window.open = jest.fn();
        window.alert = jest.fn();
        window.open = jest.fn();
        window.open = jest.fn();
        
        jest.mock("@aws-sdk/client-s3", () => ({
            S3: function S3() {
                this.putObject = mockCreateAlbum;
                this.deleteObject = mockDeletePhoto;
                this.deleteObjects = mockDeleteAlbum;
                this.ListObjectsCommand = mockListObjects;
                this.addPhoto = mockAddPhoto;
            },
        }));
    })

    //test function
    test("has to mock S3#deleteAlbum", async (done) => {
        await deleteAlbum(ALBUM_NAME);
        expect(mockDeleteAlbum).toHaveBeenCalled;
        done();
    });
    
    //test function
    test("has to mock S3#listAlbums", async (done) => {
        await listAlbums();
        expect(mockListObjects).toHaveBeenCalled;
        done();
    });

    //test function
    test("has to mock S3#addPhoto", async (done) => {
        await addPhoto(ALBUM_NAME);
        expect(mockAddPhoto).toHaveBeenCalled;
        done();
    });

    //test function
    test("has to mock S3#viewAlbum", async (done) => {
        await viewAlbum(ALBUM_NAME);
        expect(mockListObjects).toHaveBeenCalled;
        done();
    });

   
    //test function
    test("has to mock S3#createAlbum", async (done) => {
        await createAlbum(ALBUM_NAME);
        expect(mockCreateAlbum).toHaveBeenCalled;
        done();
    });

    //test function
    test("has to mock S3#deletePhoto", async (done) => {
        await deletePhoto(ALBUM_NAME, PHOTO_NAME);
        expect(mockDeletePhoto).toHaveBeenCalled;
        done();
    });
});