/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.selection;

/**
 *
 * @author upachler
 */
public interface SelectionSetListener {

    public void selectionAdded(SelectionSet selectionSet, Selection[] selections);

    public void selectionRemoved(SelectionSet selectionSet, Selection[] selections);

}
