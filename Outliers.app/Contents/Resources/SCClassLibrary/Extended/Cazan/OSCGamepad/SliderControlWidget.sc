// A control widget for use with anything. Can be given a synth and the mappings for the synth.

SliderControlWidget : SCControlView {

	var w, controllerOneSlid2d;
	var controllerWindow;
	var controllerOneSlid2d, <>controllerOneButton, <>controllerOneButton2, sliderControl,
		controllerOneValueLabel, chanLabel;
	var synthListPopUp;

	//Parameters for the GamePad. THESE SHOULD BE SET BEFORE LOAD.
	var <>synthsOne;
	var <>controllerOneXMappings, <>controllerOneYMappings;

	//A variable representing the current instrument "patch"
	var currentSelectedInstrumentOne = 0;
	var currentSelectedSynth = 0;

	//Variable for whether the parameter joystick is active
	var controllerOneActive = false;

	
	//Constructor
	*new { arg window, synthsOne, xMappings, yMappings;
		^super.new.init(window, synthsOne, xMappings, yMappings);
		
	}

	init { arg window, synths, xMappings, yMappings;
		controllerWindow = window;
		
		//Initialize the arrays
		synthsOne = Array.newClear(16);
		controllerOneXMappings = Array.newClear(16);
		controllerOneYMappings = Array.newClear(16);

		synthsOne = synths;
		controllerOneXMappings = xMappings;
		controllerOneYMappings = yMappings;
	}


	//Load!
	loadGUI {arg xIndex=0, yIndex=0;

		var synthList;
		var xOffset = xIndex*55;
		var yOffset = yIndex*140;

		//SwingButton
		controllerOneButton = SCButton(controllerWindow,Rect(5+xOffset,140+yOffset,45,15));
		controllerOneButton.states = [    // array of states
			    [ "ON", Color.black,Color.red ], // first state
			    [ "OFF", Color.white, Color.black ],  // second state etc.
			];
		controllerOneButton.action = { arg button;
			var volume;
			volume = switch(button.value)
				{1}{0.0}
				{0}{0.3};
			synthsOne[currentSelectedSynth].set(\vol,volume);
			//volume.postln;	
		};

		//SwingRecordButton
		controllerOneButton2 = SCButton(controllerWindow,Rect(5+xOffset,165+yOffset,45,15));
		controllerOneButton2.states = [    // array of states
				[ "Button 1", Color.white, Color.black ],  // first state
				[ "Button 1", Color.black,Color.red ] // second state
			];
		controllerOneButton2.action = { arg button;
			var recSwitch, x;
			recSwitch = switch(button.value)
				{1}{1}
				{0}{0};
			switch(recSwitch)
				{1}{}
				{0}{};
			//synth1.set(\runValue,recSwitch, \triggerValue, recSwitch);
			recSwitch.postln;

		};
		
		//A list of synths you can choose from
		synthListPopUp = SCPopUpMenu( controllerWindow, Rect( 5+xOffset,185+yOffset, 45, 20 ));
		synthList = Array.new(synthsOne.size);
		synthsOne.do({arg synth;
			synthList.add(synth.defName);
		});
		synthListPopUp.items = synthList;
		synthListPopUp.action = { arg index;
			currentSelectedSynth = index.value
		};
			
		//A configurable slider
		sliderControl = SCSlider( controllerWindow, Rect( 5+xOffset,5+yOffset, 30, 125 ));
		sliderControl.background = controllerWindow.view.background;
		sliderControl.action = { arg slider;
			this.setY(slider.value);  
		};
		
		//Channel number
		chanLabel = SCStaticText( controllerWindow, Rect( 35+xOffset,120+yOffset, 20, 10 )).string_( xIndex+1 ).align_( \right );
		chanLabel.font = JFont( "SansSerif", 12 );
		chanLabel.stringColor = Color.white;

	}

	//Updates/sets the value of the value display
	updateValueDisplay {
		//controllerOneValueLabel.string = controllerOneSlid2d.x.asStringPrec(2) + "," + controllerOneSlid2d.y.asStringPrec(2);
	}
	
	//Sets the value of the slider
	setY { arg val;

			//Set all parameters of the synth mapped to Y
			controllerOneYMappings[currentSelectedSynth].do(
			{ arg item;
				synthsOne[currentSelectedSynth].set(item[0],(val*item[1])+item[2]);
			});

			sliderControl.value = val;
			this.updateValueDisplay();
	}
	
	//Sets "active" flag to enable change of parameter
	setActive { arg activeBool;
		controllerOneActive = activeBool;
	}

	//Sets the volume of the currently selected synth
	setVol {arg vol;
			synthsOne[currentSelectedSynth].set(\vol,vol);
			//sliderControl.value = vol;
	}
}
                                                                                                                                                                                                                                                                                                                                                                                     