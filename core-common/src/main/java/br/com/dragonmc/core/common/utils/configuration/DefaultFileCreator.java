/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.utils.configuration;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import br.com.dragonmc.core.common.utils.FileCreator;

public class DefaultFileCreator
implements FileCreator {
    @Override
    public File createFile(String fileName, String path) throws Exception {
        File file = new File(path + File.separatorChar + fileName);
        if (!file.exists()) {
            URL url = this.getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                return null;
            }
            file.createNewFile();
            URI uri = url.toURI();
            HashMap<String, String> env = new HashMap<String, String>();
            env.put("create", "true");
            FileSystem zipfs = FileSystems.newFileSystem(uri, env);
            Path resourceFile = Paths.get(uri);
            Files.copy(resourceFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            zipfs.close();
        }
        return file;
    }
}

