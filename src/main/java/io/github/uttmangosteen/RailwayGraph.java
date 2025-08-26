package io.github.uttmangosteen;

import java.util.*;

public class RailwayGraph {
    Map<Integer, List<Rail>> stationMap;

    RailwayGraph(Map<Integer, List<Rail>> stationMap) {
        this.stationMap = stationMap;
    }

    public void addRail(int stationID_1, int stationID_2, Double distance) {
        addStation(stationID_1);
        addStation(stationID_2);
        stationMap.get(stationID_1).add(new Rail(stationID_2, distance));
        stationMap.get(stationID_2).add(new Rail(stationID_1, distance));
    }

    void addStation(int stationID) {
        if (stationMap.containsKey(stationID)) return;
        stationMap.put(stationID, new ArrayList<>());
    }

    //グラフの状況を見る(デバック用)
    public void printGraph() {
        System.out.println("==========グラフの状況==========");
        for (int key : stationMap.keySet()) System.out.println(key + " -> " + stationMap.get(key));
        System.out.println("================================");
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