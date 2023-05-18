import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class PasswordVerify {
    String password;

    public PasswordVerify(File passwordFile) throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader(passwordFile));
        this.password = fileReader.readLine().substring(9);
    }

    public boolean verify(String password) {
        return this.password.equals(password);
    }
}
