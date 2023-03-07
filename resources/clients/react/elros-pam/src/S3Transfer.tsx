import {
  Button,
  Form,
  FormField,
  Header,
  Modal,
  Input,
  SpaceBetween,
  Alert,
} from "@cloudscape-design/components";
import { useState } from "react";
import { s3Copy } from "./pam-api";
import { useAuthStore } from "./store-auth";

function S3Transfer() {
  const [modalVisible, setModalVisible] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sourceBucket, setSourceBucket] = useState("");
  const { token, authStatus } = useAuthStore();

  const handleCopy = async () => {
    setError(null);
    setSourceBucket("");
    try {
      await s3Copy(sourceBucket, { token });
    } catch (err) {
      console.error(err);
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError("Copy failed.");
      }
    }
  };

  const handleDismiss = () => {
    setModalVisible(false);
    setSourceBucket("");
    setError(null);
  };

  return (
    <>
      <Modal
        visible={modalVisible}
        onDismiss={handleDismiss}
        header={
          <Header
            variant="h2"
            description="Copy a large number of files from an S3 bucket into the archive."
          >
            Bulk copy from S3
          </Header>
        }
      >
        <Form>
          {error && <Alert type="error">{error}</Alert>}
          <FormField description="S3 source bucket name" label="Source bucket">
            <SpaceBetween size="s">
              <Input
                value={sourceBucket}
                onChange={(event) => setSourceBucket(event.detail.value)}
              ></Input>
              <Button disabled={sourceBucket.length < 3} onClick={handleCopy}>
                Copy
              </Button>
            </SpaceBetween>
          </FormField>
        </Form>
      </Modal>
      <Button
        disabled={authStatus !== "signed_in"}
        onClick={() => setModalVisible(true)}
      >
        S3 copy
      </Button>
    </>
  );
}

export default S3Transfer;
