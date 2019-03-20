import java.lang.reflect.Field;
import java.lang.NoSuchFieldException;
import java.io.FileOutputStream;

//class ClassLoader is an abstract class so we have to extend it, if we want to use it.
public class Sol extends ClassLoader{

    public static void main(String[] args) throws Exception{
        /*
        Verifier0 ver0 = new Verifier0();
        if( ver0.verifyFlag("test")) {
            System.out.println("CORRECT FLAG");
        }
        else {
            System.out.println("INCORRECT FLAG");
        }
        */
        
        /*System.out.println(ver0.arr);*/
        
        
        for (int i = 0; i < 8; i++) {
            
            int currentVer = 0;
            String name = "Verifier" + String.valueOf(i);
            
            Class classT = Class.forName(name);
            Object ver = classT.newInstance();
            
            while (true) {
                Field f;
                try {
                    f = ver.getClass().getDeclaredField("arr");
                    f.setAccessible(true);
                }
                catch(NoSuchFieldException e) {
                    break;
                }
                byte [] arr =  (byte []) f.get(ver);
                
                try (FileOutputStream fos = new FileOutputStream(name + String.valueOf(currentVer) + ".class")) {
                    fos.write(arr);
                }
                
                /* We have to recreate class loader every time to avoid
                java.lang.LinkageError: loader (instance of  Sol): attempted  duplicate class definition
                */
                Sol _classLoader = new Sol();
                Class loadedClass  = _classLoader.defineClass(name, arr,0, arr.length);
                ver = loadedClass.newInstance();
                currentVer += 1;
            }
            
            System.out.println(name);
        }
    }

}
//C:\"Program Files"\Java\jdk1.8.0_171\bin\javac .\Sol.java















