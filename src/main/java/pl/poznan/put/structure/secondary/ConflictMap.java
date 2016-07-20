package pl.poznan.put.structure.secondary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConflictMap {

  private HashMap<Integer, HashMap<Integer, Boolean>> conflicts = new HashMap<Integer, HashMap<Integer, Boolean>>();


  // Create a basic 2DMap from which tells about conflicts. It can return if given Region have conflicts and give these conflicts
  public ConflictMap (Map<Integer, Region> idToRegion) {
    boolean conflict = false;

    for(int i = 0; i < idToRegion.size(); i++) {
      conflicts.put(i, new HashMap<Integer, Boolean>());
    }

    for(int i = 0; i < idToRegion.size(); i++) {
      for(int j = i+1; j < idToRegion.size(); j++) {
        conflict = isConflicting(idToRegion.get(i), idToRegion.get(j));
        if(conflict){
          conflicts.get(i).put(j, true);
          conflicts.get(j).put(i, true);
        }
      }
    }

    for(int i = 0; i < idToRegion.size(); i++) {
      if(conflicts.get(i).isEmpty()) conflicts.remove(i);
    }
  }

  // Return array of ids that are still conflicting
  public ArrayList<Integer> conflicting() {
    ArrayList<Integer> conflictsVector = new ArrayList<Integer>(conflicts.keySet());

    return conflictsVector;
  }

  // Check if given Regions are conflicting
  private boolean isConflicting(Region first, Region second) {
    int startFirst = first.start, endFirst = first.end, startSecond = second.start, endSecond = second.end;

    return (((startFirst < startSecond) && (endFirst < endSecond) && (startSecond < endFirst)) || ((startSecond < startFirst) && (endSecond < endFirst) && (startFirst < endSecond)));    
  }

  // Remove Region and all associated conflicts with it
  public void remove(int idToRemove) {
    int id;
    ArrayList<Integer> conflictingIDs = new ArrayList<Integer>(conflicts.get(idToRemove).keySet());
    for(int i = 0; i < conflictingIDs.size(); i++) {
      id = conflictingIDs.get(i);
      conflicts.get(id).remove(idToRemove);
      if(conflicts.get(id).isEmpty()) conflicts.remove(id);
    }
    conflicts.remove(idToRemove);
  }

  // Return all Regions (ID) that conflicts with given Region
  public ArrayList<Integer> conflictsWith(int id) {
    return new ArrayList<Integer>(conflicts.get(id).keySet());
  }
}
