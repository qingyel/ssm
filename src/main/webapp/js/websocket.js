$(function () {
    refresh();
    connectSocket();
    // $("#send").click(function () {
    //     var websocket = temp.ws;
    // });
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
    let self = this;
    var ws; //websocket实例
    var robotCode = "test"+ Math.floor(Math.random() * 10);
    var wsUrl = "ws://localhost:8080/apiWebSocket?robotCode=" + robotCode;
    var maxConnect = 10; //最大重连次数
    var curAttemp = 0; //失败后当前尝试连接次数
    createWebSocket(wsUrl);

    function createWebSocket(url) {
        try {
            ws = new WebSocket(url);
            initEventHandle();

        } catch (e) {
            reconnect(url);
        }
    }

    function initEventHandle() {
        ws.onclose = function () {
            // window.JsClient.setLog("close");
            setMessageInnerHTML("连接关闭,尝试重新连接");
            if (curAttemp < maxConnect) {
                reconnect(wsUrl);
            } else {
                setMessageInnerHTML("尝试重新连接失败");
            }
        };
        ws.onerror = function () {
            // console.log('error')
            setMessageInnerHTML("连接错误,尝试重新连接");
            if (curAttemp < maxConnect) {
                reconnect(wsUrl);
            } else {
                setMessageInnerHTML("尝试重新连接失败");
            }
        };
        ws.onopen = function () {
            curAttemp = 0;
            setMessageInnerHTML("socket建立连接");
            //心跳检测重置
            heartCheck.reset().start();
        };
        ws.onmessage = function (evt) {
            let data = JSON.parse(evt.data);
            if (data.message != "pong") {
                let messageVo = {};
                messageVo["message"] = "pong";
                messageVo["messageType"] = 1;
                messageVo["robotCode"] = robotCode;
                ws.send(JSON.stringify(messageVo));
                // ws.send('{\"message\":\"pong\",\"messageType\":0,\"robotCode\":\"test\"}');
            }
            setMessageInnerHTML("from server" + evt.data);
            heartCheck.reset().start();
        };

        window.onbeforeunload = function () {
            var is = confirm("确定关闭窗口？");

            if (true) {
                curAttemp = 999;
                ws.close();
            }
        }
        $("#send").click(function () {
            var message = $("#text").val();
            var messageVo = {};
            messageVo["message"] = message;
            messageVo["messageType"] = 1;
            messageVo["robotCode"] = robotCode;
            ws.send(JSON.stringify(messageVo));
            $("#text").val("");
        })
    }

    function reconnect(url) {

        if (self.lockReconnect) return;
        self.lockReconnect = true;
        // 最多重连10次
        setTimeout(function () {
            setMessageInnerHTML("掉线重连"+curAttemp);
            createWebSocket(url);
            curAttemp++;
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
                let messageVo = {};
                messageVo["message"] = "ping";
                messageVo["messageType"] = 1;
                messageVo["robotCode"] = robotCode;
                ws.send(JSON.stringify(messageVo));
                // ws.send('{\"message\":\"ping\",\"messageType\":0,\"robotCode\":\"test\"}');
                _self.serverTimeoutObj = setTimeout(function () {//如果超过一定时间还没重置，说明后端主动断开了
                    // console.log('111')
                    ws.close();//如果onclose会执行reconnect，我们执行ws.close()就行了.如果直接执行reconnect 会触发onclose导致重连两次
                }, _self.timeout)
            }, this.timeout);
        }
    };

    //将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        $("#msg").append(innerHTML + "<br/>")
    };


    //发送消息
    function send() {
        var message = $("#text").val();
        ws.send(message);
        $("#text").val("");
    }

    function sendMsg() {
        var msg = ue.getContent();
        ws.send(msg);
        ue.setContent('');
    }
}