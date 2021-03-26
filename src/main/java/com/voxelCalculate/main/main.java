package com.voxelCalculate.main;

import java.io.IOException;

public class main {
    public static void main(String[] args) throws Exception {
        String path = "D:\\mycode\\体素分析\\MyAp\\nii\\Filtered_4DVolume.mat";
        String maskpath = "D:\\mycode\\体素分析\\MyAp\\Mask\\mask.mat";
        String targerpath = "D:\\mycode\\体素分析\\MyAp\\result\\res.mat";


//        String path = "D:\\mycode\\体素分析\\MyAp\\nii\\test.mat";
//        String maskpath = "D:\\mycode\\体素分析\\MyAp\\Mask\\testmask.mat";
//        String targerpath = "D:\\mycode\\体素分析\\MyAp\\result\\restest.mat";

//
        MultiThreadsCal calculater = new MultiThreadsCal(maskpath, path);
        float[][][] stream = calculater.readAndCal();
        ReadMyMat rm = new ReadMyMat();
        rm.writeMat(targerpath, stream);
        System.out.println("ok");



    }
}
