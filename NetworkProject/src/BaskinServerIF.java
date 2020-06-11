import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/*
    베스킨 라빈스 Sever 인터페이스
*/
public interface BaskinServerIF extends Remote {
	public String whoTurn(int n) throws RemoteException;
	public String whoLeader(int n) throws RemoteException;
	public void sendExcept(ArrayList<BaskinClientIF> list, String clientName, String chatInput) throws RemoteException;
	public void sendAll(ArrayList<BaskinClientIF> list, String msg) throws RemoteException;
	public void alertClient(String clientName) throws RemoteException;
	public void putClient(String clientName, String chatInput) throws RemoteException;
	public void addClient(BaskinClientIF client) throws RemoteException;
	public void removeClient(BaskinClientIF client) throws RemoteException;
    
}