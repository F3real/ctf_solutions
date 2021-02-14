
/* Class: Lcom/mobisec/upos/FC;
   Class Access Flags:
    ACC_PUBLIC
   
   Superclass: Ljava/lang/Object;
   Source File: FC.java
   
   Method Signature: Z( Landroid/content/Context;
    Ljava/lang/String;
     )
   Method Access Flags:
    ACC_PUBLIC
    ACC_STATIC
   
   Method Register Size: 30
   Method Incoming Size: 2
   Method Outgoing Size: 3
   Method Debug Info Offset: 0x218a7
   Method ID Offset: 0xccfc
    */

boolean checkFlag(Context ctx,String flag)

{
  long lVar1;
  boolean bVar2;
  char cVar5;
  int flagLen;
  String flag8;
  boolean bVar3;
  boolean bVar4;
  char cVar6;
  String ref;
  String[] ppSVar7;
  String flag22;
  String flag17_21;
  PackageManager ref_00;
  List ref_01;
  Iterator ref_02;
  Object ref_03;
  String flag12_18;
  int iVar8;
  String flag30;
  Class ref_04;
  Method ref_05;
  String flag19_22;
  String flag30x2;
  PackageInfo pPVar9;
  byte[] pbVar10;
  CertificateFactory ref_06;
  Certificate ref_07;
  MessageDigest ref_08;
  boolean[] boolArray;
  Streamer streamer;
  int idx;
  int i;
  Class[] ppCVar11;
  StringBuilder ref_09;
  Object[] ppOVar12;
  int iVar13;
  Signature ref_10;
  InputStream ref_11;
  String ref_12;
  
  checkCast(ctx,MainActivity);
  Activity.initActivity(ctx);
  FC.ctx = ctx;
  boolArray = new boolean[200];
  streamer = new Streamer();
  FC.lm(FC.m);
  flagLen = flag.length();
  if (flagLen != 69) {
    return false;
  }
  bVar2 = flag.startsWith("MOBISEC{");
  boolArray[0] = bVar2;
  flag8 = flag.substring(8);
  bVar2 = flag8.endsWith("}");
  boolArray[1] = bVar2;
  bVar2 = true;
  streamer.step();
  if (MainActivity.g2 != false) {
    return false;
  }
  streamer.step();
  streamer.step();
  bVar3 = flag8.startsWith("this_is_");
  boolArray[2] = bVar3;
  bVar3 = flag8.endsWith("upos");
  boolArray[3] = bVar3;
  cVar5 = flag8.charAt(10);
  if ((cVar5 == 'c') || (cVar5 = flag8.charAt(13), cVar5 == 'q')) {
    bVar3 = true;
  }
  else {
    bVar3 = false;
  }
  boolArray[4] = bVar3;
  cVar5 = flag8.charAt(3);
  cVar6 = flag8.charAt(7);
  boolArray[5] = (int)cVar5 + (int)cVar6 == 0x72;
  streamer.step();
  bVar3 = flag8.contains("really_");
  boolArray[6] = bVar3;
  bVar3 = false;
  ref_09 = new StringBuilder();
  ref = ctx.getString(0x7f0b0028);
  ref = FC.dec(ref);
  ref_09.append(ref);
  ref_09.append(" ");
  ref = ctx.getString(0x7f0b0029);
  ref = FC.dec(ref);
  ref_09.append(ref);
  ref = ref_09.toString();
  ref = FC.ec(ref);
  ppSVar7 = ref.split("\n");
  iVar13 = 0;
  while (iVar13 < ppSVar7.length) {
    ref_12 = ppSVar7[iVar13];
    ref_09 = new StringBuilder();
    ref = ctx.getString(0x7f0b002a);
    ref = FC.dec(ref);
    ref_09.append(ref);
    ref_09.append(" ");
    ref = ctx.getString(0x7f0b0029);
    ref = FC.dec(ref);
    ref_09.append(ref);
    ref_09.append("/");
    ref_09.append(ref_12);
    ref_09.append("/maps");
    ref = ref_09.toString();
    ref = FC.ec(ref);
    ref_12 = ctx.getString(0x7f0b002b);
    ref_12 = FC.dec(ref_12);
    bVar4 = ref.contains(ref_12);
    if (bVar4 != false) {
      bVar3 = true;
      break;
    }
    iVar13 = iVar13 + 1;
  }
  boolArray[7] = bVar3;
  if (boolArray[7] != false) {
    MainActivity.g2 = true;
    return false;
  }
  streamer.step();
  flag22 = flag8.substring(14);
  bVar3 = flag22.endsWith("_evil");
  boolArray[8] = bVar3;
  flag17_21 = flag8.substring(9,13);
  bVar3 = flag17_21.endsWith("bam");
  boolArray[9] = bVar3;
  streamer.step();
  if (MainActivity.g4 != false) {
    return false;
  }
  streamer.step();
  ref_00 = ctx.getPackageManager();
  ref_01 = ref_00.getInstalledApplications(0x80);
  bVar3 = false;
  ref_02 = ref_01.iterator();
  do {
    bVar4 = ref_02.hasNext();
    if (bVar4 == false) goto LAB_5002514a;
    ref_03 = ref_02.next();
    checkCast(ref_03,ApplicationInfo);
    ref_12 = ref_03.packageName;
    ref = ctx.getString(0x7f0b002c);
    ref = FC.dec(ref);
    bVar4 = ref_12.equals(ref);
  } while (bVar4 == false);
  bVar3 = true;
LAB_5002514a:
  boolArray[10] = bVar3;
  streamer.step();
  iVar13 = 0xc;
  flag12_18 = flag8.substring(4,10);
  ref = flag12_18.toLowerCase();
  bVar3 = ref.equals("incred");
  boolArray[0xb] = bVar3;
  if (MainActivity.g1 != false) {
    return false;
  }
  streamer.step();
  iVar8 = streamer.step();
  if ((iVar8 < 1) && (bVar3 = MainActivity.g1, iVar8 = (int)bVar3, bVar3 != false)) {
    streamer.step();
    streamer.step();
    streamer.step();
    streamer.step();
    streamer.step();
  }
  else {
    flag30 = flag8.substring(22);
    ref = flag30.toUpperCase();
    bVar2 = ref.startsWith("mayb");
    boolArray[iVar8] = bVar2;
    streamer.step();
    if (boolArray[iVar8 + -2] == false) {
      MainActivity.g3 = true;
      return false;
    }
    if (MainActivity.g3 != false) {
      streamer.step();
      return false;
    }
    streamer.step();
    ref = ctx.getString(0x7f0b002e);
    ref = FC.dec(ref);
    ref_04 = Class.forName(ref);
    ref = ctx.getString(0x7f0b002f);
    ref = FC.dec(ref);
    ppCVar11 = new Class[0];
    ref_05 = ref_04.getMethod(ref,ppCVar11);
    ppOVar12 = new Object[0];
    ref_03 = ref_05.invoke(null,ppOVar12);
    checkCast(ref_03,Boolean);
    bVar2 = ref_03.booleanValue();
    boolArray[iVar8 + 1] = bVar2;
    iVar13 = iVar8 + 2;
    if (boolArray[iVar8 + 1] != false) {
      MainActivity.g1 = true;
      return false;
    }
    bVar2 = false;
  }
  if (bVar2) {
    return false;
  }
  ref = flag8.toLowerCase();
  flag19_22 = ref.substring(11,14);
  cVar5 = flag19_22.charAt(1);
  boolArray[iVar13] = cVar5 == '4';
  flag30x2 = flag8.substring(22);
  ref = flag30x2.toUpperCase();
  bVar2 = ref.startsWith("mayb");
  boolArray[iVar13 + 1] = bVar2;
  ref_00 = ctx.getPackageManager();
  ref = ctx.getPackageName();
  pPVar9 = ref_00.getPackageInfo(ref,0x40);
  ref_10 = pPVar9.signatures[0];
  pbVar10 = ref_10.toByteArray();
  ref_11 = new InputStream(pbVar10);
  ref_06 = CertificateFactory.getInstance("X509");
  ref_07 = ref_06.generateCertificate(ref_11);
  checkCast(ref_07,X509Certificate);
  ref_08 = MessageDigest.getInstance("SHA1");
  pbVar10 = ref_07.getEncoded();
  pbVar10 = ref_08.digest(pbVar10);
  ref = FC.th(pbVar10);
  ref_12 = ctx.getString(0x7f0b002d);
  bVar2 = ref.equals(ref_12);
  boolArray[iVar13 + 2] = bVar2;
  if (boolArray[iVar13 + 2] == false) {
    MainActivity.g4 = true;
    return false;
  }
  if ((boolArray[0] != false) && (boolArray[1] != false)) {
    idx = 100;
    i = 0;
    while (i < 30) {
      boolArray[idx] = true;
      ref_09 = new StringBuilder();
      cVar5 = flag8.charAt(i * 2);
      ref = Character.toString(cVar5);
      ref_09.append(ref);
      cVar5 = flag8.charAt(i * 2 + 1);
      ref = Character.toString(cVar5);
      ref_09.append(ref);
      ref = ref_09.toString();
      bVar2 = FC.ip(i);
      if (bVar2 != false) {
        iVar13 = 0;
        while (iVar13 < i) {
          streamer.step();
          iVar13 = iVar13 + 1;
        }
      }
      iVar13 = streamer.g2();
      iVar8 = streamer.g2();
      ref = FC.r(ref);
      lVar1 = FC.sq(ref);
      if (lVar1 != FC.m[iVar13 & 0xff][(iVar8 & 0xff00) >> 8]) {
        boolArray[idx] = false;
      }
      idx = idx + 1;
      i = i + 1;
    }
    iVar13 = idx + -0x1e;
    while( true ) {
      if (idx <= iVar13) {
        ref = FC.h(flag);
        bVar2 = ref.equals("4193d9b72a5c4805e9a5cc739f8a8fc23b2890e13b83bb887d96f86c30654a12");
        if (bVar2 != false) {
          return true;
        }
        return false;
      }
      if (boolArray[iVar13] == false) break;
      iVar13 = iVar13 + 1;
    }
    return false;
  }
  return false;
}

