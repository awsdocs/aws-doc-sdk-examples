import { useCallback, useState } from "react";
import "./App.css";
import Microphone from "./Microphone";

function App() {
  const [recording, setRecording] = useState(false);
  const transcription = useTranscriptionService({ recording });

  const handleClick = useCallback(() => {
    const newRecordingState = !recording;
    setRecording(newRecordingState);
  }, [recording, setRecording]);

  return (
    <>
      <div className="App">
        <div className="transcription">{transcription}</div>
      </div>
      <Microphone recording={recording} onClick={handleClick} />
    </>
  );
}

export default App;
