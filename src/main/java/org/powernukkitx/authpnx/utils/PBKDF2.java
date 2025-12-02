package org.powernukkitx.authpnx.utils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.powernukkitx.authpnx.AuthPNX;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@RequiredArgsConstructor
public class PBKDF2 {

    byte[] salt = generateSalt();
    private final int iterations;
    private final int keyLength;

    public String encode(String password) {
        return bytesToHex(hashPassword(password, salt, iterations, keyLength));
    }

    @SneakyThrows
    private static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength) {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    private static byte[] generateSalt() {
        SecureRandom random = new SecureRandom(AuthPNX.get().getConfig().getString("pbkdf2.salt").getBytes(StandardCharsets.UTF_8));
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
