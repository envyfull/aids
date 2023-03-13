/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.chat.BaseComponent
 *  net.md_5.bungee.api.chat.ClickEvent
 *  net.md_5.bungee.api.chat.ClickEvent$Action
 *  net.md_5.bungee.api.chat.HoverEvent
 *  net.md_5.bungee.api.chat.HoverEvent$Action
 *  net.md_5.bungee.api.chat.TextComponent
 */
package br.com.dragonmc.core.common.utils.string;

import java.util.ArrayList;
import java.util.List;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class MessageBuilder {
    private String message;
    private boolean hoverable;
    private HoverEvent hoverEvent;
    private boolean clickable;
    private ClickEvent clickEvent;
    private List<TextComponent> componentList;

    public MessageBuilder(String message) {
        this.message = message;
        this.componentList = new ArrayList<TextComponent>();
    }

    public MessageBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    public MessageBuilder setHoverable(boolean hoverable) {
        this.hoverable = hoverable;
        return this;
    }

    public MessageBuilder setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
        this.hoverable = true;
        return this;
    }

    public MessageBuilder setHoverEvent(HoverEvent.Action action, String text) {
        this.hoverEvent = new HoverEvent(action, TextComponent.fromLegacyText((String)text));
        this.hoverable = true;
        return this;
    }

    public MessageBuilder setHoverEvent(String text) {
        this.hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText((String)text));
        this.hoverable = true;
        return this;
    }

    public MessageBuilder setClickable(boolean clickable) {
        this.clickable = clickable;
        return this;
    }

    public MessageBuilder setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        this.clickable = true;
        return this;
    }

    public MessageBuilder setClickEvent(ClickEvent.Action action, String text) {
        this.clickEvent = new ClickEvent(action, text);
        this.clickable = true;
        return this;
    }

    public MessageBuilder setClickEvent(String text) {
        this.clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, text);
        this.clickable = true;
        return this;
    }

    public MessageBuilder extra(String message) {
        this.componentList.add(new TextComponent(message));
        return this;
    }

    public MessageBuilder extra(TextComponent textComponent) {
        this.componentList.add(textComponent);
        return this;
    }

    public MessageBuilder extra(List<TextComponent> extra) {
        this.componentList.addAll(extra);
        return this;
    }

    public TextComponent create() {
        TextComponent textComponent = new TextComponent(this.message);
        if (this.hoverable) {
            textComponent.setHoverEvent(this.hoverEvent);
        }
        if (this.clickable) {
            textComponent.setClickEvent(this.clickEvent);
        }
        for (TextComponent text : this.componentList) {
            textComponent.addExtra((BaseComponent)text);
        }
        return textComponent;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isHoverable() {
        return this.hoverable;
    }

    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }

    public boolean isClickable() {
        return this.clickable;
    }

    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }

    public List<TextComponent> getComponentList() {
        return this.componentList;
    }
}

