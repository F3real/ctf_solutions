import java.util.ArrayList; 
import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;


public class Blockchain {

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] decrypt(byte[] in, byte[] key) throws Exception {
        Key aesKey = new SecretKeySpec(key, "AES");
        Cipher encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        encryptCipher.init(Cipher.DECRYPT_MODE, aesKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryptCipher);
        cipherOutputStream.write(in);
        cipherOutputStream.flush();
        cipherOutputStream.close();
        return outputStream.toByteArray();
    }

    public static byte[] hash(byte[] in) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(in);
        return md.digest();
    }

    private static void decodeFlag(byte[] key) throws Exception{
        byte[] currPt = hexStringToByteArray("0eef68c5ef95b67428c178f045e6fc8389b36a67bbbd800148f7c285f938a24e696ee2925e12ecf7c11f35a345a2a142639fe87ab2dd7530b29db87ca71ffda2af558131d7da615b6966fb0360d5823b79c26608772580bf14558e6b7500183ed7dfd41dbb5686ea92111667fd1eff9cec8dc29f0cfe01e092607da9f7c2602f5463a361ce5c83922cb6c3f5b872dcc088eb85df80503c92232bf03feed304d669ddd5ed1992a26674ecf2513ab25c20f95a5db49fdf6167fda3465a74e0418b2ea99eb2673d4c7e1ff7c4921c4e2d7b");
        
        byte[] currKey = key;
        
        ArrayList<byte[]> keys = new ArrayList<>();
        
        keys.add(hash(currKey));
        for (int i = 1; i <10; i++) {
            keys.add(hash(keys.get(i-1)));
        }

        for (int i = 0; i < 10; i++) {
            currPt = decrypt(currPt, keys.get(9 - i));
        }
        String res = new String(currPt);
        if (res.matches("\\A\\p{ASCII}*\\z")) {
            System.out.println(res);
        }
    }
    
    public static void main(String[] args) throws Exception{
        for ( int i = -128; i < 128; i++) {
            for ( int j = -128; j < 128; j++) {
                for ( int k = -128; k < 128; k++) {
                    byte[] currKey = new byte[]{(byte)k, (byte)j, (byte)i};
                    decodeFlag(currKey);
                }              
            }
        }
        
        
    }

}       



