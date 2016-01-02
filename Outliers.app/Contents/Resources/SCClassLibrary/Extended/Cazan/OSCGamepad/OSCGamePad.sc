/* 

OSCGamePad
Scott Cazan

A class for use with two PS/2-style gamepad controllers in conjunction with HIDserver from ixi software and SwingOSC... needs to be updated for the newer GUI classes.

Numbered buttons control the instrument banks. Outer joysticks control volume at the moment (need to be able to map those) and inner joysticks are mappable.

Synths can be hot-swapped and multiple synths can be assigned on the fly to the three banks per joystick.

*/

OSCGamePad {

	var w, w2,b2;
	var controllerWindow;
	var joystickOSC;
	
	//Two arrays to contain the widgets that will control each synth. The gamepads interact with the widgets, not the synths.
	var controlWidgetsOne, controlWidgetsTwo;

	//Parameters for the GamePads. THESE SHOULD BE SET BEFORE LOAD.
	//Pass in a 2D array of synths. One array of synths for each of the three controller-selectable instruments.
	var <>synthsOne;
	var <>synthsTwo;
	//Pass in a 3D array of controller mappings. Why 3D? First dimension for each controller-selectable instrument,
	//	second dimension for each synth that is accesible to that button (selectable from the Widget window,
	//	and third dimension so you can have multiple parameters mapped to the movement of X or Y.
	var <>controllerOneXMappings, <>controllerOneYMappings, <>controllerOneButtonOneMappings, <>controllerOneButtonTwoMappings;
	var <>controllerTwoXMappings, <>controllerTwoYMappings, <>controllerTwoButtonOneMappings, <>controllerTwoButtonTwoMappings;

	//A variable representing the current instrument "patch"
	var currentSelectedInstrumentOne = 0;
	var currentSelectedInstrumentTwo = 0;

	//Variables for whether the parameter joystick is active
	var controllerOneActive = false;
	var controllerTwoActive = false;




	*new {
		^super.new.init()
	}

	init {
		//Initialize all the arrays
		synthsOne = Array.newClear(3);
		synthsTwo = Array.newClear(3);

		controllerOneXMappings = Array.newClear(3);
		controllerOneYMappings = Array.newClear(3);
		controllerTwoXMappings = Array.newClear(3);
		controllerTwoYMappings = Array.newClear(3);
		controllerOneButtonOneMappings = Array.newClear(3);
		controllerOneButtonTwoMappings = Array.newClear(3);
		controllerTwoButtonOneMappings = Array.newClear(3);
		controllerTwoButtonTwoMappings = Array.newClear(3);
		controlWidgetsOne = Array.newClear(3);
		controlWidgetsTwo = Array.newClear(3);
		
	}

	load {
		this.loadGamePadOSC;
		"OSC Loaded".postln;
		this.loadGUI;
	}
	
	loadGUI {

		//SC Window
		controllerWindow=SCWindow("Gamepads", Rect(50,50,895,450));
		controllerWindow.view.background = Color.gray(0.5,1);

		this.loadControllerOneGUI;
		this.loadControllerTwoGUI;

		controllerWindow.front;
		controllerWindow.onClose={
			
			joystickOSC.remove;
		};
	}

	//Controller One GUI
	loadControllerOneGUI {

		//For each member of synths, load an XYWidget for it
		synthsOne.do({ arg item, i;
			var widget;
			if(item==nil,{},{
				widget = XYControlWidget.new(controllerWindow, synthsOne[i], controllerOneXMappings[i], controllerOneYMappings[i], controllerOneButtonOneMappings[i], controllerOneButtonTwoMappings[i]);
				widget.loadGUI(i,0);
				controlWidgetsOne.put(i, widget);
			});
		});

	}

	//Controller Two GUI
	loadControllerTwoGUI {

		//For each member of synths, load an XYWidget for it
		synthsTwo.do({ arg item, i;
			var widget;
			if(item==nil,{},{
				widget = XYControlWidget.new(controllerWindow, synthsTwo[i], controllerTwoXMappings[i], controllerTwoYMappings[i], controllerTwoButtonOneMappings[i], controllerTwoButtonTwoMappings[i]);
				widget.loadGUI(i,1);
				controlWidgetsTwo.put(i, widget);
			});
		});

	}

	loadGamePadOSC {

		//Gamepad Mappings -----------------------------------------------------------

		joystickOSC = OSCresponderNode ( nil , '/hid' , { arg time, responder, msg;
				//msg.postln; 
				var messageXOne, messageYOne, volumeOne, messageXTwo, messageYTwo, volumeTwo;
				//Determine which joystick you are using
				
				//Joystick One
				if (msg[2]==0,{
					//Joysticks
					if (msg[1]=='axismotion',{
							//X-Axis
							if(msg[3]==2,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentOne != 3,{
										if(controllerOneActive, {
											messageXOne = (msg[4]+1)/2;
											
											controlWidgetsOne[currentSelectedInstrumentOne].setX(messageXOne);
										});
									});
								}
							);
							//Y-Axis
							if(msg[3]==3,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentOne != 3,{
										if(controllerOneActive, {
											messageYOne = (msg[4]+1)/2;
											controlWidgetsOne[currentSelectedInstrumentOne].setY(messageYOne);
										});
									});
								}
							);
							//X-Axis for volume stick
							if(msg[3]==0,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentOne != 3,{
										volumeOne;
										volumeOne = msg[4].cubed;

										if(msg[4] <= 0.0, {volumeOne = 0});
										//volumeOne.postln;
										controlWidgetsOne[currentSelectedInstrumentOne].setVol(volumeOne);
									});
								}
							);
						}
					);

					//Buttons
					if (msg[1]=='button',{
							if(msg[3]==0 && msg[4]==1,
								{
									currentSelectedInstrumentOne = 0;
									//synthsOne[0].defName.postln;
								}
							);
							if(msg[3]==1 && msg[4]==1,
								{
									currentSelectedInstrumentOne = 1;
									//synthsOne[1].defName.postln;
								}
							);
							if(msg[3]==2 && msg[4]==1,
								{
									currentSelectedInstrumentOne = 2;
									//synthsOne[2].defName.postln;
								}
							);
							if(msg[3]==3 && msg[4]==1,
								{
									currentSelectedInstrumentOne = 3;
									"Empty".postln;
								}
							);

							//Top buttons
							if(msg[3]==4 && msg[4]==1,
								{
									controllerOneActive = true;
								}
							);
							if(msg[3]==4 && msg[4]==0,
								{
									controllerOneActive = false;
								}
							);
						}
					);
				});

				//Joystick Two
				if (msg[2]==1,{
					//Joysticks
					if (msg[1]=='axismotion',{
							if(msg[3]==0,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentTwo != 3,{
										if(controllerTwoActive, {
											messageXTwo = (msg[4]+1)/2;
											controlWidgetsTwo[currentSelectedInstrumentTwo].setX(messageXTwo);
										});
									});
								}
							);
							if(msg[3]==1,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentTwo != 3,{
										if(controllerTwoActive, {
											messageYTwo = (msg[4]+1)/2;
											controlWidgetsTwo[currentSelectedInstrumentTwo].setY(messageYTwo);
										});
									});
								}
							);
							if(msg[3]==2,
								{
									//I'm using array pos. 3 as a controller "disconnect",
									//so only do something if anything other than 3 (btn 4)
									//is selected.
									if(currentSelectedInstrumentTwo != 3,{
										volumeTwo = (msg[4]*(-1)).cubed;

										if(volumeTwo <= 0.0,{volumeTwo=0});
										//volume.postln;
										controlWidgetsTwo[currentSelectedInstrumentTwo].setVol(volumeTwo);
									});
										
								}
							);
						}
					);

					//Buttons
					if (msg[1]=='button',{
							if(msg[3]==0 && msg[4]==1,
								{
									currentSelectedInstrumentTwo = 0;
									//synthsTwo[0].defName.postln;
								}
							);
							if(msg[3]==1 && msg[4]==1,
								{
									currentSelectedInstrumentTwo = 1;
									//synthsTwo[1].defName.postln;
								}
							);
							if(msg[3]==2 && msg[4]==1,
								{
									currentSelectedInstrumentTwo = 2;
									//synthsTwo[2].defName.postln;
								}
							);
							if(msg[3]==3 && msg[4]==1,
								{
									currentSelectedInstrumentTwo = 3;
									"Empty".postln;
								}
							);

							//Top buttons
							if(msg[3]==5 && msg[4]==1,
								{
									controllerTwoActive = true;
								}
							);
							if(msg[3]==5 && msg[4]==0,
								{
									controllerTwoActive = false;
								}
							);
						}
					);
				});
			}).add;

	//END Gamepad Mappings-------------------------------------------------
	}
}                                                                                                                                                                                                                                                                                                     