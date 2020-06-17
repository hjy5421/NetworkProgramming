import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;

/*
  베스킨 라빈스 Server의 class
*/
public class BaskinServerImpl extends UnicastRemoteObject implements BaskinServerIF {

	private static final long serialVersionUID = 1L;
	public ArrayList<BaskinClientIF> clientList = null; // 연결된 client의 정보를 담을 리스트
	ArrayList<BaskinClientIF> gameList = null; // 게임에 참여중인 client 리스트
	static int turn = 0;
	static int leader = 0; // 방장
	static int state = 0; // 0=채팅, 1=게임 시작, 2=게임 진행중, 3=게임 끝
	private static final int MAXPLAYER = 5; // 최대 플레이 인원 5명
	static int first; // 이전 끝난 숫자, 게임 판별 시 사용

	protected BaskinServerImpl() throws RemoteException {
		super();
		clientList = new ArrayList<BaskinClientIF>();
		gameList = new ArrayList<BaskinClientIF>();
	}

	/*
	 * 현재 차례 client의 name을 알려주는 메소드
	 */
	public String whoTurn(int n) throws RemoteException {
		String clientName = "";
		clientName = gameList.get(n).getClientName();
		return clientName;
	}

	/*
	 * leader client의 name을 알려주는 메소드
	 */
	public String whoLeader(int n) throws RemoteException {
		String clientName = "";
		clientName = clientList.get(n).getClientName();
		return clientName;
	}

	/*
	 * client가 존재하는 리스트 알려주는 메소드
	 * 
	 * @return : 1 gameList, 2 clientList
	 */
	private int inWhichList(BaskinClientIF client) throws RemoteException {
		if (gameList.contains(client))
			return 1;
		else
			return 2;
	}

	/*
	 * 입력받은 clientName의 인덱스 알려주는 메소드
	 */
	private int whoClient(int n, BaskinClientIF client) throws RemoteException {
		// int idx = 0;
		if (n == 1) { // gamelist
			return gameList.indexOf(client);
		} else { // clientlist
			return clientList.indexOf(client);
		}
	}

