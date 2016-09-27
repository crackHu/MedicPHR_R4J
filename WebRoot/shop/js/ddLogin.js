//window.location.href = "http://192.168.1.102:8080/vdsCXH/shop/index.html?openid=12345678";

var timeStamp, nonceStr, signature;
ddgetAjax(
		'{"din":{"url":"'
				+  location.href
				+ '","corpId":"ding688410f040b30d55"},"fid":"sha1Encrypt","pid":"boVdsSha1Encrypt"}',
		function(d) {

			var v_rst = JSON.stringify(ddw.ajax.data);
			timeStamp = ddw.ajax.data.timestamp;
			nonceStr = ddw.ajax.data.noncestr;
			signature = ddw.ajax.data.signature;
			//alert(timeStamp);
			dd.config({
				//debug: false,
				//appId: 'ding239a3f9c77d7a402',
				agentId : ddw.ajax.data.agertId, // 必填，微应用ID 5761771
				corpId : getCookie('corpId'),//必填，企业ID
				timeStamp : timeStamp, // 必填，生成签名的时间戳
				nonceStr : nonceStr, // 必填，生成签名的随机串
				signature : signature, // 必填，签名
				jsApiList : [ 'device.notification.alert',
						'device.notification.confirm', 'biz.util.open',
						'biz.util.openLink' ]
			// 必填，需要使用的jsapi列表
			});

			alert(timeStamp + ":" + nonceStr + ":" + signature + ":"
					+ ddw.ajax.data.agertId + ":" + getCookie('corpId'));
		});
//showPreloader();					
var code;
//$( getUserId );
// 登陆
function getUserId() {
	if (ddw.corpid != null && ddw.corpid != "") {
		setCookie("corpId", ddw.corpid);
	}
	dd.ready(function() {
		dd.runtime.info({
			onSuccess : function(info) {
				//logger.i('runtime info: ' + JSON.stringify(info));
			},
			onFail : function(err) {
				//logger.e('runtime info fail: ' + JSON.stringify(err));
			}
		});

		dd.runtime.permission.requestAuthCode({
			corpId : getCookie('corpId'),
			onSuccess : function(info) {
				//logger.i('authcode: ' + info.code);						
				code = info.code;
				//loadIden();
			},
			onFail : function(err) {
				//logger.e('authcode fail: ' + JSON.stringify(err));
				//处理不是该企业人员的注册登录的问题
				if (err["errorCode"] == "3") {
					//跳去登陆页面
					window.location.href = "register.html?corpId="
							+ getCookie('corpId') + "";
				}
			}
		});
	});

	dd.error(function(err) {
		alert(JSON.stringify(err) + "jsApi验证失败");
		//logger.e('dd error: ' + JSON.stringify(err));
	});

}

function loadIden() {
	//hidePreloader();
	var v_call = '{"din":{"code":"' + code + '","corpId":"'
			+ getCookie('corpId') + '"},"fid":"login","pid":"boVdsLogin"}';
	 
	ddgetAjax(v_call, function(d) {
		setCookie("userId", d.userId);
		setCookie("name", d.name);
		setCookie("avatar", d.avatar);
	    alert("ID="+d.userId+"; 名称="+d.name+"; 头像="+d.avatar);
	    location.reload();
	});
}
