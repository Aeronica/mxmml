package net.aeronica.libs.mml.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.ShortMessage;
import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public class MIDIHelper
{
    private MIDIHelper()
    {
        /* NOP */
    }

    public static MidiEvent createProgramChangeEvent(int channel, int value, long tick) throws InvalidMidiDataException
    {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.PROGRAM_CHANGE, channel, value, 0);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createBankSelectEventMSB(int channel, int value, long tick) throws InvalidMidiDataException
    {
        ShortMessage msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 0, value >> 7);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createBankSelectEventLSB(int channel, int value, long tick) throws InvalidMidiDataException
    {
        ShortMessage msg = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 32, value & 0x7F);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createNoteOnEvent(int channel, int pitch, int velocity, long tick) throws InvalidMidiDataException
    {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.NOTE_ON, channel, pitch, velocity);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createNoteOffEvent(int channel, int pitch, int velocity, long tick) throws InvalidMidiDataException
    {
        ShortMessage msg = new ShortMessage();
        msg.setMessage(ShortMessage.NOTE_OFF, channel, pitch, velocity);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createTempoMetaEvent(int tempo, long tick) throws InvalidMidiDataException
    {
        MetaMessage msg = new MetaMessage();
        byte[] data = ByteBuffer.allocate(4).putInt(1000000 * 60 / tempo).array();
        data[0] = data[1];
        data[1] = data[2];
        data[2] = data[3];
        msg.setMessage(0x51, data, 3);
        return new MidiEvent(msg, tick);
    }

    public static MidiEvent createTextMetaEvent(String text, long tick) throws InvalidMidiDataException
    {
        MetaMessage msg = new MetaMessage();
        byte[] data = text.getBytes();
        msg.setMessage(0x01, data, data.length);
        return new MidiEvent(msg, tick);
    }
}
