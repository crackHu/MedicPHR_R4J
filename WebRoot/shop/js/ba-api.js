//接口验证
var timeStamp, nonceStr, signature, isdd = false, endTime;


function apiCheck(callback) {

	//isv取消这个注释
	var corpid = ddw.corpid;
    if (corpid == 'undefined' || corpid == null || corpid == '')
        corpid = ddw.corpId;
    var apiList = ['ui.pullToRefresh.stop'    , //停止下拉刷新
        'ui.pullToRefresh.enable'   ,           //开始下拉刷新
        'document.addEventListener' ,           //监听返回键（安卓）
        'biz.navigation.setLeft'    ,           //设置导航栏左键（苹果）
        'biz.navigation.setRight'   ,           //设置导航栏右键
        'biz.util.datepicker'       ,           //日期选择器
        'biz.util.datetimepicker'   ,           //日期时间选择器
        'biz.navigation.setTitle'   ,           //设置导航栏标题
        'biz.util.openLink'         ,           //浏览器打开页面
        'biz.util.open'             ,           //打开应用内页面
        'biz.contact.createGroup'   ,  //创建群组
        'biz.util.previewImage'     ,           //图片预览
        'biz.util.uploadImage'      ,           //上传图片
        'biz.chat.toConversation'   ,
        'biz.chat.pickConversation',
        'biz.chat.chooseConversationByCorpId',
        'biz.util.share'];                         //分享
	//corpid = 'ding7994267727edd4e5';
	//alert('corpid========================='+corpid);

	if (corpid != 'undefined' && corpid != null && corpid != ''){
		setCookie("corpId", corpid);
	}

	//alert('getCookie(corpId)========================='+getCookie('corpId'));
	getAjax('{"din":{"url":"'+ location.href +'","corpId":"'+getCookie('corpId')+'"},"fid":"sha1Encrypt","pid":"boVdsSha1Encrypt"}', function (d) {
        timeStamp = ddw.ajax.data.timestamp;
        nonceStr = ddw.ajax.data.noncestr;
        signature = ddw.ajax.data.signature;
        endTime = ddw.ajax.data.endTime;
		//alert('ddw.ajax.data === '+JSON.stringify(ddw.ajax.data));
        //console.log('ddw.ajax.data === '+JSON.stringify(ddw.ajax.data));
       	dd.config({
		    agentId: ddw.ajax.data.agertId, // 必填，微应用ID
		    corpId: getCookie('corpId'),//必填，企业ID
		    timeStamp: timeStamp, // 必填，生成签名的时间戳
		    nonceStr: nonceStr, // 必填，生成签名的随机串
		    signature: signature, // 必填，签名
		    jsApiList: apiList // 必填，需要使用的jsapi列表

		});
		//alert("check");
        console.log("apicheck endtime: "+ endTime + "||now time: " + parseInt(Date.parse(new Date())));
		//alert("endTime:"+endTime);
		//alert("callback:"+callback);
		//alert(endTime > parseInt(Date.parse(new Date())));
        if (endTime > parseInt(Date.parse(new Date())))
        {
            console.log("11111111111111");
            setCookie("refreshCount", 0);
			callback();
            return callback;
        }
        else if (callback!=null && callback != "undefined" && endTime < parseInt(Date.parse(new Date())))
        {
            console.log("................")
            var refresh = getCookie("refreshCount");
            if (checkParams(refresh)=="")
                refresh = 0;
            if (parseInt(refresh)  < 2)
            {
                setCookie("refreshCount", parseInt(refresh)+1);
                //init();
				apiCheck(callback);
                return apiCheck(callback);
            }
            else
            {
                return null;
            }

            //location.reload();
        }

    });
	
}

