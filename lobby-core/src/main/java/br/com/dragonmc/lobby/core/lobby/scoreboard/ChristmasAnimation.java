/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.lobby.core.lobby.scoreboard;

public class ChristmasAnimation
implements ScoreboardAnimation {
    private String text;
    private int frame;
    private int frameLimit;

    public ChristmasAnimation(String text) {
        this.text = text.replace("MC", "");
        this.frameLimit = 1;
    }

    @Override
    public String next() {
        int n = this.frame = this.frame == this.frameLimit ? 0 : this.frame + 1;
        if (this.frame == 0) {
            return "\u00a7f\u00a7l" + this.text;
        }
        return "\u00a7c\u00a7l" + this.text;
    }

    public String getText() {
        return this.text;
    }

    public int getFrame() {
        return this.frame;
    }

    public int getFrameLimit() {
        return this.frameLimit;
    }
}

