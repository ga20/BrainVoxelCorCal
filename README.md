# BrainVoxelCorCal
A Java program for computing Pearson's correlation coefficient between each voxel in brain 

## Intoduction

This project is implementation of Nodal Functional Connectivity Strength Analysis  in [Identifying and Mapping Connectivity Patterns of Brain Network Hubs in Alzheimer's Disease](https://pubmed.ncbi.nlm.nih.gov/25331602/).It uses multi-threads to accelerate the progress computing the mean correlation Pearson Correlation between time series of two brain voxels. And the approach results in good performance.

## Backgroud

[Functional Magnetic Resonance Imaging](https://en.wikipedia.org/wiki/Functional_magnetic_resonance_imaging) had been widely used in brain research. Researchers took advantage of bold-series to track blood flow in human brain during a period of time. The signals from different areas of brain indicated whether neurones in different  area were active. We could determined whether two areas of brain had [Dynamic functional connectivity](https://en.wikipedia.org/wiki/Dynamic_functional_connectivity) by correlation analysis. The number of whole brain voxels was huge, so correlation analysis was time-consuming. In Matlab platform, this work cost about several days. It was unbearable! So as to accelerated the progress of analysis , this project was born.

## Install

Clone from git:

```git
git clone git@github.com:ga20/BrainVoxelCorCal.git
```

Dependencies:

- jmatio

Enviroment:

- jdk 1.8.0

You can test it by compile src/main/java/com/voxelCalculate/main.java

## Usage

```java
        String path = "nii\\test.mat"; // your voxel mat file path
        String maskpath = "Mask\\testmask.mat"; // your mask file path
        String targerpath = "result\\res_test.mat"; // path where you want to save result
        MultiThreadsCal calculater = new MultiThreadsCal(maskpath, path);
        float[][][] stream = calculater.readAndCal();
        ReadMyMat rm = new ReadMyMat();
        rm.writeMat(targerpath, stream);
```

