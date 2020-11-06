package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.oldcore.TestData;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;

import java.nio.charset.StandardCharsets;

import static net.aeronica.libs.mml.parser.ElementTypes.*;
import static net.aeronica.libs.mml.test.MMLUtil.MML_LOGGER;
import static net.aeronica.libs.mml.test.MMLUtil.getMIDINote;

@SuppressWarnings("unused")
public class testNoteRestTie
{
    private static final String mmlString = TestData.MML2.getMML();
    private static final InstState instState = new InstState();
    private static final PartState partState = new PartState();
    private static final NoteState noteState = new NoteState();
    
    public static void main(String[] args)
    {
        DataByteBuffer dataBuffer = new DataByteBuffer();
        //dataBuffer.data = mmlString.getBytes(StandardCharsets.US_ASCII);
        dataBuffer.data = "MML@i6t180v10l8o5d+-+-egc&c+4&c.;".getBytes(StandardCharsets.US_ASCII);
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
                case MML_INSTRUMENT: { doInstrument(navigator); } break;
                case MML_OCTAVE:
                case MML_PERFORM:
                case MML_SUSTAIN:
                case MML_TEMPO:
                case MML_VOLUME: { doCommand(navigator); } break;
                case MML_LENGTH: { doLength(navigator); } break;
                case MML_OCTAVE_UP:
                case MML_OCTAVE_DOWN: { navigator.next(); } break;
                case MML_NOTE: { doNote(navigator); } break;
                case MML_FLAT:
                case MML_SHARP: { navigator.next(); } break;
                case MML_MIDI: { navigator.next(); } break;
                case MML_DOT: { navigator.next(); } break;
                case MML_TIE: { doTie(navigator); } break;
                case MML_REST: { navigator.next(); } break;
                case MML_NUMBER: { navigator.next(); } break;
                case MML_BEGIN: { instState.init(); navigator.next(); } break;
                case MML_CHORD: { partState.init(); navigator.next(); } break;
                case MML_END: { MML_LOGGER.info(instState);navigator.next(); } break;
            }
        }  while (navigator.hasNext());
        MML_LOGGER.info(instState);
        MML_LOGGER.info(partState);
    }
    
    static void doInstrument(MMLNavigator nav)
    {
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER)
            {
                instState.setInstrument(nav.asInt());
                nav.next();
            }
        }
    }

    static void doCommand(MMLNavigator nav)
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
                    case MML_OCTAVE: partState.setOctave(value); break;
                    case MML_PERFORM: partState.setPerform(value); break;
                    case MML_SUSTAIN: partState.setSustain(value); break;
                    case MML_TEMPO: instState.setTempo(value); break;
                    case MML_VOLUME: partState.setVolume(value); break;
                }
                nav.next();
            }
        }
    }

    static void doLength(MMLNavigator nav)
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
                    partState.setMMLLength(value, true);
                    nav.next();
                }
                else
                {
                    partState.setMMLLength(value, false);
                }
            }
        }
    }

    static void doTie(MMLNavigator nav)
    {
        partState.setTied(true);
        if (nav.hasNext())
            nav.next();
    }

    static void doOctaveUpDown(MMLNavigator nav)
    {
        if (nav.type() == MML_OCTAVE_UP)
            partState.upOctave();
        else
            partState.downOctave();
        if (nav.hasNext())
            nav.next();
    }

    static void doNote(MMLNavigator nav)
    {
        int prevPitch = partState.getPrevPitch();
        noteState.init();
        noteState.setPitch(getMIDINote(nav.asChar(), partState.getOctave()));
        noteState.setDuration(partState.getMMLLength());
        noteState.setDotted(partState.isDotted());
        int nextType;
        do // handle a crazy ass run on accidental sequence +-+---++++ as seen in some whack MML.
        {
            nextType = peekNextType(nav);
            if (nextType == MML_SHARP || nextType == MML_FLAT)
            {
                nav.next();
                noteState.setAccidental(nav.type());
                nextType = peekNextType(nav);
            }
        }
        while (nextType == MML_SHARP || nextType == MML_FLAT);
        if (nextType == MML_NUMBER)
        {
            nav.next();
            noteState.setDuration(nav.asInt());
            nextType = peekNextType(nav);
        }
        if (nextType == MML_DOT)
        {
            nav.next();
            noteState.setDotted(true);
        }
        MML_LOGGER.info(noteState);

        // Do Tie Processing HERE ****
        // Emit/Store Note depending on tie/pitch

        if (nav.hasNext())
            nav.next();
    }

    static byte peekNextType(MMLNavigator nav)
    {
        byte elementType = EOF;
        if (nav.hasNext())
        {
            nav.next();
            elementType = nav.type();
            nav.previous();
        }
        return elementType;
    }
}
