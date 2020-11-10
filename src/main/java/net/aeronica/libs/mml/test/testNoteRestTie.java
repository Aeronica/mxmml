package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;

import java.util.ArrayList;
import java.util.List;

import static net.aeronica.libs.mml.parser.ElementTypes.*;
import static net.aeronica.libs.mml.test.MMLUtil.MML_LOGGER;
import static net.aeronica.libs.mml.test.MMLUtil.getMIDINote;

public class testNoteRestTie
{
    private static final InstState instState = new InstState();
    private static final PartState partState = new PartState();
    private static final TempState tempState = new TempState();

    // Collect Notes, Rests, etc.
    private static final List<MMLObject> mmlObjs = new ArrayList<>(1000);

    // MIDI Constants
    public static final double PPQ = 480.0;

    
    public static void main(String[] args)
    {
        DataByteBuffer dataBuffer = new DataByteBuffer();
        dataBuffer.data = TestData.MML13.getMML().getBytes();
//        dataBuffer.data = (
//                "MML@l8o5cc+c4c+4c.c+.c+4.g&g&e;" +
//                "MML@l8o3rr+r4r+4r.r+.e+4.b&b&c;"
//        ).getBytes(StandardCharsets.US_ASCII);
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
                          .startingTicks(instState.getLongestDurationTicks())
                          .text("EOF")
                          .build());
        //mmlObjs.forEach(p-> MML_LOGGER.info("{} {} {}", String.format("%05d",mmlObjs.lastIndexOf(p)), p.getType(), (p.isTied() ? "&" : "")));

