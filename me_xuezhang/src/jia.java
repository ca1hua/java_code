/*
package merkle1;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class merkle2 {
    private static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);

    public static void main(String[] args) {
        int n = 102400;
        long startTime = System.nanoTime();
        int m = getAdjustedSize(n);

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

        String finalHash = generateMerkleRoot(hashedNumbers);
        System.out.println("\n最终的Merkle根:");
        System.out.println(finalHash);

        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        double seconds = (double) timeElapsed / 1_000_000_000.0;
        System.out.println("运行完成时间: " + seconds + " 秒");
        System.out.println("运行完成时间: " + timeElapsed + " 纳秒");
    }

    private static String generateMerkleRoot(List<String> hashedNumbers) {
        int round = 1;
        while (hashedNumbers.size() > 1) {
            // 确保有偶数个哈希值
            if (hashedNumbers.size() % 2 != 0) {
                hashedNumbers.add(hashedNumbers.get(hashedNumbers.size() - 1));
            }

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


    private static List<BigInteger> dotProductModP(List<String[]> coordinatePoints) {
        List<BigInteger> modResults = new ArrayList<>();
        for (int i = 0; i < coordinatePoints.size(); i++) {
            String[] point1 = coordinatePoints.get(i);
            String[] point2 = (i + 1 < coordinatePoints.size()) ? coordinatePoints.get(i + 1) : new String[]{"0", "0"};

            String x1Str = point1[0].length() >= 16 ? point1[0].substring(0, 16) : String.format("%-16s", point1[0]).replace(' ', '0');
            String y1Str = point1[1].length() >= 16 ? point1[1].substring(0, 16) : String.format("%-16s", point1[1]).replace(' ', '0');
            String x2Str = point2[0].length() >= 16 ? point2[0].substring(0, 16) : String.format("%-16s", point2[0]).replace(' ', '0');
            String y2Str = point2[1].length() >= 16 ? point2[1].substring(0, 16) : String.format("%-16s", point2[1]).replace(' ', '0');

            BigInteger x1 = new BigInteger(x1Str, 16);
            BigInteger y1 = new BigInteger(y1Str, 16);
            BigInteger x2 = new BigInteger(x2Str, 16);
            BigInteger y2 = new BigInteger(y2Str, 16);

            BigInteger dotProduct = x1.multiply(y1).add(x2.multiply(y2));
            BigInteger modResult = dotProduct.mod(P);

            modResults.add(modResult);

            i++;
        }
        return modResults;
    }



    private static List<Integer> generateRandomNumbers(int m) {
        List<Integer> randomNumbers = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < m; i++) {
            randomNumbers.add(random.nextInt());
        }
        return randomNumbers;
    }

    private static List<String> hashNumbers(List<Integer> numbers) {
        List<String> hashedNumbers = new ArrayList<>();
        for (int number : numbers) {
            hashedNumbers.add(hashWithSHA256(String.valueOf(number)));
        }
        return hashedNumbers;
    }

    private static List<String[]> pairHashedNumbers(List<String> hashedNumbers) {
        List<String[]> pairs = new ArrayList<>();
        for (int i = 0; i < hashedNumbers.size(); i += 2) {
            String[] pair = new String[2];
            pair[0] = hashedNumbers.get(i);
            pair[1] = hashedNumbers.get(i + 1);
            pairs.add(pair);
        }
        return pairs;
    }

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

    private static int getAdjustedSize(int n) {
        if (n % 2 == 0) {
            return n;
        } else {
            return n + 1;
        }
    }
}



//package merkle1;
//
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Random;
//
//public class merkle2 {
//    private static final BigInteger P = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);
//
//    public static void main(String[] args) {
//        int n = 1024000;
//        long startTime = System.nanoTime();
//        int m = getAdjustedSize(n);
//
//        List<Integer> randomNumbers = generateRandomNumbers(m);
//        List<String> hashedNumbers = hashNumbers(randomNumbers);
//        String finalHash = generateMerkleRoot(hashedNumbers);
//
//        long endTime = System.nanoTime();
//        long timeElapsed = endTime - startTime;
//        double seconds = (double) timeElapsed / 1_000_000_000.0;
//        // You can keep or remove these lines depending on whether you consider them as "print statements".
//        System.out.println("运行完成时间: " + seconds + " 秒");
//        System.out.println("运行完成时间: " + timeElapsed + " 纳秒");
//    }
//
//    private static String generateMerkleRoot(List<String> hashedNumbers) {
//        while (hashedNumbers.size() > 1) {
//            if (hashedNumbers.size() % 2 != 0) {
//                hashedNumbers.add(hashedNumbers.get(hashedNumbers.size() - 1));
//            }
//
//            List<String[]> coordinatePoints = pairHashedNumbers(hashedNumbers);
//            List<BigInteger> modResults = dotProductModP(coordinatePoints);
//
//            hashedNumbers.clear();
//            for (BigInteger result : modResults) {
//                String hashedResult = hashWithSHA256(result.toString(16));
//                hashedNumbers.add(hashedResult);
//            }
//        }
//        return hashedNumbers.get(0);
//    }
//
//    private static List<BigInteger> dotProductModP(List<String[]> coordinatePoints) {
//        List<BigInteger> modResults = new ArrayList<>();
//        for (int i = 0; i < coordinatePoints.size(); i++) {
//            String[] point1 = coordinatePoints.get(i);
//            String[] point2 = (i + 1 < coordinatePoints.size()) ? coordinatePoints.get(i + 1) : new String[]{"0", "0"};
//
//            String x1Str = point1[0].length() >= 16 ? point1[0].substring(0, 16) : String.format("%-16s", point1[0]).replace(' ', '0');
//            String y1Str = point1[1].length() >= 16 ? point1[1].substring(0, 16) : String.format("%-16s", point1[1]).replace(' ', '0');
//            String x2Str = point2[0].length() >= 16 ? point2[0].substring(0, 16) : String.format("%-16s", point2[0]).replace(' ', '0');
//            String y2Str = point2[1].length() >= 16 ? point2[1].substring(0, 16) : String.format("%-16s", point2[1]).replace(' ', '0');
//
//            BigInteger x1 = new BigInteger(x1Str, 16);
//            BigInteger y1 = new BigInteger(y1Str, 16);
//            BigInteger x2 = new BigInteger(x2Str, 16);
//            BigInteger y2 = new BigInteger(y2Str, 16);
//
//            BigInteger dotProduct = x1.multiply(y1).add(x2.multiply(y2));
//            BigInteger modResult = dotProduct.mod(P);
//
//            modResults.add(modResult);
//
//            i++;
//        }
//        return modResults;
//    }
//
//    private static List<Integer> generateRandomNumbers(int m) {
//        List<Integer> randomNumbers = new ArrayList<>();
//        Random random = new Random();
//        for (int i = 0; i < m; i++) {
//            randomNumbers.add(random.nextInt());
//        }
//        return randomNumbers;
//    }
//
//    private static List<String> hashNumbers(List<Integer> numbers) {
//        List<String> hashedNumbers = new ArrayList<>();
//        for (int number : numbers) {
//            hashedNumbers.add(hashWithSHA256(String.valueOf(number)));
//        }
//        return hashedNumbers;
//    }
//
//    private static List<String[]> pairHashedNumbers(List<String> hashedNumbers) {
//        List<String[]> pairs = new ArrayList<>();
//        for (int i = 0; i < hashedNumbers.size(); i += 2) {
//            String[] pair = new String[2];
//            pair[0] = hashedNumbers.get(i);
//            pair[1] = hashedNumbers.get(i + 1);
//            pairs.add(pair);
//        }
//        return pairs;
//    }
//
//    private static String hashWithSHA256(String input) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(input.getBytes());
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private static int getAdjustedSize(int n) {
//        if (n % 2 == 0) {
//            return n;
//        } else {
//            return n + 1;
//        }
//    }
//}

*/
