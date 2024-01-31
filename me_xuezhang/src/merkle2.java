import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class merkle2 {
    private static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16); // 使用比特币的椭圆曲线secp256k1的素数作为模数示例

    public static void main(String[] args) {
        int n = 1024; // 示例值
        long startTime = System.nanoTime(); // 开始计时
        int m = getAdjustedSize(n);

        // 生成并哈希处理随机数
        List<Integer> randomNumbers = generateRandomNumbers(m);
        System.out.println("生成的随机数:");
        for (int num : randomNumbers) {
            System.out.println(num);
        }

        List<String> hashedNumbers = hashNumbers(randomNumbers);
        System.out.println("\n哈希后的数字:");
        for (String hash : hashedNumbers) {
            System.out.println(hash);
        }

        // 生成Merkle根
        String finalHash = generateMerkleRoot(hashedNumbers);
        System.out.println("\n最终的Merkle根:");
        System.out.println(finalHash);


        long endTime = System.nanoTime(); // 结束计时
        long timeElapsed = endTime - startTime;
        double seconds = (double)timeElapsed / 1_000_000_000.0;
        System.out.println("运行完成时间: " + seconds + " 秒");
        System.out.println("运行完成时间: " + timeElapsed + " 纳秒");
    }
    // 生成Merkle根的过程中增加打印输出
    private static String generateMerkleRoot(List<String> hashedNumbers) {
        int round = 1;
        while (hashedNumbers.size() > 1) {
            System.out.println("\nRound " + round + ":");
            List<String[]> coordinatePoints = pairHashedNumbers(hashedNumbers);

            System.out.println("坐标点:");
            for (String[] point : coordinatePoints) {
                System.out.println("(" + point[0] + ", " + point[1] + ")");
            }

            List<BigInteger> modResults = dotProductModP(coordinatePoints);
            System.out.println("点乘模P结果:");
            for (BigInteger result : modResults) {
                System.out.println(result.toString(16));
            }

            hashedNumbers.clear();
            for (BigInteger result : modResults) {
                String hashedResult = hashWithSHA256(result.toString(16));
                hashedNumbers.add(hashedResult);
                System.out.println("哈希后的结果: " + hashedResult);
            }

            round++;
        }
        return hashedNumbers.get(0);
    }


    // 对坐标点进行点乘并模P
    private static List<BigInteger> dotProductModP(List<String[]> coordinatePoints) {
        List<BigInteger> modResults = new ArrayList<>();
        for (int i = 0; i < coordinatePoints.size(); i += 2) {
            String[] point1 = coordinatePoints.get(i);
            String[] point2 = (i + 1 < coordinatePoints.size()) ? coordinatePoints.get(i + 1) : new String[]{"0", "0"};

            // 将哈希值的前16个字符转换为BigInteger
            BigInteger x1 = new BigInteger(point1[0].substring(0, 16), 16);
            BigInteger y1 = new BigInteger(point1[1].substring(0, 16), 16);
            BigInteger x2 = new BigInteger(point2[0].substring(0, 16), 16);
            BigInteger y2 = new BigInteger(point2[1].substring(0, 16), 16);

            // 计算两对坐标的点乘
            BigInteger dotProduct = x1.multiply(y1).add(x2.multiply(y2));

            // 进行模P运算
            BigInteger modResult = dotProduct.mod(P);

            modResults.add(modResult);
        }
        return modResults;
    }

    // 生成指定数量的随机数
    private static List<Integer> generateRandomNumbers(int m) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < m; i++) {
            randomNumbers.add(random.nextInt());
        }
        return randomNumbers;
    }

    // 对数字列表进行哈希处理
    private static List<String> hashNumbers(List<Integer> numbers) {
        List<String> hashedNumbers = new ArrayList<>();
        for (int number : numbers) {
            hashedNumbers.add(hashWithSHA256(String.valueOf(number)));
        }
        return hashedNumbers;
    }

    // 将哈希值两两一组
    private static List<String[]> pairHashedNumbers(List<String> hashedNumbers) {
        List<String[]> pairs = new ArrayList<>();
        for (int i = 0; i < hashedNumbers.size(); i += 2) {
            String[] pair = new String[2];
            pair[0] = hashedNumbers.get(i);
            pair[1] = (i + 1 < hashedNumbers.size()) ? hashedNumbers.get(i + 1) : "0";
            pairs.add(pair);
        }
        return pairs;
    }

    // 使用SHA-256算法对字符串进行哈希处理
    private static String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 检查数字是否为2的倍数，不是则调整到下一个2的倍数
    private static int getAdjustedSize(int n) {
        if (n % 2 == 0) {
            return n;
        } else {
            return n + 1;
        }
    }
}