	/*
	 * 방장부터 시계방향으로 돌아가게 gameList 설정
	 */
	private void setLeader(String clientName) throws RemoteException {
		gameList.clear();
		turn = 0; // 턴 초기화
		int n = 0; // Leader 인덱스
		for (int i = 0; i < clientList.size(); i++) {
			if (clientList.get(i).getClientName().equals(clientName))
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

	/*
	 * 해당 client 제외한 다른 client에게 메세지 전송
	 */
	public void sendExcept(ArrayList<BaskinClientIF> list, String clientName, String chatInput) throws RemoteException {
		for (BaskinClientIF client : list) {
			if (!client.getClientName().equals(clientName)) {
				client.receiveMsg(clientName + " : " + chatInput);
			}
		}
	}

	/*
	 * 모든 client에게 메세지 전송
	 */
	public void sendAll(ArrayList<BaskinClientIF> list, String msg) throws RemoteException {
		for (BaskinClientIF client : list) {
			client.receiveMsg(msg);
		}
	}

	/*
	 * client가 처음 접속했을 때 방상태 알려주는 메세지를 전송
	 */
	public void alertClient(BaskinClientIF client) throws RemoteException {
		// 방장 알려주고
		client.receiveMsg("방장은 " + whoLeader(leader) + "입니다.");

	}

	/*
	 * 채팅, 게임진행 함수
	 */
	// TODO: leader로 설정된 사람이 게임이 끝나고 나가면...->arrayList니까 해결될듯?
	public void putClient(String clientName, String chatInput, BaskinClientIF client) throws RemoteException {
		if (state == 0) { // 채팅
			sendExcept(clientList, clientName, chatInput);
			if (chatInput.equals("!leader")) {
				alertClient(client);
			}
			System.out.println("현재 술래 client 이름 : " + whoLeader(leader));
			if (chatInput.equalsIgnoreCase("start")) {
				System.out.println("현재 술래 제대로 입력중. state : " + state);

				if (clientName.equals(whoLeader(leader))) {
					state = 2;
					setLeader(whoLeader(leader));
					sendAll(clientList, "==========================================================");
					sendAll(clientList, "게임이 시작되었습니다.\n술래부터 시작해 주세요.!!!");
					sendAll(clientList, "게임의 순서 ->");
					for (BaskinClientIF temp : gameList) {
						sendAll(gameList, temp.getClientName() + " ");
					}
					sendAll(clientList, "==========================================================");
					return;
				} else {
					sendAll(clientList, "*** 게임의 리더만 시작할 수 있습니다. ***");
				}
			}
		}
		if (state == 2) { // 게임 진행
			// System.out.println("게임 잘 실행됩니다,.");
			if (clientName.equals(whoTurn(turn))) { // 자기 차례에 맞게 입력
				int check30 = 0; // 30 인지
				switch (checkNumber(chatInput, client)) {
					case 0: // 입력 오류

						break;
					case 1: // 정상 입력
						if (turn == gameList.size() - 1)// 마지막 사람
							turn = 0;
						else
							turn++;
						sendExcept(gameList, clientName, chatInput);

						break;
					case 2: // 게임 종료
						// leader=turn;
						String tmp = whoTurn(turn);
						setLeader(tmp);
						System.out.println("leader : " + leader + " turn  : " + turn);
						state = 0;
						first = 0;
						sendAll(gameList, "==========================================================");
						sendAll(gameList, "게임이 종료되었습니다.!!\n이번판의 패자는 " + clientName + "입니다!");
						sendAll(gameList, "다음판의 리더는 패자로 설정됩니다.");
						sendAll(gameList, "==========================================================");
						break;
				}
			} else { // 차례가 아닌 client가 입력했을 때
				int whichlist = inWhichList(client);
				int idx = whoClient(whichlist, client);
				if (whichlist == 1) {
					gameList.get(idx).receiveMsg("***본인 차례가 아닙니다.***");
				} else {
					clientList.get(idx).receiveMsg("***게임이 진행중입니다.***");
				}
			}

		}

	}

	/*
	 * 클라이언트 리스트에 추가하는 메소드
	 */
	@Override
	public void addClient(BaskinClientIF client) throws RemoteException {

		// 최대인원 5명을 초과하였는지?
		if (clientList.size() == MAXPLAYER) {
			client.receiveMsg("최대 인원수를 초과하였습니다!.");
			client.threadStop(); // 쓰레드 종료 호출
		} else {
			clientList.add(client);
			if (state == 2) {// 게임 중일 경우
				sendExcept(clientList, client.getClientName(), client.getClientName() + "님이 관전중입니다.");
				client.receiveMsg("현재 게임이 진행중입니다. 게임이 끝날때까지 기다려 주세요.");
			} else
				sendExcept(clientList, client.getClientName(), client.getClientName() + "님이 입장하였습니다.");
		}
	}

	/*
	 * 클라이언트 리스트에서 제거하는 메소드
	 */
	public void removeClient(BaskinClientIF client) throws RemoteException {
		sendExcept(clientList, client.getClientName(), client.getClientName() + "님이 퇴장하였습니다.");
		clientList.remove(client);
		if (state == 2) // 게임 진행중일때
			gameList.remove(client);
	}

	/*
	 * 베스킨 게임에서 정상적으로 입력받았는지.
	 * 
	 * @return 0 : 입력 오류, 1: 정상 입력, 2: 게임 종료
	 */
	public int checkNumber(String chatInput, BaskinClientIF client) throws RemoteException {
		// Scanner scan = new Scanner(System.in);
		String regex = "[0-9,]+";
		int[] intarr; // , 로 나눈 입력값 담을 배열
		boolean flag = false; // 정상인지 오류인지
		// firstNumber = 20; // 디버그 위함
		boolean check31 = false; // 31 인지 확인
		// chatInput = scan.nextLine();
		if (chatInput.matches(regex)) {
			try {
				intarr = Arrays.stream(chatInput.split(",")).map(x -> Integer.parseInt(x)).mapToInt(x -> x).toArray();
			} catch (NumberFormatException e) {
				client.receiveMsg(",는 하나만 이용해 숫자를 구분해 주세요!");
				return 0;
			}
			if (Arrays.stream(intarr).allMatch(x -> x > first && x < first + 4 && x < 32))// 범위에 맞는 입력
				if (intarr.length <= 3 && intarr.length >= 0) { // c최대 3개까지 입력
					if (!(intarr[0] == first + 1)) {// ex)10으로 끝났을때, 다음 차례가 11이 아닌 값 입력
						client.receiveMsg("잘못된 입력입니다. 다시 입력해 주세요.");
						flag = false;
						return 0;
					}
					for (int i = 0; i <= intarr.length - 1; i++) {
						if (i > 0 && !(intarr[i] == intarr[i - 1] + 1)) { // 연속된 숫자가 아닐경우
							// System.out.println(intarr[i + 1] + " : " + intarr[i]);
							client.receiveMsg("연속되는 숫자가 아닙니다. 다시 입력해 주세요.");
							flag = false;
							return 0;
						} else if (intarr[i] == 31) { // 31을 말할 경우
							check31 = true;
							break;
						} else {
							flag = true;
						}
					}
					if (check31) {
						sendAll(gameList, "게임이 끝났습니다. ");
						return 2;
					} else if (flag) { // 정상 입력
						client.receiveMsg("정상입니다.");
						first = intarr[intarr.length - 1];
						System.out.println("다음값 " + first);
						return 1;
					} else // 오류 입력
						return 0;
				} else {
					client.receiveMsg("입력 갯수 초과입니다. 다시 입력해 주세요.");
					return 0;
				}
			else {
				client.receiveMsg("잘못된 범위의 입력입니다. 다시 입력해 주세요.");
				return 0;
			}
		} else {
			client.receiveMsg("잘못된 형식의 입력입니다. 다시 입력해 주세요.");
			return 0;
		}

	}
}