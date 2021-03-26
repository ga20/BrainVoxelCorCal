package com.voxelCalculate.main;
import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLSingle;
import com.jmatio.types.MLUInt8;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class ReadMyMat {

    private float[][][][] nodeCollection;
    private short[][][] mask;
    private List<Node> nodeContent;
    public int[] shape;
    public int maskValidElementCount;

    public ReadMyMat(){};
    ReadMyMat(String maskpath, String filePath) throws IOException {
        mask = readMask(maskpath);
        nodeCollection = readMat(filePath);
    }
    ReadMyMat(String filePath) throws IOException {
        nodeCollection = readMat(filePath);
    }

    public float[][][][] readMat(String filePath) throws IOException {

        MatFileReader read = new MatFileReader(filePath);
        MLArray mlArray = read.getMLArray("myniidata");
        shape = mlArray.getDimensions();
        int sizex = shape[0];
        int sizey = shape[1];
        int sizez = shape[2];
        int time = shape[3];
        MLSingle d =(MLSingle)mlArray;
        ByteBuffer buffer = d.getRealByteBuffer();
        // save matrix element
        float[][][][] mat = new float[sizex][sizey][sizez][time];

        /**
         *  Because of ..
         *
         * */
        for(int t = 0 ; t <  time; ++t) {
            for (int z = 0; z < sizez; ++z) {
                for (int y = 0; y < sizey; ++y) {
                    for (int x = 0; x < sizex; ++x) {
                        mat[x][y][z][t] = buffer.getFloat();
                    }
                }
            }
        }
        return mat;
    }

    public short[][][] readMask(String maskPath) throws IOException {

        MatFileReader read = new MatFileReader(maskPath);
        // parameter "name" is variable name in .mat file
        MLArray mlArray = read.getMLArray("mask");
        int sizex = mlArray.getDimensions()[0];
        int sizey = mlArray.getDimensions()[1];
        int sizez = mlArray.getDimensions()[2];

        MLUInt8 d =(MLUInt8)mlArray;
        ByteBuffer buffer = d.getRealByteBuffer();
        // save intermediate
        // type of the variable in .mat file is "uint8"
        short[][][] mat = new short[sizex][sizey][sizez];
        for (int z = 0; z < sizez; ++z) {
            for (int y = 0; y < sizey; ++y) {
                for (int x = 0; x < sizex; ++x) {
                    mat[x][y][z] = buffer.get();
                }
            }
        }
        return mat;
    }

    public List<Node> getNodeContent(){

        int sizex = shape[0];
        int sizey = shape[1];
        int sizez = shape[2];
        int time = shape[3];
        if(mask == null){
            mask = new short[sizex][sizey][sizez];
            setDefaultMask(mask);
        }
        int listLength = 0;
        for(int x = 0 ; x < sizex ; ++x) {
            for (int y = 0; y < sizey; ++y) {
                for (int z = 0; z < sizez; ++z) {
                    if ((int) mask[x][y][z] == 1) {
                        listLength++;
                    }
                }
            }
        }
        maskValidElementCount = listLength;
        nodeContent = new ArrayList<>(listLength);

        // filter element  which has no zero time series in mask
        for(int x = 0 ; x < sizex ; ++x) {
            for (int y = 0; y < sizey; ++y) {
                for (int z = 0; z < sizez; ++z) {
                    if(mask[x][y][z] == 0 || !isTimeseriesNotZero( nodeCollection[x][y][z] )){
                        continue;
                    }
                    nodeContent.add(new Node(x,y,z, nodeCollection[x][y][z]));
                }
            }
        }

        return nodeContent;
    }

    public void writeMat(String targetpath, float[][][] mat) throws IOException {
        if(mat == null || mat.length  == 0 || mat[0].length == 0 || mat[0][0].length == 0){
            throw new NullPointerException("Array is null or there's no element in the array ");
        }
        int sizex = mat.length;
        int sizey = mat[0].length;
        int sizez = mat[0][0].length;
        Float[] source = new Float[sizex * sizey * sizez];
        int i = 0;
        for(int x = 0 ; x < sizex ; ++x) {
            for (int y = 0; y < sizey; ++y) {
                for (int z = 0; z < sizez; ++z) {
                    source[i++] = mat[x][y][z];
                }
            }
        }
        MLSingle mls = new MLSingle("meancorelationmat",source,1);
        MatFileWriter mfw = new MatFileWriter();
        List<MLArray> t = new ArrayList<>();
        t.add(mls);
        mfw.write(targetpath, t);
        System.out.println("Write 1d-array successfully!");
    }

    private boolean isTimeseriesNotZero(float[] ts){
        if(ts == null || shape == null || shape.length < 4 || ts.length != shape[3]){
            throw new RuntimeException("Length of the array is not same as time series or shape is not initialized!");
        }
        int n = ts.length;
        for(int i = 0; i < n; ++i){
            if(ts[i] != 0){
                return true;
            }
        }
        return false;
    }

    private void setDefaultMask(short[][][] mask){
        if(mask == null || mask.length == 0 || mask[0].length == 0 || mask[0][0].length == 0){
            throw new RuntimeException("Initialize default mask failed");
        }
        for(int i = 0 ; i < mask.length; ++i){
            for(int j = 0 ; j < mask[0].length ; ++j){
                Arrays.fill(mask[i][j],(short) 1);
            }
        }
    }

}

class Node{
    public int x;
    public int y;
    public int z;
    public float[] timeSeries;
    public float meanCorrelationScore;
    Node(int x, int y, int z, float[] timeSeries){
        this.x = x;
        this.y = y;
        this.z = z;
        this.timeSeries = timeSeries;
    }
}

