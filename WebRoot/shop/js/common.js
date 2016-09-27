// JavaScript Document

/*
 * Author : wenyejie
 * Mail : wenyejie@foxmail
 * Link : http://wenyejie.com/
 */

//可用于获取url参数，如：UrlParm.parm("name");
UrlParm = function() { 
	var data= [];
	var index = {};
	(function init() {
		data = [];
		index = {};
		var u = window.location.search.substr(1);
		if (u != '') {
			var parms = decodeURIComponent(u).split('&');
			for (var i = 0, len = parms.length; i < len; i++) {
				if (parms[i] != '') {
					var p = parms[i].split("=");
					if (p.length == 1 || (p.length == 2 && p[1] == '')) {// p | p=
						data.push(['']);
						index[p[0]] = data.length - 1;
					} else if (typeof(p[0]) == 'undefined' || p[0] == '') { // =c | =
						data[0] = [p[1]];
					} else if (typeof(index[p[0]]) == 'undefined') { // c=aaa
						data.push([p[1]]);
						index[p[0]] = data.length - 1;
					} else {// c=aaa
						data[index[p[0]]].push(p[1]);
					}
				}
			}
		}
	})();
	return {
		// 获得参数,类似request.getParameter()
		parm : function(o) { // o: 参数名或者参数次序
			try {
				return (typeof(o) == 'number' ? data[o][0] : data[index[o]][0]);
			} catch (e) {
			}
		},
		//获得参数组, 类似request.getParameterValues()
		parmValues : function(o) { // o: 参数名或者参数次序
			try {
				return (typeof(o) == 'number' ? data[o] : data[index[o]]);
			} catch (e) {}
		},
		//是否含有parmName参数
		hasParm : function(parmName) {
			return typeof(parmName) == 'string' ? typeof(index[parmName]) != 'undefined' : false;
		},
		// 获得参数Map ,类似request.getParameterMap()
		parmMap : function() {
			var map = {};
			try {
				for (var p in index) { map[p] = data[index[p]]; }
			} catch (e) {}
				return map;
		}
	};
}();


var w = {
	address : "http://172.16.1.219:8080/vds5/prjX/boVdsRequest.jsp", // 请求地址
    payAddress : 'http://120.197.59.213:10004/hzvds/alipay/alipayapi.jsp', // 支付地址
    url : getUrlVal(),
    sort : "synthesis", 
    pageVal : 1,
    ajax : null,
    alert : true,
    siteurl:"http://172.16.1.219:8080/vdsCXH/shop/",
    appid:"wx93a749c7cbfa9d52",
    corpid : UrlParm.parm("corpid")
};


/*------------addBymin-------------*/
function GetParam(sHref, sParam) {
	var args = sHref.split("?");
	var retval = "";
	
	if (args[0] == sHref) /*参数为空*/
	{
		return retval; /*无需做任何处理*/
	}
	var str = args[1];
	args = str.split("&");
	for ( var i = 0; i < args.length; i++) {
		str = args[i];
		var arg = str.split("=");
		if (arg.length <= 1)
			continue;
		if (arg[0] == sParam)
			retval = decodeURIComponent(arg[1]);
	}
	return retval;
}
/*------------addBymin-------------*/

if (navigator.userAgent.match(/IEMobile\/10\.0/)) {
  var msViewportStyle = document.createElement('style')
  msViewportStyle.appendChild(
    document.createTextNode(
      '@-ms-viewport{width:auto!important}'
    )
  )
  document.querySelector('head').appendChild(msViewportStyle)
}

$(jsInit);

// jsInit
function jsInit () {

    // 全局参数
    w.danger = $(".danger-wrap");
    w.dangerTxt = w.danger.find(".alert-txt");
    w.success = $(".success-wrap");
    w.successTxt = w.success.find(".alert-txt");
    w.warning = $(".warning-wrap");
    w.warningTxt = w.warning.find(".alert-txt");
    if(!!$.cookie("login")) {
        w.user = $.parseJSON($.cookie("login"));
    } else {
        w.user = null;
    }

    // 展开分类
    //$(".classify-btn").click(showClassify);

    // 收起分类
    //$(".classify-close-btn").click(showClassify);

    // 展开搜索
    $(".search-btn").click(showSearch);

    // 收起搜索
    $(".search-close").click(showSearch);

    // 获取登录状态
    getCookieType();

    // 搜索
    $(".search span").click(search);

    $(".showCart-btn").click(function(e) {
        if(loginAlert()) return false;
    });

    // 返回顶部
    $(".go-top-btn").click(function(){  
        $('body,html').animate({scrollTop : 0});  
        return false;
    });

    // 数量操作
    num();
}

