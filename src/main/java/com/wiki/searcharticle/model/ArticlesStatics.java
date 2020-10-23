package com.wiki.searcharticle.model;

public class ArticlesStatics {
    long sizeMin;
    long sizeMax;
    double sizeMedian;
    long wordCountMax;
    long wordCountMin;
    double wordCountMedian;

    public long getSizeMin() {
        return sizeMin;
    }

    public void setSizeMin(long sizeMin) {
        this.sizeMin = sizeMin;
    }

    public long getSizeMax() {
        return sizeMax;
    }

    public void setSizeMax(long sizeMax) {
        this.sizeMax = sizeMax;
    }

    public double getSizeMedian() {
        return sizeMedian;
    }

    public void setSizeMedian(double sizeMedian) {
        this.sizeMedian = sizeMedian;
    }

    public long getWordCountMax() {
        return wordCountMax;
    }

    public void setWordCountMax(long wordCountMax) {
        this.wordCountMax = wordCountMax;
    }

    public long getWordCountMin() {
        return wordCountMin;
    }

    public void setWordCountMin(long wordCountMin) {
        this.wordCountMin = wordCountMin;
    }

    public double getWordCountMedian() {
        return wordCountMedian;
    }

    public void setWordCountMedian(double wordCountMedian) {
        this.wordCountMedian = wordCountMedian;
    }
}
