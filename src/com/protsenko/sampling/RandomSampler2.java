package com.protsenko.sampling;

import java.util.Random;

public class RandomSampler2 extends Sampler {

    public RandomSampler2(int sampleRate) {
        super("Random2Sampler", sampleRate);
    }

    @Override
    public boolean performSample() {
        Random r = new Random();
        int rand = r.nextInt(getRate());
        return rand == 0;
    }

    @Override
    public Sampler cloneInstance() {
        return new RandomSampler2(getRate());
    }

}
