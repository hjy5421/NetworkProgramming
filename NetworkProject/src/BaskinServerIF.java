import java.rmi.Remote;
import java.rmi.RemoteException;

/*
    베스킨 라빈스 Sever 인터페이스
*/
public interface BaskinServerIF extends Remote {
	public String whoClient(int n) throws RemoteException;
	public void putClient(String clientName, String chatInput) throws RemoteException;
	public void addClient(BaskinClientIF client) throws RemoteException;
	public void removeClient(BaskinClientIF client) throws RemoteException;
    
}