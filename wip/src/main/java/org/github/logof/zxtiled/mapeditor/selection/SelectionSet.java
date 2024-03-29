/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.github.logof.zxtiled.mapeditor.selection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * @author upachler
 */
public class SelectionSet implements Iterable<Selection> {
    Set<Selection> set = new HashSet<>();
    private final Vector<SelectionSetListener> listeners = new Vector<>();

    public void addSelectionListener(SelectionSetListener l) {
        listeners.add(l);
    }

    public void removeSelectionListener(SelectionSetListener l) {
        listeners.remove(l);
    }

    /**
     * Adds a Selection to the selection set.
     *
     * @param selection
     */
    void addSelection(Selection selection) {
        addSelection(new Selection[]{selection});
    }

    void addSelection(Selection[] selections) {
        Collections.addAll(set, selections);
        fireSelectionAdded(selections);
    }

    void clearSelection() {
        if (set.isEmpty()) {
            return;
        }
        Selection[] a = set.toArray(new Selection[set.size()]);
        set.clear();
        fireSelectionRemoved(a);
    }

    /**
     * clears the selection set and adds the given selection
     */
    void setSelection(Selection selection) {
        setSelection(new Selection[]{selection});
    }

    void setSelection(Selection[] selections) {
        clearSelection();
        addSelection(selections);
    }

    public Iterator<Selection> iterator() {
        return set.iterator();
    }

    private void fireSelectionAdded(Selection[] selections) {
        for (SelectionSetListener l : listeners)
            l.selectionAdded(this, selections);
    }

    private void fireSelectionRemoved(Selection[] a) {
        for (SelectionSetListener l : listeners)
            l.selectionRemoved(this, a);
    }
}
