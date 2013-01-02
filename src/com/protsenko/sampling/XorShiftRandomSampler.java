package com.protsenko.sampling;


public class XorShiftRandomSampler extends Sampler {

    public XorShiftRandomSampler(int sampleRate) {
        super("WeakRandomSampler", sampleRate);
    }

    @Override
    public boolean performSample() {
        long x = rand();
        return x <= (Long.MAX_VALUE / getRate());
    }

    private long rand() {
        long x = System.currentTimeMillis();
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        return x;
    }

    @Override
    public Sampler cloneInstance() {
        return new XorShiftRandomSampler(getRate());
    }

}
