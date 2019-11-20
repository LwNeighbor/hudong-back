package org.jeecg;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import io.lettuce.core.ScriptOutputType;
import org.jeecg.common.util.IPUtils;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.util.Date;

public class testMain {



    public static void main(String[] args) {
       /* DateTime dateTime = DateUtil.offsetMinute(new Date(),-(-6));
        System.out.println(dateTime);*/

        String format = DateUtil.format(DateUtil.offsetMinute(new Date(), -2), "HH:mm");
        System.out.println(format);


        ClassPathResource classPathResource = new ClassPathResource("jeecg/jeecg_config.properties");

        System.out.println(classPathResource.getAbsolutePath());

    }
}
