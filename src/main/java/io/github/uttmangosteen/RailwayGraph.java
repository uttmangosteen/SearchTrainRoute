package io.github.uttmangosteen;

import java.util.*;

//重くなりそうだったので一応ArrayListは使わないように
public class RailwayGraph {
    int[] stationIDs;
    Rail[] rails;

    RailwayGraph(int[] stationIDs, Rail[] rails) {
        this.stationIDs = stationIDs;
        this.rails = rails;
    }

    void addStation(int stationID) {
        for (int id : stationIDs) {
            if (id == stationID) return;
        }
        int[] newStationIDList = new int[stationIDs.length + 1];
        System.arraycopy(stationIDs, 0, newStationIDList, 0, stationIDs.length);
        newStationIDList[stationIDs.length] = stationID;
        stationIDs = newStationIDList;
        Arrays.sort(stationIDs);
    }

    public void addRail(int stationID_1, int stationID_2, Double distance) {
        addStation(stationID_1);
        addStation(stationID_2);
        if (stationID_1 > stationID_2) {
            int t = stationID_1;
            stationID_1 = stationID_2;
            stationID_2 = t;
        }
        Rail[] newRailList = new Rail[rails.length + 1];
        System.arraycopy(rails, 0, newRailList, 0, rails.length);
        newRailList[rails.length] = new Rail(stationID_1, stationID_2, distance);
        rails = newRailList;
        // stationID_1 → stationID_2 → distance の順でソート(意味はないが見やすさのため)
        Arrays.sort(rails, Comparator
                .comparingInt((Rail r) -> r.stationID_1)
                .thenComparingInt(r -> r.stationID_2)
                .thenComparingDouble(r -> r.distance));
    }

    public Rail[] getEnabledRails(RailwayGraph graph, int currentStationID) {
        if (rails == null) return new Rail[0];
        List<Rail> enabledRails = new ArrayList<>();
        for (Rail rail : rails) {
            if (rail.stationID_1 == currentStationID || rail.stationID_2 == currentStationID) {
                enabledRails.add(rail);
            }
        }
        return enabledRails.toArray(new Rail[0]);
    }

    //グラフの状況を見る(デバック用)
    public void printGraph() {
        System.out.println("======グラフの状況======");
        System.out.println("駅ID一覧:");
        System.out.println(Arrays.toString(stationIDs));
        System.out.println("路線一覧:");
        for (Rail rail : rails) {
            System.out.println(rail.stationID_1 + " <-> " + rail.stationID_2 + " (距離: " + rail.distance + ")");
        }
        System.out.println("========================");
    }
}

