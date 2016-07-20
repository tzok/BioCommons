package pl.poznan.put.structure.secondary;

import java.util.ArrayList;

// Class that contains compressed connections
public class Region {

  private ArrayList<Pair> contain = null;
  public int ID, start, end, length;
  private boolean isRemoved;

  // Create a region
  public Region(ArrayList<Pair> currentRegions, int regionID){
    contain = new ArrayList<Pair>(currentRegions);
    start = currentRegions.get(0).getFirst();
    end = currentRegions.get(0).getSecond();
    length = currentRegions.size();
    ID = regionID;
    isRemoved = false;
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

  public ArrayList<Pair> getPairs(){
    return contain;
  }

  public boolean isRemoved(){
    return isRemoved;
  }

  public void remove(){
    isRemoved = true;
  }

  public void restore(){
    isRemoved = false;
  }

}
