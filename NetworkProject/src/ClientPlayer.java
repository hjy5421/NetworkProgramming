import java.io.*;
import java.lang.Thread.State;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMISocketFactory;
import java.rmi.Remote;

public class ClientPlayer {

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Usage: Classname localhost rmiServerName clientName");
            System.exit(1);
        }
        String sServer = args[0];
        String mServName = args[1];
        String clientName = args[2];
        BaskinServerIF server;

        try {
            server = (BaskinServerIF) Naming.lookup("rmi://" + sServer + "/" + mServName);
            Thread thread = new Thread(new BaskinClientImpl(server, clientName));
            thread.start();
            // TODO : 쓰레드 종료
            if (thread.getState() == State.TERMINATED) {
                System.exit(1);
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}