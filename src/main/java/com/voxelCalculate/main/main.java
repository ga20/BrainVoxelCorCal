package com.voxelCalculate.main;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws Exception {
        String path = "nii\\test.mat";
        String maskpath = "Mask\\testmask.mat";
        String targerpath = "result\\res_test.mat";
        MultiThreadsCal calculater = new MultiThreadsCal(maskpath, path);
        float[][][] stream = calculater.readAndCal();
        ReadMyMat rm = new ReadMyMat();
        rm.writeMat(targerpath, stream);
        System.out.println("ok");
    }
}
