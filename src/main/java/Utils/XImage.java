package Utils;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.ImageIcon;

public class XImage {
    public static Image getAppIcon(){
        URL url = XImage.class.getResource("/Icon/logo.png");
        return new ImageIcon(url).getImage();
    }
    
    public static boolean save(File src){
        File dst = new File("src\\main\\resources\\IconTest", src.getName());
        if(!dst.getParentFile().exists()){
            dst.getParentFile().mkdirs(); // tao thu muc
        }
        try{
            Path from = Paths.get(src.getAbsolutePath());
            Path to = Paths.get(dst.getAbsolutePath());
            Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public static ImageIcon read(String fileName){
        File path = new File("src\\main\\resources\\IconTest", fileName);
        return new ImageIcon(path.getAbsolutePath());
    }
}
