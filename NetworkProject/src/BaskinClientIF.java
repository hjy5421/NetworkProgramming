import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    베스킨 라빈스 Client 인터페이스
*/
public interface BaskinClientIF extends Remote {
    // TODO : 메시지를 받아올 메소드?
    public String getClientName() throws RemoteException;

    public void receiveMsg(String msg) throws RemoteException;

    public void threadStop() throws RemoteException;
}