package com.protsenko.sampling;

public abstract class Sampler{

    private String name;
    private int rate;
    
	public Sampler(String name, int rate) {
        super();
        this.name = name;
        this.rate = rate;
    }
	
    public abstract boolean performSample();
    public abstract Sampler cloneInstance();

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
