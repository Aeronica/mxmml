package net.aeronica.libs.mml.parser;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.core.ParserException;

public class MMLParser
{
    private IndexBuffer tokenBuffer   = null;
    private IndexBuffer   elementBuffer = null;
    private int           elementIndex  = 0;
    private MMLTokenizer mmlTokenizer = null;

    public MMLParser(IndexBuffer tokenBuffer, IndexBuffer elementBuffer) {
        this.tokenBuffer   = tokenBuffer;
        this.mmlTokenizer = new MMLTokenizer(this.tokenBuffer);
        this.elementBuffer = elementBuffer;
    }


    public void parse(DataCharBuffer dataBuffer) {
        this.elementIndex  = 0;

        this.mmlTokenizer.reInit(dataBuffer, this.tokenBuffer);

        parseObject(this.mmlTokenizer);

        this.elementBuffer.count = this.elementIndex;
    }

    @SuppressWarnings("ncomplete-switch")
    private void parseObject(MMLTokenizer tokenizer) {
        assertHasMoreTokens(tokenizer);
        tokenizer.parseToken();
        assertThisTokenType(tokenizer.tokenType(), TokenTypes.MML_BEGIN);
        setElementData     (tokenizer, ElementTypes.MML_BEGIN);

        tokenizer.nextToken();
        tokenizer.parseToken();
        byte tokenType = tokenizer.tokenType();

        while( tokenType != TokenTypes.MML_END)
        {
            switch(tokenType) {
                case TokenTypes.MML_CMD    : { setElementData(tokenizer, ElementTypes.MML_CMD);    } break;
                case TokenTypes.MML_LEN    : { setElementData(tokenizer, ElementTypes.MML_LEN);    } break;
                case TokenTypes.MML_OCT    : { setElementData(tokenizer, ElementTypes.MML_OCT);    } break;
                case TokenTypes.MML_NOTE   : { setElementData(tokenizer, ElementTypes.MML_NOTE);   } break;
                case TokenTypes.MML_ACC    : { setElementData(tokenizer, ElementTypes.MML_ACC);    } break;
                case TokenTypes.MML_MIDI   : { setElementData(tokenizer, ElementTypes.MML_MIDI);   } break;
                case TokenTypes.MML_DOT    : { setElementData(tokenizer, ElementTypes.MML_DOT);    } break;
                case TokenTypes.MML_TIE    : { setElementData(tokenizer, ElementTypes.MML_TIE);    } break;
                case TokenTypes.MML_REST   : { setElementData(tokenizer, ElementTypes.MML_REST);   } break;
                case TokenTypes.MML_NUMBER : { setElementData(tokenizer, ElementTypes.MML_NUMBER); } break;
                case TokenTypes.MML_CHORD  : { setElementData(tokenizer, ElementTypes.MML_CHORD);  } break;
            }
            tokenizer.nextToken();
            tokenizer.parseToken();
            tokenType = tokenizer.tokenType();
        }
        setElementData(tokenizer, ElementTypes.MML_END);
    }

    private void setElementData(MMLTokenizer tokenizer, byte elementType) {
        this.elementBuffer.position[this.elementIndex] = tokenizer.tokenPosition();
        this.elementBuffer.length  [this.elementIndex] = tokenizer.tokenLength();
        this.elementBuffer.type    [this.elementIndex] = elementType;
        this.elementIndex++;
    }

    private void assertThisTokenType(byte tokenType, byte expectedTokenType) {
        if(tokenType != expectedTokenType) {
            throw new ParserException("Token type mismatch: Expected " + expectedTokenType + " but found " + tokenType);
        }
    }

    private void assertHasMoreTokens(MMLTokenizer tokenizer) {
        if(! tokenizer.hasMoreTokens()) {
            throw new ParserException("Expected more tokens available in the tokenizer");
        }
    }
}
