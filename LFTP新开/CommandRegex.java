import java.util.Scanner;
import java.util.regex.Pattern;

class CommandRegex{
    static CommandAnalysis ca;
    String inputString;
    public static void main(String [] args) {
        String test1 = "LFTP lsend 172.18.34.217 2018.txt";
        String test2 = "LFTP lget 172.18.34.217 2018.mp4";
        System.out.println(isDownLoadCommandCorret(test1) || isUpLoadCommandCorret(test1));
    }

    public CommandRegex(String inputString){
        this.inputString = inputString;
        ca = new CommandAnalysis(inputString);
    }

    static boolean isUpLoadCommandCorret(String input){
        String upLoadPattern = "LFTP lsend " + "(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))" + " .*";
        ca = new CommandAnalysis(input);
        if(!ca.getIsCorrect()){
            return false;
        }
        //System.out.println("Up");
        return Pattern.matches(upLoadPattern,input);
    }

    static boolean isDownLoadCommandCorret(String input){
        String downLoadPattern = "LFTP lget " + "(?=(\\b|\\D))(((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))\\.){3}((\\d{1,2})|(1\\d{1,2})|(2[0-4]\\d)|(25[0-5]))(?=(\\b|\\D))" + " .*";
        ca = new CommandAnalysis(input);
        if(!ca.getIsCorrect()){
            return false;
        }
        //System.out.println("Down");
        return Pattern.matches(downLoadPattern,input);
    }

    public String getIpAddress(){
        return ca.getIPAddress();
    }

    public String getFilePath(){
        return  ca.getFilePath();
    }

    public String getUpOrDownLoad(){
        return ca.getUpOrDownLoad();
    }

    public boolean getIsValid() {
        return isUpLoadCommandCorret(inputString) || isDownLoadCommandCorret(inputString);
    }
}