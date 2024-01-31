import java.security.MessageDigest;
import java.util.*;
import java.security.SecureRandom;

public class merkle0 {
    private List<String> leafNodes = new ArrayList<>();

    public static void main(String[] args) {
        try {
            merkle0 merkleTree = new merkle0();
            int n = 1024; // 示例值
            long startTime = System.nanoTime(); // 开始计时
            Random rand = new SecureRandom();

            System.out.println("Generating " + n + " random numbers for the Merkle Tree:");
            for (int i = 0; i < n; i++) {
                int randomNumber = rand.nextInt();
                System.out.println("Number " + (i + 1) + ": " + randomNumber);
                merkleTree.addLeafNode(String.valueOf(randomNumber));
            }

            System.out.println("\nBuilding Merkle Tree...");
            String rootHash = merkleTree.getRootHash();
            System.out.println("\nRoot Hash of the Merkle Tree: " + rootHash);
            long endTime = System.nanoTime(); // 结束计时
            long timeElapsed = endTime - startTime;
            double seconds = (double)timeElapsed / 1_000_000_000.0;
            System.out.println("运行完成时间: " + seconds + " 秒");
            System.out.println("运行完成时间: " + timeElapsed + " 纳秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addLeafNode(String value) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = md.digest(value.getBytes());
        String hashValue = bytesToHex(hashBytes);

        leafNodes.add(hashValue);
    }

    public String getRootHash() throws Exception {
        int level = 0;
        List<String> currentLevel = new ArrayList<>(leafNodes);

        while (currentLevel.size() > 1) {
            System.out.println("Level " + level + ": " + currentLevel);
            List<String> parentNodes = new ArrayList<>();

            for (int i = 0; i < currentLevel.size(); i += 2) {
                if ((i + 1) >= currentLevel.size()) {
                    parentNodes.add(combineHashes(currentLevel.get(i), ""));
                } else {
                    parentNodes.add(combineHashes(currentLevel.get(i), currentLevel.get(i+1)));
                }
            }

            currentLevel = parentNodes;
            level++;
        }

        return currentLevel.get(0);
    }

    private static String combineHashes(String firstHash, String secondHash) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        StringBuilder combinedData = new StringBuilder();
        combinedData.append(firstHash).append(secondHash);

        byte[] combinedBytes = md.digest(combinedData.toString().getBytes());
        return bytesToHex(combinedBytes);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder();

        for (byte b : bytes) {
            hexBuilder.append(Integer.toHexString((b & 0xFF) | 0x100));
        }

        return hexBuilder.substring(1).toUpperCase();
    }
}
