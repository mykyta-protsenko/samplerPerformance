package com.protsenko.sampling;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicSampler extends Sampler{

    private AtomicInteger counter = new AtomicInteger(0);

    public AtomicSampler(int sampleRate) {
        super("Atomic Sampler", sampleRate);
        counter.set(0);
    }

    @Override
    public boolean performSample() {
        int c = counter.incrementAndGet();
        if (c == getRate()) {
            counter.set(0);
            return true;
        }
        return false;
    }

    @Override
    public Sampler cloneInstance() {
        return new AtomicSampler(getRate());
    }

}
