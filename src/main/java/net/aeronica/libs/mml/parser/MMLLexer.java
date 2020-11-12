package net.aeronica.libs.mml.parser;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class MMLLexer
{
    private int    position     = 0;
    private int    elementIndex = 0;

    @SuppressWarnings("incomplete-switch")
    public void parse(DataByteBuffer buffer, IndexBuffer elementBuffer)
    {
        this.position = 0;
        this.elementIndex = 0;

        for(; position < buffer.length; position++)
        {
            switch (buffer.data[position])
            {
                case 'i':
                case 'I': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_INSTRUMENT, this.position); } break;
                case 'p':
                case 'P': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_PERFORM, this.position); } break;
                case 's':
                case 'S': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_SUSTAIN, this.position); } break;
                case 't':
                case 'T': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_TEMPO, this.position); } break;
                case 'o':
                case 'O': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_OCTAVE, this.position); } break;
                case 'v':
                case 'V': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_VOLUME, this.position); } break;
                case 'l':
                case 'L': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_LENGTH, this.position); } break;

                case '>': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_OCTAVE_UP, this.position); } break;
                case '<': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_OCTAVE_DOWN, this.position); } break;

                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_NOTE, this.position); } break;

                case '+':
                case '#': { parseSharp(buffer, elementBuffer); elementIndex++; } break;
                case '-': { parseFlat(buffer, elementBuffer); elementIndex++; } break;

                case 'n':
                case 'N': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_MIDI, this.position); } break;

                case '.': { parseDot(buffer, elementBuffer); elementIndex++; } break;

                case '&': { parseTie(buffer, elementBuffer); elementIndex++; } break;

                case 'r':
                case 'R': { setElementDataLength(elementBuffer, elementIndex++, ElementTypes.MML_REST, this.position); } break;

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
                case ',': { parseChord(buffer, elementBuffer); elementIndex++;  break;}
                case ';': { parseEnd(buffer, elementBuffer); elementIndex++;  break;}
            }
        }
        elementBuffer.count = this.elementIndex;
    }

    private void parseMMLBegin(DataByteBuffer buffer, IndexBuffer elementBuffer)
    {
        int tempPos = this.position;
        tempPos++;
        if (!(tempPos >= buffer.data.length))
            if (buffer.data[tempPos] == 'M')
            {
                tempPos++;
                if (!(tempPos >= buffer.data.length))
                {
                    if (buffer.data[tempPos] == 'L')
                    {
                        tempPos++;
                        if (!(tempPos >= buffer.data.length))
                        {
                            if (buffer.data[tempPos] == '@')
                            {
                                tempPos++;
                                if (!(tempPos >= buffer.data.length))
                                {
                                    setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_BEGIN, this.position, tempPos - this.position);
                                    this.position = tempPos - 1; // -1 because the outer for-loop adds 1 to the position too
                                }
                            }
                        }
                    }
                }
            }
    }

    private void parseNumberToken(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfNumberFound = false;
        while(!isEndOfNumberFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
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

    private void parseSharp(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case '+':
                case '#': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_SHARP, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void parseFlat(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case '-': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_FLAT, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void parseDot(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case '.': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_DOT, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void parseTie(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case '&': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_TIE, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void parseChord(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case ',': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_CHORD, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void parseEnd(DataByteBuffer buffer, IndexBuffer elementBuffer) {
        int tempPos = this.position;
        boolean isEndOfRunFound = false;
        while(!isEndOfRunFound) {
            tempPos++;
            if (tempPos >= buffer.data.length) break;
            switch(buffer.data[tempPos]){
                case ';': break;

                default    :  { isEndOfRunFound = true; }
            }
        }
        setElementData(elementBuffer, this.elementIndex, ElementTypes.MML_END, this.position, tempPos - this.position);
        this.position = tempPos -1; // -1 because the outer for-loop adds 1 to the position too
    }

    private void setElementDataLength(IndexBuffer elementBuffer, int index, byte type, int position) {
        elementBuffer.type    [index] = type;
        elementBuffer.position[index] = position;
        elementBuffer.length  [index] = 1;
    }

    private void setElementData(IndexBuffer elementBuffer, int index, byte type, int position, int length) {
        elementBuffer.type    [index] = type;
        elementBuffer.position[index] = position;
        elementBuffer.length  [index] = length;
    }
}
