//A class to translate incoming frequencies into the closest member within a given tuning table
//Pass in another object representing a table of pitches (possibly extends array) to use so that
//class is encapsulated
//OR: just keep this as an abstract class and inherit from it.

AbstractTuningTable {

	var <>tableOfPitches;

	*new {
		^super.new.init();
	}

	//A method to receive a pitch, then sort through to find its closest related pitch
	//in the provided tuning table
	getTemperedPitch { arg incomingFreq;
		//Need a good sorting algorithm here to be efficient and find the proper pitch
		//if the pitch is closer to index 0 than size/2
		if( (incomingFreq - tableOfPitches[0]) <= (incomingFreq-tableOfPitches[(tableOfPitches.size-1)/2]),
		{
			//start from beginning of set (direction = 0 to move forward)
			^this.findClosestPitch(incomingFreq, 0, 0);
		},
		{ //else 
			//if the pitch is closer to index size/2 but is less than size/2
			if( (incomingFreq - tableOfPitches[(tableOfPitches.size-1)/2]) < 1,
			{
				//start from middle of set counting backwards
				^this.findClosestPitch(incomingFreq, ((tableOfPitches.size-1)/2), 1 );
			},
			{//otherwise if the pitch is closer to index size/2 but is above size/2 but not closer to size
				if( (incomingFreq - tableOfPitches[(tableOfPitches.size-1)/2]) <= (tableOfPitches[tableOfPitches.size-1]-incomingFreq),
				{	//start from middle of set counting forwards
					^this.findClosestPitch(incomingFreq, ((tableOfPitches.size-1)/2), 0 );
				},
				{
					//if the pitch is closer to index size than size/2
					//start from end of set counting backwards
					^this.findClosestPitch(incomingFreq, (tableOfPitches.size-1), 1 );
				});
			});

		});
	}

	//a function to start searching for the nearest pitch to the given pitch starting from a given index
	findClosestPitch { arg incomingFreq, startingIndex, direction;

		if( (tableOfPitches[tableOfPitches.size-1] <= incomingFreq),
		{
			//startingIndex = tableOfPitches.size;
			^this.tableOfPitches[tableOfPitches.size-1];
		});

		if( direction == 0,
		{
			//Check each member from starting point and if we hit size then we return the last pitch.
			//You should maybe write in something that divides incomingFreq by 2 if the pitch is above
			//our highest pitch (tableOfPitches.size) figuring its an overtone that is picked up (most
			//likely an octave of some sort).
			while( { (tableOfPitches[startingIndex] < incomingFreq) && (startingIndex < tableOfPitches.size) }, 
			{
				startingIndex = startingIndex + 1;
			});

			^this.tableOfPitches[startingIndex];
		},
		{
			while( { (tableOfPitches[startingIndex] > incomingFreq) && (startingIndex >= 0) }, 
			{
				//startingIndex.postln;
				startingIndex = startingIndex - 1;
			});

			^this.tableOfPitches[startingIndex];
		});
	}
}                                                                                                                              