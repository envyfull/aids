/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package br.com.dragonmc.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.dragonmc.core.common.backend.Credentials;
import br.com.dragonmc.core.common.command.CommandSender;
import br.com.dragonmc.core.common.language.Language;
import br.com.dragonmc.core.common.medal.Medal;
import br.com.dragonmc.core.common.permission.Group;
import br.com.dragonmc.core.common.permission.Tag;
import net.md_5.bungee.api.ChatColor;

public class PluginInfo {
    private String website;
    private String discord;
    private String store;
    private String ip;
    private Language defaultLanguage;
    private boolean piratePlayersEnabled;
    private boolean redisDebugEnabled;
    private boolean debug;
    private Credentials mongoCredentials;
    private Credentials redisCredentials;
    private LinkedHashMap<String, Group> groupMap;
    private LinkedHashMap<String, Tag> tagMap;
    private Map<Language, Map<String, String>> languageMap;
    private Map<String, Medal> medalMap;
    private transient Tag defaultTag;
    private transient Group defaultGroup;

    public String getWebsiteUrl() {
        return "https://" + this.getWebsite() + "/";
    }

    public void loadMedal(Medal medal) {
        this.medalMap.put(medal.getMedalName().toLowerCase(), medal);
        CommonPlugin.getInstance().saveConfig("medalMap");
    }

    public void loadGroup(Group group) {
        this.groupMap.put(group.getGroupName().toLowerCase(), group);
        CommonPlugin.getInstance().saveConfig("groupMap");
    }

    public void loadTag(Tag tag) {
        this.tagMap.put(tag.getTagName().toLowerCase(), tag);
        CommonPlugin.getInstance().saveConfig("tagMap");
    }

    public Tag getTagById(int id) {
        return this.tagMap.values().stream().filter(tag -> tag.getTagId() == id).findFirst().orElse(null);
    }

    public Group filterGroup(Predicate<? super Group> filter) {
        return this.groupMap.values().stream().filter(filter).findFirst().orElse(null);
    }

    public Group filterGroup(Predicate<? super Group> filter, Group orElse) {
        return this.groupMap.values().stream().filter(filter).findFirst().orElse(orElse);
    }

    public Group getFirstLowerGroup(int id) {
        return this.groupMap.values().stream().filter(group -> group.getId() < id).findFirst().orElse(null);
    }

    public Group getGroupById(int id) {
        return this.groupMap.values().stream().filter(group -> group.getId() == id).findFirst().orElse(null);
    }

    public Group getGroupByName(String string) {
        Group group = this.groupMap.get(string.toLowerCase());
        if (group == null) {
            group = this.groupMap.values().stream().filter(g -> g.getGroupName().equalsIgnoreCase(string)).findFirst().orElse(null);
        }
        return group;
    }

    public String translate(Language language, String key, String ... replaces) {
        Map map = this.languageMap.computeIfAbsent(language, v -> new HashMap());
        String translate = "[NOT FOUND: " + key + "]";
        if (map.containsKey(key)) {
            translate = this.languageMap.get((Object)language).get(key);
            if (replaces.length > 0 && replaces.length % 2 == 0) {
                for (int i = 0; i < replaces.length; i += 2) {
                    translate = translate.replace(replaces[i], replaces[i + 1]);
                }
            }
        } else {
            if (this.defaultLanguage == language) {
                map.put(key, translate);
            } else {
                translate = this.translate(this.defaultLanguage, key, replaces) + " \u00a70\u00a7l*";
                map.put(key, translate);
            }
            CommonPlugin.getInstance().saveConfig("languageMap");
        }
        return ChatColor.translateAlternateColorCodes((char)'&', (String)translate);
    }

    public String findAndTranslate(Language lang, String string) {
        if (string != null && !string.isEmpty()) {
            Matcher matcher = CommonConst.TRANSLATE_PATTERN.matcher(string);
            while (matcher.find()) {
                String replace = matcher.group();
                String id = matcher.group(2).toLowerCase();
                string = string.replace(replace, this.translate(lang, id, new String[0]));
            }
        }
        return string;
    }

    public String translate(String key) {
        return this.translate(this.getDefaultLanguage(), key, new String[0]);
    }

    public void addTranslate(Language language, String translateKey, String translate) {
        this.languageMap.computeIfAbsent(language, v -> new HashMap()).put(translateKey, translate);
        CommonPlugin.getInstance().saveConfig("languageMap");
    }

