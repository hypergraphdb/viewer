// TextFileReader.java

/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

//---------------------------------------------------------------------------
//  $Revision: 1.1 $ $Date: 2005/12/25 01:22:41 $
//---------------------------------------------------------------------------
package org.hypergraphdb.viewer.data.readers;
//------------------------------------------------------------------------------
import java.io.*;
//---------------------------------------------------------------------------
public class TextFileReader {
  String filename;
  BufferedReader bufferedReader;
  StringBuffer strbuf;
//---------------------------------------------------------------------------
public TextFileReader (String filename)
{
  this.filename = filename;
  try {
    //reader = new FileReader (filename);
    //bufferedReader = new BufferedReader (reader);
    bufferedReader = new BufferedReader (new FileReader (filename));
    }
  catch (IOException e) {
    e.printStackTrace ();
    return;
    }
 
  strbuf = new StringBuffer ();

} // ctor
//---------------------------------------------------------------------------
public int read ()
{
  String newLineOfText;
 
  try {
    while ((newLineOfText = bufferedReader.readLine()) != null) {
      strbuf.append (newLineOfText + "\n");
      }
    }
  catch (IOException e) {
    e.printStackTrace ();
    return -1;
    }

  return (strbuf.length ());

} // read
//---------------------------------------------------------------------------
public String getText ()
{
  return (new String (strbuf));

} // read
//---------------------------------------------------------------------------
public static void main (String argv[])
{
  String fileToRead;

  int argCount = argv.length;
  if (argCount == 0) 
    fileToRead = "TextFileReader.java";
  else
    fileToRead = argv [0];
  
  TextFileReader reader = new TextFileReader (fileToRead);
  int size = reader.read ();
  System.out.println ("size of text block: " + size);
  System.out.println (reader.getText ());

}// main
//---------------------------------------------------------------------------
}


