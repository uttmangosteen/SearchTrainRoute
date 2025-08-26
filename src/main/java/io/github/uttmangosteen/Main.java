package io.github.uttmangosteen;

import java.util.HashMap;
import java.util.Scanner;

import static io.github.uttmangosteen.SearchLongRoute.searchLongRoute;

public class Main {
    public static void main(String[] args) {
        //路線図の情報
        RailwayGraph graph = new RailwayGraph(new HashMap<>());

        //標準入力読取↑の関数に格納(parseIntの例外処理一切してないのちょっと怖い)
        //今は、全要素入力後再度enterを入力してくれる想定
        Scanner sc = new Scanner(System.in);
        while (sc.hasNextLine()) {
            String[] input = sc.nextLine().split(",");

            //適当
            if (input.length != 3) break;

            //情報入力
            graph.addRail(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim()), Double.parseDouble(input[2].trim()));
        }

        //グラフ確認(デバック用)
        graph.printGraph();

        //解答出力
        for (int s : searchLongRoute(graph)) System.out.println(s);
    }
}