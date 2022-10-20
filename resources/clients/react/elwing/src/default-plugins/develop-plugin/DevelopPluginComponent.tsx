/**
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

import { useEffect, useState } from "react";
import raw from "raw.macro";
import { Converter } from "showdown";

const converter = new Converter();

const Component = () => {
  const [md, setMd] = useState("");

  useEffect(() => {
    const file = raw("../../../README.md");
    setMd(file);
  }, []);

  return (
    <div dangerouslySetInnerHTML={{ __html: converter.makeHtml(md) }}></div>
  );
};

export default Component;
