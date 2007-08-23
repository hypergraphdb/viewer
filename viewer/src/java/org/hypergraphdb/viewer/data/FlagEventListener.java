//---------------------------------------------------------------------------
//  $Revision: 1.1 $ 
//  $Date: 2005/12/25 01:22:41 $
//  $Author: bobo $
//---------------------------------------------------------------------------
package org.hypergraphdb.viewer.data;
//---------------------------------------------------------------------------
/**
 * Listener for FlagEvents fired by a FlagFilter object.
 */
public interface FlagEventListener {
    void onFlagEvent(FlagEvent event);
}

