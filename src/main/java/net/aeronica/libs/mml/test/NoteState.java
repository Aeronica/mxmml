package net.aeronica.libs.mml.test;

public class NoteState
{
    private int lastNote;
    private int currentNote;
    private int accidental;
    private int duration;

    public NoteState()
    {
        init();
    }

    @Override
    public String toString()
    {
        return "@NoteState: lastNote=" + lastNote + ", currentNote" +  currentNote + ", acc=" + accidental;
    }

    void init()
    {
        lastNote = -1;
        currentNote = -1;
        accidental = 0;
        duration = -1;
    }

    public int getLastNote()
    {
        return lastNote;
    }

    public void setLastNote(int lastNote)
    {
        this.lastNote = lastNote;
    }

    public int getCurrentNote()
    {
        return currentNote;
    }

    public void setCurrentNote(int currentNote)
    {
        this.currentNote = currentNote;
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
}
