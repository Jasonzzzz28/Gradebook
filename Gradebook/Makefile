all: setup gradebookadd gradebookdisplay

JFLAGS=-g -classpath .:./lib/*
JC = javac 
#Use to incorporate libraries at run-time
CPFLAGS = -classpath .:./lib/*

gradebookadd:
	$(JC) $(JFLAGS) gradebookadd.java Gradebook.java Helper.java
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookadd \$$@" > gradebookadd
	chmod +x gradebookadd

gradebookdisplay:
	$(JC) $(JFLAGS) gradebookdisplay.java Gradebook.java Helper.java
	echo "#!/bin/bash\n java $(CPFLAGS) gradebookdisplay \$$@" > gradebookdisplay
	chmod +x gradebookdisplay

setup:
	$(JC) $(JFLAGS) setup.java Gradebook.java Helper.java
	echo "#!/bin/bash\n java $(CPFLAGS) setup \$$@" > setup
	chmod +x setup

clean:
	rm -f *.class
	rm -rf gradebookadd gradebookdisplay setup