//登录
function dm_login(loginCallback) {

    dd.ready(function() {
        //logger.i('dd.ready rocks!');

        dd.runtime.info({
            onSuccess: function(info) {
                //logger.i('runtime info: ' + JSON.stringify(info));
            },
            onFail: function(err) {
                //logger.e('runtime info fail: ' + JSON.stringify(err));
            }
        });

        dd.runtime.permission.requestAuthCode({
            corpId: getCookie('corpId'),
            onSuccess: function (info) {
                delCookie("refreshCount");
                //logger.i('authcode: ' + info.code);
                var code = info.code;

                var v_call = '{"din":{"code":"'+ code +'","corpId":"' + getCookie('corpId') + '"},"fid":"login","pid":"boVdsLogin"}';

                if(DBG_INT_TEST)
                    alert("loadIden.v_call: " + v_call);

                var login_suc = function(d_1){
                    if (d_1 == null || d_1 == "undefined" || d_1 == "")
                        return false;

                    var d1emplId = checkParams(d_1.emplId);
                    var d1noPeatient = checkParams(d_1.noPeatient);
                    var d1memberId = checkParams(d_1.memberId);
                    var d1hospitalId = checkParams(d_1.hospitalId);
                    var d1type = checkParams(d_1.type);
                    var d1name = checkParams(d_1.name);
                    //alert("d1hospitalId: " + d1hospitalId);
                    setCookie("l_token", code);
                    setCookie("uId",d1memberId);
                    setCookie("hId",d1hospitalId);
                    setCookie("mId",d1memberId);
                    setCookie("uType",d1type);
                    setCookie("uName",d1name);
                    setCookie("eId",d1emplId);
                    setCookie("noPat",d1noPeatient);
                    if (loginCallback)
                        loginCallback(d_1);
                };

                (v_call, login_suc);
            },
            onFail: function (err) {
                //logger.e('authcode fail: ' + JSON.stringify(err));
                //alert("====="+JSON.stringify(err));
            }
        });
    });

    dd.error(function(err) {
        //alert("系统异常，请联系客服：400-8319-13");
        //alert('dd error: ' + JSON.stringify(err));
        console.log('dd error: ' + JSON.stringify(err));
        //location_jump(location.href);
/*
        var v_call_1 = '{"din":{"corpId":"'+getCookie('corpId')+'"},"fid":"updateTicket","pid":"boVdsUpdateTicket"}';
        data_get(v_call_1, function(){});

        console.log('dd error: ' + JSON.stringify(err));
        var refresh = getCookie("refreshCount");
        if (checkParams(refresh)=="")
            refresh = 0;
        if (refresh < 2)
        {
            setCookie("refreshCount", refresh+1);
            //init();
            location_jump(location.href);
        }
        */
    });

}
// 须把相关接口放在ready函数中调用来确保正确执行
// 后期会加入接口权限校验机制，通过dd.config配置，本期暂没有开通该功能
dd.ready(function() {isdd = true;});

//聊天窗口
function chat(exam_id) {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(chat(exam_id));
        }
		dd.biz.util.open({
			name : "chat",//call 免费电话
			params : {
				users : exam_id,
				corpId: getCookie('corpId')
			},
			onSuccess : function(result) {},
			onFail : function(err) {}
		});
	});
}

//个人资料页面
function personalData(exam_id){	

	dd.ready(function() {
		//alert("personalData");
        
		dd.biz.util.open({
		    name: 'profile',
		    params: {
		        id: exam_id,
		        corpId: getCookie('corpId'),
		    },
		    onSuccess : function(result) {
				a},
		    onFail : function(err) {
				//alert(JSON.stringify(err)+"123");
				}
		});
	});
}

//提示并跳转
function ddToast(txt,url) {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddToast(txt,url));
        }
		dd.device.notification.toast({
		    icon: '', //icon样式，有success和error，默认为空 0.0.2
		    text: txt, //提示信息
		    duration: 2, //显示持续时间，单位秒，默认按系统规范[android只有两种(<=2s >2s)]
		    delay: 0, //延迟显示，单位秒，默认0
		    onSuccess : function(result) {
		    	if ("" == url || null == url) {
		    		return;
		    	} else {		    		
		    		location.href = url + "?rnd=" + Math.random();
		    	}
		    },
		    onFail : function(err) {}
		});
		
	});

    if (!isdd)
        alert(txt);
}

//打开远程连接
function openLink (href) {
    console.log("endTime： " + endTime + "||now: "+ parseInt(Date.parse(new Date())));
    /*if (endTime < parseInt(Date.parse(new Date())) && parseInt(getCookie("refreshCount")) < 2)
    {

    }*/
	dd.ready(function() {

		dd.biz.util.openLink({
			url: href,
			enableShare: "ture",
			onSuccess : function(result) {
			},
			onFail : function(err) {}
		});	
	});
} 

//图片放大和图片浏览
function photoesGallery (urls, current) {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(photoesGallery (urls, current));
        }
		dd.biz.util.previewImage({
		    urls: [urls],//图片地址列表
		    current: current,//当前显示的图片链接
		    onSuccess : function(result) {},
		    onFail : function(err) {}
		});
	});
}

//显示菊花
function showPreloader () {	
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(showPreloader ());
        }
		dd.device.notification.showPreloader({
		    text: "使劲加载中..", //loading显示的字符，空表示不显示文字
		    showIcon: true, //是否显示icon，默认true
		    onSuccess : function(result) {},
		    onFail : function(err) {}
		});
	});
}

