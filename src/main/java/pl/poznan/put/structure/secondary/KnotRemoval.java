package pl.poznan.put.structure.secondary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class KnotRemoval {

  private static char[] openingBracket = {'(', '[', '{', '<', 'A', 'B', 'C', 'D', 'E', 'F',
                                          'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                                          'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
  private static char[] closingBracket = {')', ']', '}', '>', 'a', 'b', 'c', 'd', 'e', 'f',
                                          'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                                          'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
  // Dynamic Programming
  public static RNAStructure dynamicProgramming(RNAStructure structure) {
    // Create a deep copy of RNAStructure
    RNAStructure str = new RNAStructure (structure);
    str.DP = true;
    int bracket = -1;
    while(havePairs(str)) {
      ++bracket;
      Map<Integer, Integer> idMap = new HashMap<Integer, Integer>();
      ArrayList<Integer> connections = filterNotConnected(str.originalConnections, idMap);

      connections = addOuterRange(connections);
      int size = connections.size();

      // Create array of ranges and sort them
      ArrayList<Range> ranges = createRanges(connections);
      Collections.sort(ranges);

      // Initialize arrays needed for dynamic programming (it takes approximately 2/3 of program execution time)
      int[][] dpScore = new int[size][size];
      int[][] dpPosition = new int[size][size];
      for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
          dpPosition[i][j] = -1;

      // Fill dp array for results
      for(Range range : ranges) {
        int[] prefixesScore = dpScore[range.left + 1];
        int[] prefixesPosition = dpPosition[range.left + 1];

        for(int pos = range.left + 1; pos < range.right; pos++){
          int paired = connections.get(pos);

          if(pos == range.left + 1) continue;

          if(!range.contains(paired) || (paired > pos)) {
            if(prefixesScore[pos] < prefixesScore[pos-1]) {
              prefixesScore[pos] = prefixesScore[pos-1];
              prefixesPosition[pos] = prefixesPosition[pos-1];
            }
          } else if(paired < pos) {
            if(prefixesScore[pos-1] < (prefixesScore[paired-1] + dpScore[paired][pos]) && prefixesScore[pos] < (prefixesScore[paired-1] + dpScore[paired][pos])) {
              prefixesScore[pos] = prefixesScore[paired-1] + dpScore[paired][pos];
              prefixesPosition[pos] = pos;
            } else if(prefixesScore[pos] < prefixesScore[pos-1]){
              prefixesScore[pos] = prefixesScore[pos-1];
              prefixesPosition[pos] = prefixesPosition[pos-1];
            }
          }
        }
        dpScore[range.left][range.right] = 1 + prefixesScore[range.right-1];
        dpPosition[range.left][range.right]  = range.right;
      }


      ArrayList<Integer> correctConnections = new ArrayList<Integer>();
      appendCorrectConnections(1, size - 2, connections, dpPosition, correctConnections);

      ArrayList<Integer> nonConflicting = keepSelected(connections, correctConnections);
      nonConflicting = removeOuterRange(nonConflicting);

      str.correctConnections = mapBack(str.originalConnections, nonConflicting, idMap);

      updateDotBracket(str, bracket);
      removeUsedConnections(str);
    }

    return str;
  }

  // Elimination Gain
  public static RNAStructure eliminationGain(RNAStructure structure) {

    // Make a deep copy of processing structure
    RNAStructure str = new RNAStructure (structure);
    int bracket = -1;
    while(havePairs(str)) {
      ++bracket;

      // Initialize all variables for Elimination Gain function
      ArrayList<Region> regions = new ArrayList<Region>(createRegions(str));
      Map<Integer, Region> idToRegion = createMap(regions);
      ConflictMap cm = new ConflictMap(idToRegion);
      ArrayList<Integer> conflicts = cm.conflicting();
      ArrayList<Region> removed = new ArrayList<Region>();

      int idToRemove;

      // Main loop with function
      while (!conflicts.isEmpty()) {
        idToRemove = findMinGain(conflicts, cm, idToRegion);
        removed.add(idToRegion.get(idToRemove));
        regions.get(idToRemove).remove();
        idToRegion.remove(idToRemove);
        cm.remove(idToRemove);
        conflicts = cm.conflicting();
      }

      // Check if removed regions are still conflicting, if not add them back
      ArrayList<Region> addBackRegions = new ArrayList<Region>(addBackNonConflicting(idToRegion, removed));

      for(int i = 0; i < addBackRegions.size(); i++) {
        regions.add(addBackRegions.get(i));
      }

      // Change Regions to pairs
      str.connections = toPairs(regions);

      updateConnectionsFromPairs(str);
      updateDotBracket(str, bracket);
      removeUsedConnections(str);
      updateConnectionPairs(str);
    }

    return str; //new RNAStructure(structure.connections, structure.rnaSequence);
  }

  ///////////////////////////////////
  // Elimination Gain functions
  ///////////////////////////////////

  // Create Array of Regions from connections stored in RNAStructure
  private static ArrayList<Region> createRegions (RNAStructure structure) {

    ArrayList<Region> regions = new ArrayList<Region>();
    ArrayList<Pair> currentRegion = new ArrayList<Pair>();
    int regionID = -1;

    for (int i = 0; i < structure.connections.size(); i++) {
      if (currentRegion.isEmpty()) {
        currentRegion.add(structure.connections.get(i));
      }
      else {
        if (structure.connections.get(i).getFirst()  == currentRegion.get(currentRegion.size()-1).getFirst()+1 &&
            structure.connections.get(i).getSecond() == currentRegion.get(currentRegion.size()-1).getSecond()-1){
          currentRegion.add(structure.connections.get(i));
            }
        else {
          regionID++;
          regions.add(new Region(currentRegion, regionID));
          currentRegion.clear();
          currentRegion.add(structure.connections.get(i));
        }
      }
    }
    if(!currentRegion.isEmpty()){
      regionID++;
      regions.add(new Region(currentRegion, regionID));
    }

    return regions;
  }

  // Create map of Regions
  private static Map<Integer, Region> createMap(ArrayList<Region> regions){
    Map<Integer, Region> idToRegion = new HashMap<Integer, Region>();

    for(int i =  0; i < regions.size(); i++){
      idToRegion.put(regions.get(i).ID, regions.get(i));
    }

    return idToRegion;
  }

  public static ArrayList<Pair> toPairs(ArrayList<Region> regions) {
    ArrayList<Pair> connections = new ArrayList<Pair>();
    int start, end;



    for(int i = 0; i < regions.size(); i++) {
      if(!regions.get(i).isRemoved()) {
        start = regions.get(i).start;
        end = regions.get(i).end;
        for(int j = 0; j < regions.get(i).length; j++) {
          connections.add(new Pair(start + j, end - j));
        }
      }
    }

    return connections;
  }

  // Rewritten find_min_gain function from PyCogent-1.5.3
  private static Integer findMinGain(ArrayList<Integer> conflicts, ConflictMap cm, Map<Integer, Region> idToRegion){
    int id, regionLength, conflictLength, lenDiff, lenDiffMin = 2000000000, noc, nocMax = -100, start, startMax = -100;
    ArrayList<Integer> conflictsWith = new ArrayList<Integer>();
    SortedMap<Integer, ArrayList<Integer> > lenDiffs = new TreeMap<Integer, ArrayList<Integer> >();
    SortedMap<Integer, ArrayList<Integer> > numberOfConflicts = new TreeMap<Integer, ArrayList<Integer> >();
    SortedMap<Integer, Integer> startValues = new TreeMap<Integer, Integer>();

    for(int i = 0; i < conflicts.size(); i++) {
      id = conflicts.get(i);
      regionLength = idToRegion.get(id).length;
      conflictLength = 0;
      conflictsWith = cm.conflictsWith(id);
      for(int j = 0; j < conflictsWith.size(); j++) {
        conflictLength += idToRegion.get(conflictsWith.get(j)).length;
      }
      lenDiff = regionLength - conflictLength;
      if(!lenDiffs.containsKey(lenDiff))
        lenDiffs.put(lenDiff, new ArrayList<Integer>());
      lenDiffs.get(lenDiff).add(id);
      lenDiffMin = Math.min(lenDiff, lenDiffMin);
    }

    ArrayList<Integer> minIDs = new ArrayList<Integer>(lenDiffs.get(lenDiffMin));
    if(minIDs.size() == 1) {
      return minIDs.get(0);
    } else {
      for(int i = 0; i < minIDs.size(); i++) {
        id = minIDs.get(i);
        noc = cm.conflictsWith(id).size();
        if(!numberOfConflicts.containsKey(noc))
          numberOfConflicts.put(noc, new ArrayList<Integer>());
        numberOfConflicts.get(noc).add(id);
        nocMax = Math.max(nocMax, noc);
      }
      ArrayList<Integer> maxIDs = new ArrayList<Integer>(numberOfConflicts.get(nocMax));

      if(maxIDs.size() == 1) {
        return maxIDs.get(0);
      } else {
        for(int i = 0; i < minIDs.size(); i++) {
          start = idToRegion.get(minIDs.get(i)).start;
          startValues.put(start, minIDs.get(i));
          startMax = Math.max(startMax, start);
        }
        return startValues.get(startMax);
      }
    }
  }

  // Create Array of Regions from connections stored in RNAStructure
  public ArrayList<Region> Regions (RNAStructure structure) {

    ArrayList<Region> regions = new ArrayList<Region>();
    ArrayList<Pair> currentRegion = new ArrayList<Pair>();
    int regionID = -1;

    for (int i = 0; i < structure.connections.size(); i++) {
      if (currentRegion.isEmpty()) {
        currentRegion.add(structure.connections.get(i));
      }
      else {
        if (structure.connections.get(i).getFirst()  == currentRegion.get(currentRegion.size()-1).getFirst()+1 &&
            structure.connections.get(i).getSecond() == currentRegion.get(currentRegion.size()-1).getSecond()-1){
          currentRegion.add(structure.connections.get(i));
            }
        else {
          regionID++;
          regions.add(new Region(currentRegion, regionID));
          currentRegion.clear();
          currentRegion.add(structure.connections.get(i));
        }
      }
    }
    if(!currentRegion.isEmpty()){
      regionID++;
      regions.add(new Region(currentRegion, regionID));
    }

    return regions;
  }

  // Returns Array of Regions that were removed but can be added back because the are not conflicting anymore
  private static ArrayList<Region> addBackNonConflicting(Map<Integer, Region> idToRegion, ArrayList<Region> removed) {
    ArrayList<Region> addBack = new ArrayList<Region>();
    SortedMap<Integer, Integer> order = new TreeMap<Integer, Integer>();
    Map<Integer, Region> idToRemovedRegion = createMap(removed);
    boolean added = true, conflicting;

    for(int i = 0; i < removed.size(); i++) {
      order.put(removed.get(i).start, removed.get(i).ID);
    }

    while(added) {
      added = false;
      for(Integer start: order.keySet()) {
        conflicting = false;
        for(Region pr2: idToRegion.values()) {
          if(isConflicting(idToRemovedRegion.get(order.get(start)), pr2)) {
            conflicting = true;
            break;
          }
        }
        if(!conflicting) {
          idToRegion.put(order.get(start), idToRemovedRegion.get(order.get(start)));
          idToRemovedRegion.get(order.get(start)).restore();
          addBack.add(idToRemovedRegion.get(order.get(start)));
          idToRemovedRegion.remove(order.get(start));
          for(int k = 0; k < removed.size(); k++) {
            if(removed.get(k).ID == order.get(start))
              removed.remove(k);
          }
          order.remove(start);
          added = true;
          break;
        }
      }
    }
    return addBack;
  }

  // Check if given Regions are conflicting
  private static boolean isConflicting(Region first, Region second) {
    int startFirst = first.start, endFirst = first.end, startSecond = second.start, endSecond = second.end;

    return (((startFirst < startSecond) && (endFirst < endSecond) && (startSecond < endFirst)) || ((startSecond < startFirst) && (endSecond < endFirst) && (startFirst < endSecond)));    
  }

  // Update correct connections from pairs
  private static void updateConnectionsFromPairs(RNAStructure str) {
    ArrayList<Integer> correctConnections = new ArrayList<Integer>();
    int size = str.rnaSequence.size();
    int[] temp = new int[size+2];

    for(int i = 0; i < str.connections.size(); i++) {
      temp[str.connections.get(i).getFirst()] = str.connections.get(i).getSecond();
      temp[str.connections.get(i).getSecond()] = str.connections.get(i).getFirst();
    }
    for(int i = 0; i < str.rnaSequence.size(); i++)
      correctConnections.add(temp[i+1]);
    str.correctConnections = correctConnections;
  }

  /////////////////////////////////////
  // Dynamic Programming functions
  /////////////////////////////////////

  // Create idMap to access connections and Array of connections
  private static ArrayList<Integer> filterNotConnected(ArrayList<Integer> originalConnections, Map<Integer, Integer> idMap) {
    ArrayList<Integer> filtered = new ArrayList<Integer>();
    int id = 0;
    Map<Integer, Integer> originalToFiltered = new HashMap<Integer, Integer>();
    for(int i = 0; i < originalConnections.size(); i++) {
      if(originalConnections.get(i) != -1) {
        originalToFiltered.put(i, id);
        idMap.put(id++, i);
        filtered.add(originalConnections.get(i));
      }
    }

    for(int i = 0; i < filtered.size(); i++) {
      int pos = filtered.get(i);
      filtered.set(i, originalToFiltered.get(pos));
    }
    return filtered;
  }

  // Create one range that contains all of the other
  private static ArrayList<Integer> addOuterRange(ArrayList<Integer> connections) {
    ArrayList<Integer> res = new ArrayList<Integer>();

    res.add(connections.size() + 1);
    for(int i = 0; i < connections.size(); i++) {
      res.add(connections.get(i) + 1);
    }
    res.add(0);

    return res;
  }

  // Create Ranges from given connections
  private static ArrayList<Range> createRanges(ArrayList<Integer> connections) {
    ArrayList<Range> res = new ArrayList<Range>();

    for(int i = 0; i < connections.size(); i++) {
      if(i < connections.get(i)) {
        res.add(new Range(i, connections.get(i)));
      }
    }
    return res;
  }

  // If connections is correct add them to final connections array (make final correct connections)
  private static void appendCorrectConnections(int a, int b, ArrayList<Integer> connections, int[][] dpPosition, ArrayList<Integer> appendTo) {
    if(a >= b) return;
    if(dpPosition[a][b] != -1) {
      int right = dpPosition[a][b];
      int left = connections.get(right);

      appendTo.add(left);
      appendTo.add(right);

      appendCorrectConnections(left + 1, right -1, connections, dpPosition, appendTo);
      appendCorrectConnections(a, left - 1, connections, dpPosition, appendTo);
    }
  }

  // Tell which connections should be as the final ones
  private static ArrayList<Integer> keepSelected(ArrayList<Integer> connections, ArrayList<Integer> toKeep) {
    boolean[] shouldKeep = new boolean[connections.size()];
    Arrays.fill(shouldKeep, false);
    for(int c : toKeep) shouldKeep[c] = true;
    ArrayList<Integer> res = new ArrayList<Integer>(connections);
    for(int i = 0; i < res.size(); i++) {
      if(!shouldKeep[i]) {
        res.set(i, -1);
      }
    }
    return res;
  }

  // Remove helping range that contains everything
  private static ArrayList<Integer> removeOuterRange(ArrayList<Integer> connections) {
    ArrayList<Integer> res = new ArrayList<Integer>();

    for(int i = 1; i < connections.size() - 1; i++) {
      if(connections.get(i) == -1) {
        res.add(-1);
      } else {
        res.add(connections.get(i) - 1);
      }
    }

    return res;
  }

  // Build final connections using idMap and filtered connections
  private static ArrayList<Integer> mapBack(ArrayList<Integer> original, ArrayList<Integer> filtered, Map<Integer, Integer> idMap) {
    ArrayList<Integer> result = new ArrayList<Integer>(original);

    for(int i = 0; i < filtered.size(); i++) {
      if(filtered.get(i) == -1) {
        result.set(idMap.get(i), -1);
      }
    }

    return result;
  }

  /////////////////////////////////////
  // Misc functions
  /////////////////////////////////////

  private static boolean havePairs(RNAStructure structure) {
    int isDP = 0;
    if (structure.DP) isDP = -1;
    for (int i : structure.originalConnections)
      if (i != isDP) return true;
    return false;
  }

  private static void updateDotBracket(RNAStructure str, int bracket) {
    int isDP = 0;
    if (str.DP) isDP = -1;
    for (int i = 0; i < str.correctConnections.size(); i++) {
      if (str.correctConnections.get(i) > i && str.correctConnections.get(i) != isDP) str.DotBracket.set(i, openingBracket[bracket]);
      else if (str.correctConnections.get(i) <= i && str.correctConnections.get(i) != isDP) str.DotBracket.set(i, closingBracket[bracket]);
    }
  }

  private static void removeUsedConnections(RNAStructure str) {
    int isDP = 1;
    if (str.DP) isDP = 0;
    for (int i = 0; i < str.correctConnections.size(); i++) {
      if (str.originalConnections.get(i) + isDP == str.correctConnections.get(i)) {
        str.originalConnections.set(i, isDP-1);
        str.correctConnections.set(i, isDP-1);
      }
    }
  }

  private static void updateConnectionPairs(RNAStructure str) {
    str.connections.clear();
    for (int i = 0; i < str.originalConnections.size(); i++) {
      if(i < str.originalConnections.get(i))
        str.connections.add(new Pair(i+1, str.originalConnections.get(i)+1));
    }
  }
}
