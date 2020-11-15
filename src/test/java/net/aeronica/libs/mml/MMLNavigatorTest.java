package net.aeronica.libs.mml;

import net.aeronica.libs.mml.parser.ElementTypes;
import net.aeronica.libs.mml.parser.MMLLexer;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.util.DataByteBuffer;
import net.aeronica.libs.mml.util.IndexBuffer;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MMLNavigatorTest
{

    @Test
    public void testWithParser()
    {
        DataByteBuffer dataBuffer = new DataByteBuffer("MML@V10T240C+;".getBytes(StandardCharsets.US_ASCII));

        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.getLength(), true);

        MMLLexer parser = new MMLLexer();

        parser.parse(dataBuffer, elementBuffer);
        assertEquals(8, elementBuffer.getCount());

        assertsOnNavigator(dataBuffer, elementBuffer);
    }

    private void assertsOnNavigator(DataByteBuffer dataBuffer, IndexBuffer elementBuffer)
    {
        MMLNavigator navigator = new MMLNavigator(dataBuffer, elementBuffer);

        assertEquals(ElementTypes.MML_BEGIN, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_VOLUME, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NUMBER, navigator.type());
        assertEquals(10, navigator.asInt());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_TEMPO, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NUMBER, navigator.type());
        assertEquals(240, navigator.asInt());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NOTE, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_SHARP, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_END, navigator.type());
    }
}
