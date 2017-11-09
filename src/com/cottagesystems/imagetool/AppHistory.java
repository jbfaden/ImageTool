/*
 * AppHistory.java
 *
 * Created on December 15, 2006, 7:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.cottagesystems.imagetool;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * TODO: not thread safe!
 * @author jbf
 */
public class AppHistory {
    
    LinkedList<Object> history;
    int sizeLimit;
    int currentState;
    
    /** Creates a new instance of AppHistory */
    public AppHistory( int sizeLimit ) {
        history= new LinkedList<Object>();
        currentState= -1;
        this.sizeLimit= sizeLimit;
    }
    
    public void pushState( Object state, String name ) {
        while ( history.size()>(currentState+1) ) {
            history.remove( history.size()-1 );
        }
        history.add( state );
        currentState++;
        
        while ( history.size()>sizeLimit ) {
            history.remove(0);
            currentState--;
        }
    }
    
    
    public Object undo() {
        if ( currentState<0 || history.size()==0 ) {
            return null;
        } else {
            Object result= history.get(currentState--);
            return result;
        }
    }
    
    public Object redo() {
        currentState++;
        if ( currentState==history.size() ) {
            currentState--;
            return null;
        } else {
            return history.get(currentState);
        }
    }
    
    public Object peek() {
        if ( currentState<0 || history.size()==0 ) {
            return null;
        } else {
            Object result= history.get(currentState);
            return result;
        }
    }
    
    public Iterator peekAll() {
        if ( currentState<0 || history.size()==0 ) {
            return null;
        } else {
            List l= history.subList(0,currentState);
            return l.iterator();
        }
    }
    
    public String toString() {
        return "size="+history.size()+" current="+currentState ;
    }
}
