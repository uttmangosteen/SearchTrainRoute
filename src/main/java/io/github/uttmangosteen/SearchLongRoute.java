package io.github.uttmangosteen;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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
    private static int nStation;
    private static Rail[] rails;
    private static int nRail;

    //最長経路置き場
    private static boolean[] longestPathRailFlags;
    private static double longestPathDistance;


    //元も距離が長くなるRailのListを探す
    public static int[] searchLongRoute(RailwayGraph graph) {
        rails = graph.rails;
        nRail = rails.length;
        if (nRail == 0) {
            System.out.println("グラフが小さいです");
            return new int[0];
        }
        stationIDs = graph.stationIDs;
        nStation = stationIDs.length;

        longestPathRailFlags = new boolean[nRail];
        longestPathDistance = 0;

        // すべての駅を開始点としてDFS試行
        for (int startStationID : stationIDs) dfs(startStationID, startStationID, new HashSet<>(), new boolean[nRail]);

        return buildPath(longestPathRailFlags);
    }

    private static void dfs(int currentStationID, int startStationID, Set<Integer> usedStationList, boolean[] usedRailFlags) {
        usedStationList.add(currentStationID);

        // 現在の距離が最大なら上書き
        double currentDistance = sumTotalDistance(usedRailFlags);
        if (currentDistance > longestPathDistance) {
            longestPathDistance = currentDistance;
            longestPathRailFlags = usedRailFlags.clone();
        }

        boolean[] enabledRails = getEnabledRails(currentStationID, startStationID, usedStationList, usedRailFlags);

        for (int i = 0; i < nRail; i++) {
            if (!enabledRails[i]) continue;

            Rail rail = rails[i];
            int nextStationID = (rail.stationID_1 == currentStationID) ? rail.stationID_2 : rail.stationID_1;

            // 次の駅がスタート駅の場合の処理
            if (nextStationID == startStationID) {
                usedRailFlags[i] = true;
                double loopDistance = sumTotalDistance(usedRailFlags);
                if (loopDistance > longestPathDistance) {
                    longestPathDistance = loopDistance;
                    longestPathRailFlags = usedRailFlags.clone();
                }
                usedRailFlags[i] = false; //railのバックトラック
                continue;
            }

            // 通常のDFS（未訪問駅のみ）
            if (!usedStationList.contains(nextStationID)) {
                usedRailFlags[i] = true;
                dfs(nextStationID, startStationID, usedStationList, usedRailFlags);
                usedRailFlags[i] = false; //railのバックトラック
            }
        }

        //駅のバックトラック
        usedStationList.remove(currentStationID);
    }


    //選べるrailListを返す
    private static boolean[] getEnabledRails(int currentStationID, int startStationID, Set<Integer> usedStationList, boolean[] usedRailFlags) {
        boolean[] enableRailList = new boolean[nRail];
        for (int i = 0; i < nRail; i++) {
            if (usedRailFlags[i]) continue;
            int nextStationID = rails[i].stationID_1 == currentStationID ? rails[i].stationID_2 :
                    rails[i].stationID_2 == currentStationID ? rails[i].stationID_1 : 0;
            if (nextStationID == 0) continue;
            //最初の駅だけは行っても良い
            if (nextStationID == startStationID) enableRailList[i] = true;
            else if (!usedStationList.contains(nextStationID)) enableRailList[i] = true;
        }
        return enableRailList;
    }

    //経路の総距離を計算
    private static double sumTotalDistance(boolean[] railFlags) {
        double distance = 0;
        for (int i = 0; i < nRail; i++) if (railFlags[i]) distance += rails[i].distance;
        return distance;
    }

    //RailListからpathを求める
    private static int[] buildPath(boolean[] railFlags) {
        if (railFlags == null) return new int[0];
        int nPath = (int) IntStream.range(0, nRail).filter(i -> railFlags[i]).count();
        if (nPath == 0) return new int[0];

        //デバック用
        System.out.println("======使用する路線======");
        IntStream.range(0, nRail).filter(i -> railFlags[i]).forEach(i -> {
            Rail r = rails[i];
            System.out.println(r.stationID_1 + " <-> " + r.stationID_2 + " (距離: " + r.distance + ")");
        });
        System.out.println("========================");

        //端の駅の有無を確認
        Set<Integer> openStations = new HashSet<>();
        for (int i = 0; i < railFlags.length; i++) {
            if (!railFlags[i]) continue;
            Rail rail = rails[i];
            if (!openStations.add(rail.stationID_1)) openStations.remove(rail.stationID_1);
            if (!openStations.add(rail.stationID_2)) openStations.remove(rail.stationID_2);
        }

        int[] stations = new int[nPath + 1];

        //最初の駅を選択
        if (openStations.isEmpty()) {
            for (int i = 0; i < nRail; i++)
                if (railFlags[i]) {
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
                if (!railFlags[j] || usedRailList[j]) continue;
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