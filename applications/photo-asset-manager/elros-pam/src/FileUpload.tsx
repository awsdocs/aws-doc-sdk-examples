// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import {
  Alert,
  Button,
  Header,
  Modal,
  SpaceBetween,
  Spinner,
} from "@cloudscape-design/components";
import { ChangeEvent, useCallback, useEffect, useRef, useState } from "react";

export interface FileUploadProps {
  accept?: string[];
  disabled: boolean | undefined;
  onSubmit: (file: File) => Promise<void>;
}

function FileUpload({ disabled, onSubmit, accept = [] }: FileUploadProps) {
  const [modalVisible, setModalVisible] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isTooLarge, setIsTooLarge] = useState(false);
  const fileInput = useRef<HTMLInputElement>(null);
  const imgRef = useRef<HTMLImageElement>(null);

  useEffect(() => {
    if (imgRef.current) {
      const img = imgRef.current;
      img.onload = () => {
        if (img.naturalWidth > 10000 || img.naturalHeight > 10000) {
          setIsTooLarge(true);
        } else {
          setIsTooLarge(false);
        }
      };
    }
  }, [selectedFile, imgRef]);

  const handleChange = useCallback(
    ({ target }: ChangeEvent<HTMLInputElement>) => {
      if (target.files?.length) {
        const file = target.files[0];
        setSelectedFile(file);
      }
    },
    [setSelectedFile]
  );

  const handleDismiss = useCallback(() => {
    setModalVisible(false);
    setError(null);
    setSelectedFile(null);
  }, [setModalVisible, setError, setSelectedFile]);

  const handleSelectFiles = useCallback(() => {
    setError(null);
    setSelectedFile(null);
    fileInput.current?.click();
  }, [setError, setSelectedFile, fileInput]);

  const handleSubmit = useCallback(
    async (e: React.FormEvent<HTMLFormElement>) => {
      e.preventDefault();
      setError(null);
      const formData = new FormData(e.currentTarget);
      const file = formData.get("file") as File | null;
      if (!file) {
        setError("No file selected.");
        return;
      }
      try {
        setIsLoading(true);
        await onSubmit(file);
        setModalVisible(false);
      } catch (err) {
        console.error("Upload failed.", err);
        const message = (err as Error)?.message ?? "Upload failed.";
        setError(message);
      } finally {
        setIsLoading(false);
      }
    },
    [setError, setIsLoading, setModalVisible, onSubmit]
  );

  return (
    <>
      <Button disabled={disabled} onClick={() => setModalVisible(true)}>
        Upload
      </Button>
      <Modal
        visible={modalVisible}
        header={<Header variant="h2">Upload file</Header>}
        onDismiss={handleDismiss}
      >
        <SpaceBetween size="s">
          {error && <Alert type="error">{error}</Alert>}
          <form encType="multipart/form-data" onSubmit={handleSubmit}>
            {selectedFile && (
              <img
                ref={imgRef}
                width={125}
                src={URL.createObjectURL(selectedFile)}
                alt="Image to upload for archival and label detection"
              />
            )}
            <SpaceBetween size="s" direction="horizontal">
              <Button
                disabled={disabled}
                formAction="none"
                onClick={handleSelectFiles}
              >
                Select file
              </Button>
              <Button
                disabled={
                  isTooLarge ||
                  isLoading ||
                  fileInput.current?.value.length === 0
                }
              >
                {isLoading ? <Spinner /> : "Upload"}
              </Button>
            </SpaceBetween>
            <input
              name="file"
              ref={fileInput}
              type="file"
              accept={accept.join(",")}
              onChange={handleChange}
              hidden
            />
          </form>
        </SpaceBetween>
      </Modal>
    </>
  );
}

export default FileUpload;
