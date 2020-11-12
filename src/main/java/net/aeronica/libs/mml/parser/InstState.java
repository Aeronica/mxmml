package net.aeronica.libs.mml.parser;

import static net.aeronica.libs.mml.oldcore.MMLUtil.clamp;

public class InstState
{
    private int tempo;
    private int instrument;
    private long longestPart;
    private int minVolume = 127;
    private int maxVolume = 0;
    private boolean volumeArcheAge = false;
    private int perform = 0;
    private int sustain = 0;

    InstState()
    {
        this.init();
        this.longestPart = 0;
    }

    public void init()
    {
        tempo = 120;
        instrument = 0;
    }

    public void setTempo(int tempo)
    {
        // tempo 32-255, anything outside the range resets to 120
        if (tempo < 32 || tempo > 255)
        {
            this.tempo = 120;
        } else
        {
            this.tempo = tempo;
        }
    }

    public int getTempo() {return tempo;}

    public int getInstrument() {return instrument;}

    public void setInstrument(int preset)
    {
        this.instrument = (clamp(0, 99999, preset));
    }

    public int getPerform()
    {
        return perform;
    }

    public void setPerform(int perform)
    {
        this.perform = perform;
    }

    public int getSustain()
    {
        return sustain;
    }

    public void setSustain(int sustain)
    {
        this.sustain = sustain;
    }

    void collectDurationTicks(long durationTicks)
    {
        if (durationTicks > this.longestPart) this.longestPart = durationTicks;
    }

    long getLongestDurationTicks() {return this.longestPart;}

    @Override
    public String toString()
    {
        return "@InstState: tempo=" + tempo + ", instrument=" + instrument + ", LongestDurationTicks= " + longestPart;
    }

    void collectVolume(int volumeIn)
    {
        int volume = clamp(0, 127, volumeIn);
        if (volume > 15)
            volumeArcheAge = true;
        this.minVolume = Math.min(this.minVolume, volume);
        this.maxVolume = Math.max(this.maxVolume, volume);
    }

    int getMinVolume()
    {
        return volumeArcheAge ? minVolume : minVolume * 127 / 15;
    }

    int getMaxVolume()
    {
        return volumeArcheAge ? maxVolume : maxVolume * 127 / 15;
    }
}
