<%--
  Created by IntelliJ IDEA.
  User: HP
  Date: 2019/7/30
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

    <script type="text/javascript" src="/js/jquery.min.js"></script>
    <script type="text/javascript" src="/js/spark-md5/spark-md5.js"></script>

    <script type="text/javascript">
        /*将post method 改变为delete*/
        $(function () {
        })
    </script>
</head>
<body>
<div class="row pb40 pt40 mlr-10 border-top-1">
    <div class="col-xs-12 col-lg-12">
        <div class="col-xs-12 col-lg-12">
            <span id="mediaLable">添加视频内容</span>
            <small style="color: #ff5704;margin-left:10px;" id="mediaLimit">视频格式仅支持MP4及AVI，文件大小需小于500M</small>
        </div>
        <div id="mediaFiles" class="col-xs-12 col-lg-12 mt20 video-list-wrap">
        </div>
        <div class="col-xs-10 col-xs-offset-1 col-lg-10 col-lg-offset-1 mt20 mb10">
            <a href="javascript:;" id="addMediaButton" onclick="addFileInput()" class="mybtn btn-add-user2">添加视频</a>
        </div>
        <div>
            <button onclick="submitFile()">开始上传</button>
        </div>
    </div>
</div>
<script type="text/javascript">
    //删除一个上传文件文本框
    function delFileInput(that) {
        var delFileDivId = $(that).parent().attr("id");
        //如果是修改页面的删除则将要删除的附件ID保存在隐藏框
        if (delFileDivId.indexOf("modify") == 0) {
            var deleteFileIds = $("#deleteFileIds").val();
            if (deleteFileIds.length > 0) {
                deleteFileIds = deleteFileIds + "," + delFileDivId.split("_").pop();
                $("#deleteFileIds").val(deleteFileIds);
            } else {
                $("#deleteFileIds").val(delFileDivId.split("_").pop());
            }
        }
        var uploadFileDivs = $("#mediaFiles").children();
        episodeCount = uploadFileDivs.length - 1;
        //删除后修改集数
        for (var i = 1; i < uploadFileDivs.length; i++) {
            if ($("#mediaFiles").children()[i].id == $(that).parent().parent().attr("id")) {
                $(that).parent().parent().remove();
                for (var j = i; j < uploadFileDivs.length; j++) {
                    $($("#mediaFiles").children()[j]).find(".account").text("第" + (j + 1) + "集");
                }
                break;
            }
        }
    }

    //上传附件文本框计数器
    var inputCount = 0;
    var episodeCount = 0;

    //点击按钮后新增一个上传文件文本框
    function addFileInput() {
        //把新增的每一行都放到一个div中，删除时删除这个父div节点即可
        var rootFileDiv = document.createElement("div");
        rootFileDiv.setAttribute("id", "rootFileDiv" + inputCount);
        if (inputCount == 0) {
            rootFileDiv.setAttribute("class", "col-xs-12 col-lg-12 video-list");
        } else {
            rootFileDiv.setAttribute("class", "col-xs-12 col-lg-12 full video-list mt20");
        }
        $("#mediaFiles").append(rootFileDiv);
        //删除图标
        var delDiv = document.createElement("div");
        delDiv.setAttribute("id", "delFileDiv" + inputCount);
        delDiv.setAttribute("class", "delete");
        if (inputCount > 0) {
            var delDivText = "<span>×</span>";
            delDiv.innerHTML = delDivText;
        }
        //集数
        var countDiv = document.createElement("div");
        countDiv.setAttribute("id", "account");
        countDiv.setAttribute("class", "account");
        var countDivText = "第" + (episodeCount + 1) + "集";
        countDiv.innerHTML = countDivText;
        //文件名称显示文本框
        var mediaFileNameDiv = document.createElement("div");
        mediaFileNameDiv.setAttribute("class", "name");
        var mediaFileNameDivInput = "<input type='text' id=mediaFile" + inputCount + "_Name" + " class='form-control' />";
        mediaFileNameDiv.innerHTML = mediaFileNameDivInput;
        //文件上传文本框
        var mediaFileDiv = document.createElement("div");
        mediaFileDiv.setAttribute("class", "filebtn");
        var mediaType = $("#type").val();
        var mediaFileInput;
        if (mediaType == 0) {
            // onchange='handleFile(this)'
            mediaFileInput = "<a><span>选择文件 ></span><input type='file' accept='.mp4,.avi' name='myfiles' id=mediaFile" + inputCount + "_ class='' /></a>";
        } else {
            mediaFileInput = "<a><span>选择文件 ></span><input type='file' accept='.*' name='myfiles' id=mediaFile" + inputCount + "_ class='' /></a>";
        }
        mediaFileDiv.innerHTML = mediaFileInput;
        //上传进度条
        var progressbarDiv = document.createElement("div");
        progressbarDiv.setAttribute("id", "progressbar" + inputCount);
        progressbarDiv.setAttribute("class", "progress hide");
        var progressbarDivText = '<div class="progress-bar progress-bar-green" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 0%;"></div><div class="percent">0%</div><div class="size">40M/200M</div>';
        progressbarDiv.innerHTML = progressbarDivText;
        //暂停上传或者继续上传按钮
        var uploadStatusButtonDiv = document.createElement("div");
        uploadStatusButtonDiv.setAttribute("id", "uploadStatusButton" + inputCount);
        uploadStatusButtonDiv.setAttribute("class", "result");
        uploadStatusButtonDivButton = '<span class="success hide"><i class="fa fa-check-circle"></i>上传完成</span><span class="stop-continue hide" id="uploadStatusButton" onclick="changeUploadStatus(this)">暂停上传</span>';
        uploadStatusButtonDiv.innerHTML = uploadStatusButtonDivButton;
        $("#rootFileDiv" + inputCount).append(delDiv);
        $("#rootFileDiv" + inputCount).append(countDiv);
        $("#rootFileDiv" + inputCount).append(mediaFileNameDiv);
        $("#rootFileDiv" + inputCount).append(mediaFileDiv);
        $("#rootFileDiv" + inputCount).append(progressbarDiv);
        $("#rootFileDiv" + inputCount).append(uploadStatusButtonDiv);
        inputCount++;
        episodeCount++;
        return rootFileDiv;
    }

    //mediaFileArray存放分片数据信息
    var mediaFileArray = new Array();

    //获取上传分片数据基本信息,获取完毕后调用上传方法提交分片
    function submitFile() {
        var fileInputs = $("input[name='myfiles']");
        for (var i = 0; i < fileInputs.length; i++) {
            if (fileInputs[i].files[0] != null) {
                var newName = guid();
                var name = fileInputs[i].files[0].name, //文件名
                    size = fileInputs[i].files[0].size, //总大小
                    type = fileInputs[i].files[0].type, //文件类型
                    shardSize = 10 * 1024 * 1024, // shardSize = 10 * 1024 * 1024, 以10MB为一个分片
                    shardCount = Math.ceil(size / shardSize); //总片数
                var shardArray = new Array();
                shardArray[0] = name;
                shardArray[1] = size;
                shardArray[2] = type;
                shardArray[3] = shardSize;
                shardArray[4] = shardCount;
                shardArray[5] = 0;//当前上传状态,0-等待上传,1-正在上传,2是上传完成
                shardArray[6] = 0;//已传输最后分片编号
                shardArray[7] = newName;//服务器存储名称
                shardArray[8] = "";//MD5值
                mediaFileArray[i] = shardArray;//将每个file的分片信息放入全局数组
                var blobSlice = File.prototype.slice || File.prototype.mozSlice || File.prototype.webkitSlice,
                    file = fileInputs[i].files[0],
                    currentShard = 0,
                    spark = new SparkMD5(),    //创建SparkMD5的实例
                    // time,
                    running = true;
                fileReader = new FileReader();
                fileReader.onload = function (e) {

                    console.log("Read chunk number (currentChunk + 1) of  chunks ");

                    spark.appendBinary(e.target.result);                 // append array buffer
                    currentShard += 1;
                    if (currentShard < shardCount) {
                        loadNext();
                    } else {
                        running = false;
                        console.log("Finished loading!");
                        shardArray[8] = spark.end();
                        console.log(shardArray[8]);
                        // return spark.end();     // 完成计算，返回结果
                    }
                };

                fileReader.onerror = function () {
                    running = false;
                    console.log("something went wrong");
                };
                running = true;
                loadNext();

                function loadNext() {
                    let start = currentShard * shardSize,
                        end = start + shardSize >= file.size ? file.size : start + shardSize;

                    fileReader.readAsBinaryString(blobSlice.call(file, start, end));
                }
            } else {
                //TODO 当进入修改页面file为空时
                var newName = guid();
                var name = "", //文件名
                    size = 0, //总大小
                    type = "", //文件类型
                    shardSize = 10 * 1024 * 1024, // shardSize = 10 * 1024 * 1024, 以10MB为一个分片
                    shardCount = 0; //总片数
                var shardArray = new Array();
                shardArray[0] = name;
                shardArray[1] = size;
                shardArray[2] = type;
                shardArray[3] = shardSize;
                shardArray[4] = shardCount;
                shardArray[5] = 0;//当前上传状态,0-等待上传,1-正在上传,2是上传完成
                shardArray[6] = 0;//已传输最后分片编号
                shardArray[7] = newName;//服务器存储名称
                shardArray[8] = "";//MD5值
                mediaFileArray[i] = shardArray;//将每个file的分片信息放入全局数组
            }
        }
        postFile(0, 0);//第一个参数是第几个file元素,最后一个参数是从第几个分片开始上传
    }


    //附件完成上传计数器
    var endCount = 0;
    //控制是否继续往服务端发送分片数据,0-继续提交,1-停止提交
    var flag = 0;

    //文件断点续传
    function postFile(fileNum, shardNum) {
        if (flag == 0) {
            //计算每一片的起始与结束位置
            var file = $("input[name='myfiles']")[fileNum].files[0];
            var oldName;
            if ($("#mediaFiles").find(".video-list").eq(fileNum).find(".form-control").eq(0).val().length > 0) {
                oldName = $("#mediaFiles").find(".video-list").eq(fileNum).find(".form-control").eq(0).val() + "#";
            } else {
                oldName = mediaFileArray[fileNum][0] + "#";
            }
            //修改时需要oldname拼接fileId
            var fileNameId = $("#mediaFiles").find(".video-list").eq(fileNum).find(".form-control").eq(0).attr("id");
            if (fileNameId.indexOf("modify") == 0 && fileNameId.length > 0 && fileNameId.split("_").length > 0) {
                oldName = oldName + fileNameId.split("_")[1];
            }
            var start = shardNum * mediaFileArray[fileNum][3],
                end = Math.min(mediaFileArray[fileNum][1], start + mediaFileArray[fileNum][3]);
            /* //修改文件为空，但所有文件都已传输完则跳转回view
            if(file==null&&endCount>=mediaFileArray.length){
                window.location.href="micro/index?type="+$("#type").val();
                return;
            } */
            //当文件不为空，当前文件所有分片都传输完时控制button显示并跳转回view
            if (shardNum >= mediaFileArray[fileNum][4] && file != null) {
                $("#mediaFiles").find(".video-list").eq(fileNum).find(".progress").eq(0).addClass("hide");
                $("#mediaFiles").find(".video-list").eq(fileNum).find(".stop-continue").eq(0).addClass("hide");
                $("#mediaFiles").find(".video-list").eq(fileNum).find('.success').eq(0).removeClass("hide");
                //如果本文件传完,开始传输下一个文件
                if (fileNum < mediaFileArray.length - 1 && mediaFileArray[fileNum + 1][6] == 0) {
                    postFile(fileNum + 1, 0);
                }
                endCount++;
                //如果所有的文件都传输完则跳转回view
                // if (endCount >= mediaFileArray.length) {
                //     var location = (window.location + '').split('/');
                //     var basePath = location[0] + '//' + location[2] + '/' + location[3];
                //     window.location.href = basePath + "/micro/index?type=" + $("#type").val();
                // }
                return;
            }
            //服务端URL
            var requestUrl = "/micro/uploadSlice";
            //构造一个表单，FormData是HTML5新增的
            var form = new FormData();
            if (file != null) {
                form.append("data", file.slice(start, end));  //slice方法用于切出文件的一部分
            }
            // form.append("lastModified", file.lastModified);  //文件最后修改时间不支持IE
            form.append("name", mediaFileArray[fileNum][7]); //文件存储名称
            form.append("fileType", mediaFileArray[fileNum][2]); //文件类型
            form.append("total", mediaFileArray[fileNum][4]);  //总片数
            form.append("index", shardNum + 1);  //当前是第几片
            form.append("oldName", oldName);  //用户自定义文件名
            form.append("seq", fileNum);  //文件所处的顺序
            form.append("microClassId", $("#microClassId").val());  //microClassId
            //Ajax提交
            $.ajax({
                url: requestUrl,
                type: "POST",
                data: form,
                async: true,        //异步
                processData: false,  //很重要，告诉jquery不要对form进行处理
                contentType: false,  //很重要，指定为false才能形成正确的Content-Type
                success: function (data) {
                    //显示暂停按钮
                    if (start == 0 && file != null) {
                        $("#mediaFiles").find(".video-list").eq(fileNum).find(".stop-continue").eq(0).removeClass("hide");
                    }
                    mediaFileArray[fileNum][6] = shardNum + 1;
                    if (data != "0") {
                        shardNum = data++;
                        console.log("当前分片数：", shardNum);
                        // var num = Math.ceil(shardNum * mediaFileArray[fileNum][3] * 100 / mediaFileArray[fileNum][1]); //百分比进度
                        var num = Math.ceil(shardNum * 100 / mediaFileArray[fileNum][4]); //百分比进度
                        //改变进度条进度
                        $("#mediaFiles").find(".video-list").eq(fileNum).find(".progress").eq(0).removeClass("hide");
                        $("#mediaFiles").find(".video-list").eq(fileNum).find(".progress-bar").eq(0).attr("style", "width: " + num + "%;");
                        $("#mediaFiles").find(".video-list").eq(fileNum).find(".percent").eq(0).text(num + "%");
                        if (shardNum == mediaFileArray[fileNum][4]) {
                            $("#mediaFiles").find(".video-list").eq(fileNum).find(".size").eq(0).text(Math.ceil(mediaFileArray[fileNum][1] / (1024 * 1024)) + "/" + Math.ceil(mediaFileArray[fileNum][1] / (1024 * 1024)) + "M");
                        } else {
                            $("#mediaFiles").find(".video-list").eq(fileNum).find(".size").eq(0).text(shardNum * 10 + "/" + Math.ceil(mediaFileArray[fileNum][1] / (1024 * 1024)) + "M");
                        }
                        //通过button状态来判断是否继续上传
                        var text = $("#mediaFiles").find(".video-list").eq(fileNum).find(".stop-continue").eq(0).text();
                        if (text == "暂停上传") postFile(fileNum, shardNum);
                    } else {
                        //当修改文件为空时的处理
                        $("#mediaFiles").find(".video-list").eq(fileNum).find('.success').eq(0).removeClass("hide");
                        endCount = endCount + 1;
                        if (endCount < mediaFileArray.length) {
                            postFile(fileNum + 1, 0);
                        } else {
                            $("#mediaFiles").find(".video-list").eq(fileNum).find(".progress").eq(0).addClass("hide");
                            $("#mediaFiles").find(".video-list").eq(fileNum).find(".stop-continue").eq(0).addClass("hide");
                            $("#mediaFiles").find(".video-list").eq(fileNum).find('.success').eq(0).removeClass("hide");
                            var location = (window.location + '').split('/');
                            var basePath = location[0] + '//' + location[2] + '/' + location[3];
                            window.location.href = basePath + "/micro/index?type=" + $("#type").val();
                        }

                    }
                },
                error: function (xhr, errorText, errorType) {
                    $("#wrong_msg").text("网络错误,上传失败,请检查网络后继续上传!");
                    // $("#tips1").modal("show");
                    mediaFileArray[fileNum][6] = shardNum;
                    $("#mediaFiles").find(".video-list").eq(fileNum).find(".stop-continue").eq(0).text("继续上传");
                }
            });
        } else {
            return;
        }
    }

    //控制上传状态
    function changeUploadStatus(that) {
        if ($(that).text() == "暂停上传") {
            var fileNum = $(that).parents(".video-list").eq(0).index();
            flag = 1;
            $(that).text("继续上传");
            mediaFileArray[fileNum][5] = 0;
            if (fileNum < mediaFileArray.length - 1 && mediaFileArray[fileNum + 1][6] == 0) {
                $(that).parents("#mediaFiles").find(".video-list").eq(fileNum + 1).find(".stop-continue").eq(0).text("暂停上传");
                flag = 0;
                postFile(fileNum + 1, mediaFileArray[fileNum + 1][6]);
            }
            return;
        } else {
            var fileNum = $(that).parents(".video-list").eq(0).index();
            var shardNum = mediaFileArray[fileNum][6];
            for (var i = 0; i < mediaFileArray.length; i++) {
                if (mediaFileArray[i][5] == 1) {
                    mediaFileArray[i][5] == 0;
                    flag = 1;
                    $(that).parents("#mediaFiles").find(".video-list").eq(i).find(".stop-continue").eq(0).text("继续上传");
                }
            }
            flag = 0;
            mediaFileArray[fileNum][5] = 1;
            $(that).text("暂停上传");
            postFile(fileNum, shardNum);
            return;
        }
    }

    //用于生成uuid
    function S4() {
        return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }

    function guid() {
        return (S4() + S4() + S4() + S4() + S4() + S4() + S4() + S4());
    }
</script>

</body>
</html>
