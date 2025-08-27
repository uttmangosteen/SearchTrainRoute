package io.github.uttmangosteen;

import java.util.ArrayList;
import java.util.Scanner;

import static io.github.uttmangosteen.SearchLongRoute.searchLongRoute;

public class Main {
    public static void main(String[] args) {
        //路線図の情報
        RailwayGraph graph = new RailwayGraph(new int[0], new Rail[0]);

        //標準入力読取↑の関数に格納(parseIntの例外処理一切してないのちょっと怖い)
        //今は、全要素入力後再度enterを入力してくれる想定
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String[] input = sc.nextLine().split(",");

            //適当
            if (input.length != 3) break;

            //情報入力
            int stationID_1 = Integer.parseInt(input[0].trim());
            int stationID_2 = Integer.parseInt(input[1].trim());
            double distance = Double.parseDouble(input[2].trim());
            graph.addRail(stationID_1, stationID_2, distance);
        }

        //グラフ確認(デバック用)
        graph.printGraph();

        //解答出力
        for (int s : searchLongRoute(graph)) System.out.println(s);
    }
}