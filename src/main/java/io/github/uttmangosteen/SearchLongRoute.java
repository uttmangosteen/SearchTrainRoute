package io.github.uttmangosteen;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

/*
=====目標=====
制約に沿って最も長い経路を探す

=====制約(問題文より)=====
・同じ点を2回通ることはできません
・始点と終点はすべての点の中から自由に選択できます
・始点と終点を同じ点にしても構いません
*/

public class SearchLongRoute {
    private static int[] stationIDs;
    private static Rail[] rails;
    private static int nRail;

    //元も距離が長くなるRailのListを探す
    public static int[] searchLongRoute(RailwayGraph graph) {
        stationIDs = graph.stationIDs;
        rails = graph.rails;
        nRail = rails.length;

        if (nRail == 0) {
            System.out.println("グラフが小さいです");
            return new int[0];
        }

        boolean[] longestPathRailList = new boolean[nRail];
        double longestPathDistance = 0;

        for (int startStationID : stationIDs) {
            boolean[] unusedRailList = new boolean[nRail];
            for (int i = 0; i < nRail; i++) unusedRailList[i] = true;

            //全探索のコードを以下に加筆

        }

        return buildPath(longestPathRailList);
    }

    //行ける駅を探索
    private static boolean[] getEnabledRails(int currentStationID, boolean[] unusedRailList) {
        boolean[] enabledRails = new boolean[nRail];
        for (int i = 0; i < nRail; i++)
            enabledRails[i] = unusedRailList[i] && (rails[i].stationID_1 == currentStationID || rails[i].stationID_2 == currentStationID);
        return enabledRails;
    }

    //経路の総距離を計算
    private static double sumTotalDistance(boolean[] railList) {
        return IntStream.range(0, nRail).filter(i -> railList[i]).mapToDouble(i -> rails[i].distance).sum();
    }

    //RailListからpathを求める
    private static int[] buildPath(boolean[] railList) {
        if (railList == null) return new int[0];
        int nPath = (int) IntStream.range(0, nRail).filter(i -> railList[i]).count();
        if (nPath == 0) return new int[0];

        //デバック用
        System.out.println("======使用する路線======");
        IntStream.range(0, nRail).filter(i -> railList[i]).forEach(i -> {
            Rail r = rails[i];
            System.out.println(r.stationID_1 + " <-> " + r.stationID_2 + " (距離: " + r.distance + ")");
        });
        System.out.println("========================");

        //閉路の有無を確認
        Set<Integer> openStations = new HashSet<>();
        for (int i = 0; i < railList.length; i++) {
            if (!railList[i]) continue;
            Rail rail = rails[i];
            if (!openStations.add(rail.stationID_1)) openStations.remove(rail.stationID_1);
            if (!openStations.add(rail.stationID_2)) openStations.remove(rail.stationID_2);
        }

        int[] stations = new int[nPath + 1];

        //最初の駅を選択
        if (openStations.isEmpty()) {
            for (int i = 0; i < nRail; i++)
                if (railList[i]) {
                    stations[0] = rails[i].stationID_1;
                    break;
                }
        } else if (openStations.size() == 2) {
            stations[0] = openStations.iterator().next();
        } else {
            System.out.println("RailListから発売可能な経路が生成できません");
            return new int[0];
        }

        boolean[] usedRailList = new boolean[nRail];
        for (int i = 1; i <= nPath; i++) {
            boolean found = false;
            for (int j = 0; j < nRail; j++) {
                if (!railList[j] || usedRailList[j]) continue;
                Rail rail = rails[j];
                if (rail.stationID_1 == stations[i - 1]) {
                    stations[i] = rail.stationID_2;
                    usedRailList[j] = true;
                    found = true;
                    break;
                } else if (rail.stationID_2 == stations[i - 1]) {
                    stations[i] = rail.stationID_1;
                    usedRailList[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("RailListが連結でありません");
                return new int[0];
            }
        }

        return stations;
    }
}