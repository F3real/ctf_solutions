import java.lang.reflect.Array;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.security.MessageDigest;

public class Upos {
   
    static void lm(long[][] matrix) throws Exception
    {
      long lVar1;
      String ref_00;
      String[] ppSVar3;
      BufferedReader ref_01;
      int iVar5;
      int iVar6;
      int iVar7;

      ref_01 = new BufferedReader(new FileReader("lotto.dat"));

      iVar5 = 0;
      while ((ref_00 = ref_01.readLine()) != null) {
        ppSVar3 = ref_00.split(" ");
        iVar7 = 0;
        iVar6 = 0;
        while (iVar6 < ppSVar3.length) {
          lVar1 = Long.parseLong(ppSVar3[iVar6]);
          matrix[iVar5][iVar7] = lVar1;
          iVar7 = iVar7 + 1;
          iVar6 = iVar6 + 1;
        }
        if (iVar7 != 0x100) {
            System.out.println("Fail!");
        }
        iVar5 = iVar5 + 1;
      }
      if (iVar5 == 0x100) {
        ref_01.close();
        return;
      }
      System.out.println("Fail!");
      return;
    }  
    
    private static String h(String flag) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(flag.getBytes());
            return th(md.digest());
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String th(byte[] a) {
       StringBuilder sb = new StringBuilder(a.length * 2);
       for(byte b: a)
          sb.append(String.format("%02x", b));
       return sb.toString();
    }

    public static boolean ip(int x)
    {
      int local_0 = 2;
      while( true ) {
        if (x <= local_0) {
          return true;
        }
        if (x % local_0 == 0) break;
        local_0 = local_0 + 1;
      }
      return false;
    }

    public static String r(String s) {
        String out = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 's') {
                c = (char) (c + 7);
            } else if (c >= 'A' && c <= 'S') {
                c = (char) (c + 7);
            } else if (c >= 't' && c <= 'z') {
                c = (char) (c - 19);
            } else if (c >= 'T' && c <= 'Z') {
                c = (char) (c - 19);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(out);
            sb.append(c);
            out = sb.toString();
        }
        return out;
    }

    public static long sq(String a) {
        return (long) Math.pow((double) ((a.charAt(0) + (a.charAt(1) << 8)) & 0x0000ffff), 2.0d);
    }

    public static long[][] m = ((long[][]) Array.newInstance(long.class, new int[]{256, 256}));

    
    public static void main(String args[]) throws Exception {
        
        Streamer streamer = new Streamer();
        boolean[] boolArray = new boolean[200];
        boolean bVar2 = true;
        long lVar1 = 0;
        int iVar14 = 0;
        int iVar8 = 0;      
        String ref = "";   
        lm(m);
        
        String flag  = "MOBISEC{Isnt_this_a_truly_evil_undebuggable_piece_of_sh^W_software??}";
        //Streamer step
        for (int i=0; i < 12; i++) {
            streamer.step();
        }       

        /*FileWriter fw = new FileWriter("2letter_combinations.txt");
        for(int i = '!'; i <= '~'; i++){
            for(int j = '!'; j <= '~'; j++){
                StringBuilder combination = new StringBuilder();
                combination.append(Character.toString((char)i));
                combination.append(Character.toString((char)j));
                String comb =  combination.toString();
                fw.write(comb);
                comb = r(comb);
                lVar1 = sq(comb);
                fw.write(" " + lVar1 + "\n");
            }
        }
        fw.close();*/
        
        String flag8 =  flag.substring(8);
        if (flag.length() != 69) {
            System.out.println("Wrong flag length!");
        }
        
        //Algorithm        
        int idx = 100;
        int i = 0;
        while (i < 30) {
          boolArray[idx] = true;
          StringBuilder ref_09 = new StringBuilder();
          char cVar5 = flag8.charAt(i * 2);
          ref = Character.toString(cVar5);
          ref_09.append(ref);
          cVar5 = flag8.charAt(i * 2 + 1);
          ref = Character.toString(cVar5);
          ref_09.append(ref);
          ref = ref_09.toString();
          bVar2 = ip(i);
          if (bVar2) {
            iVar14 = 0;
            while (iVar14 < i) {
              streamer.step();
              iVar14 = iVar14 + 1;
            }
          }
          iVar14 = streamer.g2();
          iVar8 = streamer.g2();
          ref = r(ref);
          lVar1 = sq(ref);
          //Key part
          if (lVar1 != m[iVar14 & 0xff][(iVar8 & 0xff00) >> 8]) {
            System.out.println(m[iVar14 & 0xff][(iVar8 & 0xff00) >> 8]);
          }
          idx = idx + 1;
          i = i + 1;
        }
        
        iVar14 = 100;
        
        while( true ) {
          if (idx <= iVar14) {
            ref = h(flag);
            System.out.println(ref);
            bVar2 = ref.equals("4193d9b72a5c4805e9a5cc739f8a8fc23b2890e13b83bb887d96f86c30654a12");
            if (bVar2 != false) {
              System.out.println("Correct flag!!!!");
              System.exit(0);
            }
            else {
                System.out.println("Wrong flag");
            }
          }
          if (boolArray[iVar14] == false) {
              break;
          }
          iVar14 = iVar14 + 1;
        }
        System.out.println("Wrong flag 2");
    }
}
