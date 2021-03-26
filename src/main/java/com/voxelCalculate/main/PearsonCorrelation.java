package com.voxelCalculate.main;

import java.util.List;
/**
 * calculate Pearson Correlation between two groups
 */
public class PearsonCorrelation {

    private final static float delta = 0.000001f;

    public static void main(String[] args) {
        test();
    }
    private static void test(){
        // test
        float[] x = new float[] { 0.98f, 0.96f, 0.96f, 0.94f, 0.925f, 0.9025f, 0.875f };
        float[] y = new float[] { 1, 1, 1, 1, 0.961483893f, 0.490591662f, 0.837341784f };
        float score = getScore(x, y);
        System.out.println(score);

    }

    public static float getScore(List<Float> x, List<Float> y) {
        if (x.size() != y.size())
            throw new RuntimeException("两组数据维度不正确！");
        float[] xData = new float[x.size()];
        float[] yData = new float[x.size()];
        for (int i = 0; i < x.size(); i++) {
            xData[i] = (float) x.get(i);
            yData[i] = (float) y.get(i);
        }
        return getPearsonCorrelationScore(xData,yData);
    }

    public static float getScore(float[] x, float[] y) {
        if (x.length != y.length)
            throw new RuntimeException("两组数据维度不正确！");
        return getPearsonCorrelationScore(x,y);
    }

    public static float getPearsonCorrelationScore(float[] xData, float[] yData) {
        if (xData.length != yData.length)
            throw new RuntimeException("两组数据维度不正确！");
        float xMeans;
        float yMeans;
        float numerator = 0;
        float denominator = 0;

        float result = 0;
        // get means
        xMeans = getMeans(xData);
        yMeans = getMeans(yData);
        // get numerator
        numerator = generateNumerator(xData, xMeans, yData, yMeans);
        // get Denomiator
        denominator = generateDenomiator(xData, xMeans, yData, yMeans);
        // final calculation
        result = numerator / (denominator + delta);
        return result;
    }


    /**
     * calculate the Numerator
     * @param xData
     * @param xMeans
     * @param yData
     * @param yMeans
     * @return numerator of the Pearson Correlation formula
     */
    private static float generateNumerator(float[] xData, float xMeans, float[] yData, float yMeans) {
        float numerator = 0.0f;
        for (int i = 0; i < xData.length; i++) {
            numerator += (xData[i] - xMeans) * (yData[i] - yMeans);
        }
        return numerator;
    }

    /**
     *  generate Denomiator
     * @param yMeans
     * @param yData
     * @param xMeans
     * @param xData
     * @return Denomiator of the Pearson Correlation fomula
     */
    private static float generateDenomiator(float[] xData, float xMeans, float[] yData, float yMeans) {
        float xSum = 0.0f;
        for (int i = 0; i < xData.length; i++) {
            xSum += (xData[i] - xMeans) * (xData[i] - xMeans);
        }
        float ySum = 0.0f;
        for (int i = 0; i < yData.length; i++) {
            ySum += (yData[i] - yMeans) * (yData[i] - yMeans);
        }
        return (float) Math.sqrt((xSum) * (ySum));
    }

    /**
     * calculate mean of data array
     *
     * @param  datas
     * @return mean of data
     */
    private static float getMeans(float[] datas) {
        float sum = 0.0f;
        for (int i = 0; i < datas.length; i++) {
            sum += datas[i];
        }
        return sum / datas.length;
    }
}