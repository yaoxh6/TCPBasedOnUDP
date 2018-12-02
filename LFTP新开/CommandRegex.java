import java.util.Scanner;
import java.util.regex.Pattern;

class CommandRegex{
    public static void main(String [] args) {

        String test1 = "LFTP lsend 172.168.18.20 mylargefile";
        System.out.println(isUpLoadCommandCorret(test1));
        String test2 = "LFTP lget 255.255.255.255 mylargefile";
        System.out.println(isDownLoadCommandCorret(test2));
    }
    static boolean isUpLoadCommandCorret(String input){
        String upLoadPattern = "LFTP lsend " + "(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))" + " mylargefile";
        return Pattern.matches(upLoadPattern,input);
    }

    static boolean isDownLoadCommandCorret(String input){
        String downLoadPattern = "LFTP lget " + "(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))" + " mylargefile";
        return Pattern.matches(downLoadPattern,input);
    }
}