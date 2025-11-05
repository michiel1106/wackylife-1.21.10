package bikerboys.wackylife.voicechat;

public class DeepVoiceEffect {

    private static final double SAMPLE_RATE = 16000.0;

    public static short[] applyAnonymousVoice(short[] audio) {
        short[] pitched = pitchShiftManual(audio, -8, -30);  // -8 semitones, -30 cents
        short[] muffled = lowPassSmooth(pitched, 0.07);       // soft low-pass
        short[] amplified = normalizeGain(muffled, 1.3);      // gentle gain
        return amplified;
    }


    private static short[] pitchShiftManual(short[] input, double semitones, double cents) {
        // Convert semitones + cents to pitch factor
        double factor = Math.pow(2, semitones / 12.0) * Math.pow(2, cents / 1200.0);

        short[] output = new short[input.length];
        for (int i = 0; i < input.length; i++) {
            double srcIndex = i * factor;
            if (srcIndex >= input.length) srcIndex = input.length - 1;
            int index1 = (int) Math.floor(srcIndex);
            int index2 = Math.min(index1 + 1, input.length - 1);
            double frac = srcIndex - index1;
            double sample = input[index1] * (1 - frac) + input[index2] * frac;
            output[i] = (short) Math.max(Math.min(sample, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }


    private static short[] lowPassSmooth(short[] input, double alpha) {
        short[] output = new short[input.length];
        double y = input[0];
        for (int i = 0; i < input.length; i++) {
            y += alpha * (input[i] - y);
            output[i] = (short)Math.max(Math.min(y, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }

    private static short[] normalizeGain(short[] input, double gain) {
        short[] output = new short[input.length];
        for (int i = 0; i < input.length; i++) {
            double v = input[i] * gain;
            output[i] = (short)Math.max(Math.min(v, Short.MAX_VALUE), Short.MIN_VALUE);
        }
        return output;
    }
}