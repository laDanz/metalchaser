#!/bin/bash
# Script um wav in (unser) ogg format zu konvertiern

if [ $# != 1  ]; then	
	echo "Usage: sound_convert.sh SOURCEFILE.wav"
else
	sox -V $1 -r 44100 -w -c 2 -s $1.raw
	oggenc -r -q 10 $1.raw 
	rm $1.raw
fi
