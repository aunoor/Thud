package btthud.util;

import java.io.*;
import java.util.*;
import java.awt.*;

// From: http://www.javaworld.com/javaworld/javatips/jw-javatip76-p2.html
// Provides a class useful for making deep copies of objects that are serializable... ie MUPrefs

public class ObjectCloner
{

   // so that nobody can accidentally create an ObjectCloner object
   private ObjectCloner(){}

   // returns a deep copy of an object
   static public Object deepCopy(Object oldObj) throws Exception
   {

      ObjectOutputStream oos = null;
      ObjectInputStream ois = null;

      try
      {

         ByteArrayOutputStream bos = 
               new ByteArrayOutputStream(); // A
         oos = new ObjectOutputStream(bos); // B

         // serialize and pass the object
         oos.writeObject(oldObj);   // C
         oos.flush();               // D

         ByteArrayInputStream bin = 
               new ByteArrayInputStream(bos.toByteArray()); // E
         ois = new ObjectInputStream(bin);                  // F

         // return the new object
         return ois.readObject(); // G

      }
      catch(Exception e)
      {
         System.out.println("Exception in ObjectCloner = " + e);
         throw(e);
      }
      finally
      {
         oos.close();
         ois.close();
      }

   }

}
