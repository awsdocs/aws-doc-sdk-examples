import {
  Alert,
  Button,
  Header,
  Modal,
  SpaceBetween,
  Spinner,
} from "@cloudscape-design/components";
import { ChangeEvent, useRef, useState } from "react";

export interface FileUploadProps {
  accept?: string[];
  disabled: boolean | undefined;
  onSubmit: (file: File) => Promise<void>;
}

function FileUpload({ disabled, onSubmit, accept }: FileUploadProps) {
  const [modalVisible, setModalVisible] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const fileInput = useRef<HTMLInputElement>(null);

  const handleChange = ({ target }: ChangeEvent<HTMLInputElement>) => {
    if (target.files?.length) {
      setSelectedFile(target.files[0]);
    }
  };

  const handleDismiss = () => {
    setModalVisible(false);
    setError(null);
    setSelectedFile(null);
  };

  const handleSelectFiles = () => {
    setError(null);
    setSelectedFile(null);
    fileInput.current?.click();
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setError(null);
    const formData = new FormData(e.currentTarget);
    const file = formData.get("file") as File | null;
    if (!file) {
      setError("No file selected.");
    } else {
      try {
        setIsLoading(true);
        await onSubmit(file);
        setModalVisible(false);
      } catch (err) {
        console.error(err);
        if (err instanceof Error) {
          setError(err.message);
        } else {
          setError("Upload failed.");
        }
      } finally {
        setIsLoading(false);
      }
    }
  };

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
              <img width={125} src={URL.createObjectURL(selectedFile)} />
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
                disabled={isLoading || fileInput.current?.value.length === 0}
              >
                {isLoading ? <Spinner /> : "Upload"}
              </Button>
            </SpaceBetween>
            <input
              name="file"
              ref={fileInput}
              type="file"
              accept={accept && accept.join(",")}
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
