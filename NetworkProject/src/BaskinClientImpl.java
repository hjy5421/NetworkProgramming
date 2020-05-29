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
    Scanner scan = new Scanner(System.in);
    boolean stop = false; // 쓰레드 stop 하기위한 변수

    protected BaskinClientImpl(BaskinServerIF server, String clientName) throws RemoteException {
        this.server = server;
        this.clientName = clientName;
        server.addClient(this);
    }

    public String getClientName() {
        return clientName;
    }

    public void receiveMsg(String msg) {
        System.out.println(msg);
    }

    /*
     * 최대 인원 초과시, 쓰레드 종료하는 함수.
     */
    public void threadStop() {
        this.stop = true;
    }

    // TODO : 채팅 send 부분
    public void run() {
        String inputChat = ""; // 입력받은 채팅 메세지
        try {
            // while (true) { // 최대 인원수 초과 시
            // if (stop)
            // break;

            // inputChat = scan.nextLine(); // 메세지 입력
            // // TODO : 숫자만 입력 받을 경우. 자유롭게 채팅 가능하게 할것인지?
            // if (inputChat.equalsIgnoreCase("exit")) {
            // // TODO : sever.remove 함수 호출하여 정상적으로 종료할 것
            // break;
            // }
            // }
            server.putClient(clientName, "ok");
            System.out.println("성공적으로 연결되었습니다.");
            System.out.println("Player 이름  : " + this.clientName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}