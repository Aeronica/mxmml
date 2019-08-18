package net.aeronica.libs.mml.parser;

/**
 */
public class TokenTypes
{
    public static final byte MML_CMD = 1;       // [iopstvIOPSTV] Instrument, Octave, Perform, Sustain, Tempo, Volume
    public static final byte MML_LEN = 2;       // [lL] Length Command
    public static final byte MML_OCT = 3;       // [<>] Octave down/up
    public static final byte MML_NOTE = 4;      // [a-gA-G] Notes
    public static final byte MML_ACC = 5;       // [+#-] Accidental
    public static final byte MML_MIDI = 6;      // [nN] MIDI note
    public static final byte MML_DOT = 7;       // '.' dotted
    public static final byte MML_TIE = 8;       // '&' Tie
    public static final byte MML_REST = 9;      // [rR] Rest
    public static final byte MML_NUMBER = 10;      // Positive Integer
    public static final byte MML_BEGIN = 11;    // 'MML@' MML Begin PART/Instrument
    public static final byte MML_CHORD = 12;    // ',' Add Chord
    public static final byte MML_END = 13;      // ';' MML End PART/Instrument
}
