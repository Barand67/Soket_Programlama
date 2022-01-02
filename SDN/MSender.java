package SDN;

import Root.Sender; //paketi ekliyor


public class MSender {
    
    public static void main(String[] args) {
        Sender sender = new Sender(1000);//port numarasını verdik
        sender.start();//programı başlatıyor
    }
}
