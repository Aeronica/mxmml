package net.aeronica.libs.mml.parser;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;

public class MMLParser2
{
    private static final byte NOP = 0;

    private byte[] stateStack   = new byte[256];
    private int    stateIndex   = 0;
    private int    position     = 0;
    private int    elementIndex = 0;

    @SuppressWarnings("ncomplete-switch")
    public void parse(DataCharBuffer buffer, IndexBuffer elementBuffer)
    {
        this.position = 0;
        this.elementIndex = 0;
        this.stateIndex = 0;
        this.stateStack[stateIndex] = NOP;

        for(; position < buffer.length; position++)
        {
            switch (buffer.data[position])
            {
                case 'i':
                case 'o':
                case 'p':
                case 's':
                case 't':
                case 'v':
                case 'I':
                case 'O':
                case 'S':
                case 'P':
                case 'T':
                case 'V': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_CMD, this.position); } break;

                case 'l':
                case 'L': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_LEN, this.position); } break;

                case '<':
                case '>': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_OCT, this.position); } break;

                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_NOTE, this.position); } break;

                case '+':
                case '#':
                case '-': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_ACC, this.position); } break;

                case 'n':
                case 'N': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_MIDI, this.position); } break;

                case '.': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_DOT, this.position); } break;

                case '&': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_TIE, this.position); } break;

                case 'r':
                case 'R': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_REST, this.position); } break;

                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': { parseNumberToken(buffer, elementBuffer); elementIndex++; } break;

                case 'M': { parseMMLBegin(buffer, elementBuffer); elementIndex++;  break;}
                case ',': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_CHORD, this.position); } break;
                case ';': { setElementDataLength1(elementBuffer, elementIndex++, ElementTypes.MML_END, this.position); } break;
            }
        }
        elementBuffer.count = this.elementIndex;
    }

    private boolean parseMMLBegin(DataCharBuffer buffer, IndexBuffer elementBuffer)
    {
        if (
                buffer.data[this.position + 1] == 'M' &&
                buffer.data[this.position + 2] == 'L' &&
                buffer.data[this.position + 3] == '@' )
        {
            this.position += 3; // +4, but the outer for-loop will add 1 too
            setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_BEGIN, this.position, 4);
            return true;
        }
        return false;
    }

    private void parseNumberToken(DataCharBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfNumberFound = false;
        while(!isEndOfNumberFound) {
            tempPos++;
            switch(buffer.data[tempPos]){
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': break;

                default    :  { isEndOfNumberFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_NUMBER, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void setState(byte state){
        this.stateStack[this.stateIndex] = state;
    }

    private void pushState(byte state){
        this.stateStack[this.stateIndex] = state;
        this.stateIndex++;
    }
    private void popState() {
        this.stateIndex--;
    }

    private final void setElementDataNoLength(IndexBuffer elementBuffer, int index, byte type, int position) {
        elementBuffer.type    [index] = type;
        elementBuffer.position[index] = position;
    }

    private final void setElementDataLength1(IndexBuffer elementBuffer, int index, byte type, int position) {
        elementBuffer.type    [index] = type;
        elementBuffer.position[index] = position;
        elementBuffer.length  [index] = 1;
    }

    private final void setElementData(IndexBuffer elementBuffer, int index, byte type, int position, int length) {
        elementBuffer.type    [index] = type;
        elementBuffer.position[index] = position;
        elementBuffer.length  [index] = length;
    }
}
