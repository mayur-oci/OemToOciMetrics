import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Manager {



    public Properties readPropertiesFile(String fileName) throws IOException {
        InputStream fis = null;
        Properties prop = null;
        try {
            prop = new Properties();
            fis = this.getClass().getResourceAsStream(fileName);

            // create Properties class object
            if (fis != null) {
                // load properties file into it
                prop.load(fis);
            } else {
                throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
            }

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        } finally {
            fis.close();
        }

        return prop;
    }

}
