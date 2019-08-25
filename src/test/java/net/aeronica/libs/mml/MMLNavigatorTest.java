package net.aeronica.libs.mml;

import net.aeronica.libs.mml.core.DataCharBuffer;
import net.aeronica.libs.mml.core.IndexBuffer;
import net.aeronica.libs.mml.parser.ElementTypes;
import net.aeronica.libs.mml.parser.MMLNavigator;
import net.aeronica.libs.mml.parser.MMLParser1;
import net.aeronica.libs.mml.parser.MMLParser2;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class MMLNavigatorTest
{

    public void testWithParser1()
    {
        DataCharBuffer dataBuffer = new DataCharBuffer();
        dataBuffer.data = "MML@V10T240C+;".toCharArray();
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer tokenBuffer = new IndexBuffer(dataBuffer.data.length, true);
        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser1 parser = new MMLParser1(tokenBuffer, elementBuffer);

        parser.parse(dataBuffer);
        assertEquals(8, elementBuffer.count);


        assertsOnNavigator(dataBuffer, elementBuffer);
    }

    @Test
    public void testWithParser2()
    {
        DataCharBuffer dataBuffer = new DataCharBuffer();
        dataBuffer.data = "MML@V10T240C+;".toCharArray();
        dataBuffer.length = dataBuffer.data.length;

        IndexBuffer elementBuffer = new IndexBuffer(dataBuffer.data.length, true);

        MMLParser2 parser = new MMLParser2();

        parser.parse(dataBuffer, elementBuffer);
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
