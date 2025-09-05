package javasabr.rlib.reference;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author JavaSaBr
 */
public class ReferencesTest {

  @Test
  void shouldReferencesWork() {

    var byteRef = ReferenceFactory.byteRef((byte) 5);
    var charRef = ReferenceFactory.charRef('T');
    var doubleRef = ReferenceFactory.doubleRef(1.5D);
    var floatRef = ReferenceFactory.floatRef(2.5F);
    var intRef = ReferenceFactory.intRef(5);
    var longRef = ReferenceFactory.longRef(7L);
    var objRef = ReferenceFactory.objRef("Val");
    var shortRef = ReferenceFactory.newShortRef((short) 7);

    Assertions.assertEquals(5, byteRef.value());
    Assertions.assertEquals('T', charRef.value());
    Assertions.assertEquals(1.5D, doubleRef.value());
    Assertions.assertEquals(2.5F, floatRef.value());
    Assertions.assertEquals(5, intRef.value());
    Assertions.assertEquals(7L, longRef.value());
    Assertions.assertEquals("Val", objRef.value());
    Assertions.assertEquals(7, shortRef.value());
  }

  @Test
  void shouldTLReferencesWork() {

    var byteRef = ReferenceFactory.threadLocalByteRef((byte) 3);
    var charRef = ReferenceFactory.threadLocalCharRef('d');
    var doubleRef = ReferenceFactory.threadLocalDoubleRef(3.5D);
    var floatRef = ReferenceFactory.threadLocalFloatRef(1.5F);
    var intRef = ReferenceFactory.threadLocalIntRef(7);
    var longRef = ReferenceFactory.threadLocalLongRef(4L);
    var objRef = ReferenceFactory.threadLocalObjRef("Val3");
    var shortRef = ReferenceFactory.threadLocalShortRef((short) 2);

    Assertions.assertEquals(3, byteRef.value());
    Assertions.assertEquals('d', charRef.value());
    Assertions.assertEquals(3.5D, doubleRef.value());
    Assertions.assertEquals(1.5F, floatRef.value());
    Assertions.assertEquals(7, intRef.value());
    Assertions.assertEquals(4L, longRef.value());
    Assertions.assertEquals("Val3", objRef.value());
    Assertions.assertEquals(2, shortRef.value());

    byteRef.release();
    charRef.release();
    doubleRef.release();
    floatRef.release();
    intRef.release();
    longRef.release();
    objRef.release();
    shortRef.release();

    var byteRef2 = ReferenceFactory.threadLocalByteRef((byte) 3);
    var charRef2 = ReferenceFactory.threadLocalCharRef('d');
    var doubleRef2 = ReferenceFactory.threadLocalDoubleRef(3.5D);
    var floatRef2 = ReferenceFactory.threadLocalFloatRef(1.5F);
    var intRef2 = ReferenceFactory.threadLocalIntRef(7);
    var longRef2 = ReferenceFactory.threadLocalLongRef(4L);
    var objRef2 = ReferenceFactory.threadLocalObjRef("Val3");
    var shortRef2 = ReferenceFactory.threadLocalShortRef((short) 2);

    Assertions.assertSame(byteRef, byteRef2);
    Assertions.assertSame(charRef, charRef2);
    Assertions.assertSame(doubleRef, doubleRef2);
    Assertions.assertSame(floatRef, floatRef2);
    Assertions.assertSame(intRef, intRef2);
    Assertions.assertSame(longRef, longRef2);
    Assertions.assertSame(objRef, objRef2);
    Assertions.assertSame(shortRef, shortRef2);

    var byteRef3 = ReferenceFactory.threadLocalByteRef((byte) 3);
    var charRef3 = ReferenceFactory.threadLocalCharRef('d');
    var doubleRef3 = ReferenceFactory.threadLocalDoubleRef(3.5D);
    var floatRef3 = ReferenceFactory.threadLocalFloatRef(1.5F);
    var intRef3 = ReferenceFactory.threadLocalIntRef(7);
    var longRef3 = ReferenceFactory.threadLocalLongRef(4L);
    var objRef3 = ReferenceFactory.threadLocalObjRef("Val3");
    var shortRef3 = ReferenceFactory.threadLocalShortRef((short) 2);

    Assertions.assertNotSame(byteRef, byteRef3);
    Assertions.assertNotSame(charRef, charRef3);
    Assertions.assertNotSame(doubleRef, doubleRef3);
    Assertions.assertNotSame(floatRef, floatRef3);
    Assertions.assertNotSame(intRef, intRef3);
    Assertions.assertNotSame(longRef, longRef3);
    Assertions.assertNotSame(objRef, objRef3);
    Assertions.assertNotSame(shortRef, shortRef3);
  }
}
