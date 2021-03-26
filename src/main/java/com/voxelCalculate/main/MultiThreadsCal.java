package com.voxelCalculate.main;


import com.sun.org.apache.xml.internal.utils.res.XResources_sv;
import org.ujmp.core.task.Task;
import org.ujmp.core.util.R;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiThreadsCal {

    private String maskPath ;
    private String filePath ;
    private List<Node> nodeCollection;
    // valid voxel element in deed
    private int amount;
    private float[] dp ;
    // dp array capacity
    private int dpactualamout;
    public float[][][] result;

    public MultiThreadsCal(String maskPath, String filePath) {
        this.maskPath = maskPath;
        this.filePath = filePath;
    }

    public MultiThreadsCal(String filePath) {
        this.filePath = filePath;
    }

    class CalThread implements Runnable {
        private final String name;
        private final int numofNode;
        private CountDownLatch countDownLatch;

        public CalThread(String name , int numofNode, CountDownLatch countDownLatch) {
            this.name = name;
            this.numofNode = numofNode;
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            Node cur = nodeCollection.get(numofNode);
            double sumScore = 0;
            try{
                for(int i = 0 ; i < amount ; ++i){
                    // i correlate i = 1
                    if(i == numofNode){
                        sumScore += 1;
                        continue;
                    }

                    if(numofNode >= dpactualamout || i >= dpactualamout){
                        float score = PearsonCorrelation.getPearsonCorrelationScore(cur.timeSeries, nodeCollection.get(i).timeSeries);
                        sumScore += score;
                        continue;
                    }

                    // get dp address of numofNode and target node
                    int k = getIndexOfDp(numofNode + 1, i + 1);
                    if(dp[k] != 0){
                        sumScore += dp[k];
                        continue;
                    }

                    float score = PearsonCorrelation.getPearsonCorrelationScore(cur.timeSeries, nodeCollection.get(i).timeSeries);
                    sumScore += score;
                    if(dp[k] == 0) {
                        synchronized (dp){
                            dp[k] = score;
                        }
                    }
                }
                cur.meanCorrelationScore = (float) sumScore / amount;
                countDownLatch.countDown();
               System.out.println(String.format("( %d / %d )", numofNode, amount));
            }catch (Exception e) {
                System.out.println(numofNode + "error");
                throw new RuntimeException(e + "computational process exception");
            }
        }
    }

    public float[][][] readAndCal() throws Exception {
        ReadMyMat reader;
        try{
            if(maskPath == null){
                reader = new ReadMyMat(filePath);
            }else {
                reader = new ReadMyMat(maskPath, filePath);
            }

        }catch (Exception e){
            throw new RuntimeException(e + "\n error in read mask or data file" );
        }
        nodeCollection = reader.getNodeContent();
        amount = nodeCollection.size();

        // compute length of dp arr
        int dpLength = 0;
        dpactualamout = amount - 20000;
        for(int i = 1; i <= dpactualamout; i++){
            dpLength += i;
        }


        dp = new float[dpLength];
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        CountDownLatch countDownLatch = new CountDownLatch(amount);
        try{
            for(int i = 0; i < amount ; i++){
                String name = "myThread_" + i;
                Runnable runner = new CalThread(name, i, countDownLatch);
                executor.execute(runner);
            }
        }catch (Exception e){
            throw new Exception("Exception appeared in process of multiple threads compute ");
        }

        countDownLatch.await();


        System.out.println("All voxels have been computed");

        if(executor.getActiveCount() > 0){
            executor.shutdownNow();
        }

        int x = reader.shape[0];
        int y = reader.shape[1];
        int z = reader.shape[2];
        result = new float[x][y][z];
        for(Node node : nodeCollection){
            result[node.x][node.y][node.z] = node.meanCorrelationScore;
        }

        return result;

    }


    public float[][][] getRes(){
        if(result == null){
            throw new RuntimeException("Please invoke readAndCal to compute result");
        }
        return result;
    }


    private int getIndexOfDp(int i, int j){
        // i and j == j and i
        if(i > j){
            int t = i;
            i = j;
            j = t;
        }
        return (i - 1) * (2 * dpactualamout - i + 2) / 2 + j - i + 1;
    }
}
