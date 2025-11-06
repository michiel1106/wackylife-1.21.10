package bikerboys.wackylife.voicechat;

public class DeepVoiceEffect {
    public static short[] applyEffect(short[] audio) {
        return deepenVoice(audio, 0.85f);
    }

    private static short[] deepenVoice(short[] audio, float pitchFactor) {
        if (audio == null || audio.length == 0) return audio;

        int stretchedLength = Math.max(1, Math.round(audio.length / pitchFactor));
        float[] stretched = new float[stretchedLength];

        int grainSize = 1024; // length of each analysis window
        int overlap = grainSize / 2;
        int hopIn = grainSize - overlap;
        int hopOut = Math.round(hopIn / pitchFactor);

        float[] window = new float[grainSize];
        for (int i = 0; i < grainSize; i++) {
            window[i] = 0.5f * (1f - (float)Math.cos(1f * Math.PI * i / (grainSize - 1)));
        }

        int inputPos = 0;
        int outputPos = 0;

        // first grain straight copy
        for (int i = 0; i < grainSize && i < audio.length && i < stretched.length; i++) {
            stretched[i] += window[i] * audio[i];
        }

        inputPos += hopIn;
        outputPos += hopOut;

        while (inputPos + grainSize < audio.length && outputPos + grainSize < stretched.length) {
            // find best overlap offset in +/- searchRange
            int searchRange = overlap / 2;
            int bestOffset = 0;
            float bestCorr = -Float.MAX_VALUE;

            for (int offset = -searchRange; offset <= searchRange; offset++) {
                float corr = 0f;
                for (int j = 0; j < overlap; j++) {
                    int aIdx = inputPos + offset + j;
                    int bIdx = inputPos - overlap + j;
                    if (aIdx < 0 || aIdx >= audio.length || bIdx < 0 || bIdx >= audio.length) continue;
                    corr += audio[aIdx] * audio[bIdx];
                }
                if (corr > bestCorr) {
                    bestCorr = corr;
                    bestOffset = offset;
                }
            }

            int alignedPos = inputPos + bestOffset;

            // add grain with Hann window
            for (int i = 0; i < grainSize && outputPos + i < stretched.length && alignedPos + i < audio.length; i++) {
                stretched[outputPos + i] += window[i] * audio[alignedPos + i];
            }

            inputPos += hopIn;
            outputPos += hopOut;
        }

        // resample back to short array using linear interpolation
        short[] result = new short[audio.length];
        float scale = (stretchedLength - 1f) / (result.length - 1f);
        for (int i = 0; i < result.length; i++) {
            float pos = i * scale;
            int idx = (int)pos;
            float frac = pos - idx;
            float s0 = stretched[idx];
            float s1 = (idx + 1 < stretched.length) ? stretched[idx + 1] : s0;
            result[i] = clampToShort(Math.round(s0 * (1 - frac) + s1 * frac));
        }

        return result;
    }

    private static short clampToShort(int val) {
        if (val > Short.MAX_VALUE) return Short.MAX_VALUE;
        if (val < Short.MIN_VALUE) return Short.MIN_VALUE;
        return (short) val;
    }



}