import { useEffect, useRef, useState } from "react";

interface AudioProps {
  src: string;
}

function Audio(props: AudioProps) {
  const ref = useRef<HTMLAudioElement>(null);
  const [playing, setPlaying] = useState(false);

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
    ref.current?.addEventListener("ended", () => {
      setPlaying(false);
    });
    return () => {
      ref.current?.removeEventListener("ended", () => {});
    };
  }, [ref]);

  return (
    <>
      <audio ref={ref} src={props.src} />
      <button
        className={`media-button ${playing ? "pause" : ""}`}
        aria-label="Toggle audio"
        onClick={handleClick}
      ></button>
    </>
  );
}

export default Audio;
