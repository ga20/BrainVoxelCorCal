package com.voxelCalculate.main;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class provides the multi-thread calculation function.
 * @Description This is core multi-thread class
 **/
public class MultiThreadsCal {

    private String maskPath ;
    private String filePath ;
    private List<Node> nodeCollection;
    // valid voxel element in deed
    private int amount;


    /** Due to Correlation matrix is symmetric. So we can store half of the matrix;
     *  In order to minimize the dp space, here we compress matrix storage.
     *  We store upper triangular matrix, so the real address for correlation(i,j)
     * equals (i - 1) * (2 * dpactualamout - i + 2) / 2 + j - i + 1.
     */
    private float[] dp ;
    // dp array capacity
    private int dpactualamout;
    public float[][][] result;

    public MultiThreadsCal(String maskPath, String filePath) {
        try {
            this.maskPath = maskPath;
            this.filePath = filePath;
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }

    }

    public MultiThreadsCal(String filePath) {
        try {
            this.filePath = filePath;
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    /**
     * task class
     */
    class CalThread implements Runnable {
        private String name;
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
                // current node correlate with other node
                for(int i = 0 ; i < amount ; ++i){
                    // i correlate i = 1
                    if(i == numofNode){
                        sumScore += 1;
                        continue;
                    }
                    // current index is out of the correlation matrix(dp), calculate directly
                    if(numofNode >= dpactualamout || i >= dpactualamout){
                        float score = PearsonCorrelation.getPearsonCorrelationScore(cur.timeSeries, nodeCollection.get(i).timeSeries);
                        sumScore += score;
                        continue;
                    }

                    // get dp address of numofNode and target node in dp
                    int k = getIndexOfDp(numofNode + 1, i + 1);
                    if(dp[k] != 0){
                        sumScore += dp[k];
                        continue;
                    }

                    float score = PearsonCorrelation.getPearsonCorrelationScore(cur.timeSeries, nodeCollection.get(i).timeSeries);
                    sumScore += score;
                    if(dp[k] == 0) {
                        // lock when thread write dp
                        synchronized (dp){
                            dp[k] = score;
                        }
                    }
                }
                cur.meanCorrelationScore = (float) sumScore / amount;
                // countDownLatch-- when task finished
                countDownLatch.countDown();
               System.out.println(String.format("( %d / %d )", numofNode + 1, amount));
            }catch (Exception e) {
                System.out.println(numofNode + "error");
                throw new RuntimeException(e + "computational process exception");
            }
        }
    }

    /**
     * read matrix and calculate
     * @return 3-d meanCorrelationScore
     * @throws Exception
     */
    public float[][][] readAndCal() throws Exception {
        ReadMyMat reader;
        try{
            if(maskPath == null){
                reader = new ReadMyMat(filePath);
            }else {
                reader = new ReadMyMat(maskPath, filePath);
            }
        }catch (IOException e){
            throw new IOException(e + "\n error in read mask or data file" );
        }
        nodeCollection = reader.getNodeContent();
        amount = nodeCollection.size();

        // compute length of dp arr
        int dpLength = 0;
        // real dp's length depends on JVM heap space, maybe cause OOM
        dpactualamout = amount * 4 / 5;
        for(int i = 1; i <= dpactualamout; i++){
            dpLength += i;
        }


        dp = new float[dpLength];
        // Thread pool
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

        // main thread wait until all tasks finished
        countDownLatch.await();

        Thread.sleep(1000 );

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

    /**
     * compute real address in dp
     *
     * @param i
     * @param j
     * @return real address in dp
     */
    private int getIndexOfDp(int i, int j){
        if(i < 0 || j < 0){
            throw new IllegalArgumentException("Parameters error");
        }
        // i and j == j and i
        if(i > j){
            int t = i;
            i = j;
            j = t;
        }
        return (i - 1) * (2 * dpactualamout - i + 2) / 2 + j - i + 1;
    }
}
