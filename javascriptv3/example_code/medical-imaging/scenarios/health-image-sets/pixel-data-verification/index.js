// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0

// gunzip to decompress metadata response
const zlib = require('zlib');
const util = require('util');
const gunzip = util.promisify(zlib.gunzip);
const CRC32 = require('crc-32');

// js/webassembly build of openjph
const openjphjs = require('./openjphjs/openjphjs.js');

// aws healthimaging js sdk v3
const AHI_REGION = '';
const { MedicalImagingClient, GetImageSetMetadataCommand, GetImageFrameCommand } = require('@aws-sdk/client-medical-imaging');
let imagingClientConfig;
if (AHI_REGION) imagingClientConfig.endpoint = AHI_REGION;
const miClient = new MedicalImagingClient(imagingClientConfig);

/**
 * @param {object} metadata - ImageSet metadata
 * @param {string} seriesInstanceUid - series instance UID
 * @param {string} sopInstanceUid - SOP instance UID
 * @return {Array} array of image frame IDs { ID: "imageFrameId" }
 */
const getImageFrameForSopInstance = (metadata, seriesInstanceUid, sopInstanceUid) => {
    try {
        return metadata.Study.Series[seriesInstanceUid].Instances[sopInstanceUid].ImageFrames;
    }
    catch (e) {
        throw 'Unable to get image frame ID from metadata. Check series and SOP instance UIDs.';
    }
};

openjphjs.onRuntimeInitialized = async (_) => {
    const decoder = new openjphjs.HTJ2KDecoder();

    if (process.argv.length < 5) {
        console.log('node index.js <datastoreid> <imagesetid> <seriesInstanceUid> <sopInstanceUid>');
        process.exit(1);
    }
    const datastoreId = process.argv[2];
    const imageSetId = process.argv[3];
    const seriesInstanceUid = process.argv[4];
    const sopInstanceUid = process.argv[5];

    // get the imageset metadata
    const getImageSetMetadataInput = {
        datastoreId: datastoreId,
        imageSetId: imageSetId,
    };
    const getImageSetMetadataCmd = new GetImageSetMetadataCommand(getImageSetMetadataInput);
    const getImageSetMetadataRsp = await miClient.send(getImageSetMetadataCmd);
    const imageSetMetadataBlobByteArray = await getImageSetMetadataRsp.imageSetMetadataBlob.transformToByteArray();
    const imageSetMetadataBuffer = await gunzip(imageSetMetadataBlobByteArray);
    const imageSetMetadata = JSON.parse(imageSetMetadataBuffer);

    // lookup the ImageFrame for the sopInstanceUid
    const imageFrameMeta = getImageFrameForSopInstance(imageSetMetadata, seriesInstanceUid, sopInstanceUid);
    const imageFrameId = imageFrameMeta[0].ID;

    // get the image frame
    const getImageFrameInput = {
        datastoreId: datastoreId,
        imageSetId: imageSetId,
        imageFrameInformation: {
            imageFrameId: imageFrameId,
        },
    };
    const getImageFrameCmd = new GetImageFrameCommand(getImageFrameInput);
    const getImageFrameRsp = await miClient.send(getImageFrameCmd);
    const imageFrameData = await getImageFrameRsp.imageFrameBlob.transformToByteArray();

    // decode the image frame
    const encodedBuffer = decoder.getEncodedBuffer(imageFrameData.length);
    encodedBuffer.set(imageFrameData);
    decoder.decode();
    const decodedBuffer = decoder.getDecodedBuffer();

    // calculate the CRC32 for the image frame
    const fullResCRC32Signed = CRC32.buf(decodedBuffer);
    const fullResCRC32 = fullResCRC32Signed >>> 0; // convert to unsigned
    
    // compare it to the value in the metadata
    const fullResCRC32FromMeta =
        imageFrameMeta[0].PixelDataChecksumFromBaseToFullResolution[
            imageFrameMeta[0].PixelDataChecksumFromBaseToFullResolution.length - 1
        ].Checksum;

    if (fullResCRC32 === fullResCRC32FromMeta) {
        console.log('CRC32 match!');
    } else {
        console.log('CRC32 does NOT match!');
    }
};
