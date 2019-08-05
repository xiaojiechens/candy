Ext.require([
    'Ext.container.Viewport',
    'Ext.grid.Panel',
    'Ext.grid.plugin.RowEditing',
    'Ext.layout.container.Border',
    'Ext.TaskMgr'
]);

Date.prototype.Format = function (fmt) {
    var o = {
        "y+": this.getFullYear(),
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S+": this.getMilliseconds()             //毫秒
    };
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(fmt)) {
            if (k == "y+") {
                fmt = fmt.replace(RegExp.$1, ("" + o[k]).substr(4 - RegExp.$1.length));
            } else if (k == "S+") {
                var lens = RegExp.$1.length;
                lens = lens == 1 ? 3 : lens;
                fmt = fmt.replace(RegExp.$1, ("00" + o[k]).substr(("" + o[k]).length - 1, lens));
            } else {
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
            }
        }
    }
    return fmt;
}

function ajaxOpenWebPage(domain, accountNo) {
    Ext.Ajax.request({
        url: "./loginWebPage",
        contentType: 'application/json;charset=UTF-8',
        params: {
            domain: domain,
            accountNo: accountNo,
        },
        method: 'POST',
        success: function (response, options) {
        }
    });
}

function ajaxOpenEliminateCntPage(domain, accountNo) {
    Ext.Ajax.request({
        url: "./openEliminateCntPage",
        contentType: 'application/json;charset=UTF-8',
        params: {
            domain: domain,
            accountNo: accountNo,
        },
        method: 'POST',
        success: function (response, options) {
        }
    });
}

function ajaxOpenLucyWagerIdPage(domain, accountNo) {
    Ext.Ajax.request({
        url: "./openLucyWagerIdPage",
        contentType: 'application/json;charset=UTF-8',
        params: {
            domain: domain,
            accountNo: accountNo,
        },
        method: 'POST',
        success: function (response, options) {
        }
    });
}

function ajaxOpenDailySignPage(domain, accountNo) {
    Ext.Ajax.request({
        url: "./openDailySignPage",
        contentType: 'application/json;charset=UTF-8',
        params: {
            domain: domain,
            accountNo: accountNo,
        },
        method: 'POST',
        success: function (response, options) {
        }
    });
}

function ajaxOpenGameWindowUrlPage(domain, accountNo) {
    Ext.Ajax.request({
        url: "./openGameWindowUrlPage",
        contentType: 'application/json;charset=UTF-8',
        params: {
        	domain: domain,
        	accountNo: accountNo
        },
        method: 'POST',
        success: function (response, options) {
        }
    });
}

function ajaxStartGame(domain, accountNo) {
    Ext.Ajax.request({
        url: "./startGame",
        contentType: 'application/json;charset=UTF-8',
        params: {
        	domain: domain,
        	accountNo: accountNo
        },
        method: 'POST',
        success: function (response, options) {
        	if(!response.responseText){
        		return;
        	}
        	// {"threadNames":["candy_0","candy_1","candy_2"]}
        	var o_response = Ext.util.JSON.decode(response.responseText);

            var items = [];
            for(var i in o_response.threadNames){
                items.push({
                    autoScroll : true,// 自动卷轴
                    scrollable : true,
                    frame:true,//渲染框架
                    border : true,//边框
                    split : true,// 分割条面板组合是经常用到
                    height : 600,//自动高度以下参数用于Panel的各个部位工具栏
                    title: o_response.threadNames[i],
                    id: o_response.threadNames[i],
                    text: '',
                    columnWidth: 1/o_response.threadNames.length,
                    html: ''
                });
            }

            var newWin = Ext.create('Ext.window.Window', {
                title: '监控窗口',
                layout: 'column',
                width: 800,
                height: 600,
                autoHeight : true,//自动高度以下参数用于Panel的各个部位工具栏
                items: items,
            });

            ajaxGetMessage(domain, accountNo, o_response.threadNames);
            newWin.show();
        }
    });
}

