package com.rengu.machinereadingcomprehension.Controller;

import com.rengu.machinereadingcomprehension.Entity.ResultEntity;
import com.rengu.machinereadingcomprehension.Entity.UserEntity;
import com.rengu.machinereadingcomprehension.Service.DownLoadLogService;
import com.rengu.machinereadingcomprehension.Service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @program: machine-reading-comprehension
 * @author: hanch
 * @create: 2018-08-31 09:06
 **/

@RestController
@RequestMapping(value = "/downloadlogs")
public class DownLoadLogController {

    private final DownLoadLogService downLoadLogService;

    @Autowired
    public DownLoadLogController(DownLoadLogService downLoadLogService) {
        this.downLoadLogService = downLoadLogService;
    }

    @PostMapping
    public ResultEntity saveDownloadLog(@AuthenticationPrincipal UserEntity loginUser, @RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(downLoadLogService.saveDownloadLog(type, loginUser));
    }

    @GetMapping(value = "/countsbytype")
    public ResultEntity saveDownloadLog(@RequestParam(value = "type") int type) {
        return ResultService.resultBuilder(downLoadLogService.countDownloadLogsByType(type));
    }

    @GetMapping(value = "/counts")
    public ResultEntity saveDownloadLog() {
        return ResultService.resultBuilder(downLoadLogService.countDownloadLogs());
    }

    @GetMapping(value = "/users")
    public ResultEntity getUsers(@AuthenticationPrincipal UserEntity loginUser) {
        return ResultService.resultBuilder(downLoadLogService.getUsers(loginUser));
    }
}
