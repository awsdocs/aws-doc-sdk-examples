// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { useEffect, useState } from "react";
import { useStore } from "./store";

interface ImageProps {
  src: string;
  alt: string;
}

function Image(props: ImageProps) {
  const [image, setImage] = useState("");
  const { downloadFile } = useStore();

  useEffect(() => {
    const getImage = async () => {
      try {
        const dataUrl = await downloadFile(props.src);
        setImage(dataUrl);
      } catch (e) {
        console.error(e);
      }
    };
    if (props.src) {
      getImage();
    }
  }, [props.src, downloadFile]);

  return image ? <img src={image} alt={props.alt} /> : null;
}

export default Image;
