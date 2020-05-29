import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/*
  베스킨 라빈스 Server의 class
*/
public class BaskinServerImpl extends UnicastRemoteObject implements BaskinServerIF {

	private static final long serialVersionUID = 1L;
	ArrayList<BaskinClientIF> clientList = null; // 연결된 client의 정보를 담을 리스트
	ArrayList<BaskinClientIF> gameList = null; // 게임에 참여중인 client 리스트
	int turn = 0;
	int leader = 0; // 방장
	int state = 0; // 0=시작, 1=게임 진행중, 2=게임 끝
	private static final int MAXPLAYER = 5; // 최대 플레이 인원 5명

	protected BaskinServerImpl() throws RemoteException {
		super();
		clientList = new ArrayList<BaskinClientIF>();
	}

	// 어떤 client 차례인지 알려주는 메소드
	public String whoClient(int n) throws RemoteException {
		String clientName = "";
		clientName = gameList.get(n).getClientName();
		return clientName;
	}

	// TODO: addclient하기 전에 clientList size가 5개 다 안 찼는지 검사 ->addclient 함수에서 구현
	// 방장이 누구인지 뿌려주기
	// 게임유저 중 한 명 나가면 arraylist에서 빼기 -> remove 함수 안에서 구현

	// gameList 설정, 방장 설정
	private void setLeader(String clientName) throws RemoteException {
		gameList.clear();
		int n = 0; // Leader 인덱스
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).getClientName() == clientName)
				n = i;
		}
		if (clientList.size() > 1 && clientList.size() <= MAXPLAYER) { // 플레이어가 2명 이상, 5명 미만
			for (int i = n; i < clientList.size(); i++) { // 리더 기준 오른쪽 플레이어
				gameList.add(clientList.get(i));
			}
			if (n > 0) { // 리더 기준 왼쪽 플레이어
				for (int i = 0; i < n; i++) {
					gameList.add(clientList.get(i));
				}
			}
		}
		leader = n;
	}

	public void putClient(String clientName, String chatInput) throws RemoteException {
		if (state == 0) {
			String findClient = gameList.get(turn).getClientName();
			setLeader(findClient);
			if (chatInput.equals("start")) {
				for (int i = 0; i < gameList.size(); i++) {
					gameList.get(i).receiveMsg("게임이 시작되었습니다."); // TODO: client의 run()에서 게임이 시작되었습니다 치면 입력하도록 할 것
				}
			}
			state = 1;
		} else if (state == 1) {
			int check30 = 0;
			String[] chatNum = chatInput.split(" |,");
			for (int i = 0; i < chatNum.length; i++) {
				if (chatNum[i].equals("30")) {
					check30 = 1;
				}
			}
			if (check30 == 1) {
				if (turn == gameList.size() - 1) // 리스트 가장 마지막 사람이 30을 말했을 경우
					turn = 0;
				else
					turn++;
				state = 2;
			} else {
				turn++;
			}
		} else {
			for (int i = 0; i < gameList.size(); i++) {
				gameList.get(i).receiveMsg("게임이 끝났습니다.");
			}
			state = 0;
		}
	}

	@Override
	// 클라이언트 리스트에 추가하는 메소드
	public void addClient(BaskinClientIF client) throws RemoteException {
		// 최대인원 5명을 초과하였는지?
		if (clientList.size() == MAXPLAYER) {
			client.receiveMsg("최대 인원수를 초과하였습니다!.");
			client.threadStop(); // 쓰레드 종료 호출
		} else
			clientList.add(client);

	}

	// 클라이언트 리스트에서 제거하는 메소드
	public void removeClient(BaskinClientIF client) throws RemoteException {
		clientList.remove(client);
		// TODO: 게임 시작 전 사용자가 나가게 된다면, 다음 방장은 누가?
	}
}