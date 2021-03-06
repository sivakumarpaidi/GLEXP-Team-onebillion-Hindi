package org.onebillion.onecourse.utils;

import android.media.MediaRecorder;

import org.onebillion.onecourse.mainui.MainActivity;

import java.io.IOException;

public class OBGeneralAudioRecorder extends Object implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener
{
    MediaRecorder audioRecorder;
    public String recordingPath;
    OBConditionLock recorderLock;
    static int ST_UNINIT = 0,
    ST_PREPARED = 1,
    ST_RECORDING = 2,
    ST_FINISHED = 3;
    int state;

    public OBGeneralAudioRecorder()
    {
    }

    protected void initRecorder()
    {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        audioRecorder.setAudioEncodingBitRate(128000);
        audioRecorder.setAudioSamplingRate(44100);
        audioRecorder.setOutputFile(recordingPath);
        audioRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int i, int i1)
            {
                stopRecording();
            }
        });
    }

    void prepare()
    {
        try
        {
            audioRecorder.prepare();
            state = ST_PREPARED;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void startRecordingToPath(String path,float secs)
    {
        recordingPath = path;
        state = ST_UNINIT;
        initRecorder();
        audioRecorder.setMaxDuration((int)(secs * 1000));
        prepare();
        audioRecorder.start();
        state = ST_RECORDING;
    }

    public void stopRecording()
    {
        try
        {
            if (audioRecorder != null)
                audioRecorder.stop();
        }
        catch(Exception e)
        {
            MainActivity.log("OBGeneralAudioRecorder error in stopRecording");
        }
        state = ST_FINISHED;
        if (audioRecorder != null)
        {
            try
            {
                audioRecorder.reset();
                audioRecorder.release();
            }
            catch(Exception e)
            {
            }
            audioRecorder = null;
        }
    }

    public boolean recording()
    {
        return state == ST_RECORDING;
    }

    public float getAveragePower()
    {
        return audioRecorder.getMaxAmplitude();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra)
    {
        stopRecording();
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra)
    {
        stopRecording();
    }
}
