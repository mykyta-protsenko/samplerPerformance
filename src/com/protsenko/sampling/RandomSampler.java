package com.protsenko.sampling;

import java.util.Random;

public class RandomSampler extends Sampler {

    private Random r = new Random();

    public RandomSampler(int sampleRate) {
        super("RandomSampler", sampleRate);
    }

    @Override
    public boolean performSample() {
        int rand = r.nextInt(getRate());
        return rand == 0;
    }

    @Override
    public Sampler cloneInstance() {
        return new RandomSampler(getRate());
    }

}
