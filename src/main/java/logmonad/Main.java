package logmonad;

public class Main {
    public static void main(String... args){
//        final Service service = new Service();
//        service.run();

        final Service2 service2 = new Service2(new Subservice1(), new Subservice2());
        service2.run();
    }
}
