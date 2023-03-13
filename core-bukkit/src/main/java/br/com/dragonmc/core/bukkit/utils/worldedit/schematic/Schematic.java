/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.bukkit.utils.worldedit.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Schematic {
    private static Schematic instance = new Schematic();
    private short[] blocks;
    private byte[] data;
    private short width;
    private short lenght;
    private short height;

    private Schematic(short[] blocks, byte[] data, short width, short lenght, short height) {
        this.blocks = blocks;
        this.data = data;
        this.width = width;
        this.lenght = lenght;
        this.height = height;
    }

    public Schematic loadSchematic(File file) throws IOException, DataException {
        FileInputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(stream);
        CompoundTag schematicTag = (CompoundTag)nbtStream.readTag();
        nbtStream.close();
        if (!schematicTag.getName().equalsIgnoreCase("Schematic")) {
            throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
        }
        Map<String, Tag> schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
        }
        short width = ((ShortTag)this.getChildTag((Map<String, Tag>)schematic, "Width", ShortTag.class)).getValue();
        short length = ((ShortTag)this.getChildTag((Map<String, Tag>)schematic, "Length", ShortTag.class)).getValue();
        short height = ((ShortTag)this.getChildTag((Map<String, Tag>)schematic, "Height", ShortTag.class)).getValue();
        byte[] blockId = ((ByteArrayTag)this.getChildTag((Map<String, Tag>)schematic, "Blocks", ByteArrayTag.class)).getValue();
        byte[] blockData = ((ByteArrayTag)this.getChildTag((Map<String, Tag>)schematic, "Data", ByteArrayTag.class)).getValue();
        byte[] addId = new byte[]{};
        short[] blocks = new short[blockId.length];
        if (schematic.containsKey("AddBlocks")) {
            addId = ((ByteArrayTag)this.getChildTag((Map<String, Tag>)schematic, "AddBlocks", ByteArrayTag.class)).getValue();
        }
        for (int index = 0; index < blockId.length; ++index) {
            blocks[index] = index >> 1 >= addId.length ? (short)(blockId[index] & 0xFF) : ((index & 1) == 0 ? (short)(((addId[index >> 1] & 0xF) << 8) + (blockId[index] & 0xFF)) : (short)(((addId[index >> 1] & 0xF0) << 4) + (blockId[index] & 0xFF)));
        }
        return new Schematic(blocks, blockData, width, length, height);
    }

    private <T extends Tag> Tag getChildTag(Map<String, Tag> items, String key, Class<T> expected) throws DataException {
        if (!items.containsKey(key)) {
            throw new DataException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new DataException(key + " tag is not of tag type " + expected.getName());
        }
        return (Tag)expected.cast(tag);
    }

    public static Schematic getInstance() {
        return instance;
    }

    public short[] getBlocks() {
        return this.blocks;
    }

    public byte[] getData() {
        return this.data;
    }

    public short getWidth() {
        return this.width;
    }

    public short getLenght() {
        return this.lenght;
    }

    public short getHeight() {
        return this.height;
    }

    public Schematic() {
    }
}

