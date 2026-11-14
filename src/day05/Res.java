package src.day05;

public class Res implements AutoCloseable {
    String user;
    StringBuilder sb;
    public Res(String a, StringBuilder sb) {
        user = a;
       this.sb = sb;
        sb.append("open:" + a +";");
    }


    @Override
    public void close() throws Exception {
        this.sb.append("close:" + user +";");
    }
}
