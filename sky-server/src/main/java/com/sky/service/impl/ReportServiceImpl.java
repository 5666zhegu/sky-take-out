package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {


    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnOverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }


        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);

            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            map.put("status", Orders.COMPLETED);
            Double amount = orderMapper.sumAmount(map);
            turnoverList.add(amount == null ? 0.0 : amount);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList,","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> NewUserList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            LocalDateTime localDateTimeMIN = LocalDateTime.of(date, LocalTime.MIN);
            Map map = new HashMap();
            map.put("beginTime", localDateTimeMIN);
            map.put("endTime", localDateTimeMAX);
            Integer count = userMapper.sumUser(map);
            NewUserList.add(count == null ? 0 : count);
        }

        List<Integer> totalUserList = new ArrayList<>();
        for(LocalDate date : dateList){
            LocalDateTime localDateTimeMAX = LocalDateTime.of(date, LocalTime.MAX);
            Integer count = userMapper.getTotalByDate(localDateTimeMAX);
            totalUserList.add(count == null ? 0 : count);
        }



        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(NewUserList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .build();
    }
}
