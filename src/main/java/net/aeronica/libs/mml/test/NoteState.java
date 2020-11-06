package net.aeronica.libs.mml.test;

public class NoteState
{
    private int pitch;
    private int accidental;
    private int duration;
    private boolean dotted;

    public NoteState()
    {
        init();
    }

    @Override
    public String toString()
    {
        return "@NoteState: note:" + pitch + ", acc: " + accidental +", duration: " + duration + ", dot: " + dotted;
    }

    void init()
    {
        pitch = -1;
        accidental = 0;
        duration = -1;
        dotted = false;
    }

    public int getPitch()
    {
        return pitch;
    }

    public void setPitch(int pitch)
    {
        this.pitch = pitch;
    }

    public int getAccidental()
    {
        return accidental;
    }

    public void setAccidental(int accidental)
    {
        this.accidental = accidental;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public boolean isDotted()
    {
        return dotted;
    }

    public void setDotted(boolean dotted)
    {
        this.dotted = dotted;
    }
}
