import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    베스킨 라빈스 Sever 인터페이스
*/
public interface BaskinServerIF extends Remote {
    // 클라이언트 리스트에 추가하는 메소드
    public void addClient(BaskinClientIF client) throws RemoteException;

    // TODO: client에 메시지 출력하는 메소드?
    public void putClient() throws RemoteException;
}