        MML_LOGGER.info("");
        MML_LOGGER.info("*** Process Tied Notes ***");
        processTiedNotes();
        MMLToMIDI toMIDI = new MMLToMIDI();
        toMIDI.processMObjects(mmlObjs);
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
        collectDataToText(nav.anyChar());
        MML_LOGGER.info("BEGIN");
        instState.init();
        partState.init();
        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST_BEGIN).startingTicks(0).text("@").build());
        if (nav.hasNext())
            nav.next();
    }

    static void doChord(MMLNavigator nav)
    {
        collectDataToText(nav.anyChar());
        MML_LOGGER.info("CHORD");
        instState.collectDurationTicks(partState.getRunningTicks());
        addMMLObj(new MMLObject.Builder(MMLObject.Type.PART)
                          .text(getText())
                          .startingTicks(0)
                          .build());

        clearText();
        partState.init();
        if (nav.hasNext())
            nav.next();
    }

    static void doEnd(MMLNavigator nav)
    {
        collectDataToText(nav.anyChar());
        MML_LOGGER.info("END");
        MML_LOGGER.info(instState);
        MML_LOGGER.info(partState);
        instState.collectDurationTicks(partState.getRunningTicks());
        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST_END)
                          .cumulativeTicks(partState.getRunningTicks())
                          .startingTicks(partState.getRunningTicks())
                          .text(getText())
                          .build());

        clearText();
        instState.init();
        partState.init();
        if (nav.hasNext())
            nav.next();
    }

    static void doCommand(MMLNavigator nav)
    {
        char anyChar = nav.anyChar();
        byte type = nav.type();
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER && nav.asInt() >= 0)
            {
                int value = nav.asInt();
                collectDataToText(anyChar);
                collectDataToText(value);
                switch (type)
                {
                    case MML_INSTRUMENT:
                        instState.setInstrument(nav.asInt());
                        addMMLObj(new MMLObject.Builder(MMLObject.Type.INST)
                                          .instrument(instState.getInstrument())
                                          .startingTicks(partState.getRunningTicks())
                                          .build());
                        break;
                    case MML_OCTAVE:
                        partState.setOctave(value);
                        break;
                    case MML_PERFORM:
                        partState.setPerform(value);
                        break;
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
                    case MML_VOLUME:
                        partState.setVolume(value);
                        break;
                }
            }
        }
    }

    static void doLength(MMLNavigator nav)
    {
        collectDataToText(nav.anyChar());
        if (nav.hasNext())
        {
            nav.next();
            if (nav.type() == MML_NUMBER && nav.asInt() >= 0)
            {
                int value = (nav.asInt());
                collectDataToText(value);
                partState.setMMLLength(value, false);
                if (nav.hasNext())
                    nav.next();
                if (nav.type() == MML_DOT)
                {
                    collectDataToText(nav.anyChar());
                    partState.setMMLLength(value, true);
                    if (nav.hasNext())
                        nav.next();
                }
            }
        }
    }

    static void doTie(MMLNavigator nav)
    {
        collectDataToText(nav.anyChar());
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
        collectDataToText(nav.anyChar());
        if (nav.type() == MML_OCTAVE_UP)
            partState.upOctave();
        else if (nav.type() == MML_OCTAVE_DOWN)
            partState.downOctave();
        if (nav.hasNext())
            nav.next();
    }

    /**
     * Process Notes 'CDEFGAB' or MIDI 'N'
     * @param nav navigator reference
     */
    static void doNote(MMLNavigator nav)
    {
        byte noteType = nav.type();
        int prevPitch = partState.getPrevPitch();
        tempState.init();
        if (noteType == MML_NOTE)
        {
            collectDataToText(nav.asChar());
            tempState.setPitch(getMIDINote(nav.asChar(), partState.getOctave()));
        }
        else if (noteType == MML_MIDI)
        {
            collectDataToText(nav.anyChar());
            tempState.setPitch(127); // N<n> MIDI Note
        }
        tempState.setDuration(partState.getMMLLength());
        tempState.setDotted(partState.isDotted());
        int nextType;
        do // handle a crazy ass run on accidental sequence +-+---++++ as seen in some whack MML.
        {
            nextType = peekNextType(nav);
            if (nextType == MML_SHARP || nextType == MML_FLAT)
            {
                nav.next();
                if (noteType == MML_NOTE)
                {
                    collectDataToText(nav.anyChar());
                    tempState.setAccidental(nav.type());
                }
                nextType = peekNextType(nav);
            }
        }
        while (nextType == MML_SHARP || nextType == MML_FLAT);

        if (nextType == MML_NUMBER)
        {
            nav.next();
            if (nav.asInt() >= 0)
                if (noteType == MML_NOTE)
                {
                    collectDataToText(nav.asInt());
                    tempState.setDuration(nav.asInt());
                }
                else if (noteType == MML_MIDI)
                {
                    collectDataToText(nav.asInt());
                    tempState.setPitch(nav.asInt() + 12); //MIDI Note
                }
            nextType = peekNextType(nav);
        }
        if (nextType == MML_DOT)
        {
            nav.next();
            if (noteType == MML_NOTE)
            {
                collectDataToText(nav.anyChar());
                tempState.setDotted(true);
            }
        }

        long lengthTicks = durationTicks(tempState.getDuration(), tempState.isDotted());
        boolean tiedNote = (tempState.getPitch() == prevPitch && partState.isTied());

        addMMLObj(new MMLObject.Builder(MMLObject.Type.NOTE)
                          .midiNote(tempState.getPitch())
                          .startingTicks(partState.getRunningTicks())
                          .lengthTicks(lengthTicks)
                          .volume(partState.getVolume())
                          .tied(tiedNote)
                          .text(getText())
                          .build());

        partState.accumulateTicks(lengthTicks);
        if (noteType == MML_NOTE)
            MML_LOGGER.info("NOTE { " + getText() + " }, " + tempState + (tiedNote ? " *** Tied to Previous Note ***" : ""));
        else
            MML_LOGGER.info("MIDI { " + getText() + " }, " + tempState + (tiedNote ? " *** Tied to Previous Note ***" : ""));
        partState.setPrevPitch(tempState.getPitch());
        partState.setTied(false);
        clearText();
        if (nav.hasNext())
            nav.next();
    }

    static void doRest(MMLNavigator nav)
    {
        // REST breaks ties between notes
        partState.setTied(false);
        partState.setPrevPitch(-1);

        collectDataToText(nav.anyChar());
        tempState.init();
        tempState.setDuration(partState.getMMLLength());
        tempState.setDotted(partState.isDotted());
        int nextType;
        // RESTs don't really need these, but I've seen MML where people treat them like notes. example: r&r+2.
        //  I'm guessing they simply silence notes that way for testing.
        do // handle a crazy ass run on accidental sequence +-+---++++ as seen in some whack MML.
        {
            nextType = peekNextType(nav);
            if (nextType == MML_SHARP || nextType == MML_FLAT)
            {
                nav.next();
                collectDataToText(nav.anyChar());
                nextType = peekNextType(nav);
            }
        }
        while (nextType == MML_SHARP || nextType == MML_FLAT);

        if (nextType == MML_NUMBER)
        {
            nav.next();
            collectDataToText(nav.asInt());
            if (nav.asInt() >= 0)
                tempState.setDuration(nav.asInt());
            nextType = peekNextType(nav);
        }
        if (nextType == MML_DOT)
        {
            nav.next();
            collectDataToText(nav.anyChar());
            tempState.setDotted(true);
        }


        // Do rest Processing HERE ****
        long lengthTicks = durationTicks(tempState.getDuration(), tempState.isDotted());

        addMMLObj(new MMLObject.Builder(MMLObject.Type.REST)
            .startingTicks(partState.getRunningTicks())
            .lengthTicks(lengthTicks)
            .text(getText())
            .build());

        MML_LOGGER.info("REST { " + getText() + " }, " + tempState);
        clearText();
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
        double dot = dottedLEN ? 15.0d : 10.0d;
        return (long) (((4.0d / (double) mmlNoteLength) * dot / 10.0d) * PPQ);
    }

    // debugging

    private static final StringBuilder sb = new StringBuilder();
    static void collectDataToText(char c)
    {
        sb.append(c);
    }

    static void collectDataToText(int number)
    {
        sb.append(number);
    }

    static String getText()
    {
        return sb.toString();
    }

    static void clearText()
    {
        try {sb.delete(0, Math.max(sb.length(), 0));} catch(Exception e) {/* NOP */}
    }
}
