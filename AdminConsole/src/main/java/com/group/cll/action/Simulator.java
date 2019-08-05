package com.group.cll.action;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
	/**
	 * 局号金额，连消次数
	 * 返回第一关换分
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
		
		// 下注，// 每下注一次记录bet_number-打码数，并写入文件
		requestActions.put("beginGame2", "{\"action\":\"beginGame2\",\"line\":\"1\",\"lineBet\":10}");
		
		// 完全退出(前置动作balanceExchange)
		requestActions.put("leaveMachine", "{\"action\":\"leaveMachine\"}");
	}
	
	public List<String> messages;
	
	private Session session;
	
	public String threadName;
	
	/** 连接sessionId*/
	public String sessionId;
	
	public Account account;
	
	/** 模块名称*/
	private ActionSet actionSet = ActionSet.LOGIN;

	private int gridNum = 0;// 拐棍个数
	
	public Simulator() {
		super();
	}
	
	public Simulator(String threadName) {
		super();
		this.threadName = threadName;
	}

	public void play(Account account, String sessionId) {
		this.account = account;
		this.sessionId = sessionId;
		messages = new ArrayList<String>();
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();	// 获得WebSocketContainer
			this.session = container.connectToServer(Simulator.class,  new URI( "wss://homelaohuji.com/fxCasino/fxLB?gameType=5902" ));	// 该方法会阻塞
			
			session2Simulator.put(this.session.toString(), this);
		
		} catch (Exception e) {
			throw new RuntimeException(e);
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
		session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("leaveMachine"));
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
					gridNum++;
				}
			}
		}

		int freeTime = data.getInt("FreeTime");
		
		// 免费游戏则直接返回
		if(freeTime > 0) {
			logger.info(session2Simulator.get(this.session.toString()).threadName+"免费游戏中："+freeTime);
		} else {
			// 拐棍个数大于等于15, 则返回第一关
			logger.info(session2Simulator.get(this.session.toString()).threadName+"拐棍个数："+gridNum);
			if(gridNum >= 15) {
				logger.info(session2Simulator.get(this.session.toString()).threadName+"返回第一关");
				gridNum = 0;
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
			
			int eliminateCnt = 0;
			
			// 统计拐棍个数和连消次数
			for(int i = 0; i < data.getJSONArray("Lines").size() ; i++) {
				for(int j = 0 ; j < data.getJSONArray("Lines").getJSONArray(i).size(); j++) {
					int elementID = data.getJSONArray("Lines").getJSONArray(i).getJSONObject(j).getInt("ElementID");
					if(elementID < 6){// 非拐棍
						eliminateCnt ++;
						break;
					}
				}
			}
			outputWagerInfo(result, eliminateCnt);
			logger.info(session2Simulator.get(this.session.toString()).threadName+":局号：" + wagersID + "--次数："+eliminateCnt);
		}
		
		// 暂停
		try {
			// 用户输入
			Thread.sleep(session2Simulator.get(this.session.toString()).account.getPeriodSeconds() + random.nextInt(1000));
		} catch (Exception e) {
			e.printStackTrace();	
		}
		session2Simulator.get(this.session.toString()).sendMessage(requestActions.get("beginGame2"));
	}
	
	/**
	 * 
	 * @param result
	 */
	public void outputWagerInfo(JSONObject result, int eliminateCnt) {
//		{"action":"onBeginGame","result":{"event":true,"data":{"event":true,"WagersID":"417358987506","EncryID":null,"BetInfo":{"event":true,"LineNum":"1","LineBet":"10","BetBalanceRate":"1","BetCreditRate":"10","BetCredit":10},"Credit":183,"Credit_End":"183.00","Cards":["2-3-3-5,4-2-4-2,3-1-2-3,2-1-3-2,3-5-2-4"],"Lines":[[]],"BrickNum":42,"LevelID":1,"PayTotal":0,"FreeTime":0,"DoubleTime":0,"BetTotal":10,"BBJackpot":{"Pools":[{"PoolID":"grand","JPTypeID":1,"PoolAmount":20625468.24073691,"timestamp":1564920379958},{"PoolID":"3819831","JPTypeID":2,"PoolAmount":11458248.331218395,"timestamp":1564920379958},{"PoolID":"3819831-5902","JPTypeID":3,"PoolAmount":9626724.942645557,"timestamp":1564920379958},{"PoolID":"573521233","JPTypeID":4,"PoolAmount":27.229499999999305,"timestamp":1564920379958}]},"BetValue":1,"PayValue":0},"time":[1564920379.928515,0.025136947631835938,0.008624076843261719,0.0019659996032714844,0.00015091896057128906,0.000027894973754882812,9.5367431640625e-7,0.0000059604644775390625,0.002902984619140625,0.00014495849609375,0.0039179325103759766,0.0000030994415283203125,0.0000040531158447265625,0.006662845611572266,0.0000011920928955078125,0.0003218650817871094,0.0000050067901611328125,0.00037789344787597656,0.0000050067901611328125]}}
		
		String creditEnd = result.getJSONObject("data").getString("Credit_End");
		
		String message = "余额:" + creditEnd +"--连消次数"+ eliminateCnt ;
		
		if(this.messages == null) {
			this.messages = new ArrayList<>();
		}
		
		if(session2Simulator.get(this.session.toString()) != null) {
			session2Simulator.get(this.session.toString()).messages.add(message);
		} else {
			messages.add(message);
		}
	}
	
	public void recordEliminateWagerIds(JSONObject result, String wagerIdAndNums) {
		session2Simulator.get(this.session.toString()).account.getEliminateWagerIds().add(wagerIdAndNums);
		// TODO: 写入文件
	}
	
	public void recordLucyWager(JSONObject result, String wagerId) {
		session2Simulator.get(this.session.toString()).account.getLucyWagerIds().add(wagerId);
		// TODO: 写入文件
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