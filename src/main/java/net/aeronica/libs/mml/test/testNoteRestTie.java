package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.oldcore.TestData;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;

import java.nio.charset.StandardCharsets;

import static net.aeronica.libs.mml.parser.ElementTypes.*;
import static net.aeronica.libs.mml.test.MMLUtil.MML_LOGGER;

@SuppressWarnings("unused")
public class testNoteRestTie
{
    private static final String mmlString = TestData.MML2.getMML();
    private static final StateInst stateInst = new StateInst();
    private static final StatePart statePart = new StatePart();
    
    public static void main(String[] args)
    {
        DataByteBuffer dataBuffer = new DataByteBuffer();
        //dataBuffer.data = mmlString.getBytes(StandardCharsets.US_ASCII);
        dataBuffer.data = "MML@i65535t180v10o5cccc&c&c;".getBytes(StandardCharsets.US_ASCII);
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser parser = new MMLParser();
        parser.parse(dataBuffer, elementBuffer);



        MMLNavigator navigator = new MMLNavigator(dataBuffer, elementBuffer);
        if (!navigator.hasNext()) return;
        do
        {
            switch(navigator.type())
            {
                case MML_INSTRUMENT: { setInstrument(navigator); } break;
                case MML_OCTAVE:
                case MML_PERFORM:
                case MML_SUSTAIN:
                case MML_TEMPO:
                case MML_VOLUME: { setCommand(navigator); } break;
                case MML_LENGTH: { setLength(navigator); } break;
                case MML_OCTAVE_UP:
                case MML_OCTAVE_DOWN:
                case MML_FLAT: { navigator.next(); } break;
                case MML_NOTE: { navigator.next(); } break;
                case MML_SHARP: { navigator.next(); } break;
                case MML_MIDI: { navigator.next(); } break;
                case MML_DOT: { navigator.next(); } break;
                case MML_TIE: { navigator.next(); } break;
                case MML_REST: { navigator.next(); } break;
                case MML_NUMBER: { navigator.next(); } break;
                case MML_BEGIN: { navigator.next(); } break;
                case MML_CHORD: { navigator.next(); } break;
                case MML_END: { navigator.next(); } break;
            }
        }  while (navigator.hasNext());
        MML_LOGGER.info(stateInst);
        MML_LOGGER.info(statePart);
    }
    
    static void setInstrument(MMLNavigator nav)
    {
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER)
            {
                stateInst.setInstrument(nav.asInt());
                nav.next();
            }
        }
    }

    static void setCommand(MMLNavigator nav)
    {
        byte type = nav.type();
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER)
            {
                int value = nav.asInt();
                switch (type)
                {
                    case MML_OCTAVE: statePart.setOctave(value); break;
                    case MML_PERFORM: statePart.setPerform(value); break;
                    case MML_SUSTAIN: statePart.setSustain(value); break;
                    case MML_TEMPO: stateInst.setTempo(value); break;
                    case MML_VOLUME: statePart.setVolume(value); break;
                }
                nav.next();
            }
        }
    }

    static void setLength(MMLNavigator nav)
    {
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER)
            {
                int value = (nav.asInt());
                nav.next();
                if (nav.type() == MML_DOT)
                {
                    statePart.setMMLLength(value, true);
                    nav.next();
                }
                else
                {
                    statePart.setMMLLength(value, false);
                }
            }
        }
    }
}