// 数量操作
function num () {
    var num = 0, txt = null;
    $("body").on("click", ".num_subtract", function () {
        txt = $(this).next();
        num = Number( txt.html() );
        if( num - 1 <= 0 ) return;
        txt.html( Number( txt.html() ) - 1 );
        try{count()}catch(e){};
    })
    $("body").on("click", ".num_add", function () {
        txt = $(this).prev();
        txt.html( Number( txt.html() ) + 1 );
        try{count()}catch(e){};
    })
}

// loading
function loading (ele) {
    if(typeof ele == 'string' || typeof ele == 'undefined') {
        $('.spinner').remove();
    } else {
        var html = '<div class="spinner"></div>';
        ele.append(html);
    }
}

/*
 * 函数名称 : 秒数倒计时
 * 函数描述 : 秒数倒计时
 * countdown : 倒计时父元素
 * resend : 倒计时结束后的展现元素
 * num : 秒数
 */
function countdown (p) {

    // 默认值
    var o = {
        countdown : $(".countdown"),
        resend : $(".resend"),
        num : 60,
        fun : function () {}
    };
    o = $.extend(o, p || {});

    o.countdown.show();
    o.resend.hide();
    var setTime = setInterval(function(){

        if(o.num == 0) {
            o.resend.show();
            o.countdown.hide();
            o.countdown.val(60);
            o.fun();
            clearInterval(setTime);
            return true;
        }
        o.countdown.val(o.num + "’S后重新获取");
        o.num--;
    }, 1000);
}

// 只能输入数字
function onlyNumber () {
    $(this).val($(this).val().replace(/\D/g,''));
}

// 搜索
function search () {
    location.href = "search.html?search=" + $(".search-text").val();
}

// 未登录提示
function loginAlert (id) {
    if(!w.user) {
       // danger('你还没有登录, 请先<a href="login.html?result=true">登录</a>!');
    	var goodid="";
    	if(id!=null){
    		goodid=id;
    	}
    	var str= encodeURIComponent("http://cxh.basoft.cn/vdsCXH/servlet/OAuthServletToLogin?goodid="+goodid);
    	var url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx93a749c7cbfa9d52&redirect_uri=" + str + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
    	danger('你还没有登录, 请先<a href="'+url+'">登录</a>!');
       
        return true;
    }
    return false;
}

// warning
function warning (txt, fn) {
    fn = !fn ? function () {} : fn
    w.warningTxt.html(txt);
    w.warning.popUpBox({
        fnY : fn
    });
}

// danger
function danger (txt) {
    w.dangerTxt.html(txt);
    w.danger.popUpBox();
}
// success
function success (txt) {
    w.successTxt.html(txt);
    w.success.popUpBox();
}

// 获取购物车信息
function getCart (fn) {
    if(!w.user) return false;
    getAjax('{"din":{"id":"' + w.user.id + '"},"fid":"cartList","pid":"boVdsCartList"}', function (d) {
        $(".showCart-num").html(d.goods_list.length);
        $(".nav_msgs").html(d.goods_list.length).show();
        !!fn ? fn(d) : "";
    });
}

// 获取登录状态
function getCookieType () {
    var ele = $(".person-type");
    if(!w.user) {
       // ele.removeClass("active").attr("href", "login.html?result=true");
    	var goodid="";
 	   	var str= encodeURIComponent("http://cxh.basoft.cn/vdsCXH/servlet/OAuthServletToLogin?goodid="+goodid);
     	var url="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx93a749c7cbfa9d52&redirect_uri=" + str + "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
     	ele.removeClass("active").attr("href", url);
    } else {
        ele.addClass("active").attr("href", "person.html?id=" + w.user.id);
    }
}

// required 提示插件
$.required = function(ele) {

    // 插件返回值
    var r = true; 

    ele = !ele ? $("body") : ele;

    // 查找并验证是否为空
    ele.find("[required]").each(function(i, e) {
        if( ( !$(this).val() && !$(this).is(":hidden") )) {
            danger($(this).attr("data-required"));
            r = false;
            return false;
        }
    });
    return r;
}

// ajax
function getAjax (para, success) {
    $.getAjax({
        data : {
            json : para
        },
        success : function (d) {
            success(d);
        }
    });
}

/*
 * ajax
 */
$.getAjax = function (p) {
    var d = {
            url : w.address, // 请求链接
            data : {}, // 请求参数
            async : true, // 是否异步请求 true|是, false|否
            cache : true, // 是否缓存页面 true|是, false|否
            dataType : "json", // 数据类型
            type : "get", // 请求方式 post|get
            success : function () {},
            error : function () {}
        },
        o = $.extend(d, p || {})
        ;
    $.ajax({
        url : o.url,
        type : o.type,
        dataType  : o.dataType,
        data : o.data,
        async : o.async,
        crossDomain : true,
        cache : o.cache,
        success : function (d) {
            if(d.resultCode == -99) {
                danger("未知错误! errorNo : -99");
                return false;
            } 
            w.ajax = d;
            if(d.status.resultCode == 1) {
                o.success(d.data);
                if(d.status.resultMsg != "") success(d.status.resultMsg);
            } else if(d.status.resultCode == 100){
            	o.success(d.data);
            	danger(d.status.resultMsg);
            } else if(w.alert) {
                danger(d.status.resultMsg);
                
            }else {
                o.success(d);
            }
        },
        error : function (e) {
            o.error(e);
        }
    });
};

