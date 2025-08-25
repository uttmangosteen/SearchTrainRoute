package io.github.uttmangosteen;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RailwayGraph {
    Map<Integer, Set<Rail>> stationMap;

    RailwayGraph(Map<Integer, Set<Rail>> stationMap) {
        this.stationMap = stationMap;
    }

    public void addRail(int stationID_1, int stationID_2, Double distance) {
        addStation(stationID_1);
        addStation(stationID_2);
        //もとから長い経路がある場合は無視(最短経路探すときは符号逆)
        if (stationMap.get(stationID_1).stream().anyMatch(rail ->
                rail.destinationStationID == stationID_2 &&rail.distance >= distance)) return;
        stationMap.get(stationID_1).add(new Rail(stationID_2, distance));
        stationMap.get(stationID_2).add(new Rail(stationID_1, distance));
    }

    void addStation(int stationID) {
        if (stationMap.containsKey(stationID)) return;
        stationMap.put(stationID, new HashSet<>());
    }

    public void printGraph() {
        for (int key : stationMap.keySet()) {
            System.out.println(key + " -> " + stationMap.get(key));
        }
    }
}

class Rail {
    int destinationStationID;
    Double distance;

    Rail(int destinationStationID, Double distance) {
        this.destinationStationID = destinationStationID;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "(" + destinationStationID + ", " + distance + ")";
    }

}