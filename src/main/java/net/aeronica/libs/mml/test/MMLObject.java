package net.aeronica.libs.mml.test;

/*
 * MML Object builder class
 */
public class MMLObject
{

    private final Type type;
    private final long longestPartTicks;
    private final int instrument;
    private final long startingTicks;
    private final long cumulativeTicks;
    private final String text;
    private final int midiNote;
    private final long lengthTicks;
    private final int volume;
    private final int tempo;
    private final int minVolume;
    private final int maxVolume;
    private final boolean sustain;
    private final boolean tied;
    
    public MMLObject(Builder builder)
    {
        this.type = builder.type;
        this.longestPartTicks = builder.longestPartTicks;
        this.instrument = builder.instrument;
        this.startingTicks = builder.startingTicks;
        this.cumulativeTicks = builder.cumulativeTicks;
        this.text = builder.text;
        this.midiNote = builder.midiNote;
        this.lengthTicks = builder.lengthTicks;
        this.volume = builder.volume;
        this.tempo = builder.tempo;
        this.minVolume = builder.minVolume;
        this.maxVolume = builder.maxVolume;
        this.sustain = builder.sustain;
        this.tied = builder.tied;
    }

    public Type getType() {return type;}
    public long getlongestPartTicks() {return longestPartTicks;}
    public int getInstrument() {return instrument;}
    public long getStartingTicks() {return startingTicks;}
    public long getCumulativeTicks() {return cumulativeTicks;}
    public int getMidiNote() {return midiNote;}
    public int getNoteVolume() {return volume;}
    public String getText() {return text;}
    public long getLengthTicks() {return lengthTicks;}
    public int getTempo() {return tempo;}
    public int getMinVolume() {return minVolume;}
    public int getMaxVolume() {return maxVolume;}
    
    public static class Builder
    {
        private final Type type;
        private long longestPartTicks;
        private int instrument;
        private long startingTicks;
        private long cumulativeTicks;
        private String text;
        private int midiNote;
        private long lengthTicks;
        private int volume;
        private int tempo;
        private int minVolume;
        private int maxVolume;
        private boolean sustain;
        private boolean tied;
        
        public Builder(Type type)
        {
            this.type = type;
        }        
        public Builder longestPartTicks(long longestPartTicks)
        {
            this.longestPartTicks = longestPartTicks;
            return this;
        }
        public Builder instrument(int instrument)
        {
            this.instrument = instrument;
            return this;
        }        
        public Builder startingTicks(long startingTicks)
        {
            this.startingTicks = startingTicks;
            return this;
        }
        public Builder cumulativeTicks(long cumulativeTicks)
        {
            this.cumulativeTicks = cumulativeTicks;
            return this;
        }
        public Builder text(String text)
        {
            this.text = text;
            return this;
        }
        public Builder midiNote(int midiNote)
        {
            this.midiNote = midiNote;
            return this;
        }        
        public Builder lengthTicks(long lengthTicks)
        {
            this.lengthTicks = lengthTicks;
            return this;
        }
        public Builder volume(int volume)
        {
            this.volume = volume;
            return this;
        }
        public Builder tempo(int tempo)
        {
            this.tempo = tempo;
            return this;
        }
        public Builder minVolume(int minVolume)
        {
            this.minVolume = minVolume;
            return this;
        }
        public Builder maxVolume(int maxVolume)
        {
            this.maxVolume = maxVolume;
            return this;
        }
        public Builder sustain(boolean sustain)
        {
            this.sustain = sustain;
            return this;
        }
        public Builder tied(boolean tied)
        {
            this.tied = tied;
            return this;
        }
        public MMLObject build() {
            MMLObject mmlObj = new MMLObject(this);
            validateMMLObject(mmlObj);
            return mmlObj;
        }
        public void validateMMLObject(MMLObject mmlObj)
        {
            // basic validations
        }
    }
    public enum Type { INST_BEGIN, TEMPO, SUSTAIN, INST, PART, NOTE, REST, INST_END, DONE }
}
