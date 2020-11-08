package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.oldcore.TestData;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static net.aeronica.libs.mml.parser.ElementTypes.*;
import static net.aeronica.libs.mml.test.MMLUtil.*;

@SuppressWarnings("unused")
public class testNoteRestTie
{
    private static final String mmlString = TestData.MML0.getMML();
    private static final InstState instState = new InstState();
    private static final PartState partState = new PartState();
    private static final NoteState noteState = new NoteState();
    private static final NoteState restState = new NoteState();

    // Collect Notes, Rests, etc.
    private static final List<MMLObject> mmlObjs = new ArrayList<>(1000);

    // MIDI Constants
    private static final double PPQ = 96.0;

    
    public static void main(String[] args)
    {
        DataByteBuffer dataBuffer = new DataByteBuffer();
        //dataBuffer.data = mmlString.getBytes(StandardCharsets.US_ASCII);
        dataBuffer.data = (
                "MML@cc.c2c2.;" +
                "MML@e2.e2e.e;"
        ).getBytes(StandardCharsets.US_ASCII);
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
                case MML_INSTRUMENT:
                case MML_OCTAVE:
                case MML_PERFORM:
                case MML_SUSTAIN:
                case MML_TEMPO:
                case MML_VOLUME: { doCommand(navigator); } break;
                case MML_LENGTH: { doLength(navigator); } break;
                case MML_OCTAVE_UP:
                case MML_OCTAVE_DOWN: { doOctaveUpDown(navigator); } break;
                case MML_MIDI:
                case MML_NOTE: { doNote(navigator); } break;
                case MML_NUMBER:
                case MML_FLAT:
                case MML_SHARP:
                case MML_DOT: { navigator.next(); } break;
                case MML_TIE: { doTie(navigator); } break;
                case MML_REST: { doRest(navigator); } break;
                case MML_BEGIN: { doBegin(navigator); } break;
                case MML_CHORD: { doChord(navigator); } break;
                case MML_END: { doEnd(navigator); } break;
            }
        }  while (navigator.hasNext());

        addMMLObj(new MMLObject.Builder(MMLObject.Type.DONE)
                          .longestPartTicks(instState.getLongestDurationTicks())
                          .minVolume(instState.getMinVolume())
                          .maxVolume(instState.getMaxVolume())
                          .build());
        //mmlObjs.forEach(p-> MML_LOGGER.info("{} {} {}", String.format("%05d",mmlObjs.lastIndexOf(p)), p.getType(), (p.isTied() ? "&" : "")));

        MML_LOGGER.info("");
        MML_LOGGER.info("*** Process Tied Notes ***");
        processTiedNotes();
        MMLToMIDI toMIDI = new MMLToMIDI(mmlObjs);

        PlayMIDI player = new PlayMIDI();
        player.mmlPlay(toMIDI.getSequence());
    }

    static void processTiedNotes()
    {
        boolean lastTied = false;
        for (int idx = mmlObjs.size()-1; idx > 0; idx-- )
        {
            MMLObject mo = mmlObjs.get(idx);
            if (mo.getType() == MMLObject.Type.PART || mo.getType() == MMLObject.Type.INST_END || mo.getType() == MMLObject.Type.REST)
                lastTied = false;
            if (mo.getType() == MMLObject.Type.NOTE)
            {
                if (mo.isTied() && !lastTied) // End of tie
                {
                    MML_LOGGER.info("{} End of tie", mo.getMidiNote());
                    lastTied = true;
                    mo.setDoNoteOn(false);
                    mo.setDoNoteOff(true);
                } else if (mo.isTied() && lastTied) // Mid tie
                {
                    MML_LOGGER.info("{}    Mid tie", mo.getMidiNote());
                    lastTied = true;
                    mo.setDoNoteOn(false);
                    mo.setDoNoteOff(false);
                } else if (!mo.isTied() && lastTied) // Begin tie
                {
                    MML_LOGGER.info("{}  Begin tie", mo.getMidiNote());
                    lastTied = false;
                    mo.setDoNoteOn(true);
                    mo.setDoNoteOff(false);
                } else if (!mo.isTied() && !lastTied)
                {
                    MML_LOGGER.info("{}     No tie", mo.getMidiNote());
                    lastTied = false;
                    mo.setDoNoteOn(true);
                    mo.setDoNoteOff(true);
                }
            }
        }
    }

    static void doBegin(MMLNavigator nav)
    {
        MML_LOGGER.info("BEGIN");
        instState.init();
        partState.init();
        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST_BEGIN).build());
        nav.next();
    }

    static void doChord(MMLNavigator nav)
    {
        MML_LOGGER.info("CHORD");
        instState.collectDurationTicks(partState.getRunningTicks());
        addMMLObj(new MMLObject.Builder(MMLObject.Type.PART)
                          .cumulativeTicks(partState.getRunningTicks())
                          .build());
        partState.init();
        nav.next();
    }

    static void doEnd(MMLNavigator nav)
    {
        MML_LOGGER.info("END");
        MML_LOGGER.info(instState);
        MML_LOGGER.info(partState);
        instState.collectDurationTicks(partState.getRunningTicks());
        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST_END)
                          .cumulativeTicks(partState.getRunningTicks())
                          .build());
        nav.next();
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
                    case MML_INSTRUMENT:
                        instState.setInstrument(nav.asInt());
                        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST)
                            .instrument(instState.getInstrument())
                            .startingTicks(partState.getRunningTicks())
                            .build());
                        break;
                    case MML_OCTAVE: partState.setOctave(value); break;
                    case MML_PERFORM: partState.setPerform(value); break;
                    case MML_SUSTAIN:
                        partState.setSustain(value);
                        addMMLObj(new MMLObject.Builder(MMLObject.Type.SUSTAIN)
                            .sustain(partState.getSustain())
                            .startingTicks(partState.getRunningTicks())
                            .build());
                        break;
                    case MML_TEMPO:
                        instState.setTempo(value);
                        addMMLObj(new MMLObject.Builder(MMLObject.Type.TEMPO)
                            .tempo(instState.getTempo())
                            .startingTicks(partState.getRunningTicks())
                            .build());
                        break;
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
                partState.setMMLLength(value, false);
                if (nav.hasNext())
                    nav.next();
                if (nav.type() == MML_DOT)
                {
                    partState.setMMLLength(value, true);
                    if (nav.hasNext())
                        nav.next();
                }
            }
        }
    }

    static void doTie(MMLNavigator nav)
    {
        // Only tie if the next element is a NOTE/MIDI
        byte peekValue = peekNextType(nav);
        if (peekValue == MML_NOTE || peekValue == MML_MIDI)
        {
            partState.setTied(true); MML_LOGGER.info(" &");
        }
        if (nav.hasNext())
            nav.next();
    }

    static void doOctaveUpDown(MMLNavigator nav)
    {
        if (nav.type() == MML_OCTAVE_UP)
            partState.upOctave();
        else if (nav.type() == MML_OCTAVE_DOWN)
            partState.downOctave();
        if (nav.hasNext())
            nav.next();
    }

    static void doNote(MMLNavigator nav)
    {
        byte noteType = nav.type();
        int prevPitch = partState.getPrevPitch();
        noteState.init();
        if (noteType == MML_NOTE)
            noteState.setPitch(getMIDINote(nav.asChar(), partState.getOctave()));
        else
            noteState.setPitch(127);
        noteState.setDuration(partState.getMMLLength());
        noteState.setDotted(partState.isDotted());
        int nextType;
        do // handle a crazy ass run on accidental sequence +-+---++++ as seen in some whack MML.
        {
            nextType = peekNextType(nav);
            if (nextType == MML_SHARP || nextType == MML_FLAT)
            {
                nav.next();
                if (noteType == MML_NOTE)
                    noteState.setAccidental(nav.type());
                nextType = peekNextType(nav);
            }
        }
        while (nextType == MML_SHARP || nextType == MML_FLAT);

        if (nextType == MML_NUMBER)
        {
            nav.next();
            if (noteType == MML_NOTE)
                noteState.setDuration(nav.asInt());
            else
                noteState.setPitch(nav.asInt()+12); //MIDI Note
            nextType = peekNextType(nav);
        }
        if (nextType == MML_DOT)
        {
            nav.next();
            if (noteType == MML_NOTE)
                noteState.setDotted(true);
        }

        long lengthTicks = durationTicks(noteState.getDuration(), noteState.isDotted());
        boolean tiedNote = (noteState.getPitch() == prevPitch && partState.isTied());

        addMMLObj(new MMLObject.Builder(MMLObject.Type.NOTE)
                          .midiNote(noteState.getPitch())
                          .startingTicks(partState.getRunningTicks())
                          .lengthTicks(lengthTicks)
                          .volume(partState.getVolume())
                          .tied(tiedNote)
                          .build());

        partState.accumulateTicks(lengthTicks);
        if (noteType == MML_NOTE)
            MML_LOGGER.info("NOTE " + noteState + (tiedNote ? " *** Tied to Previous Note ***" : ""));
        else
            MML_LOGGER.info("MIDI " + noteState + (tiedNote ? " *** Tied to Previous Note ***" : ""));
        partState.setPrevPitch(noteState.getPitch());
        partState.setTied(false);

        if (nav.hasNext())
            nav.next();
    }

    static void doRest(MMLNavigator nav)
    {
        // REST breaks ties between notes
        partState.setTied(false);
        partState.setPrevPitch(-1);

        restState.init();
        restState.setDuration(partState.getMMLLength());
        restState.setDotted(partState.isDotted());
        int nextType;
        // RESTs don't really need these, but I've seen MML where people treat them like notes. example: r&r+2.
        //  I'm guessing they simply silence notes that way for testing.
        do // handle a crazy ass run on accidental sequence +-+---++++ as seen in some whack MML.
        {
            nextType = peekNextType(nav);
            if (nextType == MML_SHARP || nextType == MML_FLAT)
            {
                nav.next();
                nextType = peekNextType(nav);
            }
        }
        while (nextType == MML_SHARP || nextType == MML_FLAT);

        if (nextType == MML_NUMBER)
        {
            nav.next();
            restState.setDuration(nav.asInt());
            nextType = peekNextType(nav);
        }
        if (nextType == MML_DOT)
        {
            nav.next();
            restState.setDotted(true);
        }
        MML_LOGGER.info("REST " + restState);

        // Do rest Processing HERE ****
        long lengthTicks = durationTicks(restState.getDuration(), restState.isDotted());

        addMMLObj(new MMLObject.Builder(MMLObject.Type.REST)
            .startingTicks(partState.getRunningTicks())
            .lengthTicks(lengthTicks)
            .build());

        partState.accumulateTicks(lengthTicks);
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

    private static void addMMLObj(MMLObject mmlObject)
    {
        mmlObjs.add(mmlObject);
    }

    private static long durationTicks(int mmlNoteLength, boolean dottedLEN)
    {
        mmlNoteLength = clamp(1, 64, mmlNoteLength);
        double dot = dottedLEN ? 15.0d : 10.0d;
        return (long) (((4.0d / (double) mmlNoteLength) * dot / 10.0d) * PPQ);
    }
}
