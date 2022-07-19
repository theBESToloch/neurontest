package com.test.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Slf4j
@Service
public class VectorGeneratorService {

    private final Random rand;

    public VectorGeneratorService(){
        this.rand = new Random();
    }

    public double[] getOutputVector(int elementsCount) {
        double[] vector = new double[elementsCount];
        for (int in = 0; in < elementsCount; in++) {
            vector[in] = 0;
        }
        vector[rand.nextInt(elementsCount)] = 1;
        return vector;
    }

    public double[] getVector(int elementsCount) {
        double[] vector = new double[elementsCount];
        for (int in = 0; in < elementsCount; in++) {
            vector[in] = rand.nextInt(100);
        }
        return vector;
    }
}
