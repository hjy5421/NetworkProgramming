
import java.io.*;
import java.security.*;
import javax.net.ssl.*;
import java.rmi.Naming;

public class SSLSocketServer {

	public static void main(String[] args) {

		final KeyStore ks;
		final KeyManagerFactory kmf;
		final SSLContext sc;

		final String runRoot = "E:/Network_Project/NetworkProject/bin/"; // root
		// change : your system root
		int clientCount = 0;

		SSLServerSocketFactory ssf = null;
		SSLServerSocket s = null;
		SSLSocket c = null;

		if (args.length != 3) {
			System.out.println("Usage: Classname rmiserver rmiServname Port");
			// java SSLSocketServer localhost bk 8888
			System.exit(1);
		}
		String rmiServer = args[0];
		String rmiServName = args[1];
		int sPort = Integer.parseInt(args[2]);
		String ksName = runRoot + ".keystore/SSLSocketServerKey";

		char keyStorePass[] = "1q2w3e4r".toCharArray();
		char keyPass[] = "1q2w3e4r".toCharArray();

		try {
			// .keystore 에서 키 오브젝트 생성
			ks = KeyStore.getInstance("JKS");
			ks.load(new FileInputStream(ksName), keyStorePass);
			// keyManangerFactory -> keyContext
			kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, keyPass);

			sc = SSLContext.getInstance("TLS");
			sc.init(kmf.getKeyManagers(), null, null);

			SSLEngine sslEngine = sc.createSSLEngine();
			sslEngine.setUseClientMode(true);
			// sslEngine = SSLContext.createSSLEngine();
			// sslEngine.setUseClientMode(false);
			// sslSession = sslEngine.getSession();

			// dummy = ByteBuffer.allocate(0);
			// outNetBuffer = ByteBuffer.allocate(this.getNetBufferSize());
			// inAppBuffer = ByteBuffer.allocate(this.getAppBufferSize());

			/* SSLServerSocket */
			ssf = sc.getServerSocketFactory(); // SSL socket factory
			s = (SSLServerSocket) ssf.createServerSocket(sPort); // SSL socket server
			printServerSocketInfo(s);
			c = (SSLSocket) s.accept(); // SSL socket

			// while (true) {
			// if (clientCount > 2)
			// break;
			// try {
			// c = (SSLSocket) s.accept(); // SSL socket

			// } catch (IOException e) {
			// System.out.println("Accept Fail");
			// }
			// clientCount++;
			// }
			System.out.println("started at " + rmiServer + "and use default port(1099),Service name : " + rmiServName);
			Naming.rebind("rmi://" + rmiServer + ":1099/" + rmiServName, new BaskinServerImpl());
			printSocketInfo(c);

			// RMI bind 하는 부분. Localhost 접속

			// Baskin Server의 Impl rmi서버에 등록

		} catch (

		SSLException se) {
			System.out.println("SSL problem, exit~");
		} catch (Exception e) {
			System.out.println("What?? exit~");

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

	private static void printServerSocketInfo(SSLServerSocket s) {
		System.out.println("Server socket class: " + s.getClass());
		System.out.println("   Server address = " + s.getInetAddress().toString());
		System.out.println("   Server port = " + s.getLocalPort());
		System.out.println("   Need client authentication = " + s.getNeedClientAuth());
		System.out.println("   Want client authentication = " + s.getWantClientAuth());
		System.out.println("   Use client mode = " + s.getUseClientMode());
	}
}