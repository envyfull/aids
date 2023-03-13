/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 */
package br.com.dragonmc.core.common.permission;

import java.util.List;

import br.com.dragonmc.core.common.utils.string.StringFormat;
import net.md_5.bungee.api.ChatColor;

public class Tag {
    private int tagId;
    private String tagName;
    private String tagPrefix;
    private List<String> aliases;
    private boolean exclusive;
    private boolean defaultTag;

    public String getRealPrefix() {
        return this.tagPrefix + (ChatColor.stripColor((String)this.tagPrefix).trim().length() > 0 ? " " : "");
    }

    public String getStrippedColor() {
        if (this.tagPrefix.length() > 2) {
            return this.tagPrefix;
        }
        return this.tagPrefix + StringFormat.formatString(this.tagName) + "";
    }

    public String getColor() {
        return this.tagPrefix.length() > 2 ? this.tagPrefix.substring(0, 2) : this.tagPrefix;
    }

    public Tag(int tagId, String tagName, String tagPrefix, List<String> aliases, boolean exclusive, boolean defaultTag) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagPrefix = tagPrefix;
        this.aliases = aliases;
        this.exclusive = exclusive;
        this.defaultTag = defaultTag;
    }

    public int getTagId() {
        return this.tagId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public String getTagPrefix() {
        return this.tagPrefix;
    }

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean isExclusive() {
        return this.exclusive;
    }

    public boolean isDefaultTag() {
        return this.defaultTag;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public void setTagPrefix(String tagPrefix) {
        this.tagPrefix = tagPrefix;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public void setDefaultTag(boolean defaultTag) {
        this.defaultTag = defaultTag;
    }
}

