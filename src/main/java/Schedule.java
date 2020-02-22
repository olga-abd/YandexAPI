import sun.management.StackTraceElementCompositeData;

import java.text.SimpleDateFormat;
import java.util.List;

public class Schedule {
    public Pagination pagination;
    public List<Segment> segments;
    public Search search;

    public void print() {
        SimpleDateFormat sdp = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdp_time = new SimpleDateFormat("yyyy-MM-dd в hh:mm");
        System.out.println("******************");
        System.out.println ("Найденное кол-во маршрутов на " + sdp.format(search.date) + " из города "
                + search.from.title + " в город " + search.to.title + ": " + pagination.total);
        int i = 0;
        StringBuffer tr_type = new StringBuffer();
        for (Segment s : segments) {
            switch (s.from.transport_type){
                case "train":
                    tr_type = new StringBuffer("Поезд");
                    break;
                case "plane":
                    tr_type = new StringBuffer("Самолет");
                    break;
                default:
                    tr_type = new StringBuffer(s.from.transport_type);
            }
            System.out.print(++i + ". ");
            System.out.println(tr_type + " " + s.thread.title + ", " + s.thread.number);
            System.out.print("отправление " + sdp_time.format(s.departure));
            System.out.println(", " + s.from.station_type_name + " " + s.from.title);
            System.out.print("прибытие " + sdp_time.format(s.arrival));
            System.out.println(", " + s.to.station_type_name + " " + s.to.title);
        }
        System.out.println();
    }

}
