package org.hypergraphdb.viewer.util;

import java.util.Arrays;

public class PrimeFinder
{

 protected PrimeFinder()
 {
 }

 protected static void main(String args[])
 {
     int i = Integer.parseInt(args[0]);
     int j = Integer.parseInt(args[1]);
     statistics(i, j);
 }

 public static int nextPrime(int i)
 {
     int j = Arrays.binarySearch(primeCapacities, i);
     if(j < 0)
         j = -j - 1;
     return primeCapacities[j];
 }

 protected static void statistics(int i, int j)
 {
     for(int k = 0; k < primeCapacities.length - 1; k++)
         if(primeCapacities[k] >= primeCapacities[k + 1])
             throw new RuntimeException("primes are unsorted or contain duplicates; detected at " + k + "@" + primeCapacities[k]);

     double d = 0.0D;
     double d1 = -1D;
     for(int l = i; l <= j; l++)
     {
         int i1 = nextPrime(l);
         double d2 = (double)(i1 - l) / (double)l;
         if(d2 > d1)
         {
             d1 = d2;
             System.out.println("new maxdev @" + l + "@dev=" + d1);
         }
         d += d2;
     }

     long l1 = (1L + (long)j) - (long)i;
     double d3 = d / (double)l1;
     System.out.println("Statistics for [" + i + "," + j + "] are as follows");
     System.out.println("meanDeviation = " + (float)d3 * 100F + " %");
     System.out.println("maxDeviation = " + (float)d1 * 100F + " %");
 }

 public static final int largestPrime = 0x7fffffff;
 private static final int primeCapacities[] = {
     0x7fffffff, 5, 11, 23, 47, 97, 197, 397, 797, 1597, 
     3203, 6421, 12853, 25717, 51437, 0x191dd, 0x323bf, 0x64787, 0xc8f4d, 0x191e9d, 
     0x323d49, 0x647a97, 0xc8f539, 0x191ea81, 0x323d521, 0x647aa43, 0xc8f5489, 0x191ea927, 0x323d525b, 0x647aa4bf, 
     433, 877, 1759, 3527, 7057, 14143, 28289, 56591, 0x1ba25, 0x3744b, 
     0x6e897, 0xdd14f, 0x1ba2a3, 0x37454b, 0x6e8a99, 0xdd1563, 0x1ba2ac7, 0x374559b, 0x6e8ab8b, 0xdd1572b, 
     0x1ba2ae79, 0x37455d1b, 0x6e8aba45, 953, 1907, 3821, 7643, 15287, 30577, 61169, 
     0x1ddeb, 0x3bbdf, 0x777bf, 0xeef85, 0x1ddf13, 0x3bbe4d, 0x777cad, 0xeef96f, 0x1ddf2f3, 0x3bbe5ed, 
     0x777cbdb, 0xeef97cb, 0x1ddf2f9b, 0x3bbe5f3b, 0x777cbe79, 1039, 2081, 4177, 8363, 16729, 
     33461, 0x1056b, 0x20add, 0x415c1, 0x82bb9, 0x105785, 0x20af19, 0x415e3b, 0x82bc79, 0x10578f7, 
     0x20af203, 0x415e415, 0x82bc82d, 0x1057909f, 0x20af2147, 0x415e428f, 31, 67, 137, 277, 
     557, 1117, 2237, 4481, 8963, 17929, 35863, 0x1183d, 0x2307b, 0x460fd, 
     0x8c201, 0x118411, 0x230833, 0x461069, 0x8c20e1, 0x11841cb, 0x2308397, 0x461075b, 0x8c20ecb, 0x11841da5, 
     0x23083b61, 0x461076c7, 599, 1201, 2411, 4831, 9677, 19373, 38747, 0x12ec5, 
     0x25d93, 0x4bb41, 0x9768b, 0x12ed29, 0x25da59, 0x4bb4b3, 0x976975, 0x12ed2ef, 0x25da5ef, 0x4bb4bed, 
     0x97697dd, 0x12ed2fbd, 0x25da5f7b, 0x4bb4bf6b, 311, 631, 1277, 2557, 5119, 10243, 
     20507, 41017, 0x14075, 0x280f9, 0x50215, 0xa042d, 0x140863, 0x2810e1, 0x5021c9, 0xa04395, 
     0x1408739, 0x2810e79, 0x5021d05, 0xa043a0b, 0x14087417, 0x2810e841, 0x5021d089, 3, 7, 17, 
     37, 79, 163, 331, 673, 1361, 2729, 5471, 10949, 21911, 
     43853, 0x156a7, 0x2ad57, 0x55ab3, 0xab57b, 0x156af9, 0x2ad607, 0x55ac41, 0xab5893, 0x156b12f, 
     0x2ad6285, 0x55ac519, 0xab58a35, 0x156b14a3, 0x2ad62961, 0x55ac52c5, 43, 89, 179, 359, 
     719, 1439, 2879, 5779, 11579, 23159, 46327, 0x169f1, 0x2d3eb, 0x5a7e5, 
     0xb4fd9, 0x169fd3, 0x2d3fad, 0x5a7f87, 0xb4ff1f, 0x169fe4d, 0x2d3fca1, 0x5a7f95b, 0xb4ff2b9, 0x169fe58d, 
     0x2d3fcb1b, 0x5a7f9637, 379, 761, 1523, 3049, 6101, 12203, 24407, 48817, 
     0x17d71, 0x2faef, 0x5f5f7, 0xbebf5, 0x17d7f3, 0x2fb009, 0x5f6029, 0xbec0b1, 0x17d8195, 0x2fb0337, 
     0x5f60687, 0xbec0d15, 0x17d81a33, 0x2fb03481, 0x5f606903
 };

 static 
 {
     Arrays.sort(primeCapacities);
 }
}