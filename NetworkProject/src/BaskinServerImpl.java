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
	int state = 0; // 0=채팅, 1=게임 시작, 2=게임 진행중, 3=게임 끝
	private static final int MAXPLAYER = 5; // 최대 플레이 인원 5명

	protected BaskinServerImpl() throws RemoteException {
		super();
		clientList = new ArrayList<BaskinClientIF>();
	}

	/*
	 현재 차례 client의 name을 알려주는 메소드
	 */
	public String whoTurn(int n) throws RemoteException {
		String clientName = "";
		clientName = gameList.get(n).getClientName();
		return clientName;
	}
	
	/*
	 leader client의 name을 알려주는 메소드
	 */
	public String whoLeader(int n) throws RemoteException{
		String clientName="";
		clientName=clientList.get(n).getClientName();
		return clientName;
	}
	
	/*
	 client가 존재하는 리스트 알려주는 메소드
	 */
	private int inWhichList(String clientName) throws RemoteException{
		int n=0; //1=in gameList, 2=in clientList
		for(int i=0;i<gameList.size();i++) {
			if(gameList.get(i).getClientName()==clientName) {
				n=1;
			}
		}
		if(n==0) {
			n=2;
		}
		return n;
	}
	
	/*
	 입력받은 clientName의 인덱스 알려주는 메소드
	 */
	private int whoClient(int n,String clientName) throws RemoteException{
		int idx=0;
		if(n==1) {
			for(int i=0;i<gameList.size();i++) {
				if(gameList.get(i).getClientName()==clientName)
					idx=i;
			}
		}else {
			for(int i=0;i<clientList.size();i++) {
				if(clientList.get(i).getClientName()==clientName)
					idx=i;
			}
		}
		return idx;
	}

	/*
	 방장부터 시계방향으로 돌아가게 gameList 설정
	 */
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
	
	/*
	 해당 client 제외한 다른 client에게 메세지 전송
	 */
	public void sendExcept(ArrayList<BaskinClientIF> list, String clientName, String chatInput) throws RemoteException{
		for(BaskinClientIF client : list) {
			if(client.getClientName()!=clientName) {
				client.receiveMsg(clientName+" : "+chatInput);
			}
		}
	}
	
	/*
	 모든 client에게 메세지 전송
	 */
	public void sendAll(ArrayList<BaskinClientIF> list, String msg) throws RemoteException{
		for(BaskinClientIF client:list) {
			client.receiveMsg(msg);
		}
	}
	
	/*
	 client가 처음 접속했을 때 방상태 알려주는 메세지를 전송 
	 */
	public void alertClient(String clientName) throws RemoteException{
		//방장 알려주고
		sendAll(clientList,"방장은 "+whoLeader(leader)+"입니다.");
		
		//게임중에 입장하면 게임중이라고 알려주고
		//nullpointer exception날 것도 같음
		int whichlist=inWhichList(clientName);
		int idx=whoClient(whichlist,clientName);
		if(whichlist==2) {
			if(state==2) {
				clientList.get(idx).receiveMsg("게임이 진행중입니다.");
			}
		}
	}
	
	/*
	 채팅, 게임진행 함수
	 */
	//TODO: leader로 설정된 사람이 게임이 끝나고 나가면...->arrayList니까 해결될듯?
	public void putClient(String clientName, String chatInput) throws RemoteException {
		if(state==0) { //채팅 
			if(clientName==whoLeader(leader)) {
				if(chatInput=="start") 
					state=1;
			}
			else {
				sendExcept(clientList,clientName,chatInput);
			}	
		}else if(state==1) { //게임 시작
			setLeader(whoLeader(leader));
			// TODO: client의 run()에서 게임이 시작되었습니다 치면 입력하도록 할 것
			sendAll(clientList, "게임이 시작되었습니다.");
			state=2;
		}else if(state==2) { //게임 진행
			if(clientName==whoTurn(turn)) {
				sendExcept(gameList,clientName,chatInput);
				int check30 = 0;
				//TODO: 문자열 토큰 기준으로 제대로 나눠지는지 확인해서 수정
				String[] chatNum = chatInput.split(" |,");
				for (int i = 0; i < chatNum.length; i++) {
					if (chatNum[i].equals("30")) {
						check30 = 1;
					}
				}
				if (check30 == 1) {
					if (turn == gameList.size() - 1) // 리스트 가장 마지막 사람이 30을 말했을 경우
						leader = 0;
					else
						leader=++turn;
					state = 3;
					check30=0;
				} else {
					turn++;
				}
			}else { //차례가 아닌 client가 입력했을 때
				int whichlist=inWhichList(clientName);
				int idx=whoClient(whichlist,clientName);
				if(whichlist==1) {
					gameList.get(idx).receiveMsg("본인 차례가 아닙니다.");
				}else {
					clientList.get(idx).receiveMsg("게임이 진행중입니다.");
				}
			}
			
		}else { //게임 종료
			sendAll(clientList,"게임이 끝났습니다.");
			state = 0;
		}
		
	}

	/*
	 클라이언트 리스트에 추가하는 메소드 
	 */
	@Override
	public void addClient(BaskinClientIF client) throws RemoteException {
		// 최대인원 5명을 초과하였는지?
		if (clientList.size() == MAXPLAYER) {
			client.receiveMsg("최대 인원수를 초과하였습니다!.");
			client.threadStop(); // 쓰레드 종료 호출
		} else {
			clientList.add(client);
			sendExcept(clientList,client.getClientName(),client.getClientName()+"님이 입장하였습니다.");
		}
	}

	/*
	 클라이언트 리스트에서 제거하는 메소드
	 */
	public void removeClient(BaskinClientIF client) throws RemoteException {
		sendExcept(clientList,client.getClientName(),client.getClientName()+"님이 퇴장하였습니다.");
		clientList.remove(client);
		for(BaskinClientIF c:gameList) {
			if(c.getClientName()==client.getClientName())
				gameList.remove(client);
		}
	}
}