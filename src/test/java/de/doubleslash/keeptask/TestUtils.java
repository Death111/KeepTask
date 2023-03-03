package de.doubleslash.keeptask;

import de.doubleslash.keeptask.model.WorkItem;

import java.util.ArrayList;
import java.util.Collections;

public class TestUtils {
    public static ArrayList<WorkItem> getArrayListReverted(ArrayList<WorkItem> arrayListToRevert) {
        ArrayList<WorkItem> revertedArrayList = (ArrayList<WorkItem>) arrayListToRevert.clone();
        Collections.reverse(revertedArrayList);
        return revertedArrayList;
    }
}
