package net.aeronica.libs.mml;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.parser.ElementTypes;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MMLNavigatorTest
{
    @Test
    public void testWithParser()
    {
        DataCharBuffer dataBuffer = new DataCharBuffer();
        dataBuffer.data = "MML@V10T240C+;".toCharArray();
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer tokenBuffer = new IndexBuffer(dataBuffer.data.length, true);
        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser parser = new MMLParser(tokenBuffer, elementBuffer);

        parser.parse(dataBuffer);
        assertEquals(8, elementBuffer.count);


        assertsOnNavigator(dataBuffer, elementBuffer);
    }

    private void assertsOnNavigator(DataCharBuffer dataBuffer, IndexBuffer elementBuffer)
    {
        MMLNavigator navigator = new MMLNavigator(dataBuffer, elementBuffer);

        assertEquals(ElementTypes.MML_BEGIN, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_CMD, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NUMBER, navigator.type());
        assertEquals(10, navigator.asInt());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_CMD, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NUMBER, navigator.type());
        assertEquals(240, navigator.asInt());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_NOTE, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_ACC, navigator.type());

        assertTrue(navigator.hasNext());
        navigator.next();

        assertEquals(ElementTypes.MML_END, navigator.type());
    }
}
