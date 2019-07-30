package com.how2java.controller;

import com.how2java.util.FileUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * @program: ssm
 * @description:
 * @author: syx
 * @create: 2019-07-30 15:53
 **/
@Controller
@RequestMapping("micro")
public class UploadController {
    @RequestMapping(value = "indexUpload")
    public ModelAndView index(){
       return new ModelAndView("upload");
    }

    /**
     * @Description: 新增基本信息时保存form表单
     * @param title
     *            音视频名称
     * @param coverFile
     *            封面对象
     * @param desc
     *            音视频描述字段
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public Integer create(String title,String desc, MultipartFile coverFile) {
        try {
            //保存基本信息逻辑，自己实现，然后返回基本信息插入后的主键
            //request,即HttpServletRequest对象，在项目启动时候就被注入，如下：
            /**
             * @Autowired
             * private HttpServletRequest request;
             **/
//            Integer videoId = videoService.save(title,desc, coverFile, request);
//            return videoId;
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Description: 上传文件分片
     *
     * @param data
     *            分片文件
     * @param fileType
     *            文件类型 video/avi audio/mp3
     * @param name
     *            文件名称(newName),由Client生成,即NewName 如
     *            MICRO_CLASS_1502179979829.mp4
     * @param total
     *            分片总数
     * @param index
     *            当前分片数
     * @param microClassId
     *            文件关联的MicroClass主键,通过先保存基本信息取得并返回(DataFile中的OwerID)
     * @param oldName
     *            由用户自己输入的节目名称，需要与文件对应起来，如 实现两个100年奋斗目标
     * @param seq
     *            文件的顺序,如第一集对应1，第二集对应2....
     *
     * @return index 当前合并到文件的分片数
     *
     * @throws Exception
     */
//    uploadSlice
    @RequestMapping("/uploadSlice")
    @ResponseBody
//    public void uploadTest(@RequestBody  MultipartFile data,String name,String fileType){
//        System.out.println("hello");
//        System.out.println("hello");
//    }
    public Integer uploadSlice(@RequestBody  MultipartFile data, String fileType, String name, Integer total, Integer index,
                                String oldName, int seq) throws Exception {
//    public Integer uploadSlice(@RequestBody  String fileType, String name, Integer total, Integer index,
//                               Integer microClassId, String oldName, int seq) throws Exception {
        int countFile = 0;// 记录一次保存中上传的文件数目

        /**
         * oldName：使用输入框中的字符串与fileId拼接，形如D1#D2 如：
         * 实现两个100年奋斗目标#36,其中“实现两个100年奋斗目标”在数据库中存为OldName，36表示数据库中fileData主键
         *
         * D1: 可以为空，表示用户将Client获取到的文件名称删除，并且未输入任何字符串，可以为空; D2:
         * 可以为空，表示新增的文件，不为空，则表示在修改页面，传回的fileID；
         **/
        String[] str=oldName.split("#");
        String oldNameFiled=null;
        String fileDataId=null;
        System.out.println(str);
        switch (str.length) {
            case 0:
                break;
            case 1:
                oldNameFiled = oldName.split("#")[0];
                break;
            case 2:
                oldNameFiled = oldName.split("#")[0];
                fileDataId = oldName.split("#")[1];
                break;
            default:
                break;
        }

        if (total == 0) {
            // 表示在修改页面，用户只可能修改了oldName，但是未修改文件
            if (fileDataId != null && !fileDataId.equals("")) {
                if (oldNameFiled != null && !oldNameFiled.equals("")) {
                    //TODO
//                    dataFileService.updateOldNameById(fileDataId, oldNameFiled);//
                }
            }
        } else {
            if (index <= total) {//说明是有分片
                String dirType = fileType.split("/")[0];// 文件类型，用于创建不同的目录，如(video/audio)
                String fileExt = "." + fileType.split("/")[1];// 文件扩展名，如.mp3/.mp4/.avi
//                System.out.println(data.getSize() + "----" + name + "-----" + total + "----" + index);
                // 追加分片到已有的分片上，返回保存文件的路径,如/fileDate/video/2017/08/09
//                String savePath = FileUtil.randomWrite(request, data.getBytes(), name, dirType, fileExt);
                //TODO
//                if (index == 1 && savePath != null) {// 说明是新的文件的第一个分片，在数据库中创建相应的记录，并且状态为无效,等到全部上传完毕之后在修改为有效
//                    dataFile = new DataFile();
//                    dataFile.setOldName(oldNameFiled);
//                    dataFile.setFileUrl(savePath);
//                    dataFile.setNewName(name + fileExt);
//                    dataFile.setOwerId(microClassId);
//                    dataFile.setSeq(seq);
//                    dataFile.setStatus(1);
//                    dataFileService.saveDataFile(dataFile);
//                }
//                if (index == total) {// 说明已经成功上传一个文件
//                    // 根据文件名称和OwerId来更新文件记录，把记录的状态修改为0(有效)
//                    dataFileService.updateByNewNameAndOwerId(name+fileExt, microClassId);
//                    countFile++;
//                    if (countFile == 1) {// 说明已经上传成功一个文件，则吧MicroClass的状态改为0(有效)；
//                        microClassService.updateMicroClass(microClassId);// 根据microClassId来修改status
//                    }
//                    LOGGER.info("已上传 " + countFile + " 个文件");
//                }
                return index++;
            } else {
                return 0;
            }
        }
        return 0;
    }





}
