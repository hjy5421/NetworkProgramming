
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.lang.Thread.State;
import javax.net.ssl.*;
import java.util.concurrent.TimeUnit;

public class SSLSocketClient {

	public static void main(String[] args) {

		SSLSocketFactory f = null;
		SSLSocket c = null;

		String sServer = ""; // SSL Server Name
		int sPort = -1; // SSl Port
		String mServer = ""; // Rmi Server, Default : localhost
		String mServName = ""; // Rmi Server Name
		String clientName = ""; // client name

		if (args.length != 4) {
			System.out.println("Usage: Classname SSLServerName securePort rmiServerName clientName");
			// java SSLSocketClient localhost 8888 bk LEE
			System.exit(1);
		}
		sServer = args[0];
		sPort = Integer.parseInt(args[1]);
		mServName = args[2];
		clientName = args[3];

		try {
			// trustedcerts 파일 경로
			System.setProperty("javax.net.ssl.trustStore", "trustedcerts");
			System.setProperty("javax.net.ssl.trustStorePassword", "1q2w3e4r");

			f = (SSLSocketFactory) SSLSocketFactory.getDefault(); // SSLSocketFacroty info
			c = (SSLSocket) f.createSocket(sServer, sPort); // Socket Create

			String[] supported = c.getSupportedCipherSuites();
			c.setEnabledCipherSuites(supported);
			printSocketInfo(c);
			c.startHandshake();
			
			System.out.println("--------------------------");
			System.out.println("방을 개설했습니다");
		} catch (IOException io) {
			System.out.println("Exception : " + io);
			io.printStackTrace();

		}
		// 등록된 server객체의 RMI를 찾아 Client 파일의 생성자 arg로 넘겨줌
		try {
			BaskinServerIF server = (BaskinServerIF) Naming.lookup("rmi://" + sServer + "/" + mServName);
			Thread thread = new Thread(new BaskinClientImpl(server, clientName));
			thread.start();

		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			System.out.println("Exception : " + e);
			e.printStackTrace();
		}

	}

	private static void printSocketInfo(SSLSocket s) {
		System.out.println("Socket class: " + s.getClass());
		System.out.println("   Remote address = " + s.getInetAddress().toString());
		System.out.println("   Remote port = " + s.getPort());
		System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
		System.out.println("   Local address = " + s.getLocalAddress().toString());
		System.out.println("   Local port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		SSLSession ss = s.getSession();
		System.out.println("   Cipher suite = " + ss.getCipherSuite());
		System.out.println("   Protocol = " + ss.getProtocol());
	}
}