function ajaxStopGame(domain, accountNo) {
	Ext.Ajax.request({
		url: "./stopGame",
		contentType: 'application/json;charset=UTF-8',
        params: {
        	domain: domain,
        	accountNo: accountNo
        },
		method: 'POST',
		success: function (response, options) {
		}
	});
}

function ajaxGetMessage(domain, accountNo, threadNames) {
    var that = this;
	Ext.Ajax.request({
		url: "./getMessage",
		contentType: 'application/json;charset=UTF-8',
		params: {
			domain: domain,
			accountNo: accountNo
		},
		method: 'POST',
		success: function (response, options) {
            // e.g {"candy_2":["启动"],"candy_0":["启动"],"candy_1":["启动"]}
            var threadMessages = Ext.util.JSON.decode(response.responseText);

            for(var i in threadNames){
                var messages = threadMessages[threadNames[i]];
                var text = "";
                for(var j in messages){
                    text = text + messages[j] + "<br>" ;
                }
                if(Ext.getCmp(threadNames[i]).text.length > 2000){
                	Ext.getCmp(threadNames[i]).body.update(text);
                	Ext.getCmp(threadNames[i]).text = text;
                } else{
                	Ext.getCmp(threadNames[i]).body.update(Ext.getCmp(threadNames[i]).text + text);
                	Ext.getCmp(threadNames[i]).text = Ext.getCmp(threadNames[i]).text + text;
                }
                
                Ext.getCmp(threadNames[i]).scrollBy(0,1);//滚动条向下移动一个像素
            }
            if (!that.messageRunner) {
                that.messageRunner = {};
                that.onMessageTaskRunner = {};
                if (!that.messageRunner[accountNo+"_"+domain]) {
                    that.messageRunner[accountNo+"_"+domain] = new Ext.util.TaskRunner();
                    that.onMessageTaskRunner[accountNo+"_"+domain] = that.messageRunner[accountNo+"_"+domain].start({
                        run: function () {
                            ajaxGetMessage(domain, accountNo, threadNames);
                        },
                        interval: 1000
                    });
                    that.messageRunner[accountNo+"_"+domain].start(that.onMessageTaskRunner[accountNo+"_"+domain]);
                }
            }
		}
	});
}

