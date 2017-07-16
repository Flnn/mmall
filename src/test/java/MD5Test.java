import com.mmall.util.MD5Util;

/**
 * Created by flnnf on 2017/7/2.
 */
public class MD5Test {
    public static void main(String[] args) {
        String admin = "flnn";
        System.out.println(MD5Util.MD5EncodeUtf8(admin));
    }
}
