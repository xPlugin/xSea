package pr.lofe.mdr.xsea.util;

import net.kyori.adventure.text.Component;
import pr.lofe.lib.xbase.text.TextWrapper;

public class Message {

    public static Component gen(String name, String msg) {
        return TextWrapper.text(String.format(
                "<color:#b0dbff>[%s]</color> <white>%s</white>",
                name,
                msg
        ));
    }

}