//隐藏菊花
function hidePreloader () {	
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(hidePreloader ());
        }
		dd.device.notification.hidePreloader({
		    onSuccess : function(result) {},
		    onFail : function(err) {}
		});
	});
}

//设置页面title
function setTitle(title) {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(setTitle (title));
        }
		dd.biz.navigation.setTitle({
		    title : title,//控制标题文本，空字符串表示显示默认文本
		    onSuccess : function(result) { },
		    onFail : function(err) {}
		});	
	});
}

//触发关闭
function navClose() {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(navClose ());
        }
		dd.biz.navigation.close({
		    onSuccess : function(result) {},
		    onFail : function(err) {}
		});
	});	
}


//定制返回按钮
function backButton(href){
	
	var u = navigator.userAgent;
	var isIOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
	//var isIOS = isIOS();
	if(isIOS==true){
		//apiCheck(["biz.navigation.setLeft"]);
		dd.ready(function(){
			dd.biz.navigation.setLeft({
			    show: true,//控制按钮显示， true 显示， false 隐藏， 默认true
			    control: true,//是否控制点击事件，true 控制，false 不控制， 默认false
			    showIcon: true,//是否显示icon，true 显示， false 不显示，默认true； 注：具体UI以客户端为准
			    text: '',//控制显示文本，空字符串表示显示默认文本
			    onSuccess : function(result) {
			        
			        href();
			    },
			    onFail : function(err) {
			    	alert("IOS返回按钮");
			    }
			});
			
		});
		
	}else{
		apiCheck(["document.addEventListener"]);
		dd.ready(function(){
			document.addEventListener('backbutton', function(e) {
				href();
			}, false);
		});
	}
}

//返回按钮(android)
function backButtonEvt(url) {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(backButtonEvt(url));
        }
		document.addEventListener('backbutton', function(e) {
		    e.preventDefault();
		    if (url == "")
	    		history.back(-1);
		    else if (url == "homepage")
		    	navClose();
	    	else
	    		location.href = url;
		}, false);
	});
	
}

//设置导航栏左按钮(iOS)
function setLeftBtn(url) {
    dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(setLeftBtn(url));
        }
        dd.biz.navigation.setLeft({
            show: true,//控制按钮显示， true 显示， false 隐藏， 默认true
            control: true,//是否控制点击事件，true 控制，false 不控制， 默认false
            showIcon: true,//是否显示icon，true 显示， false 不显示，默认true； 注：具体UI以客户端为准
            text: '',//控制显示文本，空字符串表示显示默认文本
            onSuccess : function(result) {
                if (url == "")
                    history.back(-1);
                else if (url == "homepage")
                    navClose();
                else
                    location.href = url;
            },
            onFail : function(err) {}
        });
    });
}

//设置导航栏右边按钮
function setRightBtn(sucFunc, controler, text) {

    dd.ready(function(){
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(setRightBtn(sucFunc, controler, text));
        }
        dd.biz.navigation.setRight({
            show: true,//控制按钮显示， true 显示， false 隐藏， 默认true
            control: controler,//是否控制点击事件，true 控制，false 不控制， 默认false
            text: text,//控制显示文本，空字符串表示显示默认文本
            onSuccess : function(result) {
                sucFunc(result);
            },
            onFail : function(err) {
              console.log('setRightBtn! Errror!!!!');
            }
        });
    });
}

//下拉刷新
function pullToRefresh() {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(pullToRefresh());
        }
		dd.ui.pullToRefresh.enable({
		    onSuccess: function() {
		    	dd.ui.pullToRefresh.stop();		//收起下拉
		    	//dd.ui.pullToRefresh.disable();	//禁用下拉
		    	location.reload();
		    },
		    onFail: function() {}
		});
	});
}

//禁用下拉
function disableToRefresh() {
	dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(disableToRefresh());
        }
		dd.ui.pullToRefresh.disable();	//禁用下拉
	});
}


//震动
function vibrate() {
	dd.ready(function (){
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(vibrate());
        }
		dd.device.notification.vibrate({
		    duration: 300, //震动时间，android可配置 iOS忽略
		    onSuccess : function(result) {
		    },
		    onFail : function(err) {}
		});
	});
}

//弹窗
function ddAlert(msg, title, bTxt) {
	dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddAlert(msg, title, bTxt));
        }
		dd.device.notification.alert({
		    message: msg,
		    title: title,// 可传空
		    buttonName: bTxt,
		    onSuccess : function() {
		        /*回调*/
		    },
		    onFail : function(err) {}
		});
	});

    if (!isdd)
        alert(msg);
}

//唤醒（webview)
function ddResume() {
	dd.ready(function (){
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddResume());
        }
		document.addEventListener('resume', function(e) {
		    e.preventDefault();
		}, false);
	});
}


