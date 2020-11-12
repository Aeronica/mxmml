package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.midi.MMLToMIDI;
import net.aeronica.libs.mml.parser.MMLParser;

public class MMLParserTest
{
    public static void main(String[] args)
    {
        String Test = "@R1abc,R1def,R1gab; @bag,fed,cba; @n60n61n62,n63n64n65,n66n67n68;";
        //MMLParser mmlParser = new MMLParser(Test);

        MMLParser mmlParser = new MMLParser(TestData.MML2.getMML());
        MMLToMIDI toMIDI = new MMLToMIDI();
        toMIDI.processMObjects(mmlParser.getMmlObjects());
        PlayMIDI player = new PlayMIDI();
        player.mmlPlay(toMIDI.getSequence());
    }
}
