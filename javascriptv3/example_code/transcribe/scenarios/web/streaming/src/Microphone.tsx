import "./Microphone.css";

interface MicrophoneProps {
  recording: boolean;
  onClick: () => void;
}

function Microphone({ recording, onClick }: MicrophoneProps) {
  return (
    <div className="Microphone">
      <button
        aria-label={recording ? "stop recording" : "start recording"}
        title={recording ? "stop recording" : "start recording"}
        className={recording ? "recording" : "record"}
        onClick={() => onClick()}
      ></button>
    </div>
  );
}

export default Microphone;
