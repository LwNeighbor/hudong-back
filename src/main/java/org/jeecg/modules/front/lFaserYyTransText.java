package org.jeecg.modules.front;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.iflytek.msp.cpdb.lfasr.util.PropConfig;
import org.jeecg.common.api.vo.Result;

/**
 * 非实时转写SDK调用demo
 * 此demo只是一个简单的调用示例, 不适合用到实际生产环境中
 *
 * @author white
 *
 */
public class lFaserYyTransText {


    // 原始音频存放地址
    private static final String local_file_prefix = "/home/upFiles/";

    /*
     * 转写类型选择：标准版和电话版(旧版本, 不建议使用)分别为：
     * LfasrType.LFASR_STANDARD_RECORDED_AUDIO 和 LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO
     * */
    private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;

    // 等待时长（秒）
    private static int sleepSecond = 0;

    public static Result<JSONObject> getLfasrClientImp(String filePath){

        String text = "";

        Result<JSONObject> result = new Result<JSONObject>();

        JSONObject jsonObject = new JSONObject();
        // 初始化LFASRClient实例
        LfasrClientImp lc = null;

        String task_id = "";
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("has_participle", "true");
        try {
            lc = LfasrClientImp.initLfasrClient();
            String path = local_file_prefix + filePath;
            // 上传音频文件
            Message uploadMsg = lc.lfasrUpload(path, type, params);

            // 判断返回值
            int ok = uploadMsg.getOk();
            if (ok == 0) {
                // 创建任务成功
                task_id = uploadMsg.getData();
            } else {
                System.out.println("上传失败：====" + uploadMsg.getFailed());
                // 创建任务失败-服务端异常
                jsonObject.put("ecode",uploadMsg.getErr_no());
                jsonObject.put("failed",uploadMsg.getFailed());
                result.setResult(jsonObject);
                result.error500(uploadMsg.getFailed());
                return result;
            }

            while (true) {
                // 获取处理进度
                Message progressMsg = lc.lfasrGetProgress(task_id);

                // 如果返回状态不等于0，则任务失败
                if (progressMsg.getOk() != 0) {
                    System.out.println("进度失败");
                    jsonObject.put("ecode",progressMsg.getErr_no());
                    jsonObject.put("failed",progressMsg.getFailed());
                    result.setResult(jsonObject);
                    result.error500(progressMsg.getFailed());
                    return result;
                } else {
                    ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
                    if (progressStatus.getStatus() == 9) {
                        // 处理完成
                        break;
                    } else {
                        // 未处理完成
                        continue;
                    }
                }

            }

            Message resultMsg = lc.lfasrGetResult(task_id);
            // 如果返回状态等于0，则获取任务结果成功
            if (resultMsg.getOk() == 0) {
                String aa  = resultMsg.getData();
                JSONArray jsonArray = JSONArray.parseArray(aa);
                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                text = (String) jsonObject1.get("onebest");
            } else {
                System.out.println("获取任务失败");
                // 获取任务结果失败
                jsonObject.put("ecode",resultMsg.getErr_no());
                jsonObject.put("failed",resultMsg.getFailed());
                result.setResult(jsonObject);
                result.error500(resultMsg.getFailed());
                return result;
            }


        } catch (Exception e) {
            System.out.println("初始化异常");
            // 初始化异常，解析异常描述信息
            Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
            jsonObject.put("ecode",initMsg.getErr_no());
            jsonObject.put("failed",initMsg.getFailed());
            result.setResult(jsonObject);
            result.error500(initMsg.getFailed());
            return result;
        }

        jsonObject.put("ecode","1");
        jsonObject.put("text",text);
        result.setResult(jsonObject);
        return result;


    }

}
