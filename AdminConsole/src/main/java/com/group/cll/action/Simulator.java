package com.group.cll.action;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.group.cll.model.Account;

import net.sf.json.JSONObject;

@ClientEndpoint
public class Simulator {
	
	/**
	 * 连消次数达到三次，投注2块二次
	 */
	private static final Logger logger = LoggerFactory.getLogger(Simulator.class);

	public static Map<String, Simulator> session2Simulator = new HashMap<>();
	
	public static Random random = new Random(1);
	
	public static Map<String, String> requestActions = new HashMap<>();
	
	static {
		// 登录-1
		requestActions.put("loginBySid", "{\"action\":\"loginBySid\",\"sid\":\"#{sessionId}\",\"gtype\":\"5902\",\"lang\":\"cn\",\"dInfo\":{\"rd\":\"fx\",\"ua\":\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.109 Safari/537.36\",\"os\":\"Windows 7\",\"srs\":\"1920x1080\",\"wrs\":\"1292x904\",\"dpr\":1,\"pl\":\"H5\",\"wv\":\"false\",\"aio\":false,\"vga\":\"undefined\",\"tablet\":false,\"cts\":1552114614631,\"mua\":\"\",\"dtp\":\"\"}}");

		// 登录-2(前置动作loginBySid)
		requestActions.put("onLoadInfo2", "{\"action\":\"onLoadInfo2\"}");
		
		// 分数兑换界面，每次登录或者打开换分界面
		requestActions.put("getMachineDetail", "{\"action\":\"getMachineDetail\"}");
		
		// 换分(钱->分数)(前置动作getMachineDetail)
		requestActions.put("creditExchange", "{\"action\":\"creditExchange\",\"rate\":\"1:10\",\"credit\":\"200\"}");
		
		// 点击结算离开(前置动作getMachineDetail)
		requestActions.put("balanceExchange", "{\"action\":\"balanceExchange\"}");
		
		// 下注10分
		requestActions.put("beginGame2", "{\"action\":\"beginGame2\",\"line\":\"1\",\"lineBet\":10}");
		
		// 下注20分
		requestActions.put("beginGame2_20", "{\"action\":\"beginGame2\",\"line\":\"1\",\"lineBet\":20}");
		
		// 完全退出(前置动作balanceExchange)
		requestActions.put("leaveMachine", "{\"action\":\"leaveMachine\"}");
	}
	
	public static Lock lock = new ReentrantLock();
	
	public List<String> messages;
	
	private Session session;
	
	public String threadName;
	
	/** 连接sessionId*/
	public String sessionId;
	
	public Account account;
	
	public int lineBetCount = 0;
	
	/** 模块名称*/
	private ActionSet actionSet = ActionSet.LOGIN;

	private int stickNum = 0;// 拐棍个数
	private int betNum = 0;// 打码总数

	public Simulator() {
		super();
		this.threadName = this.toString();
	}

