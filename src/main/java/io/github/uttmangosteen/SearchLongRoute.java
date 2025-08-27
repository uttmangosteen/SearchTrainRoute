package io.github.uttmangosteen;

import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.*;

//最長片道切符の解答探索において、整数計画法が良いらしいのでそれを用いる
/*
=====目標=====
制約に沿って最も長い経路を探す

=====制約(問題文より)=====
・同じ点を2回通ることはできません
・始点と終点はすべての点の中から自由に選択できます
・始点と終点を同じ点にしても構いません

=====実際に加えた制約=====
・各路線は1度しか使えない
・目的関数→総距離の最大化
・各路線は1度しか使えない
・駅の次数は0or1or2
・全てのRailが連続
*/

public class SearchLongRoute {
    public static int[] searchLongRoute(RailwayGraph graph) {
        int[] stations = graph.stationIDList.stream().mapToInt(Integer::intValue).toArray();
        if (stations.length < 2) {
            System.out.println("グラフが小さいです");
            return new int[0];
        }
        Rail[] rails = graph.railList.toArray(Rail[]::new);

        // ソルバーを作成（整数計画法を使用）
        Loader.loadNativeLibraries();
        MPSolver solver = MPSolver.createSolver("SCIP");
        if (solver == null) {
            System.out.println("ソルバーの作成に失敗しました");
            return new int[0];
        }

        // 各路線を使うかどうかのバイナリ変数
        MPVariable[] useRails = new MPVariable[rails.length];
        for (int i = 0; i < rails.length; i++) useRails[i] = solver.makeBoolVar("rail_" + i);

        // 目的関数：総距離の最大化
        MPObjective objective = solver.objective();
        for (int i = 0; i < rails.length; i++) objective.setCoefficient(useRails[i], rails[i].distance);
        objective.setMaximization();

        // 次数制約: 0～2
        HashMap<Integer, ArrayList<Integer>> stationToRails = new HashMap<>();
        for (int i = 0; i < rails.length; i++) {
            stationToRails.computeIfAbsent(rails[i].stationID_1, k -> new ArrayList<>()).add(i);
            stationToRails.computeIfAbsent(rails[i].stationID_2, k -> new ArrayList<>()).add(i);
        }
        for (Map.Entry<Integer, ArrayList<Integer>> entry : stationToRails.entrySet()) {
            MPConstraint constraint = solver.makeConstraint(0, 2, "connect_" + entry.getKey());
            for (int idx : entry.getValue()) constraint.setCoefficient(useRails[idx], 1);
        }

        //使用するRailが全て連結になる制約をここに足す

        //計算
        MPSolver.ResultStatus resultStatus = solver.solve();

        if (resultStatus == MPSolver.ResultStatus.OPTIMAL || resultStatus == MPSolver.ResultStatus.FEASIBLE) {

            // 使用された路線を特定
            ArrayList<Integer> usedRails = new ArrayList<>();
            for (int i = 0; i < rails.length; i++) if (useRails[i].solutionValue() > 0.5) usedRails.add(i);

            ArrayList<Rail> usedRailList = new ArrayList<>();
            for (int i : usedRails) usedRailList.add(rails[i]);

            //デバック用
            System.out.println("========使用路線========");
            for (Rail rail : usedRailList)
                System.out.println(rail.stationID_1 + " <-> " + rail.stationID_2 + " (距離: " + rail.distance + ")");
            System.out.println("========================");

            return buildPath(usedRailList);
        }
        System.out.println("解が見つかりませんでした: " + resultStatus);
        return new int[0];
    }

    //使うRailから経路を求める
    private static int[] buildPath(ArrayList<Rail> usedRailList) {
        if (usedRailList.isEmpty()) return new int[0];

        int n = usedRailList.size();
        int[] path = new int[n + 1];

        // Railごとの駅ペアを格納
        int[][] stationIDPair = new int[n][2];
        for (int i = 0; i < n; i++) {
            stationIDPair[i][0] = usedRailList.get(i).stationID_1;
            stationIDPair[i][1] = usedRailList.get(i).stationID_2;
        }

        // 各駅の出現回数をカウント
        HashMap<Integer, Integer> stationAmount = new HashMap<>();
        for (int[] pair : stationIDPair) {
            for (int station : pair) {
                stationAmount.put(station, stationAmount.getOrDefault(station, 0) + 1);
            }
        }

        int onepathStationAmount = 0;
        int twopathStationAmount = 0;

        // スタート駅候補を探す
        for (int station : stationAmount.keySet()) {
            if (stationAmount.get(station) == 1) {
                if (onepathStationAmount == 0) path[0] = station;
                onepathStationAmount++;
            }
            if (stationAmount.get(station) == 2) twopathStationAmount++;
        }

        if (onepathStationAmount == 0 && twopathStationAmount == n) {
            //巡回路
            path[0] = stationIDPair[0][0];
        } else if (onepathStationAmount == 2 && twopathStationAmount == n - 1) {
            //path[0]は↑で初期化済み
        } else {
            System.out.println("RailListから販売可能な経路が生成できません");
            return new int[0];
        }

        //始点からpathをたどる
        for (int i = 1; i <= n; i++) {
            boolean found = false;
            for (int j = 0; j < n; j++) {
                if (stationIDPair[j][0] == path[i - 1] || stationIDPair[j][1] == path[i - 1]) {
                    path[i] = (stationIDPair[j][0] == path[i - 1]) ? stationIDPair[j][1] : stationIDPair[j][0];
                    stationIDPair[j] = new int[]{0, 0}; // 使用済みにする
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println("RailListが連結でありません");
                return new int[0];
            }
        }
        return path;
    }
}