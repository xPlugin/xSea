package pr.lofe.mdr.xsea.loader;

import pr.lofe.mdr.xsea.xSea;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ChangesDetect {

    public static void onDisable() {
        try (JarFile jf = new JarFile(new File("E:/Новая папка (3)/plugins/xSea-1.0-SNAPSHOT.jar"))) {
            for (Enumeration<JarEntry> en = jf.entries(); en.hasMoreElements(); ) {
                JarEntry e = en.nextElement();
                String name = e.getName();
                // Check for package or sub-package (you can change the test for *exact* package here)
                if (name.startsWith("pr/lofe/mdr/xsea") && name.endsWith(".class")) {
                    // Strip out ".class" and reformat path to package name
                    String javaName = name.substring(0, name.lastIndexOf('.')).replace('/', '.');
                    System.out.print("Checking "+javaName+" ... ");
                    Class<?> cls;
                    try {
                        cls = Class.forName(javaName);
                    } catch (ClassNotFoundException ex)  { // E.g. internal classes, ...
                        continue;
                    }
                    if ((cls.getModifiers() & Modifier.ABSTRACT) != 0) { // Only instanciable classes
                        System.out.println("(abstract)");
                        continue;
                    }
                    // Found!
                    System.out.println(javaName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
