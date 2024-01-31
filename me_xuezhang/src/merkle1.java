import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class merkle1 {
    public static void main(String[] args) {
        int n = 1024; // 示例输入

        long startTime = System.nanoTime(); // 开始计时

        int m = (n % 4 == 0) ? n : (n + 4 - n % 4);
        Random random = new Random();
        List<String> hashes = new ArrayList<>();

        // 生成初始哈希值
        System.out.println("原始数据hash生成:");
        for (int i = 0; i < m; i++) {
            int randomNumber = random.nextInt();
            String hash = sha256Hash(Integer.toString(randomNumber));
            hashes.add(hash);
            System.out.println("Hash " + i + ": " + hash);
        }

        int[][] flagArr = {{1, 2}, {3, 4}};
        List<String> currentHashes = hashes;
        int iteration = 1;

        while (currentHashes.size() > 1) {
            List<String> newHashes = new ArrayList<>();
            System.out.println("\n层数 " + iteration + ":");

            // 创建二维矩阵并填充不足的部分
            for (int i = 0; i < currentHashes.size(); i += 4) {
                List<String> matrix = new ArrayList<>();
                for (int j = i; j < i + 4; j++) {
                    if (j < currentHashes.size()) {
                        matrix.add(currentHashes.get(j));
                    } else {
                        matrix.add("0"); // 用零填充不足的部分
                    }
                }

                // 打印当前矩阵
                System.out.println("拟合矩阵: " + matrix);

                // 矩阵加和运算
                int sumResult = matrixSum(matrix, flagArr);
                System.out.println("卷积后的结果：" + sumResult);

                // 对求和结果进行哈希运算
                String newHash = sha256Hash(Integer.toString(sumResult));
                newHashes.add(newHash);
                System.out.println("新生成的hash值：" + newHash);
            }

            currentHashes = newHashes;
            iteration++;
        }

        // 打印最终的哈希值
        System.out.println("\n最终的树根值: " + currentHashes.get(0));


        long endTime = System.nanoTime(); // 结束计时
        long timeElapsed = endTime - startTime;
        double seconds = (double)timeElapsed / 1_000_000_000.0;
        System.out.println("运行完成时间: " + seconds + " 秒");
        System.out.println("运行完成时间: " + timeElapsed + " 纳秒");
    }

    public static String sha256Hash(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException | java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // 矩阵加和运算函数
    public static int matrixSum(List<String> matrix, int[][] flagArr) {
        int sum = 0;
        int[] convertedMatrix = new int[4];
        for (int i = 0; i < matrix.size(); i++) {
            // 从哈希值的前几个字符获取数值
            convertedMatrix[i] = matrix.get(i).equals("0") ? 0 : Integer.parseInt(matrix.get(i).substring(0, 4), 16);
        }

        // 执行矩阵加和运算
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                sum += convertedMatrix[i * 2 + j] * flagArr[i][j];
            }
        }
        return sum;
    }
}
