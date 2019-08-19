package net.aeronica.libs.mml.parser;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;

public class MMLNavigator
{
    private DataCharBuffer buffer     = null;
    private IndexBuffer elementBuffer = null;
    private int         elementIndex  = 0;

    public MMLNavigator(DataCharBuffer buffer, IndexBuffer elementBuffer)
    {
        this.buffer = buffer;
        this.elementBuffer = elementBuffer;
    }

    // IndexBuffer (elementBuffer) navigation support methods

    public boolean hasNext()
    {
        return this.elementIndex < this.elementBuffer.count - 1;
    }

    public void next()
    {
        this.elementIndex++;
    }

    public void previous()
    {
        this.elementIndex--;
    }

    // Parser element location methods

    public int position()
    {
        return this.elementBuffer.position[this.elementIndex];
    }

    public int length()
    {
        return this.elementBuffer.length[this.elementIndex];
    }

    public byte type()
    {
        return this.elementBuffer.type[this.elementIndex];
    }

    // Data extraction support methods

    /**
     * Primitive integer bounded to 5 significant digits. -1 as invalid data.
     * @return -1 for invalid, 0<->99999
     */
    public int asInt()
    {
        byte numberType = this.elementBuffer.type[this.elementIndex];
        switch (numberType)
        {
            case ElementTypes.MML_NUMBER :
            {
                String number = new String(this.buffer.data, this.elementBuffer.position[this.elementIndex], this.elementBuffer.length[this.elementIndex]);
                int length = number.length();
                try {
                    if (length >= 1 && length <= 5)
                        return Integer.valueOf(number);
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }
}
