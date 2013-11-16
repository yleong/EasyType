/*
 * Copyright (C) 2008-2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.softkeyboard;

import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodSubtype;

public class LatinKeyboardView extends KeyboardView {

    static final int KEYCODE_OPTIONS = -100;
	private static final String DEBUG_TAG = "KBView:";
	private static final double MIN_SWIPE_LEN = 30.0; //TODO change this!
	public enum Direction{N, NE, E, SE, S, SW, W, NW}
	
    public LatinKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LatinKeyboardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected boolean onLongPress(Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) {
            getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null);
            return true;
        } else {
            return super.onLongPress(key);
        }
    }
    private float prevX;
    private float prevY;
    
    private void savePoint(MotionEvent me){
    	this.prevX = me.getX();
    	this.prevY = me.getY();
    }
    
    private double distance(MotionEvent me){
    //distance from prevPoint to point from this MotionEvent
    	float currx = me.getX();
    	float curry = me.getY();
    	float dx, dy;
    	dx = (currx - prevX);
    	dy = (curry - prevY);
    	return Math.sqrt(dx*dx + dy*dy);    	
    }
    
    private Direction getDir(MotionEvent me){
    //calculate direction of the swipe	
    	//1. calculate angle (w.r.t. North) between 2 points
    	double angle = getAngle(me); //in radians?
    	//2  depending on the angle, determine what direction
    	// divide 360degrees or 2pi up into 8 equal intervals
    	// each interval has length pi/4
    	// so [-pi/8, pi/8) is North [pi/8, pi/4) is NE, and so on
    	double intervalLength = Math.PI / 4.0;

    	// equivalently, if I augment the angle += pi/8
    	// then i can use intervals [0, pi/4), [pi/4, pi/2) and so on
    	double augmentedAngle = (angle + Math.PI / 8.0 ) % (2*Math.PI); 
    	int interval = (int)(augmentedAngle / intervalLength);
    	Direction direction = Direction.S;
    	switch (interval){  			
    		case 0:
    			direction = Direction.N;
    			break;
    		case 1:
    			direction = Direction.NE;
    			break;
    		case 2:
    			direction = Direction.E;
    			break;
    		case 3:
    			direction = Direction.SE;
    			break;
    		case 4:
    			direction = Direction.S;
    			break;
    		case 5:
    			direction = Direction.SW;
    			break;
    		case 6:
    			direction = Direction.W;
    			break;
    		case 7:
    			direction = Direction.NW;
    			break;
    		default:
    			Log.d(DEBUG_TAG, "unable to getDir()!");
    	}
    	Log.d(DEBUG_TAG, "direction is: " + direction);
    	return direction;
    }
    
    private double getAngle(MotionEvent me){
    //returns angle wrt North (i.e. if pointing to North, angle = 0 radians)
    	float currx = me.getX();
    	float curry = me.getY();
    	float dx, dy;
    	dx = (currx - prevX);
    	dy = (curry - prevY);
    	double rawangle = Math.atan(Math.abs(dy/dx));
    	double angle = 0.0;
    	//Have to remember than (0,0) starts at top left corner
    	//for graphics coordinates! This is not standard cartesian system
    	//quadrant 1
    	if(dy < 0.0 && dx > 0.0){
    		Log.d(DEBUG_TAG, "quadrant1");
    		angle = Math.PI/2.0 - rawangle;    		
    	} 
    	//quadrant 2
    	else if (dx > 0.0 && dy > 0.0){
    		Log.d(DEBUG_TAG, "quadrant2");
    		angle = Math.PI/2.0 + rawangle;    		
    	} 
    	//quadrant 3
    	else if (dx < 0.0 && dy > 0.0) {
    		Log.d(DEBUG_TAG, "quadrant3");
    		angle = Math.PI * 3.0 / 2.0 - rawangle;    		
    	}
    	//quadrant 4
    	else{
    		Log.d(DEBUG_TAG, "quadrant4");
    		angle = Math.PI * 3.0/2.0 + rawangle;    		
    	}
    	Log.d(DEBUG_TAG, "angle is: " + angle);
    	return angle;
    }
    @Override
    public boolean onTouchEvent (MotionEvent me) {
    	
    	int action = me.getActionMasked();
    	
    	switch(action) {
         case (MotionEvent.ACTION_DOWN) :
             Log.d(DEBUG_TAG,"Action was DOWN");
         	 savePoint(me);
             return true;
         case (MotionEvent.ACTION_MOVE) :
             Log.d(DEBUG_TAG,"Action was MOVE");
             return true;
         case (MotionEvent.ACTION_UP) :
             Log.d(DEBUG_TAG,"Action was UP");
         	 double d = distance(me);
         	 if (d < MIN_SWIPE_LEN){
         		 Log.d(DEBUG_TAG, "Too short, parse as a tap");         		 
         	 } else {
         		Log.d(DEBUG_TAG, "Parse as a swipe");
         		 Direction dir = getDir(me);
         		 switch(dir){
         		 case N:
         			 break;
         		 case NE:
         			 break;
         		 case E:
         			 break;
         		 case SE:
         			 break;
         		 case S:
         			 break;
         		 case SW:
         			 break;
         		 case W:
         			 break;
         		 case NW:
         			 break;     
         		 default:
         			 break;
         		 }
         	 }
             return true;
         case (MotionEvent.ACTION_CANCEL) :
             Log.d(DEBUG_TAG,"Action was CANCEL");
             return true;
         case (MotionEvent.ACTION_OUTSIDE) :
             Log.d(DEBUG_TAG,"Movement occurred outside bounds " +
                     "of current screen element");
             return true;      
         default : 
             return super.onTouchEvent(me);
        }      
    }
    
    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final LatinKeyboard keyboard = (LatinKeyboard)getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
}
