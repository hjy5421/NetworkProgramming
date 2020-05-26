import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
  베스킨 라빈스 Server의 class
*/
public class BaskinServerImpl extends UnicastRemoteObject implements BaskinServerIF {

    ArrayList<BaskinClientIF> clientList = null; // 연결된 client의 정보를 담을 리스트
    private static final long serialVersionUID = 1L;

    protected BaskinServerImpl() throws RemoteException {
        super();
        clientList = new ArrayList<BaskinClientIF>();
    }

    // TODO: client에 메시지 출력하는 메소드?
    public void putClient() {
        System.out.println("client 갯수 : " + clientList.size()); // 디버깅용
    }

    @Override
    // 클라이언트 리스트에 추가하는 메소드
    public void addClient(BaskinClientIF client) throws RemoteException {
        clientList.add(client);

    }
}