/* 弹出框
 * fn : 当弹出框出现时，执行的函数
 * fnY : 当弹出框确定时执行的函数，当该函数返回为true时不关闭弹出框 默认为false
 * fnN : 当弹出框被取消时执行的函数
 */
$.fn.popUpBox = function (p) {
    var d = {
            fn : function () {},
            fnY : function () {},
            fnN : function () {}
        },
        o = $.extend(d, p || {}),
        bg = $('<div class="popUpBoxBg"></div>'),
        $this = $(this),
        _this = this
    ;
    $this.before(bg);
    bg.click(function(e) {
        _this.close();
    });
    _this.close = function () {
        bg.remove();
        $this.hide();
    };
    $this
        .find(".popUpBoxYes")
        .unbind("click")
        .click(function(e) {
            if(!o.fnY()) _this.close();
        })
    ;
    $this
        .find(".popUpBoxNo")
        .unbind("click")
        .click(function(e) {
            if(!o.fnN()) _this.close();
        })
    ;
    $this.show();
    o.fn();
};

// 颜色选择
function color_selection () {
    $(this).toggleClass("active").siblings().removeClass("active");
}

// 轮播
function slider (fn, fn1) {
    fn = !fn ? function () {} : fn; 
    fn1 = !fn1 ? function () {} : fn1; 
    window.mySwipe = new Swipe(document.getElementById('slider'), {
        startSlide: 0,
        speed: 400,
        auto: 5000,
        continuous: false,
        disableScroll: false,
        stopPropagation: false,
        callback: fn1,
        transitionEnd: fn
    });
}

//轮播
function menuslider (fn, fn1, p,eid) {
    fn = !fn ? function () {} : fn; 
    fn1 = !fn1 ? function () {} : fn1; 
    var d = {
        speed: 400, //速度
            auto: 5000,//时间间隔
            continuous: false,//是否循环
            disableScroll: false,//是否显示滚动条
            stopPropagation: false,
            callback: fn1,//滚动开始即触发
            transitionEnd: fn//滚动结束后触发
        },
        o = $.extend(d, p || {})
        ;
    //cxpslider menuslider
    window.mySwipe = new Swipe(document.getElementById(eid), {
        startSlide: 0,
        speed: o.speed,
        auto: o.auto,
        continuous: o.continuous,
        disableScroll: o.disableScroll,
        stopPropagation: o.stopPropagation,
        callback: o.callback,
        transitionEnd: o.transitionEnd
    });
}


// 自动获取轮播按钮
function swipe_btn () {
    var len = $(".swipe-wrap > div").length, html = "<div class='active'></div>";
    for(var i = 2; i <= len; i++) {
        html += "<div></div>";
    }
    $(".swipe-btn").html(html);
}

//自动获取轮播按钮
function menuswipe_btn () {
    var len = $(".menuswipe-wrap > div").length, html = "<div class='active'></div>";
    for(var i = 2; i <= len; i++) {
        html += "<div></div>";
    }
    $(".menuswipe-btn").html(html);
}

// 主要内容背景
function bodyShade (fn) {
    $(".body-shade").fadeToggle().unbind("click").click(fn);
}

// 展开搜索
function showSearch () {
    bodyShade(showSearch);
    $(".header").toggleClass("search-header");
}

// 展开分类
function showClassify () {
    bodyShade(showClassify);
    $(".header").toggleClass("classify-header");
}

/*
 * 获取name的值 (必须有值而且可见)
 * ele为要获取值得公共父元素
 * r : 返回值json格式
 */
$.getNameVal = function(d) {
  var p = {
      ele : $("body"),
      name : "name",
      val : "value",
      r : {}
    },
    o = $.extend(d, p || {})
  ;
  o.ele.find("[" + o.name + "]").each(function(i, e) {
    if($(this).attr(o.name) == "" || $(this).is(":hidden") || $(this).is(":disabled")) return;
    o.r[$(this).attr(o.name)] = $(this).attr(o.val);
  });
  return o.r;
};

