import java.util.Base64;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class MyClass {
    private static byte[] initVector = new byte[]{-34, -83, -66, -17, -34, -83, -66, -17, -34, -83, -66, -17, -34, -83, -66, -17};

    public static String ds(String enc) {
        try {
            String[] parts = "com.mobisec.dexclassloader".split(Pattern.quote("."));
            StringBuilder sb = new StringBuilder();
            sb.append(parts[1]);
            sb.append(parts[0]);
            sb.append("key!!!");
            String key = sb.toString();
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, skeySpec, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(enc.getBytes())));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private static String decryptString(String enc) {
        try {
            String[] parts = "com.mobisec.stage1".split(Pattern.quote("."));
            String key = "!!!" + parts[0] + parts[1] + "key";
            IvParameterSpec iv = new IvParameterSpec(initVector);
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(2, skeySpec, iv);
            return new String(cipher.doFinal(Base64.getDecoder().decode(enc.getBytes())));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static String gc() {
        return ds("ca9O9YbCZ/+vIYUvxPQUHA4SgyL/m3cwlvVZ4ArkBFQ=");
    }

    private static String gm() {
        return ds("6RSjLOfRkvb/qXa34Y7cOQ==");
    }

    public static String gu() {
        return ds("MAi9CEe4K9a+JzgsNqdYYh13dk7SQQ/yo5BN5HF39nYtgnOBmO4EV9Y2sQDthTG9");
    }
    public static String gf() {
        return ds("QLrdlqkhOkxIe5FEfpCLWw==");
    }
    private static String getAssetsName() {
        return decryptString("VYsdn556h+cox7bnQV4UsA==");
    }

    private static String getCodeName() {
        return decryptString("SXkAHK1O8Ssd6aCiqtpiAg==");
    }
    private static String generateMethod() {
        return decryptString("zkKQzoRGUwBJurGdAYVuMw==");
    }
    private static String generateClass() {
        return decryptString("4hJIFOEdvGy81kngg5W2mh4ZMYOQVmqX+FqCg8UmFmc=");
    }


    public static void main(String args[]) {
        System.out.println(generateClass());
    }
}