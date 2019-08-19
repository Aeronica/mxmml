package net.aeronica.libs.mml.parser;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;

public class MMLTokenizer
{
    private DataCharBuffer dataBuffer  = null;
    private IndexBuffer tokenBuffer = null;

    private int tokenIndex   = 0;
    private int dataPosition = 0;
    private int tokenLength  = 0;

    public MMLTokenizer(IndexBuffer tokenBuffer) {
        this.tokenBuffer = tokenBuffer;
    }

    public MMLTokenizer(DataCharBuffer dataBuffer, IndexBuffer tokenBuffer) {
        this.dataBuffer  = dataBuffer;
        this.tokenBuffer = tokenBuffer;
    }

    public void reinit(DataCharBuffer dataBuffer, IndexBuffer tokenBuffer) {
        this.dataBuffer  = dataBuffer;
        this.tokenBuffer = tokenBuffer;
        this.tokenIndex  = 0;
        this.dataPosition= 0;
        this.tokenLength = 0;
    }

    public boolean hasMoreTokens() {
        return (this.dataPosition + this.tokenLength) < this.dataBuffer.length ;
    }

    public void parseToken()
    {
        skipWhiteSpace();

        this.tokenBuffer.position[this.tokenIndex] = this.dataPosition;
        char nextChar = this.dataBuffer.data[this.dataPosition];

        switch (nextChar)
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
            case 'V': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_CMD; } break;
            case 'l':
            case 'L': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_LEN; } break;
            case '<':
            case '>': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_OCT; } break;
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
            case 'G': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_NOTE; } break;
            case '+':
            case '#':
            case '-': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_ACC; } break;
            case 'n':
            case 'N': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_MIDI; } break;
            case '.': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_DOT; } break;
            case '&': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_TIE; } break;
            case 'r':
            case 'R': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_REST; } break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9': { parseNumberToken(); this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_NUMBER; } break;
            case 'M': { if(parseMMLBegin()) { this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_BEGIN;} } break;
            case ',': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_CHORD; } break;
            case ';': { /*this.tokenLength = 1;*/ this.tokenBuffer.type[this.tokenIndex] = TokenTypes.MML_END; } break;
        }
        this.tokenBuffer.length[this.tokenIndex] = this.tokenLength;
    }

    private void parseNumberToken()
    {
        this.tokenLength = 1;

        boolean isEndOfNumberFound = false;
        while(!isEndOfNumberFound) {
            switch(this.dataBuffer.data[this.dataPosition + this.tokenLength])
            {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9': { this.tokenLength++; } break;

                default:  { isEndOfNumberFound = true; }
            }
        }
    }

    private boolean parseMMLBegin()
    {
        if (
            this.dataBuffer.data[this.dataPosition + 1] == 'M' &&
            this.dataBuffer.data[this.dataPosition + 2] == 'L' &&
            this.dataBuffer.data[this.dataPosition + 3] == '@' )
        {
            this.tokenLength = 4;
            return true;
        }
        return false;
    }

    private void skipWhiteSpace()
    {
        boolean isWhiteSpace = true;
        while(isWhiteSpace) {
            switch(this.dataBuffer.data[this.dataPosition]) {
                case ' '    :  ;  /* falling through - all white space characters are treated the same*/
                case '\r'   :  ;
                case '\n'   :  ;
                case '\t'   :  { this.dataPosition++; } break;

                default     :  { isWhiteSpace = false; }  /* any non white space char will break the while loop */
            }
        }
    }

    public void nextToken()
    {
        switch(this.tokenBuffer.type[this.tokenIndex]){
            case TokenTypes.MML_CMD: { this.dataPosition++; break;}
            case TokenTypes.MML_LEN: { this.dataPosition++; break;}
            case TokenTypes.MML_OCT: {this.dataPosition++; break;}
            case TokenTypes.MML_NOTE: {this.dataPosition++; break;}
            case TokenTypes.MML_ACC: {this.dataPosition++; break;}
            case TokenTypes.MML_MIDI: {this.dataPosition++; break;}
            case TokenTypes.MML_DOT: {this.dataPosition++; break;}
            case TokenTypes.MML_TIE: {this.dataPosition++; break;}
            case TokenTypes.MML_REST: {this.dataPosition++; break;}
            case TokenTypes.MML_CHORD: {this.dataPosition++; break;}
            case TokenTypes.MML_END: {this.dataPosition++; break;}
            default: {this.dataPosition += this.tokenLength;}
        }
        //this.dataPosition += this.tokenBuffer.length[this.tokenIndex]; //move data position to end of current token.
        this.tokenIndex++;  //point to next token index array cell.
    }

    public int tokenPosition() {
        return this.tokenBuffer.position[this.tokenIndex];
    }

    public int tokenLength() {
        return this.tokenBuffer.length[this.tokenIndex];
    }

    public byte tokenType() {
        return this.tokenBuffer.type[this.tokenIndex];
    }
}
