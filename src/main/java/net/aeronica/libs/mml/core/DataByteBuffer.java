package net.aeronica.libs.mml.core;

/**
 */
public class DataByteBuffer
{
    public byte[] data = null;
    public int length = 0;

    public DataByteBuffer()
    {
        /* NOP */
    }

    public DataByteBuffer(byte[] data)
    {
        this.data = data;
    }

    public DataByteBuffer(int capacity)
    {
        this.data = new byte[capacity];
    }
}
