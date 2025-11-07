package bikerboys.wackylife.voicechat;

import de.maxhenkel.voicechat.api.audio.AudioConverter;

import java.util.*;

public class DeepVoiceEffect {

    private static AudioConverter converter;
    private static Sonic sonic;
    private static short[] leftover = new short[0];
    private static short[] outputBuffer = new short[0];
    private static final int FRAME_SIZE = 960;

    public static void init(AudioConverter audioConverter) {
        converter = audioConverter;
        sonic = new Sonic(48000, 1);
        sonic.setPitch(0.7f);
        sonic.setRate(1.0f);
        sonic.setSpeed(1f);
    }

    public static short[] applyEffect(short[] input) {
        if (sonic == null || input == null || input.length == 0) {
            return new short[FRAME_SIZE];
        }

        short[] combined = new short[leftover.length + input.length];
        System.arraycopy(leftover, 0, combined, 0, leftover.length);
        System.arraycopy(input, 0, combined, leftover.length, input.length);

        int total = combined.length;
        int pos = 0;

        short[] tempChunk = new short[FRAME_SIZE];
        List<Short> allOutput = new ArrayList<>();
        short[] readBuffer = new short[FRAME_SIZE * 4];

        // 2. Process full frames
        while (pos + FRAME_SIZE <= total) {
            System.arraycopy(combined, pos, tempChunk, 0, FRAME_SIZE);
            sonic.writeShortToStream(tempChunk, FRAME_SIZE);
            pos += FRAME_SIZE;

            while (sonic.samplesAvailable() > 0) {
                int samplesRead = sonic.readShortFromStream(readBuffer, readBuffer.length);
                if (samplesRead > 0) {
                    for (int i = 0; i < samplesRead; i++) {
                        allOutput.add(readBuffer[i]);
                    }
                }
            }
        }

        leftover = Arrays.copyOfRange(combined, pos, total);

        short[] newOutput = new short[allOutput.size()];
        for (int i = 0; i < allOutput.size(); i++) {
            newOutput[i] = allOutput.get(i);
        }

        short[] tempBuffer = new short[outputBuffer.length + newOutput.length];
        System.arraycopy(outputBuffer, 0, tempBuffer, 0, outputBuffer.length);
        System.arraycopy(newOutput, 0, tempBuffer, outputBuffer.length, newOutput.length);
        outputBuffer = tempBuffer;

        short[] result = new short[FRAME_SIZE];
        if (outputBuffer.length >= FRAME_SIZE) {
            System.arraycopy(outputBuffer, 0, result, 0, FRAME_SIZE);
            outputBuffer = Arrays.copyOfRange(outputBuffer, FRAME_SIZE, outputBuffer.length);
        } else {
            if (outputBuffer.length > 0) {
                System.arraycopy(outputBuffer, 0, result, 0, outputBuffer.length);
            }
            outputBuffer = new short[0];
        }

        return result;
    }

    public static short[] flushAndGetRemaining() {
        if (sonic == null) return new short[0];
        sonic.flushStream();
        int available = sonic.samplesAvailable();
        if (available == 0) return new short[0];
        short[] res = new short[available];
        sonic.readShortFromStream(res, available);
        leftover = new short[0];
        outputBuffer = new short[0];
        return res;
    }
}