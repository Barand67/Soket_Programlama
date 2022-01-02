//Soket programlama yapmak için gereken kütüphanaleri ekliyoruz
package Root;
import java.io.*;
import java.net.*;
import java.util.*;

public class Sender extends Thread { // Thred de ki bilgileri Sender e çağırıyor
    private InetAddress host;
    private final int PORT;
    private Socket link = null;//soketi dinlemek için 
    Scanner input;//soketden gelen bilgiyi okumak için
    PrintWriter output;//sokete bilgi yazabilmek için

    public Sender(int port) { //dinleyeceği portun numarasını veriyor
        this.PORT = port;
        this.serviceStart(PORT);
    }

    @Override
    public void run() {
        try {
            while (true) {//servere erişimi sağlıyor
                this.accessServer();
            }
        } 
        catch (IOException ioEx) {
            ioEx.printStackTrace();
        } 
        finally{
            try {
                //Socketi kapatıyor bilgilendirme mesajı veriyor
                System.out.println("\n* Closing connections (Sender side)*");
                output.println("***CLOSE***");
                link.close();
            } 
            catch (IOException ioEx) {
                //Socketi kapatırken hata ile karşılaşırsa bilgi veriyor
                System.out.println("Unable to disconnect!");
                System.exit(1);
            }
        }
    }

    //Try catch bloguna ihtiyaç duymadan bu fonksiyonun throws ile  hata atabileceğini bildiriyor
    private void accessServer() throws IOException {
        System.out.println("How many packets? ");//yollancak paket sayısını soruyor
        Scanner userEntry = new Scanner(System.in);//kullanıcıdan bilgiyi alıyor
        String message, str2, response;
        int number;

        response = userEntry.nextLine();//alınan bilgiyi responseye atıyor
        number = Integer.parseInt(response);//str bir ifadeyi int e dönüştürüp number a atıyor
        int counter = 0, attempt = 0; // kayıplerı gösteriyor

        //Paket yollamaya başlamadan önceki başlangıç zamanı
        long startTime = System.nanoTime(); //başlangıç zamanını ayarlıyor
        do {
            message = "PCK";
            this.sendMessage(message + counter);//mesaj ile sayacı yolluyor
            attempt++;

            String request = this.getRequest(); //dinleyeceği portdan gelen mesajı alıyor
            //Gelen mesajda "Port adı , mesaj " şeklinde geldiğinden port adı ile mesajı ayırmak için split kullanıyor
            String[] split = request.split(",");//port adı ile mesajı ayırıyor
            str2 = split[split.length-1].substring(0, 3);//ACK mı yollanıyor diye bakıyor
            //Paket yerine ulaşmamış ise ekrana yazı bastırılıyor tekrar kontrol ediliyor
            while (!str2.equals("ACK")) {//gelen mesaj ACK değil ise 
                System.out.println(message + counter + " Resending...");//tekrardan mesajı gönderiyor
                output.println(message + counter);//kaç defa yollanığını buluyr
                attempt++;//kayıp olan paket sayısını artırıyor 
                split = this.getRequest().split(","); // gelen mesajı split ile bölüyor
                if(split != null){
                    str2 = split[split.length-1].substring(0, 3);//mesajın ilk 3 hanesini alıyor ekrana yazdırıyor
                    System.out.println(str2);
                }
            }
            System.out.println(request + " received from receiver successfully");
            counter++;//yolanan paket sayısını artırıyor 
        } while (counter < number);//while döngüsü bittikten sonra 
        long endTime = System.nanoTime();//bitiş zamanının alıyor
        System.out.println("Total number of try: " + attempt);//toplam kayıp
        System.out.println("System afficiency: " + (double) number/attempt);//sistemin verimliğini alıyor
        System.out.println("Time taken to send all packets: " +(endTime - startTime) + " nano seconds.");//kaç nano saniyede işlemin bittiğini söylüyor
    }
    public void sendMessage(String message){
        output.println(message);
    }
    public String getRequest(){
        return input.hasNext() ? input.nextLine() : "";
    }
    public void serviceStart(int PORT) {
        try {
            host = InetAddress.getLocalHost();//hostun bilgisini alıyor
            link = new Socket(host, PORT);//soketi oluşturuyor
            input = new Scanner(link.getInputStream());//gelen bilgiyi okumak için oluşturuyor
            output = new PrintWriter(link.getOutputStream(), true);//bigliyi yazmak için oluşturuyor
        } catch (Exception uhEx) {
            System.exit(1);
        }
    }
}
