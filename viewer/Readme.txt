Welcome to HGViewer
=================

HGViewer is a free, open-source viewer for HyperGraph DB-s. 

For more information, please visit http://www.hypergraphdb.org. Introductory documentation on the viewer can be found directly at this links http://code.google.com/p/hypergraphdb/wiki/HGViewer.

Licensing information may be found in the LicensingInformation file in this directory.

Requirements
============

HGViewer needs HypergraphDB and  Java 5 to run. It has been tested on Windows and Linux. It should work anywhere HyperGraphDB itself runs.

Running With Seco
=================

While the viewer runs as a standalone program, not much effort has been put in its UI. To take advantage of its full capabilities, it is best to use it as a component in the Seco environment - please see http://www.kobrix.com/seco.jsp.

Running on Windows
==================

Edit the run.cmd file to set the HGDB_HOME and JAVA_HOME environment variables. JAVA_HOME may point to a JDK installation or to a JRE installation. Then you can run the file from anywhere in your computer.

Running on Linux
================

Edit the run.sh file to set the HGDB_HOME environment variable. If java is not in your path, then you may need to modify the script further to point to it (just find where it is being called). If run.sh is not recognized as a program, you may need to set its execute permissions like this:

chmod +x run.sh


HAVE FUN!