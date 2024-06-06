package pr.lofe.mdr.xsea.util;

public class StringUtils {

    private final static String SYMBOLS = "0123456789.-";

    public static boolean isNumeric(String string) {
        boolean state = true;
        for(char s : string.toCharArray()) {
            if(SYMBOLS.indexOf(s) == -1) {
                state = false;
                break;
            }
        }
        return state;
    }


}
