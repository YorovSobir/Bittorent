package ru.spbau.mit.bittorrent.client.message;

public class BitField implements Message {
    private byte[] bitField;

    public static BitField getEmpty(int length) {
        return new BitField(new byte[length]);
    }

    public BitField(byte[] bitField) {
        this.bitField = bitField;
    }

    @Override
    public int getMessageLength() {
        return 1 + bitField.length;
    }

    @Override
    public byte getMessageId() {
        return 5;
    }

    public byte[] getBitField() {
        return bitField;
    }

    public void setBitField(byte[] bitField) {
        this.bitField = bitField;
    }
}
