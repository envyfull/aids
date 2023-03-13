/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package br.com.dragonmc.core.common.utils.string;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;

public class Line {
    private List<String> lines = new ArrayList<String>();

    public Line line(String line) {
        this.lines.add(line.isEmpty() ? "\u00a7f" : line);
        return this;
    }

    public String toString() {
        return Joiner.on((char)'\n').join(this.lines);
    }

    public static Line create() {
        return new Line();
    }
}

