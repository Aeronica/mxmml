package net.aeronica.libs.mml;

import net.aeronica.libs.mml.core.DataByteBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.parser.ElementTypes;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MMLNavigatorTest
{

    @Test
    public void testWithParser()
    {
        DataByteBuffer dataBuffer = new DataByteBuffer();
        dataBuffer.data = "MML@V10T240C+;".getBytes(StandardCharsets.US_ASCII);
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser parser = new MMLParser();

        parser.parse(dataBuffer, elementBuffer);
        assertEquals(8, elementBuffer.count);

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