Ext.onReady(function () {

/**重写ext filed组件, 实现表单必填项加红色*星号**/
Ext.override(Ext.form.field.Base,{
    initComponent:function(){
        if(this.allowBlank!==undefined && !this.allowBlank){
            if(this.fieldLabel){
                this.fieldLabel += '<font color=red>*</font>';
            }
        }
        this.callParent(arguments);
    }
});

    Ext.tip.QuickTipManager.init();
    var store = new Ext.data.JsonStore({
        remoteSort: true,
        fields: [
            {name: 'accountNo', type: 'string', convert: null},
            {name: 'webSiteName', type: 'string', convert: null},
            {name: 'domain', type: 'string', convert: null},
            {name: 'balance', type: 'float', convert: null},
            {name: 'amount', type: 'float', convert: null},
            {name: 'maxEliminateCntWagersIdAndNum', type: 'int', convert: null},
            {name: 'luckiestWagersIds', type: 'string', convert: null},
            {name: 'betNumber', type: 'int', convert: null},
            {name: 'bombIndex', type: 'string', convert: null},
            {name: 'lastFiveBombScore', type: 'string', convert: null},
            {name: 'activities', type: 'auto', convert: null},
        ],
        proxy: {
            type: 'ajax',
            url: './getIndexes',
            reader: {
                type: 'json',
                rootProperty: 'topics'
            }
        },
        autoLoad: true
    });

    Ext.create('Ext.container.Viewport', {
        layout: 'border',
        rtl: true,
        items: [{
            id: 'northPanel',
            region: 'north',
            title: '当前时间:${curTime}&nbsp;&nbsp;&nbsp;&nbsp;本机余额:${localBalance}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
            split: true
        }, {
            region: 'center',
            xtype: 'gridpanel',
            columns: [{
                xtype: 'rownumberer', flex: 0.1, sortable: false
            }, {
                dataIndex: 'accountNo', flex: 0.5, header: '账号', field: {xtype: 'textfield'}
            }, {
            	dataIndex: 'webSiteName', flex: 0.5, header: '网站地址', field: {xtype: 'textfield'},
            	renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
            		var domain = record.data["domain"];
            		var accountNo = record.data["accountNo"];
            		return '<a title="' + domain + '" href="javascript:void(0)" onclick="ajaxOpenWebPage(\'' + domain + '\',\'' + accountNo + '\')" target="_blank">' + value + '</a>';
            	}
            }, {
                dataIndex: 'domain', flex: 0.8, header: '网站域名', field: {xtype: 'textfield'}, hidden: true
            }, {
                dataIndex: 'balance', flex: 0.5, header: '账户余额', align: "right", field: {xtype: 'textfield'}
            }, {
                dataIndex: 'amount', flex: 0.5, header: '换分总额', align: "right", field: {xtype: 'textfield'}
            }, {
                dataIndex: 'maxEliminateCntWagersIdAndNum', flex: 0.8, header: '连消次数', align: "right", field: {xtype: 'numberfield'},
                renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                    var domain = record.data["domain"];
                    var accountNo = record.data["accountNo"];
                    var eliminateCnt = record.data["eliminateCnt"];
                    return '<a title="' + value + '" href="javascript:void(0)" onclick="ajaxOpenEliminateCntPage(\'' + domain + '\',\'' + accountNo + '\',\'' + eliminateCnt + '\')" target="_blank">' + value + '</a>';
                }
            }, {
                dataIndex: 'luckiestWagersIds', flex: 0.8, header: '幸运注单', align: "right", field: {xtype: 'numberfield'},
                renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                    var domain = record.data["domain"];
                    var accountNo = record.data["accountNo"];
                    var wagersId = record.data["wagersId"];
                    return '<a title="' + value + '" href="javascript:void(0)" onclick="ajaxOpenLucyWagerIdPage(\'' + domain + '\',\'' + accountNo + '\',\'' + wagersId + '\')" target="_blank">' + value + '</a>';
                }
            }, {
                dataIndex: 'betNumber', flex: 0.5, header: '打码总数', align: "right", field: {xtype: 'numberfield'},
                renderer: function (value, cellmeta, record, rowIndex, columnIndex, store) {
                    var domain = record.data["domain"];
                    var accountNo = record.data["accountNo"];
                    return '<a title="' + value + '" href="javascript:void(0)" onclick="openDailySignPage(\'' + domain + '\',\'' + accountNo + '\')" target="_blank">' + value + '</a>';
                }
            }, {
                dataIndex: 'bombIndex', flex: 1.2, header: '爆分指标', align: "right", field: {xtype: 'textfield'}
            }, {
                dataIndex: 'lastFiveBombScore', flex: 1, header: '近五次爆分', align: "right", field: {xtype: 'textfield'}
            }, {
                header: '打开游戏', flex: 0.5, xtype: 'actioncolumn', align: "center",
                items: [{
                    tooltip: '打开游戏',
                    icon: "resources/icons/main-open.png",
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        var domain = record.data["domain"];
                        var accountNo = record.data["accountNo"];

                        ajaxOpenGameWindowUrlPage(domain, accountNo);
                    }
                }]
            }, {
                header: '开始挂机', flex: 0.5, xtype: 'actioncolumn', align: "center",
                items: [{
                    tooltip: '开始挂机',
                    icon: "resources/icons/main-start.png",
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        var domain = record.data["domain"];
                        var accountNo = record.data["accountNo"];
                        ajaxStartGame(domain, accountNo);
                    }
                }]
            }, {
                header: '停止挂机', flex: 0.5, xtype: 'actioncolumn', align: "center",
                items: [{
                    tooltip: '停止挂机',
                    icon: "resources/icons/main-shutdown.png",
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        var domain = record.data["domain"];
                        var accountNo = record.data["accountNo"];
                        ajaxStopGame(domain, accountNo);
                    }
                }]
            }],
            store: store,
            viewConfig: {
                loadMask: false
            },
            listeners: {
            	rowclick: function(grid, rowIndex, e){

            		var selectedRow = grid.getSelectionModel().getSelected().items[0];

            		var domain = selectedRow.data['domain'];
            		var accountNo = selectedRow.data['accountNo'];

                    Ext.Ajax.request({
                        url: "./queryAccount",
                        contentType: 'application/json;charset=UTF-8',
                        params: {
                        	domain: domain,
                        	accountNo: accountNo,
                        },
                        method: 'POST',
                        success: function (response, options) {
                        	var o_response = Ext.util.JSON.decode(response.responseText)
				    		Ext.getCmp('domain').setValue(o_response.domain);
				    		Ext.getCmp('gameWindowUrl').setValue(o_response.gameWindowUrl);
				    		Ext.getCmp('luckNum1').setValue(o_response.luckNum1);
				    		Ext.getCmp('luckNum2').setValue(o_response.luckNum2);
				    		Ext.getCmp('luckNum3').setValue(o_response.luckNum3);
				    		Ext.getCmp('luckNum4').setValue(o_response.luckNum4);
				    		Ext.getCmp('accountNo').setValue(o_response.accountNo);
				    		Ext.getCmp('password').setValue(o_response.password);
				    		Ext.getCmp('leastEliminateCnt').setValue(o_response.leastEliminateCnt);
				    		Ext.getCmp('periodSeconds').setValue(o_response.periodSeconds);
				    		Ext.getCmp('simulatorNums').setValue(o_response.simulatorNums);
                        }
                    });
				}
			}
        }, {
            id: 'southPanel',
            region: 'south',
            title: '增加-修改-删除网站和账户信息',
            collapsed: false,
            collapsible: true,
            layout: 'form',
            frame: true,
            items: [{
                columnWidth: 1,
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    columnWidth: 0.6,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '网站域名',
                    name: 'domain',
                    id: 'domain'
                }]
            },{
                columnWidth: 1,
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    columnWidth: 0.6,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '游戏网址',
                    name: 'gameWindowUrl',
                    id: 'gameWindowUrl'
                }]
            },{
                columnWidth: 1,
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    columnWidth: 0.2,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '局号保留',
                    name: 'luckNum1',
                    id: 'luckNum1'
                },{
                    xtype: 'textfield',
                    columnWidth: 0.2,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '-',
                    name: 'luckNum2',
                    id: 'luckNum2'
                },{
                    xtype: 'textfield',
                    columnWidth: 0.2,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '-',
                    name: 'luckNum3',
                    id: 'luckNum3'
                },{
                    xtype: 'textfield',
                    columnWidth: 0.2,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '-',
                    name: 'luckNum4',
                    id: 'luckNum4'
                }]
            }, {
                columnWidth: 1.0,
                layout: 'column',
                labelAlign: 'right',
                items: [{
                    xtype: 'textfield',
                    columnWidth: 0.3,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '账号',
                    name: 'accountNo',
                    id: 'accountNo'
                }, {
                    xtype: 'textfield',
                    columnWidth: 0.3,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '密码',
                    name: 'password',
                    id: 'password'
                }, {
                    xtype: 'textfield',
                    columnWidth: 0.3,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '连消',
                    name: 'leastEliminateCnt',
                    id: 'leastEliminateCnt'
                }]
            }, {
                columnWidth: 1.0,
                layout: 'column',
                items: [{
                    xtype: 'textfield',
                    columnWidth: 0.3,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '发包时间',
                    name: 'periodSeconds',
                    id: 'periodSeconds'
                }, {
                    xtype: 'textfield',
                    columnWidth: 0.3,
                    allowBlank: false,
                    labelAlign: 'right',
                    fieldLabel: '挂机数量',
                    name: 'simulatorNums',
                    id: 'simulatorNums'
                }]
            }],
            tbar: [
                {
                    text: '保存账号和网站',
                    icon: "resources/icons/main-add.png",
                    handler: function () {
                    	var domain = Ext.getCmp('domain').getValue();
                    	var gameWindowUrl = Ext.getCmp('gameWindowUrl').getValue();
                    	var luckNum1 = Ext.getCmp('luckNum1').getValue();
                    	var luckNum2 = Ext.getCmp('luckNum2').getValue();
                    	var luckNum3 = Ext.getCmp('luckNum3').getValue();
                    	var luckNum4 = Ext.getCmp('luckNum4').getValue();
                    	var accountNo = Ext.getCmp('accountNo').getValue();
                    	var password = Ext.getCmp('password').getValue();
                    	var leastEliminateCnt = Ext.getCmp('leastEliminateCnt').getValue();
                    	var periodSeconds = Ext.getCmp('periodSeconds').getValue();
                    	var simulatorNums = Ext.getCmp('simulatorNums').getValue();

                        Ext.Ajax.request({
                            url: "./saveOrUpdateAccount",
                            contentType: 'application/json;charset=UTF-8',
                            params: {
                            	domain: domain,
                            	gameWindowUrl: gameWindowUrl,
                            	luckNum1: luckNum1,
                            	luckNum2: luckNum2,
                            	luckNum3: luckNum3,
                            	luckNum4: luckNum3,
                            	accountNo: accountNo,
                            	password: password,
                            	leastEliminateCnt: leastEliminateCnt,
                            	periodSeconds: periodSeconds,
                            	simulatorNums: simulatorNums,
                            },
                            method: 'POST',
                            success: function (response, options) {
        				    	Ext.MessageBox.show({
    					           title: '提示',
    					           msg: '保存成功',
    					           icon:Ext.MessageBox.INFO,
    					           buttons:Ext.Msg.OK,
    					           closable:true,
	       					    });
                            }
                        });
                    }
                }, {
                    text: '删除',
                    icon: "resources/icons/main-delete.png",
                    handler: function () {
                    	var domain = Ext.getCmp('domain').getValue();
                    	var accountNo = Ext.getCmp('accountNo').getValue();

                        Ext.Ajax.request({
                            url: "./deleteAccount",
                            contentType: 'application/json;charset=UTF-8',
                            params: {
                            	domain: domain,
                            	accountNo: accountNo,
                            },
                            method: 'POST',
                            success: function (response, options) {
    				    		Ext.getCmp('domain').setValue("");
    				    		Ext.getCmp('gameWindowUrl').setValue("");
    				    		Ext.getCmp('luckNum1').setValue("");
    				    		Ext.getCmp('luckNum2').setValue("");
    				    		Ext.getCmp('luckNum3').setValue("");
    				    		Ext.getCmp('luckNum4').setValue("");
    				    		Ext.getCmp('accountNo').setValue("");
    				    		Ext.getCmp('password').setValue("");
    				    		Ext.getCmp('leastEliminateCnt').setValue("");
    				    		Ext.getCmp('periodSeconds').setValue("");
    				    		Ext.getCmp('simulatorNums').setValue("");

        				    	Ext.MessageBox.show({
     					           title: '提示',
     					           msg: '删除成功',
     					           icon:Ext.MessageBox.INFO,
     					           buttons:Ext.Msg.OK,
     					           closable:true,
 	       					    });
                            }
                        });
                    }
                }
            ]
        }]
    });
    var me = this;
    if (!me.runner) {
        var northPanelTitle = Ext.getCmp('northPanel').getTitle();
        me.runner = new Ext.util.TaskRunner();
        this.onTaskRunner = me.runner.start({
            run: function () {
                store.reload();
                Ext.getCmp('northPanel').setTitle(northPanelTitle.replace('${curTime}', new Date().Format("yyyy/M/d hh:mm:ss")).replace('${localBalance}', store.sum('balance')));
            },
            interval: 1000
        });
        me.runner.start(this.onTaskRunner);
    }

});