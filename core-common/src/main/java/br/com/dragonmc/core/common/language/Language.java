/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.language;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import br.com.dragonmc.core.common.CommonPlugin;
import br.com.dragonmc.core.common.member.Member;

public enum Language {
    PORTUGUESE("Portugu\u00eas", "pt-br", "portugues", "br", "brasileiro"),
    ENGLISH("English", "en-us", "ingl\u00eas", "ingles", "england", "us", "eua");

    private String languageName;
    private String languageCode;
    private String[] languageAliases;
    private static final Map<String, Language> LANGUAGE_MAP;

    private Language(String languageName, String languageCode, String ... languageAliases) {
        this.languageName = languageName;
        this.languageCode = languageCode;
        this.languageAliases = languageAliases;
    }

    public String t(String key, String ... replaces) {
        return CommonPlugin.getInstance().getPluginInfo().translate(this, key, replaces);
    }

    public static Language getLanguageByName(String languageName) {
        return LANGUAGE_MAP.get(languageName.toLowerCase());
    }

    public static Language getLanguage(UUID uniqueId) {
        Member member = CommonPlugin.getInstance().getMemberManager().getMember(uniqueId);
        return member == null ? CommonPlugin.getInstance().getPluginInfo().getDefaultLanguage() : member.getLanguage();
    }

    public Locale getLocale() {
        String[] split = this.getLanguageCode().split("-");
        return new Locale(split[0].toLowerCase(), split[1].toUpperCase());
    }

    public String getLanguageName() {
        return this.languageName;
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    public String[] getLanguageAliases() {
        return this.languageAliases;
    }

    static {
        LANGUAGE_MAP = new HashMap<String, Language>();
        for (Language language : Language.values()) {
            LANGUAGE_MAP.put(language.name().toLowerCase(), language);
            LANGUAGE_MAP.put(language.getLanguageName().toLowerCase(), language);
            LANGUAGE_MAP.put(language.getLanguageCode().toLowerCase(), language);
            for (String alias : language.getLanguageAliases()) {
                LANGUAGE_MAP.put(alias.toLowerCase(), language);
            }
        }
    }
}

