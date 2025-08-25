package io.github.uttmangosteen;

import java.util.Map;

class RailGraph {
    Map<Integer, StationData> stationMap;
    RailGraph(Map<Integer, StationData> stationMap) {
        this.stationMap = stationMap;
    }
}


class Rail{
    int destinationStationID;
    Double distance;
    Rail(int destinationStationID, Double distance){
        this.destinationStationID = destinationStationID;
        this.distance = distance;
    }
}
