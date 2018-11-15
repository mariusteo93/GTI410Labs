public class Main {

    public static void main(String[] args) {

        String result = solution1(s, k);
        String result2 = solution2(s, k);

        String array []= new String []{"-1","0","1","2","3"};
        int d =2;
    }

    public static int [] solution1(int d, String array[]) {
        int result []=new int[array.length];
     for (int i =0 ; i< array.length;i++){
         int valueAt = Integer.parseInt(array[i]);
         int ancestor = (int) valueAt - d;
         if (ancestor < -1){
             result[i]=-1;
         }else{
            for (int j =0 ; j<array.length;i++){
                array[i]
            }

         }


     }



    }
    public static String solution2(String s, int K) {


    }
}
