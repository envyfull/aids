/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassGetter {
    public static ArrayList<Class<?>> getClassesForPackage(Class<?> clas, String pkgname) {
        ArrayList classes = new ArrayList();
        CodeSource src = clas.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL resource = src.getLocation();
            resource.getPath();
            ClassGetter.processJarfile(resource, pkgname, classes);
        }
        return classes;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
        catch (NoClassDefFoundError e) {
            return null;
        }
    }

    private static void processJarfile(URL resource, String pkgname, ArrayList<Class<?>> classes) {
        JarFile jarFile;
        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        try {
            jarFile = new JarFile(jarPath);
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            Class<?> c;
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;
            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length()) {
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            }
            if (className == null || (c = ClassGetter.loadClass(className)) == null) continue;
            classes.add(c);
        }
        try {
            jarFile.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Class<?>> getClassesForPackageByFile(File file, String pkgname) {
        ArrayList classes = new ArrayList();
        try {
            String relPath = pkgname.replace('.', '/');
            try (JarFile jarFile = new JarFile(file);){
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    Class<?> c;
                    JarEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    if (!entryName.endsWith(".class") || !entryName.startsWith(relPath) || entryName.length() <= relPath.length() + "/".length()) continue;
                    String className = entryName.replace('/', '.').replace('\\', '.');
                    if (className.endsWith(".class")) {
                        className = className.substring(0, className.length() - 6);
                    }
                    if ((c = ClassGetter.loadClass(className)) == null) continue;
                    classes.add(c);
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + file.getAbsolutePath() + "'", e);
        }
        return classes;
    }

    public static List<Class<?>> getClassesForPackageByPlugin(Object plugin, String pkgname) {
        try {
            Method method = plugin.getClass().getMethod("getFile", new Class[0]);
            method.setAccessible(true);
            File file = (File)method.invoke(plugin, new Object[0]);
            return ClassGetter.getClassesForPackageByFile(file, pkgname);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList();
        }
    }
}

