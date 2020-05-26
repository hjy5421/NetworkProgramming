import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

/*
  베스킨 라빈스 Client의 class
*/
public class BaskinClientImpl extends UnicastRemoteObject implements Runnable, BaskinClientIF {
    private static final long serialVersionUID = 1L;
    BaskinServerIF server = null;
    String clientName = "";

    protected BaskinClientImpl(BaskinServerIF server, String clientName) throws RemoteException {
        this.server = server;
        this.clientName = clientName;
        server.addClient(this);
    }
    // TODO: 메시지를 recieve 하는 메소드??

    // TODO : 채팅 send 부분
    public void run() {
        try {
            server.putClient();
            System.out.println("성공적으로 연결되었습니다.");
            System.out.println("Player 이름  : "+this.clientName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}