    public static String t(CommandSender sender, String translate) {
        return sender.getLanguage().t(translate, new String[0]);
    }

    public static String t(CommandSender sender, String translate, String ... replaces) {
        return sender.getLanguage().t(translate, replaces);
    }

    public void sort() {
        for (Language language : this.languageMap.keySet()) {
            Map<String, String> map = this.languageMap.get((Object)language);
            List<Map.Entry<String, String>> list = map.entrySet().stream().sorted((o1, o2) -> ((String)o1.getKey()).compareTo((String)o2.getKey())).collect(Collectors.toList());
            map.clear();
            for (Map.Entry entry : list) {
                map.put((String)entry.getKey(), (String)entry.getValue());
            }
        }
        this.sortGroup();
        this.sortTag();
    }

    public Group getHighGroup() {
        return this.getSortGroup().findFirst().orElse(null);
    }

    public Stream<Group> getSortGroup() {
        return this.groupMap.values().stream().sorted((o1, o2) -> o2.getId() - o1.getId());
    }

    public Collection<Tag> getTags() {
        return this.tagMap.values();
    }

    public Tag getTagByGroup(Group serverGroup) {
        Tag tag = this.tagMap.get(serverGroup.getGroupName().toLowerCase());
        return tag == null ? this.getDefaultTag() : tag;
    }

    public Tag getTagByName(String string) {
        return this.tagMap.containsKey(string.toLowerCase()) ? this.tagMap.get(string.toLowerCase()) : (Tag)this.tagMap.values().stream().filter(t -> t.getTagName().equalsIgnoreCase(string) || t.getAliases().contains(string.toLowerCase())).findFirst().orElse(null);
    }

    public Tag getDefaultTag() {
        return this.defaultTag == null ? (this.defaultTag = this.tagMap.values().stream().filter(tag -> tag.isDefaultTag()).findFirst().orElse(new Tag(20, "membro", "\u00a77", new ArrayList<String>(), false, true))) : this.defaultTag;
    }

    public Group getDefaultGroup() {
        return this.defaultGroup == null ? (this.defaultGroup = (Group)this.groupMap.values().stream().filter(tag -> tag.isDefaultGroup()).findFirst().orElse(null)) : this.defaultGroup;
    }

    public Medal getMedalByName(String medalName) {
        return this.medalMap.containsKey(medalName.toLowerCase()) ? this.medalMap.get(medalName.toLowerCase()) : (Medal)this.medalMap.values().stream().filter(medal -> medal.getMedalName().equalsIgnoreCase(medalName) || medal.getAliases().contains(medalName.toLowerCase())).findFirst().orElse(null);
    }

    public void sortGroup() {
        List<Map.Entry<String, Group>> list = (List<Map.Entry<String, Group>>)this.groupMap.entrySet().stream().sorted((o1, o2) -> ((Group)o2.getValue()).getId() - ((Group)o1.getValue()).getId()).collect(Collectors.toList());        this.groupMap.clear();
        for (Map.Entry<String, Group> entry : list) {
            this.groupMap.put((String)entry.getKey(), (Group)entry.getValue());
        }
    }

    public void sortTag() {
        List<Map.Entry<String, Tag>> list = this.tagMap.entrySet().stream().sorted((o1, o2) -> ((Tag)o1.getValue()).getTagId() - ((Tag)o2.getValue()).getTagId()).collect(Collectors.toList());
        this.tagMap.clear();
        for (Map.Entry entry : list) {
            this.tagMap.put((String)entry.getKey(), (Tag)entry.getValue());
        }
    }

    public String getWebsite() {
        return this.website;
    }

    public String getDiscord() {
        return this.discord;
    }

    public String getStore() {
        return this.store;
    }

    public String getIp() {
        return this.ip;
    }

    public Language getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public boolean isPiratePlayersEnabled() {
        return this.piratePlayersEnabled;
    }

    public boolean isRedisDebugEnabled() {
        return this.redisDebugEnabled;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public Credentials getMongoCredentials() {
        return this.mongoCredentials;
    }

    public Credentials getRedisCredentials() {
        return this.redisCredentials;
    }

    public LinkedHashMap<String, Group> getGroupMap() {
        return this.groupMap;
    }

    public LinkedHashMap<String, Tag> getTagMap() {
        return this.tagMap;
    }

    public Map<Language, Map<String, String>> getLanguageMap() {
        return this.languageMap;
    }

    public Map<String, Medal> getMedalMap() {
        return this.medalMap;
    }

    public void setDefaultLanguage(Language defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}