// 对URL地址进行拆分，返回其中附带的值
// a返回对象,b值得字符串集合,c子值（即类似于"XX=XX"）
// 当没有附带值是返回false
function getUrlVal(url){
    var a = {}, b, c, u;
    url = !url ? window.location.href : url;
    u = url.split("?");
    if(u.length == 1) {
        return false;
    } else {
        b = u[1].split("&");
        for(var i = 0; i < b.length ; i++){
            c = b[i].split("=");
            a[c[0]] = c[1];
        };
    }
    return a;
};

// json to string
function jsonToString (obj){   
    var THIS = this;    
    switch(typeof(obj)){   
        case 'string':   
            return '"' + obj.replace(/(["\\])/g, '\\$1') + '"';   
        case 'array':   
            return '[' + obj.map(THIS.jsonToString).join(',') + ']';   
        case 'object':   
             if(obj instanceof Array){   
                var strArr = [];   
                var len = obj.length;   
                for(var i = 0; i < len; i++){   
                    strArr.push(THIS.jsonToString(obj[i]));   
                }   
                return '[' + strArr.join(',') + ']';   
            }else if(obj == null){   
                return 'null';   

            }else{   
                var string = [];   
                for (var property in obj) string.push(THIS.jsonToString(property) + ':' + THIS.jsonToString(obj[property]));   
                return '{' + string.join(',') + '}';   
            }   
        case 'number':   
            return obj;   
        case false:   
            return obj;   
    }   
}

//cookies
function setCookie(name,value)//两个参数，一个是cookie的名子，一个是值
{
    var Days = 30; //此 cookie 将被保存 30 天
    var exp  = new Date();    //new Date("December 31, 9998");
    exp.setTime(exp.getTime() + Days*24*60*60*1000);
    document.cookie = name + "=" + escape (value) + ";expires=" + exp.toGMTString();
}
function getCookie(name)//取cookies函数        
{
    var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
     if(arr != null) return unescape(arr[2]); return null;

}
function delCookie(name)//删除cookie
{
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var cval=getCookie(name);
    if(cval!=null) 
    	document.cookie= name + "="+cval+";expires="+exp.toGMTString();
}  
(function(){
	var s,c,str=[],
	Min = 58880,//不同图标字体16进制转换10进制起止不同
	Max = 59207,
	style = document.createElement("style"), 
	head = document.getElementsByTagName("head")[0];
	
	for(i=Min; i < Max; i++)
	{
		s = i.toString(16);//转换16进制
		str.push('.icon-uni' + s.toUpperCase() + ':before {content: "' + '\\' + s + '";}');				}

	c = str.join('');
	//console.log(c);

	style.type = "text/css"; 
	try { 
		style.appendChild(document.createTextNode(c)); 
	} catch (ex) { 
		style.styleSheet.cssText = c; 
	} 
	
	head.appendChild(style); 
})();

/*!
 * jQuery Cookie Plugin v1.4.1
 * https://github.com/carhartl/jquery-cookie
 *
 * Copyright 2006, 2014 Klaus Hartl
 * Released under the MIT license
 */
(function(factory){if(typeof define==="function"&&define.amd){define(["jquery"],factory)}else{if(typeof exports==="object"){factory(require("jquery"))}else{factory(jQuery)}}}(function($){var pluses=/\+/g;function encode(s){return config.raw?s:encodeURIComponent(s)}function decode(s){return config.raw?s:decodeURIComponent(s)}function stringifyCookieValue(value){return encode(config.json?JSON.stringify(value):String(value))}function parseCookieValue(s){if(s.indexOf('"')===0){s=s.slice(1,-1).replace(/\\"/g,'"').replace(/\\\\/g,"\\")}try{s=decodeURIComponent(s.replace(pluses," "));return config.json?JSON.parse(s):s}catch(e){}}function read(s,converter){var value=config.raw?s:parseCookieValue(s);return $.isFunction(converter)?converter(value):value}var config=$.cookie=function(key,value,options){if(arguments.length>1&&!$.isFunction(value)){options=$.extend({},config.defaults,options);if(typeof options.expires==="number"){var days=options.expires,t=options.expires=new Date();t.setTime(+t+days*86400000)}return(document.cookie=[encode(key),"=",stringifyCookieValue(value),options.expires?"; expires="+options.expires.toUTCString():"",options.path?"; path="+options.path:"",options.domain?"; domain="+options.domain:"",options.secure?"; secure":""].join(""))}var result=key?undefined:{};var cookies=document.cookie?document.cookie.split("; "):[];for(var i=0,l=cookies.length;i<l;i++){var parts=cookies[i].split("=");var name=decode(parts.shift());var cookie=parts.join("=");if(key&&key===name){result=read(cookie,value);break}if(!key&&(cookie=read(cookie))!==undefined){result[name]=cookie}}return result};config.defaults={};$.removeCookie=function(key,options){if($.cookie(key)===undefined){return false}$.cookie(key,"",$.extend({},options,{expires:-1}));return !$.cookie(key)}}));