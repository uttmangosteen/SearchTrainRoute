package io.github.uttmangosteen;

import java.util.*;

public class RailwayGraph {
    ArrayList<Integer> stationIDList;
    ArrayList<Rail> railList;

    RailwayGraph(ArrayList<Integer> stationIDList, ArrayList<Rail> railList) {
        this.stationIDList = stationIDList;
        this.railList = railList;
    }

    void addStation(int stationID) {
        if (stationIDList.contains(stationID)) return;
        stationIDList.add(stationID);
        stationIDList.sort(Comparator.naturalOrder());
    }

    public void addRail(int stationID_1, int stationID_2, Double distance) {
        addStation(stationID_1);
        addStation(stationID_2);
        if (stationID_1 > stationID_2) {
            int t = stationID_1;
            stationID_1 = stationID_2;
            stationID_2 = t;
        }
        railList.add(new Rail(stationID_1, stationID_2, distance));
        // stationID_1 → stationID_2 → distance の順でソート(意味はないが見やすさのため)
        railList.sort(Comparator
                .comparingInt((Rail r) -> r.stationID_1)
                .thenComparingInt(r -> r.stationID_2)
                .thenComparingDouble(r -> r.distance));
    }

    //グラフの状況を見る(デバック用)
    public void printGraph() {
        System.out.println("======グラフの状況======");
        System.out.println("駅ID一覧:");
        System.out.println(stationIDList.toString());
        System.out.println("路線一覧:");
        for (Rail rail : railList) {
            System.out.println(rail.stationID_1 + " <-> " + rail.stationID_2 + " (距離: " + rail.distance + ")");
        }
        System.out.println("========================");
    }
}

class Rail {
    int stationID_1;
    int stationID_2;
    Double distance;

    Rail(int stationID_1, int stationID_2, Double distance) {
        this.stationID_1 = stationID_1;
        this.stationID_2 = stationID_2;
        this.distance = distance;
    }
}