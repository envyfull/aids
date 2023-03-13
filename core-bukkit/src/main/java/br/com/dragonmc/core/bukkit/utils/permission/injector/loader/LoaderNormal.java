/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheLoader
 */
package br.com.dragonmc.core.bukkit.utils.permission.injector.loader;

import com.google.common.cache.CacheLoader;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class LoaderNormal
extends CacheLoader<String, Pattern> {
    public static final String RAW_REGEX_CHAR = "$";

    public Pattern load(String arg0) throws Exception {
        return LoaderNormal.createPattern(arg0);
    }

    protected static Pattern createPattern(String expression) {
        try {
            return Pattern.compile(LoaderNormal.prepareRegexp(expression), 2);
        }
        catch (PatternSyntaxException e) {
            return Pattern.compile(Pattern.quote(expression), 2);
        }
    }

    public static String prepareRegexp(String expression) {
        boolean rawRegexp;
        if (expression.startsWith("-")) {
            expression = expression.substring(1);
        }
        if (expression.startsWith("#")) {
            expression = expression.substring(1);
        }
        if (rawRegexp = expression.startsWith(RAW_REGEX_CHAR)) {
            expression = expression.substring(1);
        }
        String regexp = rawRegexp ? expression : expression.replace(".", "\\.").replace("*", "(.*)");
        return regexp;
    }
}

