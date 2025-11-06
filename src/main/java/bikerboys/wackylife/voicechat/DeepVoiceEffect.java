package bikerboys.wackylife.voicechat;


import java.io.*;


public class DeepVoiceEffect {

    public static short[] applyEffect(short[] audio) {
        int sampleRate = 48000;
        int numChannels = 1;

        Sonic sonic = new Sonic(sampleRate, numChannels);
        sonic.setSpeed(1.0f);
        sonic.setPitch(0.8f); // lower pitch
        sonic.setRate(1.0f);
        sonic.setVolume(1.0f);
        sonic.setChordPitch(false);
        sonic.setQuality(1);

        byte[] inBuffer = shortsToBytes(audio);
        byte[] outBuffer = new byte[inBuffer.length * 2];
        ByteArrayInputStream input = new ByteArrayInputStream(inBuffer);

        byte[] temp = new byte[4096];
        int numRead, numWritten, totalWritten = 0;

        do {
            numRead = input.read(temp, 0, temp.length);
            if (numRead <= 0) {
                sonic.flushStream();
            } else {
                sonic.writeBytesToStream(temp, numRead);
            }

            do {
                numWritten = sonic.readBytesFromStream(outBuffer, outBuffer.length - totalWritten);
                if (numWritten > 0) {
                    totalWritten += numWritten;
                }
            } while (numWritten > 0);
        } while (numRead > 0);

        short[] result = new short[totalWritten / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = (short) ((outBuffer[i * 2 + 1] << 8) | (outBuffer[i * 2] & 0xff));
        }
        return result;

    }

    private static byte[] shortsToBytes(short[] data) {
        byte[] out = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            out[i * 2] = (byte) (data[i] & 0xff);
            out[i * 2 + 1] = (byte) ((data[i] >> 8) & 0xff);
        }
        return out;
    }
}