//日期选择器
function ddDatepicker(nowDate, func) {	
	dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddDatepicker(nowDate, func))
        }
		dd.biz.util.datepicker({
		    format: 'yyyy-MM-dd',
		    value: nowDate, //默认显示日期
		    onSuccess: func,
		    onFail : function() {}
		});
	});
}

//时间选择器
function ddTimepicker(nowDate, func) {
    dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddTimepicker(nowDate, func))
        }
        dd.biz.util.timepicker({
            format: 'HH:mm',
            value: nowDate, //默认显示时间
            onSuccess: func,
            onFail : function() {}
        });
    });
}

//日期+时间选择器
function ddDatetimepicker(nowDate, format, func) {

    dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddDatetimepicker(nowDate, format, func));
        }
        dd.biz.util.datetimepicker({
            format: format,
            value: nowDate, //默认显示日期+时间
            onSuccess: func,
            onFail : function() {}
        });
    });
}

//下拉选择
function ddChosen(source, func) {
	dd.ready(function (){
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddChosen(source, func));
        }
		dd.biz.util.chosen({
		    source: source/*[{key: '选项1', value: '123'},
		            {key: '选项2', value: '234'}]*/,
		    onSuccess : func,
		    onFail : function() {}
		});
	});
}

//确认弹窗
function ddConfirm(msg, tit, cancelTxt, okTxt, sucFunc) {
	dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddConfirm(msg, tit, cancelTxt, okTxt, sucFunc));
        }
		dd.device.notification.confirm({
		    message: msg,
		    title: tit,
		    buttonLabels: [cancelTxt, okTxt],
		    onSuccess : sucFunc,
		    onFail : function(err) {}
		});
	});
}

function ddShare(url, title, content, image) {
    var type = arguments[4]?arguments[4]:2;
    var suncFunc = arguments[5]?arguments[5]:"false";

	dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddShare(url, title, content, image, type, suncFunc));
        }
		dd.biz.util.share({
			type: type,//分享类型，0:全部组件 默认； 1:只能分享到钉钉；2:不能分享，只有刷新按钮
			url: url,
			title: title,
			content: content,
			image: image,
			onSuccess : function() {
                if (suncFunc != "false")
                    suncFunc();
            },
			onFail : function(err) {}
		});
	});
}

//输入弹窗
function ddPrompt(msg, tit, cancelTxt, okTxt, sucFunc) {
	dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddPrompt(msg, tit, cancelTxt, okTxt, sucFunc));
        }
		dd.device.notification.prompt({
			message: msg,
			title: tit,
			buttonLabels: [cancelTxt, okTxt],
			onSuccess : sucFunc/*function(result) {
				//onSuccess将在点击button之后回调
				/!*
				 {
				 buttonIndex: 0, //被点击按钮的索引值，Number类型，从0开始
				 value: '' //输入的值
				 }
				 *!/
			}*/,
			onFail : function(err) {}
		});
	});
}

//创建群组
function ddCreateGroup(emplIdList, sucFunc) {
    dd.ready(function () {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddCreateGroup(emplIdList, sucFunc));
        }
        dd.biz.contact.createGroup({
            corpId: getCookie('corpId'), //企业id，可选，配置该参数即在指定企业创建群聊天
            users: [emplIdList], //默认选中的用户工号列表，可选；使用此参数必须指定corpId
            onSuccess: sucFunc/*function(result) {
                /!*{
                 id: 123   //企业群id
                 }*!/
            }*/,
            onFail: function(err) {
            }
        });
    });
}

//获取会话信息
function ddPickConversation(isConfirm, sucFunc){
    dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddPickConversation(isConfirm, sucFunc));
        }
        dd.biz.chat.chooseConversationByCorpId({
            corpId: getCookie('corpId'), //企业id
            //isConfirm:isConfirm, //是否弹出确认窗口，默认为true
            onSuccess : sucFunc/*function() {
                //onSuccess将在选择结束之后调用
                /!*{
                 cid: 'xxxx',
                 title:'xxx'
                 }*!/
            }*/,
            onFail : function() {console.log("err............................")}
        });
    });
}

//根据chatid跳转到对应会话
function ddToConversation(chatId) {
    dd.ready(function() {
        if (endTime < parseInt(Date.parse(new Date())))
        {
            return apiCheck(ddToConversation(chatId));
        }
        dd.biz.chat.toConversation({
            corpId: getCookie('corpId'), //企业id
            chatId:chatId,//会话Id
            onSuccess : function() {console.log("success");},
            onFail : function(err) {console.log("error:" + JSON.stringify(err));}
        });
    });
}