


var POSTURL ="http://dc.basoft.cn/vds5/prjX/";

var ddw = {
    address : POSTURL + "boVdsRequest.jsp",  // 请求地址
    address2 : POSTURL + "boCallMethod.jsp", // 请求地址
    corpid : "ding688410f040b30d55"
    /*sort : "synthesis", 
    pageVal : 1,
    ajax : null,
    alert : true*/
};


// ajax
function ddgetAjax (para, success) {
    $.ddgetAjax({
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
$.ddgetAjax = function (p) {
    var d = {
            url : ddw.address, // 请求链接
            data : {}, // 请求参数
            async : true, // 是否异步请求 true|是, false|否
            cache : true, // 是否缓存页面 true|是, false|否
            dataType : "json", // 数据类型
            type : "get", // 请求方式 post|get
            success : function () {},
            error : function () {}
        },
        o = $.extend(d, p || {}),
        isShowResultMsg = arguments[1]?arguments[1]:"true",
        customMsg = arguments[2]?arguments[2]:""
        ;
       alert( o.url+":"+ o.type +":"+o.dataType+":"+ o.data +":"+o.async +":"+o.cache+":"+isShowResultMsg+"===="+customMsg);
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
            	ddToast("系统繁忙", "");
                return false;
            } 
            ddw.ajax = d;
            if(d.status.resultCode >= 0) {
            	o.success(d.dout);
                if(d.status.resultMsg != "" && isShowResultMsg == "true") ddToast(d.status.resultMsg, "");
                else if (isShowResultMsg == "false" && customMsg != "") ddToast(customMsg, "");
            } else if(d.status.resultCode < 0) {
            	ddToast(d.status.resultMsg, "");
            } else {
            	o.success(d);
            }
        },
        error : function (e) {
            o.error(e);
            //danger("没有响应");
            ddToast("网络异常,请在良好的网络环境下使用", "");
        }
    });
};