	public static void write(String fileName, String content) {
		lock.lock();
		
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, true)));
			out.write(content);
			out.newLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
		lock.unlock();
	}
	
	public boolean play(Account account, String sessionId) {
		
		this.account = account;
		this.sessionId = sessionId;
		messages = new ArrayList<String>();
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();	// 获得WebSocketContainer
			this.session = container.connectToServer(Simulator.class,  new URI( "wss://homelaohuji.com/fxCasino/fxLB?gameType=5902" ));	// 该方法会阻塞
			
			session2Simulator.put(this.session.toString(), this);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	@OnOpen
	public void onOpen(Session session){
	}
	
	@OnClose
	public void onClose(){
		try {
			this.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnError
	public void onError(Throwable thr){
	}
	
	public void sendMessage(String message) {
		
		logger.info(threadName+">>>>>>"+message);
		try {
			this.session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				this.session.getBasicRemote().flushBatch();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		
		if(this.session == null) {
			this.session = session;
		}
		
		if(session2Simulator.get(this.session.toString()) != null) {
			if(this.account == null) {
				this.account = session2Simulator.get(this.session.toString()).account;
			}
			
			if(this.threadName == null) {
				this.threadName = session2Simulator.get(this.session.toString()).threadName;
			}
			
			if(this.sessionId == null) {
				this.sessionId = session2Simulator.get(this.session.toString()).sessionId;
			}
			
			if(this.messages == null) {
				this.messages = session2Simulator.get(this.session.toString()).messages;
			}
		}
		
		JSONObject response = JSONObject.fromObject(message);
		
		ResponseAction action = null;
		try {
			action = ResponseAction.valueOf(response.getString("action"));
			if(session2Simulator.get(this.session.toString()) != null) {
				logger.info(session2Simulator.get(this.session.toString()).threadName+"<<<<<<--"+actionSet+"--"+action.getDescribe()+"--"+message);
			} else {
				logger.info("<<<<<<--"+actionSet+"--"+action.getDescribe()+"--"+message);
			}
		} catch(Exception e) {// 不能解析的报文，直接返回
			if(session2Simulator.get(this.session.toString()) != null) {
				logger.info(session2Simulator.get(this.session.toString()).threadName+"<<<<<<--"+actionSet+"--"+message);
			} else {
				logger.info("<<<<<<--"+actionSet+"--"+message);
			}
			return;
		}
		
		JSONObject resultObject = response.getJSONObject("result");
		if(resultObject.isNullObject()) {
			resultObject = null;
		}
		
		switch(actionSet) {
			case LOGIN:// 登录
				login(action, resultObject);
				break;
			case GAMING:// 游戏
				gaming(ResponseAction.onBeginGame, resultObject);
				break;// 回到第一关
			case BACK_FIRST_LEVEL:
				backFirstLevel(action, resultObject);
				break;
			case EXCHANGE_BALANCE_ON_WAY:// 中途换分
				exchangeBalanceOnWay(action, resultObject);
				break;
			case LOGOUT:// 结算离开
				logout(action, resultObject);
				break;
			default:// 强制退出
				forceLogout();
				break;
		}
	}
	
	// 登录
	public void login(ResponseAction responseAction, JSONObject result) {
		actionSet = ActionSet.LOGIN;
		
		if(result != null && !result.getBoolean("event")) {
			// 账户余额是否足够，是-继续，否-退出
			//	{"action":"onLogin","result":{"event":false,"errCode":"NO_MONEY_NO_PLAY","ErrorID":"13670137"}}
			// 分数余额是否足够，是-继续，否-换分，记录换分数据。
			// {'action': 'onCreditExchange', 'result': {'event': True, 'data': {'event': True, 'Credit': 200, 'Balance': 1800.2, 'BetBase': '1:10'}, 'time': [0.07327389717102051, 0.0053958892822265625, 0.06787514686584473]}})
			forceLogout();
			return;
		}
		switch(responseAction) {
			case ready:
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("loginBySid").replace("#{sessionId}", session2Simulator.get(this.session.toString()).sessionId));
				return;	
			case onTakeMachine:
				try {
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("onLoadInfo2"));
				return;
			case onOnLoadInfo2:
				try {
					Thread.sleep(120);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("getMachineDetail"));
				return;
			case onGetMachineDetail:// 打开换分界面以后
				try {
					Thread.sleep(150);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("creditExchange"));
				return;
			case onCreditExchange:// 换分以后
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
				actionSet = ActionSet.GAMING;
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
				return;
			default:
				break;
		}
	}
	
	// 结算离开
	public void logout(ResponseAction responseAction, JSONObject result) {
		actionSet = ActionSet.LOGOUT;
		if(result != null && !result.getBoolean("event")) {
			forceLogout();
			return;
		}
		switch(responseAction) {
			case onLogout:
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("getMachineDetail"));
				return;
			case onGetMachineDetail:
				try {
					Thread.sleep(40);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("balanceExchange"));
				return;
			case onBalanceExchange:
				try {
					Thread.sleep(20);
				} catch (Exception e) {
					e.printStackTrace();
				}
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("leaveMachine"));
				onClose();
				return;
			default:
				break;
		}
	}
	
	// 强制退出
	public void forceLogout() {
		// 关闭websocket
		onClose();
	}
	
	// 回到第一关
	public void backFirstLevel(ResponseAction responseAction, JSONObject result) {
		actionSet = ActionSet.BACK_FIRST_LEVEL;
		if(result != null && !result.getBoolean("event")) {
			logout(ResponseAction.onLogout, null);
			return;
		}
		
		switch(responseAction) {
			case onBackFirstLevel:// 确定回到第一关以后
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("getMachineDetail"));
				return;
			case onGetMachineDetail:// 确定回到第一关以后
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("balanceExchange"));
				return;
			case onBalanceExchange:// 确定回到第一关以后
				outputWagerInfo("回到第一关");
				outputWagerInfo("结算--账户余额："+result.getJSONObject("data").getDouble("Balance") +"--兑换分数：" +result.getJSONObject("data").getString("TransCredit"));
				recordExchangeInfo(result);
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("creditExchange"));
				return;
			case onCreditExchange:// 确定回到第一关以后
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}
                session2Simulator.get(this.session.toString()).account.setBalance(result.getJSONObject("data").getDouble("Balance"));
                session2Simulator.get(this.session.toString()).account.setAmount(Integer.parseInt(result.getJSONObject("data").getString("Credit")));

				outputWagerInfo("换分--账户余额："+result.getJSONObject("data").getDouble("Balance") +"--兑换分数：" +result.getJSONObject("data").getString("Credit"));
				actionSet = ActionSet.GAMING;
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
				return;
			default:
				break;
		}
	}
	
	// 中途换分
	public void exchangeBalanceOnWay(ResponseAction responseAction, JSONObject result) {
		actionSet = ActionSet.EXCHANGE_BALANCE_ON_WAY;
		if(result != null && !result.getBoolean("event")) {
			logout(ResponseAction.onLogout, null);
			return;
		}
		switch(responseAction) {
			case onExchangeBalanceOnWay:// 确定中途换分以后
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("getMachineDetail"));
				return;
			case onGetMachineDetail:// 确定回到第一关以后
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("creditExchange"));
				return;
			case onCreditExchange:// 确定回到第一关以后
				try {
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				actionSet = ActionSet.GAMING;
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
				return;
			default:
				break;
		}
	}
	
	/**游戏中*/
	public void gaming(ResponseAction responseAction, JSONObject result) {
		actionSet = ActionSet.GAMING;
		
		JSONObject data = result.getJSONObject("data");
		
		// 统计拐棍个数
		for(int i = 0; i < data.getJSONArray("Lines").size() ; i++) {
			for(int j = 0 ; j < data.getJSONArray("Lines").getJSONArray(i).size(); j++) {
				
				int elementID = data.getJSONArray("Lines").getJSONArray(i).getJSONObject(j).getInt("ElementID");
				if(elementID == 6) {// 拐棍
					stickNum++;
				}
			}
		}

		int freeTime = data.getInt("FreeTime");
		
		int eliminateCnt = 0;
		
		// 免费游戏则直接返回
		if(freeTime > 0) {
			logger.info(session2Simulator.get(this.session.toString()).threadName+"免费游戏中："+freeTime);
			outputWagerInfo("免费游戏，第" + freeTime + "次") ;
		} else {
			betNum++;
			session2Simulator.get(this.session.toString()).account.setBetNumber(betNum);
			// 拐棍个数大于等于15, 则返回第一关
			logger.info(session2Simulator.get(this.session.toString()).threadName+"拐棍个数："+stickNum);
			if(stickNum >= 15) {
				logger.info(session2Simulator.get(this.session.toString()).threadName+"返回第一关");
				stickNum = 0;
				backFirstLevel(ResponseAction.onBackFirstLevel, null);
				return;
			}
			
			String wagersID = data.getString("WagersID");
			
			String[] luckNums = new String[4];
			luckNums[0] = session2Simulator.get(this.session.toString()).account.getLuckNum1();
			luckNums[1] = session2Simulator.get(this.session.toString()).account.getLuckNum2();
			luckNums[2] = session2Simulator.get(this.session.toString()).account.getLuckNum3();
			luckNums[3] = session2Simulator.get(this.session.toString()).account.getLuckNum4();
			
			// 统计幸运局号(注单)号码
			for(int i = 0 ; i < luckNums.length ; i++) {
				if(wagersID.endsWith(luckNums[i])) {
                    // 更新幸运注单记录
					recordLucyWager(result, session2Simulator.get(this.session.toString()).account.getEliminateWagerIds().size(), stickNum);
                    session2Simulator.get(this.session.toString()).account.getLucyWagerIds().add(wagersID);
                    logger.info(session2Simulator.get(this.session.toString()).threadName+":幸运局号" + wagersID);
					break;
				}
			}
			
			// 游戏点数（分）
			double creditEnd = data.getDouble("Credit_End");
			
			if(creditEnd < 10) {// 如果游戏分不够，则中途换分
				exchangeBalanceOnWay(ResponseAction.onExchangeBalanceOnWay, null);
				return;
			} else if(responseAction != ResponseAction.onBeginGame) {// 如果回包格式不正确，则丢弃
				return;
			}
			
			// 统计连消次数
			for(int i = 0; i < data.getJSONArray("Lines").size() ; i++) {
				for(int j = 0 ; j < data.getJSONArray("Lines").getJSONArray(i).size(); j++) {
					int elementID = data.getJSONArray("Lines").getJSONArray(i).getJSONObject(j).getInt("ElementID");
					if(elementID < 6){// 非拐棍
						eliminateCnt ++;
						break;
					}
				}
			}

			if(eliminateCnt >= session2Simulator.get(this.session.toString()).account.getLeastEliminateCnt()) {
				recordEliminateWagerIds(result, eliminateCnt, stickNum);
				
				// 更新连消记录
				session2Simulator.get(this.session.toString()).account.getEliminateWagerIds().add(wagersID+":"+eliminateCnt);
			}

			outputWagerInfo("游戏分数余额:" + result.getJSONObject("data").getString("Credit_End") +"--连消次数"+ eliminateCnt) ;

			logger.info(session2Simulator.get(this.session.toString()).threadName+":局号：" + wagersID + "--次数："+eliminateCnt);
		}
		
		// 暂停
		try {
			Thread.sleep(session2Simulator.get(this.session.toString()).account.getPeriodSeconds() + random.nextInt(1000));
		} catch (Exception e) {
			e.printStackTrace();	
		}
		
		if(eliminateCnt <= 0) {
			lineBetCount ++;
			if(lineBetCount > 3) {
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2_20"));
				lineBetCount = 0;
			} else {
				session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
			}
		} else {
			session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
		}
	}
	
	public void outputWagerInfo(String message) {
		if(this.messages == null) {
			this.messages = new ArrayList<>();
		}
		
		if(session2Simulator.get(this.session.toString()) != null) {
			session2Simulator.get(this.session.toString()).messages.add(message);
		} else {
			messages.add(message);
		}
	}
	
	/**
	 * 记录连消数据
	 * @param result
	 * @param wagerIdAndNums
	 */
	public void recordEliminateWagerIds(JSONObject result, int eliminateCnt, int stickNum) {
		JSONObject response = new JSONObject();
		
		response.put("wagers_id", result.getJSONObject("data").getDouble("WagersID"));
		response.put("eliminate_cnt", eliminateCnt);
		response.put("stick_num", stickNum);
		response.put("lines", result.getJSONObject("data").getJSONArray("Lines"));
		
		session2Simulator.get(this.session.toString()).account.getEliminateRecords().add(response);
		
//			{"action":"onBeginGame","result":{"event":true,"data":{"event":true,"WagersID":"417344235702","EncryID":null,"BetInfo":{"event":true,"LineNum":"1","LineBet":"10","BetBalanceRate":"1","BetCreditRate":"10","BetCredit":10},"Credit":190,"Credit_End":"197.00","Cards":["4-3-4-2,1-2-1-3,1-1-3-3,1-2-5-3,5-1-2-1","2-4-2-1,1-3-4-5,3-2-1-3,4-2-5-2,5-1-2-1"],"Lines":[[{"ElementID":1,"Grids":"0,4,5,8","GridNum":4,"Payoff":2},{"ElementID":3,"Grids":"3,6,7,11","GridNum":4,"Payoff":5}],[]],"BrickNum":45,"LevelID":1,"PayTotal":7,"FreeTime":0,"DoubleTime":0,"BetTotal":10,"BBJackpot":{"Pools":[{"PoolID":"grand","JPTypeID":1,"PoolAmount":20583131.352584556,"timestamp":1564916247402},{"PoolID":"3819831","JPTypeID":2,"PoolAmount":11445838.140197525,"timestamp":1564916247402},{"PoolID":"3819831-5902","JPTypeID":3,"PoolAmount":9617510.15844473,"timestamp":1564916247402},{"PoolID":"573521233","JPTypeID":4,"PoolAmount":26.602499999999306,"timestamp":1564916247402}]},"BetValue":1,"PayValue":0.7},"time":[1564916247.36685,0.03379106521606445,0.008374929428100586,0.0034661293029785156,0.00016498565673828125,0.000019073486328125,9.5367431640625e-7,0.0000050067901611328125,0.009112119674682617,0.000370025634765625,0.002775907516479492,0.0000021457672119140625,0.0000040531158447265625,0.008796930313110352,0,0.000308990478515625,0.0000059604644775390625,0.0003631114959716797,0.0000069141387939453125]}}
		// e.g. {"pid_num": 8172, "wagers_id": "395872629447", "eliminate_cnt": 8, "stick_num": 6, "lines": [[{"ElementID": 1, "Grids": "1,2,3,5,7,8,9,10,13,14", "GridNum": 10, "Payoff": 30}], [{"ElementID": 2, "Grids": "5,8,9,12", "GridNum": 4, "Payoff": 4}], [{"ElementID": 3, "Grids": "6,8,9,10,12,14", "GridNum": 6, "Payoff": 20}], [{"ElementID": 1, "Grids": "10,12,13,14", "GridNum": 4, "Payoff": 2}], [{"ElementID": 2, "Grids": "6,7,10,11,15", "GridNum": 5, "Payoff": 5}], [{"ElementID": 5, "Grids": "8,9,10,13", "GridNum": 4, "Payoff": 20}], [{"ElementID": 4, "Grids": "10,13,14,15", "GridNum": 4, "Payoff": 10}], [{"ElementID": 3, "Grids": "8,12,13,14", "GridNum": 4, "Payoff": 5}], []]}
		write(session2Simulator.get(this.session.toString()).account.getWebSiteName() + "eliminate_record.txt", response.toString());
	}

	public static void main(String[] args) {
		JSONObject o = JSONObject.fromObject("{\"event\":true,\"data\":{\"event\":true,\"WagersID\":\"417344235702\",\"EncryID\":null,\"BetInfo\":{\"event\":true,\"LineNum\":\"1\",\"LineBet\":\"10\",\"BetBalanceRate\":\"1\",\"BetCreditRate\":\"10\",\"BetCredit\":10},\"Credit\":190,\"Credit_End\":\"197.00\",\"Cards\":[\"4-3-4-2,1-2-1-3,1-1-3-3,1-2-5-3,5-1-2-1\",\"2-4-2-1,1-3-4-5,3-2-1-3,4-2-5-2,5-1-2-1\"],\"Lines\":[[{\"ElementID\":1,\"Grids\":\"0,4,5,8\",\"GridNum\":4,\"Payoff\":2},{\"ElementID\":3,\"Grids\":\"3,6,7,11\",\"GridNum\":4,\"Payoff\":5}],[]],\"BrickNum\":45,\"LevelID\":1,\"PayTotal\":7,\"FreeTime\":0,\"DoubleTime\":0,\"BetTotal\":10,\"BBJackpot\":{\"Pools\":[{\"PoolID\":\"grand\",\"JPTypeID\":1,\"PoolAmount\":20583131.352584556,\"timestamp\":1564916247402},{\"PoolID\":\"3819831\",\"JPTypeID\":2,\"PoolAmount\":11445838.140197525,\"timestamp\":1564916247402},{\"PoolID\":\"3819831-5902\",\"JPTypeID\":3,\"PoolAmount\":9617510.15844473,\"timestamp\":1564916247402},{\"PoolID\":\"573521233\",\"JPTypeID\":4,\"PoolAmount\":26.602499999999306,\"timestamp\":1564916247402}]},\"BetValue\":1,\"PayValue\":0.7},\"time\":[1564916247.36685,0.03379106521606445,0.008374929428100586,0.0034661293029785156,0.00016498565673828125,0.000019073486328125,9.5367431640625e-7,0.0000050067901611328125,0.009112119674682617,0.000370025634765625,0.002775907516479492,0.0000021457672119140625,0.0000040531158447265625,0.008796930313110352,0,0.000308990478515625,0.0000059604644775390625,0.0003631114959716797,0.0000069141387939453125]}");
		
				new Simulator().recordEliminateWagerIds(o, 1 ,2);
	}
	
	/**
	 * 记录幸运注单
	 * @param result
	 * @param wagerId
	 */
	public void recordLucyWager(JSONObject result, int eliminateCnt, int stickNum) {
		JSONObject response = new JSONObject();
		
		response.put("wagers_id", result.getJSONObject("data").getDouble("WagersID"));
		response.put("eliminate_cnt", eliminateCnt);
		response.put("stick_num", stickNum);
		response.put("lines", result.getJSONObject("data").getJSONArray("Lines"));

		session2Simulator.get(this.session.toString()).account.getLucyWagerRecords().add(response);
		
//			{"action":"onBeginGame","result":{"event":true,"data":{"event":true,"WagersID":"417344235702","EncryID":null,"BetInfo":{"event":true,"LineNum":"1","LineBet":"10","BetBalanceRate":"1","BetCreditRate":"10","BetCredit":10},"Credit":190,"Credit_End":"197.00","Cards":["4-3-4-2,1-2-1-3,1-1-3-3,1-2-5-3,5-1-2-1","2-4-2-1,1-3-4-5,3-2-1-3,4-2-5-2,5-1-2-1"],"Lines":[[{"ElementID":1,"Grids":"0,4,5,8","GridNum":4,"Payoff":2},{"ElementID":3,"Grids":"3,6,7,11","GridNum":4,"Payoff":5}],[]],"BrickNum":45,"LevelID":1,"PayTotal":7,"FreeTime":0,"DoubleTime":0,"BetTotal":10,"BBJackpot":{"Pools":[{"PoolID":"grand","JPTypeID":1,"PoolAmount":20583131.352584556,"timestamp":1564916247402},{"PoolID":"3819831","JPTypeID":2,"PoolAmount":11445838.140197525,"timestamp":1564916247402},{"PoolID":"3819831-5902","JPTypeID":3,"PoolAmount":9617510.15844473,"timestamp":1564916247402},{"PoolID":"573521233","JPTypeID":4,"PoolAmount":26.602499999999306,"timestamp":1564916247402}]},"BetValue":1,"PayValue":0.7},"time":[1564916247.36685,0.03379106521606445,0.008374929428100586,0.0034661293029785156,0.00016498565673828125,0.000019073486328125,9.5367431640625e-7,0.0000050067901611328125,0.009112119674682617,0.000370025634765625,0.002775907516479492,0.0000021457672119140625,0.0000040531158447265625,0.008796930313110352,0,0.000308990478515625,0.0000059604644775390625,0.0003631114959716797,0.0000069141387939453125]}}
		// e.g. {"pid_num": 8172, "wagers_id": "395871676888", "eliminate_cnt": 0, "stick_num": 11, "lines": [[{"ElementID": 6, "GridNum": 1, "Grids": "8", "Payoff": 0, "BrickNum": [34]}], []]}
		write(session2Simulator.get(this.session.toString()).account.getWebSiteName() + "wagers_record.txt", response.toString());
	}
	
	/**
	 * 记录换分数据
	 * @param result
	 * @param wagerId
	 */
	public void recordExchangeInfo(JSONObject result) {
		JSONObject response = new JSONObject();
		
		response.put("balance", result.getJSONObject("data").getDouble("Balance"));
		response.put("amount", result.getJSONObject("data").getDouble("Amount"));
		response.put("balance_total", result.getJSONObject("data").getDouble("Balance") + result.getJSONObject("data").getDouble("Amount"));
		response.put("bet_number", session2Simulator.get(this.session.toString()).account.getBetNumber());
		
		session2Simulator.get(this.session.toString()).account.getExchangeRecords().add(response);
		
//		{"action":"onBalanceExchange","result":{"event":true,"data":{"event":true,"TransCredit":"348.00","Amount":34.8,"Balance":103.3,"BetBase":""},"time":[0.05563497543334961,0.001756906509399414,0.053273916244506836,0.0006000995635986328]}}
		// e.g. {"pid_num": 6792, "balance": 72.8, "amount": 4.3, "balance_total": 77.1, "bet_number": 3000}
		write(session2Simulator.get(this.session.toString()).account.getWebSiteName() + "wagers_record.txt", response.toString());
	}
}

enum ActionSet{
	
	LOGIN("登录"),
	
	LOGOUT("退出"), 

	BACK_FIRST_LEVEL("回到第一关"), 
	
	EXCHANGE_BALANCE_ON_WAY("中途换分"),
	
	GAMING("游戏中");
	
	private String describe;
	
	ActionSet(String describe){
		this.describe = describe;
	}
	
	public String getDescribe() {
		return this.describe;
	}
}

enum ResponseAction{

	ready("服务器已准备"),

	onLogin("登录"),
	
	onLogout("退出"),
	
	onTakeMachine("打开菜单面板"),
	
	onOnLoadInfo2("打开账户信息"),
	
	onBackFirstLevel("回到第一关"),
	
	onExchangeBalanceOnWay("中途换分"),
	
	onGetMachineDetail("打开换分界面"),
	
	onCreditExchange("换分"),
	
	onBalanceExchange("结算"),
	
	onBeginGame("开始游戏");
	
	private String describe;
	
	ResponseAction(String describe){
		this.describe = describe;
	}
	
	public String getDescribe() {
		return this.describe;
	}
}