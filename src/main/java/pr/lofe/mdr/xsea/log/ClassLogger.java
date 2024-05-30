package pr.lofe.mdr.xsea.log;

import java.util.logging.Logger;

public class ClassLogger {

    public static Logger get(Class clazz) {
        return Logger.getLogger(clazz.getName());
    }

}
