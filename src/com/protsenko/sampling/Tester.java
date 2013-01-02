package com.protsenko.sampling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Tester {
    
    private static final int SAMPLE_RATE = 1000;


    public long singleRun(Sampler sampler) {
        long t1 = System.currentTimeMillis();
        for (int i=0; i<10000000; i++) {
            sampler.performSample();
        }
        long t2 = System.currentTimeMillis();
        return t2-t1;
    }
    
    private void realTest(Sampler sampler) {
        long s = 0;
        for (int i=0; i<10; i++) {
            long res = singleRun(sampler);
            s += res;
            System.out.println(res);
        }
        s /= 10;
        System.out.println("average:" + s);
    }

    private void realMultithreadedTest(final Sampler sampler) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
        for (int i=0; i<10; i++) {
            tasks.add(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return singleRun(sampler);
                }
            });
        }
        
        List<Future<Long>> results = executor.invokeAll(tasks);

        long s = 0;
        for (Future<Long> res: results) {
            long r1 = res.get();
            s += r1;
            System.out.println(r1);
        }
        s /= 10;
        System.out.println("average:" + s);
        executor.shutdown();
    }

    private void realMultithreadedTestWithSeparateInstances(final List<Sampler> samplers) throws InterruptedException, ExecutionException, InstantiationException, IllegalAccessException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<Long>> tasks = new ArrayList<Callable<Long>>();
        for (int i=0; i<samplers.size(); i++) {
            final Sampler sampler = samplers.get(i);
            tasks.add(new Callable<Long>() {
                @Override
                public Long call() throws Exception {
                    return singleRun(sampler);
                }
            });
        }
        
        List<Future<Long>> results = executor.invokeAll(tasks);

        long s = 0;
        for (Future<Long> res: results) {
            long r1 = res.get();
            s += r1;
            System.out.println(r1);
        }
        s /= samplers.size();
        System.out.println("average:" + s);
        executor.shutdown();
    }
    
    private void warmUp(Sampler... samplers) {
        for (Sampler sampler: samplers) {
            singleRun(sampler);
        }
    }
    
    private void singleThreadTest(Sampler... samplers) {
        System.out.println("");
        System.out.println("=== Single thread ===:");
        for (Sampler sampler: samplers) {
            System.out.println(sampler.getName());
            realTest(sampler);
        }
    }

    private void concurrentTest_noShare(Sampler... samplers) throws InstantiationException, IllegalAccessException, InterruptedException, ExecutionException {
        System.out.println("");
        System.out.println("=== 10 separate threads using 10 separate samplers ===:");
        for (Sampler sampler: samplers) {
            List<Sampler> newSamplers = new ArrayList<Sampler>(); 
            for (int i=0;i<10;i++) {
                newSamplers.add(sampler.cloneInstance());
            }
            System.out.println(sampler.getName());
            realMultithreadedTestWithSeparateInstances(newSamplers);
        }
    }

    private void concurrentSharedTest(Sampler... samplers) throws InstantiationException, IllegalAccessException, InterruptedException, ExecutionException {
        System.out.println("");
        System.out.println("=== 10 separate threads sharing 1 sampler ===:");
        for (Sampler sampler: samplers) {
            System.out.println(sampler.getName());
            realMultithreadedTest(sampler);
        }
    }
    
    
    public static void main(String[] args) throws InterruptedException, ExecutionException, InstantiationException, IllegalAccessException {
        RandomSampler randomSampler = new RandomSampler(SAMPLE_RATE);
        RandomSampler2 randomSampler2 = new RandomSampler2(SAMPLE_RATE);
        AtomicSampler atomicSampler = new AtomicSampler(SAMPLE_RATE);
        XorShiftRandomSampler xorSampler = new XorShiftRandomSampler(SAMPLE_RATE);
        
        Tester tester = new Tester();
        // warming up - giving HotSpot compiler chance to compile tested methods
        tester.warmUp(atomicSampler, randomSampler, randomSampler2);
        
        tester.singleThreadTest(atomicSampler, randomSampler, randomSampler2, xorSampler);
        tester.concurrentTest_noShare(atomicSampler, randomSampler, randomSampler2, xorSampler);
        tester.concurrentSharedTest(atomicSampler, randomSampler, randomSampler2, xorSampler);
    }


}
