import com.voxelCalculate.main.ReadMyMat;

import java.io.IOException;

public class testWrite {

    public static void main(String[] args) throws IOException {
        ReadMyMat rmt  = new ReadMyMat();
        String targetpath = "D:\\mycode\\体素分析\\MyAp\\result\\res.mat";
        float[][][] a = new float[3][4][5];
        int i = 0;
        for(int x = 0 ; x < 3 ; ++x) {
            for (int y = 0; y < 4; ++y) {
                for (int z = 0; z < 5; ++z) {
                     a[x][y][z] = i++;
                }
            }
        }
        rmt.writeMat(targetpath, a);

    }
}
