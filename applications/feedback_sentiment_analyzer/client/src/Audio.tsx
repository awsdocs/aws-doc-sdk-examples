// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0
import { useEffect, useRef, useState } from "react";
import { useStore } from "./store";

interface AudioProps {
  src: string;
}

function Audio(props: AudioProps) {
  const [audio, setAudio] = useState("");
  const ref = useRef<HTMLAudioElement>(null);
  const [playing, setPlaying] = useState(false);
  const { downloadFile } = useStore();

  const handleClick = async () => {
    if (playing) {
      ref.current?.pause();
      setPlaying(false);
    } else {
      try {
        await ref.current?.play();
        setPlaying(true);
      } catch (e) {
        console.error(e);
      }
    }
  };

  useEffect(() => {
    const getAudio = async () => {
      try {
        const dataUrl = await downloadFile(props.src);
        setAudio(dataUrl);
      } catch (e) {
        console.error(e);
      }
    };
    if (props.src) {
      getAudio();
    }
  }, [props.src, downloadFile]);

  useEffect(() => {
    ref.current?.addEventListener("ended", () => {
      setPlaying(false);
    });
    return () => {
      ref.current?.removeEventListener("ended", () => {});
    };
  }, [ref.current]);

  return audio ? (
    <>
      <audio ref={ref} src={audio} />
      <button
        className={`media-button ${playing ? "pause" : ""}`}
        aria-label="Toggle audio"
        onClick={handleClick}
      ></button>
    </>
  ) : null;
}

export default Audio;
