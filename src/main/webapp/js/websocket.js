$(function () {
    refresh();
    connectSocket();
});


function refresh() {
    $.ajax({
        type: 'POST',
        url: getPath() + '/content_load',
        success: function (data) {
            if (data.result == 1) {
                var contents = data.data.contents;
                for (var i = 0; i < contents.length; i++) {
                    var content = contents[i];
                    var msg = content.content;
                    $("#msg").append(msg + "<br/>")
                }
            } else {

            }
        }
    });
}

function connectSocket() {
    // 三亚人民医院socket
    // websocket  linkstart! //  //ws://pay.epsit.cn:8088/endpointAric?robotId=1
    let self = this;
    var ws; //websocket实例
    // websocket = new WebSocket("ws://localhost:8080/apiWebSocket?robotCode=test")
    // var wsUrl =
    //     "ws://pay.epsit.cn:8088/payment/api/endpointAric?robotId=" +
    //     localStorage.useId;
    // ws://pay.epsit.cn:8088/payment/api/endpointAric?robotId="+localStorage.useId //ws://192.168.3.9:8089/payment/api/endpointAric?robotId="+localStorage.useId
    // var wsUrl = 'ws://192.168.3.9:8089/payment/api/endpointAric?robotId='+localStorage.useId; // ws://pay.epsit.cn:8088/payment/api/endpointAric?robotId="+localStorage.useId //ws://192.168.3.9:8089/payment/api/endpointAric?robotId="+localStorage.useId

    var wsUrl = "ws://localhost:8080/apiWebSocket?robotCode=test";
    var maxConnect = 10; //最大重连次数
    var curAttemp = 0; //失败后当前尝试连接次数
    function createWebSocket(url) {
        try {
            ws = new WebSocket(url);
            initEventHandle();

        } catch (e) {
            // console.log('catch')
            reconnect(url);
        }
    }

    function initEventHandle() {

        ws.onclose = function () {
            // console.log('close')
            // window.JsClient.setLog("close");
            setMessageInnerHTML("连接关闭,尝试重新连接");
            if (curAttemp <= maxConnect) {
                reconnect(wsUrl);
            } else {
                setMessageInnerHTML("尝试重新连接失败");
            }
        };
        ws.onerror = function () {
            // console.log('error')
            setMessageInnerHTML("连接错误");
            reconnect(wsUrl);
        };
        ws.onopen = function () {
            // console.log('start')
            // console.log(ws.readyState)
            // window.JsClient.setLog(
            //     "socket建立连接" + JSON.stringify(ws.readyState)
            // );
            curAttemp = 0;
            setMessageInnerHTML("socket建立连接");
            //心跳检测重置
            heartCheck.reset().start();
        };
        ws.onmessage = function (evt) {
            // console.log('reset')
            // console.log(evt)
            // console.log(evt, '=============> 2934')
            // window.JsClient.setLog(
            //     "收到服务器推送的消息--------" + JSON.stringify(evt)
            // );
            setMessageInnerHTML(evt.data);
            //如果获取到消息，心跳检测重置
            //拿到任何消息都说明当前连接是正常的
            // window.payStatus = 2
            // let data = JSON.parse(evt.data);
            // if (data.message == "支付成功") {
            //     // console.log(data)
            //     if (window.payStatus != 1) {
            //         window.payStatus = 2;
            //         // window.JsClient.setLog("socket发起的--------");
            //         // self.pay(data);
            //     }
            // }
            heartCheck.reset().start();
        };
    }

    function reconnect(url) {
        curAttemp++;
        // console.log(url)
        // console.log(self.lockReconnect)
        // window.JsClient.setLog("掉线重连");
        setMessageInnerHTML("掉线重连");
        if (self.lockReconnect) return;
        self.lockReconnect = true;
        //没连接上会一直重连，设置延迟避免请求过多
        //TODO 最多重连次数
        setTimeout(function () {
            createWebSocket(url);
            self.lockReconnect = false;
        }, 2000);
    }

    //心跳检测
    var heartCheck = {
        timeout: 20000, //20秒
        timeoutObj: null,
        serverTimeoutObj: null,
        reset: function () {
            clearTimeout(this.timeoutObj);
            clearTimeout(this.serverTimeoutObj);
            return this;
        },
        start: function () {
            // console.log('heart-start')
            var _self = this;
            this.timeoutObj = setTimeout(function () {
                //这里发送一个心跳，后端收到后，返回一个心跳消息，
                //onmessage拿到返回的心跳就说明连接正常
                ws.send('{\"message\":\"heart\",\"messageType\":0,\"robotCode\":\"test\"}');
                _self.serverTimeoutObj = setTimeout(function () {//如果超过一定时间还没重置，说明后端主动断开了
                    // console.log('111')
                    ws.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                }, _self.timeout)
            }, this.timeout);
        }
    };

    // //将消息显示在网页上
    // function setMessageInnerHTML(innerHTML) {
    //     $("#msg").append(innerHTML + "<br/>")
    // };

    createWebSocket(wsUrl);

    window.onbeforeunload = function () {
        var is = confirm("确定关闭窗口？");
        if (is) {
            ws.close();
        }
    };

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        $("#msg").append(innerHTML + "<br/>")
    };

    //关闭连接
    function closeWebSocket() {
        ws.close();
    }

    //发送消息
    function send() {
        var message = $("#text").val();
        // websocket.send(message);
        ws.send(message);
        $("#text").val("");
    }

    function sendMsg() {
        var msg = ue.getContent();
        ws.send(msg);
        ue.setContent('');
    }
}