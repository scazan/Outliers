// A control widget for use with anything. Can be given a synth and the mappings for the synth.

XYControlWidget : SCView {

	var w, controllerOneSlid2d;
	var controllerWindow;
	var controllerOneSlid2d, <>buttonOne, <>buttonTwo, sliderControl,
		controllerOneValueLabel;
	var synthListPopUp;

	//Parameters for the GamePad. THESE SHOULD BE SET BEFORE LOAD.
	var <>synthsOne;
	var <>controllerOneXMappings, <>controllerOneYMappings, <>controllerButtonOneMappings, <>controllerButtonTwoMappings;

	//A variable representing the current instrument "patch"
	var currentSelectedInstrumentOne = 0;
	var currentSelectedSynth = 0;

	//Variable for whether the parameter joystick is active
	var controllerOneActive = false;

	
	//Constructor
	*new { arg window, synthsOne, xMappings, yMappings, buttonOneMappings, buttonTwoMappings;
		^super.new.init(window, synthsOne, xMappings, yMappings, buttonOneMappings, buttonTwoMappings);
		
	}

	init { arg window, synths, xMappings, yMappings, buttonOneMappings, buttonTwoMappings;
		controllerWindow = window;
		
		//Initialize the arrays
		synthsOne = Array.newClear(3);
		controllerOneXMappings = Array.newClear(3);
		controllerOneYMappings = Array.newClear(3);
		controllerButtonOneMappings = Array.newClear(3);
		controllerButtonTwoMappings = Array.newClear(3);

		synthsOne = synths;
		controllerOneXMappings = xMappings;
		controllerOneYMappings = yMappings;
		controllerButtonOneMappings = buttonOneMappings;
		controllerButtonTwoMappings = buttonTwoMappings;
	}


	//Load!
	loadGUI {arg xOffset=0, yOffset=0;

		var synthList;
		xOffset = xOffset*295;
		yOffset = yOffset*220;

		//SwingButton
		buttonOne = SCButton(controllerWindow,Rect(210+xOffset,5+yOffset,75,25));
		buttonOne.states = [    // array of states
			    [ "ON", Color.black,Color.red ], // first state
			    [ "OFF", Color.white, Color.black ],  // second state etc.
			];
		buttonOne.action = { arg button;
			switch(button.value)
				{1}{
					//Set all parameters of the synth mapped to the button
					//*mapping*.put(widgetIndex, symbolForParameter, valueForParameter);
					controllerButtonOneMappings[currentSelectedInstrumentOne].do(
					{ arg item;
						synthsOne[currentSelectedSynth].set(item[0], item[2]);
					});
				}
				{0}{
					//Set all parameters of the synth mapped to the button
					controllerButtonOneMappings[currentSelectedInstrumentOne].do(
					{ arg item;
						synthsOne[currentSelectedSynth].set(item[0], item[1]);
					});
				};
			//synthsOne[currentSelectedSynth].set(\vol,volume);
			//volume.postln;	
		};

		//SwingRecordButton
		buttonTwo = SCButton(controllerWindow,Rect(210+xOffset,40+yOffset,75,25));
		buttonTwo.states = [    // array of states
				[ "ON", Color.white, Color.black ],  // first state
				[ "OFF", Color.black,Color.red ] // second state
			];
		buttonTwo.action = { arg button;
			switch(button.value)
				{1}{
					//Set all parameters of the synth mapped to the button
					//*mapping*.put(widgetIndex, symbolForParameter, valueForParameter);
					controllerButtonTwoMappings[currentSelectedInstrumentOne].do(
					{ arg item;
						synthsOne[currentSelectedSynth].set(item[0], item[2]);
					});
				}
				{0}{
					//Set all parameters of the synth mapped to the button
					controllerButtonTwoMappings[currentSelectedInstrumentOne].do(
					{ arg item;
						synthsOne[currentSelectedSynth].set(item[0], item[1]);
					});
				};
			//synth1.set(\runValue,recSwitch, \triggerValue, recSwitch);
			//recSwitch.postln;

		};
		
		//A display for the controls current value
		controllerOneValueLabel = SCTextField(controllerWindow,Rect(210+xOffset,75+yOffset,75,20));
		controllerOneValueLabel.enabled = false;
		controllerOneValueLabel.stringColor = Color.black;
		controllerOneValueLabel.boxColor = Color.gray(1.0,0.5);
		
		//A list of synths you can choose from
		synthListPopUp = SCPopUpMenu( controllerWindow, Rect( 210+xOffset,105+yOffset, 75, 20 ));
		synthList = Array.new(synthsOne.size);
		synthsOne.do({arg synth;
			//if(synth.defName.isNil;,
				//{
				//synthList.add("Routine");
				//},
			//	{
				//synthList.add(synth.defName);
				//}
			//);
		});
		synthListPopUp.items = synthList;
		synthListPopUp.action = { arg index;
			currentSelectedSynth = index.value
		};
			
		//A configurable slider (right now I'm mapping it to volume explicitly)
		sliderControl = SCSlider( controllerWindow, Rect( 200+xOffset,130+yOffset, 30, 70 ));
		sliderControl.background = controllerWindow.view.background;
		sliderControl.action = { arg slider;
			this.setVol(slider.value);  
		};

		//2D Slider
		controllerOneSlid2d= SC2DSlider(controllerWindow,Rect(5+xOffset,5+yOffset,200,200));
		controllerOneSlid2d.action_({  
			//Set all parameters of the synth mapped to X
			controllerOneXMappings[currentSelectedInstrumentOne].do(
			{ arg item;
				synthsOne[currentSelectedSynth].set(item[0],(controllerOneSlid2d.x*item[1])+item[2]);
			});
			//Set all parameters of the synth mapped to Y
			controllerOneYMappings[currentSelectedInstrumentOne].do(
			{ arg item;
				synthsOne[currentSelectedSynth].set(item[0],(controllerOneSlid2d.y*item[1])+item[2]);
			});

			//[controllerOneSlid2d.x, controllerOneSlid2d.y].postln;
			this.updateValueDisplay();
		});

	}

	//Updates/sets the value of the value display
	updateValueDisplay {
		controllerOneValueLabel.string = controllerOneSlid2d.x.asStringPrec(2) + "," + controllerOneSlid2d.y.asStringPrec(2);
	}

	//Sets the value of the 2D slider
	setX { arg val;

			//Set all parameters of the synth mapped to X
			controllerOneXMappings[currentSelectedSynth].do(
			{ arg item;
				synthsOne[currentSelectedSynth].set(item[0],(val*item[1])+item[2]);
			});

			controllerOneSlid2d.x_(val);
			this.updateValueDisplay();
	}

	//Sets the value of the 2D slider
	setY { arg val;

			//Set all parameters of the synth mapped to Y
			controllerOneYMappings[currentSelectedSynth].do(
			{ arg item;
				synthsOne[currentSelectedSynth].set(item[0],(val*item[1])+item[2]);
			});

			controllerOneSlid2d.y_(val);
			this.updateValueDisplay();
	}
	
	//Sets "active" flag to enable change of parameter
	setActive { arg activeBool;
		controllerOneActive = activeBool;
	}

	//Sets the volume of the currently selected synth
	setVol {arg vol;
			synthsOne[currentSelectedSynth].set(\vol,vol);
			sliderControl.value = vol;
	}
}
                                                                                                                                                                                                                                                                                                                                                                                  