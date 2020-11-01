package net.aeronica.libs.mml.test;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.oldcore.MMLUtil;
import net.aeronica.libs.mml.oldcore.TestData;
import net.aeronica.libs.mml.parser.ElementTypes;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;

@SuppressWarnings("unused")
public class test
{
    private static final String mmlString = TestData.MML2.getMML();

    public static void main(String[] args)
    {
        DataCharBuffer dataBuffer = new DataCharBuffer();
        // dataBuffer.data = mmlString.toCharArray();
        dataBuffer.data = "MML@V10T240C+++D---E###F&F&&&FL8,,,,,T240V12GGG;;;--".toCharArray();
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser parser = new MMLParser();
        parser.parse(dataBuffer, elementBuffer);

        MMLNavigator navigator = new MMLNavigator(dataBuffer, elementBuffer);
        if (!navigator.hasNext()) return;
        for (int i = 0; i < elementBuffer.count; i++)
        {
            switch(navigator.type())
            {
                case ElementTypes.MML_INSTRUMENT: { MMLUtil.MML_LOGGER.info(" instrument"); } break;
                case ElementTypes.MML_OCTAVE: { MMLUtil.MML_LOGGER.info(" octave"); } break;
                case ElementTypes.MML_PERFORM: { MMLUtil.MML_LOGGER.info(" perform"); } break;
                case ElementTypes.MML_SUSTAIN: { MMLUtil.MML_LOGGER.info(" sustain"); } break;
                case ElementTypes.MML_TEMPO: { MMLUtil.MML_LOGGER.info(" tempo"); } break;
                case ElementTypes.MML_VOLUME: { MMLUtil.MML_LOGGER.info(" volume"); } break;
                case ElementTypes.MML_LENGTH: { MMLUtil.MML_LOGGER.info(" length"); } break;
                case ElementTypes.MML_OCTAVE_UP: { MMLUtil.MML_LOGGER.info("  >"); } break;
                case ElementTypes.MML_OCTAVE_DOWN: { MMLUtil.MML_LOGGER.info("  <"); } break;
                case ElementTypes.MML_NOTE: { MMLUtil.MML_LOGGER.info(" note: {}", navigator.asChar()); } break;
                case ElementTypes.MML_SHARP: { MMLUtil.MML_LOGGER.info("  +"); } break;
                case ElementTypes.MML_FLAT: { MMLUtil.MML_LOGGER.info("  -"); } break;
                case ElementTypes.MML_MIDI: { MMLUtil.MML_LOGGER.info(" midi"); } break;
                case ElementTypes.MML_DOT: { MMLUtil.MML_LOGGER.info("  ."); } break;
                case ElementTypes.MML_TIE: { MMLUtil.MML_LOGGER.info("  &"); } break;
                case ElementTypes.MML_REST: { MMLUtil.MML_LOGGER.info(" rest"); } break;
                case ElementTypes.MML_NUMBER: { MMLUtil.MML_LOGGER.info("  {}", navigator.asInt()); } break;
                case ElementTypes.MML_BEGIN: { MMLUtil.MML_LOGGER.info("BEGIN"); } break;
                case ElementTypes.MML_CHORD: { MMLUtil.MML_LOGGER.info("CHORD"); } break;
                case ElementTypes.MML_END: { MMLUtil.MML_LOGGER.info("END"); } break;
            }
            navigator.next();
        }
    }
}
