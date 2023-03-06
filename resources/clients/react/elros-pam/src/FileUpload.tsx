import { Button } from "@cloudscape-design/components";
import { ChangeEvent, useRef } from "react";

export interface FileUploadProps {
  disabled: boolean | undefined;
}

function FileUploadField({ disabled }: FileUploadProps) {
  const fileInput = useRef<HTMLInputElement>(null);

  const handleChange = ({ target }: ChangeEvent<HTMLInputElement>) => {
    if (target.files) {
      for (const file of target.files) {
        console.log(file);
      }
    }
  };
  // TODO this should open a modal that contains the input
  return (
    <form encType="multipart/form-data" method="post" action="/upload-file">
      <Button
        disabled={disabled}
        formAction="none"
        onClick={() => fileInput.current?.click()}
      >
        <input
          ref={fileInput}
          type="file"
          accept=".jpg, .jpeg"
          onChange={handleChange}
          hidden
          multiple
        />
        Upload
      </Button>
    </form>
  );
}

export default FileUploadField;
