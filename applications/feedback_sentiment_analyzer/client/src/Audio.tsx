import { useEffect, useRef, useState } from "react";
import { useStore } from "./store";

interface AudioProps {
  src: string;
}

function Audio(props: AudioProps) {
  const [objectUrl, setObjectUrl] = useState("");
  const ref = useRef<HTMLAudioElement>(null);
  const [playing, setPlaying] = useState(false);
  const { downloadFile } = useStore();

  const handleClick = () => {
    if (playing) {
      ref.current?.pause();
      setPlaying(false);
    } else {
      ref.current?.play();
      setPlaying(true);
    }
  };

  useEffect(() => {
    const getAudio = async () => {
      const audio = await downloadFile(props.src);
      const audioUrl = URL.createObjectURL(audio);
      setObjectUrl(audioUrl);
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
  }, [ref]);

  return (
    objectUrl ?? (
      <>
        <audio ref={ref} src={objectUrl} />
        <button
          className={`media-button ${playing ? "pause" : ""}`}
          aria-label="Toggle audio"
          onClick={handleClick}
        ></button>
      </>
    )
  );
}

export default Audio;
