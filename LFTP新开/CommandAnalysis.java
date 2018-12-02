import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.io.File;

public class CommandAnalysis {
    String UpOrDownLoad;
    String IPAddress;
    String FilePath;
    String inputString;
    boolean isCorrect;
    public void main(String [] args) {

    }

    public CommandAnalysis(String inputString){
        this.inputString = inputString;
        isCorrect = true;
        CommandSplit();
    }
    public void CommandSplit(){
        String[] temp = inputString.split("\\s");
        if(temp.length != 4){
            System.out.println("Length");
            isCorrect = false;
            return;
        }
        UpOrDownLoad = temp[1];
        IPAddress = temp[2];
        FilePath = temp[3];
        File file = new File(FilePath);
        if(!file.exists()){
            System.out.println("File");
            isCorrect = false;
        }
    }

    public String getFilePath() {
        return FilePath;
    }

    public String getIPAddress(){
        return IPAddress;
    }

    public boolean getIsCorrect(){
        return isCorrect;
    }

    public String getUpOrDownLoad(){
        return UpOrDownLoad;
    }
}
